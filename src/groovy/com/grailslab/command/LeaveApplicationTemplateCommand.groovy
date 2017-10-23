package com.grailslab.command

import com.grailslab.enums.LeaveApplyType

@grails.validation.Validateable
class LeaveApplicationTemplateCommand {
    Long id
    LeaveApplyType applyType
    String leaveTemplate


    static constraints = {
        id nullable: true
        leaveTemplate nullable: true

    }
}
