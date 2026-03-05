# Library usage

This guide summarizes practical SDK usage for Java application integrations.

## Authentication

### Option A: username + password

```java
import com.DeathByCaptcha.Client;
import com.DeathByCaptcha.HttpClient;

Client client = new HttpClient("username", "password");
```

### Option B: token

```java
import com.DeathByCaptcha.Client;
import com.DeathByCaptcha.HttpClient;

Client client = new HttpClient("auth_token");
```

## Choose a client

- `HttpClient`: recommended to start, simpler to debug.
- `SocketClient`: useful for higher throughput, requires TCP ports 8123-8130 open.

```java
import com.DeathByCaptcha.Client;
import com.DeathByCaptcha.SocketClient;

Client client = new SocketClient("username", "password");
```

## Core operations

### Check balance

```java
double balance = client.getBalance();
System.out.println("Balance: " + balance + " US cents");
```

### Solve captcha by type with JSON

```java
import com.DeathByCaptcha.Captcha;
import org.json.JSONObject;

JSONObject params = new JSONObject()
    .put("googlekey", "SITE_KEY")
    .put("pageurl", "https://example.com");

Captcha solved = client.decode(4, params); // 4 = reCAPTCHA v2
if (solved != null) {
    System.out.println("Captcha ID: " + solved.id);
    System.out.println("Answer: " + solved.text);
}
```

### Poll status manually

```java
Captcha status = client.getCaptcha(captchaId);
```

### Report incorrect solutions

```java
boolean reported = client.report(captchaId);
```

## Recommended production flow

1. Create the client with timeout and verbose mode based on environment.
2. Check balance before large batches.
3. Submit captcha with `decode(...)`.
4. Store `captcha.id` and `captcha.text` for traceability.
5. Report invalid answers when applicable.

## Error handling

Relevant SDK exceptions:

- `AccessDeniedException`: invalid credentials or denied access.
- `ServiceOverloadException`: temporary service overload.
- `InvalidCaptchaException`: invalid or non-existing captcha.

Example:

```java
try {
    Captcha result = client.decode(4, params);
} catch (com.DeathByCaptcha.AccessDeniedException e) {
    // invalid credentials
} catch (com.DeathByCaptcha.ServiceOverloadException e) {
    // retry with backoff
} catch (java.io.IOException | InterruptedException e) {
    // network or interruption error
}
```
