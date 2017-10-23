package com.grailslab

import com.grailslab.enums.OpenContentType
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.open.OpenContent
import com.grailslab.open.OpenTimeLine
import grails.transaction.Transactional
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

class TimeLineService {
    static transactional = false

    static final String[] sortColumns = ['sortOrder','title','name','iconClass','activeStatus']
    LinkedHashMap timeLinePaginateList(GrailsParameterMap params){
        int iDisplayStart = params.iDisplayStart ? params.getInt('iDisplayStart') : CommonUtils.DEFAULT_PAGINATION_START
        int iDisplayLength = params.iDisplayLength ? params.getInt('iDisplayLength') : CommonUtils.DEFAULT_PAGINATION_LENGTH
        String sSortDir = params.sSortDir_0 ? params.sSortDir_0 : CommonUtils.SORT_ORDER_DESC
        int iSortingCol = params.iSortCol_0 ? params.getInt('iSortCol_0') : CommonUtils.DEFAULT_PAGINATION_SORT_IDX
        String sSearch = params.sSearch ? params.sSearch : null
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }
        String sortColumn = CommonUtils.getSortColumn(sortColumns,iSortingCol)
        List dataReturns = new ArrayList()
        def c = OpenTimeLine.createCriteria()
        def results = c.list(max: iDisplayLength, offset: iDisplayStart) {
            and {
                eq("activeStatus",ActiveStatus.ACTIVE)
            }
            if (sSearch) {
                or {
                    ilike('title', sSearch)
                    ilike('name', sSearch)
                }
            }
            order(sortColumn, sSortDir)
        }
        int totalCount = results.totalCount
        if (totalCount > 0) {
            results.each { OpenTimeLine obj ->
                dataReturns.add([DT_RowId: obj.id,  0: obj.sortOrder, 1: obj.title, 2:obj.name, 3:obj.iconClass, 4:obj.activeStatus.value, 5:obj.description, 6: ''])
            }
        }
        return [totalCount:totalCount,results:dataReturns]
    }

    def showTimeLine(){
        int iDisplayStart = CommonUtils.DEFAULT_PAGINATION_START
        int iDisplayLength = CommonUtils.MAX_PAGINATION_LENGTH
        String sSortDir = CommonUtils.SORT_ORDER_DESC
        String sortColumn = "sortOrder"
        def c = OpenTimeLine.createCriteria()
        def results = c.list(max: iDisplayLength, offset: iDisplayStart) {
            and {
                eq("activeStatus",ActiveStatus.ACTIVE)
            }
            order(sortColumn, sSortDir)
        } as List
    }
}
