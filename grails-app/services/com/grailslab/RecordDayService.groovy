package com.grailslab

import com.grailslab.attn.AttnRecordDay
import com.grailslab.enums.AttendanceType
import com.grailslab.gschoolcore.ActiveStatus
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.joda.time.DateTime
import org.joda.time.DateTimeConstants

class RecordDayService {

    static transactional = false
    def commonService
    def holidayService
    static final String[] sortColumns = ['recordDate','dayType','workingDayType','lastUploadOn','holidayName']
    LinkedHashMap paginateList(GrailsParameterMap params) {
        int iDisplayStart = params.iDisplayStart ? params.getInt('iDisplayStart') : CommonUtils.DEFAULT_PAGINATION_START
        int iDisplayLength = params.iDisplayLength ? params.getInt('iDisplayLength') : CommonUtils.DEFAULT_PAGINATION_LENGTH
        String sSortDir = params.sSortDir_0 ? params.sSortDir_0 : CommonUtils.SORT_ORDER_DESC
        int iSortingCol = params.iSortCol_0 ? params.getInt('iSortCol_0') : CommonUtils.DEFAULT_PAGINATION_SORT_IDX
        String sSearch = params.sSearch ? params.sSearch : null
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }
        String sortColumn = CommonUtils.getSortColumn(sortColumns, iSortingCol)
        List dataReturns = new ArrayList()

        def c = AttnRecordDay.createCriteria()
        def results = c.list(max: iDisplayLength, offset: iDisplayStart) {
            if (sSearch) {
                or {
                    ilike('holidayName', sSearch)
                }
            }
            order(sortColumn, sSortDir)
        }
        int totalCount = results.totalCount
        int serial = iDisplayStart;
        if (totalCount > 0) {
            String loadRecordDate
            String lastUploadOn
            Integer recordAdded
            String workDay
            results.each { AttnRecordDay record ->
                loadRecordDate = CommonUtils.getUiDateStr(record.recordDate)
                lastUploadOn = CommonUtils.getUiTimeStr12Hr(record.lastUploadOn)
                recordAdded = 23
                if (record.dayType == AttendanceType.Open_Day) {
                    workDay = record.workingDayType.value
                } else {
                    workDay = record.holidayName
                }
                dataReturns.add([DT_RowId: record.id, 0: loadRecordDate, 1: record.dayType.value, 2:workDay, 3: lastUploadOn, 4:recordAdded, 5:''])
            }
        }
        return [totalCount: totalCount, results: dataReturns]
    }
    def addRecordDay(Date startDate, Date endDate) {
        DateTime startDt = new DateTime(startDate)
        DateTime endDt = new DateTime(endDate).plusHours(23)
        DateTime tempDate = new DateTime(startDt.getMillis());
        def weeklyList = commonService.weeklyHolidays()
        int dayOfWeek
        List<AttnRecordDay> attRecordDayList = new ArrayList<AttnRecordDay>()
        AttnRecordDay theRecord
        def holidayObj
        while(tempDate.compareTo(endDt) <=0 ){
            dayOfWeek = tempDate.getDayOfWeek()
            if(dayOfWeek ==  DateTimeConstants.FRIDAY && weeklyList.contains("Fri")){
                theRecord = new AttnRecordDay(recordDate:tempDate.toDate(), isClassDay:false, forceLoad: false, activeStatus:ActiveStatus.ACTIVE, dayType: AttendanceType.Weekly_Holiday, holidayName:"Friday")
                attRecordDayList.add(theRecord)
            } else if(dayOfWeek ==  DateTimeConstants.SATURDAY && weeklyList.contains("Sat")){
                theRecord = new AttnRecordDay(recordDate:tempDate.toDate(), isClassDay:false, forceLoad: false, activeStatus:ActiveStatus.ACTIVE, dayType: AttendanceType.Weekly_Holiday, holidayName:"Saturday")
                attRecordDayList.add(theRecord)
            } else if(dayOfWeek ==  DateTimeConstants.SUNDAY && weeklyList.contains("Sun")){
                theRecord = new AttnRecordDay(recordDate:tempDate.toDate(), isClassDay:false, forceLoad: false, activeStatus:ActiveStatus.ACTIVE, dayType: AttendanceType.Weekly_Holiday, holidayName:"Sunday")
                attRecordDayList.add(theRecord)
            } else {
                holidayObj = holidayService.getByDate(tempDate)
                if (holidayObj) {
                    theRecord = new AttnRecordDay(recordDate:tempDate.toDate(), isClassDay:false, forceLoad: false, activeStatus:ActiveStatus.ACTIVE, dayType: AttendanceType.Holiday, holidayName:holidayObj.name)
                    attRecordDayList.add(theRecord)
                } else {
                    theRecord = new AttnRecordDay(recordDate:tempDate.toDate(), isClassDay:true, forceLoad: true, activeStatus:ActiveStatus.ACTIVE, dayType: AttendanceType.Open_Day, workingDayType: AttendanceType.Regular)
                    attRecordDayList.add(theRecord)
                }
            }
            tempDate = tempDate.plusDays(1);
        }
        attRecordDayList.each {attnRecordDay ->
            try {
                theRecord = AttnRecordDay.findByRecordDate(attnRecordDay.recordDate)
                if (theRecord) {
                    theRecord.properties = attnRecordDay.properties
                    theRecord.save()
                } else {
                    attnRecordDay.save()
                }
            } catch (Exception ex){
                log.error("addRecordDay : failed to save AttnRecordDay"+ex.getMessage())
            }
        }
    }
    def recordDayIdsList(Date startDate, Date endDate){
        def c = AttnRecordDay.createCriteria()
        def results = c.list() {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
                between("recordDate", startDate, endDate)
            }
            projections {
                property('id')
            }
            order("recordDate", CommonUtils.SORT_ORDER_ASC)
        }
        return results
    }

    def recordDayList(Date startDate, Date endDate){
        def c = AttnRecordDay.createCriteria()
        def results = c.list() {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
                between("recordDate", startDate, endDate)
            }
            order("recordDate", CommonUtils.SORT_ORDER_ASC)
        }
        return results
    }

    int workingDay(Date startDate, Date endDate){
        def c = AttnRecordDay.createCriteria()
        def results = c.list() {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
                eq("dayType", AttendanceType.Open_Day)
                between("recordDate", startDate, endDate)
            }
            projections {
                count()
            }
        }
        return results[0]
    }
    int holidayCount(Date startDate, Date endDate){
        def c = AttnRecordDay.createCriteria()
        def results = c.list() {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
                'ne'("dayType", AttendanceType.Open_Day)
                between("recordDate", startDate, endDate)
            }
            projections {
                count()
            }
        }
        return results[0]
    }

    AttnRecordDay recordDayForDevice(Date recordDay) {
        AttnRecordDay theRecord = AttnRecordDay.findByRecordDateAndActiveStatus(recordDay, ActiveStatus.ACTIVE)
        if (theRecord) return theRecord

        DateTime startDt = new DateTime(recordDay)
        def weeklyList = commonService.weeklyHolidays()
        int dayOfWeek
            dayOfWeek = startDt.getDayOfWeek()
            if(dayOfWeek ==  DateTimeConstants.FRIDAY && weeklyList.contains("Fri")){
                theRecord = new AttnRecordDay(recordDate:startDt.toDate(), isClassDay:false, forceLoad: false, activeStatus:ActiveStatus.ACTIVE, dayType: AttendanceType.Weekly_Holiday, holidayName:"Friday")
            } else if(dayOfWeek ==  DateTimeConstants.SATURDAY && weeklyList.contains("Sat")){
                theRecord = new AttnRecordDay(recordDate:startDt.toDate(), isClassDay:false, forceLoad: false, activeStatus:ActiveStatus.ACTIVE, dayType: AttendanceType.Weekly_Holiday, holidayName:"Saturday")
            } else if(dayOfWeek ==  DateTimeConstants.SUNDAY && weeklyList.contains("Sun")){
                theRecord = new AttnRecordDay(recordDate:startDt.toDate(), isClassDay:false, forceLoad: false, activeStatus:ActiveStatus.ACTIVE, dayType: AttendanceType.Weekly_Holiday, holidayName:"Sunday")
            } else {
               def holidayObj = holidayService.getByDate(startDt)
                if (holidayObj) {
                    theRecord = new AttnRecordDay(recordDate:startDt.toDate(), isClassDay:false, forceLoad: false, activeStatus:ActiveStatus.ACTIVE, dayType: AttendanceType.Holiday, holidayName:holidayObj.name)
                } else {
                    theRecord = new AttnRecordDay(recordDate:startDt.toDate(), isClassDay:true, forceLoad: true, activeStatus:ActiveStatus.ACTIVE, dayType: AttendanceType.Open_Day, workingDayType: AttendanceType.Regular)
                }
            }
        theRecord.save(flush: true)
    }
}
