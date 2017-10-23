package com.grailslab


import grails.transaction.Transactional
import com.grailslab.festival.FesRegistration
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

@Transactional
class OlymPaidProgramService {

    static transactional = false
    def grailsApplication

    static final String[] sortColumns = ['name','className','activeStatus']
    LinkedHashMap paginateList(GrailsParameterMap params) {
        int iDisplayStart = params.iDisplayStart ? params.getInt('iDisplayStart') : CommonUtils.DEFAULT_PAGINATION_START
        int iDisplayLength = params.iDisplayLength ? params.getInt('iDisplayLength') : CommonUtils.DEFAULT_PAGINATION_LENGTH
        String sSortDir = params.sSortDir_0 ? params.sSortDir_0 : CommonUtils.SORT_ORDER_ASC
        int iSortingCol = params.iSortCol_0 ? params.getInt('iSortCol_0') : CommonUtils.DEFAULT_PAGINATION_SORT_IDX
        //Search string, use or logic to all fields that required to include
        String sSearch = params.sSearch ? params.sSearch : null
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }
        String sortColumn = CommonUtils.getSortColumn(sortColumns, iSortingCol)
        List dataReturns = new ArrayList()
        def c = FesRegistration.createCriteria()
        def results = c.list(max: iDisplayLength, offset: iDisplayStart) {
            if (sSearch) {
                or {
                    ilike('title', sSearch)
                }
            }
            order(sortColumn, sSortDir)
        }
        int totalCount = results.totalCount
        String description
        if (totalCount > 0) {
            def g = grailsApplication.mainContext.getBean('org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib')
            results.each { FesRegistration obj ->

                dataReturns.add([DT_RowId: obj.id, 0: obj.name,1:obj.instituteName, 2: obj.className, 3: obj.contactNo, 4: obj.activeStatus?.value, 5: ''])
            }
        }
        return [totalCount: totalCount, results: dataReturns]
    }
}
