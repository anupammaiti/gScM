package com.grailslab.command

/**
 * Created by aminul on 10/5/2015.
 */
@grails.validation.Validateable
class HrPeriodCommand {
    Long id
    String name
    String startOn      //10 AM
    Integer duration    // 40
    Integer lateTolerance    // 40

    static constraints = {
        id nullable: true
    }
}
