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

/**
 * Created by ivar on 12.11.15.
 */
@Log4j
class MobileIdAuthenticationFilter extends GenericFilterBean implements ApplicationEventPublisherAware {
    String filterProcessesUrl
    AuthenticationManager authenticationManager
    SessionAuthenticationStrategy sessionAuthenticationStrategy
    ApplicationEventPublisher applicationEventPublisher
    AuthenticationSuccessHandler authenticationSuccessHandler
    AuthenticationFailureHandler authenticationFailureHandler

    MobileIdAuthenticationFilter() {
        log.info('MobileIdAuthenticationFilter construct')
    }

    @Override
    void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)  throws IOException,
            ServletException {
        HttpServletRequest request = (HttpServletRequest) req
        HttpServletResponse response = (HttpServletResponse) resp

        // If the request URI doesn't contain the filterProcessesUrl,
        // it isn't a request that should be handled by this filter
        if(!request.getRequestURI().contains(filterProcessesUrl)) {
            log.info 'filterProcessesUrl(\''+filterProcessesUrl+'\') doesn\'t match with \''+request.getRequestURI()+'\''
            chain.doFilter(request, response)
            return
        }

        logger.debug('Request requires mobileId authentication')

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication()

        try {
            authentication = attemptAuthentication(request, authentication)
            if(!authentication) {
                return
            }
            //sessionAuthenticationStrategy.onAuthentication(authentication, request, response)

        } catch(MobileIdAuthenticationException ex) {
            insufficientAuthentication(request, response, ex)
            return
        } catch(AuthenticationException ex) {
            unsuccessfulAuthentication(request, response, ex)
            return
        }

        successfulAuthentication(request, response, authentication)
    }

    public Authentication attemptAuthentication(HttpServletRequest request, MobileIdAuthenticationToken token) throws AuthenticationException {
        log.info 'attempting authentication'

        if(token == null) {
            String phoneNo = obtainPhoneNo(request)?.trim()
            token = new MobileIdAuthenticationToken(phoneNo)
        }

        return this.getAuthenticationManager().authenticate(token)
    }

    private void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        logger.debug("Successfully authenticated with mobileId authentication: " + authentication)

        // When a populated Authentication object is placed in the SecurityContextHolder,
        // the user is authenticated.
        SecurityContextHolder.getContext().setAuthentication(authentication)

        applicationEventPublisher.publishEvent(new InteractiveAuthenticationSuccessEvent(authentication, this.getClass()))

        authenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication)
    }

    private void insufficientAuthentication(HttpServletRequest request, HttpServletResponse response, MobileIdAuthenticationException ex) {
        SecurityContextHolder.getContext().setAuthentication(ex.authentication)
        logger.debug('mobileId authentication insufficient: ' + ex.toString())
        authenticationFailureHandler.onAuthenticationFailure(request, response, ex)
    }

    private void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex) {
        SecurityContextHolder.clearContext()
        logger.debug('mobileId authentication failed: ' + ex.toString())
        authenticationFailureHandler.onAuthenticationFailure(request, response, ex)
    }

    private String obtainPhoneNo(HttpServletRequest request) {
        return request.getParameter('phoneNo')
    }
}