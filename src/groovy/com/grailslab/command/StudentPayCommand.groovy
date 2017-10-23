package com.grailslab.command

import com.grailslab.gschoolcore.AcademicYear
import com.grailslab.stmgmt.Student

/**
 * Created by Hasnat on 6/27/2015.
 */
@grails.validation.Validateable
class StudentPayCommand {
    String student
    String payStatus
    AcademicYear academicYear
    static constraints = {
        academicYear nullable: true
    }
}
