package ee.bitweb.grails.springsecurity.estonianid

import groovy.util.logging.Slf4j
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
 * @author ivar
 */
@Slf4j
class MobileIdAuthenticationFilter extends GenericFilterBean implements ApplicationEventPublisherAware {
    String filterProcessesUrl
    AuthenticationManager authenticationManager
    SessionAuthenticationStrategy sessionAuthenticationStrategy
    ApplicationEventPublisher applicationEventPublisher
    AuthenticationSuccessHandler authenticationSuccessHandler
    AuthenticationFailureHandler authenticationFailureHandler

    def localeResolver
    String defaultLanguageCode
    Map localeToLangMap

    @Override
    void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)  throws IOException, ServletException {
        HttpServletRequest request = req
        HttpServletResponse response = resp

        // If the request URI doesn't contain the filterProcessesUrl,
        // it isn't a request that should be handled by this filter
        if(!request.requestURI.contains(filterProcessesUrl)) {
            chain.doFilter(request, response)
            return
        }

        log.debug('Request requires mobileId authentication')

        Authentication token = SecurityContextHolder.context.authentication

        try {
            token = attemptAuthentication(request, token)
            if(!token) {
                return
            }
            //sessionAuthenticationStrategy.onAuthentication(authentication, request, response)
            successfulAuthentication(request, response, token)
        } catch(MobileIdAuthenticationOutstandingException ex) {
            insufficientAuthentication(request, response, ex)
        } catch(MobileIdAuthenticationException ex) {
            unsuccessfulAuthentication(request, response, ex)
        } catch(AuthenticationException ex) {
            unsuccessfulAuthentication(request, response, ex)
        }
    }

    Authentication attemptAuthentication(HttpServletRequest request, MobileIdAuthenticationToken token) throws AuthenticationException {
        String phoneNo = obtainPhoneNo(request)?.trim()

        if(token == null) {
            token = new MobileIdAuthenticationToken(phoneNo)
        } else {
            if(token.userPhoneNo != phoneNo) {
                token = new MobileIdAuthenticationToken(phoneNo)
            }
        }

        Locale locale = localeResolver.resolveLocale(request)
        String languageCode = defaultLanguageCode
        if (locale && localeToLangMap.containsKey(locale.toString())) {
            languageCode = localeToLangMap[locale.toString()]
        }
        token.userLanguageCode = languageCode

        return authenticationManager.authenticate(token)
    }

    private void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        SecurityContextHolder.context.authentication = authentication

        applicationEventPublisher.publishEvent(new InteractiveAuthenticationSuccessEvent(authentication, getClass()))

        authenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication)
    }

    private void insufficientAuthentication(HttpServletRequest request, HttpServletResponse response, MobileIdAuthenticationException e) {
        SecurityContextHolder.context.authentication = e.authentication
        authenticationFailureHandler.onAuthenticationFailure(request, response, e)
    }

    private void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) {
        SecurityContextHolder.clearContext()
        authenticationFailureHandler.onAuthenticationFailure(request, response, e)
    }

    private String obtainPhoneNo(HttpServletRequest request) {
        return request.getParameter('phoneNo')
    }
}
