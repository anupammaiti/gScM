package com.grailslab.command.library
/**
 * Created by Hasnat on 6/28/2015.
 */
@grails.validation.Validateable
class BookListCommand {

    Long bookCategory
    Long bookAuthor
    Long bookPublisher
    String bookReportFor
    String bookListPrintType
    String bookLanguage

    static constraints = {
    }
}
