package com.grailslab.account

import com.grailslab.CommonUtils
import com.grailslab.accounting.InvoiceDay
import com.grailslab.command.InvoiceDayCommand
import com.grailslab.enums.InvoiceDayType
import com.grailslab.gschoolcore.ActiveStatus
import grails.converters.JSON

class InvoiceDayController {
    def messageSource
    def invoiceDayService

    def index() {
        LinkedHashMap resultMap = invoiceDayService.paginateList(params)
        if (!resultMap || resultMap.totalCount == 0) {
            render(view: '/collection/invoiceDay/invoiceDay', model: [dataReturn: null, totalCount: 0])
            return
        }
        int totalCount = resultMap.totalCount
        render(view: '/collection/invoiceDay/invoiceDay', model: [dataReturn: resultMap.results, totalCount: totalCount])
    }

    def list() {
        LinkedHashMap gridData
        String result
        LinkedHashMap resultMap = invoiceDayService.paginateList(params)

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

    def save(InvoiceDayCommand command) {
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
        InvoiceDay invoiceDay
        String message
        if (command.id) {
            invoiceDay = InvoiceDay.get(command.id)
            if (!invoiceDay) {
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
                outPut = result as JSON
                render outPut
                return
            }
            invoiceDay.properties = command.properties
            result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
            message = 'Invoice Date Updated successfully'
        } else {
            invoiceDay = new InvoiceDay(command.properties)
            result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
            message = 'Invoice Date Added successfully'
        }
        if (invoiceDay.hasErrors() || !invoiceDay.save()) {
            def errorList = invoiceDay?.errors?.allErrors?.collect { messageSource.getMessage(it, null) }
            result.put(CommonUtils.MESSAGE, errorList?.join('\n'))
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            return
        }
        result.put(CommonUtils.MESSAGE, message)
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
        InvoiceDay invoiceDay = InvoiceDay.read(id)
        if (!invoiceDay) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.OBJ, invoiceDay)
        outPut = result as JSON
        render outPut
    }

    def reOpen(Long id) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        InvoiceDay invoiceDay = InvoiceDay.get(id)
        if (!invoiceDay) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        String message = "Can't process your action. Please contact to admin"
        if(invoiceDay.invoiceDayType.equals(InvoiceDayType.OPEN)){
            //logic to close an Open Day
            invoiceDay.invoiceDayType=InvoiceDayType.CLOSED
            message = "Invoice Day CLOSED Successfully"
        } else if (invoiceDay.invoiceDayType.equals(InvoiceDayType.REOPEN)){
            //logic to close an Reopen Day
            invoiceDay.invoiceDayType=InvoiceDayType.CLOSED
            message = "Invoice Day CLOSED Successfully"
        } else if (invoiceDay.invoiceDayType.equals(InvoiceDayType.CLOSED)){
            //logic to reopen an closed Day
            invoiceDay.invoiceDayType=InvoiceDayType.REOPEN
            message = "Invoice Day REOPEN Successfully"
        }

        invoiceDay.save()
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, message)
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
        InvoiceDay invoiceDay = InvoiceDay.get(id)
        if (!invoiceDay) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }

        if(invoiceDay.activeStatus.equals(ActiveStatus.INACTIVE)){
            invoiceDay.activeStatus=ActiveStatus.ACTIVE
        }else {
            invoiceDay.activeStatus=ActiveStatus.INACTIVE
        }
        invoiceDay.save()
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, 'Invoice Date Deleted Successfully')
        outPut = result as JSON
        render outPut
    }
}
