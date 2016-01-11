import grails.plugin.springsecurity.SpringSecurityUtils

class MobileIdLoginController {

    def index() {
        def config = SpringSecurityUtils.securityConfig

        String authIdCardUrl = "${request.contextPath}${config.estonianId.filter.idCardLogin.processUrl}"
        String authMobileIdUrl = "${request.contextPath}${config.estonianId.filter.mobileIdLogin.processUrl}"
        String authSuccessUrl = "${request.contextPath}${config.estonianId.redirect.authSuccessUrl}"

        log.info config.userLookup.userDomainClassName
        log.info config.estonianId.domain.estonianIdUserClassName
        log.info config.estonianId.redirect.authSuccessUrl

        [authIdCardUrl: authIdCardUrl,
         authMobileIdUrl: authMobileIdUrl,
         authSuccessUrl: authSuccessUrl,
         domainClass: config.userLookup.userDomainClassName,
         authFailUrl: config.redirect.authFailUrl])
    }
}
