package com.grailslab.command

import com.grailslab.enums.ExamTerm
import com.grailslab.enums.ExamType
import com.grailslab.enums.GroupName
import com.grailslab.enums.Shift
import com.grailslab.settings.ClassName
import com.grailslab.settings.ShiftExam

/**
 * Created by Hasnat on 6/27/2015.
 */
@grails.validation.Validateable
class AddClassScheduleCommand {
    ClassName className
    ExamType examType
    ShiftExam shiftExam
    Boolean forceEdit

    static constraints = {
        forceEdit nullable: true
    }
}
