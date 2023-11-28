package com.casinthecloud.simpletest.cas;

import com.casinthecloud.simpletest.execution.Context;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;

import static com.casinthecloud.simpletest.util.Utils.addUrlParameter;
import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.substringBetween;

/**
 * A test performing a CAS login in the CAS server.
 *
 * @author Jerome LELEU
 * @since 1.0.0
 */
@Getter
@Setter
public class CasLoginTest extends CasTest {

    public void run(final Context ctx) throws Exception {

        val loginUrl = addUrlParameter(getCasPrefixUrl() + "/login", "service", getServiceUrl());
        login(ctx, loginUrl);

        validateSt(ctx);

    }

    public String login(final Context ctx, final String loginUrl) throws Exception {

        callLoginPage(ctx, loginUrl);

        if (ctx.getStatus() == 200) {
            postCredentials(ctx, loginUrl);
        }

        assertStatus(ctx, 302);

        return getLocation(ctx);
    }

    public void callLoginPage(final Context ctx, final String loginUrl) throws Exception {
        val tgc = (Pair<String, String>) ctx.getData().get(TGC);

        if (tgc != null) {
            info("Re-use: " + tgc.getLeft() + "=" + tgc.getRight());
            ctx.getCookies().put(getCasCookieName(), tgc.getRight());
        }

        ctx.setRequest(get(ctx, loginUrl));
        execute(ctx);
    }

    public void postCredentials(final Context ctx, final String loginUrl) throws Exception {
        val webflow = substringBetween(ctx.getBody(), "name=\"execution\" value=\"", "\"/>");

        ctx.getFormParameters().put("username", getUsername());
        ctx.getFormParameters().put("password", getPassword());
        ctx.getFormParameters().put("execution", webflow);
        ctx.getFormParameters().put("_eventId", "submit");
        ctx.getFormParameters().put("geolocation", "");

        ctx.setRequest(post(ctx, loginUrl));
        execute(ctx);

        val tgc = getCookie(ctx, getCasCookieName());
        ctx.getData().put(TGC, tgc);
        info("Found: " + tgc.getLeft() + "=" + tgc.getRight());
    }

    public void validateSt(final Context ctx) throws Exception {
        val callbackUrl = getLocation(ctx);
        val st = substringAfter(callbackUrl, "ticket=");

        var validateUrl = getCasPrefixUrl() + "/p3/serviceValidate";
        validateUrl = addUrlParameter(validateUrl, "service", getServiceUrl());
        validateUrl = addUrlParameter(validateUrl, "ticket", st);

        ctx.setRequest(get(ctx, validateUrl));
        execute(ctx);
        assertStatus(ctx, 200);
    }
}
