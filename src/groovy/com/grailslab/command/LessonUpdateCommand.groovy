package com.grailslab.command

import com.grailslab.enums.ExamTerm
import com.grailslab.hr.Employee
import com.grailslab.settings.SubjectName

/**
 * Created by aminul on 5/31/2015.
 */
@grails.validation.Validateable
class LessonUpdateCommand {
    Long lessonId
    Long lessonDetailId
    ExamTerm examTerm
    Date lessonDate
    Employee employee
    String topics
    String homeWork
    Date examDate
    SubjectName subject
    static constraints = {
        examDate nullable: true
        homeWork nullable: true
    }
}
