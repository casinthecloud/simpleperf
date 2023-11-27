package com.casinthecloud.simpletest.test;

import lombok.Getter;
import lombok.Setter;
import lombok.val;

import static com.casinthecloud.simpletest.util.Utils.between;

/**
 * A test for CAS.
 *
 * @author Jerome LELEU
 * @since 1.0.0
 */

@Getter
@Setter
public abstract class CasTest extends WebTest {

    protected static final String TGC = "TGC";
    protected static final String DISSESSION = "DISSESSION";

    private String casPrefixUrl = "http://localhost:8080/cas";

    private String username = "jleleu";

    private String password = "jleleu";

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
}
