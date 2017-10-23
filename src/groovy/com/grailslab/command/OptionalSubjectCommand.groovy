package com.grailslab.command

import com.grailslab.gschoolcore.AcademicYear
import com.grailslab.settings.Section
import com.grailslab.stmgmt.Student

/**
 * Created by aminul on 3/19/2015.
 */
@grails.validation.Validateable
class OptionalSubjectCommand {
    Section section
    AcademicYear academicYear
    Student student
}
