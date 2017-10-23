package com.grailslab.command

/**
 * Created by aminul on 3/22/2015.
 */
@grails.validation.Validateable
class ClassRoomCommand {
    Long id
    String name
    String description

    static constraints = {
        description nullable: true
        id nullable: true
    }
}
