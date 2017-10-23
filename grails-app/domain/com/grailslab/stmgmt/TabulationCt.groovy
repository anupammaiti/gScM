package com.grailslab.stmgmt

import com.grailslab.enums.ExamTerm
import com.grailslab.enums.ResultStatus
import com.grailslab.settings.ClassName
import com.grailslab.settings.Exam
import com.grailslab.settings.Section

class TabulationCt {
    ClassName className
    Section section
    Exam exam
    ExamTerm examTerm
    Student student

    Double subject0Mark =0
    Double subject1Mark =0
    Double subject2Mark =0
    Double subject3Mark =0
    Double subject4Mark =0
    Double subject5Mark =0
    Double subject6Mark =0
    Double subject7Mark =0
    Double subject8Mark =0
    Double subject9Mark =0
    Double subject10Mark =0
    Double subject11Mark =0
    Double subject12Mark =0
    Double subject13Mark =0
    Double subject14Mark =0
    Double subject15Mark =0
    Double subject16Mark =0
    Double subject17Mark =0
    Double subject18Mark =0
    Double subject19Mark =0
    Double subject20Mark =0
    Double subject21Mark =0
    Double subject22Mark =0

    Double totalObtainMark =0

    Integer positionInSection=0
    String sectionStrPosition
    Integer positionInClass=0
    String classStrPosition
    Integer failedSubCounter=0
    Double gPoint       //1
    String lGrade       //F
    ResultStatus resultStatus

    static constraints = {

        subject0Mark nullable: true
        subject1Mark nullable: true
        subject2Mark nullable: true
        subject3Mark nullable: true
        subject4Mark nullable: true
        subject5Mark nullable: true
        subject6Mark nullable: true
        subject7Mark nullable: true
        subject8Mark nullable: true
        subject9Mark nullable: true
        subject10Mark nullable: true
        subject11Mark nullable: true
        subject12Mark nullable: true
        subject13Mark nullable: true
        subject14Mark nullable: true
        subject14Mark nullable: true
        subject16Mark nullable: true
        subject17Mark nullable: true
        subject18Mark nullable: true
        subject19Mark nullable: true
        subject20Mark nullable: true
        subject21Mark nullable: true
        subject22Mark nullable: true



        positionInSection nullable: true
        sectionStrPosition nullable: true
        positionInClass nullable: true
        classStrPosition nullable: true
        failedSubCounter nullable: true
        gPoint nullable: true
        lGrade nullable: true
        resultStatus nullable: true
        totalObtainMark nullable: true






    }
}
