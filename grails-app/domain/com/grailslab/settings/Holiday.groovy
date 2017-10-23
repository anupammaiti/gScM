package com.grailslab.settings


import com.grailslab.enums.EventType
import com.grailslab.enums.RepeatType
import com.grailslab.gschoolcore.BasePersistentObj

class Holiday extends BasePersistentObj{

    String name             // weekly or special(eid,puza)
    Date startDate
    Date endDate
    RepeatType repeatType=RepeatType.NEVER
    String repeatDates     // coma separated sun,mon
    EventType eventType
    static constraints = {
        repeatDates(nullable: true)
    }
}
