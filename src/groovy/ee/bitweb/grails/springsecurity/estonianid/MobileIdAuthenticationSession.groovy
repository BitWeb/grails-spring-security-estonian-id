package ee.bitweb.grails.springsecurity.estonianid

/**
 * @author ivar
 */
class MobileIdAuthenticationSession {
    String sesscode
    String challengeId

    String userIdCode
    String userGivenname
    String userSurname

    Date timeStarted
    Date timePolled

    String status = ''
    Integer errorCode = 0

    Integer getError() {
        return errorCode
    }
}
