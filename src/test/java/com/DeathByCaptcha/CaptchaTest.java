package com.DeathByCaptcha;

import org.json.JSONObject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;


public class CaptchaTest {

    @Test
    public void testCaptchaDefaultConstructor() {
        Captcha captcha = new Captcha();
        
        assertNotNull(captcha);
        assertEquals(0, captcha.id);
        assertEquals("", captcha.text);
    }

    @Test
    public void testCaptchaFromValidJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("captcha", 98765);
            json.put("text", "ABCD1234");
            json.put("is_correct", true);
        } catch (java.lang.Exception e) {
            fail("Failed to create JSON: " + e.getMessage());
        }
        
        Captcha captcha = new Captcha(json);
        
        assertEquals(98765, captcha.id);
        assertEquals("ABCD1234", captcha.text);
    }

    @Test
    public void testCaptchaFromJSONZeroId() {
        JSONObject json = new JSONObject();
        try {
            json.put("captcha", 0);
            json.put("text", "XYZ");
        } catch (java.lang.Exception e) {
            fail("Failed to create JSON: " + e.getMessage());
        }
        
        Captcha captcha = new Captcha(json);
        
        assertEquals(0, captcha.id);
        assertEquals("", captcha.text);
    }

    @Test
    public void testCaptchaIsUploaded() {
        Captcha captcha = new Captcha();
        assertFalse(captcha.isUploaded());
        
        JSONObject json = new JSONObject();
        try {
            json.put("captcha", 123);
        } catch (java.lang.Exception e) {
            fail("Failed to create JSON: " + e.getMessage());
        }
        Captcha uploaded = new Captcha(json);
        assertTrue(uploaded.isUploaded());
    }

    @Test
    public void testCaptchaIsSolved() {
        Captcha captcha = new Captcha();
        assertFalse(captcha.isSolved());
        
        captcha.text = "SOLUTION";
        assertTrue(captcha.isSolved());
        
        captcha.text = "";
        assertFalse(captcha.isSolved());
    }

    @Test
    public void testCaptchaIsCorrect() {
        JSONObject json = new JSONObject();
        try {
            json.put("captcha", 100);
            json.put("text", "SOLVED");
            json.put("is_correct", true);
        } catch (java.lang.Exception e) {
            fail("Failed to create JSON: " + e.getMessage());
        }
        
        Captcha captcha = new Captcha(json);
        assertTrue(captcha.isCorrect());
    }

    @Test
    public void testCaptchaIsCorrectFalse() {
        JSONObject json = new JSONObject();
        try {
            json.put("captcha", 100);
            json.put("text", "WRONG");
            json.put("is_correct", false);
        } catch (java.lang.Exception e) {
            fail("Failed to create JSON: " + e.getMessage());
        }
        
        Captcha captcha = new Captcha(json);
        assertFalse(captcha.isCorrect());
    }

    @Test
    public void testCaptchaIsCorrectNotSolved() {
        JSONObject json = new JSONObject();
        try {
            json.put("captcha", 100);
            json.put("is_correct", true);
        } catch (java.lang.Exception e) {
            fail("Failed to create JSON: " + e.getMessage());
        }
        
        Captcha captcha = new Captcha(json);
        assertFalse(captcha.isCorrect());
    }

    @Test
    public void testCaptchaToInt() {
        Captcha captcha = new Captcha();
        assertEquals(0, captcha.toInt());
        
        JSONObject json = new JSONObject();
        try {
            json.put("captcha", 777);
        } catch (java.lang.Exception e) {
            fail("Failed to create JSON: " + e.getMessage());
        }
        Captcha captcha2 = new Captcha(json);
        assertEquals(777, captcha2.toInt());
    }

    @Test
    public void testCaptchaToString() {
        Captcha captcha = new Captcha();
        assertEquals("", captcha.toString());
        
        captcha.text = "TEST123";
        assertEquals("TEST123", captcha.toString());
    }

    @Test
    public void testCaptchaToBoolean() {
        JSONObject json = new JSONObject();
        try {
            json.put("captcha", 1);
            json.put("text", "SOLUTION");
            json.put("is_correct", true);
        } catch (java.lang.Exception e) {
            fail("Failed to create JSON: " + e.getMessage());
        }
        
        Captcha captcha = new Captcha(json);
        assertTrue(captcha.toBoolean());
    }

    @Test
    public void testCaptchaToBooleanFalse() {
        JSONObject json = new JSONObject();
        try {
            json.put("captcha", 1);
            json.put("text", "WRONG");
            json.put("is_correct", false);
        } catch (java.lang.Exception e) {
            fail("Failed to create JSON: " + e.getMessage());
        }
        
        Captcha captcha = new Captcha(json);
        assertFalse(captcha.toBoolean());
    }

    @Test
    public void testCaptchaFieldsMutable() {
        Captcha captcha = new Captcha();
        captcha.id = 666;
        captcha.text = "MUTABLE";
        
        assertEquals(666, captcha.id);
        assertEquals("MUTABLE", captcha.text);
        assertTrue(captcha.isSolved());
        assertTrue(captcha.isUploaded());
    }

    @Test
    public void testCaptchaFromJSONWithNegativeId() {
        JSONObject json = new JSONObject();
        try {
            json.put("captcha", -50);
            json.put("text", "TEST");
        } catch (java.lang.Exception e) {
            fail("Failed to create JSON: " + e.getMessage());
        }
        
        Captcha captcha = new Captcha(json);
        
        assertEquals(0, captcha.id);
        assertEquals("", captcha.text);
    }
}
