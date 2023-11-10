package com.casinthecloud.simpleperf.cas;

import com.casinthecloud.simpleperf.test.CasTest;
import lombok.val;

/**
 * A test performing a SAML login in the CAS server (pac4j client).
 *
 * @author Jerome LELEU
 * @since 1.0.0
 */
public class CasSAMLLoginTest extends CasTest {

    public void run() throws Exception {
        // call SP
        _request = get("http://localhost:8081/saml/index.html");
        execute();
        val pac4jSessionId = getCookie(JSESSIONID);
        val samlSsoUrl = htmlDecode(between(_body, "<form action=\"", "\" met"));
        val samlRequest = htmlDecode(between(_body, "\"SAMLRequest\" value=\"", "\"/>"));
        assertStatus(200);

        startTimer();
        // post facade SAML
        _data.put("SAMLRequest", samlRequest);
        _request = post(samlSsoUrl);
        execute();
        val loginCasUrl = getLocation();
        val casSessionId = getCookie(JSESSIONID);
        assertStatus(302);

        // call login page
        _request = get(loginCasUrl);
        execute();
        var webflow = between(_body, "name=\"execution\" value=\"", "\"/>");
        assertStatus(200);

        // post credentials
        _data.put("username", getUsername());
        _data.put("password", getPassword());
        _data.put("execution", webflow);
        _data.put("_eventId", "submit");
        _data.put("geolocation", "");
        _request = post(loginCasUrl);
        execute();
        val samlCallbackUrl = getLocation();
        val tgc = getCookie(TGC);
        assertStatus(302);

        // call callback
        _cookies.put(JSESSIONID, casSessionId);
        _cookies.put(TGC, tgc);
        _request = get(samlCallbackUrl);
        execute();
        assertStatus(200);
        saveTimer();

        val pac4jCallbackUrl = htmlDecode(between(_body, "<form action=\"", "\" met"));
        val samlResponse = htmlDecode(between(_body, "\"SAMLResponse\" value=\"", "\"/>"));

        _data.put("SAMLResponse", samlResponse);
        _data.put("RelayState", "https://specialurl");
        _cookies.put("JSESSIONID", pac4jSessionId);
        _request = post(pac4jCallbackUrl);
        execute();
        assertStatus(303);
        val protectedUrl = getLocation();
        val newPac4jSessionId = getCookie(JSESSIONID);

        _cookies.put(JSESSIONID, newPac4jSessionId);
        _request = get(protectedUrl);
        execute();
        assertStatus(200);
    }

    protected String getUsername() {
        return "jleleu";
    }

    protected String getPassword() {
        return "jleleu";
    }
}
