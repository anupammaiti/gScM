package com.grailslab.command

import com.grailslab.enums.LeavePayType

/**
 * Created by Hasnat on 6/28/2015.
 */
@grails.validation.Validateable
class LeaveNameCommand {
    Long id
    String name             //sick Leave
    Integer numberOfDay     // 15
    LeavePayType payType

    static constraints = {
        id nullable: true
        numberOfDay nullable: true
    }
}
