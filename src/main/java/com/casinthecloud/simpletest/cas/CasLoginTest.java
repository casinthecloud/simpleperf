package com.casinthecloud.simpletest.cas;

import com.casinthecloud.simpletest.execution.Context;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

import static com.casinthecloud.simpletest.util.Utils.addUrlParameter;
import static org.apache.commons.lang3.StringUtils.isBlank;
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

        val loginUrl = callLoginPage(ctx);

        if (ctx.getStatus() == 200) {
            postCredentials(ctx, loginUrl);
        }

        assertStatus(ctx, 302);

    }

    protected String callLoginPage(final Context ctx) throws Exception {
        var loginUrl = getLocation(ctx);
        if (isBlank(loginUrl)) {
            loginUrl = addUrlParameter(getCasPrefixUrl() + "/login", "service", getServiceUrl());
        }

        useSsoSession(ctx);

        ctx.setRequest(get(ctx, loginUrl));
        execute(ctx);

        return loginUrl;
    }



    protected void postCredentials(final Context ctx, final String loginUrl) throws Exception {
        val webflow = substringBetween(ctx.getBody(), "name=\"execution\" value=\"", "\"/>");

        ctx.getFormParameters().put("username", getUsername());
        ctx.getFormParameters().put("password", getPassword());
        ctx.getFormParameters().put("execution", webflow);
        ctx.getFormParameters().put("_eventId", "submit");
        ctx.getFormParameters().put("geolocation", "");

        ctx.setRequest(post(ctx, loginUrl));
        execute(ctx);

        saveSsoSession(ctx);
    }
}
