package com.casinthecloud.simpleperf.cas;

import com.casinthecloud.simpleperf.test.CasTest;
import lombok.val;

/**
 * A test performing a CAS login in the CAS server.
 *
 * @author Jerome LELEU
 * @since 1.0.0
 */
public class CasLoginTest extends CasTest {

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
        val st = after(getLocation(), "ticket=");

        var validateUrl = getCasPrefixUrl() + "/p3/serviceValidate";
        validateUrl = addUrlParameter(validateUrl, "service", serviceUrl);
        validateUrl = addUrlParameter(validateUrl, "ticket", st);

        _request = get(validateUrl);
        execute();
        assertStatus(200);
        saveTimer();
    }

    protected String getCasPrefixUrl() {
        return "http://localhost:8080/cas";
    }

    protected String getServiceUrl() {
        return "http://localhost:8081/";
    }
}
