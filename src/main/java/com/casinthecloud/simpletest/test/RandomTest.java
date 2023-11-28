package com.casinthecloud.simpletest.test;

import lombok.val;

import java.util.Map;

import static com.casinthecloud.simpletest.util.Utils.random;

/**
 * Randomly run of the tests.
 *
 * @author Jerome LELEU
 * @since 1.0.0
 */
public class RandomTest extends MultiTest {

    public RandomTest(final BaseTest... tests) {
        this.tests = tests;
    }

    @Override
    public void run(final Map<String, Object> ctx) throws Exception {
        val r = random(tests.length);

        tests[r].run(ctx);
    }
}
