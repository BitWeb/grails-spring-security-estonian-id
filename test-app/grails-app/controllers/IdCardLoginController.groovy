import grails.plugin.springsecurity.SpringSecurityUtils

class IdCardLoginController {

    def index() {
        def config = SpringSecurityUtils.securityConfig

        String postUrl = "${request.contextPath}${config.estonianId.filter.idCardLogin.processUrl}"
        String authSuccessUrl = "${request.contextPath}${config.estonianId.redirect.authSuccessUrl}"

        [postUrl: postUrl, authSuccessUrl: authSuccessUrl]
    }
}
