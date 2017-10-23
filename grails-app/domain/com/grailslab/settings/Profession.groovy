package com.grailslab.settings

import com.grailslab.gschoolcore.BasePersistentObj


class Profession extends  BasePersistentObj{

    String name
    String description

    static constraints = {
        description nullable: true
    }
}
