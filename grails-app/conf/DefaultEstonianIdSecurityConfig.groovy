security {

    estonianId {

        domain {
            estonianIdUserClassName = 'ee.bitweb.User'

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
            authSuccessUrl = "/test"
        }

        beans {
            //successHandler =
            //failureHandler =
            //redirectSuccessHandler =
            //redirectFailureHandler =
        }

    }
}