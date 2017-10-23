package com.grailslab

import grails.transaction.Transactional
import org.codehaus.groovy.grails.web.context.ServletContextHolder as SCH

class CommonService {
    static transactional = false
    def grailsApplication

    List<String> weeklyHolidays(){
        String weeklyHoliday = grailsApplication.config.grailslab.gschool.weekly.holiday
        return Arrays.asList(weeklyHoliday.split("\\s*,\\s*"))
    }

    String getAttendanceCalMethod(){
        String calMethod = grailsApplication.config.grailslab.gschool.attendance.calculation
    }
}
