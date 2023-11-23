package com.casinthecloud.simpleperf.test;

import lombok.val;

import java.net.http.HttpClient;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Randomly run test.
 *
 * @author Jerome LELEU
 * @since 1.0.0
 */
public class RandomTest extends BaseTest {

    private BaseTest[] tests;

    public RandomTest(final BaseTest... tests) {
        this.tests = tests;
    }

    @Override
    public void run() throws Exception {
        val r = random(tests.length);

        tests[r].run();
    }

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
}
