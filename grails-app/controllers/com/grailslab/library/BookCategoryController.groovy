package com.grailslab.library

import com.grailslab.CommonUtils
import com.grailslab.command.BookCategoryCommand
import com.grailslab.gschoolcore.ActiveStatus
import grails.converters.JSON

class BookCategoryController {

    def categoryService
    def messageSource

    def index() {
        render(view: '/library/category')
    }
    def list() {
        LinkedHashMap gridData
        String result
        LinkedHashMap resultMap = categoryService.categoryPaginateList(params)

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
    def save(BookCategoryCommand command) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
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
        BookCategory category
        String message
        if (command.id) {
            category = BookCategory.get(command.id)
            if (!category) {
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
                outPut=result as JSON
                render outPut
                return
            }
            category.properties = command.properties
            result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
            message ='BookDetails Category Updated successfully'
        } else {
            category = new BookCategory(command.properties)
            result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
            message ='BookDetails Category Added successfully'
        }

        if(category.hasErrors() || !category.save()){
            def errorList = category?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            message = errorList?.join('\n')
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
        }
        result.put(CommonUtils.MESSAGE,message)
        outPut=result as JSON
        render outPut
    }
    def edit(Long id) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        BookCategory category = BookCategory.read(id)
        if (!category) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.OBJ, category)
        outPut = result as JSON
        render outPut
    }
    def delete(Long id) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        BookCategory category = BookCategory.get(id)
        if (!category) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        category.activeStatus=ActiveStatus.INACTIVE
        category.save(flush: true)
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, 'Category deleted successfully')
        outPut = result as JSON
        render outPut
    }
}
