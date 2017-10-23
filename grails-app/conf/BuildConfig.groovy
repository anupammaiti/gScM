grails.servlet.version = "3.0" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.work.dir = "target/work"
grails.project.target.level = 1.8
grails.project.source.level = 1.8
grails.project.war.file = "target/idealschool-${appVersion}.war"
//grails.project.war.file = "target/baily-${appVersion}.war"
//grails.project.war.file = "target/nhs-${appVersion}.war"
//grails.project.war.file = "target/adarshaschool-${appVersion}.war"
//grails.project.war.file = "target/demo-${appVersion}.war"
//grails.project.war.file = "target/${appName}-${appVersion}.war"

//Add application plugins
//grails.plugin.location."ponline" = "gschoolapps/online"
grails.server.port.http=8090

//http://grails.github.io/grails-doc/2.5.3/guide/upgradingFrom22.html
//http://naleid.com/blog/2014/11/10/debugging-grails-forked-mode
//grails -reloading run-app
//forkConfig = [maxMemory: 1024, minMemory: 64, debug: true, maxPerm: 256]

grails.project.fork = [
        // configure settings for compilation JVM, note that if you alter the Groovy version forked compilation is required
        //  compile: [maxMemory: 256, minMemory: 64, debug: false, maxPerm: 256, daemon:true],

        // configure settings for the test-app JVM, uses the daemon by default
        test: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, daemon:true],
//        test: false,
        // configure settings for the run-app JVM
//        run: [maxMemory: 1024, minMemory: 64, debug: true, maxPerm: 256, forkReserve:false],
        run: false,
//        run: [maxMemory: 1024, minMemory: 64, debug: true, maxPerm: 256],
        // configure settings for the run-war JVM
        war: [maxMemory: 1024, minMemory: 64, debug: false, maxPerm: 256, forkReserve:false],
        // configure settings for the Console UI JVM
        console: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256]
]

grails.project.dependency.resolver = "maven" // or ivy
grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // specify dependency exclusions here; for example, uncomment this to disable ehcache:
        // excludes 'ehcache'
    }
    log "error" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve
    legacyResolve false // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility

    repositories {
        inherits true // Whether to inherit repository definitions from plugins

        grailsPlugins()
        grailsHome()
        mavenLocal()
        grailsCentral()
        mavenCentral()
        // uncomment these (or add new ones) to enable remote dependency resolution from public Maven repositories
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
    }

    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes e.g.
        runtime 'mysql:mysql-connector-java:5.1.29'
        compile 'org.apache.commons:commons-lang3:3.1'
//        compile 'joda-time:joda-time:2.3'
        compile 'joda-time:joda-time:2.9.6'
        build 'com.lowagie:itext:2.1.7'
        // runtime 'org.postgresql:postgresql:9.3-1101-jdbc41'
//        test "org.grails:grails-datastore-test-support:1.0.2-grails-2.4"
        compile 'net.sf.jasperreports:jasperreports:6.1.0' // needed by jasper
        compile "com.squareup.okhttp3:okhttp:3.8.1"

    }

    plugins {

        compile "org.grails.plugins:spring-security-core:2.0.0"
        compile(":jasper:1.11.0") { excludes 'jasperreports' }
        // plugins for the build system only
        build ":tomcat:8.0.22" // or ":tomcat:8.0.22"
//        build ':tomcat:8.0.30' // or ":tomcat:8.0.22"

        // plugins for the compile step
//        compile ":scaffolding:2.1.2"
        compile ":cache:1.1.8"
//        compile ":asset-pipeline:2.5.7"
        compile "org.grails.plugins:asset-pipeline:2.14.1"

        // plugins needed at runtime but not for compilation
//        runtime ":hibernate4:4.3.10" // or ":hibernate:3.6.10.18"
//        compile "org.grails.plugins:hibernate4:5.0.0.RC1" // or ':hibernate:3.6.10.19'
        runtime ':hibernate4:4.3.10' // or ':hibernate:3.6.10.19'
//        runtime ":hibernate4:4.3.8.1" // or ":hibernate:3.6.10.18"
        runtime ":database-migration:1.4.0"
        runtime ":jquery:1.11.1"
        compile ":sequence-generator:1.1"
        compile "org.grails.plugins:csv:0.3.1"
    }
}
