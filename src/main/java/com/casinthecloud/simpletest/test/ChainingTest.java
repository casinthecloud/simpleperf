package com.casinthecloud.simpletest.test;

import lombok.val;

import java.util.Map;

/**
 * Run all tests.
 *
 * @author Jerome LELEU
 * @since 1.0.0
 */
public class ChainingTest extends MultiTest {

    public ChainingTest(final BaseTest... tests) {
        this.tests = tests;
    }

    @Override
    public void run(final Map<String, Object> ctx) throws Exception {
        for (val test : tests) {
            test.run(ctx);
        }
    }
}
