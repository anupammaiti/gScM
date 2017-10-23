package com.grailslab.command

import com.grailslab.gschoolcore.AcademicYear
import com.grailslab.settings.ClassName

/**
 * Created by aminul on 3/19/2015.
 */
@grails.validation.Validateable
class LoadReAdmissionCommand {
    AcademicYear academicYear
    ClassName className
}
