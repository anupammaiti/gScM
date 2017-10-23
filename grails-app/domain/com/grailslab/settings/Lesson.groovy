package com.grailslab.settings


import com.grailslab.enums.ExamTerm
import com.grailslab.gschoolcore.BasePersistentObj

class Lesson extends  BasePersistentObj {

    Integer weekNumber
    Date weekStartDate
    Date weekEndDate
    ExamTerm examTerm
    Section section
    Date lessonDate
    static hasMany = [lessonDetails:LessonDetails]

    static constraints = {
        examTerm nullable: true
    }
}
