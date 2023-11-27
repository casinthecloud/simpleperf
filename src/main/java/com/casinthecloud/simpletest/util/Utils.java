package com.casinthecloud.simpletest.util;

import com.casinthecloud.simpletest.test.BaseTest;
import org.apache.commons.lang3.StringEscapeUtils;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

/**
 * An utility class.
 *
 * @author Jerome LELEU
 * @since 1.0.0
 */
public final class Utils {

    public static final BaseTest CLEAR_CONTEXT = new BaseTest() {
        @Override
        public void run(final Map<String, Object> ctx) throws Exception {
            info("Clear context");
            ctx.clear();
        }
    };

    public static final BaseTest NO_TEST = new BaseTest() {
        @Override
        public void run(final Map<String, Object> ctx) throws Exception {
        }
    };

    public static int random(final int max) {
        return (int)(Math.random() * max);
    }

    public static String urlEncode(final String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    public static String urlDecode(final String s) {
        return URLDecoder.decode(s, StandardCharsets.UTF_8);
    }

    public static String htmlDecode(final String s) {
        return StringEscapeUtils.unescapeHtml4(s);
    }

    public static String addUrlParameter(final String url, final String name, final String value) {
        if (url != null) {
            final var sb = new StringBuilder();
            sb.append(url);
            if (name != null) {
                if (url.indexOf("?") >= 0) {
                    sb.append("&");
                } else {
                    sb.append("?");
                }
                sb.append(name);
                sb.append("=");
                if (value != null) {
                    sb.append(urlEncode(value));
                }
            }
            return sb.toString();
        }
        return null;
    }

    public static void print(final char c) {
        System.out.print(c);
    }

    public static void print(final String t) {
        System.out.print(t);
    }

    public static void println(final String t) {
        System.out.println(t);
    }

    public static void println() {
        System.out.println();
    }

    public static String base64Encode(final String t) {
        return Base64.getEncoder().encodeToString(t.getBytes(StandardCharsets.UTF_8));
    }

    public static String base64Decode(final String t) {
        return new String(Base64.getDecoder().decode(t), StandardCharsets.UTF_8);
    }
}
