package com.casinthecloud.simpletest.cas;

import com.casinthecloud.simpletest.execution.Context;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

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
public class CasSAML2Login extends CasTest {

    private String serviceUrl = "http://localhost:8082/callback?client_name=SAML2Client";

    private String relayState = "https://specialurl";

    public CasSAML2Login() {
        this(new CasLogin());
    }

    public CasSAML2Login(final CasTest casTest) {
        this.tests = new CasTest[] { casTest };
    }

    public void run(final Context ctx) throws Exception {

        info("> BEGIN CasSAML2Login");

        postRequest(ctx);

        incrLevel();
        this.tests[0].run(ctx);
        decrLevel();

        callback(ctx, 200);

        val pac4jCallbackUrl = htmlDecode(substringBetween(ctx.getBody(), "<form action=\"", "\" met"));
        val samlResponse = htmlDecode(substringBetween(ctx.getBody(), "\"SAMLResponse\" value=\"", "\"/>"));
        assertEquals(serviceUrl, pac4jCallbackUrl);
        assertNotNull(base64Decode(samlResponse));

        info("< END CasSAML2Login");

    }

    protected void postRequest(final Context ctx) throws Exception {
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

        val fp = ctx.getFormParameters();
        fp.put("RelayState", relayState);
        fp.put("SAMLRequest", samlRequest);
        ctx.setRequest(post(ctx, samlSsoUrl));
        execute(ctx);
        assertStatus(ctx, 302);

        saveCasSession(ctx);
    }
}