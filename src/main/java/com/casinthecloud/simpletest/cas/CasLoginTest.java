package com.casinthecloud.simpletest.cas;

import com.casinthecloud.simpletest.test.CasTest;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

import static com.casinthecloud.simpletest.util.Utils.addUrlParameter;
import static org.apache.commons.lang3.StringUtils.substringAfter;

/**
 * A test performing a CAS login in the CAS server.
 *
 * @author Jerome LELEU
 * @since 1.0.0
 */
@Getter
@Setter
public class CasLoginTest extends CasTest {

    private String serviceUrl = "http://localhost:8081/";

    public void run(final Map<String, Object> ctx) throws Exception {

        startTimer();
        val serviceUrl = getServiceUrl();

        var loginUrl = getCasPrefixUrl() + "/login";
        loginUrl = addUrlParameter(loginUrl, "service", serviceUrl);
        var tgc = (Pair<String, String>) ctx.get(TGC);

        if (tgc != null) {
            info("Re-use " + tgc.getLeft() + " : " + tgc.getRight());
            _cookies.put(getCasCookieName(), tgc.getRight());
        }
        _request = get(loginUrl);
        execute();
        if (_status == 200) {

            executePostCasCredentials(loginUrl);
            tgc = getCookie(getCasCookieName());
            ctx.put(TGC, tgc);
            info("Found " + tgc.getLeft() + " : " + tgc.getRight());

        } else {
            assertStatus(302);
        }

        val callbackUrl = getLocation();
        val st = substringAfter(callbackUrl, "ticket=");
        var validateUrl = getCasPrefixUrl() + "/p3/serviceValidate";
        validateUrl = addUrlParameter(validateUrl, "service", serviceUrl);
        validateUrl = addUrlParameter(validateUrl, "ticket", st);

        _request = get(validateUrl);
        execute();
        assertStatus(200);
        saveTimer();
    }
}
