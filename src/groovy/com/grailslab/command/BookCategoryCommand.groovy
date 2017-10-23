package com.grailslab.command

/**
 * Created by Hasnat on 6/28/2015.
 */
@grails.validation.Validateable
class BookCategoryCommand {
    Long id
    String name
    String description

    static constraints = {
        id nullable: true
        description nullable: true
    }
}
