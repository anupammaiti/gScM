package com.grailslab.library

import com.grailslab.CommonUtils
import com.grailslab.command.BookPublisherCommand
import com.grailslab.gschoolcore.ActiveStatus
import grails.converters.JSON

class BookPublisherController {
    def publisherService
    def messageSource

    def index() {
        render(view: '/library/publisher')
    }
    def list() {
        LinkedHashMap gridData
        String result
        LinkedHashMap resultMap = publisherService.publisherList(params)

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
    def save(BookPublisherCommand command) {
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
        BookPublisher publisher
        String message
        if (command.id) {
            publisher = BookPublisher.get(command.id)
            if (!publisher) {
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
                outPut=result as JSON
                render outPut
                return
            }
            publisher.properties = command.properties
            result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
            message ='BookDetails Publisher Updated successfully'
        } else {
            publisher = new BookPublisher(command.properties)
            result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
            message ='BookDetails Publisher Added successfully'
        }

        if(publisher.hasErrors() || !publisher.save()){
            def errorList = publisher?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
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
        BookPublisher publisher = BookPublisher.read(id)
        if (!publisher) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.OBJ, publisher)
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
        BookPublisher publisher = BookPublisher.get(id)
        if (!publisher) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        publisher.activeStatus=ActiveStatus.INACTIVE
        publisher.save()
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, 'Publisher deleted successfully')
        outPut = result as JSON
        render outPut
    }

}
