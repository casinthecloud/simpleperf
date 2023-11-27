package com.casinthecloud.simpleperf.cas;

import lombok.val;

import static com.casinthecloud.simpleperf.util.Utils.*;

/**
 * A test performing an OIDC login in the CAS server.
 *
 * @author Jerome LELEU
 * @since 1.0.0
 */
public class CasOIDCLoginTest extends CasLoginTest {

    public void run() throws Exception {
        startTimer();

        val clientId = getClientId();
        val serviceUrl = getServiceUrl();
        val state = "s" + random(10000);

        var authorizeUrl = getCasPrefixUrl() + "/oidc/oidcAuthorize";
        authorizeUrl = addUrlParameter(authorizeUrl, "response_type", "code");
        authorizeUrl = addUrlParameter(authorizeUrl, "client_id", clientId);
        authorizeUrl = addUrlParameter(authorizeUrl, "scope", getScope());
        authorizeUrl = addUrlParameter(authorizeUrl, "redirect_uri", serviceUrl);
        authorizeUrl = addUrlParameter(authorizeUrl, "state", state);

        _request = get(authorizeUrl);
        execute();
        assertStatus(302);
        val loginUrl = getLocation();
        val casSessionId = getCookie(DISSESSION);

        _request = get(loginUrl);
        execute();
        assertStatus(200);

        executePostCasCredentials(loginUrl);
        val callbackUrl = getLocation();
        val tgc = getCookie(TGC);

        _cookies.put(casSessionId.getLeft(), casSessionId.getRight());
        _cookies.put(TGC, tgc.getRight());
        _request = get(callbackUrl);
        execute();
        assertStatus(302);
        val authorizeUrl2 = getLocation();

        _cookies.put(casSessionId.getLeft(), casSessionId.getRight());
        _cookies.put(TGC, tgc.getRight());
        _request = get(authorizeUrl2);
        execute();
        assertStatus(302);
        val clientAppUrl = getLocation();
        val code = between(clientAppUrl, "code=", "&state");

        var tokenUrl = getCasPrefixUrl() + "/oidc/token";
        tokenUrl = addUrlParameter(tokenUrl, "grant_type", "authorization_code");
        tokenUrl = addUrlParameter(tokenUrl, "client_id", clientId);
        tokenUrl = addUrlParameter(tokenUrl, "client_secret", getClientSecret());
        tokenUrl = addUrlParameter(tokenUrl, "redirect_uri", serviceUrl);
        tokenUrl = addUrlParameter(tokenUrl, "code", code);

        _request = post(tokenUrl);
        execute();
        assertStatus(200);

        saveTimer();
    }

    protected String getClientId() {
        return "myclient";
    }

    protected String getClientSecret() {
        return "mysecret";
    }

    protected String getScope() {
        return "openid email profile";
    }
}
