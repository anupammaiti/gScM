package com.grailslab.command


import com.grailslab.enums.ExamTerm
import com.grailslab.gschoolcore.AcademicYear
import com.grailslab.settings.Section
import com.grailslab.settings.ShiftExam

/**
 * Created by aminul on 3/19/2015.
 */
@grails.validation.Validateable
class SectionStudentCommand {
    Section section
    AcademicYear academicYear
    ShiftExam shiftExam

    static constraints = {
        shiftExam nullable: true
    }
}