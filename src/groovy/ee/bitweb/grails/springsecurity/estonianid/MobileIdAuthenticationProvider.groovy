package ee.bitweb.grails.springsecurity.estonianid

import ee.bitweb.grails.springsecurity.userdetails.GenericUserDetails
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.InsufficientAuthenticationException

import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import groovy.util.logging.Log4j
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

import ee.bitweb.grails.springsecurity.userdetails.GenericUserDetailsChecker

import org.springframework.security.authentication.dao.DaoAuthenticationProvider

/**
 * Created by ivar on 11.11.15.
 */
@Log4j
class MobileIdAuthenticationProvider implements AuthenticationProvider {

    MobileIdAuthenticationService authenticationService
    EstonianIdAuthenticationDao authenticationDao
    List<String> defaultRoleNames
    boolean fCreateNewUsers

    GenericUserDetailsChecker preAuthenticationChecks// = new DefaultEstonianIdPreAuthenticationChecks();
    GenericUserDetailsChecker postAuthenticationChecks// = new DefaultEstonianIdPostAuthenticationChecks();

    @Override
    Authentication authenticate(Authentication auth) throws AuthenticationException {
        MobileIdAuthenticationToken token = (MobileIdAuthenticationToken) auth

        //this.preAuthenticationChecks.check(estonianIdUser);

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
                token = new MobileIdAuthenticationToken(((EstonianIdUserDetails) principal).getAuthorities(), token.userPhoneNo, token.userLanguageCode, token.authSession)
                token.userIdCode = token.authSession.userIdCode
            } else {
                token = new MobileIdAuthenticationToken(authenticationDao.getRoles(appUser), token.userPhoneNo, token.userLanguageCode, token.authSession)
                token.userIdCode = token.authSession.userIdCode
            }

            token.details = null
            token.principal = principal
        } else {
            //TODO: Authentication without domain class?
        }

        //this.postAuthenticationChecks.check(estonianIdUser);

        return token
    }

    @Override
    boolean supports(Class<? extends Object> authentication) {
        return MobileIdAuthenticationToken.class.isAssignableFrom(authentication)
    }

    protected GenericUserDetails retrieveUser(Integer id, Authentication token) {
        GenericUserDetails loadedUser

        loadedUser = this.getUserDetailsService().loadById(id)

        return loadedUser
        /*try {
            loadedUser = this.getUserDetailsService().loadById(id);
        } catch (UsernameNotFoundException var6) {
            if(token.getCredentials() != null) {
                String presentedPassword = authentication.getCredentials().toString();
                this.passwordEncoder.isPasswordValid(this.userNotFoundEncodedPassword, presentedPassword, (Object)null);
            }

            throw var6;
        } catch (Exception var7) {
            throw new InternalAuthenticationServiceException(var7.getMessage(), var7);
        }

        if(loadedUser == null) {
            throw new InternalAuthenticationServiceException("UserDetailsService returned null, which is an interface contract violation");
        } else {
            return loadedUser;
        }*/
    }
}
