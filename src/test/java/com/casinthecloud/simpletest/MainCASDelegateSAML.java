package com.casinthecloud.simpletest;

import com.casinthecloud.simpletest.cas.CasDelegate;
import com.casinthecloud.simpletest.cas.CasSAML2Login;
import com.casinthecloud.simpletest.cas.CasValidate;
import com.casinthecloud.simpletest.execution.Execution;
import lombok.val;

import static com.casinthecloud.simpletest.util.Utils.AND;

public class MainCASDelegateSAML {

    public static void main(final String... args) throws Exception {
        new Execution(() -> {
            val login = new CasDelegate(1, "SAML2Client", new CasSAML2Login());
            login.setCasPrefixUrl("http://oidc-server:8080/cas");
            val validate = new CasValidate();
            validate.setCasPrefixUrl(login.getCasPrefixUrl());
            return AND(login, validate);
        }).launch();
    }
}
