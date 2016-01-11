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
import javax.xml.bind.DatatypeConverter

import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

import sun.security.provider.X509Factory

/**
 * @author ivar
 */
@Slf4j
class IdCardAuthenticationFilter extends GenericFilterBean implements ApplicationEventPublisherAware {
    String filterProcessesUrl
    AuthenticationManager authenticationManager
    SessionAuthenticationStrategy sessionAuthenticationStrategy
    ApplicationEventPublisher applicationEventPublisher
    AuthenticationSuccessHandler authenticationSuccessHandler
    AuthenticationFailureHandler authenticationFailureHandler

    boolean fGetClientCertFromHeader
    String clientCertHeaderName

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

        logger.debug('Request requires IdCard authentication')

        Authentication token

        try {
            token = attemptAuthentication(request)
            if(!token) {
                return
            }
            //sessionAuthenticationStrategy.onAuthentication(authentication, request, response)
            successfulAuthentication(request, response, token)

        } catch(IdCardAuthenticationException e) {
            unsuccessfulAuthentication(request, response, e)
        } catch(AuthenticationException e) {
            unsuccessfulAuthentication(request, response, e)
        }
    }

    Authentication attemptAuthentication(HttpServletRequest request) throws AuthenticationException {
        log.debug 'attempting authentication'

        X509Certificate cert = obtainCert(request)
        return authenticationManager.authenticate(new IdCardAuthenticationToken(cert))
    }

    private void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        log.debug("Successfully authenticated with mobileId authentication: " + authentication)

        // When a populated Authentication object is placed in the SecurityContextHolder,
        // the user is authenticated.
        SecurityContextHolder.context.authentication = authentication

        applicationEventPublisher.publishEvent(new InteractiveAuthenticationSuccessEvent(authentication, getClass()))

        authenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication)
    }

    private void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) {
        SecurityContextHolder.clearContext()
        log.debug("IdCard authentication failed: $e")
        authenticationFailureHandler.onAuthenticationFailure(request, response, e)
    }

    private X509Certificate obtainCert(HttpServletRequest request) {
        if(fGetClientCertFromHeader) {
            X509Certificate cert

            String certStr = request.getHeader(clientCertHeaderName)

            if(certStr?.length()) {
                byte[] certArr = DatatypeConverter.parseBase64Binary(certStr.replaceAll(X509Factory.BEGIN_CERT, "").replaceAll(X509Factory.END_CERT, ""))

                cert = CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(certArr))
            } else {
                log.debug 'No client certificate'
            }

            return cert
        } else {
            X509Certificate[] certs = request.getAttribute("javax.servlet.request.X509Certificate")
            if(certs) {
                return certs[0]
            } else {
                log.debug 'No client certificate'
                return null
            }
        }
    }
}
