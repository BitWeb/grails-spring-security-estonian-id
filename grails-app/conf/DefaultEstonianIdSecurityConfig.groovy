security {

    estonianId {

        domain {
            estonianIdUserClassName = 'ee.bitweb.User'
            userEstonianIdCodeProperty = 'idCode'
            connectionPropertyName = "user"

            idCodePropertyName = 'idCode'
            givennamePropertyName = 'givenname'
            surnamePropertyName = 'surname'
            screenNamePropertyName = 'name'
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