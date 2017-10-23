package com.grailslab.command

import com.grailslab.enums.GroupName
import com.grailslab.gschoolcore.AcademicYear
import com.grailslab.settings.ClassName
import com.grailslab.settings.ShiftExam

/**
 * Created by aminul on 3/19/2015.
 */
@grails.validation.Validateable
class ListSectionCommand {
    AcademicYear academicYear
    ClassName className
    GroupName groupName
    static constraints = {
        academicYear nullable: true
        className nullable: true
        groupName nullable: true
    }
}

@grails.validation.Validateable
class ListExamClassCommand {
    AcademicYear academicYear
    ShiftExam examName
    ClassName className
    GroupName groupName
    String examType
    static constraints = {
        academicYear nullable: true
        examName nullable: true
        className nullable: true
        examType nullable: true
        groupName nullable: true
    }
}
