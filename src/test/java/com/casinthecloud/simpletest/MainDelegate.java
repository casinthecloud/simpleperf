package com.casinthecloud.simpletest;

import com.casinthecloud.simpletest.cas.CasDelegate;
import com.casinthecloud.simpletest.cas.CasLogin;
import com.casinthecloud.simpletest.cas.CasOIDCLogin;
import com.casinthecloud.simpletest.cas.CasOIDCValidateOC;
import com.casinthecloud.simpletest.execution.Execution;
import lombok.val;

import static com.casinthecloud.simpletest.util.Utils.AND;

public class MainDelegate {

    public static void main(final String... args) throws Exception {
        new Execution(() -> {
            val oidcLogin = new CasOIDCLogin(new CasDelegate("CasClient", new CasLogin()));
            oidcLogin.setCasPrefixUrl("http://oidc-server:8080/cas");
            val oidcValidate = new CasOIDCValidateOC();
            oidcValidate.setCasPrefixUrl(oidcLogin.getCasPrefixUrl());
            return AND(oidcLogin, oidcValidate);
        }).launch();
    }
}
