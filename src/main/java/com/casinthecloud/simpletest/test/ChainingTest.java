package com.casinthecloud.simpletest.test;

import com.casinthecloud.simpletest.execution.Context;
import lombok.val;

/**
 * Run all tests in order.
 *
 * @author Jerome LELEU
 * @since 1.0.0
 */
public class ChainingTest extends MultiTest {

    public ChainingTest(final BaseTest... tests) {
        this.tests = tests;
    }

    @Override
    public void run(final Context ctx) throws Exception {
        if (tests != null) {
            for (val test : tests) {
                test.run(ctx);
            }
        }
    }
}
