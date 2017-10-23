package com.grailslab.stmgmt


import com.grailslab.enums.AttendStatus
import com.grailslab.enums.ResultStatus
import com.grailslab.gschoolcore.BasePersistentObj
import com.grailslab.library.Book
import com.grailslab.settings.Exam
import com.grailslab.settings.ExamSchedule
import com.grailslab.settings.SubjectName
import com.grailslab.settings.SubjectName

class ExamMark extends BasePersistentObj{
    Exam exam
    Student student
    SubjectName subject
    ExamSchedule examSchedule
    Double ctMark
    Double ctObtainMark
    Double hallWrittenMark      //hallInput1
    Double hallObjectiveMark    //hallInput2
    Double hallPracticalMark    //hallInput3
    Double hallSbaMark          //hallInput4
    Double hallInput5          //hallInput4

    Double hallMark
    Double hallObtainMark
    Double fullMark
    Double tabulationMark    // mark that count on result
    Double tabulationNosbaMark    // mark that count on result
    Boolean isOptional
    Boolean isExtraCurricular
    Integer idxOfSub       // Subject index in tabulation. Need for calculate Group subject results
    Double gPoint       //1
    String lGrade       //F
    ResultStatus resultStatus
    ResultStatus ctResultStatus
    AttendStatus ctAttendStatus=AttendStatus.NO_INPUT
    AttendStatus hallAttendStatus=AttendStatus.NO_INPUT

    //average mark for final tarm
    Double halfYearlyMark
    Double averageMark

    static constraints = {
        student nullable: true
        ctMark nullable: true
        ctObtainMark nullable: true
        halfYearlyMark nullable: true
        averageMark nullable: true
        hallInput5 nullable: true
        hallWrittenMark nullable:true
        hallPracticalMark nullable:true
        hallObjectiveMark nullable:true
        hallSbaMark nullable:true
        hallMark nullable: true
        hallObtainMark nullable: true
        fullMark nullable: true
        tabulationMark nullable: true
        tabulationNosbaMark nullable: true
        gPoint nullable: true
        lGrade nullable: true
        resultStatus nullable: true
        isOptional nullable: true
        isExtraCurricular nullable: true
        idxOfSub nullable: true
        ctResultStatus nullable: true
    }
}
