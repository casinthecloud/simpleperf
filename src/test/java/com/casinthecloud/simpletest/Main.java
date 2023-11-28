package com.casinthecloud.simpletest;

import com.casinthecloud.simpletest.cas.CasLoginTest;
import com.casinthecloud.simpletest.execution.Execution;

public class Main {

    public static void main(final String... args) throws Exception {
        new Execution(6, 2000, CasLoginTest::new).launch();
    }
}
