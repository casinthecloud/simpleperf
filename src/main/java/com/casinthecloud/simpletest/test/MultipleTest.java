package com.casinthecloud.simpletest.test;

import lombok.val;

import java.net.http.HttpClient;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Multiple tests
 *
 * @author Jerome LELEU
 * @since 1.0.0
 */
public abstract class MultipleTest extends BaseTest {

    protected BaseTest[] tests;

    public void setClient(final HttpClient client) {
        for (val test : tests) {
            test.setClient(client);
        }
    }

    public void setTime(final AtomicLong time) {
        for (val test : tests) {
            test.setTime(time);
        }
    }

    public void setDisplayInfos(final boolean displayInfos) {
        for (val test : tests) {
            test.setDisplayInfos(displayInfos);
        }
    }
}
