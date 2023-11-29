package com.casinthecloud.simpletest.execution;

import lombok.Getter;
import lombok.Setter;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The context of an execution.
 */
public class Context {

    @Getter
    @Setter
    private HttpRequest request = null;

    @Getter
    @Setter
    private HttpResponse response = null;

    @Getter
    @Setter
    private Map<String, List<String>> headers = new HashMap<>();

    @Getter
    @Setter
    private String body = null;

    @Getter
    @Setter
    private Map<String, String> formParameters = new HashMap<>();

    @Getter
    @Setter
    private Map<String, String> cookies = new HashMap<>();

    @Getter
    @Setter
    private Integer status = null;

    private Map<String, Object> data = new HashMap<>();

    public void clear() {
        request = null;
        response = null;
        headers = new HashMap<>();
        body = null;
        formParameters.clear();
        cookies.clear();
        status = null;
        data.clear();;
    }

    public Object get(final String key) {
        return data.get(key);
    }

    public void put(final String key, final Object value) {
        data.put(key, value);
    }
}
