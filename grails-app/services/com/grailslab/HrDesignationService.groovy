package com.grailslab

import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.hr.HrCategory
import com.grailslab.hr.HrDesignation
import grails.transaction.Transactional
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

class HrDesignationService {
    static transactional = false

    static final String[] sortColumns = ['sortOrder','name']
    LinkedHashMap designationPaginateList(GrailsParameterMap params){
        int iDisplayStart = params.iDisplayStart ? params.getInt('iDisplayStart') : CommonUtils.DEFAULT_PAGINATION_START
        int iDisplayLength = params.iDisplayLength ? params.getInt('iDisplayLength') : CommonUtils.DEFAULT_PAGINATION_LENGTH
        String sSortDir = params.sSortDir_0 ? params.sSortDir_0 : CommonUtils.SORT_ORDER_ASC
        int iSortingCol = params.iSortCol_0 ? params.getInt('iSortCol_0') : CommonUtils.DEFAULT_PAGINATION_SORT_IDX
        String sSearch = params.sSearch ? params.sSearch : null
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }
        HrCategory hrCategory
        if(params.hrCategoryType){
            hrCategory = HrCategory.read(params.getLong('hrCategoryType'))
        }else {
            def hrCategoryList = HrCategory.findAllByActiveStatus(ActiveStatus.ACTIVE,[sort:'sortOrder'])
            if(hrCategoryList){
                hrCategory = hrCategoryList.first()
            }
        }
        String sortColumn = CommonUtils.getSortColumn(sortColumns,iSortingCol)
        List dataReturns = new ArrayList()
        def c = HrDesignation.createCriteria()
        def results = c.list(max: iDisplayLength, offset: iDisplayStart) {
            and {
                eq("activeStatus",ActiveStatus.ACTIVE)
                if(hrCategory){
                    eq("hrCategory", hrCategory)
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
            results.each { HrDesignation obj ->
                dataReturns.add([DT_RowId: obj.id,  0: obj.sortOrder, 1: obj.name, 2:obj.hrCategory?.name, 3: ''])
            }
        }
        return [totalCount:totalCount,results:dataReturns]
    }
}
