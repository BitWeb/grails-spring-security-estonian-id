package ee.bitweb.grails.springsecurity.estonianid

import grails.converters.JSON
import groovy.util.logging.Slf4j
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @author ivar
 */
@Slf4j
class MobileIdAuthenticationHandler implements AuthenticationSuccessHandler, AuthenticationFailureHandler {

    void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        //response.status = HttpServletResponse.SC_UNAUTHORIZED

        MobileIdAuthenticationException mIdEx = e
        MobileIdAuthenticationToken token = mIdEx.authentication
        MobileIdAuthenticationSession authSession = token.authSession

        new JSON([errorCode: authSession.errorCode, status: authSession.status, challengeId: authSession.challengeId]).render(response)
    }

    void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        MobileIdAuthenticationToken token = authentication
        MobileIdAuthenticationSession authSession = token.authSession

        new JSON([errorCode: authSession.errorCode, status: authSession.status, challengeId: authSession.challengeId]).render(response)
    }
}
