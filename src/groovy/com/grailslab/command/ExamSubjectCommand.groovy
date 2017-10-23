package com.grailslab.command

import com.grailslab.enums.ExamTerm
import com.grailslab.enums.ExamType
import com.grailslab.settings.Section
import com.grailslab.settings.ShiftExam

/**
 * Created by Hasnat on 6/27/2015.
 */
@grails.validation.Validateable
class ExamSubjectCommand {
    Section section
    ShiftExam examName
    ExamType examType

    static constraints = {

    }
}
