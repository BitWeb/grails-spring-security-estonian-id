package ee.bitweb.grails.springsecurity.estonianid

/**
 * Created by ivar on 12.11.15.
 */
class MobileIdAuthenticationSession {
    String sesscode
    String userIdCode

    Date timeStarted
    Date timePolled

    String status = ''
    Integer errorCode = 0

    String getStatus() {
        return status
    }

    Integer getError() {
        return errorCode
    }
}