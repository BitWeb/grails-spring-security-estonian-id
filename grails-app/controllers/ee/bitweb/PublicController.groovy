package ee.bitweb

import grails.plugin.springsecurity.SpringSecurityService

class PublicController {
    SpringSecurityService springSecurityService

    def index() {
        [user: springSecurityService.getCurrentUser()]
    }
}
