package ee.bitweb.grails.springsecurity.estonianid

import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.InsufficientAuthenticationException

import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import groovy.util.logging.Log4j
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

/**
 * Created by ivar on 11.11.15.
 */
@Log4j
class MobileIdAuthenticationProvider implements AuthenticationProvider {

    MobileIdAuthenticationService authenticationService
    EstonianIdAuthenticationDao authenticationDao
    List<String> defaultRoleNames
    boolean fCreateNewUsers

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
        token.userGivenname = token.authSession.userGivenname
        token.userSurname = token.authSession.userSurname

        token.setAuthenticated(true)

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
            /*token = new MobileIdAuthenticationToken([new SimpleGrantedAuthority(defaultRoleName)], token.userPhoneNo, token.authSession)
            token.userIdCode = token.authSession.userIdCode*/
        }

        return token
    }

    @Override
    boolean supports(Class<? extends Object> authentication) {
        log.info authentication.getClass().getName()
        return MobileIdAuthenticationToken.class.isAssignableFrom(authentication)
    }
}
