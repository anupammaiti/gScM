package com.grailslab.library

import com.grailslab.CommonUtils
import com.grailslab.gschoolcore.ActiveStatus
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap


class CategoryService {
    static transactional = false
    static final String[] sortColumns = ['name']
    LinkedHashMap categoryPaginateList(GrailsParameterMap params){
        int iDisplayStart = params.iDisplayStart ? params.getInt('iDisplayStart') : CommonUtils.DEFAULT_PAGINATION_START
        int iDisplayLength = params.iDisplayLength ? params.getInt('iDisplayLength') : CommonUtils.DEFAULT_PAGINATION_LENGTH
        String sSortDir = params.sSortDir_0 ? params.sSortDir_0 : CommonUtils.SORT_ORDER_ASC
        int iSortingCol = params.iSortCol_0 ? params.getInt('iSortCol_0') : CommonUtils.DEFAULT_PAGINATION_SORT_IDX
        String sSearch = params.sSearch ? params.sSearch : null
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }
        String sortColumn = CommonUtils.getSortColumn(sortColumns,iSortingCol)
        List dataReturns = new ArrayList()
        def c = BookCategory.createCriteria()
        def results = c.list(max: iDisplayLength, offset: iDisplayStart) {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)

            }
            if (sSearch) {
                or {
                    ilike('name', sSearch)
                }
            }
            order(sortColumn, sSortDir)
        }
        int totalCount = results.totalCount
        if (totalCount > 0) {
            results.each { BookCategory category ->
                dataReturns.add([DT_RowId: category.id, 0: category.name, 1: category.description?:"", 2:''])
            }
        }
        return [totalCount:totalCount,results:dataReturns]
    }
}
