package com.grailslab.accounting

import com.grailslab.CommonUtils
import com.grailslab.accounts.GlDetails
import com.grailslab.accounts.GlHeader
import com.grailslab.accounts.GlabPayment
import com.grailslab.accounts.Glmst
import com.grailslab.accounts.TransactionCode
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.library.LibraryConfig
import grails.transaction.Transactional
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.json.JSONArray
import org.json.JSONObject

class AccountsService {
    static transactional = false

    def sequenceGeneratorService
    def schoolService

    def glmstList() {
        Glmst.findAllByActiveStatus(ActiveStatus.ACTIVE)
    }

    def glabPaymentList() {
        GlabPayment.findAllByActiveStatus(ActiveStatus.ACTIVE)
    }


    String nextVoucherNo(){
        sequenceGeneratorService.nextNumber('Voucher')
    }

    def saveVoucher(JSONObject json) {
        def responseData
        if(json) {
            if(!schoolService.getJsonValue(json, 'xvoucher')) {
                    def xvoucher = nextVoucherNo()
                    def xdate = Date.parse('dd/MM/yyyy', json.xdate)
                    GlHeader glHeader = new GlHeader(xvoucher: xvoucher, xref: schoolService.getJsonValue(json, 'xref'), xdate: xdate, xyear: json.xyear, xper: json.xper, xpostFlag: json.xpostFlag,
                            xstatusjv: json.xstatusjv, xtermgl: json.xtermgl, xnote: schoolService.getJsonValue(json, 'xnote'), xaction:  schoolService.getJsonValue(json, 'xaction'))
                    def savedHeader = glHeader.save(flush: true)
                    JSONArray voucherJsonList = new JSONArray(schoolService.getJsonValue(json, 'voucherDetails'))
                    Date xchequeDate = null
                    for (int i= 0; i < voucherJsonList.length(); i++) {
                        JSONObject voucherJson = voucherJsonList.get(i)
                        if(schoolService.getJsonValue(voucherJson, 'xchequeDate')) {
                            xchequeDate = Date.parse('dd/MM/yyyy', voucherJson.xchequeDate)
                        }
                        GlDetails details = new GlDetails(xvoucher: xvoucher, xacc: voucherJson.xacc, xsub: schoolService.getJsonValue(voucherJson, 'xsub'), xproject: schoolService.getJsonValue(voucherJson, 'xproject'),
                                xcur: voucherJson.xcur, xexch: voucherJson.xexch, xprime: voucherJson.xprime, xbase: voucherJson.xbase, xremarks: schoolService.getJsonValue(voucherJson, 'xremarks'),
                                xpayType: voucherJson.xpayType, xcheque: schoolService.getJsonValue(voucherJson, 'xcheque'), xchequeDate: xchequeDate)
                        details.save()
                    }
                    responseData = ['hasError': false, 'message': 'SUCCESS']
            } else {
                def xdate = Date.parse('dd/MM/yyyy', json.xdate)
                def xvoucher = schoolService.getJsonValue(json, 'xvoucher')
                GlHeader glHeader = GlHeader.findByXvoucher(xvoucher)
                glHeader.xref = schoolService.getJsonValue(json, 'xref')
                glHeader.xdate = xdate
                glHeader.xyear = json.xyear
                glHeader.xper = json.xper
                glHeader.xpostFlag = json.xpostFlag
                glHeader.xstatusjv = json.xstatusjv
                glHeader.xtermgl = json.xtermgl
                glHeader.xnote = schoolService.getJsonValue(json, 'xnote')
                glHeader.xaction = schoolService.getJsonValue(json, 'xaction')
                def savedHeader = glHeader.save(flush: true)
                JSONArray voucherJsonList = new JSONArray(schoolService.getJsonValue(json, 'voucherDetails'))
                Date xchequeDate = null
                def glDetailsList = GlDetails.findAllByXvoucher(xvoucher)
                for(GlDetails details: glDetailsList) {
                    boolean needToBeDelete = true
                    for (int i= 0; i < voucherJsonList.length(); i++) {
                        JSONObject voucherJsonEach = voucherJsonList.get(i)
                        if(details.id == schoolService.getJsonValue(voucherJsonEach, 'id')) {
                            needToBeDelete = false
                            break
                        }
                    }
                    if(needToBeDelete) {
                        details.activeStatus = ActiveStatus.DELETE
                        details.save(flush: true)
                    }
                }
                for (int i= 0; i < voucherJsonList.length(); i++) {
                    JSONObject voucherJson = voucherJsonList.get(i)
                    if(schoolService.getJsonValue(voucherJson, 'xchequeDate')) {
                        xchequeDate = Date.parse('dd/MM/yyyy', voucherJson.xchequeDate)
                    }
                    if(!schoolService.getJsonValue(voucherJson, 'id')) {
                        GlDetails details = new GlDetails(xvoucher: xvoucher, xacc: voucherJson.xacc, xsub: schoolService.getJsonValue(voucherJson, 'xsub'), xproject: schoolService.getJsonValue(voucherJson, 'xproject'),
                                xcur: voucherJson.xcur, xexch: voucherJson.xexch, xprime: voucherJson.xprime, xbase: voucherJson.xbase, xremarks: schoolService.getJsonValue(voucherJson, 'xremarks'),
                                xpayType: voucherJson.xpayType, xcheque: schoolService.getJsonValue(voucherJson, 'xcheque'), xchequeDate: xchequeDate)
                        details.save(flush: true)
                    } else {
                        GlDetails details = GlDetails.findById(schoolService.getJsonValue(voucherJson, 'id'))
                        details.xacc = voucherJson.xacc
                        details.xsub = schoolService.getJsonValue(voucherJson, 'xsub')
                        details.xproject = schoolService.getJsonValue(voucherJson, 'xproject')
                        details.xcur = voucherJson.xcur
                        details.xexch = voucherJson.xexch
                        details.xprime = voucherJson.xprime
                        details.xbase = voucherJson.xbase
                        details.xremarks = schoolService.getJsonValue(voucherJson, 'xremarks')
                        details.xpayType = voucherJson.xpayType
                        details.xcheque = schoolService.getJsonValue(voucherJson, 'xcheque')
                        details.xchequeDate = xchequeDate
                        details.activeStatus = ActiveStatus.ACTIVE
                        details.save(flush: true)
                    }
                }
                responseData = ['hasError': false, 'message': 'SUCCESS']
            }
        } else {
            responseData = ['hasError': true, 'message': 'ERROR']
        }
        responseData
    }

    static final String[] sortVoucherColumns = ['id','xvoucher','xdate', 'xnote', 'xstatusjv','xpostFlag']

    LinkedHashMap voucherPaginateList(GrailsParameterMap params) {
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

        def c = GlHeader.createCriteria()
        def results = c.list(max: iDisplayLength, offset: iDisplayStart) {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
            }
            if (sSearch) {
                or {
                    ilike('xvoucher', sSearch)
                    ilike('xnote', sSearch)
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
            results.each { GlHeader glHeader ->
                if (sSortDir.equals(CommonUtils.SORT_ORDER_ASC)) {
                    serial++
                } else {
                    serial--
                }
                dataReturns.add([DT_RowId: glHeader.id, 0: serial, 1: glHeader.xtermgl+' - '+glHeader.xvoucher, 2: CommonUtils.getUiDateStr(glHeader.xdate), 3: glHeader.xnote, 4: glHeader.xstatusjv, 5: glHeader.xpostFlag?'Yes':'No', 6: ''])
            }
        }
        return [totalCount: totalCount, results: dataReturns]
    }


    static final String[] sortColumns = ['name', 'type']

    LinkedHashMap transactionList (GrailsParameterMap params) {
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
        def c = TransactionCode.createCriteria()
        def results = c.list(max: iDisplayLength, offset: iDisplayStart) {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)

            }
            if (sSearch) {
                or {
                    ilike('name', sSearch)
                    ilike('type', sSearch)
                }
        }
            order(sortColumn, sSortDir)
        }
        int totalCount = results.totalCount
        int serial = iDisplayStart
        if (totalCount > 0) {
            if (sSortDir.equals(CommonUtils.SORT_ORDER_DESC)) {
                serial = (totalCount + 1) - iDisplayStart
            }
            results.each { TransactionCode transactionCode ->
                if (sSortDir.equals(CommonUtils.SORT_ORDER_ASC)) {
                    serial++
                } else {
                    serial--
                }
                dataReturns.add([DT_RowId: transactionCode.id, 0:serial, 1: transactionCode.name, 2: transactionCode.type, 3: transactionCode.startForm,4:''])
            }
        }
        return [totalCount: totalCount, results: dataReturns]
    }


}
