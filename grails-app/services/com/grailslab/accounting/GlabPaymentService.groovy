package com.grailslab.accounting

import com.grailslab.CommonUtils
import com.grailslab.accounts.GlabDetails
import com.grailslab.accounts.GlabPayment
import com.grailslab.gschoolcore.ActiveStatus
import grails.converters.JSON
import grails.transaction.Transactional
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.json.JSONArray
import org.json.JSONObject

import java.text.SimpleDateFormat


@Transactional
class GlabPaymentService {
    def schoolService
    def sequenceGeneratorService

        static transactional = false
    static final String[] sortVoucherColumns = ['id','invoice_no','description','amount','invoice_date', 'due_date', '']

    LinkedHashMap list(GrailsParameterMap params) {
        int iDisplayStart = params.iDisplayStart ? params.getInt('iDisplayStart') : CommonUtils.DEFAULT_PAGINATION_START
        int iDisplayLength = params.iDisplayLength ? params.getInt('iDisplayLength') : CommonUtils.DEFAULT_PAGINATION_LENGTH
        String sSortDir = params.sSortDir_0 ? params.sSortDir_0 : CommonUtils.DEFAULT_PAGINATION_SORT_ORDER
        int iSortingCol = params.iSortCol_0 ? params.getInt('iSortCol_0') : CommonUtils.DEFAULT_PAGINATION_SORT_IDX
        //Search string, use or logic to all fields that required to include
        String sSearch = params.sSearch ? params.sSearch : null
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }
        String sortColumn = CommonUtils.getSortColumn(sortVoucherColumns, iSortingCol)
        List dataReturns = new ArrayList()
        String invoiceDate, dueDate

        def c = GlabPayment.createCriteria()
        def results = c.list(max: iDisplayLength, offset: iDisplayStart) {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
            }
            if (sSearch) {
                or {
                    ilike('account_no', sSearch)
                    ilike('invoice_no', sSearch)
                }
            }
            order(sortColumn, sSortDir)
        }
        int totalCount = results.totalCount
        int serial = iDisplayStart;
        if (totalCount > 0) {
            if (sSortDir.equals(CommonUtils.SORT_ORDER_DESC)) {
                serial = (totalCount + 1) - iDisplayStart
            }
            results.each { GlabPayment glabPayment ->
                if (sSortDir.equals(CommonUtils.SORT_ORDER_ASC)) {
                    serial++
                } else {
                    serial--
                }
                invoiceDate= glabPayment.invoiceDate ? CommonUtils.getUiDateStr(glabPayment.invoiceDate ):''
                dueDate= glabPayment.dueDate  ? CommonUtils.getUiDateStr(glabPayment.dueDate):''
                dataReturns.add([DT_RowId: glabPayment.id, 0: serial, 1: glabPayment.invoiceNo, 2: glabPayment.sortDesc, 3: glabPayment.invoiceAmount, 4: invoiceDate, 5: dueDate, 6: ''])
            }
        }
        return [totalCount: totalCount, results: dataReturns]
    }


    def save (JSONObject saveGlabVoucher) {
        def responseData
        Date invoDate, dueDate
        GlabPayment glabPayment
        GlabDetails glabDetails
        Long objId
        def paymentJsonList
        if (saveGlabVoucher.id == 0){
            glabPayment = new GlabPayment(invoiceNo: saveGlabVoucher.invoiceNo, invoiceDate: saveGlabVoucher.invoiceDate,
                    dueDate: saveGlabVoucher.dueDate, sortDesc: saveGlabVoucher.sortDesc,
                    accountNo: saveGlabVoucher.accountNo, terms: saveGlabVoucher.terms,
                    notes: saveGlabVoucher.notes)
            glabPayment.save(flush: true)

            paymentJsonList = schoolService.getJsonValue(saveGlabVoucher, 'paymentList')
            for (paymentObj in paymentJsonList) {
                new GlabDetails(description: paymentObj.description, rate: paymentObj.rate,
                        qty: paymentObj.qty, amount: paymentObj.amount, paymentId: glabPayment.id).save()
            }
            responseData = ['hasError': false, 'successMsg': 'Save successfully', id: glabPayment.id]

        } else {
            invoDate  = CommonUtils.searchDateFormatDMY(saveGlabVoucher.invoiceDate)
            dueDate  = CommonUtils.searchDateFormatDMY(saveGlabVoucher.dueDate)
            String id = saveGlabVoucher.id
            glabPayment = GlabPayment.get(Long.parseLong(saveGlabVoucher.get("id")))
            glabPayment.invoiceNo = saveGlabVoucher.invoiceNo
            glabPayment.invoiceDate = invoDate
            glabPayment.dueDate = dueDate
            glabPayment.sortDesc = saveGlabVoucher.sortDesc
            glabPayment.accountNo = saveGlabVoucher.accountNo
            glabPayment.terms = saveGlabVoucher.terms
            glabPayment.notes = saveGlabVoucher.notes
            glabPayment.save()

            paymentJsonList = schoolService.getJsonValue(saveGlabVoucher, 'paymentList')
            for (paymentObj in paymentJsonList) {

                if(paymentObj.id){
                    objId =  Long.valueOf(paymentObj.id)
                    glabDetails = GlabDetails.get(objId)
                    glabDetails.description = paymentObj.description
                    glabDetails.rate = Double.valueOf(paymentObj.rate)
                    glabDetails.qty = Double.valueOf(paymentObj.qty)
                    glabDetails.amount = Double.valueOf(paymentObj.amount)
                    glabDetails.save()
                } else {
                    glabDetails=  new GlabDetails(description: paymentObj.description,
                            rate: paymentObj.rate, qty: paymentObj.qty,
                            amount: paymentObj.amount, paymentId: glabPayment.id)
                    glabDetails.save(flush: true)
                }
            }


            responseData = ['hasError': false, 'updateMsg': 'Update successfully']

        }
          responseData

    }
}
