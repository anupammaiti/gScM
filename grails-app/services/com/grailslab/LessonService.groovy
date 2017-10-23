package com.grailslab

import com.grailslab.gschoolcore.AcademicYear
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.settings.Lesson
import com.grailslab.settings.LessonWeek
import com.grailslab.settings.School
import com.grailslab.settings.Section
import com.grailslab.settings.SubjectName
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

class LessonService {
    static transactional = false
    def schoolService
    def springSecurityService

    def lessonPaginateList(Section section, Integer loadWeek){
        def c = Lesson.createCriteria()
        def results = c.list() {
            and {
                eq("activeStatus",ActiveStatus.ACTIVE)
                eq("section", section)
                if(loadWeek){
                    eq("weekNumber", loadWeek)
                }
            }
            order("weekNumber", CommonUtils.SORT_ORDER_ASC)
            order("lessonDate", CommonUtils.SORT_ORDER_ASC)
        }
        return results
    }
    def todayWorkingLessonList(Section section, String username){
        Date startDates = new Date();
        def c = Lesson.createCriteria()
        def results = c.list() {
            eq("section", section)
            eq("activeStatus",ActiveStatus.ACTIVE)
            or{
                and {
                    eq("createdBy", username)
                    ge("dateCreated", startDates.clearTime())
                }
                and {
                    eq("updatedBy", username)
                    ge("lastUpdated", startDates.clearTime())
                }
            }

            order("weekNumber", CommonUtils.SORT_ORDER_ASC)
            order("lessonDate", CommonUtils.SORT_ORDER_ASC)
        }
        return results
    }

    def lessonPlanForFeedback(Section section,  Integer weekNumber){
        def c = Lesson.createCriteria()
        def results = c.list() {
            and {
                eq("section", section)
                eq("activeStatus",ActiveStatus.ACTIVE)
                eq("weekNumber", weekNumber)
            }
            order("lessonDate", CommonUtils.SORT_ORDER_ASC)
        }
        return results
    }

    Lesson getLesson(Section section, Date lessonDate){
        Lesson lesson=Lesson.findBySectionAndLessonDate(section,lessonDate)
        return lesson
    }
    def lessonWeeksList(Section section){
        def c = Lesson.createCriteria()
        def lessonWeeks = c.list() {
            and {
                eq("activeStatus",ActiveStatus.ACTIVE)
                eq("section", section)
            }
            projections {
                distinct("weekNumber")
            }
            order("weekNumber", CommonUtils.SORT_ORDER_DESC)
        }as List<Integer>
        List dataReturns = new ArrayList()
        lessonWeeks.each { obj ->
            dataReturns.add([id: obj, name: "Week No ${obj}"])
        }
        return dataReturns
    }
    def lessonWeekListForFeedback(Section section){
        def c = Lesson.createCriteria()
        def lessonWeeks = c.list() {
            and {
                eq("activeStatus",ActiveStatus.ACTIVE)
                eq("section", section)
            }
            projections {
                distinct("weekNumber")
            }
            order("weekNumber", CommonUtils.SORT_ORDER_DESC)
        }as List<Integer>
        List dataReturns = new ArrayList()
        lessonWeeks.each { obj ->
            dataReturns.add([id: obj, name: "Week No ${obj}"])
        }
        return dataReturns
    }

    LessonWeek getWeek(Date workingDate){
        LessonWeek.findByActiveStatusAndStartDateLessThanEqualsAndEndDateGreaterThanEquals(ActiveStatus.ACTIVE, workingDate, workingDate)
    }

    LessonWeek getWeekByWeekNo(Integer weekNo, AcademicYear academicYear = null){
        LessonWeek.findByWeekNumberAndActiveStatusAndAcademicYear(weekNo, ActiveStatus.ACTIVE, academicYear?: schoolService.schoolWorkingYear())
    }

    static final String[] sortLessonWeekColumns = ['id', 'weekNumber','startDate', 'endDate']

    LinkedHashMap lessonWeekPaginateList(GrailsParameterMap params) {
        int iDisplayStart = params.iDisplayStart ? params.getInt('iDisplayStart') : CommonUtils.DEFAULT_PAGINATION_START
        int iDisplayLength = params.iDisplayLength ? params.getInt('iDisplayLength') : CommonUtils.DEFAULT_PAGINATION_LENGTH
        String sSortDir = params.sSortDir_0 ? params.sSortDir_0 : CommonUtils.DEFAULT_PAGINATION_SORT_ORDER
        int iSortingCol = params.iSortCol_0 ? params.getInt('iSortCol_0') : 1//CommonUtils.DEFAULT_PAGINATION_SORT_IDX
        //Search string, use or logic to all fields that required to include
        String sSearch = params.sSearch ? params.sSearch : null
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }
        String sortColumn = CommonUtils.getSortColumn(sortLessonWeekColumns, iSortingCol)
        List dataReturns = new ArrayList()

        def workingYears = schoolService.workingYears()
        def c = LessonWeek.createCriteria()
        def results = c.list(max: iDisplayLength, offset: iDisplayStart) {
            and {
                'in'("academicYear", workingYears)
                eq("activeStatus",ActiveStatus.ACTIVE)
            }
            /*if (sSearch) {
                or {
                    ilike('name', sSearch)
                }
            }*/
            order(sortColumn, sSortDir)
        }
        int totalCount = results.totalCount
        int serial = iDisplayStart;
        if (totalCount > 0) {
            if (sSortDir.equals(CommonUtils.SORT_ORDER_DESC)) {
                serial = (totalCount + 1) - iDisplayStart
            }
            results.each { LessonWeek lWeek ->
                if (sSortDir.equals(CommonUtils.SORT_ORDER_ASC)) {
                    serial++
                } else {
                    serial--
                }
                dataReturns.add([DT_RowId: lWeek.id, 0: serial, 1: lWeek.weekNumber,2: CommonUtils.getUiDateStr(lWeek.startDate), 3: CommonUtils.getUiDateStr(lWeek.endDate), 4: ''])
            }
        }
        return [totalCount: totalCount, results: dataReturns]
    }
}