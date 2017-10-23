package com.grailslab

import com.grailslab.enums.AcaYearType
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.settings.ClassName
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

class ClassNameService {
    static transactional = false

    static final String[] sortColumns = ['sortPosition','name']
    LinkedHashMap classNamePaginateList(GrailsParameterMap params){
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
        def c = ClassName.createCriteria()
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

        String allowOptional
        String isalternative
        if (totalCount > 0) {
            results.each { ClassName className ->
                isalternative = className.isCollegeSection? "Yes" : "No"
                /*terms = ""*/
                allowOptional = ""
                allowOptional+=className.groupsAvailable?"Group, ":""
                allowOptional+=className.allowOptionalSubject?"Optional":""

                dataReturns.add([DT_RowId: className.id, 0: className.sortPosition, 1: className.name,2: allowOptional,3:isalternative,4: className.expectedNumber+' % ', 5:className.hrPeriod? className.hrPeriod.periodRange: "-" ,6:''])
            }
        }
        return [totalCount:totalCount,results:dataReturns]
    }

    def allClassNames(){
        def c = ClassName.createCriteria()
        def results = c.list() {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
            }
            order('sortPosition', CommonUtils.SORT_ORDER_ASC)
        }
        return results
    }

    def classNamesList(Long classNameId){
        def c = ClassName.createCriteria()
        def results = c.list() {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
                if (classNameId) {
                    eq("id", classNameId)
                }
            }
            order('sortPosition', CommonUtils.SORT_ORDER_ASC)
        }
        return results
    }

    def classNameDropDownList(AcaYearType yearType = AcaYearType.SCHOOL){
        def c = ClassName.createCriteria()
        def results = c.list() {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
                if (yearType == AcaYearType.SCHOOL) {
                    eq("isCollegeSection", false)
                } else if (yearType == AcaYearType.COLLEGE){
                    eq("isCollegeSection", true)
                }

            }
            order('sortPosition', CommonUtils.SORT_ORDER_ASC)
        }
        List dataReturns = new ArrayList()
        results.each { ClassName className ->
            dataReturns.add([id: className.id, name: className.name])
        }
        return dataReturns
    }

    def classNameInIdListDD(String classIds, AcaYearType yearType = AcaYearType.SCHOOL){
        if (!classIds) {
            return null
        }
        def ids = classIds.split(',').collect { it as Long }
        if (!ids) {
            return null
        }
        def c = ClassName.createCriteria()
        def results = c.list() {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
                'in'("id", ids)
                if (yearType == AcaYearType.SCHOOL) {
                    eq("isCollegeSection", false)
                } else if (yearType == AcaYearType.COLLEGE){
                    eq("isCollegeSection", true)
                }

            }
            order('sortPosition', CommonUtils.SORT_ORDER_ASC)
        }
        List dataReturns = new ArrayList()
        results.each { ClassName className ->
            dataReturns.add([id: className.id, name: className.name])
        }
        return dataReturns
    }

    def classNameInIdListDDBatch(String classIds, AcaYearType yearType = AcaYearType.SCHOOL){
        if (!classIds) {
            return null
        }
        def ids = classIds.split(',').collect { it as Long }
        if (!ids) {
            return null
        }
        def c = ClassName.createCriteria()
        def results = c.list() {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
                'in'("id", ids)
                if (yearType == AcaYearType.SCHOOL) {
                    eq("isCollegeSection", false)
                } else if (yearType == AcaYearType.COLLEGE){
                    eq("isCollegeSection", true)
                }

            }
            order('sortPosition', CommonUtils.SORT_ORDER_ASC)
        }
        List dataReturns = new ArrayList()
        results.each { ClassName className ->
            dataReturns.add([id: className.id, name: className.name])
        }
        return dataReturns
    }
    def classNameNotInIdListDD(String classIds, AcaYearType yearType = AcaYearType.SCHOOL){
        if (!classIds) {
            return null
        }
        def ids = classIds.split(',').collect { it as Long }
        if (!ids) {
            return null
        }
        def c = ClassName.createCriteria()
        def results = c.list() {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
                not {'in'("id", ids)}
                if (yearType == AcaYearType.SCHOOL) {
                    eq("isCollegeSection", false)
                } else if (yearType == AcaYearType.COLLEGE){
                    eq("isCollegeSection", true)
                }

            }
            order('sortPosition', CommonUtils.SORT_ORDER_ASC)
        }
        List dataReturns = new ArrayList()
        results.each { ClassName className ->
            dataReturns.add([id: className.id, name: className.name])
        }
        return dataReturns
    }

    def nextClasses(ClassName oldClass){
        def c = ClassName.createCriteria()
        def results = c.list() {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
                gt("sortPosition",oldClass.sortPosition)
            }
            order('sortPosition', CommonUtils.SORT_ORDER_ASC)
        }
        return results
    }


}
