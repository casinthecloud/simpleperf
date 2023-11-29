package com.casinthecloud.simpletest.cas;

import com.casinthecloud.simpletest.execution.Context;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

import static com.casinthecloud.simpletest.util.Utils.addUrlParameter;
import static com.casinthecloud.simpletest.util.Utils.random;

/**
 * A test performing an OIDC login in the CAS server.
 *
 * @author Jerome LELEU
 * @since 1.0.0
 */
@Getter
@Setter
public class CasOIDCLoginTest extends CasTest {

    private String clientId = "myclient";

    private String scope = "openid email profile";

    public CasOIDCLoginTest() {
        this(new CasLoginTest());
    }

    public CasOIDCLoginTest(final CasTest casTest) {
        this.tests = new CasTest[] { casTest };
    }

    public void run(final Context ctx) throws Exception {

        authorize(ctx);

        super.run(ctx);

        callback(ctx);
        assertStatus(ctx, 302);

        callback(ctx);
        assertStatus(ctx, 302);
    }

    protected void authorize(final Context ctx) throws Exception {
        val state = "s" + random(10000);

        var authorizeUrl = getCasPrefixUrl() + "/oidc/oidcAuthorize";
        authorizeUrl = addUrlParameter(authorizeUrl, "response_type", "code");
        authorizeUrl = addUrlParameter(authorizeUrl, "client_id", getClientId());
        authorizeUrl = addUrlParameter(authorizeUrl, "scope", getScope());
        authorizeUrl = addUrlParameter(authorizeUrl, "redirect_uri", getServiceUrl());
        authorizeUrl = addUrlParameter(authorizeUrl, "state", state);

        ctx.setRequest(get(ctx, authorizeUrl));
        execute(ctx);
        assertStatus(ctx, 302);

        saveCasSession(ctx);
    }
}
