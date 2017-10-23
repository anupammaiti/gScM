package com.grailslab.command

import com.grailslab.enums.YearMonths
import com.grailslab.gschoolcore.AcademicYear
import com.grailslab.hr.HrCategory

/**
 * Created by Hasnat on 6/27/2015.
 */
@grails.validation.Validateable
class SalAttendanceReportCommand {
    YearMonths yearMonths
    AcademicYear academicYear
    HrCategory hrCategory

    static constraints = {
        hrCategory nullable: true
        academicYear nullable: true
        yearMonths nullable: true
    }
}
