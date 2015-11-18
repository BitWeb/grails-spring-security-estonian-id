package test.app

class LoginUnmapFilters {

    def filters = {
        all(uri: '/login/**') {
            before = {
                response.sendRedirect(request.contextPath+'/')
            }
            after = { Map model ->

            }
            afterView = { Exception e ->

            }
        }
    }
}

