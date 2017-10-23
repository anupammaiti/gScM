package com.grailslab

import com.grailslab.enums.OpenContentType
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.open.OpenMgmtVoice
import com.grailslab.open.OpenSuccessStory
import grails.transaction.Transactional
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

class SuccessStoryService {
    static transactional = false
    def grailsApplication

    static final String[] sortColumns = ['sortOrder','name','description','activeStatus']
    LinkedHashMap paginateList(GrailsParameterMap params){
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
        def c = OpenSuccessStory.createCriteria()
        def results = c.list(max: iDisplayLength, offset: iDisplayStart) {
            and {
//                eq("activeStatus",ActiveStatus.ACTIVE)
            }
            if (sSearch) {
                or {
                    ilike('name', sSearch)
                    ilike('description', sSearch)
                }
            }
            order(sortColumn, sSortDir)
        }
        int totalCount = results.totalCount
        if (totalCount > 0) {
            def g = grailsApplication.mainContext.getBean('org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib')
            String imageUrl
            results.each { OpenSuccessStory obj ->
                imageUrl = '<img src="'+g.createLink(controller: 'image', action: 'streamImageFromIdentifierThumb', params: ['imagePath': obj?.imagePath], absolute: true)+'" alt="Blank" style="width:25px;height:25px;">'
                dataReturns.add([DT_RowId: obj.id,  0: obj.sortOrder, 1: imageUrl, 2:obj.name, 3:obj.description, 4:obj.activeStatus.value,  5: ''])
            }
        }
        return [totalCount:totalCount,results:dataReturns]
    }
}
