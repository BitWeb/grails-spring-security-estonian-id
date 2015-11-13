package ee.bitweb.grails.springsecurity.estonianid

import groovy.util.logging.Log4j
import wslite.soap.SOAPClient
import wslite.soap.SOAPClientException
import wslite.soap.SOAPFaultException

import java.security.cert.X509Certificate

/**
 * Created by Ivar on 13.11.2015.
 */
@Log4j
class IdCardAuthenticationService {

    String digiDocServiceUrl

    private static final DIGIDOCSERVICE_WSDL_URL = 'http://www.sk.ee/DigiDocService/DigiDocService_2_3.wsdl'

    boolean checkCertificate(X509Certificate certificate) {
        def client = new SOAPClient(digiDocServiceUrl)
        client.httpClient.sslTrustAllCerts = true

        try {
            def response = client.send() {
                body {
                    CheckCertificate(xmlns: DIGIDOCSERVICE_WSDL_URL) {
                        Certificate(certificate.encodeAsBase64())
                    }
                }
            }

            if (response.CheckCertificateResponse.Status.text() == 'GOOD') {
                log.debug(response.CheckCertificateResponse.Status.text())
                return true
            } else {
                log.debug(response.CheckCertificateResponse.Status.text())
                return false
            }
        } catch (SOAPFaultException sfe) {
            log.debug(sfe)
            return false
        } catch (SOAPClientException sce) {
            log.debug(sce)
            return false
        }
    }
}