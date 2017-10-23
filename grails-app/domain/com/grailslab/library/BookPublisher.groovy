package com.grailslab.library

import com.grailslab.gschoolcore.BasePersistentObj

class BookPublisher extends BasePersistentObj{
    String name
    String bengaliName
    String address
    String country

    static constraints = {
        bengaliName nullable: true
        address nullable: true
        country nullable: true
    }
}
