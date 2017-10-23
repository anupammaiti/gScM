package com.grailslab

import com.grailslab.gschoolcore.AcademicYear
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.salary.BonusMaster
import grails.transaction.Transactional
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

@Transactional
class SalaryBonusService {
    def schoolService

    static final String[] sortColumnsStdAtt = ['id']
    LinkedHashMap paginateList(GrailsParameterMap params) {
        Map serverParams = CommonUtils.getPaginationParams(params, sortColumnsStdAtt)
        int iDisplayStart= serverParams.iDisplayStart.toInteger();
        int  iDisplayLength= serverParams.iDisplayLength.toInteger()
        AcademicYear academicYear = schoolService.schoolWorkingYear()
        if(params.academicYear){
            academicYear = AcademicYear.valueOf(params.academicYear)
        }
        List dataReturns = new ArrayList()
        def c = BonusMaster.createCriteria()
        def results = c.list(max:iDisplayLength, offset: iDisplayStart) {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
                eq("academicYear", academicYear)
            }
            if (serverParams.sSearch) {
                or {
                    ilike('festivalName', serverParams.sSearch)
                    ilike('basedOn', serverParams.sSearch)
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
            results.each { BonusMaster bonusMaster ->
                if (serverParams.sSortDir.equals(CommonUtils.SORT_ORDER_ASC)) {
                    serial++
                } else {
                    serial--
                }
                dataReturns.add([DT_RowId: bonusMaster.id, 0: serial, 1: bonusMaster.festivalName, 2:bonusMaster.joinBefore,3: bonusMaster.basedOn, 4:bonusMaster.bonusPercentage, 5:bonusMaster.salaryStatus.value, 6:'' ])
            }
        }
        return [totalCount: totalCount, results: dataReturns]
    }

    def serviceMethod() {

    }
}
