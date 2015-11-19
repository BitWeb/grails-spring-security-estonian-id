package ee.bitweb.grails.springsecurity.estonianid

import groovy.util.logging.Log4j
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import sun.security.x509.X500Name

import java.security.Principal

/**
 * Created by ivar on 12.11.15.
 */
@Log4j
class IdCardAuthenticationProvider implements AuthenticationProvider {

    IdCardAuthenticationService authenticationService
    EstonianIdAuthenticationDao authenticationDao
    List<String> defaultRoleNames
    boolean fCreateNewUsers

    @Override
    Authentication authenticate(Authentication authentication) throws AuthenticationException {

        log.info 'try authenticate'

        IdCardAuthenticationToken token = (IdCardAuthenticationToken) authentication

        try {

            if(!token.userCert || !authenticationService.checkCertificate(token.userCert)) {
                throw new IdCardAuthenticationException('Bad certificate', token)
            }

        } catch (AuthenticationException authenticationException) {

            throw authenticationException
        }

        token.setAuthenticated(true)

        token.userIdCode = token.userCert.getSerialNumber()
        Principal certPrincipal = token.userCert.getSubjectDN()
        if(X500Name.isInstance(certPrincipal)) {
            token.userGivenname = certPrincipal.getGivenName()
            token.userSurname = certPrincipal.getSurname()
        }

        Object estonianIdUser = authenticationDao.findUser(token)

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

            Object appUser = authenticationDao.getAppUser(estonianIdUser)
            Object principal = authenticationDao.getPrincipal(appUser)

            token.details = null
            token.principal = principal

            if (EstonianIdUserDetails.isAssignableFrom(principal.class)) {
                token = new IdCardAuthenticationToken(((EstonianIdUserDetails) principal).getAuthorities(), token.userCert)
                token.userIdCode = token.userCert.getSerialNumber()
            } else {
                token = new IdCardAuthenticationToken(authenticationDao.getRoles(appUser), token.userCert)
                token.userIdCode = token.userCert.getSerialNumber()
            }

            token.details = null
            token.principal = principal
        } else {
            /*token = new MobileIdAuthenticationToken([new SimpleGrantedAuthority(defaultRoleName)], token.userPhoneNo, token.authSession)
            token.userIdCode = token.authSession.userIdCode*/
        }

        return token
    }

    @Override
    boolean supports(Class<? extends Object> authentication) {
        return IdCardAuthenticationToken.class.isAssignableFrom(authentication)
    }
}
