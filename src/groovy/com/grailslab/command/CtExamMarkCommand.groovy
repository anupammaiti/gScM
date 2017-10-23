package com.grailslab.command

import com.grailslab.enums.AttendStatus
import com.grailslab.settings.ExamSchedule
import com.grailslab.stmgmt.Student

/**
 * Created by Hasnat on 6/27/2015.
 */
@grails.validation.Validateable
class CtExamMarkCommand {

    Long id
    ExamSchedule examScheduleId
    Student studentId
    AttendStatus attendStatus
    Double markObtain

    static constraints = {
        id nullable: true
        studentId nullable: true
        markObtain blank: false, validator: { value, obj ->
            if( value> obj.examScheduleId.ctExamMark)
                return 'exam.mark.wrong.input.msg'
        }
    }
}
