package ee.bitweb.grails.springsecurity

import grails.plugin.springsecurity.SpringSecurityService

import grails.plugin.springsecurity.SpringSecurityUtils
import ee.bitweb.grails.springsecurity.userdetails.GenericUser
import grails.transaction.Transactional
import org.springframework.util.Assert

import javax.servlet.http.HttpServletRequest

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder as SCH
import org.springframework.util.Assert


class GenericSecurityService extends SpringSecurityService {

    @Override
    def getCurrentUser() {
        if (!isLoggedIn()) {
            return null
        }

        def User = getClassForName(securityConfig.userLookup.userDomainClassName)

        if (principal instanceof GenericUser) {
            User.get principal.id
        }
        else {
            return null
            //TODO
            /*User.createCriteria().get {
                eq securityConfig.userLookup.usernamePropertyName, principal.username
                cache true
            }*/
        }
    }

    @Override
    def getCurrentUserId() {
        def principal = getPrincipal()
        principal instanceof GenericUser ? principal.id : null
    }

    @Override
    def loadCurrentUser() {
        if (!isLoggedIn()) {
            return null
        }

        // load() requires an id, so this only works if there's an id property in the principal
        Assert.isInstanceOf GenericUser, principal

        getClassForName(securityConfig.userLookup.userDomainClassName).load(currentUserId)
    }
}
