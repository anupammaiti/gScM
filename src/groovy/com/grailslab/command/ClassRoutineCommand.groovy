package com.grailslab.command

import com.grailslab.gschoolcore.AcademicYear
import com.grailslab.hr.Employee
import com.grailslab.settings.ClassName
import com.grailslab.settings.ClassPeriod
import com.grailslab.settings.Section
import com.grailslab.settings.SubjectName

/**
 * Created by USER on 12-Jul-17.
 */
@grails.validation.Validateable
class ClassRoutineCommand {
    Long id
    ClassName className
    Section section
    SubjectName subjectName
    ClassPeriod classPeriod
    Employee employee
    String days
    AcademicYear academicYear


    static constraints = {
        id nullable: true
    }
}
