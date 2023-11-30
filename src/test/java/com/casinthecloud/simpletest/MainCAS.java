package com.casinthecloud.simpletest;

import com.casinthecloud.simpletest.cas.CasLogin;
import com.casinthecloud.simpletest.cas.CasValidate;
import com.casinthecloud.simpletest.execution.Execution;

import static com.casinthecloud.simpletest.util.Utils.*;

public class MainCAS {

    public static void main(final String... args) throws Exception {
        new Execution(5, () -> {
            return OR(
                    AND(new CasLogin(), new CasValidate()),
                    CLEAR_CONTEXT
            );
        }).launch();
    }
}
