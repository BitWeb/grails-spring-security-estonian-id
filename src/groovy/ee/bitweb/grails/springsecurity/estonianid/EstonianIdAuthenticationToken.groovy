package ee.bitweb.grails.springsecurity.estonianid

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

/**
 * Created by ivar on 12.11.15.
 */
class EstonianIdAuthenticationToken extends AbstractAuthenticationToken {

    String userIdCode
    String userName

    Object credentials
    Object principal

    public EstonianIdAuthenticationToken() {
        super(null)
    }
}