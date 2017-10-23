package com.grailslab.stmgmt

import com.grailslab.gschoolcore.BasePersistentObj
import com.grailslab.settings.SubjectName

class LessonFeedbackDetail extends BasePersistentObj{
    LessonFeedback feedback
    Student student
    SubjectName subjectName
    Double rating
    String comment

    static constraints = {
        comment nullable: true
    }
}
