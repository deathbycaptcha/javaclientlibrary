package com.DeathByCaptcha;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLStreamHandler;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;

public class HttpClientTest {

    private static class FakeHttpURLConnection extends HttpURLConnection {
        private final int code;
        private final String body;
        private final String location;
        private final ByteArrayOutputStream output = new ByteArrayOutputStream();

        FakeHttpURLConnection(URL url, int code, String body, String location) {
            super(url);
            this.code = code;
            this.body = body;
            this.location = location;
        }

        @Override
        public void disconnect() {
        }

        @Override
        public boolean usingProxy() {
            return false;
        }

        @Override
        public void connect() {
        }

        @Override
        public int getResponseCode() {
            return this.code;
        }

        @Override
        public String getHeaderFieldKey(int n) {
            if (n == 1) {
                return "Content-Length";
            }
            if (n == 2 && this.location != null) {
                return "Location";
            }
            return null;
        }

        @Override
        public String getHeaderField(int n) {
            if (n == 1) {
                return Integer.toString(this.body != null ? this.body.getBytes(StandardCharsets.UTF_8).length : 0);
            }
            if (n == 2) {
                return this.location;
            }
            return null;
        }

        @Override
        public InputStream getInputStream() {
            return new ByteArrayInputStream((this.body != null ? this.body : "").getBytes(StandardCharsets.UTF_8));
        }

        @Override
        public InputStream getErrorStream() {
            return new ByteArrayInputStream((this.body != null ? this.body : "").getBytes(StandardCharsets.UTF_8));
        }

        @Override
        public java.io.OutputStream getOutputStream() {
            return output;
        }
    }

    private Object newHttpClientCaller(HttpClient client) throws java.lang.Exception {
        Class<?> callerClass = Class.forName("com.DeathByCaptcha.HttpClient$HttpClientCaller");
        Constructor<?> ctor = callerClass.getDeclaredConstructor(HttpClient.class);
        ctor.setAccessible(true);
        return ctor.newInstance(client);
    }

    private String invokeCaller(Object caller, URL url, byte[] payload) throws java.lang.Exception {
        Method callMethod = caller.getClass().getDeclaredMethod(
                "call",
                Proxy.class,
                URL.class,
                byte[].class,
                String.class,
                Date.class
        );
        callMethod.setAccessible(true);
        Object value = callMethod.invoke(caller, Proxy.NO_PROXY, url, payload, "application/json", new Date(System.currentTimeMillis() + 2000));
        return (String) value;
    }

    private static class FakeHttpClient extends HttpClient {
        String lastCmd;
        byte[] lastData;
        String lastContentType;
        JSONObject lastArgs;
        JSONObject nextResponse;

        FakeHttpClient(String username, String password) {
            super(username, password);
            this.nextResponse = new JSONObject();
        }

        FakeHttpClient(String authtoken) {
            super(authtoken);
            this.nextResponse = new JSONObject();
        }

        @Override
        protected JSONObject call(String cmd, byte[] data, String contentType)
                throws IOException, com.DeathByCaptcha.Exception {
            this.lastCmd = cmd;
            this.lastData = data;
            this.lastContentType = contentType;
            return this.nextResponse;
        }

        @Override
        protected JSONObject call(String cmd, JSONObject args)
                throws IOException, com.DeathByCaptcha.Exception {
            this.lastCmd = cmd;
            this.lastArgs = args;
            return this.nextResponse;
        }

        @Override
        protected JSONObject call(String cmd)
                throws IOException, com.DeathByCaptcha.Exception {
            this.lastCmd = cmd;
            return this.nextResponse;
        }

        String payloadAsString() {
            return this.lastData == null ? "" : new String(this.lastData, StandardCharsets.UTF_8);
        }
    }

    private static JSONObject captchaResponse(int id, String text, boolean correct) throws JSONException {
        return new JSONObject()
                .put("captcha", id)
                .put("text", text)
                .put("is_correct", correct);
    }

    @Test
    public void testGetUserGetCaptchaAndReport() {
        try {
            FakeHttpClient client = new FakeHttpClient("user", "pass");

            client.nextResponse = new JSONObject()
                    .put("user", 10)
                    .put("balance", 42.5)
                    .put("is_banned", false);
            User user = client.getUser();
            assertEquals("user", client.lastCmd);
            assertEquals("user", client.lastArgs.optString("username"));
            assertEquals("pass", client.lastArgs.optString("password"));
            assertEquals(10, user.id);
            assertEquals(42.5, user.balance, 0.0001);

            client.nextResponse = captchaResponse(99, "ok", true);
            Captcha captcha = client.getCaptcha(99);
            assertEquals("captcha/99", client.lastCmd);
            assertEquals(99, captcha.id);
            assertEquals("ok", captcha.text);

            client.nextResponse = captchaResponse(99, "ok", false);
            boolean reported = client.report(99);
            assertEquals("captcha/99/report", client.lastCmd);
            assertTrue(reported);
        } catch (java.lang.Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void testUploadImageAndPayloadWithUsernameAndBanner() {
        try {
            FakeHttpClient client = new FakeHttpClient("userA", "passA");
            client.nextResponse = captchaResponse(1, "solved", true);

            byte[] image = new byte[]{1, 2, 3, 4};
            byte[] banner = new byte[]{9, 8, 7};
            Captcha captcha = client.upload(image, "challenge-x", 3, banner, "banner-text", "2x2");

            assertNotNull(captcha);
            assertEquals(1, captcha.id);
            assertEquals("captcha", client.lastCmd);
            assertNotNull(client.lastContentType);
            assertTrue(client.lastContentType.startsWith("multipart/form-data; boundary="));

            String payload = client.payloadAsString();
            assertTrue(payload.contains("name=\"username\""));
            assertTrue(payload.contains("name=\"password\""));
            assertTrue(payload.contains("name=\"challenge\""));
            assertTrue(payload.contains("challenge-x"));
            assertTrue(payload.contains("name=\"banner\""));
            assertTrue(payload.contains("name=\"grid\""));
            assertTrue(payload.contains("2x2"));
        } catch (java.lang.Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void testUploadPayloadWithAuthtokenAndNoBanner() {
        try {
            FakeHttpClient client = new FakeHttpClient("token-123");
            client.nextResponse = captchaResponse(2, "ok", true);

            Captcha captcha = client.upload(new byte[]{5, 6}, "ch", 4, null, "txt", "");

            assertNotNull(captcha);
            String payload = client.payloadAsString();
            assertTrue(payload.contains("name=\"authtoken\""));
            assertTrue(payload.contains("token-123"));
            assertFalse(payload.contains("name=\"username\""));
            assertFalse(payload.contains("name=\"banner\""));
        } catch (java.lang.Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void testUploadTextAudioAndJsonTokenParams() {
        try {
            FakeHttpClient client = new FakeHttpClient("user", "pass");
            client.nextResponse = captchaResponse(3, "ok", true);

            Captcha textCaptcha = client.upload(11, "2+2?");
            assertNotNull(textCaptcha);
            assertTrue(client.payloadAsString().contains("name=\"textcaptcha\""));
            assertTrue(client.payloadAsString().contains("2+2?"));

            client.nextResponse = captchaResponse(4, "ok", true);
            Captcha audioCaptcha = client.upload(13, "audio-content", "en");
            assertNotNull(audioCaptcha);
            assertTrue(client.payloadAsString().contains("name=\"audio\""));
            assertTrue(client.payloadAsString().contains("name=\"language\""));
            assertTrue(client.payloadAsString().contains("audio-content"));

            JSONObject tokenJson = new JSONObject().put("sitekey", "site-value").put("pageurl", "https://x");
            client.nextResponse = captchaResponse(5, "ok", true);
            Captcha tokenCaptcha = client.upload(12, tokenJson);
            assertNotNull(tokenCaptcha);
            String payload = client.payloadAsString();
            assertTrue(payload.contains("name=\"turnstile_params\""));
            assertTrue(payload.contains("site-value"));
        } catch (java.lang.Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void testNoopOverloadAndConnectClose() {
        try {
            FakeHttpClient client = new FakeHttpClient("user", "pass");
            assertTrue(client.connect());
            client.close();
            assertNull(client.upload(new byte[]{1}, 99, null, "x"));
        } catch (java.lang.Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void testServerUrlUsesHttps() {
        assertTrue(HttpClient.SERVER_URL.startsWith("https://"));
        assertEquals("https://api.dbcapi.me/api", HttpClient.SERVER_URL);
    }

    @Test
    public void testHttpClientCallerSuccessAndStatusMappings() {
        try {
            HttpClient client = new HttpClient("u", "p");
            Object caller = newHttpClientCaller(client);

            URL okUrl = new URL(null, "http://fake/ok", new URLStreamHandler() {
                @Override
                protected java.net.URLConnection openConnection(URL u) {
                    return new FakeHttpURLConnection(u, HttpURLConnection.HTTP_OK, "{\"ok\":true}", null);
                }

                @Override
                protected java.net.URLConnection openConnection(URL u, Proxy p) {
                    return new FakeHttpURLConnection(u, HttpURLConnection.HTTP_OK, "{\"ok\":true}", null);
                }
            });

            String response = invokeCaller(caller, okUrl, "payload".getBytes(StandardCharsets.UTF_8));
            assertEquals("{\"ok\":true}", response);

            URL forbiddenUrl = new URL(null, "http://fake/403", new URLStreamHandler() {
                @Override
                protected java.net.URLConnection openConnection(URL u) {
                    return new FakeHttpURLConnection(u, HttpURLConnection.HTTP_FORBIDDEN, "", null);
                }

                @Override
                protected java.net.URLConnection openConnection(URL u, Proxy p) {
                    return new FakeHttpURLConnection(u, HttpURLConnection.HTTP_FORBIDDEN, "", null);
                }
            });
            try {
                invokeCaller(caller, forbiddenUrl, new byte[0]);
                fail("Expected AccessDeniedException");
            } catch (java.lang.Exception expected) {
                assertTrue(expected.getCause() instanceof AccessDeniedException);
            }

            URL badRequestUrl = new URL(null, "http://fake/400", new URLStreamHandler() {
                @Override
                protected java.net.URLConnection openConnection(URL u) {
                    return new FakeHttpURLConnection(u, HttpURLConnection.HTTP_BAD_REQUEST, "", null);
                }

                @Override
                protected java.net.URLConnection openConnection(URL u, Proxy p) {
                    return new FakeHttpURLConnection(u, HttpURLConnection.HTTP_BAD_REQUEST, "", null);
                }
            });
            try {
                invokeCaller(caller, badRequestUrl, new byte[0]);
                fail("Expected InvalidCaptchaException");
            } catch (java.lang.Exception expected) {
                assertTrue(expected.getCause() instanceof InvalidCaptchaException);
            }

            URL unavailableUrl = new URL(null, "http://fake/503", new URLStreamHandler() {
                @Override
                protected java.net.URLConnection openConnection(URL u) {
                    return new FakeHttpURLConnection(u, HttpURLConnection.HTTP_UNAVAILABLE, "", null);
                }

                @Override
                protected java.net.URLConnection openConnection(URL u, Proxy p) {
                    return new FakeHttpURLConnection(u, HttpURLConnection.HTTP_UNAVAILABLE, "", null);
                }
            });
            try {
                invokeCaller(caller, unavailableUrl, new byte[0]);
                fail("Expected ServiceOverloadException");
            } catch (java.lang.Exception expected) {
                assertTrue(expected.getCause() instanceof ServiceOverloadException);
            }
        } catch (java.lang.Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }
}
