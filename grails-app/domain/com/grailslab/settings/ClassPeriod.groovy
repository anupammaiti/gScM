package com.grailslab.settings


import com.grailslab.enums.Shift
import com.grailslab.gschoolcore.BasePersistentObj

class ClassPeriod extends BasePersistentObj{
    String name
    Integer sortPosition      //1, 2 3 ... must be unique
    String startOn      //10 AM
    Integer duration    // 40
    Shift shift
    Boolean isPlayTime = false


    //calculated fields
    Date startTime
    String period       // 10 AM - 10.40 AM

    static constraints = {
    }
}
