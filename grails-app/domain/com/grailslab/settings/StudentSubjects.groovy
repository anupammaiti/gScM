package com.grailslab.settings

import com.grailslab.enums.SubjectType
import com.grailslab.gschoolcore.BasePersistentObj
import com.grailslab.stmgmt.Student

class StudentSubjects extends BasePersistentObj {
    Student student
    SubjectName subject
    SubjectType subjectType
    Boolean isOptional = false
    Long parentSubId     //in case of alternative, parent class subject id

    static constraints = {
        parentSubId nullable: true
    }
}
