package ee.bitweb.grails.springsecurity.estonianid

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils

import java.security.cert.X509Certificate

/**
 * @author ivar
 */
class IdCardAuthenticationToken extends EstonianIdAuthenticationToken {
    X509Certificate userCert

    IdCardAuthenticationToken(Collection<? extends GrantedAuthority> authorities, X509Certificate userCert) {
        super(authorities)
        this.userCert = userCert
    }

    IdCardAuthenticationToken(X509Certificate userCert) {
        this(AuthorityUtils.NO_AUTHORITIES, userCert)
    }
}
