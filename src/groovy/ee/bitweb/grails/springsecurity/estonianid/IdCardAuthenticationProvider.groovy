package ee.bitweb.grails.springsecurity.estonianid

import groovy.util.logging.Log4j
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException

/**
 * Created by ivar on 12.11.15.
 */
@Log4j
class IdCardAuthenticationProvider implements AuthenticationProvider {
    @Override
    Authentication authenticate(Authentication auth) throws AuthenticationException {

        log.info 'try authenticate'

        IdCardAuthenticationToken authentication = (IdCardAuthenticationToken) auth

        //String questionId = authentication.questionId
        //String guess = authentication.credentials

        try {

            //mobileIdAuthenticationService.checkGuess(questionId, guess)
            throw new MobileIdAuthenticationException('test')

        } catch (AuthenticationException authenticationException) {

            throw authenticationException
        }

        authentication.setAuthenticated(true)
        return authentication
    }

    @Override
    boolean supports(Class<? extends Object> authentication) {
        log.info authentication.getClass().getName()
        return IdCardAuthenticationToken.class.isAssignableFrom(authentication)
    }
}
