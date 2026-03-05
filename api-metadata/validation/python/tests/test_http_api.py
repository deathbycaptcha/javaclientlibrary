"""Tests for DeathByCaptcha HTTP API."""
import json
import pytest
import requests


BASE_URL = "https://api.dbcapi.me/api"  # Use HTTPS for security


def post(path, data=None, files=None):
    """Helper to POST to the HTTP API."""
    url = f"{BASE_URL}/{path.lstrip('/')}"
    headers = {"Accept": "application/json", "User-Agent": "DBC-Validator/1.0"}
    resp = requests.post(url, data=data, files=files, headers=headers)
    if resp.status_code == 501:
        pytest.fail(f"501 Not Implemented for {path} with data {data}")
    resp.raise_for_status()
    return resp.json()


class TestHTTPAuth:
    """HTTP API authentication and user management tests."""
    
    def test_user_login(self, dbc_credentials):
        """Test user creation and authentication."""
        data = dbc_credentials.copy()
        r = post("", data=data)
        
        assert "user" in r, "user field missing in response"
        assert r.get("user") != 0, "user ID should not be 0"
    
    def test_balance(self, dbc_credentials):
        """Test balance retrieval."""
        data = dbc_credentials.copy()
        r = post("", data=data)
        
        assert "balance" in r, "balance field missing"
        assert float(r.get("balance", 0)) >= 0, "balance should be non-negative"


class TestHTTPUploads:
    """HTTP API captcha upload tests."""
    
    def test_regular_image_upload(self, dbc_credentials, sample_image):
        """Test uploading a regular image captcha."""
        data = dbc_credentials.copy()
        
        with open(sample_image, "rb") as f:
            files = {"captchafile": f}
            r = post("captcha", data=data, files=files)
        
        assert "captcha" in r or "error" in r, "invalid response structure"
    
    def test_coordinates_upload(self, dbc_credentials, sample_image):
        """Test uploading a coordinates-based captcha."""
        data = dbc_credentials.copy()
        data["type"] = 2
        
        with open(sample_image, "rb") as f:
            files = {"captchafile": f}
            r = post("captcha", data=data, files=files)
        
        assert "captcha" in r or "error" in r, "invalid response structure"
    
    def test_image_group_upload(self, dbc_credentials, sample_image, banner_image):
        """Test uploading an image group (with banner) captcha."""
        data = dbc_credentials.copy()
        data["type"] = 3
        data["banner_text"] = "example"
        
        with open(sample_image, "rb") as f_img:
            with open(banner_image, "rb") as f_banner:
                files = {"captchafile": f_img, "banner": f_banner}
                r = post("captcha", data=data, files=files)
        
        assert "captcha" in r or "error" in r, "invalid response structure"
    
    def test_token_api_recaptcha_v2(self, dbc_credentials):
        """Test token-based upload (reCAPTCHA v2)."""
        data = dbc_credentials.copy()
        data["type"] = 4
        token_params = {
            "googlekey": "6Le-wvkSAAAAAPBMRTvw0Q4Muexq9bi0DJwx_mJ-",
            "pageurl": "http://test.com/path"
        }
        data["token_params"] = json.dumps(token_params)
        
        r = post("captcha", data=data)
        
        assert "captcha" in r or "error" in r, "invalid response structure"
        if "captcha" in r:
            assert isinstance(r.get("text"), str), "token text should be string"
    
    def test_recaptcha_v3(self, dbc_credentials):
        """Test reCAPTCHA v3 upload."""
        data = dbc_credentials.copy()
        data["type"] = 5
        token_params = {
            "googlekey": "6Le-wvkSAAAAAPBMRTvw0Q4Muexq9bi0DJwx_mJ-",
            "pageurl": "http://test.com/path",
            "action": "login",
            "min_score": 0.3
        }
        data["token_params"] = json.dumps(token_params)
        
        r = post("captcha", data=data)
        
        assert "captcha" in r or "error" in r, "invalid response structure"
        if "captcha" in r:
            assert isinstance(r.get("text"), str), "token text should be string"
