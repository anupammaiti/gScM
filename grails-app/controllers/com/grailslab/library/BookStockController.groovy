package com.grailslab.library

import com.grailslab.CommonUtils
import com.grailslab.command.AddBooksCommand
import com.grailslab.enums.BookStockStatus
import com.grailslab.gschoolcore.ActiveStatus
import grails.converters.JSON
import org.springframework.dao.DataIntegrityViolationException

class BookStockController {

    def bookStockService
    def libraryService
    def messageSource

    def index() {
        def categoryList=libraryService.allBookCategoryDropDownList()
        def bookStatusList = BookStockStatus.values()
        render(view: '/library/addBook', model: [categoryList: categoryList, bookStatusList:bookStatusList])
    }



    def list() {
        LinkedHashMap gridData
        String result
        LinkedHashMap resultMap = bookStockService.addBooksPaginateList(params)

        if (!resultMap || resultMap.totalCount == 0) {
            gridData = [iTotalRecords: 0, iTotalDisplayRecords: 0, aaData: []]
            result = gridData as JSON
            render result
            return
        }
        int totalCount = resultMap.totalCount
        gridData = [iTotalRecords: totalCount, iTotalDisplayRecords: totalCount, aaData: resultMap.results]
        result = gridData as JSON
        render result
    }

    def save(AddBooksCommand command) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        if (command.hasErrors()) {
            def errorList = command?.errors?.allErrors?.collect { messageSource.getMessage(it, null) }
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, errorList?.join('\n'))
            outPut = result as JSON
            render outPut
            return
        }
        Book upBook
        if (command.id) {
            upBook = Book.get(command.id)
            if (!upBook) {
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
                outPut = result as JSON
                render outPut
                return
            }
            upBook.properties['barcode','source','price','comments','stockDate'] = command.properties
            result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
            result.put(CommonUtils.MESSAGE, 'Data Updated successfully')

            if (upBook.hasErrors() || !upBook.save()) {
                def errorList = upBook?.errors?.allErrors?.collect { messageSource.getMessage(it, null) }
                result.put(CommonUtils.MESSAGE, errorList?.join('\n'))
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            }

        } else {
           // upBook.bookDetails.quantity+=command.quantity;
            Integer quantity = command.quantity
            BookDetails bookDetails = command.bookDetails
            def bookIdList = bookStockService.createBookIdList(quantity, bookDetails)
            Book book = new Book()
            book.properties = command.properties
            book.stockStatus = BookStockStatus.ADDED
            Boolean bookSave = Boolean.FALSE
            if (bookIdList) {
                bookSave = bookStockService.saveBooks(book, bookIdList)
                bookDetails.save()
            }
            if (!bookSave) {
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, 'Book cannot save please try again.')
            } else {
                result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
                result.put(CommonUtils.MESSAGE, 'Book  saved successfully.')
            }
        }
        outPut = result as JSON
        render outPut
    }

    def edit(Long id) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        Book book = Book.read(id)
        if (!book) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put('bookName', book.bookDetails.name)
        result.put(CommonUtils.OBJ, book)
        outPut = result as JSON
        render outPut
    }

    def inactive(Long id) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        Book book = Book.get(id)
        if (!book) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        book.activeStatus = ActiveStatus.INACTIVE
        book.save(flush: true)
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, 'Inactive Successfully')
        outPut = result as JSON
        render outPut
    }

    def delete(Long id) {
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        Book book = Book.get(id)
        if (!book) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
        }
        try {
            book.delete(flush: true)
            result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
            result.put(CommonUtils.MESSAGE, "Data Deleted Successfully.")

        }
        catch (DataIntegrityViolationException e) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "Data already in use. You Can Inactive Reference")
        }
        outPut = result as JSON
        render outPut
    }

}


