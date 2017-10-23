package com.grailslab

import com.grailslab.enums.FeeType
import com.grailslab.enums.PayType
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.salary.SalPc
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

class SalPcService {

    static final String[] sortColumnsStdAtt = ['id']
    /*static final String[] sortColumns = ['sortOrder','employee', 'date', 'amount', 'designation']*/
    LinkedHashMap paginateList(GrailsParameterMap params) {
        Map serverParams = CommonUtils.getPaginationParams(params, sortColumnsStdAtt)
        int iDisplayStart= serverParams.iDisplayStart.toInteger();
        int  iDisplayLength= serverParams.iDisplayLength.toInteger()
        String sSortDir = params.sSortDir_0 ? params.sSortDir_0 : CommonUtils.SORT_ORDER_ASC
        int iSortingCol = params.iSortCol_0 ? params.getInt('iSortCol_0') : CommonUtils.DEFAULT_PAGINATION_SORT_IDX
        String sSearch = params.sSearch ? params.sSearch : null
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }
        PayType payType
        if(params.payType){
            payType = PayType.valueOf(params.payType)
        }

        List dataReturns = new ArrayList()
        def c = SalPc.createCriteria()

        def results = c.list(max:iDisplayLength, offset: iDisplayStart) {
            createAlias('employee', 'emp')
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
                if (payType) {
                    eq("payType", payType)
                }
            }
            if (serverParams.sSearch) {
                or {
                    ilike('emp.empID', serverParams.sSearch)
                    ilike('emp.name', serverParams.sSearch)
                }

            }
            order(serverParams.sortColumn, serverParams.sSortDir)
        }


        int totalCount = results.totalCount
        int serial = iDisplayStart;
        if (totalCount > 0) {
            if (serverParams.sSortDir.equals(CommonUtils.SORT_ORDER_DESC)) {
                serial = (totalCount + 1) - iDisplayStart
            }
            results.each { SalPc salPc ->
                if (serverParams.sSortDir.equals(CommonUtils.SORT_ORDER_ASC)) {
                    serial++
                } else {
                    serial--
                }
                dataReturns.add([DT_RowId: salPc.id, 0: serial, 1: salPc.employee.empID, 2: salPc.employee.name,3: salPc.employee.hrDesignation?.name,4: salPc.payableAmount,5: salPc.outStandingAmount, 6:salPc.installmentAmount,7:salPc.payType.key,8:'' ])
            }
        }
        return [totalCount: totalCount, results: dataReturns]
    }


}
