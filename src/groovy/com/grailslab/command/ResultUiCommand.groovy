package com.grailslab.command


import com.grailslab.enums.ExamTerm
import com.grailslab.enums.PrintOptionType
import com.grailslab.gschoolcore.AcademicYear
import com.grailslab.settings.Section
import com.grailslab.settings.ShiftExam
import com.grailslab.stmgmt.Student

/**
 * Created by aminul on 5/3/2015.
 */
@grails.validation.Validateable
class ResultUiCommand {
    Long id         //exam id
    Section section
    AcademicYear academicYear
    ExamTerm examTerm
    ShiftExam shiftExam
    Student student
    PrintOptionType printOptionType
    static constraints = {
        id nullable: true
        section nullable: true
        academicYear nullable: true
        examTerm nullable: true
        student nullable: true
        printOptionType nullable: true
        shiftExam nullable: true
    }
}
