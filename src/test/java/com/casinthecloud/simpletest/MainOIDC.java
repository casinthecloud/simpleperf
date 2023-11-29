package com.casinthecloud.simpletest;

import com.casinthecloud.simpletest.cas.CasOIDCLoginTest;
import com.casinthecloud.simpletest.cas.CasOIDCValidateOCTest;
import com.casinthecloud.simpletest.execution.Execution;
import lombok.val;

import static com.casinthecloud.simpletest.util.Utils.AND;

public class MainOIDC {

    public static void main(final String... args) throws Exception {
        new Execution(() -> {
            val login = new CasOIDCLoginTest();
            val accessToken = new CasOIDCValidateOCTest();
            login.setCasPrefixUrl("http://oidc-server:8080/cas");
            accessToken.setCasPrefixUrl(login.getCasPrefixUrl());
            return AND(login, accessToken);
        }).launch();
    }
}
