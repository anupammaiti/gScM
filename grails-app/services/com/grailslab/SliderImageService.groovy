package com.grailslab

import com.grailslab.open.OpenSliderImage
import grails.transaction.Transactional
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

class SliderImageService {
    static transactional = false
    def grailsApplication

    static final String[] sortColumns = ['sortIndex','title','activeStatus']
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
        def c = OpenSliderImage.createCriteria()
        def results = c.list(max: iDisplayLength, offset: iDisplayStart) {
            if (sSearch) {
                or {
                    ilike('title', sSearch)
                }
            }
            order(sortColumn, sSortDir)
        }
        int totalCount = results.totalCount
        String imagePathThumb
        if (totalCount > 0) {
            def g = grailsApplication.mainContext.getBean('org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib')
            results.each { OpenSliderImage obj ->
                imagePathThumb = '<img src="'+g.createLink(controller: 'image', action: 'streamImageFromIdentifierThumb', params: ['imagePath': obj?.imagePath], absolute: true)+'" alt="Blank" style="width:45px;height:45px;">'
                dataReturns.add([DT_RowId: obj.id, 0: obj.sortIndex,1:imagePathThumb, 2: obj.title, 3: obj.description, 4: obj.activeStatus?.value, 5: ''])
            }
        }
        return [totalCount: totalCount, results: dataReturns]
    }
}


