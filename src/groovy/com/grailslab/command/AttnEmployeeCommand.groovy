package com.grailslab.command

import com.grailslab.hr.HrCategory
import com.grailslab.settings.ClassName

/**
 * Created by Aminul on 2/24/2016.
 */
@grails.validation.Validateable
class AttnEmployeeCommand {
    Long id
    Date recordDate
    Long employeeId
    String inTime
    String outTime
    String reason
    static constraints = {
        id nullable: true
        inTime nullable: true
        outTime nullable: true
        reason  nullable: true
        recordDate nullable: true
    }
}

@grails.validation.Validateable
class AttnEmployeeLateEntryCommand {
    Date startDate
    Date endDate
    Long employeeId
    HrCategory hrCategory

    static constraints = {
        employeeId nullable: true
        hrCategory nullable: true
    }
}
@grails.validation.Validateable
class AttnStudentLateEntryCommand {
    Date startDate
    Date endDate
    ClassName className

    static constraints = {
    }
}