package com.grailslab.open

import com.grailslab.gschoolcore.BasePersistentObj

class OpenSliderImage extends BasePersistentObj{
    String imagePath
    Integer sortIndex=1
    String title
    String description

    static constraints = {
        description nullable: true
    }
    static mapping = {
        sort sortIndex: "asc" // or "desc"
    }
}
