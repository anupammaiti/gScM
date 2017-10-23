package com.grailslab.settings


import com.grailslab.enums.GroupName
import com.grailslab.enums.Shift
import com.grailslab.gschoolcore.BasePersistentObj
import com.grailslab.hr.Employee

class Section extends BasePersistentObj{

    String name
    Shift shift
    ClassRoom classRoom
    ClassName className
    GroupName groupName
    Employee employee

    static constraints = {
        classRoom nullable: true
        employee nullable: true
        groupName nullable: true
    }

}
