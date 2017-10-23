package com.grailslab

import com.grailslab.enums.YearMonths
import com.grailslab.gschoolcore.AcademicYear
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.salary.SalIncrement
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

class SalIncrementService {
    def schoolService

    static final String[] sortColumnsStdAtt = ['id']

    LinkedHashMap paginateList(GrailsParameterMap params) {
        Map serverParams = CommonUtils.getPaginationParams(params, sortColumnsStdAtt)
        int iDisplayStart= serverParams.iDisplayStart.toInteger();
        int  iDisplayLength= serverParams.iDisplayLength.toInteger()
        String sSortDir = params.sSortDir_0 ? params.sSortDir_0 : CommonUtils.SORT_ORDER_DESC
        int iSortingCol = params.iSortCol_0 ? params.getInt('iSortCol_0') : CommonUtils.DEFAULT_PAGINATION_SORT_IDX
        String sSearch = params.sSearch ? params.sSearch : null
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }
        AcademicYear academicYear
        if (params.academicYear) {
            academicYear = AcademicYear.valueOf(params.academicYear)
        } else {
            academicYear = schoolService.workingYear()
        }

        YearMonths yearMonths
        if (params.yearMonths) {
            yearMonths = YearMonths.valueOf(params.yearMonths)
        }

        List dataReturns = new ArrayList()
        def c = SalIncrement.createCriteria()

        def results = c.list(max:iDisplayLength, offset: iDisplayStart) {
            createAlias('employee', 'emp')
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
                eq("academicYear", academicYear)
                if (yearMonths) {
                    eq("yearMonths", yearMonths)
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
            String increment
            results.each { SalIncrement salIncrement ->
                if (serverParams.sSortDir.equals(CommonUtils.SORT_ORDER_ASC)) {
                    serial++
                } else {
                    serial--
                }
                increment = ""
                if (salIncrement.basic || salIncrement.houseRent  || salIncrement.medical || salIncrement.inCharge || salIncrement.others) {
                    increment += "Basic: "+salIncrement.basic+ " House rent: "+salIncrement.houseRent+" Medical: "+salIncrement.medical+" Incharge: "+salIncrement.inCharge+" Others: "+salIncrement.others+ "<br/>Total Gross: "+salIncrement.grossSalary
                }else{
                    increment += "Total Gross: "+salIncrement.grossSalary
                }
                dataReturns.add([DT_RowId: salIncrement.id, 0: serial, 1: salIncrement.yearMonths.value, 2: salIncrement.employee.empID,3: salIncrement.employee.name,4: salIncrement.employee.hrDesignation?.name,5: increment,6:salIncrement.incrementStatus, 7:''])
            }
        }
        return [totalCount: totalCount, results: dataReturns]
    }
}
