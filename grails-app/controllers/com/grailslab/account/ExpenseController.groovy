package com.grailslab.account

import com.grailslab.CommonUtils
import com.grailslab.accounting.ChartOfAccount
import com.grailslab.accounting.Invoice
import com.grailslab.accounting.InvoiceDetails
import com.grailslab.enums.AccountType
import com.grailslab.gschoolcore.ActiveStatus
import grails.converters.JSON

class ExpenseController {

    def expenseService
    def chartOfAccountService
    def messageSource

    def index() {
        LinkedHashMap resultMap = expenseService.invoicePaginateList(params)
        def accountList = chartOfAccountService.accountDropDownList(AccountType.EXPENSE)

        if (!resultMap || resultMap.totalCount == 0) {
            render(view: '/collection/expense/addExpense', model: [dataReturn: null, totalCount: 0, accountList: accountList])
            return
        }
        int totalCount = resultMap.totalCount
        render(view: '/collection/expense/addExpense', model: [dataReturn: resultMap.results, totalCount: totalCount, accountList: accountList])
    }

    def save() {

        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut

        Boolean isSave = Boolean.FALSE
        List<InvoiceDetails> detailsList = new ArrayList<>()
        InvoiceDetails details
        Invoice invoice

        ChartOfAccount account
        Integer quantity
        Double unitPrice
        Double price

        print(params)

        if (!params.totalPrice) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, 'Please Fill Form Correctly.')
            outPut = result as JSON
            render outPut
            return
        }

        String invoiceNoTemp = Invoice?.last()?.invoiceNo
        if (!invoiceNoTemp) {
            invoiceNoTemp = '1000000'
        } else {
            Integer invNo = Integer.parseInt(invoiceNoTemp) + 1
            invoiceNoTemp = invNo.toString()
        }

        invoice = new Invoice(invoiceNo: invoiceNoTemp, amount: Double.parseDouble(params.totalPrice), invoiceDate: new Date(), accountType: AccountType.EXPENSE)
        params.quantityVal.each { key, val ->
            details = new InvoiceDetails(account: ChartOfAccount.get(Long.parseLong(params.accountVal."${key}")), quantity: Integer.parseInt(params.quantityVal."${key}"), unitPrice: Double.parseDouble(params.unitePriceVal."${key}"), price: Double.parseDouble(params.price."${key}"), invoice: invoice)
            detailsList.add(details)
        }
        isSave = expenseService.saveInvoice(detailsList, invoice)
        if (isSave == Boolean.FALSE) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, 'Invoice Save Failed.')
            outPut = result as JSON
            render outPut
            return
        }
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, 'Invoice Save Successfully.')
        outPut = result as JSON
        render outPut
    }

    def list() {
        LinkedHashMap gridData
        String result
        LinkedHashMap resultMap = expenseService.invoicePaginateList(params)

        if(!resultMap || resultMap.totalCount== 0){
            gridData = [iTotalRecords: 0, iTotalDisplayRecords: 0, aaData: []]
            result = gridData as JSON
            render result
            return
        }
        int totalCount =resultMap.totalCount
        gridData = [iTotalRecords: totalCount, iTotalDisplayRecords: totalCount, aaData: resultMap.results]
        result = gridData as JSON
        render result
    }

    def edit(Long id) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        Invoice invoice = Invoice.read(id)
        if (!invoice) {
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
        result.put(CommonUtils.OBJ,invoice)
        outPut = result as JSON
        render outPut
    }

    def changeStatus(Long id) {
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        Invoice invoice = Invoice.get(id)
        if(invoice) {
            invoice.activeStatus = ActiveStatus.INACTIVE
            invoice.save()
            result.put(CommonUtils.IS_ERROR, false)
            result.put(CommonUtils.MESSAGE, "Invoice Deleted successfully.")
            outPut = result as JSON
            render outPut
            return
        }
        result.put(CommonUtils.IS_ERROR,true)
        result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_ERROR_MESSAGE)
        outPut = result as JSON
        render outPut
        return
    }

    def view(Long id) {
        LinkedHashMap resultMap = expenseService.invoicePaginateList(params)
        def accountList = chartOfAccountService.accountDropDownList(AccountType.EXPENSE)

        if (!resultMap || resultMap.totalCount == 0) {
            render(view: '/collection/expense/viewExpense', model: [dataReturn: null, totalCount: 0, accountList: accountList])
            return
        }
        int totalCount = resultMap.totalCount
        render(view: '/collection/expense/viewExpense', model: [dataReturn: resultMap.results, totalCount: totalCount, accountList: accountList])
    }
}