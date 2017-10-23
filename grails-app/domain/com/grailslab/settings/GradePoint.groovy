package com.grailslab.settings


import com.grailslab.enums.LetterGrade
import com.grailslab.gschoolcore.BasePersistentObj

class GradePoint extends BasePersistentObj{
    Double upToMark    //49
    Double fromMark    //0
    Double gPoint       //1
    LetterGrade lGrade       //F
    String laterGrade       //dubplicate
    String credentials  // Unsuccessful
    ClassName className

    static constraints = {
        className nullable: true
        laterGrade nullable: true
    }
}
