import ee.bitweb.grails.springsecurity.GenericSecurityService

import grails.plugin.springsecurity.annotation.Secured

@Secured('ROLE_DEFAULT')
class TestController {

    GenericSecurityService genericSecurityService

    def index() {
        def user = genericSecurityService.currentUser

        log.info(user)

        [currentUser: user]
    }
}
