package com.grailslab

import com.grailslab.enums.PayType
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.hr.Employee
import com.grailslab.hr.HrCategory
import com.grailslab.salary.SalAdvance
import com.grailslab.salary.SalSetup
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap


class SalarySetUpService {

    static final String[] sortColumnsStdAtt = ['id']
    LinkedHashMap paginateList(GrailsParameterMap params) {
        Map serverParams = CommonUtils.getPaginationParams(params, sortColumnsStdAtt)
        int iDisplayStart= serverParams.iDisplayStart.toInteger();
        int  iDisplayLength= serverParams.iDisplayLength.toInteger()

        List dataReturns = new ArrayList()
        def c = SalSetup.createCriteria()
        def results = c.list(max:iDisplayLength, offset: iDisplayStart) {
            createAlias("employee", "emp")
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
            }
            if (serverParams.sSearch) {
                or {
                    ilike('emp.empID', serverParams.sSearch)
                    ilike('emp.name',serverParams.sSearch)
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
            results.each { SalSetup salSetup ->
                if (serverParams.sSortDir.equals(CommonUtils.SORT_ORDER_ASC)) {
                    serial++
                } else {
                    serial--
                }
                dataReturns.add([DT_RowId: salSetup.id, 0: salSetup.employee.empID, 1:salSetup.employee.name,2: salSetup.grossSalary, 3:salSetup.basic ,4: salSetup.houseRent, 5: salSetup.medical,6: salSetup.inCharge,7: salSetup.mobileAllowance,8: salSetup.others,9: salSetup.pfStatus?"Yes":"No",10:salSetup.dpsAmount,11: salSetup.adStatus?"Yes":"No",12:salSetup.adsAmount,13:salSetup.fine,14:salSetup.pc,15:salSetup.netPayable,16:'' ])
            }
        }
        return [totalCount: totalCount, results: dataReturns]
    }

    def salarySetupList(){
      def c = SalSetup.createCriteria()
      return c.list() {and {eq("activeStatus", ActiveStatus.ACTIVE)}}
    }

    def salSetupEmpList(HrCategory hrCategory = null) {
        def c = SalSetup.createCriteria()
        def results = c.list() {
            createAlias("employee", "emp")
            and {
                if (hrCategory){
                    eq("emp.hrCategory", hrCategory)
                }
                eq("activeStatus", ActiveStatus.ACTIVE)
            }
            order("emp.sortOrder", CommonUtils.SORT_ORDER_ASC)
        }

        return results?.collect {it.employee}
    }
    def salSetupEmpIdList(HrCategory hrCategory = null) {
        def c = SalSetup.createCriteria()
        def results = c.list() {
            createAlias("employee", "emp")
            and {
                if (hrCategory){
                    eq("emp.hrCategory", hrCategory)
                }
                eq("activeStatus", ActiveStatus.ACTIVE)
            }
            order("emp.sortOrder", CommonUtils.SORT_ORDER_ASC)
        }

        return results?.collect {it.employee.id}
    }

    def getEmpAdvanceAmount(Long id){
         Employee employee = Employee.read(id)
         return  SalAdvance.findByEmployeeAndPayTypeAndActiveStatus(employee,PayType.DUE,ActiveStatus.ACTIVE)
    }
}
