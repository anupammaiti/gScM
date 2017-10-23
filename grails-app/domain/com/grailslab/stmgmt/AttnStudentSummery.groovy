package com.grailslab.stmgmt

import com.grailslab.gschoolcore.BasePersistentObj

class AttnStudentSummery extends BasePersistentObj {
    Student student
    Integer term1attenDay
    Integer term2attenDay
    Integer attendDay   //final exam attendance day

    static constraints = {
        term1attenDay nullable: true
        term2attenDay nullable: true
        attendDay nullable: true
    }
}
