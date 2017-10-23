package com.grailslab.command

import com.grailslab.enums.PrintOptionType
import com.grailslab.settings.ExamSchedule

/**
 * Created by Hasnat on 6/28/2015.
 */
@grails.validation.Validateable
class ExamMarkPrintCommand {
    ExamSchedule examSchedule
    PrintOptionType printOptionType
    String inputType
    static constraints = {
        inputType nullable: true
    }
}
