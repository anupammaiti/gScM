package com.grailslab.library

import com.grailslab.gschoolcore.BasePersistentObj


class BookCategory extends BasePersistentObj{
    String name
    String description

    static constraints = {
        name unique: true
        description nullable: true
    }
}
