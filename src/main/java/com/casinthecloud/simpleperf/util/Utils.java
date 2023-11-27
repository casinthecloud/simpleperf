package com.casinthecloud.simpleperf.util;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * An utility class.
 *
 * @author Jerome LELEU
 * @since 1.0.0
 */
public final class Utils {

    public static int random(final int max) {
        return (int)(Math.random() * max);
    }

    public static String between(final String s1, final String s2, final String s3) {
        return StringUtils.substringBetween(s1, s2, s3);
    }

    public static String after(final String s1, final String s2) {
        return StringUtils.substringAfter(s1, s2);
    }

    public static String before(final String s1, final String s2) {
        return StringUtils.substringBefore(s1, s2);
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
}
