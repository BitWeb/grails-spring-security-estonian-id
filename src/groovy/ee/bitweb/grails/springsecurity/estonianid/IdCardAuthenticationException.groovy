package ee.bitweb.grails.springsecurity.estonianid

import org.springframework.security.core.AuthenticationException

/**
 * Created by ivar on 12.11.15.
 */
class IdCardAuthenticationException extends AuthenticationException {
    IdCardAuthenticationToken token

    public IdCardAuthenticationException(String msg, IdCardAuthenticationToken token, Throwable t) {
        super(msg, t);
        this.token = token
    }

    public IdCardAuthenticationException(String msg, IdCardAuthenticationToken token) {
        super(msg);
        this.token = token
    }
}
