package com.grailslab

import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.hr.LeaveTemplate
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

class LeaveTemplateService {
    static transactional = false

    static final String[] sortColumns = ['applyType']

    LinkedHashMap leaveApplicationPaginateList(GrailsParameterMap params) {
        int iDisplayStart = params.iDisplayStart ? params.getInt('iDisplayStart') : CommonUtils.DEFAULT_PAGINATION_START
        int iDisplayLength = params.iDisplayLength ? params.getInt('iDisplayLength') : CommonUtils.DEFAULT_PAGINATION_LENGTH
        String sSortDir = params.sSortDir_0 ? params.sSortDir_0 : CommonUtils.DEFAULT_PAGINATION_SORT_ORDER
        int iSortingCol = params.iSortCol_0 ? params.getInt('iSortCol_0') : CommonUtils.DEFAULT_PAGINATION_SORT_IDX
        String sSearch = params.sSearch ? params.sSearch : null
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }
        String sortColumn = CommonUtils.getSortColumn(sortColumns, iSortingCol)
        List dataReturns = new ArrayList()
        def c = LeaveTemplate.createCriteria()
        def results = c.list(max: iDisplayLength, offset: iDisplayStart) {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
            }
            if (sSearch) {
                or {
                    ilike('leaveTemplate', sSearch)
                }
            }
            order(sortColumn, sSortDir)
        }

        int totalCount = results.totalCount
        if (totalCount > 0) {
            results.each { obj ->
                dataReturns.add([DT_RowId: obj.id, 0: obj.applyType?.value, 1: ''])
            }
        }
        return [totalCount: totalCount, results: dataReturns]
    }
}
