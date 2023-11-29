package com.casinthecloud.simpletest.test;

import com.casinthecloud.simpletest.execution.Context;
import lombok.val;

import static com.casinthecloud.simpletest.util.Utils.random;

/**
 * Randomly run one of the tests.
 *
 * @author Jerome LELEU
 * @since 1.0.0
 */
public class RandomTest extends MultiTest {

    public RandomTest(final BaseTest... tests) {
        this.tests = tests;
    }

    @Override
    public void run(final Context ctx) throws Exception {
        if (tests != null) {
            val r = random(tests.length);

            tests[r].run(ctx);
        }
    }
}
