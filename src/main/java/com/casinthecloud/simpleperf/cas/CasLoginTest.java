package com.casinthecloud.simpleperf.cas;

import com.casinthecloud.simpleperf.test.CasTest;
import lombok.val;
import org.pac4j.core.util.CommonHelper;

public class CasLoginTest extends CasTest {

    public void run() throws Exception {

        startTimer();

        val serviceUrl = getServiceUrl();

        var loginUrl = getCasPrefixUrl() + "/login";
        loginUrl = CommonHelper.addParameter(loginUrl, "service", serviceUrl);

        _request = get(loginUrl);
        execute();
        String webflow = between(_body, "name=\"execution\" value=\"", "\"/>");
        assertStatus(200);

        _data.put("username", getUsername());
        _data.put("password", getPassword());
        _data.put("execution", webflow);
        _data.put("_eventId", "submit");
        _data.put("geolocation", "");
        _request = post(loginUrl);
        execute();
        assertStatus(302);
        val callbackUrl = getLocation();
        val st = after(getLocation(), "ticket=");

        var validateUrl = getCasPrefixUrl() + "/p3/serviceValidate";
        validateUrl = CommonHelper.addParameter(validateUrl, "service", serviceUrl);
        validateUrl = CommonHelper.addParameter(validateUrl, "ticket", st);

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

    protected String getUsername() {
        return "jleleu";
    }

    protected String getPassword() {
        return "jleleu";
    }
}
