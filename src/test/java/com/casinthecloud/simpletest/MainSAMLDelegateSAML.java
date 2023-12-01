package com.casinthecloud.simpletest;

import com.casinthecloud.simpletest.cas.CasDelegate;
import com.casinthecloud.simpletest.cas.CasSAML2Login;
import com.casinthecloud.simpletest.execution.Execution;

public class MainSAMLDelegateSAML {

    public static void main(final String... args) throws Exception {
        new Execution(() -> {
            return new CasSAML2Login(new CasDelegate(1, "SAML2Client", new CasSAML2Login()));
        }).launch();
    }
}
