package com.grailslab.stmgmt

import com.grailslab.gschoolcore.BasePersistentObj
import com.grailslab.settings.SubjectName


class PreviousTermMark extends BasePersistentObj{
    Student student
    Double term1Mark
    Double term2Mark
    Double ctMark
    SubjectName subjectName
//    Integer attendDay


    static constraints = {
        term1Mark nullable: true
        term2Mark nullable: true
//        attendDay nullable: true
        ctMark nullable: true
    }
}

