package com.grailslab

import com.grailslab.gschoolcore.ActiveStatus

import com.grailslab.salary.SalDps
import grails.transaction.Transactional
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

@Transactional
class DpsAmountService {

    static final String[] sortColumnsStdAtt = ['id']
    /*static final String[] sortColumns = ['sortOrder','employee', 'date', 'amount', 'designation']*/
    LinkedHashMap paginateList(GrailsParameterMap params) {
        Map serverParams = CommonUtils.getPaginationParams(params, sortColumnsStdAtt)
        int iDisplayStart= serverParams.iDisplayStart.toInteger();
        int  iDisplayLength= serverParams.iDisplayLength.toInteger()

        List dataReturns = new ArrayList()
        def c = SalDps.createCriteria()

        def results = c.list(max:iDisplayLength, offset: iDisplayStart) {
            createAlias('employee', 'emp')
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)


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
            results.each { SalDps salDps ->
                if (serverParams.sSortDir.equals(CommonUtils.SORT_ORDER_ASC)) {
                    serial++
                } else {
                    serial--
                }
                dataReturns.add([DT_RowId: salDps.id, 0: serial, 1: salDps.employee.empID, 2: salDps.employee.name,3: salDps.employee.hrDesignation.name, 4:salDps.totalInsAmount,5: salDps.totalOwnAmount,6: salDps.description, 7:'' ])
            }
        }
        return [totalCount: totalCount, results: dataReturns]
    }

}
