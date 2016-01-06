import grails.plugin.fixtures.FixtureLoader

class BootStrap {

    FixtureLoader fixtureLoader

    def init = {
        log.info 'Bootstrap'

        /*if(User.count() == 0) {
            fixtureLoader.load('roles')
        }*/
    }
}
