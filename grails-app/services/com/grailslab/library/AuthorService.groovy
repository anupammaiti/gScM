package com.grailslab.library

import com.grailslab.CommonUtils
import com.grailslab.gschoolcore.ActiveStatus
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

class AuthorService {
    static transactional = false
    static final String[] sortColumnsAuthor = ['id','name','bengaliName','country']
    LinkedHashMap paginateList(GrailsParameterMap params){
        int iDisplayStart = params.iDisplayStart ? params.getInt('iDisplayStart') : CommonUtils.DEFAULT_PAGINATION_START
        int iDisplayLength = params.iDisplayLength ? params.getInt('iDisplayLength') : CommonUtils.DEFAULT_PAGINATION_LENGTH
        String sSortDir = params.sSortDir_0 ? params.sSortDir_0 : CommonUtils.SORT_ORDER_ASC
        int iSortingCol = params.iSortCol_0 ? params.getInt('iSortCol_0') : 1
        String sSearch = params.sSearch ? params.sSearch : null
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }
        String sortColumn = CommonUtils.getSortColumn(sortColumnsAuthor,iSortingCol)
        List dataReturns = new ArrayList()
        def c = Author.createCriteria()
        def results = c.list(max: iDisplayLength, offset: iDisplayStart) {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
            }
            if (sSearch) {
                or {
                    ilike('name', sSearch)
                    ilike('bengaliName', sSearch)
                    ilike('country', sSearch)
                }
            }
            order(sortColumn, sSortDir)
        }
        int totalCount = results.totalCount
        int serial = iDisplayStart;
        if (totalCount > 0) {
            if (sSortDir.equals(CommonUtils.SORT_ORDER_DESC)) {
                serial = (totalCount + 1) - iDisplayStart
            }
            results.each { Author author ->
                if (sSortDir.equals(CommonUtils.SORT_ORDER_ASC)) {
                    serial++
                } else {
                    serial--
                }
                dataReturns.add([DT_RowId: author.id, 0: serial, 1: author.name, 2: author.bengaliName?:"", 3:author.country?:"", 4:''])
            }
        }
        return [totalCount:totalCount,results:dataReturns]
    }
}
