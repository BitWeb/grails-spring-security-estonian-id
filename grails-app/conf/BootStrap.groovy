import ee.bitweb.TestUser
import grails.plugin.fixtures.FixtureLoader

class BootStrap {

    def grailsApplication
    FixtureLoader fixtureLoader

    def init = { servletContext ->
        log.info 'Bootstrap'

        if(TestUser.count() == 0) {
            fixtureLoader.load('roles')
        }
    }
    def destroy = {
    }
}
