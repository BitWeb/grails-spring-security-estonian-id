package ee.bitweb.grails.springsecurity.estonianid

import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.InsufficientAuthenticationException

import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import groovy.util.logging.Log4j

/**
 * Created by ivar on 11.11.15.
 */
@Log4j
class MobileIdAuthenticationProvider implements AuthenticationProvider {

    MobileIdAuthenticationService authenticationService
    EstonianIdAuthenticationDao authenticationDao

    @Override
    Authentication authenticate(Authentication auth) throws AuthenticationException, InsufficientAuthenticationException {

        log.info 'try authenticate'

        MobileIdAuthenticationToken token = (MobileIdAuthenticationToken) auth

        if (!token.authSession) {
            token.authSession = authenticationService.beginAuthentication(token.userPhoneNo, 'EST')
            if (!authenticationService.isSessionAuthenticated(token.authSession)) {
                if (authenticationService.isSessionValidForPolling(token.authSession)) {
                    throw new MobileIdAuthenticationOutstandingException('authentication incomplete', token)
                } else {
                    throw new MobileIdAuthenticationException('authentication failed', token)
                }
            }
        } else {
            if (!authenticationService.isSessionAuthenticated(token.authSession)) {
                if (authenticationService.isSessionValidForPolling(token.authSession)) {
                    authenticationService.poll(token.authSession)
                    if (!authenticationService.isSessionAuthenticated(token.authSession)) {
                        if (authenticationService.isSessionValidForPolling(token.authSession)) {
                            throw new MobileIdAuthenticationOutstandingException('authentication incomplete', token)
                        } else {
                            throw new MobileIdAuthenticationException('authentication failed', token)
                        }
                    }
                } else {
                    throw new MobileIdAuthenticationException('authentication failed', token)
                }
            }
        }

        token.userIdCode = token.authSession.userIdCode

        token.setAuthenticated(true)

        Object estonianIdUser = authenticationDao.findUser(token)

        if (estonianIdUser) {
            authenticationDao.updateIfNeeded(estonianIdUser, token)

            Object appUser = authenticationDao.getAppUser(estonianIdUser)
            Object principal = authenticationDao.getPrincipal(appUser)

            if (EstonianIdUserDetails.isAssignableFrom(principal.class)) {
                token = new MobileIdAuthenticationToken(((EstonianIdUserDetails) principal).getAuthorities(), token.userPhoneNo, token.authSession)
                token.userIdCode = token.authSession.userIdCode
            } else {
                token = new MobileIdAuthenticationToken(authenticationDao.getRoles(appUser), token.userPhoneNo, token.authSession)
                token.userIdCode = token.authSession.userIdCode
            }

            token.details = null
            token.principal = principal
        } else {

        }

        return token
    }

    @Override
    boolean supports(Class<? extends Object> authentication) {
        log.info authentication.getClass().getName()
        return MobileIdAuthenticationToken.class.isAssignableFrom(authentication)
    }
}
