package com.grailslab.messaging

import com.grailslab.gschoolcore.BasePersistentObj

class SmsDraft extends BasePersistentObj{
    Integer numOfSms
    String name
    String message

    static constraints = {
        message(nullable: false, size: 1..479)
    }
}
