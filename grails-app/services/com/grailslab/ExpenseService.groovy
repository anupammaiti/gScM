package com.grailslab

import com.grailslab.accounting.Invoice
import com.grailslab.accounting.InvoiceDetails
import com.grailslab.enums.AccountType
import com.grailslab.gschoolcore.ActiveStatus
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

class ExpenseService {
    static transactional = false

    static final String[] sortColumns = ['invoiceNo']

    LinkedHashMap invoicePaginateList(GrailsParameterMap params) {
        int iDisplayStart = params.iDisplayStart ? params.getInt('iDisplayStart') : CommonUtils.DEFAULT_PAGINATION_START
        int iDisplayLength = params.iDisplayLength ? params.getInt('iDisplayLength') : CommonUtils.DEFAULT_PAGINATION_LENGTH
        String sSortDir = params.sSortDir_0 ? params.sSortDir_0 : CommonUtils.SORT_ORDER_ASC
        int iSortingCol = params.iSortCol_0 ? params.getInt('iSortCol_0') : CommonUtils.DEFAULT_PAGINATION_SORT_IDX
        //Search string, use or logic to all fields that required to include
        String sSearch = params.sSearch ? params.sSearch : null
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }
        String sortColumn = CommonUtils.getSortColumn(sortColumns, iSortingCol)
        List dataReturns = new ArrayList()
        def c = Invoice.createCriteria()
        def results = c.list(max: iDisplayLength, offset: iDisplayStart) {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
                eq("accountType", AccountType.EXPENSE)
            }
            if (sSearch) {
                or {
                    ilike('invoiceNo', sSearch)
                }
            }
            order(sortColumn, sSortDir)
        }
        int totalCount = results.totalCount
        if (totalCount > 0) {
            results.each { Invoice invoice ->
                dataReturns.add([DT_RowId: invoice.id, 0: invoice.invoiceNo, 1: invoice?.accountType?.value, 2: invoice.amount, 3: invoice.invoiceDate.dateString, 4: invoice.paid, 5: ''])
            }
        }
        return [totalCount: totalCount, results: dataReturns]
    }

    Boolean saveInvoice(List<InvoiceDetails> detailsList, Invoice invoice) {
        if (invoice.save()) {
            detailsList.each { InvoiceDetails details ->
                details.save()
            }
        }
        return Boolean.TRUE
    }
}
