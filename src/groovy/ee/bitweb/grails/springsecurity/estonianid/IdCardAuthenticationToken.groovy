package ee.bitweb.grails.springsecurity.estonianid

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils

import java.security.cert.X509Certificate

/**
 * Created by ivar on 12.11.15.
 */
class IdCardAuthenticationToken extends EstonianIdAuthenticationToken {
    X509Certificate userCert

    public IdCardAuthenticationToken(Collection<? extends GrantedAuthority> authorities, X509Certificate userCert) {
        super(authorities)
        this.userCert = userCert
    }

    public IdCardAuthenticationToken(X509Certificate userCert) {
        super(AuthorityUtils.NO_AUTHORITIES)
        this.userCert = userCert
    }
}