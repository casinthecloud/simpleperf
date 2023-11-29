package com.casinthecloud.simpletest.cas;

import com.casinthecloud.simpletest.execution.Context;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

import static com.casinthecloud.simpletest.util.Utils.addUrlParameter;
import static org.apache.commons.lang3.StringUtils.substringBetween;

/**
 * A test performing an OIDC login in the CAS server.
 *
 * @author Jerome LELEU
 * @since 1.0.0
 */
@Getter
@Setter
public class CasOIDCValidateOCTest extends CasOIDCLoginTest {

    private String clientSecret = "mysecret";

    public void run(final Context ctx) throws Exception {

        getAccessToken(ctx);

    }

    protected void getAccessToken(final Context ctx) throws Exception {
        val clientAppUrl = getLocation(ctx);
        val code = substringBetween(clientAppUrl, "code=", "&state");
        info("Code: " + code);

        var tokenUrl = getCasPrefixUrl() + "/oidc/token";
        tokenUrl = addUrlParameter(tokenUrl, "grant_type", "authorization_code");
        tokenUrl = addUrlParameter(tokenUrl, "client_id", getClientId());
        tokenUrl = addUrlParameter(tokenUrl, "client_secret", getClientSecret());
        tokenUrl = addUrlParameter(tokenUrl, "redirect_uri", getServiceUrl());
        tokenUrl = addUrlParameter(tokenUrl, "code", code);

        ctx.setRequest(post(ctx, tokenUrl));
        execute(ctx);
        assertStatus(ctx, 200);
        info(ctx.getBody());
    }
}
