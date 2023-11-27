package com.casinthecloud.simpleperf.cas;

import com.casinthecloud.simpleperf.test.CasTest;
import lombok.val;

import static com.casinthecloud.simpleperf.util.Utils.between;
import static com.casinthecloud.simpleperf.util.Utils.htmlDecode;

/**
 * A test performing a SAML2 login in the CAS server (pac4j client).
 *
 * @author Jerome LELEU
 * @since 1.0.0
 */
public class CasSAML2LoginTest extends CasTest {

    public void run() throws Exception {
        // call SP
        _request = get(getProtectedClientAppUrl());
        execute();
        // expecting POST binding
        assertStatus(200);
        val pac4jSessionId = getCookie(JSESSIONID);
        val samlSsoUrl = htmlDecode(between(_body, "<form action=\"", "\" met"));
        val samlRequest = htmlDecode(between(_body, "\"SAMLRequest\" value=\"", "\"/>"));

        startTimer();
        // post facade SAML
        _data.put("SAMLRequest", samlRequest);
        _request = post(samlSsoUrl);
        execute();
        assertStatus(302);
        val loginCasUrl = getLocation();
        val casSessionId = getCookie(JSESSIONID);

        // call login page
        _request = get(loginCasUrl);
        execute();
        assertStatus(200);

        // post credentials
        executePostCasCredentials(loginCasUrl);
        val samlCallbackUrl = getLocation();
        val tgc = getCookie(TGC);

        // call callback
        _cookies.put(casSessionId.getLeft(), casSessionId.getRight());
        _cookies.put(TGC, tgc.getRight());
        _request = get(samlCallbackUrl);
        execute();
        assertStatus(200);
        saveTimer();

        val pac4jCallbackUrl = htmlDecode(between(_body, "<form action=\"", "\" met"));
        val samlResponse = htmlDecode(between(_body, "\"SAMLResponse\" value=\"", "\"/>"));

        _data.put("SAMLResponse", samlResponse);
        _data.put("RelayState", getRelayState());
        _cookies.put("JSESSIONID", pac4jSessionId.getRight());
        _request = post(pac4jCallbackUrl);
        execute();
        assertStatus(303);
        val protectedUrl = getLocation();
        val newPac4jSessionId = getCookie(JSESSIONID);

        _cookies.put(JSESSIONID, newPac4jSessionId.getRight());
        _request = get(protectedUrl);
        execute();
        assertStatus(200);
    }

    protected String getProtectedClientAppUrl() {
        return "http://localhost:8081/saml/index.html";
    }

    protected String getRelayState() {
        return "https://specialurl";
    }
}
