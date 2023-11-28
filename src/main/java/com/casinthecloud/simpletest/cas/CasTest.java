package com.casinthecloud.simpletest.cas;

import com.casinthecloud.simpletest.test.WebTest;
import lombok.Getter;
import lombok.Setter;

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
    protected static final String CAS_SESSION = "CAS_SESSION";

    private String casPrefixUrl = "http://localhost:8080/cas";

    private String serviceUrl = "http://localhost:8081/";

    private String casCookieName = TGC;

    private String username = "jleleu";

    private String password = "jleleu";
}
