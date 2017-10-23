package com.grailslab.command

import com.grailslab.settings.ClassName
import com.grailslab.settings.LessonWeek
import com.grailslab.settings.Section
import com.grailslab.settings.SubjectName

/**
 * Created by Aminul on 2/24/2016.
 */
@grails.validation.Validateable
class FeedbackCommand {
    ClassName fclassName
    Section fSectionId
    SubjectName fSubjectId
    LessonWeek fWeekNo
    String studentIds


    static constraints = {

    }
}
