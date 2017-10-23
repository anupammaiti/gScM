package com.grailslab.open

import com.grailslab.gschoolcore.BasePersistentObj

class OpenGalleryItem extends BasePersistentObj{
    //new implement
    String itemPath
    OpenGallery openGallery
    String name
    String description
    Integer sortOrder=1

    static constraints = {
        description nullable: true
    }
}
