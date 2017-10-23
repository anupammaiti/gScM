package com.grailslab

import com.grailslab.enums.OlympiadType
import com.grailslab.festival.FesProgram
import com.grailslab.gschoolcore.AcademicYear
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.settings.ClassName
import com.grailslab.stmgmt.RegForm
import grails.transaction.Transactional
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

@Transactional
class AdmissionFormService {
    static transactional = false
    def grailsApplication
    def schoolService

    static final String[] sortColumns = ['id','className','applyType','regOpenDate','regCloseDate']

    LinkedHashMap paginateList(GrailsParameterMap params) {
        int iDisplayStart = params.iDisplayStart ? params.getInt('iDisplayStart') : CommonUtils.DEFAULT_PAGINATION_START
        int iDisplayLength = params.iDisplayLength ? params.getInt('iDisplayLength') : CommonUtils.DEFAULT_PAGINATION_LENGTH
        String sSortDir = params.sSortDir_0 ? params.sSortDir_0 : CommonUtils.SORT_ORDER_DESC
        int iSortingCol = params.iSortCol_0 ? params.getInt('iSortCol_0') : CommonUtils.DEFAULT_PAGINATION_SORT_IDX
        //Search string, use or logic to all fields that required to include
        String sSearch = params.sSearch ? params.sSearch : null
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }
        String sortColumn = CommonUtils.getSortColumn(sortColumns, iSortingCol)
        List dataReturns = new ArrayList()
        def c = RegForm.createCriteria()
        def results = c.list(max: iDisplayLength, offset: iDisplayStart) {
            createAlias('className', 'cls')
            eq("activeStatus", ActiveStatus.ACTIVE)
            eq('academicYear', schoolService.schoolAdmissionYear())
            if (sSearch) {
                or {
                    ilike('applyType', sSearch)
                    ilike('cls.name', sSearch)
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
            results.each { RegForm obj ->
                if (sSortDir.equals(CommonUtils.SORT_ORDER_ASC)) {
                    serial++
                } else {
                    serial--
                }
                dataReturns.add([DT_RowId: obj.id, 0: serial, 1: obj.className.name, 2: obj.applyType, 3: CommonUtils.getUiDateStr(obj.regOpenDate), 4: CommonUtils.getUiDateStr(obj.regCloseDate), 5: CommonUtils.getDateRange(obj.fromBirthDate, obj.toBirthDate),6:''])
            }
        }
        return [totalCount: totalCount, results: dataReturns]
    }
    def classesDropDown(AcademicYear academicYear = null) {
        def c = RegForm.createCriteria()
        def results = c.list() {
            createAlias('className', 'cls')
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
                eq('academicYear', schoolService.schoolAdmissionYear())
            }
            order("cls.sortPosition", "asc")
        }
        List dataReturns = new ArrayList()
        ClassName className
        for (regForm in results) {
            className = regForm.className
            dataReturns.add([id: className.id, name: className.name])
        }
        return dataReturns
    }




//       def regFormList() {
//       def regFormReportList = RegForm.findAllByActiveStatus(ActiveStatus.ACTIVE, [max: 10, sort: "id", order: "desc"])
//   }



}
