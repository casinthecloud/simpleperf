package com.casinthecloud.simpletest.cas;

import com.casinthecloud.simpletest.execution.Context;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;

import static com.casinthecloud.simpletest.util.Utils.addUrlParameter;
import static com.casinthecloud.simpletest.util.Utils.random;
import static org.apache.commons.lang3.StringUtils.substringBetween;

/**
 * A test performing an OIDC login in the CAS server.
 *
 * @author Jerome LELEU
 * @since 1.0.0
 */
@Getter
@Setter
public class CasOIDCLoginTest extends EmbeddedCasLoginTest {

    private String clientId = "myclient";

    private String clientSecret = "mysecret";

    private String scope = "openid email profile";

    public CasOIDCLoginTest() {
        this(new CasLoginTest());
    }

    public CasOIDCLoginTest(final CasLoginTest casLoginTest) {
        this.casLoginTest = casLoginTest;
    }

    public void run(final Context ctx) throws Exception {

        authorize(ctx);

        casLoginTest.run(ctx);

        callbackCas(ctx);

        callbackApp(ctx);

        getAccessToken(ctx);

    }

    private void authorize(final Context ctx) throws Exception {
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

        val casSession = getCookie(ctx, DISSESSION);
        ctx.getData().put(CAS_SESSION, casSession);
        info("Found CAS session: " + casSession.getLeft() + "=" + casSession.getRight());
    }

    private void callbackCas(final Context ctx) throws Exception {
        val callbackCasUrl = getLocation(ctx);
        val tgc = (Pair<String, String>) ctx.getData().get(TGC);
        val casSession = (Pair<String, String>) ctx.getData().get(CAS_SESSION);

        ctx.getCookies().put(casSession.getLeft(), casSession.getRight());
        ctx.getCookies().put(tgc.getLeft(), tgc.getRight());
        ctx.setRequest(get(ctx, callbackCasUrl));
        execute(ctx);
        assertStatus(ctx, 302);
    }

    private void callbackApp(final Context ctx) throws Exception {
        val callbackAppUrl = getLocation(ctx);
        val tgc = (Pair<String, String>) ctx.getData().get(TGC);
        val casSession = (Pair<String, String>) ctx.getData().get(CAS_SESSION);

        ctx.getCookies().put(casSession.getLeft(), casSession.getRight());
        ctx.getCookies().put(TGC, tgc.getRight());
        ctx.setRequest(get(ctx, callbackAppUrl));
        execute(ctx);
        assertStatus(ctx, 302);
    }

    private void getAccessToken(final Context ctx) throws Exception {
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
