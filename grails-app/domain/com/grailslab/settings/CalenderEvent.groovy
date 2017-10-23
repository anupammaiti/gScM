package com.grailslab.settings


import com.grailslab.enums.EventType
import com.grailslab.gschoolcore.BasePersistentObj
import com.grailslab.hr.Employee

class CalenderEvent extends BasePersistentObj{
    Long id

    Holiday holiday
    ExamSchedule examSchedule
    Employee teacher                // for class Schedule

    ClassName className
    Section section         //
    SubjectName subject

    EventType eventType
    String name
    String description
    Date startDate          // date with time
    Date endDate             // date with time
    static constraints = {
        holiday(nullable:true)
        examSchedule(nullable:true)
        teacher(nullable:true)
        description(nullable:true)
        className(nullable:true)
        section(nullable:true)
        subject(nullable:true)
    }
}
