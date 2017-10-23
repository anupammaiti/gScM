package com.grailslab

import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.settings.ClassRoom
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

class ClassRoomService {
    static transactional = false

    static final String[] sortColumns = ['id','name','description']
    LinkedHashMap classRoomPaginateList(GrailsParameterMap params){
        int iDisplayStart = params.iDisplayStart ? params.getInt('iDisplayStart') : CommonUtils.DEFAULT_PAGINATION_START
        int iDisplayLength = params.iDisplayLength ? params.getInt('iDisplayLength') : CommonUtils.DEFAULT_PAGINATION_LENGTH
        String sSortDir = params.sSortDir_0 ? params.sSortDir_0 : CommonUtils.DEFAULT_PAGINATION_SORT_ORDER
        int iSortingCol = params.iSortCol_0 ? params.getInt('iSortCol_0') : CommonUtils.DEFAULT_PAGINATION_SORT_IDX
        //Search string, use or logic to all fields that required to include
        String sSearch = params.sSearch ? params.sSearch : null
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }
        String sortColumn = CommonUtils.getSortColumn(sortColumns,iSortingCol)
        List dataReturns = new ArrayList()
        def c = ClassRoom.createCriteria()
        def results = c.list(max: iDisplayLength, offset: iDisplayStart) {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)

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
        int serial = iDisplayStart;
        if (totalCount > 0) {
            if (sSortDir.equals(CommonUtils.SORT_ORDER_DESC)) {
                serial = (totalCount + 1) - iDisplayStart
            }
            results.each { ClassRoom classRoom ->
                if (sSortDir.equals(CommonUtils.SORT_ORDER_ASC)) {
                    serial++
                } else {
                    serial--
                }
                dataReturns.add([DT_RowId: classRoom.id, 0: serial, 1: classRoom.name, 2: classRoom.description, 3: ''])
            }
        }
        return [totalCount:totalCount,results:dataReturns]
    }
    def classRoomDropDownList() {
        List dataReturns = new ArrayList()
        def c = ClassRoom.createCriteria()
        def results = c.list() {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
            }
            order("name", CommonUtils.SORT_ORDER_ASC)
        }
        results.each { ClassRoom classRoom ->
            dataReturns.add([id: classRoom.id, name: classRoom.name])
        }
        return dataReturns
    }

}
