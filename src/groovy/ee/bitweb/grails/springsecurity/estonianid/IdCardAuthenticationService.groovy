package ee.bitweb.grails.springsecurity.estonianid

import groovy.util.logging.Slf4j
import wslite.soap.SOAPClient
import wslite.soap.SOAPClientException
import wslite.soap.SOAPFaultException

import java.security.cert.X509Certificate

import javax.xml.bind.DatatypeConverter

/**
 * @author Ivar
 */
@Slf4j
class IdCardAuthenticationService {

    String digiDocServiceUrl

    private static final String DIGIDOCSERVICE_WSDL_URL = 'http://www.sk.ee/DigiDocService/DigiDocService_2_3.wsdl'

    String checkCertificate(X509Certificate certificate) {
        def client = new SOAPClient(digiDocServiceUrl)
        client.httpClient.sslTrustAllCerts = true

        try {
            def response = client.send() {
                body {
                    CheckCertificate(xmlns: DIGIDOCSERVICE_WSDL_URL) {
                        Certificate(DatatypeConverter.printBase64Binary(certificate.encoded))
                    }
                }
            }

            if (response.CheckCertificateResponse.Status.text() == 'GOOD') {
                log.debug(response.CheckCertificateResponse.Status.text())
                return response.CheckCertificateResponse.UserIDCode.text()
            } else {
                log.debug(response.CheckCertificateResponse.Status.text())
            }
        } catch (SOAPFaultException e) {
            log.debug(e)
        } catch (SOAPClientException e) {
            log.debug(e)
        }
    }
}
