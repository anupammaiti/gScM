package com.grailslab.settings

import com.grailslab.gschoolcore.AcademicYear


class School implements Serializable {
    String name
    String address
    String email
    String contactNo
    AcademicYear academicYear

    static constraints = {
        academicYear nullable: true
    }
    static mapping = {
        id generator: 'assigned'
        cache true
    }
}
