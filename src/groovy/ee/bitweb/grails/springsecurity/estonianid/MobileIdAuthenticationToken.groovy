package ee.bitweb.grails.springsecurity.estonianid

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.authority.SimpleGrantedAuthority

/**
 * Created by ivar on 12.11.15.
 */
class MobileIdAuthenticationToken extends EstonianIdAuthenticationToken {

    String userPhoneNo
    String userLanguageCode
    MobileIdAuthenticationSession authSession

    public MobileIdAuthenticationToken(Collection<? extends GrantedAuthority> authorities, String phoneNo, String userLanguageCode, MobileIdAuthenticationSession authSession) {
        super(authorities)
        this.userPhoneNo = phoneNo
        this.authSession = authSession
    }

    public MobileIdAuthenticationToken(String phoneNo, String userLanguageCode = 'EST') {
        super(AuthorityUtils.NO_AUTHORITIES)
        this.userPhoneNo = phoneNo
        this.userLanguageCode = userLanguageCode
    }
}