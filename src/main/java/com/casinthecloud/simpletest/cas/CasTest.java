package com.casinthecloud.simpletest.cas;

import com.casinthecloud.simpletest.execution.Context;
import com.casinthecloud.simpletest.test.MultiTest;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;

import static com.casinthecloud.simpletest.util.Utils.htmlDecode;
import static org.apache.commons.lang3.StringUtils.substringBetween;
import static org.junit.Assert.assertTrue;

/**
 * A test for CAS.
 *
 * @author Jerome LELEU
 * @since 1.0.0
 */
@Getter
@Setter
public abstract class CasTest extends MultiTest {

    private static final String JSESSIONID = "JSESSIONID";
    private static final String DISSESSION = "DISSESSION";
    private static final String CAS_SESSION = "CAS_SESSION";
    private static final String TGC = "TGC";

    private String casPrefixUrl = "http://localhost:8080/cas";

    private String serviceUrl = "http://localhost:8082/";

    private String casCookieName = TGC;

    private String username = "jleleu";

    private String password = "jleleu";

    protected void saveCasSession(final Context ctx) {
        var casSession = getCookie(ctx, DISSESSION);
        if (casSession == null) {
            casSession = getCookie(ctx, JSESSIONID);
        }
        if (casSession != null) {
            ctx.put(testId + CAS_SESSION, casSession);
            info("Found CAS session: " + casSession.getLeft() + "=" + casSession.getRight());
        }
    }

    protected boolean useCasSession(final Context ctx) {
        val casSession = (Pair<String, String>) ctx.get(testId + CAS_SESSION);
        if (casSession != null) {
            info("Re-use: " + casSession.getLeft() + "=" + casSession.getRight());
            ctx.getCookies().put(casSession.getLeft(), casSession.getRight());
            return true;
        } else {
            return false;
        }
    }

    protected void saveSsoSession(final Context ctx) {
        val tgc = getCookie(ctx, getCasCookieName());
        ctx.put(testId + TGC, tgc);
        info("Found: " + tgc.getLeft() + "=" + tgc.getRight());
    }

    protected void useSsoSession(final Context ctx) {
        val tgc = (Pair<String, String>) ctx.get(testId + TGC);
        if (tgc != null) {
            info("Re-use: " + tgc.getLeft() + "=" + tgc.getRight());
            ctx.getCookies().put(getCasCookieName(), tgc.getRight());
        }
    }

    protected void callback(final Context ctx, final int status) throws Exception {

        useSsoSession(ctx);
        useCasSession(ctx);

        val previousStatus = ctx.getStatus();
        if (previousStatus == 302) {
            val callbackUrl = getLocation(ctx);
            ctx.setRequest(get(ctx, callbackUrl));
        } else if (previousStatus == 200) {
            val body = ctx.getBody();
            val callbackUrl = htmlDecode(substringBetween(body, "<form action=\"", "\" met"));
            val samlResponse = htmlDecode(substringBetween(body, "\"SAMLResponse\" value=\"", "\"/>"));
            val relayState = htmlDecode(substringBetween(body, "\"RelayState\" value=\"", "\"/>"));
            ctx.getFormParameters().put("SAMLResponse", samlResponse);
            ctx.getFormParameters().put("RelayState", relayState);
            ctx.setRequest(post(ctx, callbackUrl));
        } else {
            assertTrue("Status must be 302 or 200", previousStatus == 302 || previousStatus == 200);
        }

        execute(ctx);
        assertStatus(ctx, status);
    }
}
