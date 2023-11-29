package com.casinthecloud.simpletest;

import com.casinthecloud.simpletest.cas.CasLoginTest;
import com.casinthecloud.simpletest.cas.CasValidateTest;
import com.casinthecloud.simpletest.execution.Execution;

import static com.casinthecloud.simpletest.util.Utils.*;

public class MainCAS {

    public static void main(final String... args) throws Exception {
        new Execution(5, () -> {
            return OR(
                    AND(new CasLoginTest(), new CasValidateTest()),
                    CLEAR_CONTEXT
            );
        }).launch();
    }
}
