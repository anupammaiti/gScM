package com.grailslab.command

/**
 * Created by aminul on 5/31/2015.
 */
@grails.validation.Validateable
class LessonWeekCommand {
    Long id
    Integer weekNumber
    Date startDate
    Date endDate
    static constraints = {
        id nullable: true
    }
}
