package com.grailslab.open

import com.grailslab.gschoolcore.BasePersistentObj

class OpenTimeLine extends BasePersistentObj{
    String title
    String name
    String iconClass
    String description
    Integer sortOrder=1
    static constraints = {
        description nullable: true
        iconClass nullable: true
    }

    static mapping = {
        description type: 'text'
    }
}
