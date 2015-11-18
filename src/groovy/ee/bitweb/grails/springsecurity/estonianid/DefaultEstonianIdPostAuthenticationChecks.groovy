package ee.bitweb.grails.springsecurity.estonianid

import ee.bitweb.grails.springsecurity.userdetails.GenericUserDetails
import ee.bitweb.grails.springsecurity.userdetails.GenericUserDetailsChecker
import groovy.util.logging.Log4j

/**
 * Created by ivar on 18.11.15.
 */
@Log4j
class DefaultEstonianIdPostAuthenticationChecks implements GenericUserDetailsChecker {
    public DefaultEstonianIdPostAuthenticationChecks() {

    }

    public void check(GenericUserDetails user) {
        log.debug('........................post authentication checks')
        /*if(!user.isCredentialsNonExpired()) {
            AbstractUserDetailsAuthenticationProvider.this.logger.debug("User account credentials have expired");
            throw new CredentialsExpiredException(AbstractUserDetailsAuthenticationProvider.this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.credentialsExpired", "User credentials have expired"), user);
        }*/
    }
}
