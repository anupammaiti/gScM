package com.grailslab.command.library
/**
 * Created by Hasnat on 6/28/2015.
 */
@grails.validation.Validateable
class LibraryBookTransactionCommand {

    Long transactionCategory
    Long transactionAuthor
    Long transactionPublisher
    String bookTransactionFor
    String transactionPrintType
    String transactionLanguage

    static constraints = {
    }
}
