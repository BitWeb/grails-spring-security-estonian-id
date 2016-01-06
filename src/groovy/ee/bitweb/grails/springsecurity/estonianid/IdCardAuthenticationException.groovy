package ee.bitweb.grails.springsecurity.estonianid

import org.springframework.security.core.AuthenticationException

/**
 * @author ivar
 */
class IdCardAuthenticationException extends AuthenticationException {
    IdCardAuthenticationToken token

    IdCardAuthenticationException(String msg, IdCardAuthenticationToken token, Throwable t) {
        super(msg, t)
        this.token = token
    }

    IdCardAuthenticationException(String msg, IdCardAuthenticationToken token) {
        this(msg, token, null)
    }
}
