package com.casinthecloud.simpletest.cas;

import com.casinthecloud.simpletest.execution.Context;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

import static com.casinthecloud.simpletest.util.Utils.*;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * A test performing an OIDC login in the CAS server.
 *
 * @author Jerome LELEU
 * @since 1.0.0
 */
@Getter
@Setter
public class CasOIDCLogin extends CasTest {

    private String clientId = "myclient";

    private String scope = "openid email profile";

    public CasOIDCLogin() {
        this(new CasLogin());
    }

    public CasOIDCLogin(final CasTest casTest) {
        this.tests = new CasTest[] { casTest };
    }

    public void run(final Context ctx) throws Exception {

        info("> BEGIN CasOIDCLogin");

        val alreadyAuthenticated = authorize(ctx);

        if (!alreadyAuthenticated) {
            info("> Not authenticated = performing login");
            incrLevel();
            this.tests[0].run(ctx);
            decrLevel();

            callback(ctx, 302);

            callback(ctx, 302);
        } else {
            info("> Already authenticated = skipping login");
        }

        info("< END CasOIDCLogin");

    }

    protected boolean authorize(final Context ctx) throws Exception {
        val alreadyAuthenticated = useCasSession(ctx);

        var authorizeUrl = getLocation(ctx);
        if (isNotBlank(authorizeUrl) && authorizeUrl.contains("response_type=code")) {
            info("! Existing authorizeUrl");
        } else {
            info("Client app: " + getServiceUrl());
            val state = "s" + random(10000);

            authorizeUrl = getCasPrefixUrl() + "/oidc/oidcAuthorize";
            authorizeUrl = addUrlParameter(authorizeUrl, "response_type", "code");
            authorizeUrl = addUrlParameter(authorizeUrl, "client_id", getClientId());
            authorizeUrl = addUrlParameter(authorizeUrl, "scope", getScope());
            authorizeUrl = addUrlParameter(authorizeUrl, "redirect_uri", getServiceUrl());
            authorizeUrl = addUrlParameter(authorizeUrl, "state", state);
        }

        ctx.setRequest(get(ctx, authorizeUrl));
        execute(ctx);
        assertStatus(ctx, 302);

        saveCasSession(ctx);

        return alreadyAuthenticated;
    }
}
