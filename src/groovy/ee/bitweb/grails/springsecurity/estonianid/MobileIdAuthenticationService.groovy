package ee.bitweb.grails.springsecurity.estonianid

import groovy.util.logging.Slf4j
import wslite.soap.SOAPClient
import wslite.soap.SOAPClientException
import wslite.soap.SOAPFaultException

/**
 * @author ivar
 */
@Slf4j
class MobileIdAuthenticationService {
    String appServiceName
    String digiDocServiceUrl

    private static final String DIGIDOCSERVICE_WSDL_URL = 'http://www.sk.ee/DigiDocService/DigiDocService_2_3.wsdl'
    private static final Collection<String> supportedLanguageCodes = ['EST', 'ENG', 'RUS', 'LIT']
    private static final Collection<String> VALID_STATUSES = [
        'OUTSTANDING_TRANSACTION', 'USER_AUTHENTICATED', 'NOT_VALID', 'EXPIRED_TRANSACTION',
        'USER_CANCEL', 'MID_NOT_READY', 'PHONE_ABSENT', 'SENDING_ERROR', 'SIM_ERROR', 'INTERNAL_ERROR']
    private static final Collection<Integer> ERROR_CODES = [100, 101, 102, 103, 200, 201, 202, 203, 300, 301, 302, 303, 304, 305, 413, 503]

    MobileIdAuthenticationSession beginAuthentication(String phoneNo, String languageCode) {
        MobileIdAuthenticationSession authSession = new MobileIdAuthenticationSession()

        def client = new SOAPClient(digiDocServiceUrl)
        client.httpClient.sslTrustAllCerts = true

        if(!supportedLanguageCodes.contains(languageCode)) {
            languageCode = 'EST'
        }

        String challenge = generateChallenge()

        try {
            def response = client.send() {
                body {
                    MobileAuthenticate(xmlns: DIGIDOCSERVICE_WSDL_URL) {
                        PhoneNo(phoneNo)
                        Language(languageCode)
                        ServiceName(appServiceName)
                        SPChallenge(challenge)
                        MessagingMode('asynchClientServer')
                    }
                }
            }

            if (response.MobileAuthenticateResponse.Status.text() == 'OK') {
                authSession.sesscode = response.MobileAuthenticateResponse.Sesscode
                authSession.challengeId = response.MobileAuthenticateResponse.ChallengeID
                authSession.userIdCode = response.MobileAuthenticateResponse.UserIDCode
                authSession.userGivenname = response.MobileAuthenticateResponse.UserGivenname
                authSession.userSurname = response.MobileAuthenticateResponse.UserSurname

                authSession.timeStarted = authSession.timePolled = new Date()

                authSession.status = 'OK'
            } else {
                log.warn "MobileAuthenticate returned an invalid. status: " + response.MobileAuthenticateResponse.Status.text()
                authSession.errorCode = -1
            }
        } catch (SOAPFaultException e) {
            authSession.errorCode = getSoapErrorCode(e.message)
        } catch (SOAPClientException e) {
            log.warn "Unknown SOAPClientException", e
            authSession.errorCode = -1
        }

        return authSession
    }

    MobileIdAuthenticationSession poll(MobileIdAuthenticationSession authSession) {
        if (isSessionValidForPolling(authSession)) {

            Date timeNow = new Date()
            if((timeNow.time - authSession.timeStarted.time) / 1000 > 240) {

                //authSession is > 4 minutes old. It should be expired by now
                log.warn "MobileIdAuthService::poll : Trying to use an experied or invalid MobileIdAuthSession"
                return authSession

            } else {

                Long secondsToWait = 5
                Date timePolled = authSession.timePolled
                if(authSession.status == 'OK') {
                    //It's the first poll
                    secondsToWait = 20
                }
                if((timeNow.time - timePolled.time) / 1000 < secondsToWait) {

                    //Trying to poll too soon
                    log.warn "MobileIdAuthService::poll : Trying to poll too soon"
                    return authSession

                } else {

                    def client = new SOAPClient(digiDocServiceUrl)
                    client.httpClient.sslTrustAllCerts = true

                    try {
                        def response = client.send() {
                            body {
                                GetMobileAuthenticateStatus(xmlns: DIGIDOCSERVICE_WSDL_URL) {
                                    Sesscode(authSession.sesscode)
                                    WaitSignature(0)
                                }
                            }
                        }

                        authSession.timePolled = timeNow

                        String returnedStatus = response.GetMobileAuthenticateStatusResponse.Status.text()
                        authSession.status = returnedStatus
                        if (!VALID_STATUSES.contains(returnedStatus)) {
                            log.warn "Unknown Status returned from GetMobileAuthenticate. Returned Status: $returnedStatus"
                        }
                    } catch (SOAPFaultException e) {
                        authSession.errorCode = getSoapErrorCode(e.message)
                    } catch (SOAPClientException e) {
                        log.warn "Unknown SOAPClientException", e
                        authSession.errorCode = -1
                    }
                }
            }
        } else {
            log.warn "MobileIdAuthService::poll : Trying to poll an invalid MobileIdAuthSession"
        }

        return authSession
    }

    boolean isSessionValidForPolling(MobileIdAuthenticationSession authSession) {
        authSession?.errorCode == 0 && (authSession.status == 'OK' || authSession.status == 'OUTSTANDING_TRANSACTION')
    }

    boolean isSessionAuthenticated(MobileIdAuthenticationSession authSession) {
        authSession.status == 'USER_AUTHENTICATED'
    }

    protected static String generateChallenge() {
        Random r = new Random()
        StringBuilder sb = new StringBuilder()
        while(sb.length() < 20) {
            sb.append(Integer.toHexString(r.nextInt()))
        }

        return sb.toString().substring(0, 20)
    }

    protected static int getSoapErrorCode(String message) {
        if (message?.startsWith('SOAP-ENV:Client - ')) {
            String code = message[-3..-1]
            if (code.number && ERROR_CODES.contains(code as int)) {
                return code as int
            }
        }
        -1
    }
}
