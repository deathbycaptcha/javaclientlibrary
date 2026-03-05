"""Tests for DeathByCaptcha Socket API."""
import base64
import json
import socket
import pytest


HOST = "api.dbcapi.me"
PORTS = list(range(8123, 8131))
TIMEOUT = 5


def try_connect():
    """Attempt connection to any available DBC socket port."""
    for port in PORTS:
        try:
            s = socket.create_connection((HOST, port), timeout=TIMEOUT)
            return s
        except (socket.timeout, ConnectionRefusedError):
            continue
    pytest.fail(f"Could not connect to any socket port in range {PORTS[0]}-{PORTS[-1]}")


def send_recv(sock, msg):
    """Send a message and receive the response."""
    sock.sendall((json.dumps(msg) + "\r\n").encode())
    data = sock.recv(4096)
    return json.loads(data.decode())


class TestSocketAPI:
    """Socket API tests."""
    
    @pytest.fixture(scope="function")
    def socket_conn(self):
        """Establish a socket connection for the test."""
        sock = try_connect()
        yield sock
        try:
            sock.close()
        except:
            pass
    
    def test_login(self, socket_conn, dbc_credentials):
        """Test socket login command."""
        req = {
            "cmd": "login",
            "username": dbc_credentials["username"],
            "password": dbc_credentials["password"],
            "version": "DBC-Validator/1.0"
        }
        resp = send_recv(socket_conn, req)
        
        assert resp.get("user", 0) != 0, "user ID should not be 0"
    
    def test_user_info(self, socket_conn, dbc_credentials):
        """Test retrieving user info after login."""
        # First login
        req = {
            "cmd": "login",
            "username": dbc_credentials["username"],
            "password": dbc_credentials["password"],
            "version": "DBC-Validator/1.0"
        }
        resp = send_recv(socket_conn, req)
        assert resp.get("user", 0) != 0, "login failed"
        
        # Then get user info
        resp = send_recv(socket_conn, {"cmd": "user", "version": "DBC-Validator/1.0"})
        assert "balance" in resp, "balance not in user info response"
    
    def test_regular_upload(self, socket_conn, dbc_credentials, sample_image):
        """Test regular image upload via socket."""
        # Login
        req = {
            "cmd": "login",
            "username": dbc_credentials["username"],
            "password": dbc_credentials["password"],
            "version": "DBC-Validator/1.0"
        }
        send_recv(socket_conn, req)
        
        # Upload
        with open(sample_image, "rb") as f:
            b64 = base64.b64encode(f.read()).decode("ascii")
        
        resp = send_recv(socket_conn, {
            "cmd": "upload",
            "version": "DBC-Validator/1.0",
            "captcha": b64
        })
        
        assert resp.get("status") != 501, "501 response indicates malformed request"
        assert "captcha" in resp or "error" in resp, "invalid response structure"
    
    def test_coordinates_upload(self, socket_conn, dbc_credentials, sample_image):
        """Test coordinates upload via socket."""
        # Login
        req = {
            "cmd": "login",
            "username": dbc_credentials["username"],
            "password": dbc_credentials["password"],
            "version": "DBC-Validator/1.0"
        }
        send_recv(socket_conn, req)
        
        # Upload
        with open(sample_image, "rb") as f:
            b64 = base64.b64encode(f.read()).decode("ascii")
        
        resp = send_recv(socket_conn, {
            "cmd": "upload",
            "version": "DBC-Validator/1.0",
            "captcha": b64,
            "type": 2
        })
        
        assert resp.get("status") != 501, "501 response indicates malformed request"
        assert "captcha" in resp or "error" in resp, "invalid response structure"
    
    def test_image_group_upload(self, socket_conn, dbc_credentials, sample_image):
        """Test image group upload via socket."""
        # Login
        req = {
            "cmd": "login",
            "username": dbc_credentials["username"],
            "password": dbc_credentials["password"],
            "version": "DBC-Validator/1.0"
        }
        send_recv(socket_conn, req)
        
        # Upload
        with open(sample_image, "rb") as f:
            b64 = base64.b64encode(f.read()).decode("ascii")
        
        resp = send_recv(socket_conn, {
            "cmd": "upload",
            "version": "DBC-Validator/1.0",
            "captcha": b64,
            "type": 3,
            "banner": "",
            "banner_text": "example"
        })
        
        assert resp.get("status") != 501, "501 response indicates malformed request"
        assert "captcha" in resp or "error" in resp, "invalid response structure"
    
    def test_token_api_recaptcha_v2(self, socket_conn, dbc_credentials):
        """Test token-based upload (reCAPTCHA v2) via socket."""
        # Login
        req = {
            "cmd": "login",
            "username": dbc_credentials["username"],
            "password": dbc_credentials["password"],
            "version": "DBC-Validator/1.0"
        }
        send_recv(socket_conn, req)
        
        # Upload
        token_params = {
            "googlekey": "6Le-wvkSAAAAAPBMRTvw0Q4Muexq9bi0DJwx_mJ-",
            "pageurl": "http://test.com/path"
        }
        resp = send_recv(socket_conn, {
            "cmd": "upload",
            "version": "DBC-Validator/1.0",
            "type": 4,
            "token_params": token_params
        })
        
        assert "captcha" in resp or "error" in resp, "invalid response structure"
        if "captcha" in resp:
            assert isinstance(resp.get("text"), str), "token text should be string"
    
    def test_recaptcha_v3(self, socket_conn, dbc_credentials):
        """Test reCAPTCHA v3 upload via socket."""
        # Login
        req = {
            "cmd": "login",
            "username": dbc_credentials["username"],
            "password": dbc_credentials["password"],
            "version": "DBC-Validator/1.0"
        }
        send_recv(socket_conn, req)
        
        # Upload
        token_params = {
            "googlekey": "6Le-wvkSAAAAAPBMRTvw0Q4Muexq9bi0DJwx_mJ-",
            "pageurl": "http://test.com/path",
            "action": "login",
            "min_score": 0.3
        }
        resp = send_recv(socket_conn, {
            "cmd": "upload",
            "version": "DBC-Validator/1.0",
            "type": 5,
            "token_params": token_params
        })
        
        assert "captcha" in resp or "error" in resp, "invalid response structure"
        if "captcha" in resp:
            assert isinstance(resp.get("text"), str), "token text should be string"
