package com.grailslab.command

import com.grailslab.enums.ExamTerm
import com.grailslab.enums.ExamType
import com.grailslab.enums.Shift

/**
 * Created by Hasnat on 6/28/2015.
 */
@grails.validation.Validateable
class ExamScheduleAllSectionCommand {
    Shift shift
    ExamTerm examTerm
    ExamType examType
    String sections
    static constraints = {
    }
}
