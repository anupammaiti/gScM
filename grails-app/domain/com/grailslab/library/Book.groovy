package com.grailslab.library


import com.grailslab.enums.BookStockStatus
import com.grailslab.gschoolcore.BasePersistentObj

class Book extends BasePersistentObj {
    String bookId
    String barcode
    BookDetails bookDetails
    BookStockStatus stockStatus
    String source
    Double price
    String comments
    Date stockDate
    Date lostDate
    static constraints = {
        bookId unique: true
        barcode unique: true, nullable: true
        source nullable: true
        price nullable: true
        comments nullable: true
        lostDate nullable: true
    }
}
