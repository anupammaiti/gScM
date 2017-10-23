package com.grailslab.stmgmt

import com.grailslab.gschoolcore.BasePersistentObj
import com.grailslab.settings.ClassName

class RegForm extends BasePersistentObj{
    ClassName className
    Date regOpenDate
    Date regCloseDate
    Date fromBirthDate
    Date toBirthDate
    String instruction
    String applyType  //online, offline, both
    Double formPrice
    String serialPrefix
    Integer serialStartFrom
    String applicationPrefix
    Integer applicationStartFrom

    static constraints = {
        instruction nullable: true
        formPrice nullable: true
        serialPrefix nullable: true
        serialStartFrom nullable: true
        applicationPrefix nullable: true
        applicationStartFrom nullable: true
        fromBirthDate nullable: true
        toBirthDate nullable: true
    }
    static mapping = {
        instruction type: 'text'
    }
}
