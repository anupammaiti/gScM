package com.grailslab

import com.grailslab.enums.EventType
import com.grailslab.enums.RepeatType
import com.grailslab.gschoolcore.AcademicYear
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.settings.Holiday
import com.grailslab.settings.School
import grails.transaction.Transactional
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.joda.time.DateTime

class HolidayService {
    static transactional = false
    def schoolService
    def calenderService
    def grailsApplication

    static final String[] sortColumns = ['id', 'name','eventType', 'startDate', 'endDate']

    LinkedHashMap holidayNamePaginateList(GrailsParameterMap params) {
        int iDisplayStart = params.iDisplayStart ? params.getInt('iDisplayStart') : CommonUtils.DEFAULT_PAGINATION_START
        int iDisplayLength = params.iDisplayLength ? params.getInt('iDisplayLength') : CommonUtils.DEFAULT_PAGINATION_LENGTH
        String sSortDir = params.sSortDir_0 ? params.sSortDir_0 : CommonUtils.DEFAULT_PAGINATION_SORT_ORDER
        int iSortingCol = params.iSortCol_0 ? params.getInt('iSortCol_0') : CommonUtils.DEFAULT_PAGINATION_SORT_IDX
        //Search string, use or logic to all fields that required to include
        String sSearch = params.sSearch ? params.sSearch : null
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }
        String sortColumn = CommonUtils.getSortColumn(sortColumns, iSortingCol)
        List dataReturns = new ArrayList()

        AcademicYear workingYear = schoolService.workingYear()
        def c = Holiday.createCriteria()
        def results = c.list(max: iDisplayLength, offset: iDisplayStart) {
            and {
                eq("academicYear", workingYear)
                eq("activeStatus",ActiveStatus.ACTIVE)
            }
            if (sSearch) {
                or {
                    ilike('name', sSearch)
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
            results.each { Holiday holiday ->
                if (sSortDir.equals(CommonUtils.SORT_ORDER_ASC)) {
                    serial++
                } else {
                    serial--
                }
                dataReturns.add([DT_RowId: holiday.id, 0: serial, 1: holiday.name,2:holiday.eventType.value, 3: CommonUtils.getUiDateStr(holiday.startDate), 4: CommonUtils.getUiDateStr(holiday.endDate), 5: ''])
            }
        }
        return [totalCount: totalCount, results: dataReturns]
    }

    @Transactional
    Holiday saveHoliday(Holiday newHoliday, boolean isEdit = false) {
        Holiday savedHoliday = newHoliday.save(flush: true)
        if(savedHoliday){
            def calEvents = calenderService.saveHolidayEvents(savedHoliday,isEdit)
        }
        return savedHoliday
    }

    def getByDate(DateTime theDate) {
        if (!theDate) return null
        Date searchDate = theDate.toDate()
        String name
        def results = Holiday.createCriteria().list {
            eq("eventType", EventType.HOLIDAY)
            eq("activeStatus", ActiveStatus.ACTIVE)
            'le'('startDate', searchDate)
            'ge'('endDate', searchDate)
        }
        if (results){
            def holidays = results.collect{it.name}
            return [name: holidays.join(", ")]
        }
        return null
    }

    def isHoliday(Date searchDate) {
        String name
        Boolean isHoliday=false
        Long holidayId
        def results = Holiday.createCriteria().list {
            eq("eventType", EventType.HOLIDAY)
            eq("activeStatus", ActiveStatus.ACTIVE)
            'le'('startDate', searchDate)
            'ge'('endDate', searchDate)
        }
        if (results){
            results.each {Holiday holiday->
                name+=holiday.name
                isHoliday=true
                holidayId=holiday.id
            }
            return [name:name,isHoliday:isHoliday,id:holidayId]
        }
        return null
    }

    def isExamDay(Date searchDate) {
        String name
        Boolean isHoliday=false
        def results = Holiday.createCriteria().list {
            eq("eventType", EventType.Exam_Days)
            eq("activeStatus", ActiveStatus.ACTIVE)
            'le'('startDate', searchDate)
            'ge'('endDate', searchDate)
        }
        if (results){
            results.each {Holiday holiday->
                name=holiday.name
                isHoliday=true
            }
            return [name:name,isHoliday:isHoliday]
        }
        return false
    }
    int isNoClassDay(Date theDay) {
        def results = Holiday.createCriteria().count() {
            eq("activeStatus", ActiveStatus.ACTIVE)
            'le'('startDate', theDay)
            'ge'('endDate', theDay)
        } as Integer
        return results
    }
}
