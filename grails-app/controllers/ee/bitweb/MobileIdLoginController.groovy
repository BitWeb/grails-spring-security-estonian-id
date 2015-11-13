package ee.bitweb

import grails.plugin.springsecurity.SpringSecurityUtils

class MobileIdLoginController {

    def index() {
        def config = SpringSecurityUtils.securityConfig

        String postUrl = "${request.contextPath}${config.estonianId.filter.mobileIdLogin.processUrl}"
        String authSuccessUrl = "${request.contextPath}${config.estonianId.redirect.authSuccessUrl}"

        [postUrl: postUrl, authSuccessUrl: authSuccessUrl]
    }
}
