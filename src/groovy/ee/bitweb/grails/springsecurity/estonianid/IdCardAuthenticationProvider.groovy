package ee.bitweb.grails.springsecurity.estonianid

import groovy.util.logging.Slf4j
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import sun.security.x509.X500Name

import java.security.Principal

/**
 * @author ivar
 */
@Slf4j
class IdCardAuthenticationProvider implements AuthenticationProvider {

    IdCardAuthenticationService authenticationService
    EstonianIdAuthenticationDao authenticationDao
    List<String> defaultRoleNames
    boolean fCreateNewUsers

    Authentication authenticate(Authentication authentication) throws AuthenticationException {

        log.info 'try authenticate'

        IdCardAuthenticationToken token = authentication

        if (!token.userCert) {
            throw new IdCardAuthenticationException('Bad certificate', token)
        }

        String userIdCode = authenticationService.checkCertificate(token.userCert)
        if(!userIdCode) {
            throw new IdCardAuthenticationException('Bad certificate', token)
        }

        token.authenticated = true
        token.userIdCode = userIdCode

        Principal certPrincipal = token.userCert.subjectDN
        if (certPrincipal instanceof X500Name) {
            token.userGivenname = certPrincipal.givenName
            token.userSurname = certPrincipal.surname
        }

        def estonianIdUser = authenticationDao.findUser(token)

        boolean fJustCreated = false

        if (!estonianIdUser) {
            if (fCreateNewUsers) {
                estonianIdUser = authenticationDao.create(token)
                fJustCreated = true
            }
        }

        if (estonianIdUser) {
            if (!fJustCreated) {
                authenticationDao.updateFromToken(estonianIdUser, token)
            }

            def appUser = authenticationDao.getAppUser(estonianIdUser)
            def principal = authenticationDao.getPrincipal(appUser)

            token.details = null
            token.principal = principal

            if (principal instanceof EstonianIdUserDetails) {
                token = new IdCardAuthenticationToken(principal.authorities, token.userCert)
                token.userIdCode = token.userCert.serialNumber
            } else {
                token = new IdCardAuthenticationToken(authenticationDao.getRoles(appUser), token.userCert)
                token.userIdCode = token.userCert.serialNumber
            }

            token.authenticated = true
            token.details = null
            token.principal = principal
        } else {
            /*token = new MobileIdAuthenticationToken([new SimpleGrantedAuthority(defaultRoleName)], token.userPhoneNo, token.authSession)
            token.userIdCode = token.authSession.userIdCode*/
        }

        return token
    }

    boolean supports(Class<?> authentication) {
        IdCardAuthenticationToken.isAssignableFrom(authentication)
    }
}
