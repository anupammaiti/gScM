package com.grailslab

import com.grailslab.enums.YearMonths
import com.grailslab.gschoolcore.AcademicYear
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.salary.SalExtraClass
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
class ExtraClassService {
    def schoolService

    static final String[] sortColumnsStdAtt = ['id']
        LinkedHashMap paginateList(GrailsParameterMap params) {
        Map serverParams = CommonUtils.getPaginationParams(params, sortColumnsStdAtt)
        int iDisplayStart= serverParams.iDisplayStart.toInteger();
        int  iDisplayLength= serverParams.iDisplayLength.toInteger()
        AcademicYear academicYear = schoolService.schoolAdmissionYear()
        if(params.academicYear){
            academicYear = AcademicYear.valueOf(params.academicYear)
        }
            YearMonths yearMonths  = YearMonths.JANUARY
            if(params.yearMonths){
                yearMonths = YearMonths.valueOf(params.yearMonths)
            }

            List dataReturns = new ArrayList()
        def c = SalExtraClass.createCriteria()
        def results = c.list(max:iDisplayLength, offset: iDisplayStart) {
            createAlias('employee', 'emp')
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
                eq("academicYear", academicYear)
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
            results.each { SalExtraClass salExtraClass ->
                if (serverParams.sSortDir.equals(CommonUtils.SORT_ORDER_ASC)) {
                    serial++
                } else {
                    serial--
                }
                dataReturns.add([DT_RowId: salExtraClass.id, 0: serial, 1: salExtraClass.employee.empID, 2: salExtraClass.employee.name,3:salExtraClass.numOfClass ,4: salExtraClass.rate, 5: salExtraClass.amount, 6: ''])
            }
        }
        return [totalCount: totalCount, results: dataReturns]
    }
}
