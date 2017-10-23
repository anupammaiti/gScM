package com.grailslab.hr

import com.grailslab.enums.HrKeyType
import com.grailslab.gschoolcore.BasePersistentObj

class HrCategory extends BasePersistentObj {
    String name
    HrKeyType hrKeyType
    Integer sortOrder

    static constraints = {
    }
}
