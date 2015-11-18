import ee.bitweb.grails.springsecurity.estonianid.IdCardAuthenticationHandler
import ee.bitweb.grails.springsecurity.estonianid.MobileIdAuthenticationHandler
import ee.bitweb.grails.springsecurity.estonianid.MobileIdAuthenticationFilter
import ee.bitweb.grails.springsecurity.estonianid.IdCardAuthenticationService
import ee.bitweb.grails.springsecurity.estonianid.MobileIdAuthenticationService
import ee.bitweb.grails.springsecurity.estonianid.EstonianIdUserDetailsService
import grails.plugin.springsecurity.SecurityFilterPosition
import grails.plugin.springsecurity.SpringSecurityUtils

import ee.bitweb.grails.springsecurity.estonianid.IdCardAuthenticationFilter
import ee.bitweb.grails.springsecurity.estonianid.MobileIdAuthenticationFilter
import ee.bitweb.grails.springsecurity.estonianid.IdCardAuthenticationProvider
import ee.bitweb.grails.springsecurity.estonianid.MobileIdAuthenticationProvider
import ee.bitweb.grails.springsecurity.estonianid.DefaultEstonianIdAuthenticationDao

import groovy.util.logging.Log4j

@Log4j
class SpringSecurityEstonianIdGrailsPlugin {
    // the plugin version
    def version = "0.1"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.5 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]

    // TODO Fill in these fields
    def title = "Spring Security Estonian Id Plugin" // Headline display name of the plugin
    def author = "Your name"
    def authorEmail = ""
    def description = '''\
Brief summary/description of the plugin.
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/spring-security-estonian-id"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
//    def license = "APACHE"

    // Details of company behind the plugin (if there is one)
//    def organization = [ name: "My Company", url: "http://www.my-company.com/" ]

    // Any additional developers beyond the author specified above.
//    def developers = [ [ name: "Joe Bloggs", email: "joe@bloggs.net" ]]

    // Location of the plugin's issue tracker.
//    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

    // Online location of the plugin's browseable source code.
    def scm = [ url: "https://github.com/BitWeb/grails-spring-security-estonian-id" ]

    def loadAfter = ['springSecurityCore']

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before
    }

    def doWithSpring = {
        def conf = SpringSecurityUtils.securityConfig
        if (!conf) {
            println 'ERROR: There is no Spring Security configuration'
            println 'ERROR: Stop configuring Spring Security Estonian Id'
            return
        }

        println 'Configuring Spring Security EstonianId ...'
        SpringSecurityUtils.loadSecondaryConfig 'DefaultEstonianIdSecurityConfig'
        // have to get again after overlaying DefaultEstonianIdSecurityConfig
        conf = SpringSecurityUtils.securityConfig

        estonianIdUserDetailsService(EstonianIdUserDetailsService) {

        }

        String estonianIdDaoName = conf?.estonianId?.dao ?: null

        if (estonianIdDaoName == null) {
            estonianIdDaoName = 'estonianIdAuthenticationDao'
            estonianIdAuthenticationDao(DefaultEstonianIdAuthenticationDao) {
                estonianIdUserClassName = conf.estonianId.domain.estonianIdUserClassName

                estonainIdAppUserConnectionPropertyName = conf.estonianId.domain.estonainIdAppUserConnectionPropertyName

                estonainIdUserIdCodeProperty = conf.estonianId.domain.estonainIdUserIdCodeProperty
                estonainIdUserGivennameProperty = conf.estonianId.domain.estonainIdUserGivennameProperty
                estonainIdUserSurnameProperty = conf.estonianId.domain.estonainIdUserSurnameProperty
                estonainIdUserScreenNameProperty = conf.estonianId.domain.estonainIdUserScreenNameProperty

                appUserClassName = conf.userLookup.userDomainClassName

                rolesPropertyName = conf.userLookup.authoritiesPropertyName

                coreUserDetailsService = ref('userDetailsService')
                estonianIdUserDetailsService = ref('estonianIdUserDetailsService')
                grailsApplication = ref('grailsApplication')

                defaultRoleNames = conf.estonianId.domain.defaultRoleNames
            }
        }

        estonianIdCardAuthenticationService(IdCardAuthenticationService) {
            digiDocServiceUrl = conf.estonianId.digiDocServiceUrl
        }

        estonianMobileIdAuthenticationService(MobileIdAuthenticationService) {
            digiDocServiceUrl = conf.estonianId.digiDocServiceUrl
            appServiceName = conf.estonianId.digiDocServiceAppServiceName
        }

        estonianIdCardAuthenticationHandler(IdCardAuthenticationHandler) {

        }

        estonianMobileIdAuthenticationHandler(MobileIdAuthenticationHandler) {

        }

        estonianIdCardAuthenticationProvider(IdCardAuthenticationProvider) {
            authenticationService = ref('estonianIdCardAuthenticationService')
            authenticationDao = ref(estonianIdDaoName)
            defaultRoleNames = conf.estonianId.domain.defaultRoleNames
            fCreateNewUsers = conf.estonianId.domain.fCreateNewUsers
        }
        estonianMobileIdAuthenticationProvider(MobileIdAuthenticationProvider) {
            authenticationService = ref('estonianMobileIdAuthenticationService')
            authenticationDao = ref(estonianIdDaoName)
            defaultRoleNames = conf.estonianId.domain.defaultRoleNames
            fCreateNewUsers = conf.estonianId.domain.fCreateNewUsers
        }

        estonianIdCardAuthenticationFilter(IdCardAuthenticationFilter) {
            filterProcessesUrl = conf.estonianId.filter.idCardLogin.processUrl
            authenticationSuccessHandler = ref('estonianIdCardAuthenticationHandler')
            authenticationFailureHandler = ref('estonianIdCardAuthenticationHandler')
            authenticationManager = ref('authenticationManager')
            sessionAuthenticationStrategy = ref('sessionAuthenticationStrategy')
        }

        estonianMobileIdAuthenticationFilter(MobileIdAuthenticationFilter) {
            filterProcessesUrl = conf.estonianId.filter.mobileIdLogin.processUrl
            authenticationSuccessHandler = ref('estonianMobileIdAuthenticationHandler')
            authenticationFailureHandler = ref('estonianMobileIdAuthenticationHandler')
            authenticationManager = ref('authenticationManager')
            sessionAuthenticationStrategy = ref('sessionAuthenticationStrategy')
            localeResolver = ref('localeResolver')
            defaultLanguageCode = conf.estonianId.mobileIdLang.defaultLanguageCode
            localeToLangMap = conf.estonianId.mobileIdLang.localeToLangMap
        }

        SpringSecurityUtils.registerProvider('estonianIdCardAuthenticationProvider')
        SpringSecurityUtils.registerProvider('estonianMobileIdAuthenticationProvider')
        SpringSecurityUtils.registerFilter('estonianIdCardAuthenticationFilter', SecurityFilterPosition.SECURITY_CONTEXT_FILTER.order + 10)
        SpringSecurityUtils.registerFilter('estonianMobileIdAuthenticationFilter', SecurityFilterPosition.SECURITY_CONTEXT_FILTER.order + 11)

        println '... finished configuring Spring Security EstonianId'
    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { ctx ->
        // TODO Implement post initialization spring config (optional)
    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    def onShutdown = { event ->
        // TODO Implement code that is executed when the application shuts down (optional)
    }
}
