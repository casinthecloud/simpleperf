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
@Getter
@Setter
public class Context {

    private HttpRequest request = null;

    private HttpResponse response = null;

    private Map<String, List<String>> headers = new HashMap<>();

    private String body = null;

    private Map<String, String> formParameters = new HashMap<>();

    private Map<String, String> cookies = new HashMap<>();

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
}
