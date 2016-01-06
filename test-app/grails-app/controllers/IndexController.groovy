import grails.plugin.springsecurity.SpringSecurityUtils

class IndexController {

    def index() {
        def conf = SpringSecurityUtils.securityConfig

        log.info conf.userLookup.userDomainClassName

        [domainClass: conf.userLookup.userDomainClassName]
        //grails.plugin.springsecurity.userLookup.userDomainClassName
    }
}
