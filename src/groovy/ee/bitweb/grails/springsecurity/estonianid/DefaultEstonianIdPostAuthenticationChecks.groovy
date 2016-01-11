package ee.bitweb.grails.springsecurity.estonianid

import ee.bitweb.grails.springsecurity.userdetails.GenericUserDetails
import ee.bitweb.grails.springsecurity.userdetails.GenericUserDetailsChecker
import groovy.util.logging.Slf4j

/**
 * @author ivar
 */
@Slf4j
class DefaultEstonianIdPostAuthenticationChecks implements GenericUserDetailsChecker {
    void check(GenericUserDetails user) {
        log.debug('........................post authentication checks')
        /*if(!user.credentialsNonExpired) {
            AbstractUserDetailsAuthenticationProvider.this.logger.debug("User account credentials have expired")
            throw new CredentialsExpiredException(AbstractUserDetailsAuthenticationProvider.this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.credentialsExpired", "User credentials have expired"), user)
        }*/
    }
}
