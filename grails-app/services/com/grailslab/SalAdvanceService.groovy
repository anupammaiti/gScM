package com.grailslab

import com.grailslab.enums.PayType
import com.grailslab.gschoolcore.ActiveStatus

import com.grailslab.salary.SalAdvance
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

class SalAdvanceService {

    static final String[] sortColumnsStdAtt = ['id']
    /*static final String[] sortColumns = ['sortOrder','employee', 'date', 'amount', 'designation']*/
    LinkedHashMap paginateList(GrailsParameterMap params) {
        Map serverParams = CommonUtils.getPaginationParams(params, sortColumnsStdAtt)
        int iDisplayStart= serverParams.iDisplayStart.toInteger();
        int  iDisplayLength= serverParams.iDisplayLength.toInteger()

        List dataReturns = new ArrayList()
        def c = SalAdvance.createCriteria()

        def results = c.list(max:iDisplayLength, offset: iDisplayStart) {
            createAlias('employee', 'emp')
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
                eq("payType", PayType.DUE)
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
            results.each { SalAdvance salAdvance ->
                if (serverParams.sSortDir.equals(CommonUtils.SORT_ORDER_ASC)) {
                    serial++
                } else {
                    serial--
                }
                dataReturns.add([DT_RowId: salAdvance.id, 0: serial, 1: salAdvance.employee.empID, 2: salAdvance.employee.name,3: salAdvance.employee.hrDesignation.name, 4:CommonUtils.getUiDateStr(salAdvance.advanceDate),5: salAdvance.currAmount,6: salAdvance.outStandingAmount,7: salAdvance.description, 8:'' ])
            }
        }
        return [totalCount: totalCount, results: dataReturns]
    }

}
