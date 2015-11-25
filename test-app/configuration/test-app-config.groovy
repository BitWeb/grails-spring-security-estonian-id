//EstonianId
/*grails.plugin.springsecurity.estonianId.domain.estonianIdUserClassName = 'Userxx'
grails.plugin.springsecurity.estonianId.domain.estonainIdUserIdCodeProperty = 'idCode2'
grails.plugin.springsecurity.estonianId.domain.estonainIdUserGivennameProperty = 'givenname'
grails.plugin.springsecurity.estonianId.domain.estonainIdUserSurnameProperty = 'surname'
grails.plugin.springsecurity.estonianId.domain.estonainIdUserScreenNameProperty = 'screenName'
grails.plugin.springsecurity.estonianId.domain.defaultRoleNames = ['ROLE_DEFAULT']
grails.plugin.springsecurity.estonianId.domain.fCreateNewUsers = true
grails.plugin.springsecurity.estonianId.mobileIdLang.defaultLanguageCode = 'EST'
grails.plugin.springsecurity.estonianId.mobileIdLang.localeToLangMap = ['et_EE': 'EST', 'en_EE': 'ENG', 'ru_EE': 'RUS', 'lt_EE': 'LIT']
grails.plugin.springsecurity.estonianId.digiDocServiceUrl = "https://tsp.demo.sk.ee"
grails.plugin.springsecurity.estonianId.digiDocServiceAppServiceName = "Testimine"
//grails.plugin.springsecurity.estonianId.redirect.authFailUrl = "/j_spring_security_estonianid_redirect"
//grails.plugin.springsecurity.estonianId.redirect.authSuccessUrl = "/test"*/

// Added by the Spring Security Core plugin:
grails {
        plugin {
                springsecurity {
                        userLookup {
                                userDomainClassName = 'User'
                                authorityJoinClassName = 'UserRole'
                        }
                        estonianId {

                                domain {
                                        estonianIdUserClassName = 'User'

                                        estonainIdUserIdCodeProperty = 'idCode'
                                        estonainIdUserGivennameProperty = 'givenname'
                                        estonainIdUserSurnameProperty = 'surname'
                                        estonainIdUserScreenNameProperty = 'screenName'

                                        estonainIdAppUserConnectionPropertyName = "user"

                                        defaultRoleNames = ['ROLE_DEFAULT']

                                        fCreateNewUsers = true
                                }

                                mobileIdLang {
                                        defaultLanguageCode = 'EST'
                                        localeToLangMap = ['et_EE': 'EST', 'en_EE': 'ENG', 'ru_EE': 'RUS', 'lt_EE': 'LIT']
                                }

                                digiDocServiceUrl = "https://tsp.demo.sk.ee"
                                digiDocServiceAppServiceName = "Testimine"

                                filter {
                                        idCardLogin {
                                                processUrl = "/j_spring_security_estonianid_idcard_json"
                                                type = 'json'
                                                methods = ['GET','POST']
                                        }
                                        mobileIdLogin {
                                                processUrl = "/j_spring_security_estonianid_mobileid_json"
                                                type = 'json'
                                                methods = ['GET','POST']
                                        }
                                }

                                redirect {
                                        authFailUrl = "/j_spring_security_estonianid_redirect"
                                        authSuccessUrl = "/authSuccessUrl"
                                }

                                beans {
                                        //successHandler =
                                        //failureHandler =
                                        //redirectSuccessHandler =
                                        //redirectFailureHandler =
                                }

                        }
                }
        }
}
/*grails.plugin.springsecurity.userLookup.userDomainClassName = 'Userxx'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'UserRole'*/
grails.plugin.springsecurity.authority.className = 'Role'
grails.plugin.springsecurity.controllerAnnotations.staticRules = [
        '/':                ['permitAll'],
        '/index':           ['permitAll'],
        '/index.gsp':       ['permitAll'],
        '/assets/**':       ['permitAll'],
        '/**/js/**':        ['permitAll'],
        '/**/css/**':       ['permitAll'],
        '/**/images/**':    ['permitAll'],
        '/**/favicon.ico':  ['permitAll']
]

//Extra
grails.plugin.springsecurity.securityConfigType = "Annotation"
grails.plugin.springsecurity.rejectIfNoRule = false
grails.plugin.springsecurity.fii.rejectPublicInvocations = false