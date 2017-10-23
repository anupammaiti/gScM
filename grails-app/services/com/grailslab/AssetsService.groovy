package com.grailslab

import com.grailslab.accounting.Assets
import com.grailslab.gschoolcore.ActiveStatus
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

class AssetsService {
    static transactional = false

    static final String[] sortColumns = ['name']
    LinkedHashMap assetsPaginateList(GrailsParameterMap params){
        int iDisplayStart = params.iDisplayStart ? params.getInt('iDisplayStart') : CommonUtils.DEFAULT_PAGINATION_START
        int iDisplayLength = params.iDisplayLength ? params.getInt('iDisplayLength') : CommonUtils.DEFAULT_PAGINATION_LENGTH
        String sSortDir = params.sSortDir_0 ? params.sSortDir_0 : CommonUtils.SORT_ORDER_ASC
        int iSortingCol = params.iSortCol_0 ? params.getInt('iSortCol_0') : CommonUtils.DEFAULT_PAGINATION_SORT_IDX
        //Search string, use or logic to all fields that required to include
        String sSearch = params.sSearch ? params.sSearch : null
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }
        String sortColumn = CommonUtils.getSortColumn(sortColumns,iSortingCol)
        List dataReturns = new ArrayList()
        def c = Assets.createCriteria()
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
        String terms=''
        String allowOptional=''
        if (totalCount > 0) {
            results.each { Assets assets ->
                dataReturns.add([DT_RowId: assets.id, 0: assets.name, 1: assets.account.code +' - '+assets.account.name,2: assets.buyDate.dateString,3: assets.cost, 4:assets.depreciation,5:''])
            }
        }
        return [totalCount:totalCount,results:dataReturns]
    }

}
