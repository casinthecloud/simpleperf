package com.casinthecloud.simpletest;

import com.casinthecloud.simpletest.cas.CasOIDCLoginTest;
import com.casinthecloud.simpletest.execution.Execution;
import lombok.val;

public class MainOIDC {

    public static void main(final String... args) throws Exception {
        new Execution(() -> {
            val t = new CasOIDCLoginTest();
            t.setCasPrefixUrl("http://oidc-server:8080/cas");
            return t;
        }).launch();
    }
}
