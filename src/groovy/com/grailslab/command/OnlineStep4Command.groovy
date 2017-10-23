package com.grailslab.command
/**
 * Created by Hasnat on 6/28/2015.
 */
@grails.validation.Validateable
class OnlineStep4Command {
    Long regId
    String preSchoolName
    String preSchoolAddress
    String preSchoolClass
    Date preSchoolTcDate
    static constraints = {
        preSchoolName nullable: true
        preSchoolAddress nullable: true
        preSchoolClass nullable: true
        preSchoolTcDate nullable: true
    }
}
