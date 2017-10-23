package com.grailslab.command

import com.grailslab.enums.Shift

 //ClassPeriodController - save
@grails.validation.Validateable
class ClassPeriodCommand {
    Long id
    Integer sortPosition
    String name
    String startOn      //10 AM
    Integer duration    // 40
    Shift shift
    Boolean isPlayTime =false

    static constraints = {
        id nullable: true
        shift nullable: true
        sortPosition nullable: true
    }
}
