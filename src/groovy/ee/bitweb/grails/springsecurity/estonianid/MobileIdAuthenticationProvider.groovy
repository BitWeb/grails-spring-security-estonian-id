package ee.bitweb.grails.springsecurity.estonianid

import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.InsufficientAuthenticationException

import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException

import org.springframework.security.core.userdetails.UserDetails

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

        MobileIdAuthenticationToken authentication = (MobileIdAuthenticationToken) auth

        if (!authentication.authSession) {
            authentication.authSession = authenticationService.beginAuthentication(authentication.userPhoneNo, 'EST')
            if (!authenticationService.isSessionAuthenticated(authentication.authSession)) {
                if (authenticationService.isSessionValidForPolling(authentication.authSession)) {
                    throw new MobileIdAuthenticationException('authentication incomplete', authentication)
                } else {
                    throw new MobileIdAuthenticationException('authentication failed', authentication)
                }
            }
        } else {
            if (!authenticationService.isSessionAuthenticated(authentication.authSession)) {
                if (authenticationService.isSessionValidForPolling(authentication.authSession)) {
                    authenticationService.poll(authentication.authSession)
                    if (!authenticationService.isSessionAuthenticated(authentication.authSession)) {
                        if (authenticationService.isSessionValidForPolling(authentication.authSession)) {
                            throw new MobileIdAuthenticationException('authentication incomplete', authentication)
                        } else {
                            throw new MobileIdAuthenticationException('authentication failed', authentication)
                        }
                    }
                } else {
                    throw new MobileIdAuthenticationException('authentication failed', authentication)
                }
            }
        }

        authentication.setAuthenticated(true)

        Object user = authenticationDao.findUser(authentication)

        if (user) {
            authenticationDao.updateIfNeeded(user, authentication)
        }

        Object appUser = authenticationDao.getAppUser(user)
        Object principal = authenticationDao.getPrincipal(appUser)

        authentication.details = null
        authentication.principal = principal

        if (UserDetails.isAssignableFrom(principal.class)) {
            authentication.authorities = ((UserDetails)principal).getAuthorities()
        } else {
            authentication.authorities = authenticationDao.getRoles(appUser)
        }

        return authentication
    }

    @Override
    boolean supports(Class<? extends Object> authentication) {
        log.info authentication.getClass().getName()
        return MobileIdAuthenticationToken.class.isAssignableFrom(authentication)
    }
}
