package com.grailslab.command

import com.grailslab.settings.ClassName
import com.grailslab.settings.Section

import com.grailslab.stmgmt.Student

/**
 * Created by Hasnat on 6/27/2015.
 */
@grails.validation.Validateable
class ByClassStepCommand {
    Student studentId
    String feeItemIds
    Date invoiceDate
    static constraints = {
        invoiceDate nullable: true
    }
}

@grails.validation.Validateable
class OnlineAdmissionStepCommand {
    String feeItemIds
    Section section
    String admitCardNumber
    Integer rollNo
    Date invoiceDate
    static constraints = {
        invoiceDate nullable: true
    }
}

@grails.validation.Validateable
class FormFeeInvoiceCommand {
    ClassName className
    String feeItemIds
    String name
    String mobile
    String fathersName
    Date birthDate
    static constraints = {
        birthDate nullable: true
        fathersName nullable: true
    }
}
