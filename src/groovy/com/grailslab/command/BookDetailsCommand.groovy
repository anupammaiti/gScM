package com.grailslab.command

import com.grailslab.library.Author
import com.grailslab.library.BookCategory
import com.grailslab.library.BookPublisher

/**
 * Created by Hasnat on 6/28/2015.
 */
@grails.validation.Validateable
class BookDetailsCommand {
    Long id
    BookCategory category
    String name
    String title
    BookPublisher bookPublisher
    Author author
    String addAuthor
    String language

    static constraints = {
        id nullable: true
        title nullable: true
        addAuthor nullable: true
        bookPublisher nullable: true
        category nullable: true
    }
}
