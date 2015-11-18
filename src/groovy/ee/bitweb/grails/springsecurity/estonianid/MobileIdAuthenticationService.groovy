package ee.bitweb.grails.springsecurity.estonianid

import groovy.util.logging.Log4j
import wslite.soap.SOAPClient
import wslite.soap.SOAPClientException
import wslite.soap.SOAPFaultException

/**
 * Created by ivar on 12.11.15.
 */
@Log4j
class MobileIdAuthenticationService {
    String appServiceName
    String digiDocServiceUrl

    private static final DIGIDOCSERVICE_WSDL_URL = 'http://www.sk.ee/DigiDocService/DigiDocService_2_3.wsdl'

    MobileIdAuthenticationSession beginAuthentication(String phoneNo, String languageCode) {
        MobileIdAuthenticationSession authSession = new MobileIdAuthenticationSession()

        def client = new SOAPClient(digiDocServiceUrl)
        client.httpClient.sslTrustAllCerts = true

        List supportedLanguageCodes = ['EST', 'ENG', 'RUS', 'LIT']
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

                Date timeNow = new Date()
                authSession.timeStarted = timeNow
                authSession.timePolled = timeNow

                authSession.status = 'OK'
            } else {
                log.warn "MobileAuthenticate returned an invalid. status: " + response.MobileAuthenticateResponse.Status.text()
                authSession.errorCode = -1
            }
        } catch (SOAPFaultException sfe) {
            authSession.errorCode = getSoapErrorCode(sfe.message)
        } catch (SOAPClientException sce) {
            log.warn "Unknown SOAPClientException: \n"+sce.printStackTrace()
            authSession.errorCode = -1
        }

        return authSession
    }

    MobileIdAuthenticationSession poll(MobileIdAuthenticationSession authSession) {
        if (isSessionValidForPolling(authSession)) {

            Date timeNow = new Date()
            if((timeNow.getTime() - authSession.timeStarted.getTime()) / 1000 > 240) {

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
                if((timeNow.getTime() - timePolled.getTime()) / 1000 < secondsToWait) {

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

                        switch(returnedStatus) {
                            case 'OUTSTANDING_TRANSACTION':
                                break;
                            case 'USER_AUTHENTICATED':
                                break;
                            case 'NOT_VALID':
                                break;
                            case 'EXPIRED_TRANSACTION':
                                break;
                            case 'USER_CANCEL':
                                break;
                            case 'MID_NOT_READY':
                                break;
                            case 'PHONE_ABSENT':
                                break;
                            case 'SENDING_ERROR':
                                break;
                            case 'SIM_ERROR':
                                break;
                            case 'INTERNAL_ERROR':
                                break;
                            default:
                                log.warn "Unknown Status returned from GetMobileAuthenticate. Returned Status: "+returnedStatus
                                break;
                        }
                    } catch (SOAPFaultException sfe) {
                        authSession.errorCode = getSoapErrorCode(sfe.message)
                    } catch (SOAPClientException sce) {
                        log.warn "Unknown SOAPClientException: \n"+sce.printStackTrace()
                        authSession.errorCode = -1
                    }
                }
            }
        } else {
            log.warn "MobileIdAuthService::poll : Trying to poll an invalid MobileIdAuthSession"
        }

        return authSession
    }

    public boolean isSessionValidForPolling(MobileIdAuthenticationSession authSession) {
        if (authSession && authSession.errorCode == 0 && (authSession.status == 'OK' || authSession.status == 'OUTSTANDING_TRANSACTION')) {
            return true
        }
        return false
    }

    public boolean isSessionAuthenticated(MobileIdAuthenticationSession authSession) {
        if(authSession.status == 'USER_AUTHENTICATED') {
            return true
        }

        return false
    }

    protected static String generateChallenge() {
        Random r = new Random();
        StringBuffer sb = new StringBuffer();
        while(sb.length() < 20) {
            sb.append(Integer.toHexString(r.nextInt()));
        }

        return sb.toString().substring(0, 20);
    }

    protected static Integer getSoapErrorCode(String message) {
        Integer errorCode = -1
        switch(message) {
            case 'SOAP-ENV:Client - 100':
                errorCode = 100
                break;
            case 'SOAP-ENV:Client - 101':
                errorCode = 101
                break;
            case 'SOAP-ENV:Client - 102':
                errorCode = 102
                break;
            case 'SOAP-ENV:Client - 103':
                errorCode = 103
                break;
            case 'SOAP-ENV:Client - 200':
                errorCode = 200
                break;
            case 'SOAP-ENV:Client - 201':
                errorCode = 201
                break;
            case 'SOAP-ENV:Client - 202':
                errorCode = 202
                break;
            case 'SOAP-ENV:Client - 203':
                errorCode = 203
                break;
            case 'SOAP-ENV:Client - 300':
                errorCode = 300
                break;
            case 'SOAP-ENV:Client - 301':
                errorCode = 301
                break;
            case 'SOAP-ENV:Client - 302':
                errorCode = 302
                break;
            case 'SOAP-ENV:Client - 303':
                errorCode = 303
                break;
            case 'SOAP-ENV:Client - 304':
                errorCode = 304
                break;
            case 'SOAP-ENV:Client - 305':
                errorCode = 305
                break;
            case 'SOAP-ENV:Client - 413':
                errorCode = 413
                break;
            case 'SOAP-ENV:Client - 503':
                errorCode = 503
                break;
            default: break;
        }
        return errorCode
    }
}