package com.grailslab.command.library
/**
 * Created by Hasnat on 6/28/2015.
 */
@grails.validation.Validateable
class BookHistoryCommand {
    Long bookName
    String bookHistoryPrintType
    Date fromDate
    Date toDate

    static constraints = {
        fromDate nullable: true
    }
}
