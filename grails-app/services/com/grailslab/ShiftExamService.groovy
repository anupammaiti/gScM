package com.grailslab

import com.grailslab.enums.ExamStatus
import com.grailslab.gschoolcore.AcademicYear
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.settings.ShiftExam
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

class ShiftExamService {
    static transactional = false
    def schoolService

    static final String[] sortColumns = ['id','examTerm','resultPublishOn']
    LinkedHashMap examPaginateList(GrailsParameterMap params){
        int iDisplayStart = params.iDisplayStart ? params.getInt('iDisplayStart') : CommonUtils.DEFAULT_PAGINATION_START
        int iDisplayLength = params.iDisplayLength ? params.getInt('iDisplayLength') : CommonUtils.DEFAULT_PAGINATION_LENGTH
        String sSortDir = params.sSortDir_0 ? params.sSortDir_0 : CommonUtils.SORT_ORDER_DESC
        int iSortingCol = params.iSortCol_0 ? params.getInt('iSortCol_0') : CommonUtils.DEFAULT_PAGINATION_SORT_IDX
        String sSearch = params.sSearch ? params.sSearch : null
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }

        AcademicYear academicYear = schoolService.schoolWorkingYear()
        if(params.academicYear){
            academicYear = AcademicYear.valueOf(params.academicYear)
        }

        String sortColumn = CommonUtils.getSortColumn(sortColumns,iSortingCol)
        List dataReturns = new ArrayList()
        def c = ShiftExam.createCriteria()
        def results = c.list(max: iDisplayLength, offset: iDisplayStart) {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
                eq("academicYear", academicYear)
                eq("examStatus", ExamStatus.NEW)
            }
            if (sSearch) {
                or {
                    ilike('examName', sSearch)
                }
            }
            order(sortColumn, sSortDir)
        }
        int totalCount = results.totalCount

        if (totalCount > 0) {
            String publishDate
            String examType
            String weightOnResult
            for (shiftExam in results) {
                publishDate = CommonUtils.getUiDateStr(shiftExam?.resultPublishOn)
                examType = shiftExam.isCtExam?"CT Exam, ":""
                examType += shiftExam.isHallExam?"Hall Exam":""
                weightOnResult = shiftExam.weightOnResult? "${shiftExam.weightOnResult}%":"0%"
                dataReturns.add([DT_RowId: shiftExam.id, 0: shiftExam.examName, 1: examType, 2: shiftExam.examTerm?.value, 3:weightOnResult, 4:publishDate , 5:''])
            }
        }
        return [totalCount:totalCount,results:dataReturns]
    }
    def examNameDropDown(AcademicYear academicYear = null) {
        def workingYears = schoolService.workingYears()
        def examList
        if (academicYear) {
            examList = ShiftExam.findAllByActiveStatusAndExamStatusAndAcademicYear(ActiveStatus.ACTIVE, ExamStatus.NEW, academicYear, [max: 10, sort: "id", order: "desc"])
        } else {
            examList = ShiftExam.findAllByActiveStatusAndExamStatusAndAcademicYearInList(ActiveStatus.ACTIVE, ExamStatus.NEW, workingYears, [max: 10, sort: "id", order: "desc"])
        }
        List dataReturns = new ArrayList()
        for (shiftExam in examList) {
            dataReturns.add([id: shiftExam.id, name: shiftExam.examName])
        }
        dataReturns
    }
}
