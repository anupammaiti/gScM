package com.grailslab.salary

import com.grailslab.enums.YearMonths
import com.grailslab.gschoolcore.BasePersistentObj
import com.grailslab.hr.Employee

class SalAttendance extends BasePersistentObj{
    YearMonths yearMonths
    Integer workingDays
    Integer holidays
    Employee employee
    Integer presentDays
    Integer lateDays
    Integer absentDays
    Integer leaveDays

    static constraints = {

        yearMonths nullable: true
        workingDays nullable: true
        holidays nullable: true
        presentDays nullable: true
        lateDays nullable: true
        absentDays nullable: true
        leaveDays nullable: true
    }
}
