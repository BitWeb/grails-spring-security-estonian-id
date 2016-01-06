package ee.bitweb.grails.springsecurity.estonianid

/**
 * @author Ivar
 */
class MobileIdAuthenticationOutstandingException extends MobileIdAuthenticationException {
    MobileIdAuthenticationOutstandingException(String msg, MobileIdAuthenticationToken token, Throwable t) {
        super(msg, token, t)
    }

    MobileIdAuthenticationOutstandingException(String msg, MobileIdAuthenticationToken token) {
        super(msg, token)
    }
}
