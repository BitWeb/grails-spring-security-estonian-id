package ee.bitweb.grails.springsecurity.estonianid

import groovy.util.logging.Log4j
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UserDetails

/**
 * Created by ivar on 12.11.15.
 */
@Log4j
class IdCardAuthenticationProvider implements AuthenticationProvider {

    IdCardAuthenticationService authenticationService
    EstonianIdAuthenticationDao authenticationDao

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

        Object estonianIdUser = authenticationDao.findUser(token)

        if (estonianIdUser) {
            authenticationDao.updateIfNeeded(estonianIdUser, token)

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
        } else {

        }

        return token
    }

    @Override
    boolean supports(Class<? extends Object> authentication) {
        log.info authentication.getClass().getName()
        return IdCardAuthenticationToken.class.isAssignableFrom(authentication)
    }
}
