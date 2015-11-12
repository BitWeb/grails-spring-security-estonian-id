package ee.bitweb.grails.springsecurity.estonianid

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

/**
 * Created by ivar on 12.11.15.
 */
class IdCardAuthenticationToken implements Authentication {
    Object phoneNo

    Object credentials
    Object principal
    Object details

    Collection<GrantedAuthority> authorities = []

    Boolean authenticated = false

    public MobileIdAuthenticationToken(String phoneNo) {
        this.phoneNo = phoneNo
        this.authorities << new SimpleGrantedAuthority('ROLE_USER')
    }

    @Override
    boolean isAuthenticated() {
        return authenticated
    }

    @Override
    void setAuthenticated(boolean authenticated) throws IllegalArgumentException {
        this.authenticated = authenticated
    }

    @Override
    String getName() {
        return ''
    }
}