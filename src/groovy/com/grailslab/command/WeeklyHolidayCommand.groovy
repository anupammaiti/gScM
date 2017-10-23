package com.grailslab.command

/**
 * Created by Hasnat on 6/27/2015.
 */
@grails.validation.Validateable
class WeeklyHolidayCommand {
    Long holidayId
    String repeatDates
    static constraints = {
        holidayId nullable: true
    }
}
