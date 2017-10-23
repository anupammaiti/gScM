package com.grailslab.accounts

import com.grailslab.gschoolcore.ActiveStatus

class TransactionCode {
    String name
    String type
    Integer startForm

    ActiveStatus activeStatus = ActiveStatus.ACTIVE

    static constraints = {

    }
}
