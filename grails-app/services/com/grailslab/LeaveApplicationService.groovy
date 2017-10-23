package com.grailslab

import com.grailslab.enums.LeaveApproveStatus
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.hr.LeaveApply
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

class LeaveApplicationService {
    static transactional = false

    static final String[] sortColumnsLeaveApprove = ['id', 'em.name','leaveName','fromDate','toDate','numberOfDay','approveStatus','applyDate']

    LinkedHashMap leaveApprovePaginateList(GrailsParameterMap params) {
        int iDisplayStart = params.iDisplayStart ? params.getInt('iDisplayStart') : CommonUtils.DEFAULT_PAGINATION_START
        int iDisplayLength = params.iDisplayLength ? params.getInt('iDisplayLength') : CommonUtils.DEFAULT_PAGINATION_LENGTH
        String sSortDir = params.sSortDir_0 ? params.sSortDir_0 : CommonUtils.DEFAULT_PAGINATION_SORT_ORDER
        int iSortingCol = params.iSortCol_0 ? params.getInt('iSortCol_0') : CommonUtils.DEFAULT_PAGINATION_SORT_IDX
        String sSearch = params.sSearch ? params.sSearch : null
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }
        LeaveApproveStatus approveStatus = params.approveStatus ? LeaveApproveStatus.valueOf(params.approveStatus) : LeaveApproveStatus.Applied
        String sortColumn = CommonUtils.getSortColumn(sortColumnsLeaveApprove, iSortingCol)
        List dataReturns = new ArrayList()
        def c = LeaveApply.createCriteria()
        def results = c.list(max: iDisplayLength, offset: iDisplayStart) {
            createAlias('employee', 'em')
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
                eq("approveStatus", approveStatus)
            }
            if (sSearch) {
                or {
                    ilike('em.empID', sSearch)
                    ilike('em.name', sSearch)
                }
            }
            order(sortColumn, sSortDir)
        }
        int totalCount = results.totalCount
        int serial = iDisplayStart;
        String employeeName
        if (totalCount > 0) {
            if (sSortDir.equals(CommonUtils.SORT_ORDER_DESC)) {
                serial = (totalCount + 1) - iDisplayStart
            }
            results.each { LeaveApply apply ->
                if (sSortDir.equals(CommonUtils.SORT_ORDER_ASC)) {
                    serial++
                } else {
                    serial--
                }
                employeeName = apply.employee?.empID + '  ' + apply.employee?.name
                dataReturns.add([DT_RowId: apply.id, DT_Status: apply.approveStatus?.key, 0: serial, 1: employeeName, 2: apply.leaveName?.name,  3: CommonUtils.getUiDateStr(apply.fromDate), 4: CommonUtils.getUiDateStr(apply.toDate), 5: apply.numberOfDay+"("+apply.payType?.value+")", 6: apply?.approveStatus?.value,7: CommonUtils.getUiDateStr(apply?.applyDate),  8: ''])
            }
        }
        return [totalCount: totalCount, results: dataReturns]
    }
}
