package com.casinthecloud.simpleperf.test;

import lombok.val;

/**
 * A test for CAS.
 *
 * @author Jerome LELEU
 * @since 1.0.0
 */

public abstract class CasTest extends WebTest {

    protected static final String TGC = "TGC";
    protected static final String DISSESSION = "DISSESSION";

    protected void executePostCasCredentials(final String casLoginUrl) throws Exception {
        val webflow = between(_body, "name=\"execution\" value=\"", "\"/>");

        _data.put("username", getUsername());
        _data.put("password", getPassword());
        _data.put("execution", webflow);
        _data.put("_eventId", "submit");
        _data.put("geolocation", "");
        _request = post(casLoginUrl);
        execute();
        assertStatus(302);
    }

    protected String getUsername() {
        return "jleleu";
    }

    protected String getPassword() {
        return "jleleu";
    }
}
