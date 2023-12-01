package com.casinthecloud.simpletest.util;

import com.casinthecloud.simpletest.execution.Context;
import com.casinthecloud.simpletest.test.BaseTest;
import com.casinthecloud.simpletest.test.ChainingTest;
import com.casinthecloud.simpletest.test.RandomTest;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.apache.commons.lang3.StringEscapeUtils.unescapeHtml4;
import static org.apache.commons.lang3.StringUtils.repeat;

/**
 * An utility class.
 *
 * @author Jerome LELEU
 * @since 1.0.0
 */
public final class Utils {

    private static int LEVEL = 0;

    public static void incrLevel() {
        LEVEL = LEVEL + 1;
    }

    public static void decrLevel() {
        LEVEL = LEVEL - 1;
        if (LEVEL < 0) {
            LEVEL = 0;
        }
    }

    private static final int SPACE = 4;

    public static final int NB_ITERATIONS_LIMIT = 50;

    public static final BaseTest CLEAR_CONTEXT = new BaseTest() {
        @Override
        public void run(final Context ctx) throws Exception {
            info("Clear context");
            ctx.clear();
        }
    };

    public static final BaseTest NO_TEST = new BaseTest() {
        @Override
        public void run(final Context ctx) throws Exception {
        }
    };

    public static RandomTest OR(final BaseTest... tests) {
        return new RandomTest(tests);
    }

    public static ChainingTest AND(final BaseTest... tests) {
        return new ChainingTest(tests);
    }

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
        return unescapeHtml4(s);
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
        System.out.println(repeat(" ", LEVEL * SPACE) + t);
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
