import grails.plugin.springsecurity.SpringSecurityUtils

class MobileIdLoginController {

    def index() {
        def config = SpringSecurityUtils.securityConfig

        String authIdCardUrl = "${request.contextPath}${config.estonianId.filter.idCardLogin.processUrl}"
        String authMobileIdUrl = "${request.contextPath}${config.estonianId.filter.mobileIdLogin.processUrl}"
        String authSuccessUrl = "${request.contextPath}${config.estonianId.redirect.authSuccessUrl}"

        render(view: 'index', model: [authIdCardUrl: authIdCardUrl,
                                      authMobileIdUrl: authMobileIdUrl,
                                      authSuccessUrl: authSuccessUrl])
    }
}
