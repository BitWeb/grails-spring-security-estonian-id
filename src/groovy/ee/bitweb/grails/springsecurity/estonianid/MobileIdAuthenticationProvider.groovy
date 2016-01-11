package ee.bitweb.grails.springsecurity.estonianid

import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException

import ee.bitweb.grails.springsecurity.userdetails.GenericUserDetails
import ee.bitweb.grails.springsecurity.userdetails.GenericUserDetailsChecker
import groovy.util.logging.Slf4j

/**
 * @author ivar
 */
@Slf4j
class MobileIdAuthenticationProvider implements AuthenticationProvider {

    MobileIdAuthenticationService authenticationService
    EstonianIdAuthenticationDao authenticationDao
    List<String> defaultRoleNames
    boolean fCreateNewUsers

    GenericUserDetailsChecker preAuthenticationChecks// = new DefaultEstonianIdPreAuthenticationChecks()
    GenericUserDetailsChecker postAuthenticationChecks// = new DefaultEstonianIdPostAuthenticationChecks()

    @Override
    Authentication authenticate(Authentication auth) throws AuthenticationException {
        MobileIdAuthenticationToken token = auth

        //log.info 'Mobile Id try authenticate'
        //preAuthenticationChecks.check(estonianIdUser)

        if (!token.authSession) {
            token.authSession = authenticationService.beginAuthentication(token.userPhoneNo, token.userLanguageCode)
            if (!authenticationService.isSessionAuthenticated(token.authSession)) {
                if (authenticationService.isSessionValidForPolling(token.authSession)) {
                    throw new MobileIdAuthenticationOutstandingException('Mobile ID authentication incomplete', token)
                } else {
                    throw new MobileIdAuthenticationException('Mobile ID authentication failed', token)
                }
            }
        } else {
            if (!authenticationService.isSessionAuthenticated(token.authSession)) {
                if (authenticationService.isSessionValidForPolling(token.authSession)) {
                    authenticationService.poll(token.authSession)
                    if (!authenticationService.isSessionAuthenticated(token.authSession)) {
                        if (authenticationService.isSessionValidForPolling(token.authSession)) {
                            throw new MobileIdAuthenticationOutstandingException('Mobile ID authentication incomplete', token)
                        } else {
                            throw new MobileIdAuthenticationException('Mobile ID authentication failed', token)
                        }
                    }
                } else {
                    throw new MobileIdAuthenticationException('Mobile ID authentication failed', token)
                }
            }
        }

        token.userIdCode = token.authSession.userIdCode
        token.userGivenname = token.authSession.userGivenname
        token.userSurname = token.authSession.userSurname

        token.authenticated = true

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

            if (principal instanceof EstonianIdUserDetails) {
                token = new MobileIdAuthenticationToken(principal.authorities, token.userPhoneNo, token.userLanguageCode, token.authSession)
                token.userIdCode = token.authSession.userIdCode
            } else {
                token = new MobileIdAuthenticationToken(authenticationDao.getRoles(appUser), token.userPhoneNo, token.userLanguageCode, token.authSession)
                token.userIdCode = token.authSession.userIdCode
            }

            token.authenticated = true
            token.details = null
            token.principal = principal
        } else {
            //TODO: Authentication without domain class?
        }

        //postAuthenticationChecks.check(estonianIdUser)

        return token
    }

    @Override
    boolean supports(Class<? extends Object> authentication) {
        return MobileIdAuthenticationToken.isAssignableFrom(authentication)
    }

    protected GenericUserDetails retrieveUser(Integer id, Authentication token) {
        userDetailsService.loadById(id)
        /*try {
            loadedUser = userDetailsService.loadById(id)
        } catch (UsernameNotFoundException var6) {
            if(token.credentials != null) {
                String presentedPassword = authentication.credentials
                passwordEncoder.isPasswordValid(userNotFoundEncodedPassword, presentedPassword, (Object)null)
            }

            throw var6
        } catch (Exception var7) {
            throw new InternalAuthenticationServiceException(var7.message, var7)
        }

        if(loadedUser == null) {
            throw new InternalAuthenticationServiceException("UserDetailsService returned null, which is an interface contract violation")
        } else {
            return loadedUser
        }*/
    }
}
