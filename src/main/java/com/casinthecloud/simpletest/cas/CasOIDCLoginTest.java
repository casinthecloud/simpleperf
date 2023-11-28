package com.casinthecloud.simpletest.cas;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

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
public class CasOIDCLoginTest extends CasLoginTest {

    private String clientId = "myclient";

    private String clientSecret = "mysecret";

    private String scope = "openid email profile";

    public void run(final Map<String, Object> ctx) throws Exception {

        authorize(ctx);

        val loginUrl = getLocation();
        super.login(ctx, loginUrl);

        callbackCas(ctx);

        callbackApp(ctx);

        getAccessToken(ctx);

    }

    public void authorize(final Map<String, Object> ctx) throws Exception {
        val state = "s" + random(10000);

        var authorizeUrl = getCasPrefixUrl() + "/oidc/oidcAuthorize";
        authorizeUrl = addUrlParameter(authorizeUrl, "response_type", "code");
        authorizeUrl = addUrlParameter(authorizeUrl, "client_id", getClientId());
        authorizeUrl = addUrlParameter(authorizeUrl, "scope", getScope());
        authorizeUrl = addUrlParameter(authorizeUrl, "redirect_uri", getServiceUrl());
        authorizeUrl = addUrlParameter(authorizeUrl, "state", state);

        _request = get(authorizeUrl);
        execute();
        assertStatus(302);

        val casSession = getCookie(DISSESSION);
        ctx.put(CAS_SESSION, casSession);
        info("Found CAS session: " + casSession.getLeft() + "=" + casSession.getRight());
    }

    public void callbackCas(final Map<String, Object> ctx) throws Exception {
        val callbackUrl = getLocation();
        val tgc = (Pair<String, String>) ctx.get(TGC);
        val casSession = (Pair<String, String>) ctx.get(CAS_SESSION);

        _cookies.put(casSession.getLeft(), casSession.getRight());
        _cookies.put(tgc.getLeft(), tgc.getRight());
        _request = get(callbackUrl);
        execute();
        assertStatus(302);
    }

    public void callbackApp(final Map<String, Object> ctx) throws Exception {
        val callbackAppUrl = getLocation();
        val tgc = (Pair<String, String>) ctx.get(TGC);
        val casSession = (Pair<String, String>) ctx.get(CAS_SESSION);

        _cookies.put(casSession.getLeft(), casSession.getRight());
        _cookies.put(TGC, tgc.getRight());
        _request = get(callbackAppUrl);
        execute();
        assertStatus(302);
    }

    public void getAccessToken(final Map<String, Object> ctx) throws Exception {
        val clientAppUrl = getLocation();
        val code = substringBetween(clientAppUrl, "code=", "&state");
        info("Code: " + code);

        var tokenUrl = getCasPrefixUrl() + "/oidc/token";
        tokenUrl = addUrlParameter(tokenUrl, "grant_type", "authorization_code");
        tokenUrl = addUrlParameter(tokenUrl, "client_id", getClientId());
        tokenUrl = addUrlParameter(tokenUrl, "client_secret", getClientSecret());
        tokenUrl = addUrlParameter(tokenUrl, "redirect_uri", getServiceUrl());
        tokenUrl = addUrlParameter(tokenUrl, "code", code);

        _request = post(tokenUrl);
        execute();
        assertStatus(200);
    }
}
