package com.grailslab.library

import com.grailslab.gschoolcore.BasePersistentObj

class LibraryMember extends BasePersistentObj{

    String name
    String address
    String presentAddress
    String memberId
    Date memberShipDate
    String mobile
    String email
    String referenceId


    static constraints = {
        name nullable: true
        address nullable: true
        presentAddress nullable: true
        memberId nullable: true
        memberShipDate nullable: true
        mobile nullable: true
        email nullable: true
        referenceId nullable: true
    }
}
