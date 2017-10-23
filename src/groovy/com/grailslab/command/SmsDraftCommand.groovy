package com.grailslab.command

import com.grailslab.enums.SelectionTypes

/**
 * Created by Hasnat on 6/27/2015.
 */
@grails.validation.Validateable
class SmsDraftCommand {
    Long id
    String name
    String message
    static constraints = {
        id nullable: true
        message(nullable: false, size: 1..479)
    }
}
