package com.casinthecloud.simpletest.cas;

import com.casinthecloud.simpletest.test.CasTest;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Map;

import static com.casinthecloud.simpletest.util.Utils.*;
import static org.apache.commons.lang3.StringUtils.substringBetween;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * A test performing a SAML2 login in the CAS server.
 *
 * @author Jerome LELEU
 * @since 1.0.0
 */
@Getter
@Setter
public class CasSAML2LoginTest extends CasTest {

    private String serviceUrl = "http://localhost:8081/callback?client_name=SAML2Client";

    private String relayState = "https://specialurl";

    public void run(final Map<String, Object> ctx) throws Exception {

        val samlSsoUrl = getCasPrefixUrl() + "/idp/profile/SAML2/POST/SSO";
        val relayState = getRelayState();
        val serviceUrl = getServiceUrl();
        val samlRequestId = random(1000);
        val samlRequest = base64Encode("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<saml2p:AuthnRequest\n" +
                "    xmlns:saml2p=\"urn:oasis:names:tc:SAML:2.0:protocol\" AssertionConsumerServiceURL=\"" + serviceUrl + "\" Destination=\"" + samlSsoUrl + "\" ForceAuthn=\"false\" ID=\"" + samlRequestId + "\" IsPassive=\"false\" IssueInstant=\"" + ZonedDateTime.now(ZoneOffset.UTC) + "\" ProtocolBinding=\"urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST\" ProviderName=\"client-app\" Version=\"2.0\">\n" +
                "    <saml2:Issuer\n" +
                "        xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\" Format=\"urn:oasis:names:tc:SAML:2.0:nameid-format:entity\">" + serviceUrl + "\n" +
                "    </saml2:Issuer>\n" +
                "</saml2p:AuthnRequest>");

        // post facade SAML
        _data.put("RelayState", relayState);
        _data.put("SAMLRequest", samlRequest);
        _request = post(samlSsoUrl);
        execute();
        assertStatus(302);
        val loginCasUrl = getLocation();
        val casSession = getCookie(JSESSIONID);
        info("Found CAS session: " + casSession.getLeft() + "=" + casSession.getRight());

        // call login page
        _request = get(loginCasUrl);
        execute();
        assertStatus(200);

        // post credentials
        executePostCasCredentials(loginCasUrl);
        val samlCallbackUrl = getLocation();
        val tgc = getCookie(TGC);
        info("Found TGC: " + tgc.getRight());

        // call callback
        _cookies.put(casSession.getLeft(), casSession.getRight());
        _cookies.put(TGC, tgc.getRight());
        _request = get(samlCallbackUrl);
        execute();
        assertStatus(200);

        val pac4jCallbackUrl = htmlDecode(substringBetween(_body, "<form action=\"", "\" met"));
        val samlResponse = htmlDecode(substringBetween(_body, "\"SAMLResponse\" value=\"", "\"/>"));
        assertEquals(serviceUrl, pac4jCallbackUrl);
        assertNotNull(base64Decode(samlResponse));

    }
}
