package com.grailslab.open

import com.grailslab.enums.OpenContentType
import com.grailslab.gschoolcore.BasePersistentObj

class OpenContent extends BasePersistentObj{

    OpenContentType openContentType
    String title
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
