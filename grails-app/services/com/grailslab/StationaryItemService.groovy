package com.grailslab

import com.grailslab.accounting.StationaryItem
import com.grailslab.enums.FeeAppliedType
import com.grailslab.settings.ClassName
import com.grailslab.settings.School
import grails.transaction.Transactional
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

class StationaryItemService {
    static transactional = false
    static final String[] sortColumns = ['id', 'acc.code', 'feeType', 'amount', 'discount', 'netPayable']

    LinkedHashMap stationaryItemPaginationList(GrailsParameterMap params, FeeAppliedType feeAppliedType) {
        int iDisplayStart = params.iDisplayStart ? params.getInt('iDisplayStart') : CommonUtils.DEFAULT_PAGINATION_START
        int iDisplayLength = params.iDisplayLength ? params.getInt('iDisplayLength') : CommonUtils.DEFAULT_PAGINATION_LENGTH
        String sSortDir = params.sSortDir_0 ? params.sSortDir_0 : CommonUtils.SORT_ORDER_ASC
        int iSortingCol = params.iSortCol_0 ? params.getInt('iSortCol_0') : CommonUtils.DEFAULT_PAGINATION_SORT_IDX
        String sSearch = params.sSearch ? params.sSearch : null
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }
        ClassName className = null
        if (params.className) {
            className = ClassName.read(params.className.toLong())
        }
        String sortColumn = CommonUtils.getSortColumn(sortColumns, iSortingCol)
        List dataReturns = new ArrayList()
        def c = StationaryItem.createCriteria()
        def results = c.list(max: iDisplayLength, offset: iDisplayStart) {
            createAlias('account', 'acc')
//            createAlias('className','clsName')
            and {
                eq("feeAppliedType", feeAppliedType)
                if (feeAppliedType == FeeAppliedType.CLASS_STD && className) {
                    eq("className", className)
                }
            }
            if (sSearch) {
                or {
                    ilike('acc.name', sSearch)
                }
            }
            order(sortColumn, sSortDir)
//            order('subjectName.sortPosition', CommonUtils.SORT_ORDER_ASC)
        }
        int totalCount = results.totalCount
        int serial = iDisplayStart;

        if (totalCount > 0) {
            if (sSortDir.equals(CommonUtils.SORT_ORDER_DESC)) {
                serial = (totalCount + 1) - iDisplayStart
            }
            results.each { StationaryItem item ->
                if (sSortDir.equals(CommonUtils.SORT_ORDER_ASC)) {
                    serial++
                } else {
                    serial--
                }
                dataReturns.add([DT_RowId: item.id, 0: serial, 1: "${item.account?.code} - ${item.account?.name}", 2: item.feeType.value, 3: item.description, 4: item.discount, 5: item.netPayable, 6: ''])
            }
        }
        return [totalCount: totalCount, results: dataReturns]
    }
}
