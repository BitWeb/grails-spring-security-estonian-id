package ee.bitweb.grails.springsecurity.estonianid

import org.apache.log4j.Logger
import org.codehaus.groovy.grails.commons.GrailsApplication
import grails.plugin.springsecurity.SpringSecurityUtils
import org.codehaus.groovy.grails.plugins.support.aware.GrailsApplicationAware
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.GrantedAuthorityImpl
import org.springframework.security.core.userdetails.UserDetails

import java.lang.reflect.Method

/**
 * Created by ivar on 12.11.15.
 */
class DefaultEstonianIdAuthenticationDao implements EstonianIdAuthenticationDao, InitializingBean, ApplicationContextAware, GrailsApplicationAware {

    private static def log = Logger.getLogger(this)

    GrailsApplication grailsApplication
    ApplicationContext applicationContext
    def coreUserDetailsService

    Class EstonianIdUserClass
    Class AppUserClass
    String estonianIdUserClassName
    String appUserClassName

    String userEstonianIdCodeProperty
    String usernameProperty = "username"
    String rolesPropertyName

    List<String> defaultRoleNames = ['ROLE_USER', 'ROLE_ESTONIAN_ID']

    String appUserConnectionPropertyName = "user"

    Object findUser(EstonianIdAuthenticationToken token) {
        Object user = null
        EstonianIdUserClass.withTransaction {
            user = EstonianIdUserClass.findWhere((userEstonianIdCodeProperty): token.userIdCode)
        }
        return user
    }

    void fillEstonianIdUserDetails(def user, EstonianIdAuthenticationToken token) {
        user.properties[userEstonianIdCodeProperty] = token.userIdCode
        /*if (usernameProperty && user.hasProperty(usernameProperty)) {
            user.setProperty(usernameProperty, token.screenName)
        }*/
    }

    void fillAppUserDetails(def appUser, EstonianIdAuthenticationToken token) {
        def securityConf = SpringSecurityUtils.securityConfig

        String username
        username = token.userName

        appUser.setProperty(securityConf.userLookup.usernamePropertyName, username)
        //appUser.setProperty(securityConf.userLookup.passwordPropertyName, null)
        appUser.setProperty(securityConf.userLookup.enabledPropertyName, true)
        appUser.setProperty(securityConf.userLookup.accountExpiredPropertyName, false)
        appUser.setProperty(securityConf.userLookup.accountLockedPropertyName, false)
        appUser.setProperty(securityConf.userLookup.passwordExpiredPropertyName, false)
    }

    Object create(EstonianIdAuthenticationToken token) {
        def securityConf = SpringSecurityUtils.securityConfig

        def user = null
        def appUser = null

        if (isSameDomain()) {
            user = grailsApplication.getDomainClass(EstonianIdUserClass.name).newInstance()
            fillEstonianIdUserDetails(user, token)
            fillAppUserDetails(user, token)
        } else {
            user = grailsApplication.getDomainClass(EstonianIdUserClass.name).newInstance()
            fillEstonianIdUserDetails(user, token)

            appUser = grailsApplication.getDomainClass(AppUserClass.name).newInstance()
            fillAppUserDetails(appUser, token)

            AppUserClass.withTransaction {
                appUser.save(flush: true, failOnError: true)
            }
            user[appUserConnectionPropertyName] = appUser
        }

        EstonianIdUserClass.withTransaction {
            user.save()
        }

        Class<?> PersonRole = grailsApplication.getDomainClass(securityConf.userLookup.authorityJoinClassName).clazz
        Class<?> Authority = grailsApplication.getDomainClass(securityConf.authority.className).clazz
        PersonRole.withTransaction { status ->
            defaultRoleNames.each { String roleName ->
                String findByField = securityConf.authority.nameField[0].toUpperCase() + securityConf.authority.nameField.substring(1)
                def auth = Authority."findBy${findByField}"(roleName)
                if (auth) {
                    PersonRole.create(appUser, auth)
                } else {
                    log.error("Can't find authority for name '$roleName'")
                }
            }
        }

        return user
    }

    void updateIfNeeded(Object estonianIdUser, EstonianIdAuthenticationToken token) {
        EstonianIdUserClass.withTransaction {
            try {
                if (!estonianIdUser.isAttached()) {
                    estonianIdUser.attach()
                }
                boolean update = false
                /*if (estonianIdUser.hasProperty(usernameProperty)) {
                    if (estonianIdUser.getProperty(usernameProperty) != token.screenName) {
                        update = true
                        estonianIdUser.setProperty(usernameProperty, token.screenName)
                    }
                }*/
                if (update) {
                    estonianIdUser.save()
                }
            } catch (OptimisticLockingFailureException e) {
                log.warn("Seems that user was updated in another thread (${e.message}). Skip")
            } catch (Throwable e) {
                log.error("Can't update user", e)
            }
        }
    }

    Object getAppUser(Object user) {
        if (EstonianIdUserClass == AppUserClass) {
            return user
        }
        def result = null
        AppUserClass.withTransaction {
            if (!user.isAttached()) {
                user.attach()
            }
            result = user.getProperty(appUserConnectionPropertyName)
        }
        return result
    }

    Object getPrincipal(Object appUser) {
        if (coreUserDetailsService) {
            return coreUserDetailsService.createUserDetails(appUser, getRoles(appUser))
        }
        return appUser
    }

    Collection<GrantedAuthority> getRoles(Object user) {
        if (UserDetails.isAssignableFrom(user.class)) {
            return ((UserDetails)user).getAuthorities()
        }

        def conf = SpringSecurityUtils.securityConfig
        Class<?> PersonRole = grailsApplication.getDomainClass(conf.userLookup.authorityJoinClassName)?.clazz
        if (!PersonRole) {
            log.error("Can't load roles for user $user. Reason: can't find ${conf.userLookup.authorityJoinClassName} class")
            return []
        }
        Collection roles = []
        PersonRole.withTransaction { status ->
            roles = user?.getAt(rolesPropertyName)
        }
        if (!roles) {
            roles = []
        }
        if (roles.empty) {
            return roles
        }
        return roles.collect {
            if (it instanceof String) {
                return new GrantedAuthorityImpl(it.toString())
            } else {
                new GrantedAuthorityImpl(it.getProperty(conf.authority.nameField))
            }
        }
    }

    boolean isSameDomain() {
        return AppUserClass == EstonianIdUserClass
    }

    void afterPropertiesSet() throws Exception {
        log.debug("Init default EstonianId Authentication Dao...")

        if (coreUserDetailsService != null) {
            Method m = coreUserDetailsService.class.declaredMethods.find { it.name == 'createUserDetails' }
            if (!m) {
                log.warn("UserDetailsService from spring-security-core don't have method 'createUserDetails()'. Class: ${coreUserDetailsService.getClass()}")
                coreUserDetailsService = null
            } else {
                m.setAccessible(true)
            }
        } else {
            log.warn("No UserDetailsService bean from spring-security-core")
        }

        if (EstonianIdUserClass == null) {
            EstonianIdUserClass = grailsApplication.getDomainClass(estonianIdUserClassName)?.clazz
            if (!EstonianIdUserClass) {
                log.error("Can't find domain: $estonianIdUserClassName")
            }
        }
        if (AppUserClass == null) {
            if (appUserClassName && appUserClassName.length() > 0) {
                AppUserClass = grailsApplication.getDomainClass(appUserClassName)?.clazz
            }
            if (!AppUserClass) {
                log.error("Can't find domain: $appUserClassName")
            }
        }
        if (EstonianIdUserClass == null && AppUserClass != null) {
            log.info("Use $AppUserClass to store EstonianId Authentication")
            EstonianIdUserClass = AppUserClass
        } else if (EstonianIdUserClass != null && AppUserClass == null) {
            AppUserClass = EstonianIdUserClass
        } else if(EstonianIdUserClass == null && AppUserClass == null) {

        }
        log.debug("EstonianId Authentication Dao is ready.")
    }
}