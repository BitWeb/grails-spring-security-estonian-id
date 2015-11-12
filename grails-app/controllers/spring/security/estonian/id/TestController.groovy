package spring.security.estonian.id

import grails.plugin.springsecurity.annotation.Secured

@Secured(['ROLE_IS_AUTHENTICATED'])
class TestController {

    def index() { }
}
