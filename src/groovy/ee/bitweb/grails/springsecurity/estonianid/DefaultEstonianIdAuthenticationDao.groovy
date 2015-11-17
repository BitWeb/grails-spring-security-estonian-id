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
    EstonianIdUserDetailsService estonianIdUserDetailsService

    Class EstonianIdUserClass
    Class AppUserClass
    String estonianIdUserClassName
    String appUserClassName

    String estonainIdAppUserConnectionPropertyName

    String estonainIdUserIdCodeProperty
    String estonainIdUserGivennameProperty
    String estonainIdUserSurnameProperty
    String estonainIdUserScreenNameProperty

    String rolesPropertyName

    List<String> defaultRoleNames

    Object findUser(EstonianIdAuthenticationToken token) {
        Object user = null
        EstonianIdUserClass.withTransaction {
            user = EstonianIdUserClass.findWhere((estonainIdUserIdCodeProperty): token.userIdCode)
        }
        return user
    }

    void fillEstonianIdUserDetails(def user, EstonianIdAuthenticationToken token) {
        user.properties[estonainIdUserIdCodeProperty] = token.userIdCode
        user.properties[estonainIdUserGivennameProperty] = token.userGivenname
        user.properties[estonainIdUserSurnameProperty] = token.userSurname
        user.properties[estonainIdUserScreenNameProperty] = token.userGivenname + ' ' + token.userSurname
    }

    void fillAppUserDetails(def appUser, EstonianIdAuthenticationToken token) {
        def securityConf = SpringSecurityUtils.securityConfig

        //
    }

    Object create(EstonianIdAuthenticationToken token) {
        def securityConf = SpringSecurityUtils.securityConfig

        def estonianIdUser = null
        def appUser = null

        if (isSameDomain()) {
            estonianIdUser = grailsApplication.getDomainClass(EstonianIdUserClass.name).newInstance()
            fillEstonianIdUserDetails(estonianIdUser, token)
            fillAppUserDetails(estonianIdUser, token)

            estonianIdUser.timeCreated = new Date()
            estonianIdUser.timeUpdated = new Date()

            appUser = estonianIdUser
        } else {
            estonianIdUser = grailsApplication.getDomainClass(EstonianIdUserClass.name).newInstance()
            fillEstonianIdUserDetails(estonianIdUser, token)

            appUser = grailsApplication.getDomainClass(AppUserClass.name).newInstance()
            fillAppUserDetails(appUser, token)

            appUser.timeCreated = new Date()
            appUser.timeUpdated = new Date()

            AppUserClass.withTransaction {
                appUser.save(flush: true, failOnError: true)
            }
            estonianIdUser[estonainIdAppUserConnectionPropertyName] = appUser
        }

        EstonianIdUserClass.withTransaction {
            estonianIdUser.save(flush: true, failOnError: true)
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

        return estonianIdUser
    }

    void updateFromToken(Object estonianIdUser, EstonianIdAuthenticationToken token) {
        EstonianIdUserClass.withTransaction {
            try {
                if (!estonianIdUser.isAttached()) {
                    estonianIdUser.attach()
                }
                boolean update = false
                if (estonianIdUser.hasProperty(estonainIdUserIdCodeProperty)) {
                    if (estonianIdUser.getProperty(estonainIdUserIdCodeProperty) != token.userIdCode) {
                        update = true
                        estonianIdUser.setProperty(estonainIdUserIdCodeProperty, token.userIdCode)
                    }
                }
                if (estonianIdUser.hasProperty(estonainIdUserGivennameProperty)) {
                    if (estonianIdUser.getProperty(estonainIdUserGivennameProperty) != token.userGivenname) {
                        update = true
                        estonianIdUser.setProperty(estonainIdUserGivennameProperty, token.userGivenname)
                    }
                }
                if (estonianIdUser.hasProperty(estonainIdUserSurnameProperty)) {
                    if (estonianIdUser.getProperty(estonainIdUserSurnameProperty) != token.userSurname) {
                        update = true
                        estonianIdUser.setProperty(estonainIdUserSurnameProperty, token.userSurname)
                    }
                }
                if (estonianIdUser.hasProperty(estonainIdUserScreenNameProperty)) {
                    if (estonianIdUser.getProperty(estonainIdUserScreenNameProperty) != token.userGivenname + ' ' + token.userSurname) {
                        update = true
                        estonianIdUser.setProperty(estonainIdUserScreenNameProperty, token.userGivenname + ' ' + token.userSurname)
                    }
                }
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
            result = user.getProperty(estonainIdAppUserConnectionPropertyName)
        }
        return result
    }

    Object getPrincipal(Object appUser) {
        if (estonianIdUserDetailsService) {
            return estonianIdUserDetailsService.createUserDetails(appUser, getRoles(appUser))
        } else {
            if (coreUserDetailsService) {
                return coreUserDetailsService.createUserDetails(appUser, getRoles(appUser))
            }
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