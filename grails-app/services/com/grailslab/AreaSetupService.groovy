package com.grailslab

import com.grailslab.enums.YearMonths
import com.grailslab.gschoolcore.AcademicYear
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.salary.SalArea
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

class AreaSetupService {
    def schoolService

    static final String[] sortColumns = ['id']

    LinkedHashMap paginateList(GrailsParameterMap params) {
        Map serverParams = CommonUtils.getPaginationParams(params, sortColumns)
        int iDisplayStart= serverParams.iDisplayStart.toInteger();
        int  iDisplayLength= serverParams.iDisplayLength.toInteger()
        AcademicYear academicYear = schoolService.schoolWorkingYear()
        if(params.academicYear){
            academicYear = AcademicYear.valueOf(params.academicYear)
        }
        YearMonths yearMonths  = YearMonths.JANUARY
        if(params.yearMonths){
            yearMonths = YearMonths.valueOf(params.yearMonths)
        }

        List dataReturns = new ArrayList()
        def c = SalArea.createCriteria()
        def results = c.list(max:iDisplayLength, offset: iDisplayStart) {
            createAlias('employee', 'emp')
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
                eq("academicYear", academicYear)
                eq("yearMonths",yearMonths)
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
            results.each { SalArea salArea ->
                if (serverParams.sSortDir.equals(CommonUtils.SORT_ORDER_ASC)) {
                    serial++
                } else {
                    serial--
                }
                dataReturns.add([DT_RowId: salArea.id, 0: serial, 1: salArea.employee.empID, 2: salArea.employee.name,3:salArea.amount , 4: ''])
            }
        }
        return [totalCount: totalCount, results: dataReturns]
    }


}
