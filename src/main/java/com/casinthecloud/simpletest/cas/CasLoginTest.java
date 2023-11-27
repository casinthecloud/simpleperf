package com.casinthecloud.simpletest.cas;

import com.casinthecloud.simpletest.test.CasTest;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

import static com.casinthecloud.simpletest.util.Utils.addUrlParameter;
import static com.casinthecloud.simpletest.util.Utils.after;

/**
 * A test performing a CAS login in the CAS server.
 *
 * @author Jerome LELEU
 * @since 1.0.0
 */
@Getter
@Setter
public class CasLoginTest extends CasTest {

    private String casPrefixUrl = "http://localhost:8080/cas";

    private String serviceUrl = "http://localhost:8081/";

    public void run() throws Exception {

        startTimer();
        val serviceUrl = getServiceUrl();

        var loginUrl = getCasPrefixUrl() + "/login";
        loginUrl = addUrlParameter(loginUrl, "service", serviceUrl);

        _request = get(loginUrl);
        execute();
        assertStatus(200);

        executePostCasCredentials(loginUrl);
        val callbackUrl = getLocation();
        val st = after(callbackUrl, "ticket=");

        var validateUrl = getCasPrefixUrl() + "/p3/serviceValidate";
        validateUrl = addUrlParameter(validateUrl, "service", serviceUrl);
        validateUrl = addUrlParameter(validateUrl, "ticket", st);

        _request = get(validateUrl);
        execute();
        assertStatus(200);
        saveTimer();
    }
}
