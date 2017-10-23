package com.grailslab.open

import com.grailslab.enums.OpenContentType
import com.grailslab.gschoolcore.BasePersistentObj

class OpenMgmtVoice extends BasePersistentObj{
    OpenContentType openContentType
    String title
    String name
    String iconClass
    String imagePath
    String description
    Integer sortOrder=1
    static constraints = {
        description nullable: true
        iconClass nullable: true
        imagePath nullable: true
    }

    static mapping = {
        description type: 'text'
    }
}
