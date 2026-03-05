package com.DeathByCaptcha;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;

import org.json.JSONObject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;

public class ClientDecodeTest {

    private static class SolvedClient extends Client {
        int lastId;
        int lastType;
        String lastTextCaptcha;
        JSONObject lastJson;
        byte[] lastImage;

        SolvedClient(String username, String password) {
            super(username, password);
        }

        private Captcha solvedCaptcha(int id) {
            JSONObject payload = new JSONObject();
            try {
                payload.put("captcha", id);
                payload.put("text", "solved");
                payload.put("is_correct", true);
            } catch (java.lang.Exception e) {
                // no-op for test helper
            }
            return new Captcha(payload);
        }

        @Override
        public void close() {
        }

        @Override
        public boolean connect() {
            return true;
        }

        @Override
        public User getUser() {
            return new User();
        }

        @Override
        public Captcha upload(byte[] img, String challenge, int type, byte[] banner, String banner_text, String grid) {
            this.lastImage = img;
            this.lastType = type;
            this.lastId = 100 + type;
            return solvedCaptcha(this.lastId);
        }

        @Override
        public Captcha upload(byte[] img, String challenge, int type, byte[] banner, String banner_text) {
            return upload(img, challenge, type, banner, banner_text, "");
        }

        @Override
        public Captcha upload(byte[] img, int type, byte[] banner, String banner_text) {
            return upload(img, "", type, banner, banner_text, "");
        }

        @Override
        public Captcha upload(byte[] img) {
            return upload(img, "", 0, null, "", "");
        }

        @Override
        public Captcha upload(int type, JSONObject json) {
            this.lastType = type;
            this.lastJson = json;
            this.lastId = 200 + type;
            return solvedCaptcha(this.lastId);
        }

        @Override
        public Captcha upload(int type, String textcaptcha) {
            this.lastType = type;
            this.lastTextCaptcha = textcaptcha;
            this.lastId = 300 + type;
            return solvedCaptcha(this.lastId);
        }

        @Override
        public Captcha getCaptcha(int id) {
            return solvedCaptcha(id);
        }

        @Override
        public boolean report(int id) {
            return true;
        }
    }

    @Test
    public void testDecodeOverloadsForBytesStreamsFilesAndStrings() {
        try {
            SolvedClient client = new SolvedClient("u", "p");
            byte[] image = new byte[]{1, 2, 3, 4};
            byte[] banner = new byte[]{9, 8};

            assertNotNull(client.decode(image, "challenge", 3, banner, "banner", "2x2", 1));
            assertNotNull(client.decode(image, "challenge", 3, banner, "banner", 1));
            assertNotNull(client.decode(image, 3, banner, "banner"));
            assertNotNull(client.decode(image, 3));
            assertNotNull(client.decode(image, "challenge"));
            assertNotNull(client.decode(image, 3, 1));
            assertNotNull(client.decode(image, "challenge", 1));
            assertNotNull(client.decode(image));

            ByteArrayInputStream imgSt = new ByteArrayInputStream(image);
            ByteArrayInputStream bannerSt = new ByteArrayInputStream(banner);
            assertNotNull(client.decode(imgSt, "challenge", 3, bannerSt, "banner", "2x2", 1));
            assertNotNull(client.decode(new ByteArrayInputStream(image), "challenge", 3, new ByteArrayInputStream(banner), "banner", 1));
            assertNotNull(client.decode(new ByteArrayInputStream(image), 3, new ByteArrayInputStream(banner), "banner", "3x3"));
            assertNotNull(client.decode(new ByteArrayInputStream(image), 3, new ByteArrayInputStream(banner), "banner"));
            assertNotNull(client.decode(new ByteArrayInputStream(image), 3, 1));
            assertNotNull(client.decode(new ByteArrayInputStream(image), "challenge"));
            assertNotNull(client.decode(new ByteArrayInputStream(image), "challenge", 1));
            assertNotNull(client.decode(new ByteArrayInputStream(image), 1));
            assertNotNull(client.decode(new ByteArrayInputStream(image)));

            File imageFile = File.createTempFile("img", ".bin");
            File bannerFile = File.createTempFile("banner", ".bin");
            imageFile.deleteOnExit();
            bannerFile.deleteOnExit();

            FileOutputStream fos1 = new FileOutputStream(imageFile);
            fos1.write(image);
            fos1.close();
            FileOutputStream fos2 = new FileOutputStream(bannerFile);
            fos2.write(banner);
            fos2.close();

            assertNotNull(client.decode(imageFile, "challenge", 3, bannerFile, "banner", 1));
            assertNotNull(client.decode(imageFile, 3, bannerFile, "banner"));
            assertNotNull(client.decode(imageFile, 3, 1));
            assertNotNull(client.decode(imageFile, "challenge"));
            assertNotNull(client.decode(imageFile, "challenge", 1));
            assertNotNull(client.decode(imageFile, 1));
            assertNotNull(client.decode(imageFile));

            String imagePath = imageFile.getAbsolutePath();
            String bannerPath = bannerFile.getAbsolutePath();
            assertNotNull(client.decode(imagePath, "challenge", 3, bannerPath, "banner", 1));
            assertNotNull(client.decode(imagePath, "challenge", 3, bannerPath, "banner", "4x4", 1));
            assertNotNull(client.decode(imagePath, 3, bannerPath, "banner"));
            assertNotNull(client.decode(imagePath, 3, bannerPath, "banner", 1));
            assertNotNull(client.decode(imagePath, 3, bannerPath, "banner", "2x4", 1));
            assertNotNull(client.decode(imagePath, 3, 1));
            assertNotNull(client.decode(imagePath, "challenge"));
            assertNotNull(client.decode(imagePath, "challenge", 1));
            assertNotNull(client.decode(imagePath, 1));
            assertNotNull(client.decode(imagePath));

            Captcha c = client.decode(image);
            assertNotNull(client.getCaptcha(c));
            assertEquals("solved", client.getText(c.id));
            assertEquals("solved", client.getText(c));
            assertTrue(client.report(c));
        } catch (java.lang.Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void testDecodeOverloadsForTokenJsonAndTextCaptcha() {
        try {
            SolvedClient client = new SolvedClient("u", "p");
            JSONObject tokenJson = new JSONObject().put("sitekey", "abc").put("pageurl", "https://example.com");

            assertNotNull(client.decode(4, "google-key", "https://site", 1));
            assertNotNull(client.decode(4, "google-key", "https://site"));

            assertNotNull(client.decode("proxy", "http", "google-key", "https://site"));
            assertNotNull(client.decode("proxy", "http", "google-key", "https://site", 1));

            assertNotNull(client.decode("google-key", "https://site", "data-s"));
            assertNotNull(client.decode("google-key", "https://site", "data-s", 1));

            assertNotNull(client.decode("google-key", "https://site", "action-x", 0.7));
            assertNotNull(client.decode("google-key", "https://site", "action-x", 0.7, 1));

            assertNotNull(client.decode("proxy", "http", "google-key", "https://site", "action-y", 0.5));
            assertNotNull(client.decode("proxy", "http", "google-key", "https://site", "action-y", 0.5, 1));

            assertNotNull(client.decode(12, "proxy", "http", "site-key", "https://site"));
            assertNotNull(client.decode(12, "proxy", "http", "site-key", "https://site", 1));

            assertNotNull(client.decode(tokenJson));
            assertNotNull(client.decode(tokenJson, 1));
            assertNotNull(client.decode(16, tokenJson));
            assertNotNull(client.decode(16, tokenJson, 1));

            assertNotNull(client.decode(11, "What is 2+2?", 1));
            assertEquals(11, client.lastType);
            assertEquals("What is 2+2?", client.lastTextCaptcha);

            assertNotNull(client.lastJson);
        } catch (java.lang.Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }
}
