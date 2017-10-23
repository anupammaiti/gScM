package com.grailslab.open


import com.grailslab.enums.OpenCont
import com.grailslab.gschoolcore.BasePersistentObj

class FaqCategory extends BasePersistentObj{

    String name
    OpenCont openCont
    Integer sortPosition=1
    static hasMany = [faqQuestion: FaqQuestion]
    static constraints = {
    }
}
