package com.grailslab.hr

import com.grailslab.gschoolcore.BasePersistentObj

class HrPeriod extends BasePersistentObj{
    String name
    String periodRange
    String startOn      //10 AM
    Integer duration    // 40
    Integer lateTolerance    // 40
    Date startTime      //dummy date with calculated start time

    static constraints = {
        periodRange nullable: true
    }
    static mapping = {
        cache true
    }
}
