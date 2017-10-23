package com.grailslab.command

import com.grailslab.hr.Employee
import com.grailslab.settings.ClassName
import com.grailslab.settings.ClassPeriod
import com.grailslab.settings.Section
import com.grailslab.settings.SubjectName

/**
 * Created by USER on 18-Jul-17.
 */
@grails.validation.Validateable
class TeacherRoutineCommand {

    Long id
    ClassName className
    Section section
    SubjectName subjectName
    ClassPeriod classPeriod
    Employee employee
    String days


    static constraints = {
        id nullable: true
    }
}
