package com.grailslab.open

import com.grailslab.enums.OpenContentType
import com.grailslab.gschoolcore.BasePersistentObj

class OpenGallery extends BasePersistentObj{
    // new implement
    String name
    String description
    Integer sortOrder=1
    OpenContentType galleryType     // Video, Picture

    static constraints = {
        description nullable: true
    }
}
