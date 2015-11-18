package ee.bitweb.grails.springsecurity.estonianid

import groovy.util.logging.Log4j
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.ApplicationEventPublisherAware
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy
import org.springframework.web.filter.GenericFilterBean

import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import java.security.cert.X509Certificate

/**
 * Created by ivar on 12.11.15.
 */
@Log4j
class IdCardAuthenticationFilter extends GenericFilterBean implements ApplicationEventPublisherAware {
    String filterProcessesUrl
    AuthenticationManager authenticationManager
    SessionAuthenticationStrategy sessionAuthenticationStrategy
    ApplicationEventPublisher applicationEventPublisher
    AuthenticationSuccessHandler authenticationSuccessHandler
    AuthenticationFailureHandler authenticationFailureHandler

    @Override
    void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)  throws IOException,
            ServletException {
        HttpServletRequest request = (HttpServletRequest) req
        HttpServletResponse response = (HttpServletResponse) resp

        // If the request URI doesn't contain the filterProcessesUrl,
        // it isn't a request that should be handled by this filter
        if(!request.getRequestURI().contains(filterProcessesUrl)) {
            chain.doFilter(request, response)
            return
        }

        logger.debug('Request requires IdCard authentication')

        Authentication token

        try {
            token = attemptAuthentication(request)
            if(!token) {
                return
            }
            //sessionAuthenticationStrategy.onAuthentication(authentication, request, response)

        } catch(IdCardAuthenticationException ex) {
            unsuccessfulAuthentication(request, response, ex)
            return
        } catch(AuthenticationException ex) {
            unsuccessfulAuthentication(request, response, ex)
            return
        }

        successfulAuthentication(request, response, token)
    }

    public Authentication attemptAuthentication(HttpServletRequest request) throws AuthenticationException {
        log.debug 'attempting authentication'

        X509Certificate cert = obtainCert(request)

        IdCardAuthenticationToken token = new IdCardAuthenticationToken(cert)
        return this.getAuthenticationManager().authenticate(token)
    }

    private void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        log.debug("Successfully authenticated with mobileId authentication: " + authentication)

        // When a populated Authentication object is placed in the SecurityContextHolder,
        // the user is authenticated.
        SecurityContextHolder.getContext().setAuthentication(authentication)

        applicationEventPublisher.publishEvent(new InteractiveAuthenticationSuccessEvent(authentication, this.getClass()))

        authenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication)
    }

    private void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex) {
        SecurityContextHolder.clearContext()
        log.debug('IdCard authentication failed: ' + ex.toString())
        authenticationFailureHandler.onAuthenticationFailure(request, response, ex)
    }

    private X509Certificate obtainCert(HttpServletRequest request) {
        X509Certificate[] certs = (X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate")
        if(certs && certs.size()) {
            return certs[0]
        } else {
            log.debug 'cert == null ......................'
            return null
        }
    }
}
