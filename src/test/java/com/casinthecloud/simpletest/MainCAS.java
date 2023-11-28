package com.casinthecloud.simpletest;

import com.casinthecloud.simpletest.cas.CasLoginTest;
import com.casinthecloud.simpletest.execution.Execution;
import com.casinthecloud.simpletest.test.RandomTest;

import static com.casinthecloud.simpletest.util.Utils.CLEAR_CONTEXT;

public class MainCAS {

    public static void main(final String... args) throws Exception {
        new Execution(5, () -> {
            return new RandomTest(new CasLoginTest(), CLEAR_CONTEXT);
        }).launch();
    }
}
