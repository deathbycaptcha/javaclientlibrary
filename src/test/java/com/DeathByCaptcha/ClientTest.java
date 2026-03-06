package com.DeathByCaptcha;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONObject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;


public class ClientTest {
        private static class MockClient extends Client {
            public int lastType;
            public JSONObject lastJson;
            public boolean uploadCalled = false;
            public MockClient(String username, String password) { super(username, password); }
            public MockClient(String authtoken) { super(authtoken); }
            @Override public void close() {}
            @Override public boolean connect() throws IOException { return true; }
            @Override public User getUser() throws IOException, com.DeathByCaptcha.Exception { return new User(); }
            @Override public Captcha upload(byte[] img, String challenge, int type, byte[] banner, String banner_text, String grid) throws IOException, com.DeathByCaptcha.Exception { uploadCalled = true; return null; }
            @Override public Captcha upload(byte[] img, String challenge, int type, byte[] banner, String banner_text) throws IOException, com.DeathByCaptcha.Exception { uploadCalled = true; return null; }
            @Override public Captcha upload(byte[] img, int type, byte[] banner, String banner_text) throws IOException, com.DeathByCaptcha.Exception { uploadCalled = true; return null; }
            @Override public Captcha upload(byte[] img) throws IOException, com.DeathByCaptcha.Exception { uploadCalled = true; return null; }
            @Override public Captcha getCaptcha(int captcha_id) throws IOException, com.DeathByCaptcha.Exception { return null; }
            @Override public Captcha upload(int type, JSONObject json) throws IOException, com.DeathByCaptcha.Exception { this.lastType = type; this.lastJson = json; uploadCalled = true; return null; }
            @Override public Captcha upload(int type, String textcaptcha) throws IOException, com.DeathByCaptcha.Exception { uploadCalled = true; return null; }
            @Override public boolean report(int captcha_id) throws IOException, com.DeathByCaptcha.Exception { return false; }
        }

        @Test
        public void testUploadOverloadsAndJsonLogic() throws java.lang.Exception {
            MockClient client = new MockClient("user", "pass");
            try {
                // upload(InputStream)
                byte[] data = {1,2,3};
                ByteArrayInputStream stream = new ByteArrayInputStream(data);
                client.uploadCalled = false;
                client.upload(stream);
                assertTrue(client.uploadCalled);

                // upload(File)
                File tempFile = File.createTempFile("testUpload", ".bin");
                tempFile.deleteOnExit();
                FileOutputStream fos = new FileOutputStream(tempFile);
                fos.write(new byte[]{4,5,6});
                fos.close();
                client.uploadCalled = false;
                client.upload(tempFile);
                assertTrue(client.uploadCalled);

                // upload(String)
                client.uploadCalled = false;
                client.upload(tempFile.getAbsolutePath());
                assertTrue(client.uploadCalled);

                // upload(int, String, String) - type 6 (publickey)
                client.uploadCalled = false;
                client.upload(6, "pubkey", "http://site");
                assertTrue(client.uploadCalled);
                assertEquals(6, client.lastType);
                assertEquals("pubkey", client.lastJson.optString("publickey"));
                assertEquals("http://site", client.lastJson.optString("pageurl"));

                // upload(int, String, String) - type 7 (sitekey)
                client.upload(7, "sitekey", "http://site2");
                assertEquals(7, client.lastType);
                assertEquals("sitekey", client.lastJson.optString("sitekey"));

                // upload(int, String, String) - type 12 (sitekey)
                client.upload(12, "sitekey12", "http://site3");
                assertEquals(12, client.lastType);
                assertEquals("sitekey12", client.lastJson.optString("sitekey"));

                // upload(int, String, String) - type 14 (captchaid)
                client.upload(14, "cid", "http://site4");
                assertEquals(14, client.lastType);
                assertEquals("cid", client.lastJson.optString("captchaid"));

                // upload(int, String, String) - default (googlekey)
                client.upload(99, "gkey", "http://site5");
                assertEquals(99, client.lastType);
                assertEquals("gkey", client.lastJson.optString("googlekey"));

                // upload(int, String, String, String) (googlekey, pageurl, data-s)
                client.upload(7, "gkey2", "http://site6", "datas");
                assertEquals(7, client.lastType);
                assertEquals("gkey2", client.lastJson.optString("googlekey"));
                assertEquals("datas", client.lastJson.optString("data-s"));

                // upload(int, String, String, String, double) (recaptcha v3)
                client.upload(7, "gkey3", "http://site7", "action", 0.7);
                assertEquals(7, client.lastType);
                assertEquals("gkey3", client.lastJson.optString("googlekey"));
                assertEquals("action", client.lastJson.optString("action"));
                assertEquals(0.7, client.lastJson.optDouble("min_score"), 0.0001);

                // upload(int, String, String, String, String) (proxy, proxytype, key, pageurl)
                client.upload(6, "proxy1", "http", "pubkey2", "http://site8");
                assertEquals(6, client.lastType);
                assertEquals("proxy1", client.lastJson.optString("proxy"));
                assertEquals("http", client.lastJson.optString("proxytype"));
                assertEquals("pubkey2", client.lastJson.optString("publickey"));

                // upload(int, String, String, String, String, double) (proxy, proxytype, googlekey, pageurl, action, min_score)
                client.upload(7, "proxy2", "https", "gkey4", "http://site9", "act2", 0.9);
                assertEquals(7, client.lastType);
                assertEquals("proxy2", client.lastJson.optString("proxy"));
                assertEquals("https", client.lastJson.optString("proxytype"));
                assertEquals("gkey4", client.lastJson.optString("googlekey"));
                assertEquals("act2", client.lastJson.optString("action"));
                assertEquals(0.9, client.lastJson.optDouble("min_score"), 0.0001);
            } catch (java.lang.Exception e) {
                e.printStackTrace();
                fail("Exception thrown: " + e);
            }
        }
    
    private Client testClient;

    @Before
    public void setUp() {
        testClient = new Client("testuser", "testpass") {
            @Override public void close() {}
            @Override public boolean connect() throws IOException { return true; }
            @Override public User getUser() throws IOException, com.DeathByCaptcha.Exception { return new User(); }
            @Override public Captcha upload(byte[] img, String challenge, int type, byte[] banner, String banner_text, String grid) throws IOException, com.DeathByCaptcha.Exception { return null; }
            @Override public Captcha upload(byte[] img, String challenge, int type, byte[] banner, String banner_text) throws IOException, com.DeathByCaptcha.Exception { return null; }
            @Override public Captcha upload(byte[] img, int type, byte[] banner, String banner_text) throws IOException, com.DeathByCaptcha.Exception { return null; }
            @Override public Captcha upload(byte[] img) throws IOException, com.DeathByCaptcha.Exception { return null; }
            @Override public Captcha getCaptcha(int captcha_id) throws IOException, com.DeathByCaptcha.Exception { return null; }
            @Override public Captcha upload(int type, JSONObject json) throws IOException, com.DeathByCaptcha.Exception { return null; }
            @Override public Captcha upload(int type, String textcaptcha) throws IOException, com.DeathByCaptcha.Exception { return null; }
            @Override public boolean report(int captcha_id) throws IOException, com.DeathByCaptcha.Exception { return false; }
        };
    }

    @Test
    public void testClientConstructors() {
        assertNotNull(testClient);
        
        Client client2 = new Client("authtoken123") {
            @Override public void close() {}
            @Override public boolean connect() throws IOException { return true; }
            @Override public User getUser() throws IOException, com.DeathByCaptcha.Exception { return null; }
            @Override public Captcha upload(byte[] img, String challenge, int type, byte[] banner, String banner_text, String grid) throws IOException, com.DeathByCaptcha.Exception { return null; }
            @Override public Captcha upload(byte[] img, String challenge, int type, byte[] banner, String banner_text) throws IOException, com.DeathByCaptcha.Exception { return null; }
            @Override public Captcha upload(byte[] img, int type, byte[] banner, String banner_text) throws IOException, com.DeathByCaptcha.Exception { return null; }
            @Override public Captcha upload(byte[] img) throws IOException, com.DeathByCaptcha.Exception { return null; }
            @Override public Captcha getCaptcha(int captcha_id) throws IOException, com.DeathByCaptcha.Exception { return null; }
            @Override public Captcha upload(int type, JSONObject json) throws IOException, com.DeathByCaptcha.Exception { return null; }
            @Override public Captcha upload(int type, String textcaptcha) throws IOException, com.DeathByCaptcha.Exception { return null; }
            @Override public boolean report(int captcha_id) throws IOException, com.DeathByCaptcha.Exception { return false; }
        };
        
        assertNotNull(client2);
    }

    @Test
    public void testVerboseLogging() {
        testClient.isVerbose = false;
        assertFalse(testClient.isVerbose);
        
        testClient.isVerbose = true;
        assertTrue(testClient.isVerbose);
    }

    @Test
    public void testGetPollInterval() {
        int[] result0 = Client.getPollInterval(0);
        assertNotNull(result0);
        assertEquals(1, result0[0]);
        assertEquals(1, result0[1]);
        
        int[] result4 = Client.getPollInterval(4);
        assertEquals(Client.POLLS_INTERVAL[4], result4[0]);
        
        int[] resultOutOfRange = Client.getPollInterval(100);
        assertEquals(Client.DFLT_POLL_INTERVAL, resultOutOfRange[0]);
    }

    @Test
    public void testLoadFromStream() throws IOException {
        byte[] data = {1, 2, 3, 4, 5};
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        
        byte[] loaded = testClient.load(stream);
        
        assertNotNull(loaded);
        assertEquals(5, loaded.length);
    }

    @Test
    public void testLoadFromEmptyStream() throws IOException {
        ByteArrayInputStream stream = new ByteArrayInputStream(new byte[0]);
        byte[] loaded = testClient.load(stream);
        
        assertNotNull(loaded);
        assertEquals(0, loaded.length);
    }

    @Test
    public void testLoadFromFile() throws IOException {
        File tempFile = File.createTempFile("test", ".txt");
        tempFile.deleteOnExit();

        FileOutputStream fos = new FileOutputStream(tempFile);
        fos.write(new byte[]{10, 20, 30, 40});
        fos.close();

        byte[] loaded = testClient.load(tempFile);

        assertNotNull(loaded);
        assertEquals(4, loaded.length);
    }

    @Test
    public void testLoadFromFilename() throws IOException {
        File tempFile = File.createTempFile("test", ".txt");
        tempFile.deleteOnExit();

        FileOutputStream fos = new FileOutputStream(tempFile);
        fos.write(new byte[]{50, 60, 70});
        fos.close();

        byte[] loaded = testClient.load(tempFile.getAbsolutePath());

        assertNotNull(loaded);
        assertEquals(3, loaded.length);
    }

    @Test(expected = FileNotFoundException.class)
    public void testLoadNonexistentFile() throws IOException {
        testClient.load("/this/file/does/not/exist.txt");
    }

    @Test
    public void testGetCredentialsUsername() {
        Client client = new Client("user123", "pass456") {
            @Override public void close() {}
            @Override public boolean connect() throws IOException { return true; }
            @Override public User getUser() throws IOException, com.DeathByCaptcha.Exception { return null; }
            @Override public Captcha upload(byte[] img, String challenge, int type, byte[] banner, String banner_text, String grid) throws IOException, com.DeathByCaptcha.Exception { return null; }
            @Override public Captcha upload(byte[] img, String challenge, int type, byte[] banner, String banner_text) throws IOException, com.DeathByCaptcha.Exception { return null; }
            @Override public Captcha upload(byte[] img, int type, byte[] banner, String banner_text) throws IOException, com.DeathByCaptcha.Exception { return null; }
            @Override public Captcha upload(byte[] img) throws IOException, com.DeathByCaptcha.Exception { return null; }
            @Override public Captcha getCaptcha(int captcha_id) throws IOException, com.DeathByCaptcha.Exception { return null; }
            @Override public Captcha upload(int type, JSONObject json) throws IOException, com.DeathByCaptcha.Exception { return null; }
            @Override public Captcha upload(int type, String textcaptcha) throws IOException, com.DeathByCaptcha.Exception { return null; }
            @Override public boolean report(int captcha_id) throws IOException, com.DeathByCaptcha.Exception { return false; }
        };
        
        JSONObject creds = client.getCredentials();
        
        assertNotNull(creds);
        assertEquals("user123", creds.optString("username", ""));
        assertEquals("pass456", creds.optString("password", ""));
    }

    @Test
    public void testGetCredentialsAuthtoken() {
        Client client = new Client("mytoken") {
            @Override public void close() {}
            @Override public boolean connect() throws IOException { return true; }
            @Override public User getUser() throws IOException, com.DeathByCaptcha.Exception { return null; }
            @Override public Captcha upload(byte[] img, String challenge, int type, byte[] banner, String banner_text, String grid) throws IOException, com.DeathByCaptcha.Exception { return null; }
            @Override public Captcha upload(byte[] img, String challenge, int type, byte[] banner, String banner_text) throws IOException, com.DeathByCaptcha.Exception { return null; }
            @Override public Captcha upload(byte[] img, int type, byte[] banner, String banner_text) throws IOException, com.DeathByCaptcha.Exception { return null; }
            @Override public Captcha upload(byte[] img) throws IOException, com.DeathByCaptcha.Exception { return null; }
            @Override public Captcha getCaptcha(int captcha_id) throws IOException, com.DeathByCaptcha.Exception { return null; }
            @Override public Captcha upload(int type, JSONObject json) throws IOException, com.DeathByCaptcha.Exception { return null; }
            @Override public Captcha upload(int type, String textcaptcha) throws IOException, com.DeathByCaptcha.Exception { return null; }
            @Override public boolean report(int captcha_id) throws IOException, com.DeathByCaptcha.Exception { return false; }
        };
        
        JSONObject creds = client.getCredentials();
        
        assertNotNull(creds);
        assertEquals("mytoken", creds.optString("authtoken", ""));
    }

    @Test
    public void testConstants() {
        assertEquals("DBC/Java v4.6.9", Client.API_VERSION);
        assertEquals(0, Client.SOFTWARE_VENDOR_ID);
        assertEquals(60, Client.DEFAULT_TIMEOUT);
        assertEquals(120, Client.DEFAULT_TOKEN_TIMEOUT);
        assertEquals(9, Client.LEN_POLLS_INTERVAL);
        assertNotNull(Client.POLLS_INTERVAL);
    }

    @Test
    public void testLoadLargeStream() throws IOException {
        byte[] data = new byte[5000];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte)(i % 256);
        }
        
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        byte[] loaded = testClient.load(stream);
        
        assertEquals(5000, loaded.length);
    }
}
