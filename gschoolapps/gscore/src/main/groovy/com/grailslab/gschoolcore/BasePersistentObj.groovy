package com.grailslab.gschoolcore

/**
 * Created by aminul on 8/25/2015.
 */
class BasePersistentObj implements Serializable {
    def springSecurityService
    def schoolService

    static transients = ['springSecurityService','schoolService']

    static mapping = {
        tablePerHierarchy false
    }

    Date dateCreated // autoupdated by GORM
    Date lastUpdated // autoupdated by GORM
    String createdBy
    String updatedBy
    Long schoolId
    AcademicYear academicYear
    ActiveStatus activeStatus = ActiveStatus.ACTIVE   // Active, inactive, deleted

    static constraints = {
        createdBy(nullable: true)
        updatedBy(nullable: true)
        dateCreated(nullable: true)
        lastUpdated(nullable: true)
        schoolId(nullable: true)
        academicYear(nullable: true)
        activeStatus(nullable: true)
    }

    def beforeInsert() {
        def loggedUser =springSecurityService?.principal
        if (!createdBy) {
            createdBy = loggedUser?.username?:'system'
        }
        if(!academicYear){
//               academicYear=AcademicYear.Y2014
            academicYear=loggedUser?.academicYear
        }
    }
    def beforeUpdate(){
        def loggedUser =springSecurityService?.principal
        if (!updatedBy) {
            updatedBy = loggedUser?.username?:'system'
        }
    }
}
