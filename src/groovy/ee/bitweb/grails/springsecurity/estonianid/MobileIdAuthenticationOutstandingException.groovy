package ee.bitweb.grails.springsecurity.estonianid

/**
 * Created by Ivar on 13.11.2015.
 */
class MobileIdAuthenticationOutstandingException extends MobileIdAuthenticationException {
    public MobileIdAuthenticationOutstandingException(String msg, MobileIdAuthenticationToken token, Throwable t) {
        super(msg, token, t)
    }

    public MobileIdAuthenticationOutstandingException(String msg, MobileIdAuthenticationToken token) {
        super(msg, token)
    }
}
