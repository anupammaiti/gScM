package com.grailslab.library

import com.grailslab.gschoolcore.BasePersistentObj


class Author extends BasePersistentObj{
    String name
    String bengaliName
    String country

    static constraints = {
        bengaliName nullable: true
        country nullable: true
    }
}
