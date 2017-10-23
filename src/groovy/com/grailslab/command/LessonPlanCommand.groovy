package com.grailslab.command

import com.grailslab.enums.PrintOptionType
import com.grailslab.settings.Section
import com.grailslab.settings.SubjectName

/**
 * Created by Hasnat on 6/28/2015.
 */
@grails.validation.Validateable
class LessonPlanCommand {
    Section section
    SubjectName subject
    Integer weekNumber
    PrintOptionType printOptionType
    static constraints = {
        subject nullable: true
        weekNumber nullable: true
        printOptionType nullable: true
    }
}
