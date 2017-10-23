package com.grailslab.hr


import com.grailslab.enums.EmployeeType
import com.grailslab.gschoolcore.BasePersistentObj

class Employee extends BasePersistentObj {

//    new fieldsmpl
    HrCategory hrCategory
    HrDesignation hrDesignation
    String hrStaffCategory
    Integer sortOrder=1
    HrPeriod hrPeriod

    String empID
    String name
    String barcode
    String fathersName
    String mothersName
    Date birthDate
    Date joiningDate
    Date confirmationDate
    String presentAddress
    String permanentAddress
    String emailId
    String bloodGroup
    String cardNo
    String mobile
    String fbId
    String nationalId
    String aboutMe
    String imagePath
    String salaryAccounts
    String dpsAccounts
    EmployeeType employeeType
    Boolean addedAsUser = false
    Integer deviceId

    static constraints = {
        deviceId nullable: true
        empID unique: true
        barcode unique: true, nullable: true
        mothersName nullable: true
        birthDate nullable: true
        joiningDate nullable: true
        confirmationDate nullable: true
        presentAddress nullable: true
        permanentAddress nullable: true
        emailId nullable: true, email: true
        bloodGroup nullable: true
        cardNo nullable: true
        mobile nullable: true
        addedAsUser nullable: true
        fbId nullable: true
        nationalId nullable: true
        aboutMe nullable: true
        imagePath nullable: true
        salaryAccounts nullable: true
        dpsAccounts nullable: true
        employeeType nullable: true

        hrCategory nullable: true
        hrDesignation nullable: true
        hrStaffCategory nullable: true
        sortOrder nullable: true
        hrPeriod nullable: true
    }
    static mapping = {
        aboutMe type: 'text'
    }
}
