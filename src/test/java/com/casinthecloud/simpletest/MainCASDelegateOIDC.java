package com.casinthecloud.simpletest;

import com.casinthecloud.simpletest.cas.CasDelegate;
import com.casinthecloud.simpletest.cas.CasOIDCLogin;
import com.casinthecloud.simpletest.cas.CasValidate;
import com.casinthecloud.simpletest.execution.Execution;
import lombok.val;

import static com.casinthecloud.simpletest.util.Utils.AND;

public class MainCASDelegateOIDC {

    public static void main(final String... args) throws Exception {
        new Execution(() -> {
            val login = new CasDelegate(2, "OidcClient", new CasOIDCLogin());
            login.setCasPrefixUrl("http://oidc-server:8080/cas");
            val validate = new CasValidate();
            validate.setCasPrefixUrl(login.getCasPrefixUrl());
            return AND(login, validate);
        }).launch();
    }
}
