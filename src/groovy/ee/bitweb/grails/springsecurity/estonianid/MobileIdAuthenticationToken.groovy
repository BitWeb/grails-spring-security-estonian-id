package ee.bitweb.grails.springsecurity.estonianid

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils

/**
 * @author ivar
 */
class MobileIdAuthenticationToken extends EstonianIdAuthenticationToken {

    String userPhoneNo
    String userLanguageCode
    MobileIdAuthenticationSession authSession

    MobileIdAuthenticationToken(Collection<? extends GrantedAuthority> authorities, String phoneNo, String userLanguageCode, MobileIdAuthenticationSession authSession) {
        super(authorities)
        userPhoneNo = phoneNo
        this.authSession = authSession
    }

    MobileIdAuthenticationToken(String phoneNo, String userLanguageCode = 'EST') {
        this(AuthorityUtils.NO_AUTHORITIES, phoneNo, userLanguageCode, null)
    }
}
