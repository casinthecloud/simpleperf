package com.casinthecloud.simpletest;

import com.casinthecloud.simpletest.cas.CasDelegate;
import com.casinthecloud.simpletest.cas.CasLogin;
import com.casinthecloud.simpletest.cas.CasOIDCLogin;
import com.casinthecloud.simpletest.cas.CasOIDCValidateOC;
import com.casinthecloud.simpletest.execution.Execution;

import static com.casinthecloud.simpletest.util.Utils.AND;

public class MainDelegate {

    public static void main(final String... args) throws Exception {
        new Execution(() -> {
            return AND(new CasOIDCLogin(new CasDelegate("CasClient", new CasLogin())), new CasOIDCValidateOC());
        }).launch();
    }
}
