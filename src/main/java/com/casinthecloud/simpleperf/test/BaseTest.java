package com.casinthecloud.simpleperf.test;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.pac4j.core.util.CommonHelper;

import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

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

    @Getter
    @Setter
    private int maxErrors = 5;

    @Getter
    @Setter
    private int smallInterval = 40;

    @Getter
    @Setter
    private int bigInterval = 250;

    public abstract void run() throws Exception;

    protected int random(final int max) {
        return (int)(Math.random() * max);
    }

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
        _response = theResponse;
        _request = null;
        _cookies = new HashMap<String, String>();
        _data = new HashMap<String, String>();
    }

    protected HttpRequest post(final String url) throws Exception {
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

    protected String between(final String s1, final String s2, final String s3) {
        return StringUtils.substringBetween(s1, s2, s3);
    }

    protected String after(final String s1, final String s2) {
        return StringUtils.substringAfter(s1, s2);
    }

    protected String before(final String s1, final String s2) {
        return StringUtils.substringBefore(s1, s2);
    }

    protected String urlEncode(final String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    protected String urlDecode(final String s) {
        return URLDecoder.decode(s, StandardCharsets.UTF_8);
    }

    protected String htmlDecode(final String s) {
        return StringEscapeUtils.unescapeHtml4(s);
    }

    protected String addUrlParameter(final String url, final String key, final String value) {
        return CommonHelper.addParameter(url, key, value);
    }

    protected void assertStatus(final int s) {
        if (_response.statusCode() != s) {
            throw new IllegalStateException("Expected HTTP " + s + " / Received: " + _response.statusCode());
        }
    }
}
