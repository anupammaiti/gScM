package com.grailslab.settings


import com.grailslab.enums.ExamStatus
import com.grailslab.enums.ExamTerm
import com.grailslab.enums.Shift
import com.grailslab.gschoolcore.BasePersistentObj

class ShiftExam extends BasePersistentObj{
    String examName
    Boolean isCtExam = false
    Boolean isHallExam = false
    ExamTerm examTerm       //old
    Integer weightOnResult
    String classIds
    Shift shift         //need to remove or nulll

    String sectionIds
    ExamStatus examStatus = ExamStatus.NEW
    Date resultPublishOn            //possible result publish date when create exam
    Date resultPreparedDate
    Date publishedDate
    Date periodStart
    Date periodEnd

    static constraints = {
        examName nullable: true
        isCtExam nullable: true
        isHallExam nullable: true
        weightOnResult nullable: true
        classIds nullable: true
        resultPreparedDate nullable: true
        publishedDate nullable: true
        shift nullable: true
        sectionIds nullable: true
        periodStart nullable: true
        periodEnd nullable: true
    }

    String toString(){
        return examName
    }
}
