package ee.bitweb.grails.springsecurity.estonianid

import org.springframework.security.core.AuthenticationException

/**
 * @author ivar
 */
class MobileIdAuthenticationException extends AuthenticationException {
    Date timeStarted
    Date timePolled

    String status = ''
    Integer errorCode = 0

    MobileIdAuthenticationToken token

    MobileIdAuthenticationException(String msg, MobileIdAuthenticationToken token, Throwable t) {
        super(msg, t)
        this.token = token
    }

    MobileIdAuthenticationException(String msg, MobileIdAuthenticationToken token) {
        this(msg, token, null)
    }
}