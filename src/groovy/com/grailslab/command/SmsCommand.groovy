package com.grailslab.command

import com.grailslab.enums.SelectionTypes

/**
 * Created by Hasnat on 6/27/2015.
 */
@grails.validation.Validateable
class SmsCommand {
    String message
    String selectIds
    static constraints = {
        message(nullable: false, size: 1..479)
    }
}
