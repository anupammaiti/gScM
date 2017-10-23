package com.grailslab

import com.grailslab.enums.*
import com.grailslab.gschoolcore.AcademicYear
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.settings.ClassName
import com.grailslab.settings.School
import com.grailslab.settings.Section
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import sun.rmi.runtime.Log

class SectionService {
    static transactional = false
    def schoolService

    static final String[] sortColumns = ['n.sortPosition','name','className','shift']
    LinkedHashMap sectionPaginateList(GrailsParameterMap params){
        int iDisplayStart = params.iDisplayStart ? params.getInt('iDisplayStart') : CommonUtils.DEFAULT_PAGINATION_START
        int iDisplayLength = params.iDisplayLength ? params.getInt('iDisplayLength') : CommonUtils.DEFAULT_PAGINATION_LENGTH
        String sSortDir = params.sSortDir_0 ? params.sSortDir_0 : CommonUtils.SORT_ORDER_ASC
        int iSortingCol = params.iSortCol_0 ? params.getInt('iSortCol_0') : CommonUtils.DEFAULT_PAGINATION_SORT_IDX
        //Search string, use or logic to all fields that required to include
        String sSearch = params.sSearch ? params.sSearch : null
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }
        AcademicYear academicYear
        if(params.academicYear) {
            academicYear = AcademicYear.valueOf(params.academicYear)
        } else {
            academicYear = schoolService.schoolWorkingYear()
        }
        String sortColumn = CommonUtils.getSortColumn(sortColumns,iSortingCol)
        List dataReturns = new ArrayList()
        def c = Section.createCriteria()
        def results = c.list(max: iDisplayLength, offset: iDisplayStart) {
            createAlias('className', 'n')
            and {
                eq("academicYear", academicYear)
                eq("activeStatus", ActiveStatus.ACTIVE)
            }
            if (sSearch) {
                or {
                    ilike('name', sSearch)
                    ilike('n.name', sSearch)
                }
            }
            order(sortColumn, sSortDir)
            order('id', CommonUtils.SORT_ORDER_ASC)
        }
        int totalCount = results.totalCount
        int serial = iDisplayStart;
        if (totalCount > 0) {
            if (sSortDir.equals(CommonUtils.SORT_ORDER_DESC)) {
                serial = (totalCount + 1) - iDisplayStart
            }
            String className
            results.each { Section section ->
                if (sSortDir.equals(CommonUtils.SORT_ORDER_ASC)) {
                    serial++
                } else {
                    serial--
                }
                if (section.groupName){
                    className=section.className.name+" ( "+ section.groupName+" )"
                }else {
                    className=section.className.name
                }
                dataReturns.add([DT_RowId: section.id, 0: serial, 1: className, 2: section.name,3: section?.employee?.name,4:section?.shift?.value, 5: ''])
            }
        }
        return [totalCount:totalCount,results:dataReturns]
    }
    def allSections(GrailsParameterMap params){
        def workingYears = schoolService.workingYears()
        AcademicYear academicYear= null
        if(params.academicYear){
            academicYear = AcademicYear.valueOf(params.academicYear)
        }

        def c = Section.createCriteria()
        def results = c.list() {
            createAlias('className', 'n')
            and {
                if (academicYear) {
                    eq("academicYear", academicYear)
                }else {
                    'in'("academicYear", workingYears)
                }
                eq("activeStatus", ActiveStatus.ACTIVE)
            }
            order('n.sortPosition', CommonUtils.SORT_ORDER_ASC)
            order('name', CommonUtils.SORT_ORDER_ASC)
        }

        return results
    }
    def classSections(ClassName className, AcademicYear academicYear = null){
        AcademicYear workingYear = schoolService.workingYear(className)
        if(!academicYear){
            academicYear = workingYear
        }
        def c = Section.createCriteria()
        def results = c.list() {
            and {
                eq("academicYear", academicYear)
                eq("activeStatus", ActiveStatus.ACTIVE)
                eq("className", className)
            }
            order('id', CommonUtils.SORT_ORDER_ASC)
        }

        return results
    }

    def groupSections(ClassName className, GroupName groupName = null, AcademicYear academicYear = null){
        AcademicYear workingYear = schoolService.workingYear(className)
        if(!academicYear){
            academicYear = workingYear
        }
        def c = Section.createCriteria()
        def results = c.list() {
            and {
                eq("academicYear", academicYear)
                eq("activeStatus", ActiveStatus.ACTIVE)
                eq("className", className)
                if (groupName) {
                    eq("groupName", groupName)
                }
            }
            order('id', CommonUtils.SORT_ORDER_ASC)
        }

        return results
    }

    def groupSectionsBatchPromotion(ClassName className, GroupName groupName = null, AcademicYear academicYear = null){
        AcademicYear workingYear = schoolService.workingYear(className)
        if(!academicYear){
            academicYear = workingYear
        }
        def c = Section.createCriteria()
        def results = c.list() {
            and {
                eq("academicYear", academicYear)
                eq("activeStatus", ActiveStatus.ACTIVE)
                eq("className", className)
                if (groupName) {
                    eq("groupName", groupName)
                }
            }
            order('id', CommonUtils.SORT_ORDER_ASC)
        }

        return results
    }

    def allSectionsByClassNameAndShift(ClassName className, Shift shift, AcademicYear academicYear = null){
        AcademicYear workingYear = schoolService.workingYear(className)
        if(!academicYear){
            academicYear = workingYear
        }
        def c = Section.createCriteria()
        def results = c.list() {
            createAlias('className', 'n')
            and {
                eq("academicYear", academicYear)
                eq("activeStatus", ActiveStatus.ACTIVE)
                eq("className", className)
                eq("shift", shift)
            }
            order('n.sortPosition', CommonUtils.SORT_ORDER_ASC)
            order('name', CommonUtils.SORT_ORDER_ASC)
        }

        return results
    }

    def allSectionsByClassNameAndShiftDDList(ClassName className, Shift shift, AcademicYear academicYear = null){
        AcademicYear workingYear = schoolService.workingYear(className)
        if(!academicYear){
            academicYear = workingYear
        }
        def c = Section.createCriteria()
        def results = c.list() {
            createAlias('className', 'n')
            and {
                eq("academicYear", academicYear)
                eq("activeStatus", ActiveStatus.ACTIVE)
                eq("className", className)
                eq("shift", shift)
            }
            order('n.sortPosition', CommonUtils.SORT_ORDER_ASC)
            order('name', CommonUtils.SORT_ORDER_ASC)
        }

        List dataReturns = new ArrayList()
        results.each { obj ->
            dataReturns.add([id: obj.id, name: "${obj.className.name} - ${obj.name}"])
        }
        return dataReturns
    }

    def allSectionsByClassNameAndShiftAndGroup(ClassName className, Shift shift,GroupName groupName , AcademicYear academicYear = null){
        AcademicYear workingYear = schoolService.workingYear(className)
        if(!academicYear){
            academicYear = workingYear
        }
        def c = Section.createCriteria()
        def results = c.list() {
            createAlias('className', 'n')
            and {
                eq("academicYear", academicYear)
                eq("activeStatus", ActiveStatus.ACTIVE)
                eq("className", className)
                eq("shift", shift)
                eq("groupName", groupName)
            }
            order('n.sortPosition', CommonUtils.SORT_ORDER_ASC)
            order('name', CommonUtils.SORT_ORDER_ASC)
        }

        return results
    }
    def allSectionsByShift(Shift shift, AcademicYear academicYear = null){
        def workingYears = schoolService.workingYears()

        def c = Section.createCriteria()
        def results = c.list() {
            createAlias('className', 'n')
            and {
                if (academicYear) {
                    eq("academicYear", academicYear)
                }else {
                    'in'("academicYear", workingYears)
                }
                eq("activeStatus", ActiveStatus.ACTIVE)
                eq("shift", shift)
            }
            order('n.sortPosition', CommonUtils.SORT_ORDER_ASC)
            order('name', CommonUtils.SORT_ORDER_ASC)
        }

        return results
    }

    def sectionDropDownList(AcademicYear academicYear = null){
        def workingYears = schoolService.workingYears()
        def c = Section.createCriteria()
        def results = c.list() {
            createAlias('className', 'n')
            and {
                if (academicYear) {
                    eq("academicYear", academicYear)
                }else {
                    'in'("academicYear", workingYears)
                }
                eq("activeStatus", ActiveStatus.ACTIVE)
            }
            order('n.sortPosition', CommonUtils.SORT_ORDER_ASC)
            order('name', CommonUtils.SORT_ORDER_ASC)
        }
        List dataReturns = new ArrayList()
        results.each { obj ->
            dataReturns.add([id: obj.id, name: "${obj.className.name} - ${obj.name}"])
        }
        return dataReturns
    }

    def sectionDropDownNotInList(AcademicYear academicYear = null, List sectionIds){
        def workingYears = schoolService.workingYears()
        def c = Section.createCriteria()
        def results = c.list() {
            createAlias('className', 'n')
            and {
                if (academicYear) {
                    eq("academicYear", academicYear)
                }else {
                    'in'("academicYear", workingYears)
                }
                if(sectionIds){
                    not{'in'("id", sectionIds)}
                }
                eq("activeStatus", ActiveStatus.ACTIVE)
            }
            order('n.sortPosition', CommonUtils.SORT_ORDER_ASC)
            order('name', CommonUtils.SORT_ORDER_ASC)
        }
        List dataReturns = new ArrayList()
        results.each { obj ->
            dataReturns.add([id: obj.id, name: "${obj.className.name} - ${obj.name}"])
        }
        return dataReturns
    }

    def sectionNameDropDownList(AcademicYear academicYear = null){
        def workingYears = schoolService.workingYears()

        def c = Section.createCriteria()
        def results = c.list() {
            createAlias('className', 'n')
            and {
                if(academicYear){
                    eq("academicYear", academicYear)
                } else {
                    'in'("academicYear", workingYears)
                }
                eq("activeStatus", ActiveStatus.ACTIVE)
            }
            order('n.sortPosition', CommonUtils.SORT_ORDER_ASC)
            order('name', CommonUtils.SORT_ORDER_ASC)
        }
        List dataReturns = new ArrayList()
        results.each { obj ->
            dataReturns.add([id: obj.name, name: "${obj.className.name} - ${obj.name}"])
        }
        return dataReturns
    }

    def classSectionsDDList(ClassName className, AcademicYear academicYear = null){
        if(!academicYear){
            academicYear = schoolService.workingYear(className)
        }
        def c = Section.createCriteria()
        def results = c.list() {
            createAlias('className', 'n')
            and {
                eq("academicYear", academicYear)
                eq("activeStatus", ActiveStatus.ACTIVE)
                eq("className", className)
            }
            order('n.sortPosition', CommonUtils.SORT_ORDER_ASC)
            order('id', CommonUtils.SORT_ORDER_ASC)
        }

        List dataReturns = new ArrayList()
        results.each { obj ->
            dataReturns.add([id: obj.id, name: obj.name])
        }
        return dataReturns
    }

    def classSectionsDDList(ClassName className, GroupName groupName, AcademicYear academicYear){
        if(!academicYear){
            academicYear = schoolService.workingYear(className)
        }
        def c = Section.createCriteria()
        def results = c.list() {
            createAlias('className', 'n')
            and {
                eq("academicYear", academicYear)
                eq("activeStatus", ActiveStatus.ACTIVE)
                eq("className", className)
                if (groupName) {
                    eq("groupName", groupName)
                }
            }
            order('n.sortPosition', CommonUtils.SORT_ORDER_ASC)
            order('id', CommonUtils.SORT_ORDER_ASC)
        }

        List dataReturns = new ArrayList()
        results.each { obj ->
            dataReturns.add([id: obj.id, name: obj.name])
        }
        return dataReturns
    }

    def sectionWithOptSubjectDDList(AcademicYear academicYear = null){
        def workingYears = schoolService.workingYears()
        def c = Section.createCriteria()
        def results = c.list() {
            createAlias('className', 'n')
            and {
                if(academicYear){
                    eq("academicYear", academicYear)
                } else {
                    'in'("academicYear", workingYears)
                }
                eq("activeStatus", ActiveStatus.ACTIVE)
                eq("n.allowOptionalSubject", Boolean.TRUE)
            }
            order('n.sortPosition', CommonUtils.SORT_ORDER_ASC)
            order('name', CommonUtils.SORT_ORDER_ASC)
        }
        List dataReturns = new ArrayList()
        results.each { obj ->
            dataReturns.add([id: obj.id, name: "${obj.className.name} - ${obj.name}"])
        }
        return dataReturns
    }

    def classNameDropDownListByShift(Shift shift, AcademicYear academicYear = null){
        def workingYears = schoolService.workingYears()
        def c = Section.createCriteria()
        def results = c.list() {
            createAlias('className', 'n')
            and {
                if(academicYear){
                    eq("academicYear", academicYear)
                } else {
                    'in'("academicYear", workingYears)
                }
                eq("activeStatus", ActiveStatus.ACTIVE)
                eq("shift", shift)
            }
            order('n.sortPosition', CommonUtils.SORT_ORDER_ASC)
            order('name', CommonUtils.SORT_ORDER_ASC)
        }
        Set dataReturns = new HashSet()
        ClassName className
        results.each { Section section ->
            className = section.className
            dataReturns.add([id: className.id, name: className.name])
        }
        return dataReturns
    }
    List<Long> sectionIdsByShift(List<Shift> shiftList, AcademicYear academicYear = null){
        def workingYears = schoolService.workingYears()
        def c = Section.createCriteria()
        List<Long> sectionIds = c.list() {
            and {
                if(academicYear){
                    eq("academicYear", academicYear)
                } else {
                    'in'("academicYear", workingYears)
                }
                eq("activeStatus", ActiveStatus.ACTIVE)
                'in'("shift", shiftList)
            }
            projections {
                property "id"
            }
        } as List
        return sectionIds
    }
    List<Long> sectionIdsByClassId(List<Long> classIds, AcademicYear academicYear = null){
        def workingYears = schoolService.workingYears()
        def c = Section.createCriteria()
        List<Long> sectionIds = c.list() {
            createAlias('className', 'n')
            and {
                if(academicYear){
                    eq("academicYear", academicYear)
                } else {
                    'in'("academicYear", workingYears)
                }
                eq("activeStatus", ActiveStatus.ACTIVE)
                'in'("n.id", classIds)
            }
            projections {
                property "id"
            }
        } as List
        return sectionIds
    }

    def sectionsByClassIds(String classIds, AcademicYear academicYear = null){
        if (!classIds) {
            return null
        }
        def ids = classIds.split(',').collect { it as Long }
        if (!ids) {
            return null
        }
        def workingYears = schoolService.workingYears()

        def c = Section.createCriteria()
        def results = c.list() {
            createAlias('className', 'n')
            and {
                if (academicYear) {
                    eq("academicYear", academicYear)
                }else {
                    'in'("academicYear", workingYears)
                }
                eq("activeStatus", ActiveStatus.ACTIVE)
                'in'("n.id", ids)

            }
            order('n.sortPosition', CommonUtils.SORT_ORDER_ASC)
            order('name', CommonUtils.SORT_ORDER_ASC)
        }

        return results
    }

    def allSectionsByShiftDDList(Shift shift, AcademicYear academicYear = null){
        def workingYears = schoolService.workingYears()
        def c = Section.createCriteria()
        def results = c.list() {
            createAlias('className', 'n')
            and {
                if(academicYear){
                    eq("academicYear", academicYear)
                } else {
                    'in'("academicYear", workingYears)
                }
                eq("activeStatus", ActiveStatus.ACTIVE)
                eq("shift", shift)
            }
            order('n.sortPosition', CommonUtils.SORT_ORDER_ASC)
            order('name', CommonUtils.SORT_ORDER_ASC)
        }

        List dataReturns = new ArrayList()
        results.each { obj ->
            dataReturns.add([id: obj.id, name: "${obj.className.name} - ${obj.name}"])
        }
        return dataReturns
    }

    def allSectionsByClassId(Long  clasId, AcademicYear academicYear = null){
        ClassName className=ClassName.read(clasId)
        if(!academicYear){
            academicYear = schoolService.workingYear(className)
        }
        def c = Section.createCriteria()
        def results = c.list() {
            createAlias('className', 'n')
            and {
                eq("academicYear", academicYear)
                eq("activeStatus", ActiveStatus.ACTIVE)
                eq("className", className)
            }
            order('n.sortPosition', CommonUtils.SORT_ORDER_ASC)
            order('name', CommonUtils.SORT_ORDER_ASC)
        }

        List dataReturns = new ArrayList()
        results.each { obj ->
            dataReturns.add([id: obj.id, name: "${obj.name}"])
        }
        return dataReturns
    }

}