package com.grailslab.stmgmt

import com.grailslab.gschoolcore.BasePersistentObj
import com.grailslab.settings.ClassName
import com.grailslab.settings.LessonWeek
import com.grailslab.settings.Section
import com.grailslab.settings.SubjectName

class LessonFeedback extends BasePersistentObj{
    ClassName className
    Section section
    LessonWeek lessonWeek
    Integer numOfSubject

}
