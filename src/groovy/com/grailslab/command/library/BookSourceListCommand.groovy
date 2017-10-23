package com.grailslab.command.library
/**
 * Created by Hasnat on 6/28/2015.
 */
@grails.validation.Validateable
class BookSourceListCommand {

    Long bookSourceCategory
    Long bookSourceAuthor
    Long bookSourcePublisher
    String bookSourceReportFor
    String bookSource
    String bookSourceListPrintType
    String bookSourceLanguage

    static constraints = {
    }
}
