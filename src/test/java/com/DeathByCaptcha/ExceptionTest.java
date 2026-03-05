package com.DeathByCaptcha;

import org.json.JSONObject;
import org.junit.Test;
import static org.junit.Assert.*;


public class ExceptionTest {

    @Test
    public void testExceptionCreation() {
        String message = "Test exception message";
        Exception exc = new com.DeathByCaptcha.Exception(message) {};
        
        assertNotNull(exc);
        assertEquals(message, exc.getMessage());
    }

    @Test
    public void testAccessDeniedException() {
        String message = "Access Denied";
        AccessDeniedException exc = new AccessDeniedException(message);
        
        assertNotNull(exc);
        assertEquals(message, exc.getMessage());
    }

    @Test
    public void testInvalidCaptchaException() {
        String message = "Invalid Captcha";
        InvalidCaptchaException exc = new InvalidCaptchaException(message);
        
        assertNotNull(exc);
        assertEquals(message, exc.getMessage());
    }

    @Test
    public void testServiceOverloadException() {
        String message = "Service Overload";
        ServiceOverloadException exc = new ServiceOverloadException(message);
        
        assertNotNull(exc);
        assertEquals(message, exc.getMessage());
    }
}
