package ee.bitweb.grails.springsecurity.estonianid

import grails.plugin.springsecurity.SpringSecurityUtils
import groovy.util.logging.Slf4j
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.plugins.support.aware.GrailsApplicationAware
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

import java.lang.reflect.Method

/**
 * @author ivar
 */
@Slf4j
class DefaultEstonianIdAuthenticationDao implements EstonianIdAuthenticationDao, InitializingBean, ApplicationContextAware, GrailsApplicationAware {

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

    def findUser(EstonianIdAuthenticationToken token) {
        EstonianIdUserClass.withTransaction {
            return EstonianIdUserClass.findWhere((estonainIdUserIdCodeProperty): token.userIdCode)
        }
    }

    void fillEstonianIdUserDetails(user, EstonianIdAuthenticationToken token) {
        if (user.hasProperty(estonainIdUserIdCodeProperty)) {
            user.properties[estonainIdUserIdCodeProperty] = token.userIdCode
        }
        if (user.hasProperty(estonainIdUserGivennameProperty)) {
            user.properties[estonainIdUserGivennameProperty] = token.userGivenname
        }
        if (user.hasProperty(estonainIdUserSurnameProperty)) {
            user.properties[estonainIdUserSurnameProperty] = token.userSurname
        }
        if (user.hasProperty(estonainIdUserScreenNameProperty)) {
            user.properties[estonainIdUserScreenNameProperty] = token.userGivenname + ' ' + token.userSurname
        }
    }

    void fillAppUserDetails(appUser, EstonianIdAuthenticationToken token) {
        def securityConf = SpringSecurityUtils.securityConfig

        //
    }

    def create(EstonianIdAuthenticationToken token) {
        def securityConf = SpringSecurityUtils.securityConfig

        def estonianIdUser
        def appUser

        if (isSameDomain()) {
            estonianIdUser = grailsApplication.getDomainClass(EstonianIdUserClass.name).newInstance()
            fillEstonianIdUserDetails(estonianIdUser, token)
            fillAppUserDetails(estonianIdUser, token)

            if (estonianIdUser.hasProperty('timeCreated') && estonianIdUser.hasProperty('timeUpdated')) {
                estonianIdUser.timeCreated = estonianIdUser.timeUpdated = new Date()
            }

            appUser = estonianIdUser
        } else {
            estonianIdUser = grailsApplication.getDomainClass(EstonianIdUserClass.name).newInstance()
            fillEstonianIdUserDetails(estonianIdUser, token)

            appUser = grailsApplication.getDomainClass(AppUserClass.name).newInstance()
            fillAppUserDetails(appUser, token)

            if (appUser.hasProperty('timeCreated') && appUser.hasProperty('timeUpdated')) {
                appUser.timeCreated = appUser.timeUpdated = new Date()
            }

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

    void updateFromToken(estonianIdUser, EstonianIdAuthenticationToken token) {
        EstonianIdUserClass.withTransaction {
            try {
                if (!estonianIdUser.attached) {
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

    def getAppUser(user) {
        if (EstonianIdUserClass == AppUserClass) {
            return user
        }
        def result
        AppUserClass.withTransaction {
            if (!user.attached) {
                user.attach()
            }
            result = user.getProperty(estonainIdAppUserConnectionPropertyName)
        }
        return result
    }

    def getPrincipal(appUser) {
        if (estonianIdUserDetailsService) {
            return estonianIdUserDetailsService.createUserDetails(appUser, getRoles(appUser))
        } else {
            if (coreUserDetailsService) {
                return coreUserDetailsService.createUserDetails(appUser, getRoles(appUser))
            }
        }
        return appUser
    }

    Collection<GrantedAuthority> getRoles(user) {
        if (user instanceof UserDetails) {
            return user.authorities
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
                return new SimpleGrantedAuthority(it.toString())
            } else {
                new SimpleGrantedAuthority(it.getProperty(conf.authority.nameField))
            }
        }
    }

    boolean isSameDomain() {
        return AppUserClass == EstonianIdUserClass
    }

    void afterPropertiesSet() {
        log.debug("Init default EstonianId Authentication Dao...")

        if (coreUserDetailsService) {
            Method m = coreUserDetailsService.getClass().declaredMethods.find { it.name == 'createUserDetails' }
            if (!m) {
                log.warn("UserDetailsService from spring-security-core don't have method 'createUserDetails()'. Class: ${coreUserDetailsService.getClass()}")
                coreUserDetailsService = null
            } else {
                m.accessible = true
            }
        } else {
            log.warn("No UserDetailsService bean from spring-security-core")
        }

        if (!EstonianIdUserClass) {
            EstonianIdUserClass = grailsApplication.getDomainClass(estonianIdUserClassName)?.clazz
            if (!EstonianIdUserClass) {
                log.error("Can't find domain: $estonianIdUserClassName")
            }
        }
        if (!AppUserClass) {
            if (appUserClassName && appUserClassName.length() > 0) {
                AppUserClass = grailsApplication.getDomainClass(appUserClassName)?.clazz
            }
            if (!AppUserClass) {
                log.error("Can't find domain: $appUserClassName")
            }
        }
        if (!EstonianIdUserClass && !AppUserClass) {
            log.info("Use $AppUserClass to store EstonianId Authentication")
            EstonianIdUserClass = AppUserClass
        } else if (EstonianIdUserClass && !AppUserClass) {
            AppUserClass = EstonianIdUserClass
        }
        log.debug("EstonianId Authentication Dao is ready.")
    }
}
