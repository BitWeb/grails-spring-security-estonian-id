def defaultConfigLocation = "configuration/${appName}-config.groovy"
def systemConfigLocationParameter = "${appName}.config.location"
if (System.properties[systemConfigLocationParameter]) {
    println "Loading ${appName} configuration from: ${System.properties[systemConfigLocationParameter]}"
    grails.config.locations = [
            "file:" + System.properties[systemConfigLocationParameter]
    ]
} else {
    println "${appName} system parameter '$systemConfigLocationParameter' is not configured. Loading ${appName} configuration from: $defaultConfigLocation"
    grails.config.locations = [
            "file:$defaultConfigLocation"
    ]
}


grails.project.groupId = appName
grails.mime.disable.accept.header.userAgents = ['Gecko', 'WebKit', 'Presto', 'Trident']
grails.mime.types = [
    all:           '*/*',
    atom:          'application/atom+xml',
    css:           'text/css',
    csv:           'text/csv',
    form:          'application/x-www-form-urlencoded',
    html:          ['text/html','application/xhtml+xml'],
    js:            'text/javascript',
    json:          ['application/json', 'text/json'],
    multipartForm: 'multipart/form-data',
    rss:           'application/rss+xml',
    text:          'text/plain',
    hal:           ['application/hal+json','application/hal+xml'],
    xml:           ['text/xml', 'application/xml']
]
grails.views.default.codec = "html"
grails.controllers.defaultScope = 'singleton'
grails {
    views {
        gsp {
            encoding = 'UTF-8'
            htmlcodec = 'xml'
            codecs {
                expression = 'html'
                scriptlet = 'html'
                taglib = 'none'
                staticparts = 'none'
            }
        }
    }
}

grails.converters.encoding = "UTF-8"
grails.scaffolding.templates.domainSuffix = 'Instance'
grails.json.legacy.builder = false
grails.enable.native2ascii = true
grails.spring.bean.packages = []
grails.web.disable.multipart=false
grails.exceptionresolver.params.exclude = ['password']
grails.hibernate.cache.queries = false
grails.hibernate.pass.readonly = false
grails.hibernate.osiv.readonly = false

environments {
    development {
        grails.logging.jul.usebridge = true
    }
    production {
        grails.logging.jul.usebridge = false
    }
}

log4j.main = {
    all 'grails.app'
    all ''
    all 'ee.bitweb'

    all 'grails.plugin.springsecurity.web.filter.DebugFilter'

    warn 'org.codehaus.groovy.grails',
         'org.springframework',
         'org.hibernate',
         'org.apache',
         'net.sf.ehcache.hibernate'
}

grails.plugin.springsecurity.debug.useFilter = true

grails.web.url.converter = 'hyphenated'
