package com.casinthecloud.simpletest.cas;

import com.casinthecloud.simpletest.execution.Context;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

import static com.casinthecloud.simpletest.util.Utils.addUrlParameter;
import static org.apache.commons.lang3.StringUtils.substringAfter;

/**
 * A test performing a ST validation in the CAS server.
 *
 * @author Jerome LELEU
 * @since 1.0.0
 */
@Getter
@Setter
public class CasValidate extends CasTest {

    public void run(final Context ctx) throws Exception {

        val callbackUrl = getLocation(ctx);
        val st = substringAfter(callbackUrl, "ticket=");
        info("Service ticket: " + st);

        var validateUrl = getCasPrefixUrl() + "/p3/serviceValidate";
        validateUrl = addUrlParameter(validateUrl, "service", getServiceUrl());
        validateUrl = addUrlParameter(validateUrl, "ticket", st);

        ctx.setRequest(get(ctx, validateUrl));
        execute(ctx);
        assertStatus(ctx, 200);

    }
}
