package com.grailslab.open

import com.grailslab.gschoolcore.BasePersistentObj

class OpenSuccessStory extends BasePersistentObj{
    String imagePath
    String name
    String description
    Integer sortOrder=1

    static constraints = {
        description nullable: true
    }
    static mapping = {
        sort sortOrder: "asc" // or "desc"
    }
}
