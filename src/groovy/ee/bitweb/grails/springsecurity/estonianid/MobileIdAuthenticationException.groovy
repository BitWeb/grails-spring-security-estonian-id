package ee.bitweb.grails.springsecurity.estonianid

import org.springframework.security.core.AuthenticationException

/**
 * Created by ivar on 12.11.15.
 */
class MobileIdAuthenticationException extends AuthenticationException {
    Date timeStarted
    Date timePolled

    String status = ''
    Integer errorCode = 0

    MobileIdAuthenticationToken authentication

    public MobileIdAuthenticationException(String msg, MobileIdAuthenticationToken authentication, Throwable t) {
        super(msg, t);
        this.authentication = authentication
    }

    public MobileIdAuthenticationException(String msg, MobileIdAuthenticationToken authentication) {
        super(msg);
        this.authentication = authentication
    }
}