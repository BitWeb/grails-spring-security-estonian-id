package ee.bitweb

import grails.plugin.springsecurity.annotation.Secured

@Secured(['ROLE_TEST'])
class TestController {

    def index() { }
}
