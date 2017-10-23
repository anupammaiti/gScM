package com.grailslab

import com.grailslab.enums.OpenContentType
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.open.OpenContent
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

class FeatureService {
    static transactional = false

    static final String[] sortColumns = ['sortOrder','title','iconClass','activeStatus']
    LinkedHashMap featurePaginateList(GrailsParameterMap params){
        int iDisplayStart = params.iDisplayStart ? params.getInt('iDisplayStart') : CommonUtils.DEFAULT_PAGINATION_START
        int iDisplayLength = params.iDisplayLength ? params.getInt('iDisplayLength') : CommonUtils.DEFAULT_PAGINATION_LENGTH
        String sSortDir = params.sSortDir_0 ? params.sSortDir_0 : CommonUtils.SORT_ORDER_ASC
        int iSortingCol = params.iSortCol_0 ? params.getInt('iSortCol_0') : CommonUtils.DEFAULT_PAGINATION_SORT_IDX
        String sSearch = params.sSearch ? params.sSearch : null
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }
        OpenContentType openContentType = OpenContentType.Feature_Content
        if(params.openContentType){
            openContentType = OpenContentType.valueOf(params.openContentType)
        }
        String sortColumn = CommonUtils.getSortColumn(sortColumns,iSortingCol)
        List dataReturns = new ArrayList()
        def c = OpenContent.createCriteria()
        def results = c.list(max: iDisplayLength, offset: iDisplayStart) {
            and {
                eq("activeStatus",ActiveStatus.ACTIVE)
                if(openContentType){
                    eq("openContentType", openContentType)
                }
            }
            if (sSearch) {
                or {
                    ilike('name', sSearch)
                }
            }
            order(sortColumn, sSortDir)
        }
        int totalCount = results.totalCount
        int serial = iDisplayStart;
        if (totalCount > 0) {
            results.each { OpenContent obj ->
                dataReturns.add([DT_RowId: obj.id,  0: obj.sortOrder, 1: obj.title, 2:obj.iconClass, 3:obj.activeStatus.value, 4:obj.description, 5: ''])
            }
        }
        return [totalCount:totalCount,results:dataReturns]
    }
}
