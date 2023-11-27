package com.casinthecloud.simpletest.test;

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
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static com.casinthecloud.simpletest.util.Utils.*;

/**
 * The base test.
 *
 * @author Jerome LELEU
 * @since 1.0.0
 */
public abstract class BaseTest {

    @Setter
    private HttpClient client;

    private Long t0;

    @Setter
    private AtomicLong time;

    protected HttpRequest _request = null;

    protected HttpResponse _response = null;

    protected Map<String, List<String>> _headers = new HashMap<>();

    protected String _body = null;

    protected Map<String, String> _data = new HashMap<>();

    protected Map<String, String> _cookies = new HashMap<>();

    protected Integer _status;

    @Getter
    @Setter
    private int maxErrors = 5;

    @Getter
    @Setter
    private int smallInterval = 40;

    @Getter
    @Setter
    private int bigInterval = 250;

    @Getter
    @Setter
    private boolean displayInfos;

    public abstract void run() throws Exception;

    protected void saveTimer() {
        long t1 = System.currentTimeMillis();
        time.addAndGet(t1 - t0);
        t0 = null;
    }

    protected void startTimer() {
        t0 = System.currentTimeMillis();
    }

    protected String getLocation() {
        return _headers.get("Location").get(0);
    }

    protected Pair<String, String> getCookie(final String name) {
        val listHeaders = _headers.get("set-cookie");
        for (val header : listHeaders) {
            if (header.startsWith(name)) {
                val key = before(header, "=");
                val value = between(header, "=", ";");
                return new ImmutablePair<String, String>(key, value);
            }
        }
        return null;
    }

    protected void execute() throws Exception {
        val theResponse = client.send(_request, HttpResponse.BodyHandlers.ofString());
        _headers = theResponse.headers().map();
        _body = theResponse.body();
        _status = theResponse.statusCode();
        _response = theResponse;
        _request = null;
        _cookies = new HashMap<String, String>();
        _data = new HashMap<String, String>();

        if (displayInfos) {
            println("Execute => " + _status);
        }
    }

    protected HttpRequest post(final String url) throws Exception {
        if (displayInfos) {
            println("POST : " + url);
        }

        val formBodyBuilder = new StringBuilder();
        for (Map.Entry<String, String> singleEntry : _data.entrySet()) {
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

        if (_cookies != null && !_cookies.isEmpty()) {
            val c = new StringBuilder();
            for (val cookie : _cookies.entrySet()) {
                c.append(cookie.getKey() + "=" + cookie.getValue() + "; ");
            }
            builder.setHeader("Cookie", c.toString());
        }

        return builder.build();
    }

    protected HttpRequest get(final String url) throws Exception {
        if (displayInfos) {
            println("GET : " + url);
        }

        val builder =  HttpRequest.newBuilder()
                .uri(new URI(url))
                .GET()
                .timeout(Duration.ofSeconds(10));

        if (_cookies != null && !_cookies.isEmpty()) {
            val c = new StringBuilder();
            for (val cookie : _cookies.entrySet()) {
                c.append(cookie.getKey() + "=" + cookie.getValue() + "; ");
            }
            builder.setHeader("Cookie", c.toString());
        }

        return builder.build();
    }

    protected void assertStatus(final int s) {
        if (_status == null || _status != s) {
            throw new IllegalStateException("Expected HTTP " + s + " / Received: " + _status);
        }
    }

    protected void info(final String t) {
        if (displayInfos) {
            println(t);
        }
    }
}
