package com.grailslab.command

/**
 * Created by Hasnat on 6/27/2015.
 */
@grails.validation.Validateable
class CommonCommand {
    Long id
    String name
    String description

    static constraints = {
        id nullable: true
        description nullable: true
    }
}
