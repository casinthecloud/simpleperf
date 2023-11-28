package com.casinthecloud.simpletest.cas;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;

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
public class CasSAML2LoginTest extends CasLoginTest {

    private String serviceUrl = "http://localhost:8081/callback?client_name=SAML2Client";

    private String relayState = "https://specialurl";

    public void run(final Map<String, Object> ctx) throws Exception {

        postRequest(ctx);

        val loginCasUrl = getLocation();
        super.login(ctx, loginCasUrl);

        callbackCas(ctx);

    }

    public void postRequest(final Map<String, Object> ctx) throws Exception {
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

        _data.put("RelayState", relayState);
        _data.put("SAMLRequest", samlRequest);
        _request = post(samlSsoUrl);
        execute();
        assertStatus(302);

        val casSession = getCookie(JSESSIONID);
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
        assertStatus(200);

        val pac4jCallbackUrl = htmlDecode(substringBetween(_body, "<form action=\"", "\" met"));
        val samlResponse = htmlDecode(substringBetween(_body, "\"SAMLResponse\" value=\"", "\"/>"));
        assertEquals(serviceUrl, pac4jCallbackUrl);
        assertNotNull(base64Decode(samlResponse));
    }
}
