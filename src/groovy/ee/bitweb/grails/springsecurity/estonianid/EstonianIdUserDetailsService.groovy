package ee.bitweb.grails.springsecurity.estonianid

import grails.plugin.springsecurity.SpringSecurityUtils
import org.springframework.security.core.GrantedAuthority

/**
 * @author ivar
 */
class EstonianIdUserDetailsService {
    EstonianIdUserDetails createUserDetails(user, Collection<GrantedAuthority> authorities) {

        def conf = SpringSecurityUtils.securityConfig.estonianId.domain

        String idCodePropertyName = conf.estonainIdUserIdCodeProperty
        String givennamePropertyName = conf.estonainIdUserGivennameProperty
        String surnamePropertyName = conf.estonainIdUserSurnameProperty
        String screenNamePropertyName = conf.estonainIdUserScreenNameProperty

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
