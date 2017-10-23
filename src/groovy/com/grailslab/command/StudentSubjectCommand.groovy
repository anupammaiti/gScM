package com.grailslab.command

import com.grailslab.enums.ExamType
import com.grailslab.enums.Gender
import com.grailslab.enums.PrintOptionType
import com.grailslab.enums.Religion
import com.grailslab.enums.YearMonths
import com.grailslab.gschoolcore.AcademicYear
import com.grailslab.settings.ClassName
import com.grailslab.settings.ClassSubjects
import com.grailslab.settings.Exam
import com.grailslab.settings.Section
import com.grailslab.settings.ShiftExam
import com.grailslab.settings.SubjectName
import com.grailslab.stmgmt.Registration

/**
 * Created by aminul on 3/19/2015.
 */
@grails.validation.Validateable
class StudentSubjectCommand {
    ClassName className
    Section sectionName
    Gender gender
    Religion religion
    SubjectName subjectName
    Boolean isOptional
    Boolean added
    String selectedStdId
    static constraints = {
        className nullable: true
        sectionName nullable: true
        gender nullable: true
        religion nullable: true
        subjectName nullable: true
        isOptional nullable: true
        added nullable: true
        selectedStdId nullable: true
    }
}

@grails.validation.Validateable
class TransferSubjectCommand {
    AcademicYear academicYear
    ClassName className
    Section sectionName
    Gender gender
    Religion religion
    static constraints = {
        sectionName nullable: true
        gender nullable: true
        religion nullable: true
    }
}
@grails.validation.Validateable
class AddTransferSubjectCommand {
    AcademicYear academicYear
    String selectedStdId

    static constraints = {
        academicYear nullable: true
        selectedStdId nullable: true
    }
}
@grails.validation.Validateable
class SubjectStudentReportCommand {
    ClassName className
    Section section
    SubjectName subjectName
    PrintOptionType printOptionType
    static constraints = {

    }


}


@grails.validation.Validateable
class StudentMarkEntryStatusCommand {
    ShiftExam shiftExam
    ClassName className
    Exam exam
//    ExamType examType
    PrintOptionType printOptionType
    static constraints = {
        className nullable: true
        exam nullable: true
//        examType nullable: true
    }


}