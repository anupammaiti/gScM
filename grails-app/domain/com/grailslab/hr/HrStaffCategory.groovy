package com.grailslab.hr

import com.grailslab.gschoolcore.BasePersistentObj

class HrStaffCategory extends BasePersistentObj{
    String keyName
    String name
    String description
    Integer sortOrder

    static constraints = {
        keyName unique: true
    }
    static mapping = {
        description type: 'text'
    }
}
