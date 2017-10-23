package com.grailslab.settings


import com.grailslab.enums.ExamType
import com.grailslab.gschoolcore.BasePersistentObj

class ExamPeriod extends BasePersistentObj{
    ExamType examType
    String name
    String startOn      //10 AM
    Integer duration    // 40
    Date startTime      //dummy date with calculated start time

    static constraints = {
    }
}
