package com.grailslab.library

import com.grailslab.CommonUtils
import com.grailslab.command.BookDetailsCommand
import com.grailslab.command.LibraryConfigCommand
import com.grailslab.gschoolcore.ActiveStatus
import grails.converters.JSON
import org.springframework.dao.DataIntegrityViolationException

class LibraryController {
    def libraryService
    def schoolService
    def messageSource
    def sectionService
    def springSecurityService

    def index() {
        render (view: '/common/dashboard', layout:'moduleLibraryLayout')
    }


    def bookDetail() {
        def categoryList=libraryService.allBookCategoryDropDownList()
        render(view: 'bookDetail', model: [categoryList:categoryList])
    }



    def bookDetailList() {
        LinkedHashMap gridData
        String result
        LinkedHashMap resultMap = libraryService.bookDetailPaginateList(params)

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



    def bookDetailSave(BookDetailsCommand command) {
        if (!request.method.equals('POST')) {
            redirect(action: 'book')
            return
        }
        println params
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        if (command.hasErrors()) {
            def errorList = command?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, errorList?.join('\n'))
            outPut=result as JSON
            render outPut
            return
        }
        BookDetails book
        String message
        if (command.id) {
            book = BookDetails.get(command.id)
            if (!book) {
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
                outPut=result as JSON
                render outPut
                return
            }

            book.properties = command.properties
            book.save(flush: true)
            result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
            message ='BookDetails Updated successfully'
        } else {
            def bookDetailList=BookDetails.findAllByAuthorAndName(command.author, command.name)
            if (bookDetailList) {
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, 'Book Already Exist.')
                outPut=result as JSON
                render outPut
                return
            }
            book = new BookDetails(command.properties)
            result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
            message ='BookDetails Added successfully'
        }

        if(book.hasErrors() || !book.save()){
            def errorList = book?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            message = errorList?.join('\n')
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
        }
        result.put(CommonUtils.MESSAGE,message)
        outPut=result as JSON
        render outPut
    }




    def bookDetailEdit(Long id) {
        if (!request.method.equals('POST')) {
            redirect(action: 'book')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        BookDetails bookDetails = BookDetails.read(id)
        if (!bookDetails) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put('bookAuthor', bookDetails.author.name)
        result.put('bookPublisher', bookDetails.bookPublisher?.name)
        result.put(CommonUtils.OBJ, bookDetails)
        outPut = result as JSON
        render outPut
    }





    def bookDetailInactive(Long id) {
        if (!request.method.equals('POST')) {
            redirect(action: 'book')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        BookDetails bookDetails = BookDetails.get(id)
        if (!bookDetails) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        bookDetails.activeStatus=ActiveStatus.INACTIVE
        bookDetails.save(flush: true)
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, 'Delete Successfully')
        outPut = result as JSON
        render outPut
    }



    def categoryDelete(Long id) {
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        BookCategory categoryDel = BookCategory.get(id)
        BookCategory categoryUp
        if (!categoryDel) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
        }
        try {
            categoryDel.delete(flush: true)
            result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
            result.put(CommonUtils.MESSAGE, "BookDetails Category Deleted Successfully.")

        }
        catch (DataIntegrityViolationException e) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "Category already in use. You Can Inactive Or Delete Reference.")
        }
        outPut = result as JSON
        render outPut
    }

    def authorDelete(Long id) {
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        Author author = Author.get(id)
        if (!author) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
        }
        try {
            author.delete(flush: true)
            result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
            result.put(CommonUtils.MESSAGE, "Author Deleted Successfully.")

        }
        catch (DataIntegrityViolationException e) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "Author in use. You Can Inactive Or Delete Reference.")
        }
        outPut = result as JSON
        render outPut
    }
}


