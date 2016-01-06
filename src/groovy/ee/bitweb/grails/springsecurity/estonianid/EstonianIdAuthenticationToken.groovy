package ee.bitweb.grails.springsecurity.estonianid

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

/**
 * @author ivar
 */
class EstonianIdAuthenticationToken extends AbstractAuthenticationToken {

    String userIdCode
    String userGivenname
    String userSurname

    def credentials
    def principal

    EstonianIdAuthenticationToken(Collection<? extends GrantedAuthority> authorities) {
        super(authorities)
    }
}
