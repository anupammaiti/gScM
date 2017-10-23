package com.grailslab.accounts

import com.grailslab.gschoolcore.ActiveStatus

class GlDetails {
    String xvoucher
    String xacc
    String xsub
    String xproject
    String xcur
    Double xexch
    Double xprime
    Double xbase
    String xremarks
    String xpayType
    String xcheque
    Date xchequeDate
    ActiveStatus activeStatus = ActiveStatus.ACTIVE

    static constraints = {
        xremarks nullable: true
        xsub nullable: true
        xcheque nullable: true
        xchequeDate nullable: true
        xproject nullable: true
    }
}