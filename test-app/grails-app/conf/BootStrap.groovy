import grails.plugin.fixtures.FixtureLoader

class BootStrap {

    def grailsApplication
    FixtureLoader fixtureLoader

    def init = { servletContext ->
        log.info 'Bootstrap'

        /*if(User.count() == 0) {
            fixtureLoader.load('roles')
        }*/
    }
    def destroy = {
    }
}
