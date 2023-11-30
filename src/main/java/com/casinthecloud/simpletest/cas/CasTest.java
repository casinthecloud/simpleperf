package com.casinthecloud.simpletest.cas;

import com.casinthecloud.simpletest.execution.Context;
import com.casinthecloud.simpletest.test.ChainingTest;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;

/**
 * A test for CAS.
 *
 * @author Jerome LELEU
 * @since 1.0.0
 */

@Getter
@Setter
public abstract class CasTest extends ChainingTest {

    private static final String JSESSIONID = "JSESSIONID";
    private static final String DISSESSION = "DISSESSION";
    private static final String CAS_SESSION = "CAS_SESSION";
    private static final String TGC = "TGC";

    private String casPrefixUrl = "http://localhost:8080/cas";

    private String serviceUrl = "http://localhost:8081/";

    private String casCookieName = TGC;

    private String username = "jleleu";

    private String password = "jleleu";

    protected void saveCasSession(final Context ctx) {
        var casSession = getCookie(ctx, DISSESSION);
        if (casSession == null) {
            casSession = getCookie(ctx, JSESSIONID);
        }
        ctx.put(testId + CAS_SESSION, casSession);
        info("Found CAS session: " + casSession.getLeft() + "=" + casSession.getRight());
    }

    protected void useCasSession(final Context ctx) {
        val casSession = (Pair<String, String>) ctx.get(testId + CAS_SESSION);
        if (casSession != null) {
            info("Re-use: " + casSession.getLeft() + "=" + casSession.getRight());
            ctx.getCookies().put(casSession.getLeft(), casSession.getRight());
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
        val callbackUrl = getLocation(ctx);

        useSsoSession(ctx);
        useCasSession(ctx);

        ctx.setRequest(get(ctx, callbackUrl));
        execute(ctx);
        assertStatus(ctx, status);
    }
}
