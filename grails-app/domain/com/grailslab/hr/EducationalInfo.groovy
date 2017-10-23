package com.grailslab.hr

import com.grailslab.gschoolcore.BasePersistentObj
import com.grailslab.settings.SubjectName

class EducationalInfo extends BasePersistentObj{

    String name
    Employee employee
    HrCertification certification
    Institution institution
    SubjectName majorSubject
    Board board
    Date passingYear
    String result
    String duration

    static constraints = {
        name nullable: true
        board nullable: true
        passingYear nullable: true
    }
}
