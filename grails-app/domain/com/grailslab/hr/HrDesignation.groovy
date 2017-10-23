package com.grailslab.hr

import com.grailslab.gschoolcore.BasePersistentObj

class HrDesignation extends BasePersistentObj{
    String name
    HrCategory hrCategory
    Integer sortOrder

    static constraints = {
    }
}
