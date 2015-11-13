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

    MobileIdAuthenticationToken token

    public MobileIdAuthenticationException(String msg, MobileIdAuthenticationToken token, Throwable t) {
        super(msg, t)
        this.token = token
    }

    public MobileIdAuthenticationException(String msg, MobileIdAuthenticationToken token) {
        super(msg)
        this.token = token
    }
}