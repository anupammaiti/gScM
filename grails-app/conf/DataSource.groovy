dataSource {
    pooled = true
    jmxExport = true
    driverClassName = "com.mysql.jdbc.Driver"
    dialect = "com.domain.mysql.dialect.MySQLUTF8InnoDBDialect"
}

hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = false
//    cache.region.factory_class = 'net.sf.ehcaate.cache.ehcache.EhCacheRegionFactory' // Hibernate 4
//        cache.region.factory_class = 'org.hche.hibernate.EhCacheRegionFactory' // Hibernate 3
//    cache.region.factory_class = 'org.hibernibernate.cache.SingletonEhCacheRegionFactory' // Hibernate 3
//    cache.region.factory_class = 'org.hibernate.cache.ehcache.SingletonEhCacheRegionFactory' // Hibernate 4
    cache.region.factory_class = 'org.hibernate.cache.ehcache.SingletonEhCacheRegionFactory' // Hibernate 4
    singleSession = true // configure OSIV singleSession mode
    flush.mode = 'auto' // OSIV session flush mode outside of transactional context
}

// environment specific settings
environments {
    development {
        dataSource {
            dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
 //           url = "jdbc:mysql://localhost/adarshaprod?useUnicode=yes&characterEncoding=UTF-8"
//            url = "jdbc:mysql://localhost/idealpod?useUnicode=yes&characterEncoding=UTF-8"
            url = "jdbc:mysql://localhost/bailyprod?useUnicode=yes&characterEncoding=UTF-8"
//           url = "jdbc:mysql://localhost/idealprod?useUnicode=yes&characterEncoding=UTF-8"
//           url = "jdbc:mysql://localhost/nhsprod?useUnicode=yes&characterEncoding=UTF-8"
//           url = "jdbc:mysql://localhost/schooldb?useUnicode=yes&characterEncoding=UTF-8"
            username = "root"
            password = "root"
            logSql = true
        }
    }

    nhs {
        dataSource {
            dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
            pooled = true
//            amazon Naraynganj High school & College settings
            username = "grailslabccm"
            password = "word123pass"
            url = "jdbc:mysql://siliconvelly.cexdb61sld2j.us-west-2.rds.amazonaws.com:3306/nhs?user=grailslabccm&password=word123pass&useUnicode=yes&characterEncoding=UTF-8"

            properties {
                // See http://grails.org/doc/latest/guide/conf.html#dataSource for documentation
                jmxEnabled = true
                initialSize = 5
                maxActive = 50
                minIdle = 5
                maxIdle = 25
                maxWait = 10000
                maxAge = 10 * 60000
                timeBetweenEvictionRunsMillis = 5000
                minEvictableIdleTimeMillis = 60000
                validationQuery = "SELECT 1"
                validationQueryTimeout = 3
                validationInterval = 15000
                testOnBorrow = true
                testWhileIdle = true
                testOnReturn = false
                jdbcInterceptors = "ConnectionState"
                defaultTransactionIsolation = java.sql.Connection.TRANSACTION_READ_COMMITTED
            }
        }
    }

    nideal {
        dataSource {
            dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
            pooled = true
//            amazon ideal school settings

            username = "gschoolIdeal"
            password = "word123pass"
            url = "jdbc:mysql://idealschooldb.cexdb61sld2j.us-west-2.rds.amazonaws.com:3306/idealSchool?user=grailslabccm&password=word123pass&useUnicode=yes&characterEncoding=UTF-8"

            properties {
                // See http://grails.org/doc/latest/guide/conf.html#dataSource for documentation
                jmxEnabled = true
                initialSize = 5
                maxActive = 50
                minIdle = 5
                maxIdle = 25
                maxWait = 10000
                maxAge = 10 * 60000
                timeBetweenEvictionRunsMillis = 5000
                minEvictableIdleTimeMillis = 60000
                validationQuery = "SELECT 1"
                validationQueryTimeout = 3
                validationInterval = 15000
                testOnBorrow = true
                testWhileIdle = true
                testOnReturn = false
                jdbcInterceptors = "ConnectionState"
                defaultTransactionIsolation = java.sql.Connection.TRANSACTION_READ_COMMITTED
            }
        }
    }
    bailyschool {
        dataSource {
            dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
            pooled = true

            //amazon Baily school settings
            username = "grailsscm"
            password = "word123pass"
            url = "jdbc:mysql://bailyschooldb.cexdb61sld2j.us-west-2.rds.amazonaws.com:3306/bailyprod?user=grailsscm&password=word123pass&useUnicode=yes&characterEncoding=UTF-8"

            properties {
                // See http://grails.org/doc/latest/guide/conf.html#dataSource for documentation
                jmxEnabled = true
                initialSize = 5
                maxActive = 50
                minIdle = 5
                maxIdle = 25
                maxWait = 10000
                maxAge = 10 * 60000
                timeBetweenEvictionRunsMillis = 5000
                minEvictableIdleTimeMillis = 60000
                validationQuery = "SELECT 1"
                validationQueryTimeout = 3
                validationInterval = 15000
                testOnBorrow = true
                testWhileIdle = true
                testOnReturn = false
                jdbcInterceptors = "ConnectionState"
                defaultTransactionIsolation = java.sql.Connection.TRANSACTION_READ_COMMITTED
            }
        }
    }
    atwarimphs {
        dataSource {
            dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
            pooled = true
            //amazon Atwari Model Pilot school settings
            username = "grailslabccm"
            password = "word123pass"
            url = "jdbc:mysql://siliconvelly.cexdb61sld2j.us-west-2.rds.amazonaws.com:3306/atwaripilot?user=grailslabccm&password=word123pass&useUnicode=yes&characterEncoding=UTF-8"
            properties {
                // See http://grails.org/doc/latest/guide/conf.html#dataSource for documentation
                jmxEnabled = true
                initialSize = 5
                maxActive = 50
                minIdle = 5
                maxIdle = 25
                maxWait = 10000
                maxAge = 10 * 60000
                timeBetweenEvictionRunsMillis = 5000
                minEvictableIdleTimeMillis = 60000
                validationQuery = "SELECT 1"
                validationQueryTimeout = 3
                validationInterval = 15000
                testOnBorrow = true
                testWhileIdle = true
                testOnReturn = false
                jdbcInterceptors = "ConnectionState"
                defaultTransactionIsolation = java.sql.Connection.TRANSACTION_READ_COMMITTED
            }
        }
    }
    adarshaschool {
        dataSource {
            dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
            pooled = true
//            amazon Adarsha Primary School settings
            username = "grailslabccm"
            password = "word123pass"
            url = "jdbc:mysql://siliconvelly.cexdb61sld2j.us-west-2.rds.amazonaws.com:3306/adarshaschool?user=grailslabccm&password=word123pass&useUnicode=yes&characterEncoding=UTF-8"
            properties {
                // See http://grails.org/doc/latest/guide/conf.html#dataSource for documentation
                jmxEnabled = true
                initialSize = 5
                maxActive = 50
                minIdle = 5
                maxIdle = 25
                maxWait = 10000
                maxAge = 10 * 60000
                timeBetweenEvictionRunsMillis = 5000
                minEvictableIdleTimeMillis = 60000
                validationQuery = "SELECT 1"
                validationQueryTimeout = 3
                validationInterval = 15000
                testOnBorrow = true
                testWhileIdle = true
                testOnReturn = false
                jdbcInterceptors = "ConnectionState"
                defaultTransactionIsolation = java.sql.Connection.TRANSACTION_READ_COMMITTED
            }
        }
    }
    demoschool {
        dataSource {
            dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
            pooled = true
            username = "root"
            password = "grailslab786"
            url = "jdbc:mysql://localhost/schooldb?useUnicode=yes&characterEncoding=UTF-8"

            properties {
                // See http://grails.org/doc/latest/guide/conf.html#dataSource for documentation
                jmxEnabled = true
                initialSize = 5
                maxActive = 50
                minIdle = 5
                maxIdle = 25
                maxWait = 10000
                maxAge = 10 * 60000
                timeBetweenEvictionRunsMillis = 5000
                minEvictableIdleTimeMillis = 60000
                validationQuery = "SELECT 1"
                validationQueryTimeout = 3
                validationInterval = 15000
                testOnBorrow = true
                testWhileIdle = true
                testOnReturn = false
                jdbcInterceptors = "ConnectionState"
                defaultTransactionIsolation = java.sql.Connection.TRANSACTION_READ_COMMITTED
            }
        }
    }
    test {
        dataSource {
            dbCreate = "update"
            url = "jdbc:h2:mem:testDb;MVCC=TRUE;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE"
        }
    }
}
