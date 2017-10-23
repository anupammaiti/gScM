package com.grailslab.command

import com.grailslab.settings.Exam
import com.grailslab.settings.ExamPeriod
import com.grailslab.settings.SubjectName

/**
 * Created by Hasnat on 6/27/2015.
 */
@grails.validation.Validateable
class ExamScheduleCommand {
    Long id
    SubjectName subject
    Date ctExamDate
    ExamPeriod ctPeriod
    Double ctExamMark
    Date hallExamDate
    Double hallExamMark
    ExamPeriod hallPeriod
    Exam exam

    static constraints = {
        id(nullable: true)
        ctExamDate(nullable: true)
        ctPeriod(nullable: true)
        ctExamMark(nullable: true)
        hallExamDate(nullable: true)
        hallExamMark(nullable: true)
        hallPeriod(nullable: true)
        subject(nullable: true)
        exam(nullable: true)
    }
}
