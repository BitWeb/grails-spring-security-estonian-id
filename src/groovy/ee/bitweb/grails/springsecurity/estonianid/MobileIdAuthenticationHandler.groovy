package ee.bitweb.grails.springsecurity.estonianid

import groovy.util.logging.Log4j
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException

import grails.converters.JSON

/**
 * Created by ivar on 12.11.15.
 */
@Log4j
class MobileIdAuthenticationHandler implements AuthenticationSuccessHandler, AuthenticationFailureHandler {

    void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex) throws IOException, ServletException {
        log.info 'MobileIdAuthenticationHandler::onAuthenticationFailure'

        //response.status = HttpServletResponse.SC_UNAUTHORIZED

        MobileIdAuthenticationException mIdEx = (MobileIdAuthenticationException)ex
        MobileIdAuthenticationToken token = mIdEx.authentication
        MobileIdAuthenticationSession authSession = token.authSession

        JSON json = new JSON([test: 'xxx'])
        json.render(response)
    }

    void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        MobileIdAuthenticationToken token = (MobileIdAuthenticationToken)authentication

        /*Map data = [authenticated: true, uid: token.uid, roles: token.authorities?.collect { GrantedAuthority it -> it.authority }]
        if (token.principal instanceof UserDetails) {
            data.username = ((UserDetails)token.principal).username
            data.enabled = ((UserDetails)token.principal).enabled
        }
        data = callAuthServiceOnJsonSuccess(data, authentication)
        JSON json = new JSON(data)
        if (useJsonp) {
            renderAsJSONP(json, request, response)
        } else {
            json.render(response)
        }*/
    }
}
