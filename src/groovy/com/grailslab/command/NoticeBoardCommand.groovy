package com.grailslab.command

/**
 * Created by Hasnat on 6/27/2015.
 */
@grails.validation.Validateable
class NoticeBoardCommand {
    Long id
    String title
    String details
    String scrollText
    String scrollColor
    Date publish
    Date expire
    Boolean keepBoard
    Boolean keepScroll
    static constraints = {
        id nullable: true
        scrollText nullable: true
        details nullable: true
        keepBoard nullable: true
        keepScroll nullable: true
    }
}
