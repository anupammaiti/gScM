package com.grailslab.command

import com.grailslab.enums.Gender
import com.grailslab.enums.Religion

import com.grailslab.settings.Profession

/**
 * registration controller
 */
@grails.validation.Validateable
class RegistrationCommand {
    Long id
    String studentID
    String name
    String bloodGroup
    Religion religion
    String presentAddress
    String permanentAddress
    Integer deviceId
    String email
    String mobile
    String cardNo
    Date birthDate
    Gender gender
    String nameBangla
    Integer admissionYear
    String nationality
    String birthCertificateNo

//fathers
    String fathersProfession
    String fathersName
    Double fathersIncome
    String fathersMobile
    Boolean fatherIsAlive

    String mothersName
    String mothersProfession
    String mothersMobile
    Double mothersIncome
    Boolean motherIsAlive

//guardian information
    String guardianName
    String guardianMobile


    static constraints = {
        nameBangla nullable: true
        admissionYear nullable: true
        nationality nullable: true
        birthCertificateNo nullable: true
        fathersMobile nullable: true
        fatherIsAlive nullable: true
         mothersMobile nullable: true
         mothersIncome nullable: true
         motherIsAlive nullable: true
        guardianName nullable: true
        guardianMobile nullable: true

        id nullable: true
        studentID nullable: true
        name nullable: true
        bloodGroup nullable: true
        presentAddress nullable: true
        permanentAddress nullable: true
        deviceId nullable: true
        email nullable: true
        mobile nullable: true
        fathersProfession nullable: true
        mothersProfession nullable: true
        fathersIncome nullable: true
        cardNo nullable: true
        birthDate nullable: true
        religion nullable: true
        gender nullable: true
    }
}

