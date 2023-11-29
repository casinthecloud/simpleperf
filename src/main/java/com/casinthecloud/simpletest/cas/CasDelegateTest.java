package com.casinthecloud.simpletest.cas;

import com.casinthecloud.simpletest.execution.Context;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

import static org.apache.commons.lang3.StringUtils.substringBetween;

/**
 * A test performing a CAS authn delegation in the CAS server.
 *
 * @author Jerome LELEU
 * @since 1.0.0
 */
@Getter
@Setter
public class CasDelegateTest extends CasLoginTest {

    private final String clientName;

    public CasDelegateTest(final String clientName, final CasTest casTest) {
        this.tests = new CasTest[] { casTest };
        this.clientName = clientName;
    }

    public void run(final Context ctx) throws Exception {

        val loginUrl = callLoginPage(ctx);

        delegate(ctx, loginUrl);

        super.run(ctx);

        callback(ctx);
        assertStatus(ctx, 302);

        callback(ctx);
        assertStatus(ctx, 302);
    }

    protected void delegate(final Context ctx, final String loginUrl) throws Exception {
        val webflow = substringBetween(ctx.getBody(), "name=\"execution\" value=\"", "\"/>");

        ctx.getFormParameters().put("client_name", getClientName());
        ctx.getFormParameters().put("_eventId", "delegatedAuthenticationRedirect");
        ctx.getFormParameters().put("execution", webflow);

        ctx.setRequest(post(ctx, loginUrl));
        execute(ctx);

        saveCasSession(ctx);
    }
}
