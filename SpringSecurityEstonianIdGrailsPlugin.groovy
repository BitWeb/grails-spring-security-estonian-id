import ee.bitweb.grails.springsecurity.estonianid.DefaultEstonianIdAuthenticationDao
import ee.bitweb.grails.springsecurity.estonianid.DefaultEstonianIdPostAuthenticationChecks
import ee.bitweb.grails.springsecurity.estonianid.DefaultEstonianIdPreAuthenticationChecks
import ee.bitweb.grails.springsecurity.estonianid.EstonianIdUserDetailsService
import ee.bitweb.grails.springsecurity.estonianid.IdCardAuthenticationFilter
import ee.bitweb.grails.springsecurity.estonianid.IdCardAuthenticationHandler
import ee.bitweb.grails.springsecurity.estonianid.IdCardAuthenticationProvider
import ee.bitweb.grails.springsecurity.estonianid.IdCardAuthenticationService
import ee.bitweb.grails.springsecurity.estonianid.MobileIdAuthenticationFilter
import ee.bitweb.grails.springsecurity.estonianid.MobileIdAuthenticationHandler
import ee.bitweb.grails.springsecurity.estonianid.MobileIdAuthenticationProvider
import ee.bitweb.grails.springsecurity.estonianid.MobileIdAuthenticationService
import grails.plugin.springsecurity.SecurityFilterPosition
import grails.plugin.springsecurity.SpringSecurityUtils
import groovy.util.logging.Slf4j

@Slf4j
class SpringSecurityEstonianIdGrailsPlugin {
    def version = "0.9"
    def grailsVersion = "2.5 > *"
    def title = "Spring Security Estonian Id Plugin"
    def author = "Ivar Kängsepp"
    def authorEmail = "ivar@bitweb.ee"
    def description = 'Estonian ID-card and Mobiil-ID authentication support for the Spring Security plugin.'
    def documentation = "http://grails.org/plugin/spring-security-estonian-id"
    def license = "APACHE"
    def issueManagement = [url: "https://github.com/BitWeb/grails-spring-security-estonian-id/issues"]
    def scm = [url: "https://github.com/BitWeb/grails-spring-security-estonian-id"]
    def loadAfter = ['springSecurityCore']

    def doWithSpring = {
        def conf = SpringSecurityUtils.securityConfig

        if (!conf) {
            println 'ERROR: There is no Spring Security configuration'
            println 'ERROR: Stop configuring Spring Security Estonian Id'
            return
        }

        println 'Configuring Spring Security EstonianId ...'

        SpringSecurityUtils.mergeConfig(SpringSecurityUtils.securityConfig, 'DefaultEstonianIdSecurityConfig')
        // have to get again after overlaying DefaultEstonianIdSecurityConfig
        conf = SpringSecurityUtils.securityConfig

        estonianIdUserDetailsService(EstonianIdUserDetailsService)

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

        estonianIdCardAuthenticationHandler(IdCardAuthenticationHandler)

        estonianMobileIdAuthenticationHandler(MobileIdAuthenticationHandler)

        estonianMobileIdPreAuthenticationChecks(DefaultEstonianIdPreAuthenticationChecks)

        estonianMobileIdPostAuthenticationChecks(DefaultEstonianIdPostAuthenticationChecks)

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
            preAuthenticationChecks = estonianMobileIdPreAuthenticationChecks
            postAuthenticationChecks = estonianMobileIdPostAuthenticationChecks
        }

        estonianIdCardAuthenticationFilter(IdCardAuthenticationFilter) {
            filterProcessesUrl = conf.estonianId.filter.idCardLogin.processUrl
            authenticationSuccessHandler = ref('estonianIdCardAuthenticationHandler')
            authenticationFailureHandler = ref('estonianIdCardAuthenticationHandler')
            authenticationManager = ref('authenticationManager')
            sessionAuthenticationStrategy = ref('sessionAuthenticationStrategy')
            fGetClientCertFromHeader = conf.estonianId.idCardCert.fGetClientCertFromHeader
            clientCertHeaderName = conf.estonianId.idCardCert.clientCertHeaderName
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

    def onConfigChange = { event ->
        println 'Updating configuring for Spring Security EstonianId'
        SpringSecurityUtils.mergeConfig(SpringSecurityUtils.securityConfig, 'DefaultEstonianIdSecurityConfig')
    }
}
