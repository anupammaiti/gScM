package com.grailslab.settings

import com.grailslab.gschoolcore.BasePersistentObj
import com.grailslab.hr.Employee

class ClassRoutine extends BasePersistentObj{
    ClassName className
    Section section
    SubjectName subjectName
    ClassPeriod classPeriod
    Employee employee
    String days

    static constraints = {
    }
}
