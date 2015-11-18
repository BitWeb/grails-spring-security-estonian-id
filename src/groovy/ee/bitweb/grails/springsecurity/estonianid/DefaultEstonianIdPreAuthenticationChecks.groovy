package ee.bitweb.grails.springsecurity.estonianid

import ee.bitweb.grails.springsecurity.userdetails.GenericUserDetails
import ee.bitweb.grails.springsecurity.userdetails.GenericUserDetailsChecker

import groovy.util.logging.Log4j

/**
 * Created by ivar on 18.11.15.
 */
@Log4j
class DefaultEstonianIdPreAuthenticationChecks implements GenericUserDetailsChecker {
    private DefaultEstonianIdPreAuthenticationChecks() {
    }

    public void check(GenericUserDetails user) {
        /*if(!user.isAccountNonLocked()) {
            AbstractUserDetailsAuthenticationProvider.this.logger.debug("User account is locked");
            throw new LockedException(AbstractUserDetailsAuthenticationProvider.this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.locked", "User account is locked"), user);
        } else if(!user.isEnabled()) {
            AbstractUserDetailsAuthenticationProvider.this.logger.debug("User account is disabled");
            throw new DisabledException(AbstractUserDetailsAuthenticationProvider.this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.disabled", "User is disabled"), user);
        } else if(!user.isAccountNonExpired()) {
            AbstractUserDetailsAuthenticationProvider.this.logger.debug("User account is expired");
            throw new AccountExpiredException(AbstractUserDetailsAuthenticationProvider.this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.expired", "User account has expired"), user);
        }*/
    }
}
