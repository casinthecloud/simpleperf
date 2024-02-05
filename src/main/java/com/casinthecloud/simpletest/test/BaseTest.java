package com.casinthecloud.simpletest.test;

import com.casinthecloud.simpletest.execution.Context;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static com.casinthecloud.simpletest.util.Utils.println;
import static com.casinthecloud.simpletest.util.Utils.urlEncode;
import static org.apache.commons.lang3.StringUtils.substringBefore;
import static org.apache.commons.lang3.StringUtils.substringBetween;
import static org.junit.Assert.assertTrue;

/**
 * The base test.
 *
 * @author Jerome LELEU
 * @since 1.0.0
 */
public abstract class BaseTest {

    private static int SEQ = 1;

    protected int testId;

    @Setter
    private HttpClient client;

    @Getter
    @Setter
    private int maxErrors = 5;

    @Getter
    @Setter
    private int interval = 100;

    @Getter
    @Setter
    private boolean displayInfos;

    protected BaseTest() {
        testId = SEQ++;
    }

    public abstract void run(final Context ctx) throws Exception;

    protected String getLocation(final Context ctx) {
        val location = ctx.getHeaders().get("Location");
        if (location != null) {
            return location.get(0);
        }
        return null;
    }

    protected Pair<String, String> getCookie(final Context ctx, final String name) {
        val listHeaders = ctx.getHeaders().get("set-cookie");
        if (listHeaders != null) {
            for (val header : listHeaders) {
                if (header.startsWith(name)) {
                    val key = substringBefore(header, "=");
                    val value = substringBetween(header, "=", ";");
                    return new ImmutablePair<String, String>(key, value);
                }
            }
        }
        return null;
    }

    protected void execute(final Context ctx) throws Exception {
        val theResponse = client.send(ctx.getRequest(), HttpResponse.BodyHandlers.ofString());
        ctx.setHeaders(theResponse.headers().map());
        ctx.setBody(theResponse.body());
        ctx.setStatus(theResponse.statusCode());
        ctx.setResponse(theResponse);
        ctx.setRequest(null);;
        ctx.setCookies(new HashMap<String, String>());
        ctx.setFormParameters(new HashMap<String, String>());

        if (displayInfos) {
            println("Execute => " + ctx.getStatus());
        }
    }

    protected HttpRequest post(final Context ctx, final String url) throws Exception {
        if (displayInfos) {
            println("POST: " + url);
        }

        val formBodyBuilder = new StringBuilder();
        for (Map.Entry<String, String> singleEntry : ctx.getFormParameters().entrySet()) {
            if (formBodyBuilder.length() > 0) {
                formBodyBuilder.append("&");
            }
            formBodyBuilder.append(urlEncode(singleEntry.getKey()));
            formBodyBuilder.append("=");
            formBodyBuilder.append(urlEncode(singleEntry.getValue()));
        }
        val text = formBodyBuilder.toString();

        val builder = HttpRequest.newBuilder()
                .uri(new URI(url))
                .POST(HttpRequest.BodyPublishers.ofString(text))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .timeout(Duration.ofSeconds(10));

        if (ctx.getCookies() != null && !ctx.getCookies().isEmpty()) {
            val c = new StringBuilder();
            for (val cookie : ctx.getCookies().entrySet()) {
                c.append(cookie.getKey() + "=" + cookie.getValue() + "; ");
            }
            builder.setHeader("Cookie", c.toString());
        }

        return builder.build();
    }

    protected HttpRequest get(final Context ctx, final String url) throws Exception {
        if (displayInfos) {
            println("GET: " + url);
        }

        val builder =  HttpRequest.newBuilder()
                .uri(new URI(url))
                .GET()
                .timeout(Duration.ofSeconds(10));

        if (ctx.getCookies() != null && !ctx.getCookies().isEmpty()) {
            val c = new StringBuilder();
            for (val cookie : ctx.getCookies().entrySet()) {
                c.append(cookie.getKey() + "=" + cookie.getValue() + "; ");
            }
            builder.setHeader("Cookie", c.toString());
        }

        return builder.build();
    }

    protected void assertStatus(final Context ctx, final int s) {
        assertTrue("Expected HTTP " + s + " / Received: " + ctx.getStatus(), ctx.getStatus() != null && ctx.getStatus() == s);
    }

    protected void info(final String t) {
        if (displayInfos) {
            println(t);
        }
    }
}
