package com.grailslab.command

import com.grailslab.enums.ExamTerm
import com.grailslab.enums.Shift

/**
 * Created by Hasnat on 6/27/2015.
 */
@grails.validation.Validateable
class ExamCommand {
    Long id
    String examName
    Boolean isCtExam
    Boolean isHallExam
    ExamTerm examTerm       //old
    Integer weightOnResult
    String classIds
//    Shift shift         //need to remove or nulll

    Date resultPublishOn            //possible result publish date when create exam

    Date periodStart
    Date periodEnd

    static constraints = {
        id nullable: true
        isCtExam nullable: true
        isHallExam nullable: true
        weightOnResult nullable: true
        resultPublishOn nullable: true
        periodStart nullable: true
        periodEnd nullable: true
    }
}
