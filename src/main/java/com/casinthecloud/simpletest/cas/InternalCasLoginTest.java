package com.casinthecloud.simpletest.cas;

import lombok.Getter;
import lombok.Setter;

import java.net.http.HttpClient;

/**
 * A CAS test containing a login CAS test.
 *
 * @author Jerome LELEU
 * @since 1.0.0
 */
@Getter
@Setter
public abstract class InternalCasLoginTest extends CasTest {

    protected CasLoginTest casLoginTest;

    public void setCasPrefixUrl(final String casPrefixUrl) {
        super.setCasPrefixUrl(casPrefixUrl);
        casLoginTest.setCasPrefixUrl(casPrefixUrl);
    }

    public void setServiceUrl(final String serviceUrl) {
        super.setServiceUrl(serviceUrl);
        casLoginTest.setServiceUrl(serviceUrl);
    }

    public void setCasCookieName(final String casCookieName) {
        super.setCasCookieName(casCookieName);
        casLoginTest.setCasCookieName(casCookieName);
    }

    public void setUsername(final String username) {
        super.setUsername(username);
        casLoginTest.setUsername(username);
    }

    public void setPassword(final String password) {
        super.setPassword(password);
        casLoginTest.setPassword(password);
    }

    public void setClient(final HttpClient client) {
        super.setClient(client);
        casLoginTest.setClient(client);
    }

    public void setDisplayInfos(final boolean displayInfos) {
        super.setDisplayInfos(displayInfos);
        casLoginTest.setDisplayInfos(displayInfos);
    }
}
