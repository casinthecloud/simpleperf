package com.casinthecloud.simpletest;

import com.casinthecloud.simpletest.cas.CasDelegateTest;
import com.casinthecloud.simpletest.cas.CasLoginTest;
import com.casinthecloud.simpletest.cas.CasOIDCLoginTest;
import com.casinthecloud.simpletest.cas.CasOIDCValidateOCTest;
import com.casinthecloud.simpletest.execution.Execution;

import static com.casinthecloud.simpletest.util.Utils.AND;

public class MainDelegate {

    public static void main(final String... args) throws Exception {
        new Execution(() -> {
            return AND(new CasOIDCLoginTest(new CasDelegateTest("CasClient", new CasLoginTest())), new CasOIDCValidateOCTest());
        }).launch();
    }
}
