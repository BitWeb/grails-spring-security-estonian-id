package ee.bitweb.grails.springsecurity.estonianid

import grails.converters.JSON
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created by Ivar on 13.11.2015.
 */
class IdCardAuthenticationHandler implements AuthenticationSuccessHandler, AuthenticationFailureHandler {

    void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex) throws IOException, ServletException {
        response.status = HttpServletResponse.SC_UNAUTHORIZED

        JSON json = new JSON([status: 'BAD_CERTIFICATE'])
        json.render(response)
    }

    void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        JSON json = new JSON([status: 'SUCCESS'])
        json.render(response)
    }
}