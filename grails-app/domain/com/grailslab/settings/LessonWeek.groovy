package com.grailslab.settings

import com.grailslab.gschoolcore.BasePersistentObj


class LessonWeek extends BasePersistentObj{
    Integer weekNumber
    Date startDate
    Date endDate

    static constraints = {
    }
}
