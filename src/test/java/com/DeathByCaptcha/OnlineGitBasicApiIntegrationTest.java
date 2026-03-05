package com.DeathByCaptcha;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Assume;
import org.junit.Test;

public class OnlineGitBasicApiIntegrationTest {

    private static final Path TEXT_CAPTCHA_IMAGE = Path.of(
            "api-metadata", "validation", "python", "samples", "normal.jpg"
    );
    private static final int SOLVE_TIMEOUT_SECONDS = 120;

    @Test
    public void testBasicApiFlowFromGitSource() throws java.lang.Exception {
        String username = getenvTrimmed("DBC_USERNAME");
        String password = getenvTrimmed("DBC_PASSWORD");

        Assume.assumeTrue("Skipping online integration test: DBC_USERNAME is missing", !username.isEmpty());
        Assume.assumeTrue("Skipping online integration test: DBC_PASSWORD is missing", !password.isEmpty());
        Assume.assumeTrue("Skipping online integration test: sample image not found", Files.exists(TEXT_CAPTCHA_IMAGE));

        Client client = new HttpClient(username, password);
        client.isVerbose = true;

        try {
            boolean connected = client.connect();
            System.out.println("[IT-GIT] connected=" + connected);
            assertTrue("Expected successful connection to DBC API", connected);

            User user = client.getUser();
            System.out.println("[IT-GIT] userId=" + user.id + ", isBanned=" + user.isBanned);
            assertTrue("Expected valid user id from getUser()", user.id > 0);

            double balance = client.getBalance();
            System.out.println("[IT-GIT] balance=" + balance);
            assertTrue("Expected balance >= 0 but got " + balance, balance >= 0.0);

            byte[] imageBytes = Files.readAllBytes(TEXT_CAPTCHA_IMAGE);
            Captcha uploaded = client.upload(imageBytes, "", 0, null, "");
            assertNotNull("Expected upload() to return a captcha object", uploaded);
            assertTrue("Expected uploaded captcha id > 0", uploaded.id > 0);
            System.out.println("[IT-GIT] uploadedCaptchaId=" + uploaded.id);

            Captcha solved = waitForSolve(client, uploaded.id, SOLVE_TIMEOUT_SECONDS);
            assertNotNull("Expected solved captcha within timeout", solved);
            assertTrue("Expected solved captcha text to be non-empty", solved.text != null && !solved.text.trim().isEmpty());
            System.out.println("[IT-GIT] solvedCaptchaId=" + solved.id + ", textLength=" + solved.text.length());
        } finally {
            client.close();
        }
    }

    private static Captcha waitForSolve(Client client, int captchaId, int timeoutSeconds)
            throws java.lang.Exception {
        long deadline = System.currentTimeMillis() + timeoutSeconds * 1000L;
        int pollIndex = 0;
        Captcha captcha = client.getCaptcha(captchaId);

        while (System.currentTimeMillis() < deadline && !captcha.isSolved()) {
            int[] poll = Client.getPollInterval(pollIndex);
            int waitSeconds = poll[0];
            pollIndex = poll[1];
            Thread.sleep(waitSeconds * 1000L);
            captcha = client.getCaptcha(captchaId);
            System.out.println("[IT-GIT] polling captchaId=" + captchaId + ", solved=" + captcha.isSolved());
        }

        return captcha.isSolved() ? captcha : null;
    }

    private static String getenvTrimmed(String key) {
        String value = System.getenv(key);
        return value == null ? "" : value.trim();
    }
}
