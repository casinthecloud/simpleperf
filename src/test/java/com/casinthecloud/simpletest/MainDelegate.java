package com.casinthecloud.simpletest;

import com.casinthecloud.simpletest.cas.CasDelegate;
import com.casinthecloud.simpletest.cas.CasOIDCLogin;
import com.casinthecloud.simpletest.cas.CasSAML2Login;
import com.casinthecloud.simpletest.execution.Execution;
import lombok.val;

public class MainDelegate {

    public static void main(final String... args) throws Exception {
        /*new Execution(() -> {
            val login = new CasOIDCLogin(new CasDelegate("OidcClient", new CasOIDCLogin()));
            login.setCasPrefixUrl("http://oidc-server:8080/cas");
            val validate = new CasOIDCValidateOC();
            validate.setCasPrefixUrl(login.getCasPrefixUrl());
            return AND(login, validate);
        }).launch();*/
        new Execution(() -> {
            val login = new CasOIDCLogin(new CasDelegate("OidcClient", new CasSAML2Login()));
            login.setCasPrefixUrl("http://oidc-server:8080/cas");
            return login;
        }).launch();
    }
}
