# `simpletest`: A simple framework for functional/performance tests (JDK 11)

### Launch a test:

```java
public class Main {

    public static void main(final String... args) throws Exception {
        val exec = new Execution(1, 5000, () -> { return new CasLoginTest(); });
        exec.launch();
    }
}
```

### Output:

```shell
Execution started: 1 thread, 5000 iterations per thread
aaaaaaAaaaaaaAaaaaaaAaaaaaaaAaaaaaaAaaaaaaAaaaaaaAaaaaaaaAaaaaaaAaaaaaaAaaaaaaAaaaaaaaAaaaaaaAaaaaaaAaaaaaaAaaaaaaaAaaaaaaAaaaaaaAaaaaaaAaaaaaaaA
Execution ended and took: 34 s
```

### Test example (simple login with a CAS server + service ticket validation):

```java
public class CasLoginTest extends CasTest {

    public void run() throws Exception {

        startTimer();

        var loginUrl = "http://localhost:8080/cas/login?service=http://localhost:8081/";

        _request = get(loginUrl);
        execute();
        val webflow = between(_body, "name=\"execution\" value=\"", "\"/>");
        assertStatus(200);

        _data.put("username", "jleleu");
        _data.put("password", "jleleu");
        _data.put("execution", webflow);
        _data.put("_eventId", "submit");
        _data.put("geolocation", "");
        _request = post(loginUrl);
        execute();
        assertStatus(302);
        val callbackUrl = getLocation();
        val st = after(getLocation(), "ticket=");

        var validateUrl = "http://localhost:8080/cas/p3/serviceValidate";
        validateUrl = addUrlParameter(validateUrl, "service", serviceUrl);
        validateUrl = addUrlParameter(validateUrl, "ticket", st);

        _request = get(validateUrl);
        execute();
        assertStatus(200);
        saveTimer();
    }
}
```