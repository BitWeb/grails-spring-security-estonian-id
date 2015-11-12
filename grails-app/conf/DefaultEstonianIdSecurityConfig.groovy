security {

    estonianId {

        appId = "Invalid"
        secret = 'Invalid'
        apiKey = 'Invalid'

        domain {
            classname = 'User'
            connectionPropertyName = "user"
        }

        useAjax = true
        autoCheck = true

        jsconf = "fbSecurity"

        autoCreate {
            enabled = true
            roles = ['ROLE_ID']
        }

        filter {
            json {
                processUrl = "/j_spring_security_estonianid_json"
                type = 'json' // or 'jsonp'
                methods = ['POST']
            }
            redirect {
                redirectFromUrl = "/j_spring_security_estonianid_redirect"
            }
            processUrl = "/j_spring_security_estonianid_check"
            type = 'redirect' //transparent, cookieDirect, redirect or json
            position = 720 //see SecurityFilterPosition
            forceLoginParameter = 'j_spring_estonianid_force'
        }

        beans {
            //successHandler =
            //failureHandler =
            //redirectSuccessHandler =
            //redirectFailureHandler =
        }

    }
}