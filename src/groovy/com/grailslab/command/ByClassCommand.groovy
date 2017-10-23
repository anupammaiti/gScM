package com.grailslab.command

import com.grailslab.settings.ClassName
import com.grailslab.settings.Section

import com.grailslab.stmgmt.RegOnlineRegistration
import com.grailslab.stmgmt.Student

/**
 * Created by Hasnat on 6/27/2015.
 */
@grails.validation.Validateable
class ByClassCommand {
    String feeId
    Student student
}

@grails.validation.Validateable
class FormFeeCommand {
    String feeId
    ClassName cName
}

@grails.validation.Validateable
class OnlineAdmissionCommand {
    String feeId
    RegOnlineRegistration selectedApplicant
    Section section
}
