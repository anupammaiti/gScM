package com.grailslab.command

import com.grailslab.library.Author
import com.grailslab.library.BookCategory
import com.grailslab.library.BookDetails

/**
 * Created by Hasnat on 6/28/2015.
 */
@grails.validation.Validateable
class BookTransactionCommand {
    Author author
    BookCategory bookCategory
    BookDetails bookDetails
    String publisher
    String addAuthor
    String language


    static constraints = {
    }

}
