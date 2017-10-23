package com.grailslab.hr

import com.grailslab.gschoolcore.BasePersistentObj


class EmploymentHistory extends BasePersistentObj{

    Employee employee
    String company
    String jobTitle
    Date joiningDate
    Date endDate
    String location
    static constraints = {
        joiningDate nullable: true
        endDate nullable: true
        location nullable: true
    }
}
