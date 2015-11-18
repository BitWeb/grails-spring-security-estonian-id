package ee.bitweb.grails.springsecurity.estonianid

import grails.plugin.springsecurity.SpringSecurityUtils
import org.springframework.security.core.GrantedAuthority

/**
 * Created by ivar on 16.11.15.
 */
class EstonianIdUserDetailsService {
    protected EstonianIdUserDetails createUserDetails(user, Collection<GrantedAuthority> authorities) {

        def conf = SpringSecurityUtils.securityConfig

        String idCodePropertyName = conf.estonianId.domain.estonainIdUserIdCodeProperty
        String givennamePropertyName = conf.estonianId.domain.estonainIdUserGivennameProperty
        String surnamePropertyName = conf.estonianId.domain.estonainIdUserSurnameProperty
        String screenNamePropertyName = conf.estonianId.domain.estonainIdUserScreenNameProperty

        String idCode = ''
        String givenname = ''
        String surname = ''
        String screenName = ''
        if (user.hasProperty(idCodePropertyName)) {
            idCode = user."$idCodePropertyName"
        }
        if (user.hasProperty(givennamePropertyName)) {
            givenname = user."$givennamePropertyName"
        }
        if (user.hasProperty(surnamePropertyName)) {
            surname = user."$surnamePropertyName"
        }
        if (user.hasProperty(screenNamePropertyName)) {
            screenName = user."$screenNamePropertyName"
        }

        new EstonianIdUserDetails(idCode, givenname, surname, screenName, authorities, user.id)
    }
}
