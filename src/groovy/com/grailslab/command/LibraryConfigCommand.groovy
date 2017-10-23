package com.grailslab.command

/**
 * Created by Hasnat on 6/28/2015.
 */
@grails.validation.Validateable
class LibraryConfigCommand {
    String userType
    Integer maxDay
    Integer maxBook
    Double fineAmount

    static constraints = {
    }
}

@grails.validation.Validateable
class RegApplicantSettingsCommand {
    Date startApplyDate
    Date lastApplyDate
    Date startAdmitCardDate
    Date lastAdmitCardDate
    Date examDate
    Date resultPublishDate
    Date vibaDate
    Date startAdmissionDate
    Date lastAdmissionDate
    Double admitCardFee=0
    Double admissionFee=0
    String appFormExtraInfo

    static constraints = {
        startApplyDate nullable: true
        lastApplyDate nullable: true
        examDate nullable: true
        resultPublishDate nullable: true
        vibaDate nullable: true
        startAdmitCardDate nullable: true
        lastAdmitCardDate nullable: true
        startAdmissionDate nullable: true
        lastAdmissionDate nullable: true
        admitCardFee nullable: true
        admissionFee nullable: true
        appFormExtraInfo nullable: true
    }
}
