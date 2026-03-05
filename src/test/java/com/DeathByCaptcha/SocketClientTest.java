package com.DeathByCaptcha;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;

import org.json.JSONException;
import org.json.JSONObject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;

public class SocketClientTest {

    private static class CaptureSocketClient extends SocketClient {
        String lastCmd;
        JSONObject lastArgs;
        JSONObject nextResponse;

        CaptureSocketClient(String username, String password) {
            super(username, password);
            this.nextResponse = new JSONObject();
        }

        CaptureSocketClient(String authtoken) {
            super(authtoken);
            this.nextResponse = new JSONObject();
        }

        @Override
        protected JSONObject call(String cmd, JSONObject args)
                throws IOException, com.DeathByCaptcha.Exception {
            this.lastCmd = cmd;
            this.lastArgs = args;
            return this.nextResponse;
        }
    }

    private static class CallSocketClient extends SocketClient {
        Queue<String> responses = new ArrayDeque<>();
        String lastPayload;
        boolean throwOnSend;
        int closeCount;

        CallSocketClient(String username, String password) {
            super(username, password);
        }

        @Override
        public boolean connect() {
            return true;
        }

        @Override
        public void close() {
            closeCount++;
            this.channel = null;
        }

        @Override
        protected String sendAndReceive(byte[] payload) throws IOException {
            this.lastPayload = new String(payload);
            if (throwOnSend) {
                throw new IOException("send failed");
            }
            if (responses.isEmpty()) {
                throw new IOException("no response");
            }
            return responses.remove();
        }

        JSONObject invokeCall(String cmd, JSONObject args)
                throws IOException, com.DeathByCaptcha.Exception {
            return super.call(cmd, args);
        }
    }

    private static JSONObject captchaResponse(int id, String text, boolean correct) throws JSONException {
        return new JSONObject()
                .put("captcha", id)
                .put("text", text)
                .put("is_correct", correct);
    }

    @Test
    public void testUploadAndUtilityMethodsWithCapturedArgs() {
        try {
            CaptureSocketClient client = new CaptureSocketClient("user", "pass");

            client.nextResponse = captchaResponse(101, "ok", true);
            Captcha captcha = client.upload(new byte[]{1, 2, 3}, "challenge", 3, new byte[]{7, 8}, "banner", "2x2");
            assertNotNull(captcha);
            assertEquals("upload", client.lastCmd);
            assertEquals("challenge", client.lastArgs.optString("challenge"));
            assertEquals("2x2", client.lastArgs.optString("grid"));
            assertTrue(client.lastArgs.has("banner"));
            assertEquals("3", client.lastArgs.optString("type"));

            client.nextResponse = captchaResponse(102, "ok", true);
            Captcha audio = client.upload(13, "audio-blob", "en");
            assertNotNull(audio);
            assertEquals("audio-blob", client.lastArgs.optString("audio"));
            assertEquals("en", client.lastArgs.optString("language"));

            client.nextResponse = captchaResponse(103, "ok", true);
            JSONObject token = new JSONObject().put("sitekey", "abc");
            Captcha tokenCaptcha = client.upload(8, token);
            assertNotNull(tokenCaptcha);
            assertEquals("8", client.lastArgs.optString("type"));
            assertTrue(client.lastArgs.has("geetest_params"));

            client.nextResponse = captchaResponse(104, "ok", true);
            Captcha text = client.upload(11, "What is 1+1?");
            assertNotNull(text);
            assertEquals("What is 1+1?", client.lastArgs.optString("textcaptcha"));

            client.nextResponse = new JSONObject().put("user", 9).put("balance", 10.5);
            User user = client.getUser();
            assertEquals("user", client.lastCmd);
            assertEquals(9, user.id);

            client.nextResponse = captchaResponse(88, "ans", true);
            Captcha fetched = client.getCaptcha(88);
            assertEquals("captcha", client.lastCmd);
            assertEquals(88, client.lastArgs.optInt("captcha"));
            assertEquals(88, fetched.id);

            client.nextResponse = captchaResponse(88, "ans", false);
            boolean reportResult = client.report(88);
            assertEquals("report", client.lastCmd);
            assertTrue(reportResult);

            assertNull(client.upload(new byte[]{1}, 2, null, "banner"));
        } catch (java.lang.Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void testProtectedCallSuccessAndMetadata() {
        try {
            CallSocketClient client = new CallSocketClient("user", "pass");
            client.responses.add("{\"captcha\":1,\"text\":\"ok\",\"is_correct\":true}");

            JSONObject response = client.invokeCall("login", new JSONObject());

            assertEquals(1, response.optInt("captcha"));
            assertNotNull(client.lastPayload);
            assertTrue(client.lastPayload.contains("\"cmd\":\"login\""));
            assertTrue(client.lastPayload.contains("\"version\":\"" + Client.API_VERSION + "\""));
        } catch (java.lang.Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void testProtectedCallErrorMappings() {
        try {
            CallSocketClient client = new CallSocketClient("user", "pass");

            client.responses.add("{\"error\":\"not-logged-in\"}");
            try {
                client.invokeCall("login", new JSONObject());
                fail("Expected AccessDeniedException");
            } catch (AccessDeniedException expected) {
                assertTrue(client.closeCount > 0);
            }

            client.responses.add("{\"error\":\"invalid-captcha\"}");
            try {
                client.invokeCall("login", new JSONObject());
                fail("Expected InvalidCaptchaException");
            } catch (InvalidCaptchaException expected) {
                assertTrue(client.closeCount > 0);
            }

            client.responses.add("{\"error\":\"service-overload\"}");
            try {
                client.invokeCall("login", new JSONObject());
                fail("Expected ServiceOverloadException");
            } catch (ServiceOverloadException expected) {
                assertTrue(client.closeCount > 0);
            }

            client.responses.add("{\"error\":\"unknown\"}");
            try {
                client.invokeCall("login", new JSONObject());
                fail("Expected IOException");
            } catch (IOException expected) {
                assertTrue(expected.getMessage().contains("API server error occured"));
            }
        } catch (java.lang.Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void testProtectedCallRetriesAndFailsWhenConnectionLost() {
        try {
            CallSocketClient client = new CallSocketClient("user", "pass");
            client.throwOnSend = true;

            try {
                client.invokeCall("login", new JSONObject());
                fail("Expected IOException");
            } catch (IOException expected) {
                assertTrue(expected.getMessage().contains("API connection lost or timed out"));
            }
        } catch (java.lang.Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void testConnectWithUnknownHostThrowsIoException() {
        try {
            SocketClient client = new SocketClient("user", "pass") {
                @Override
                public boolean connect() throws IOException {
                    throw new IOException("API host not found");
                }
            };

            try {
                client.connect();
                fail("Expected IOException");
            } catch (IOException expected) {
                assertTrue(expected.getMessage().contains("API host not found"));
            }
        } catch (java.lang.Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }
}
