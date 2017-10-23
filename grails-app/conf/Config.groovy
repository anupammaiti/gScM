import org.apache.log4j.DailyRollingFileAppender
import org.apache.log4j.PatternLayout
import org.codehaus.groovy.grails.web.context.ServletContextHolder

// locations to search for config files that get merged into the main config;
// config files can be ConfigSlurper scripts, Java properties files, or classes
// in the classpath in ConfigSlurper format

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if (System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }

grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination

// The ACCEPT header will not be used for content negotiation for user agents containing the following strings (defaults to the 4 major rendering engines)
grails.mime.disable.accept.header.userAgents = ['Gecko', 'WebKit', 'Presto', 'Trident']
grails.mime.types = [ // the first one is the default format
                      all          : '*/*', // 'all' maps to '*' or the first available format in withFormat
                      atom         : 'application/atom+xml',
                      css          : 'text/css',
                      csv          : 'text/csv',
                      pdf          : 'application/pdf',
                      docx         : 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
                      xlsx         : 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
                      form         : 'application/x-www-form-urlencoded',
                      html         : ['text/html', 'application/xhtml+xml'],
                      js           : 'text/javascript',
                      json         : ['application/json', 'text/json'],
                      multipartForm: 'multipart/form-data',
                      rss          : 'application/rss+xml',
                      text         : 'text/plain',
                      hal          : ['application/hal+json', 'application/hal+xml'],
                      xml          : ['text/xml', 'application/xml']
]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// Legacy setting for codec used to encode data with ${}
grails.views.default.codec = "html"

// The default scope for controllers. May be prototype, session or singleton.
// If unspecified, controllers are prototype scoped.
grails.controllers.defaultScope = 'singleton'

// GSP settings
grails {
    views {
        gsp {
            encoding = 'UTF-8'
            htmlcodec = 'xml' // use xml escaping instead of HTML4 escaping
            codecs {
                expression = 'html' // escapes values inside ${}
                scriptlet = 'html' // escapes output from scriptlets in GSPs
                taglib = 'none' // escapes output from taglibs
                staticparts = 'none' // escapes output from static template parts
            }
        }
        // escapes all not-encoded output at final stage of outputting
        // filteringCodecForContentType.'text/html' = 'html'
    }
}


grails.converters.encoding = "UTF-8"
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
grails.web.disable.multipart = false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

// configure auto-caching of queries by default (if false you can cache individual queries with 'cache: true')
grails.hibernate.cache.queries = false

// configure passing transaction's read-only attribute to Hibernate session, queries and criterias
// set "singleSession = false" OSIV mode in hibernate configuration after enabling
grails.hibernate.pass.readonly = false
// configure passing read-only to OSIV session by default, requires "singleSession = false" OSIV mode
grails.hibernate.osiv.readonly = false
grails.databinding.dateFormats = ['dd/MM/yyyy']
environments.development.searchable.bulkIndexOnStartup = false
powersms.api.userid = "grailslab"
powersms.api.password = "grailslab123"
//grails -Dgrails.env=mycustomenv run-app/war
environments {
    development {
        grails.logging.jul.usebridge = true
//        grailslab.gschool.running = 'nhs'
//         grailslab.gschool.running = 'adarshaschool'
      grailslab.gschool.running = 'bailyschool'
//        grailslab.gschool.running = 'nideal'
//        grailslab.gschool.running = 'demoschool'
        grailslab.gschool.weekly.holiday = 'Fri,Sat'
        grailslab.gschool.attendance.calculation = 'automation'
        grailslab.gschool.morning.inTime = '073000'
        grailslab.gschool.day.inTime = '103000'
        grailslab.gschool.teacher.morning.inTime = '073000'
        grailslab.gschool.teacher.day.inTime = '103000'
        grails.serverURL = "http://localhost:8090/gschool"
        app.uploads.images.filesystemPath = "${System.properties['user.home']}/grailslab/uploads/image"
        app.uploads.files.filesystemPath = "${System.properties['user.home']}/grailslab/uploads/file"
        powersms.send.enabled = false
        powersms.send.from.text = "GrailsLab"
    }
    nhs {
        //        grails -Dgrails.env=nhs war
        grails.logging.jul.usebridge = false
        grailslab.gschool.running = 'nhs'
        grailslab.gschool.weekly.holiday = 'Fri'
        grailslab.gschool.attendance.calculation = 'manual'
        grailslab.gschool.morning.inTime = '073000'
        grailslab.gschool.day.inTime = '103000'
        grailslab.gschool.teacher.morning.inTime = '073000'
        grailslab.gschool.teacher.day.inTime = '103000'
        app.uploads.images.filesystemPath = "/usr/aminul/grailslab/nhs/uploads/image"
        app.uploads.files.filesystemPath = "/usr/aminul/grailslab/nhs/uploads/file"
        powersms.send.enabled = true
        powersms.send.from.text = "Narayanganj High School and college."
    }
    nideal {
        //        grails -Dgrails.env=nideal war
        grails.logging.jul.usebridge = false
        grailslab.gschool.running = 'nideal'
        grailslab.gschool.weekly.holiday = 'Fri'
        grailslab.gschool.attendance.calculation = 'manual'
        grailslab.gschool.morning.inTime = '073000'
        grailslab.gschool.day.inTime = '103000'
        grailslab.gschool.teacher.morning.inTime = '073000'
        grailslab.gschool.teacher.day.inTime = '103000'
        app.uploads.images.filesystemPath = "/usr/aminul/grailslab/ideal/uploads/image"
        app.uploads.files.filesystemPath = "/usr/aminul/grailslab/ideal/uploads/file"
        powersms.send.enabled = true
        powersms.send.from.text = "Narayanganj Ideal School"
    }
    bailyschool {
        //        grails -Dgrails.env=bailyschool war
        grails.logging.jul.usebridge = false
        grailslab.gschool.running = 'bailyschool'
        grailslab.gschool.weekly.holiday = 'Fri,Sat'
        grailslab.gschool.attendance.calculation = 'manual'
        grailslab.gschool.morning.inTime = '073000'
        grailslab.gschool.day.inTime = '103000'
        grailslab.gschool.teacher.morning.inTime = '073000'
        grailslab.gschool.teacher.day.inTime = '103000'
        app.uploads.images.filesystemPath = "/usr/aminul/grailslab/baily/uploads/image"
        app.uploads.files.filesystemPath = "/usr/aminul/grailslab/baily/uploads/file"
        powersms.send.enabled = true
        powersms.send.from.text = "Baily School Narayanganj"
    }
    adarshaschool {
        //        grails -Dgrails.env=adarshaschool war
        powersms.send.enabled = true
        grails.logging.jul.usebridge = false
        grailslab.gschool.running = 'adarshaschool'
        grailslab.gschool.weekly.holiday = 'Fri'
        grailslab.gschool.attendance.calculation = 'manual'
        grailslab.gschool.morning.inTime = '073000'
        grailslab.gschool.day.inTime = '103000'
        grailslab.gschool.teacher.morning.inTime = '073000'
        grailslab.gschool.teacher.day.inTime = '103000'
        app.uploads.images.filesystemPath = "/usr/aminul/grailslab/adarsha/uploads/image"
        app.uploads.files.filesystemPath = "/usr/aminul/grailslab/adarsha/uploads/file"
        powersms.send.enabled = true
        powersms.send.from.text = "Adarsha shishu primary school"
    }
    demoschool {
        //        grails -Dgrails.env=demoschool war
        grails.logging.jul.usebridge = false
        grailslab.gschool.running = 'bailyschool'
        grailslab.gschool.weekly.holiday = 'Fri,Sat'
        grailslab.gschool.attendance.calculation = 'manual'
        grailslab.gschool.morning.inTime = '073000'
        grailslab.gschool.day.inTime = '103000'
        grailslab.gschool.teacher.morning.inTime = '073000'
        grailslab.gschool.teacher.day.inTime = '103000'
        app.uploads.images.filesystemPath = "/usr/aminul/grailslab/demo/uploads/image"
        app.uploads.files.filesystemPath = "/usr/aminul/grailslab/demo/uploads/file"
        powersms.send.enabled = false
        powersms.send.from.text = "GrailsLab"
    }

}

// log4j configuration
log4j.main = {
    error 'org.codehaus.groovy.grails.web.servlet',        // controllers
            'org.codehaus.groovy.grails.web.pages',          // GSP
            'org.codehaus.groovy.grails.web.sitemesh',       // layouts
            'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
            'org.codehaus.groovy.grails.web.mapping',        // URL mapping
            'org.codehaus.groovy.grails.commons',            // core / classloading
            'org.codehaus.groovy.grails.plugins',            // plugins
            'org.codehaus.groovy.grails.orm.hibernate',      // hibernate integration
            'org.springframework',
            'org.hibernate',
            'net.sf.ehcache.hibernate'
//    info 'grails.plugin.springsecurity.web.filter.DebugFilter'
}

// Added by the Spring Security Core plugin:
grails.plugin.springsecurity.userLookup.userDomainClassName = 'com.grailslab.bailyschool.uma.User'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'com.grailslab.bailyschool.uma.UserRole'
grails.plugin.springsecurity.authority.className = 'com.grailslab.bailyschool.uma.Role'
grails.plugin.springsecurity.controllerAnnotations.staticRules = [
        '/'              : ['permitAll'],
        '/index'         : ['permitAll'],
        '/assets/**'     : ['permitAll'],
        '/image/**'      : ['permitAll'],
        '/**/js/**'      : ['permitAll'],
        '/**/css/**'     : ['permitAll'],
        '/**/images/**'  : ['permitAll'],
        '/**/reports/**' : ['permitAll'],
        '/**/favicon.ico': ['permitAll']
]
grails.plugin.springsecurity.logout.postOnly = false
grails.plugin.springsecurity.rejectIfNoRule = true
grails.plugin.springsecurity.rememberMe.cookieName = "baily_remember_me"
grails.plugin.springsecurity.successHandler.defaultTargetUrl = '/login/loginSuccess'
grails.plugin.springsecurity.anon.key = "glab"
//grails.plugin.springsecurity.successHandler.alwaysUseDefault = true
grails.plugin.springsecurity.useSwitchUserFilter = true
grails.plugin.springsecurity.switchUser.switchUserUrl = '/j_spring_security_switch_user'
grails.plugin.springsecurity.switchUser.exitUserUrl = '/j_spring_security_exit_user'
grails.plugin.springsecurity.switchUser.targetUrl = null // use the authenticationSuccessHandler
grails.plugin.springsecurity.switchUser.switchFailureUrl = '/login/switchFailed'
grails.plugin.springsecurity.providerNames = ['daoAuthenticationProvider', 'rememberMeAuthenticationProvider']

grails.plugin.springsecurity.securityConfigType = "InterceptUrlMap"

grails.plugin.springsecurity.interceptUrlMap = [
        '/j_spring_security_switch_user'     : ['ROLE_SWITCH_USER'],
        '/j_spring_security_exit_user'       : ['permitAll'],
        '/'                                  : ['permitAll'],
        '/index'                             : ['permitAll'],
        '/assets/**'                         : ['permitAll'],
        '/image/**'                          : ['permitAll'],
        '/**/js/**'                          : ['permitAll'],
        '/**/css/**'                         : ['permitAll'],
        '/**/images/**'                      : ['permitAll'],
        '/**/favicon.ico'                    : ['permitAll'],
        '/login/**'                          : ['permitAll'],
        '/logout/**'                         : ['permitAll'],

//        public url
        '/home/**'                           : ['permitAll'],
        '/online/**'                         : ['permitAll'],
        '/reunion/**'                        : ['permitAll'],
        '/results/**'                        : ['permitAll'],
        '/gallery/**'                        : ['permitAll'],
        '/blog/**'                           : ['permitAll'],
        '/faq/**'                            : ['permitAll'],
        '/calendar/**'                       : ['permitAll'],
        '/lessonPlan/**'                     : ['permitAll'],
        '/olympiad/**'                       : ['permitAll'],

        '/remote/**'                         : ['ROLE_SUPER_ADMIN','ROLE_SCHOOL_HEAD','ROLE_ADMIN','ROLE_ACCOUNTS','ROLE_HR','ROLE_ORGANIZER','ROLE_TEACHER','ROLE_LIBRARY'],
        '/profile/**'                        : ['ROLE_SUPER_ADMIN','ROLE_SCHOOL_HEAD','ROLE_ADMIN','ROLE_ACCOUNTS','ROLE_HR','ROLE_ORGANIZER','ROLE_TEACHER','ROLE_LIBRARY'],
        '/attendance/**'                     : ['ROLE_SUPER_ADMIN','ROLE_SCHOOL_HEAD','ROLE_ADMIN','ROLE_ACCOUNTS', 'ROLE_HR','ROLE_ORGANIZER','ROLE_TEACHER', 'ROLE_LIBRARY'],
        '/leave/**'                          : ['ROLE_SUPER_ADMIN','ROLE_SCHOOL_HEAD','ROLE_ADMIN','ROLE_ACCOUNTS', 'ROLE_HR','ROLE_ORGANIZER','ROLE_TEACHER', 'ROLE_LIBRARY'],
        //all url related to library
        '/web/**'                            : ['ROLE_SUPER_ADMIN','ROLE_SCHOOL_HEAD', 'ROLE_ADMIN','ROLE_ORGANIZER'],
        '/portalSwitch/**'                   : ['ROLE_SUPER_ADMIN','ROLE_SCHOOL_HEAD','ROLE_ADMIN','ROLE_HR'],
        '/portal/**'                         : ['ROLE_SUPER_ADMIN','ROLE_SCHOOL_HEAD','ROLE_ADMIN', 'ROLE_HR','ROLE_STUDENT'],
        '/sms/**'                            : ['ROLE_SUPER_ADMIN','ROLE_SCHOOL_HEAD','ROLE_ADMIN','ROLE_ACCOUNTS','ROLE_HR'],
        '/stmgst/**'                         : ['ROLE_SUPER_ADMIN','ROLE_SCHOOL_HEAD','ROLE_ADMIN','ROLE_ACCOUNTS','ROLE_HR'],
        '/deviceLog/**'                      : ['permitAll'],        //device log upload
        '/attn/**'                            : ['ROLE_SUPER_ADMIN','ROLE_SCHOOL_HEAD','ROLE_ADMIN','ROLE_HR'],

        '/admin/**'                          : ['ROLE_SUPER_ADMIN','ROLE_SCHOOL_HEAD','ROLE_ADMIN'],
        '/exam/**'                           : ['ROLE_SUPER_ADMIN','ROLE_SCHOOL_HEAD','ROLE_ADMIN'],
        '/library/**'                        : ['ROLE_SUPER_ADMIN','ROLE_SCHOOL_HEAD','ROLE_ADMIN', 'ROLE_LIBRARY'],
        '/lessonPlan/**'                     : ['ROLE_SUPER_ADMIN','ROLE_SCHOOL_HEAD','ROLE_ADMIN','ROLE_TEACHER'],
        '/accounts/**'                       : ['ROLE_SUPER_ADMIN','ROLE_SCHOOL_HEAD','ROLE_ADMIN','ROLE_ACCOUNTS','ROLE_HR'],
        '/hrm/**'                            : ['ROLE_SUPER_ADMIN','ROLE_SCHOOL_HEAD','ROLE_ADMIN','ROLE_HR'],
        '/salary/**'                         : ['ROLE_SUPER_ADMIN','ROLE_SCHOOL_HEAD','ROLE_ADMIN','ROLE_HR'],
        '/LeaveMgmt/**'                      : ['ROLE_SUPER_ADMIN','ROLE_SCHOOL_HEAD','ROLE_ADMIN','ROLE_HR'],
        '/teacher/**'                        : ['ROLE_SUPER_ADMIN','ROLE_SCHOOL_HEAD','ROLE_ADMIN','ROLE_TEACHER'],
        '/collection/**'                     : ['ROLE_SUPER_ADMIN','ROLE_SCHOOL_HEAD','ROLE_ADMIN','ROLE_ACCOUNTS','ROLE_HR'],
        '/applicant/**'                      : ['ROLE_SUPER_ADMIN','ROLE_SCHOOL_HEAD','ROLE_ADMIN', 'ROLE_ACCOUNTS']

]

grails.plugin.databasemigration.updateOnStart = false
grails.plugin.databasemigration.updateOnStartFileNames = ['changelog.groovy']



beans {
    cacheManager {
        shared = true
    }
}