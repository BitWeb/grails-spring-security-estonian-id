package ee.bitweb.grails.springsecurity

import ee.bitweb.grails.springsecurity.userdetails.GenericUser
import grails.plugin.springsecurity.SpringSecurityService

class GenericSecurityService extends SpringSecurityService {

    static transactional = false

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
        assert principal instanceof GenericUser

        getClassForName(securityConfig.userLookup.userDomainClassName).load(currentUserId)
    }
}
