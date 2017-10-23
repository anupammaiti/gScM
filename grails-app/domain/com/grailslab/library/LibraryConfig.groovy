package com.grailslab.library

import com.grailslab.gschoolcore.BasePersistentObj


class LibraryConfig extends BasePersistentObj{
    String memberType
    Integer numberOfBook
    Integer allowedDays
    Double fineAmount
    Double memberFee

    static constraints = {
         fineAmount nullable: true
         memberFee nullable: true
    }
}
