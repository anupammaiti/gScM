package com.grailslab.command.library
/**
 * Created by Hasnat on 6/28/2015.
 */
@grails.validation.Validateable
class BookTopReaderCommand {

    Long bookTopCategory
    Long bookTopAuthor
    Long bookTopPublisher
    String bookTopPrintType
    String bookTopLanguage
    Date fromDate
    Date  toDate

    static constraints = {
        fromDate nullable: true
        toDate nullable: true
    }
}
