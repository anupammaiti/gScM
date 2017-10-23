package com.grailslab.stmgmt

import com.grailslab.enums.Gender
import com.grailslab.enums.Religion
import com.grailslab.enums.StudentStatus
import com.grailslab.gschoolcore.BasePersistentObj

class Registration extends BasePersistentObj{

    //reference of current class, section and student if admitted
    StudentStatus studentStatus = StudentStatus.NEW         // only using NEW - new Registration, Admission - if adimt

    String studentID
    String cardNo
    Integer deviceId
    String name
    String nameBangla
    String mobile
    Integer admissionYear
    Gender gender
    String bloodGroup
    Religion religion
    String nationality
    Date birthDate
    String birthCertificateNo
    String presentAddress
    String permanentAddress
    String email
    String imagePath

//fathers information
    String fathersName
    String fathersMobile
    String fathersProfession
    Double fathersIncome
    Boolean fatherIsAlive

 //mothers information
    String mothersName
    String mothersMobile
    String mothersProfession
    Double mothersIncome
    Boolean motherIsAlive

//guardian information
    String guardianName
    String guardianMobile

    static constraints = {
        nameBangla nullable: true
        nationality nullable: true
        fathersMobile nullable: true
        fatherIsAlive nullable: true
        mothersMobile nullable: true
        mothersIncome nullable: true
        motherIsAlive nullable: true
        guardianName nullable: true
        guardianMobile nullable: true
        admissionYear nullable: true
        deviceId unique: true, nullable: true
        studentID unique: true
        religion nullable: true
        presentAddress nullable: true
        permanentAddress nullable: true
        email nullable: true, email: true
        mobile nullable: true
        fathersProfession nullable: true
        mothersProfession nullable: true
        fathersIncome nullable: true
        cardNo nullable: true
        permanentAddress nullable: true
        birthDate nullable: true
        imagePath nullable: true
        mothersName nullable: true
        bloodGroup nullable: true
        gender nullable: true
        birthCertificateNo nullable: true
    }

}
