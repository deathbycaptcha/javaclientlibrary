
# DeathByCaptcha Java Client Library - API Usage

This guide covers practical usage of the Java client library: authentication, API methods, supported CAPTCHA types, error handling, and production best practices.

For installation and project structure, see the [getting-started](getting-started.md).

---

## How to Use the DBC API Clients

### Thread-Safety Notes

Java clients are **thread-safe**, which means it is perfectly fine to share a client between multiple threads (although in heavily multithreaded applications it is a better idea to keep a pool of clients).

### Common Client Interface

All clients must be instantiated with two string arguments: your DeathByCaptcha account's **username** and **password**.

```java
import com.DeathByCaptcha.Client;
import com.DeathByCaptcha.HttpClient;
import com.DeathByCaptcha.SocketClient;

// HTTP Client (port 80/443)
Client httpClient = new HttpClient("your_username", "your_password");

// Socket Client (ports 8123-8130, faster)
Client socketClient = new SocketClient("your_username", "your_password");
```

All clients provide a few methods to handle your CAPTCHAs and your DBC account. Below you will find those methods' short summary and signatures.

---

### API Methods Reference

#### `getBalance()`

Fetches your current DBC credit balance (in US cents).

```java
double com.DeathByCaptcha.Client.getBalance()
```

**Example:**
```java
double balance = client.getBalance();
System.out.println("Balance: " + balance + " US cents");
```

---

#### `upload()`

Uploads a CAPTCHA to the DBC service for solving, returns uploaded CAPTCHA details on success, `null` otherwise.

**Signatures:**

```java
// Upload from byte array
com.DeathByCaptcha.Captcha upload(byte[] imageData)

// Upload from input stream
com.DeathByCaptcha.Captcha upload(InputStream imageStream)

// Upload from File object
com.DeathByCaptcha.Captcha upload(File imageFile)

// Upload from file path
com.DeathByCaptcha.Captcha upload(String imageFileName)

// Upload with additional parameters (for image coordinates/groups)
com.DeathByCaptcha.Captcha upload(byte[] img, String challenge, int type, byte[] banner, String bannerExt, String grid)

// Upload by type with JSON parameters (for tokens, etc.)
com.DeathByCaptcha.Captcha upload(int type, JSONObject json)
```

---

#### `getCaptcha()`

Fetches uploaded CAPTCHA details, returns `null` on failures.

```java
com.DeathByCaptcha.Captcha getCaptcha(int captchaId)

com.DeathByCaptcha.Captcha getCaptcha(com.DeathByCaptcha.Captcha captcha)
```

**Example:**
```java
Captcha status = client.getCaptcha(captchaId);
if (status != null && status.isSolved()) {
    System.out.println("Solution: " + status.text);
}
```

---

#### `report()`

Reports incorrectly solved CAPTCHA for refund, returns `true` on success, `false` otherwise.

**⚠️ Important**: Please make sure the CAPTCHA you're reporting was in fact incorrectly solved. Do not just report them thoughtlessly, or else you'll be flagged as abuser and banned.

```java
boolean report(int captchaId)

boolean report(com.DeathByCaptcha.Captcha captcha)
```

**Example:**
```java
if (captcha != null && !isCorrectSolution(captcha.text)) {
    boolean reported = client.report(captcha);
    System.out.println("Reported: " + reported);
}
```

---

#### `decode()`

This method uploads a CAPTCHA, then polls for its status until it's solved or times out. Returns solved CAPTCHA details on success, `null` otherwise.

**For image-based CAPTCHAs (coordinates):**
```java
com.DeathByCaptcha.Captcha decode(byte[] imageData, int timeout)
com.DeathByCaptcha.Captcha decode(InputStream imageStream, int timeout)
com.DeathByCaptcha.Captcha decode(File imageFile, int timeout)
com.DeathByCaptcha.Captcha decode(String imageFileName, int timeout)
```

**For image groups:**
```java
com.DeathByCaptcha.Captcha decode(String fn, int type, String banner_fn, String bannerExt, int timeout)
com.DeathByCaptcha.Captcha decode(String fn, int type, String banner_fn, String bannerExt, String grid, int timeout)
```

**For reCAPTCHA v2:**
```java
com.DeathByCaptcha.Captcha decode(String googlekey, String pageurl, String data_s, int timeout)
com.DeathByCaptcha.Captcha decode(String proxy, String proxytype, String googlekey, String pageurl, int timeout)
```

**For reCAPTCHA v3:**
```java
com.DeathByCaptcha.Captcha decode(String googlekey, String pageurl, String action, double min_score, int timeout)
com.DeathByCaptcha.Captcha decode(String proxy, String proxytype, String googlekey, String pageurl, String action, double min_score, int timeout)
```

**Generic (with type and JSON):**
```java
com.DeathByCaptcha.Captcha decode(int type, String key, String pageurl)
com.DeathByCaptcha.Captcha decode(int type, String proxy, String proxytype, String key, String pageurl)
com.DeathByCaptcha.Captcha decode(int type, JSONObject json)
```

---

### CAPTCHA Objects

Java client wraps CAPTCHA details in `com.DeathByCaptcha.Captcha`, exposing CAPTCHA details through the following properties and methods:

- **`int id`**: CAPTCHA numeric ID
- **`String text`**: CAPTCHA solution text
- **`boolean isUploaded()`**: Flag showing whether the CAPTCHA was uploaded
- **`boolean isSolved()`**: Flag showing whether the CAPTCHA was solved
- **`boolean isCorrect()`**: Flag showing whether the CAPTCHA was solved correctly

**Example:**
```java
if (captcha != null) {
    System.out.println("CAPTCHA ID: " + captcha.id);
    System.out.println("Uploaded: " + captcha.isUploaded());
    System.out.println("Solved: " + captcha.isSolved());
    System.out.println("Solution: " + captcha.text);
}
```

---

### Basic Usage Example

```java
import com.DeathByCaptcha.AccessDeniedException;
import com.DeathByCaptcha.Captcha;
import com.DeathByCaptcha.Client;
import com.DeathByCaptcha.SocketClient;
import com.DeathByCaptcha.HttpClient;

class Example {
    public static void main(String[] args) throws Exception {
        
        // Put your DeathByCaptcha account username and password here.
        // Use HttpClient for HTTP API or SocketClient for socket API.
        Client client = new SocketClient("your_username", "your_password");
        
        try {
            // Check balance
            double balance = client.getBalance();
            System.out.println("Balance: " + balance + " US cents");

            // Put your CAPTCHA file name, or file object, or arbitrary input stream,
            // or an array of bytes, and optional solving timeout (in seconds) here:
            Captcha captcha = client.decode("captcha.jpg", 60);
            
            if (captcha != null) {
                // The CAPTCHA was solved; captcha.id property holds its numeric ID,
                // and captcha.text holds its text.
                System.out.println("CAPTCHA " + captcha.id + " solved: " + captcha.text);

                // Check if the CAPTCHA was incorrectly solved and report it
                if (/* your validation logic */) {
                    client.report(captcha);
                }
            }
        } catch (AccessDeniedException e) {
            // Access to DBC API denied, check your credentials and/or balance
            System.err.println("Access denied: " + e.getMessage());
        }
    }
}
```

---

## Supported CAPTCHA Types

The DeathByCaptcha API supports solving the following CAPTCHA types:

| Type ID | CAPTCHA Type | Parameters | Use Case |
|---------|-------------|-----------|----------|
| 0 | Standard Image | `captchafile` or `captcha` (base64) | Basic image CAPTCHA |
| 2 | reCAPTCHA Coordinates | `captchafile` (screenshot) | Legacy reCAPTCHA coordinate selection |
| 3 | Image Group | `banner`, `banner_text`, `captchafile` | Select images matching a description |
| 4 | Token (v2) | `token_params` | reCAPTCHA v2 token solving |
| 5 | reCAPTCHA v3 | `token_params` + `action`, `min_score` | reCAPTCHA v3 with risk scoring |
| 8 | Geetest v3 | `geetest_params` | Geetest v3 verification |
| 9 | Geetest v4 | `geetest_params` | Geetest v4 verification |
| 11 | Text CAPTCHA | `textcaptcha` | Text-based question solving |
| 12 | Cloudflare Turnstile | `turnstile_params` | Cloudflare Turnstile token |
| 13 | Audio CAPTCHA | `audio` (base64), `language` | Audio CAPTCHA solving |
| 14 | Lemin | `lemin_params` | Lemin CAPTCHA |
| 15 | Capy | `capy_params` | Capy CAPTCHA |
| 16 | Amazon WAF | `waf_params` | Amazon WAF verification |
| 17 | Siara | `siara_params` | Siara CAPTCHA |
| 18 | Mtcaptcha | `mtcaptcha_params` | Mtcaptcha CAPTCHA |
| 19 | Cutcaptcha | `cutcaptcha_params` | Cutcaptcha CAPTCHA |
| 20 | Friendly Captcha | `friendly_params` | Friendly Captcha |
| 21 | Datadome | `datadome_params` | Datadome verification |
| 23 | Tencent | `tencent_params` | Tencent CAPTCHA |
| 24 | ATB | `atb_params` | ATB CAPTCHA |
| 25 | reCAPTCHA v2 Enterprise | `token_enterprise_params` | reCAPTCHA v2 Enterprise tokens |

---

### Type 2: Coordinates API (reCAPTCHA Coordinates)

**What is it?**  
Provided a screenshot of a reCAPTCHA challenge, the API returns a group of coordinates to click.

**API Endpoint:**  
`POST http://api.dbcapi.me/api/captcha`

**Parameters:**
- **`username`**: Your DBC account username
- **`password`**: Your DBC account password
- **`captchafile`**: Base64 encoded or Multipart file contents with a valid reCAPTCHA screenshot
- **`type=2`**: Specifies this is a Coordinates API request

**Response:**
- **`captcha`**: ID of the provided captcha
- **`is_correct`**: (0 or 1) specifying if the captcha was marked as incorrect or unreadable
- **`text`**: A JSON-like nested list with all the coordinates (x, y) to click relative to the image
  
  Example: `[[23.21, 82.11]]` where X=23.21 and Y=82.11

**Java Example:**
```java
import org.json.JSONObject;
import com.DeathByCaptcha.Captcha;

// Using decode method with image file
Captcha captcha = client.decode("recaptcha_screenshot.png", 60);
if (captcha != null) {
    System.out.println("Coordinates: " + captcha.text);
    // Parse coordinates: [[x, y]]
}
```

---

### Type 3: Image Group API (reCAPTCHA Image Group)

**What is it?**  
Provided a group of (base64-encoded) images, the API returns the indexes of the images to click.

**API Endpoint:**  
`POST http://api.dbcapi.me/api/captcha`

**Parameters:**
- **`username`**: Your DBC account username
- **`password`**: Your DBC account password
- **`captchafile`**: Base64 encoded file contents. Send each image in a single "captchafile" parameter. The order matters
- **`banner`**: Base64 encoded banner image (the example image that appears on the upper right)
- **`banner_text`**: The banner text (the text that appears on the upper left)
- **`type=3`**: Specifies this is an Image Group API request
- **`grid`**: (Optional) Grid parameter specifies what grid individual images are aligned to (e.g., "2x4" for 4 rows with 2 images each). If not supplied, DBC will attempt to autodetect

**Response:**
- **`captcha`**: ID of the provided captcha
- **`is_correct`**: (0 or 1) specifying if the captcha was marked as incorrect or unreadable
- **`text`**: A JSON-like list of indexes for each image that should be clicked
  
  Example: `[1, 4, 6]` where images 1, 4, and 6 should be clicked (counting from 1, left to right, top to bottom)

**Java Example:**
```java
// With grid specification
Captcha captcha = client.decode(
    "captcha.jpg",      // main image file
    3,                  // type 3 = image group
    "banner.jpg",       // banner file
    "jpg",              // banner extension
    "2x4",              // grid layout
    60                  // timeout in seconds
);

if (captcha != null) {
    System.out.println("Click images at indexes: " + captcha.text);
    // Parse indexes: [1, 4, 6]
}
```

---

### Type 4: reCAPTCHA v2 Token API

**What is it?**  
New reCAPTCHA challenges that typically require the user to identify and click on certain images. Provided a site URL and site key, the API returns a token that you will use to submit the form in the page with the reCAPTCHA challenge.

**API Endpoint:**  
`POST http://api.dbcapi.me/api/captcha`

**Parameters:**
- **`username`**: Your DBC account username
- **`password`**: Your DBC account password
- **`type=4`**: Specifies this is a reCAPTCHA v2 Token API request
- **`token_params`**: JSON payload with the following structure:
  - **`proxy`**: (Optional) Your proxy URL and credentials. Examples:
    - `http://127.0.0.1:3128`
    - `http://user:password@127.0.0.1:3128`
  - **`proxytype`**: (Required if proxy is provided) Proxy connection protocol (e.g., "HTTP")
  - **`googlekey`**: The Google reCAPTCHA site key of the website (e.g., `6Le-wvkSAAAAAPBMRTvw0Q4Muexq9bi0DJwx_mJ-`)
  - **`pageurl`**: The URL of the page with the reCAPTCHA challenges. Must include the path where reCAPTCHA is loaded
  - **`data-s`**: (Optional) Only required for Google search tokens. Use the `data-s` value inside the Google search response HTML

**Note:** The **`proxy`** parameter is optional but strongly recommended to prevent token rejection due to IP inconsistencies.

**Full `token_params` Example:**
```json
{
  "proxy": "http://127.0.0.1:3128",
  "proxytype": "HTTP",
  "googlekey": "6Le-wvkSAAAAAPBMRTvw0Q4Muexq9bi0DJwx_mJ-",
  "pageurl": "http://test.com/path_with_recaptcha"
}
```

**For Google Search CAPTCHAs:**
```json
{
  "googlekey": "6Le-wvkSA...",
  "pageurl": "https://www.google.com/search?q=...",
  "data-s": "IUdfh4rh0sd..."
}
```

**Response:**
- **`captcha`**: ID of the provided captcha
- **`text`**: The reCAPTCHA token (valid for one use, 2-minute lifespan)

Example token:
```
03AOPBWq_RPO2vLzyk0h8gH0cA2X4v3tpYCPZR6Y4yxKy1s3Eo7CHZRQntxrdsaD2H0e6S3547xi1FlqJB4rob46J0-wfZMj6YpyVa0WGCfpWzBWcLn7tO_EYsvEC_3kfLNINWa5LnKrnJTDXTOz-JuCKvEXx0EQqzb0OU4z2np4uyu79lc_NdvL0IRFc3Cslu6UFV04CIfqXJBWCE5MY0Ag918r14b43ZdpwHSaVVrUqzCQMCybcGq0yxLQf9eSexFiAWmcWLI5nVNA81meTXhQlyCn5bbbI2IMSEErDqceZjf1mX3M67BhIb4
```

**Java Example:**
```java
import org.json.JSONObject;

// Basic usage without proxy
JSONObject params = new JSONObject()
    .put("googlekey", "6Le-wvkSAAAAAPBMRTvw0Q4Muexq9bi0DJwx_mJ-")
    .put("pageurl", "https://example.com/page-with-recaptcha");

Captcha captcha = client.decode(4, params);
if (captcha != null) {
    System.out.println("reCAPTCHA v2 token: " + captcha.text);
    // Use this token in your form submission
}

// With proxy
JSONObject paramsWithProxy = new JSONObject()
    .put("proxy", "http://user:pass@proxy.com:8080")
    .put("proxytype", "HTTP")
    .put("googlekey", "6Le-wvkSAAAAAPBMRTvw0Q4Muexq9bi0DJwx_mJ-")
    .put("pageurl", "https://example.com/page-with-recaptcha");

Captcha captchaWithProxy = client.decode(4, paramsWithProxy);
```

**Convenience Methods:**
```java
// Without proxy
Captcha captcha = client.decode(
    "6Le-wvkSAAAAAPBMRTvw0Q4Muexq9bi0DJwx_mJ-",  // googlekey
    "https://example.com/page",                      // pageurl
    "",                                              // data-s (empty if not Google search)
    60                                               // timeout
);

// With proxy
Captcha captcha = client.decode(
    "http://proxy:8080",                             // proxy
    "HTTP",                                          // proxytype
    "6Le-wvkSAAAAAPBMRTvw0Q4Muexq9bi0DJwx_mJ-",    // googlekey
    "https://example.com/page",                      // pageurl
    60                                               // timeout
);
```

---

### Type 5: reCAPTCHA v3 API

**What is it?**  
reCAPTCHA v3 returns a score for each user request (0.0 to 1.0) to evaluate if the user is a bot or human. Lower scores closer to 0 indicate bots. This API is similar to reCAPTCHA v2 but includes two additional parameters: **`action`** and **`min_score`**.

**What is `action`?**  
The `action` parameter is used to separate different captcha validations on the website (e.g., "login", "register", "checkout", etc.). To find this value, inspect the JavaScript code looking for the `grecaptcha.execute()` function call:

```javascript
grecaptcha.execute('6Lc2fhwTAAAAAGatXTzFYfvlQMI2T7B6ji8UVV_f', {action: 'login'})
```

If not found, the default value is "verify".

**What is `min_score`?**  
The minimal score needed for the captcha resolution. We recommend using **0.3** as the `min_score` value, as scores higher than 0.3 are hard to get.

**API Endpoint:**  
`POST http://api.dbcapi.me/api/captcha`

**Parameters:**
- **`username`**: Your DBC account username
- **`password`**: Your DBC account password
- **`type=5`**: Specifies this is a reCAPTCHA v3 API request
- **`token_params`**: JSON payload with the following structure:
  - **`proxy`**: (Optional) Your proxy URL and credentials
  - **`proxytype`**: (Required if proxy is provided) Proxy connection protocol (e.g., "HTTP")
  - **`googlekey`**: The Google reCAPTCHA site key
  - **`pageurl`**: The URL of the page with the reCAPTCHA challenges
  - **`action`**: The action name (e.g., "login", "verify")
  - **`min_score`**: The minimal score, usually 0.3

**Full `token_params` Example:**
```json
{
  "proxy": "http://127.0.0.1:3128",
  "proxytype": "HTTP",
  "googlekey": "6Le-wvkSAAAAAPBMRTvw0Q4Muexq9bi0DJwx_mJ-",
  "pageurl": "http://test.com/path_with_recaptcha",
  "action": "login",
  "min_score": 0.3
}
```

**Response:**
- **`captcha`**: ID of the provided captcha
- **`text`**: The reCAPTCHA v3 token (valid for one use, 1-minute lifespan)

**Java Example:**
```java
import org.json.JSONObject;

// Basic usage
JSONObject params = new JSONObject()
    .put("googlekey", "6Le-wvkSAAAAAPBMRTvw0Q4Muexq9bi0DJwx_mJ-")
    .put("pageurl", "https://example.com/page-with-recaptcha")
    .put("action", "login")
    .put("min_score", 0.3);

Captcha captcha = client.decode(5, params);
if (captcha != null) {
    System.out.println("reCAPTCHA v3 token: " + captcha.text);
    // Use this token in your form submission
}

// With proxy
JSONObject paramsWithProxy = new JSONObject()
    .put("proxy", "http://user:pass@proxy.com:8080")
    .put("proxytype", "HTTP")
    .put("googlekey", "6Le-wvkSAAAAAPBMRTvw0Q4Muexq9bi0DJwx_mJ-")
    .put("pageurl", "https://example.com/page-with-recaptcha")
    .put("action", "verify")
    .put("min_score", 0.3);

Captcha captchaWithProxy = client.decode(5, paramsWithProxy);
```

**Convenience Methods:**
```java
// Without proxy
Captcha captcha = client.decode(
    "6Le-wvkSAAAAAPBMRTvw0Q4Muexq9bi0DJwx_mJ-",  // googlekey
    "https://example.com/page",                      // pageurl
    "login",                                         // action
    0.3,                                             // min_score
    60                                               // timeout
);

// With proxy
Captcha captcha = client.decode(
    "http://proxy:8080",                             // proxy
    "HTTP",                                          // proxytype
    "6Le-wvkSAAAAAPBMRTvw0Q4Muexq9bi0DJwx_mJ-",    // googlekey
    "https://example.com/page",                      // pageurl
    "login",                                         // action
    0.3,                                             // min_score
    60                                               // timeout
);
```

---

## Error Handling

The client library provides specific exceptions for different error scenarios:

- **`AccessDeniedException`**: Invalid credentials or denied access (check username/password and balance)
- **`ServiceOverloadException`**: Temporary service overload (retry with exponential backoff)
- **`InvalidCaptchaException`**: Invalid or non-existing captcha
- **`IOException`**: Network connectivity issues
- **`InterruptedException`**: Thread interruption during polling

**Example with comprehensive error handling:**

```java
import com.DeathByCaptcha.AccessDeniedException;
import com.DeathByCaptcha.ServiceOverloadException;
import com.DeathByCaptcha.InvalidCaptchaException;

try {
    Captcha result = client.decode(4, params);
    if (result != null && result.isSolved()) {
        System.out.println("Solution: " + result.text);
    }
} catch (AccessDeniedException e) {
    System.err.println("Invalid credentials or insufficient balance");
    // Check your username, password, and account balance
} catch (ServiceOverloadException e) {
    System.err.println("Service temporarily overloaded, retry later");
    // Implement exponential backoff retry logic
} catch (InvalidCaptchaException e) {
    System.err.println("Invalid captcha ID or captcha not found");
} catch (IOException e) {
    System.err.println("Network error: " + e.getMessage());
    // Check network connectivity
} catch (InterruptedException e) {
    System.err.println("Operation interrupted");
    Thread.currentThread().interrupt();
}
```

---

## Recommended Production Flow

1. **Initialize the client** with appropriate timeout settings based on your environment
2. **Check balance** before processing large batches to avoid mid-batch failures
3. **Submit captcha** using the appropriate `decode()` method for your captcha type
4. **Store `captcha.id` and `captcha.text`** for traceability and debugging
5. **Validate the solution** in your application context
6. **Report incorrect solutions** when applicable (but only when genuinely incorrect)
7. **Implement retry logic** with exponential backoff for transient errors
8. **Monitor balance** and set up alerts for low balance conditions

**Production Example:**

```java
public class ProductionCaptchaSolver {
    private final Client client;
    private final double minBalance = 100.0; // minimum 100 cents ($1)
    
    public ProductionCaptchaSolver(String username, String password) {
        this.client = new SocketClient(username, password);
    }
    
    public String solveWithRetry(JSONObject params, int maxRetries) {
        // Check balance first
        try {
            double balance = client.getBalance();
            if (balance < minBalance) {
                throw new RuntimeException("Insufficient balance: " + balance + " cents");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to check balance", e);
        }
        
        // Attempt solving with retry logic
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                Captcha captcha = client.decode(4, params);
                if (captcha != null && captcha.isSolved()) {
                    // Log for audit trail
                    System.out.println("Solved captcha ID " + captcha.id);
                    return captcha.text;
                }
            } catch (ServiceOverloadException e) {
                // Exponential backoff
                long backoff = (long) (Math.pow(2, attempt) * 1000);
                System.out.println("Service overloaded, retry in " + backoff + "ms");
                Thread.sleep(backoff);
            } catch (AccessDeniedException e) {
                throw new RuntimeException("Access denied", e);
            } catch (Exception e) {
                System.err.println("Attempt " + attempt + " failed: " + e.getMessage());
            }
        }
        
        throw new RuntimeException("Failed to solve after " + maxRetries + " attempts");
    }
}
```

---

## Additional Resources

- For end-to-end usage patterns with different CAPTCHA types, see [Samples usage](samples.md).
- For browser automation integration, see [Selenium integration](selenium-integration.md).
- **Official API Documentation**: [https://deathbycaptcha.com/api](https://deathbycaptcha.com/api)
- **API Metadata Repository**: [https://github.com/deathbycaptcha/deathbycaptcha-agent-api-metadata/](https://github.com/deathbycaptcha/deathbycaptcha-agent-api-metadata/)
