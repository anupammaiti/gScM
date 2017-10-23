package com.grailslab.accounts

import com.grailslab.gschoolcore.BasePersistentObj

class GlabDetails extends BasePersistentObj {
    String description
    Double rate
    Double qty
    Double amount
    Long paymentId


    static constraints = {
        description nullable: true
        qty nullable: true
        rate nullable: true
        amount nullable: true
        paymentId nullable: true

    }
}
