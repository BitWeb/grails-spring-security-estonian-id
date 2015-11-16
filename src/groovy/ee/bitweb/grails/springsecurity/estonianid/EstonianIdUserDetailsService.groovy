package ee.bitweb.grails.springsecurity.estonianid

import grails.plugin.springsecurity.SpringSecurityUtils
import org.springframework.security.core.GrantedAuthority

/**
 * Created by ivar on 16.11.15.
 */
class EstonianIdUserDetailsService {
    protected EstonianIdUserDetails createUserDetails(user, Collection<GrantedAuthority> authorities) {

        def conf = SpringSecurityUtils.securityConfig

        String idCodePropertyName = conf.estonianId.domain.idCodePropertyName
        String givennamePropertyName = conf.estonianId.domain.givennamePropertyName
        String surnamePropertyName = conf.estonianId.domain.surnamePropertyName
        String screenNamePropertyName = conf.estonianId.domain.screenNamePropertyName
        String enabledPropertyName = conf.userLookup.enabledPropertyName
        String accountExpiredPropertyName = conf.userLookup.accountExpiredPropertyName
        String accountLockedPropertyName = conf.userLookup.accountLockedPropertyName
        String passwordExpiredPropertyName = conf.userLookup.passwordExpiredPropertyName

        String idCode = user."$idCodePropertyName"
        String givenname = user."$givennamePropertyName"
        String surname = user."$surnamePropertyName"
        String screenName = user."$screenNamePropertyName"

        new EstonianIdUserDetails(idCode, givenname, surname, screenName, authorities, user.id)
    }
}
