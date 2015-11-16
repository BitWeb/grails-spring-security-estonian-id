import ee.bitweb.grails.springsecurity.GenericSecurityService

import grails.plugin.springsecurity.annotation.Secured

@Secured(['ROLE_TEST'])
class TestController {

    GenericSecurityService genericSecurityService

    def index() {
        log.info(genericSecurityService.getCurrentUser())

        [currentUser: genericSecurityService.getCurrentUser()]
    }
}
