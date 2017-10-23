package com.grailslab.command.report

import com.grailslab.enums.PrintOptionType
import com.grailslab.settings.ClassName
import com.grailslab.settings.Section
import com.grailslab.stmgmt.Student

/**
 * Created by Aminul on 2/27/2016.
 */
@grails.validation.Validateable
class StdReligionAttendanceCommand {
    Date rStartDate
    Date rEndDate
    Student student
    String attndanceStatus
    String stdAttnCategory


    PrintOptionType printOptionType
    static constraints = {
        rEndDate nullable: true
        attndanceStatus nullable: true
        printOptionType nullable: true
        student nullable:true
        stdAttnCategory nullable: true
    }
}
