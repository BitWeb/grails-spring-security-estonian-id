import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.SpringSecurityUtils

class IndexController {
    SpringSecurityService springSecurityService

    def index() {
        def conf = SpringSecurityUtils.securityConfig

        log.info conf.userLookup.userDomainClassName
        println conf.userLookup.userDomainClassName

        [domainClass: conf.userLookup.userDomainClassName]
        //grails.plugin.springsecurity.userLookup.userDomainClassName
    }
}
