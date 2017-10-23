package com.grailslab.library

import com.grailslab.CommonUtils
import com.grailslab.gschoolcore.ActiveStatus
import grails.transaction.Transactional
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

@Transactional
class LibraryMemberService {
    static transactional = false
    static final String[] sortColumns = ['id','name']
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
        def c = LibraryMember.createCriteria()
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
        String memberShipDate
        int serial = iDisplayStart;
        if (totalCount > 0) {
            results.each { LibraryMember member ->
                if (sSortDir.equals(CommonUtils.SORT_ORDER_ASC)) {
                    serial++
                } else {
                    serial--
                }
                memberShipDate= member.memberShipDate ? CommonUtils.getUiDateStr(member.memberShipDate):''
                dataReturns.add([DT_RowId: member.id, 0: serial, 1: member.name, 2: member.memberId, 3: member.referenceId, 4: member.mobile, 5: member.presentAddress, 6: memberShipDate, 7:''])
            }
        }
        return [totalCount:totalCount,results:dataReturns]
    }
}
