package com.grailslab.library

import com.grailslab.gschoolcore.BasePersistentObj


class BookDetails extends BasePersistentObj{
    BookCategory category
    String name
    String title
    Author author
    BookPublisher bookPublisher
    String addAuthor
    String language
    Integer lostDamage = 0

    static constraints = {
        title nullable: true
        addAuthor nullable: true
        author nullable: true
        lostDamage nullable: true
        bookPublisher nullable: true

    }
}
