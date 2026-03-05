package com.DeathByCaptcha;

import org.json.JSONObject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;


public class UserTest {

    @Test
    public void testUserDefaultConstructor() {
        User user = new User();
        
        assertNotNull(user);
        assertEquals(0, user.id);
        assertEquals(0.0, user.balance, 0.01);
        assertFalse(user.isBanned);
    }

    @Test
    public void testUserFromValidJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("user", 12345);
            json.put("balance", 99.99);
            json.put("is_banned", false);
        } catch (java.lang.Exception e) {
            fail("Failed to create JSON: " + e.getMessage());
        }
        
        User user = new User(json);
        
        assertEquals(12345, user.id);
        assertEquals(99.99, user.balance, 0.01);
        assertFalse(user.isBanned);
    }

    @Test
    public void testUserFromJSONZeroId() {
        JSONObject json = new JSONObject();
        try {
            json.put("user", 0);
            json.put("balance", 50.0);
        } catch (java.lang.Exception e) {
            fail("Failed to create JSON: " + e.getMessage());
        }
        
        User user = new User(json);
        
        assertEquals(0, user.id);
    }

    @Test
    public void testUserFromJSONNegativeId() {
        JSONObject json = new JSONObject();
        try {
            json.put("user", -100);
        } catch (java.lang.Exception e) {
            fail("Failed to create JSON: " + e.getMessage());
        }
        
        User user = new User(json);
        
        assertEquals(0, user.id);
    }

    @Test
    public void testUserFromJSONBanned() {
        JSONObject json = new JSONObject();
        try {
            json.put("user", 999);
            json.put("is_banned", true);
        } catch (java.lang.Exception e) {
            fail("Failed to create JSON: " + e.getMessage());
        }
        
        User user = new User(json);
        
        assertEquals(999, user.id);
        assertTrue(user.isBanned);
    }

    @Test
    public void testUserFromEmptyJSON() {
        JSONObject json = new JSONObject();
        
        User user = new User(json);
        
        assertEquals(0, user.id);
        assertEquals(0.0, user.balance, 0.01);
        assertFalse(user.isBanned);
    }

    @Test
    public void testUserFieldsMutable() {
        User user = new User();
        user.id = 555;
        user.balance = 123.45;
        user.isBanned = true;
        
        assertEquals(555, user.id);
        assertEquals(123.45, user.balance, 0.01);
        assertTrue(user.isBanned);
    }

    @Test
    public void testUserLargeBalance() {
        JSONObject json = new JSONObject();
        try {
            json.put("user", 1);
            json.put("balance", 9999.99);
        } catch (java.lang.Exception e) {
            fail("Failed to create JSON: " + e.getMessage());
        }
        
        User user = new User(json);
        
        assertEquals(9999.99, user.balance, 0.01);
    }
}
