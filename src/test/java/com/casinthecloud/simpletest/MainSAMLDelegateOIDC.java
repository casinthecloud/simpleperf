package com.casinthecloud.simpletest;

import com.casinthecloud.simpletest.cas.CasDelegate;
import com.casinthecloud.simpletest.cas.CasOIDCLogin;
import com.casinthecloud.simpletest.cas.CasSAML2Login;
import com.casinthecloud.simpletest.execution.Execution;
import lombok.val;

public class MainSAMLDelegateOIDC {

    public static void main(final String... args) throws Exception {
        new Execution(() -> {
            val login = new CasSAML2Login(new CasDelegate(2, "OidcClient", new CasOIDCLogin()));
            login.setCasPrefixUrl("http://oidc-server:8080/cas");
            return login;
        }).launch();
    }
}
