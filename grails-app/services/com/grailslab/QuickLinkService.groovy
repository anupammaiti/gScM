package com.grailslab

import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.open.OpenQuickLink
import javafx.beans.binding.Bindings
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap


class QuickLinkService {
    static transactional = false
    static final String[] sortColumns = ['id','displayName','urlType','linkUrl','sortIndex','iconClass','activeStatus']
    LinkedHashMap paginateList(GrailsParameterMap params) {
        int iDisplayStart = params.iDisplayStart ? params.getInt('iDisplayStart') : CommonUtils.DEFAULT_PAGINATION_START
        int iDisplayLength = params.iDisplayLength ? params.getInt('iDisplayLength') : CommonUtils.DEFAULT_PAGINATION_LENGTH
        String sSortDir = params.sSortDir_0 ? params.sSortDir_0 : CommonUtils.SORT_ORDER_ASC
        int iSortingCol = params.iSortCol_0 ? params.getInt('iSortCol_0') : CommonUtils.DEFAULT_PAGINATION_SORT_IDX
        String sSearch = params.sSearch ? params.sSearch : null
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }
        String sortColumn = CommonUtils.getSortColumn(sortColumns, iSortingCol)

        List dataReturns = new ArrayList()
        def c = OpenQuickLink.createCriteria()
        def results = c.list(max: iDisplayLength, offset: iDisplayStart) {
            /*and {
                eq("galleryType", galleryType)
            }*/
            if (sSearch) {
                or {
                    ilike('displayName', sSearch)
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
            results.each { OpenQuickLink obj ->
                if (sSortDir.equals(CommonUtils.SORT_ORDER_ASC)) {
                    serial++
                } else {
                    serial--
                }
                dataReturns.add([DT_RowId: obj.id, 0: serial, 1: obj.displayName,2: obj.urlType, 3:obj.linkUrl, 4: obj.sortIndex, 5:obj.iconClass, 6: obj.activeStatus?.value, 7: ''])
            }
        }
        return [totalCount: totalCount, results: dataReturns]
    }
    def allHomeQuickLinks(){
        def c = OpenQuickLink.createCriteria()
        def results = c.list() {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
                eq("urlType", "HOME")
            }
            order('sortIndex', CommonUtils.SORT_ORDER_ASC)
        }
        return results
    }
    def allGalleryQuickLinks(){
        def c = OpenQuickLink.createCriteria()
        def results = c.list() {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
                eq("urlType", "GALLERY")
            }
            order('sortIndex', CommonUtils.SORT_ORDER_ASC)
        }
        return results
    }

}
