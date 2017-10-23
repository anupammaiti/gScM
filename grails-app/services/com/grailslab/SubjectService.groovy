package com.grailslab

import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.settings.SubjectName
import com.sun.org.apache.xpath.internal.operations.Bool
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

class SubjectService {
    static transactional = false

    static final String[] sortColumns = ['sortPosition', 'name']

    LinkedHashMap subjectPaginateList(GrailsParameterMap params) {
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
        def c = SubjectName.createCriteria()
        def results = c.list(max: iDisplayLength, offset: iDisplayStart) {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
                eq("isAlternative", false)

            }
            if (sSearch) {
                or {
                    ilike('name', sSearch)
                    ilike('description', sSearch)
                }
            }
            order(sortColumn, sSortDir)
            order('name', CommonUtils.SORT_ORDER_ASC)

        }
        int totalCount = results.totalCount
        int serial = iDisplayStart
        if (totalCount > 0) {
            if (sSortDir.equals(CommonUtils.SORT_ORDER_DESC)) {
                serial = (totalCount + 1) - iDisplayStart
            }
            results.each { SubjectName subject ->
                if (sSortDir.equals(CommonUtils.SORT_ORDER_ASC)) {
                    serial++
                } else {
                    serial--
                }
                dataReturns.add([DT_RowId: subject.id, hasChild: subject.hasChild, 0:serial, 1: subject.name, 2: subject.code,3:subject.sortPosition ,4: subject.description, 5: ''])
            }
        }
        return [totalCount: totalCount, results: dataReturns]
    }

    LinkedHashMap childPaginateList(GrailsParameterMap params, SubjectName subjectName) {
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
        def c = SubjectName.createCriteria()
        def results = c.list(max: iDisplayLength, offset: iDisplayStart) {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
                eq("isAlternative", true)
                eq("parentId", subjectName.id)
            }
            if (sSearch) {
                or {
                    ilike('name', sSearch)
                    ilike('description', sSearch)
                }
            }
            order(sortColumn, sSortDir)
            order('name', CommonUtils.SORT_ORDER_ASC)

        }
        int totalCount = results.totalCount
        int serial = iDisplayStart
        if (totalCount > 0) {
            if (sSortDir.equals(CommonUtils.SORT_ORDER_DESC)) {
                serial = (totalCount + 1) - iDisplayStart
            }
            results.each { SubjectName subject ->
                if (sSortDir.equals(CommonUtils.SORT_ORDER_ASC)) {
                    serial++
                } else {
                    serial--
                }
                dataReturns.add([DT_RowId: subject.id,0:serial, 1: subject.name, 2: subject.code,3:subject.sortPosition ,4: subject.description, 5: ''])
            }
        }
        return [totalCount: totalCount, results: dataReturns]
    }
    def allSubjectList(){
        def results = SubjectName.findAllByActiveStatus(ActiveStatus.ACTIVE, [max: 200, sort: "sortPosition", order: "asc"])
    }

    def allMainSubjectDropDownList(){
        def c = SubjectName.createCriteria()
        def results = c.list() {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
                eq("isAlternative", Boolean.FALSE)
            }
            order('sortPosition', CommonUtils.SORT_ORDER_ASC)
        }

        List dataReturns = new ArrayList()
        results.each { obj ->
            dataReturns.add([id: obj.id, name: obj.name])
        }
        return dataReturns
    }
    def allAlterSubjectDropList(SubjectName subjectName){
        def c = SubjectName.createCriteria()
        def results = c.list() {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
               eq("isAlternative", Boolean.TRUE)
                eq("parentId", subjectName.id)
            }
            order('sortPosition', CommonUtils.SORT_ORDER_ASC)
        }

        List dataReturns = new ArrayList()
        results.each { obj ->
            dataReturns.add([id: obj.id, name: obj.name])
        }
        return dataReturns
    }

    def allMainSubjectNotInListDropDown(List ids){
        def c = SubjectName.createCriteria()
        def results = c.list() {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
                eq("isAlternative", Boolean.FALSE)
                if(ids){
                    not {'in'("id",ids)}
                }
            }
            order('sortPosition', CommonUtils.SORT_ORDER_ASC)
        }

        List dataReturns = new ArrayList()
        results.each { obj ->
            dataReturns.add([id: obj.id, name: obj.name])
        }
        return dataReturns
    }

    def allSubjectInListDropDown(String ids){
        if (!ids) {
            return null
        }
        def subjectIds = ids.split(',').collect { it as Long }
        if (!subjectIds) {
            return null
        }
        def query = SubjectName.where {
            (id in subjectIds)
        }
        def results = query.list()

        List dataReturns = new ArrayList()
        for (obj in results) {
            dataReturns.add([id: obj.id, name: obj.name])
        }
        return dataReturns
    }
    def allSubjectInList(String ids){
        if (!ids) {
            return null
        }
        def subjectIds = ids.split(',').collect { it as Long }
        if (!subjectIds) {
            return null
        }
        def query = SubjectName.where {
            (id in subjectIds)
        }
        def results = query.list()
    }

    String getName(String ids) {
        if (!ids) {
            return null
        }
        def subjectIds = ids.split(',').collect { it as Long }
        if (!subjectIds) {
            return null
        }
        def query = SubjectName.where {
            (id in subjectIds)
        }
        def results = query.list()
        String subjectStr = results.collect { it.name }.join(', ')
        return subjectStr
    }

    def getCommaSeparatedIdAsList(String commaSeparatedIds) {
        if (!commaSeparatedIds) return ""
        return commaSeparatedIds.split(",") as List
    }
}
