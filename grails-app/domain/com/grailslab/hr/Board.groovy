package com.grailslab.hr

import com.grailslab.gschoolcore.BasePersistentObj


class Board extends BasePersistentObj{
    String name
    String description

    static constraints = {
        description nullable: true
    }
}
