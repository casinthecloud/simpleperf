package com.casinthecloud.simpletest.test;

import lombok.val;

import java.net.http.HttpClient;

/**
 * Multiple tests
 *
 * @author Jerome LELEU
 * @since 1.0.0
 */
public abstract class MultiTest extends BaseTest {

    protected BaseTest[] tests;

    public void setClient(final HttpClient client) {
        super.setClient(client);
        if (tests != null) {
            for (val test : tests) {
                test.setClient(client);
            }
        }
    }

    public void setDisplayInfos(final boolean displayInfos) {
        super.setDisplayInfos(displayInfos);
        if (tests != null) {
            for (val test : tests) {
                test.setDisplayInfos(displayInfos);
            }
        }
    }
}
