package com.grailslab

import com.grailslab.enums.EventType
import com.grailslab.gschoolcore.AcademicYear
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.hr.Employee
import com.grailslab.settings.*
import com.grailslab.stmgmt.Student
import com.grailslab.viewz.StdEmpView
import grails.transaction.Transactional
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.joda.time.DateTime
import org.joda.time.DateTimeConstants

@Transactional
class ClassRoutineService {
    static transactional = false
    def grailsApplication
    def schoolService
    def commonService
    def calenderService

    static final String[] sortColumns = ['id','section']

    LinkedHashMap classRoutineList(GrailsParameterMap params) {
        int iDisplayStart = params.iDisplayStart ? params.getInt('iDisplayStart') : CommonUtils.DEFAULT_PAGINATION_START
        int iDisplayLength = params.iDisplayLength ? params.getInt('iDisplayLength') : CommonUtils.DEFAULT_PAGINATION_LENGTH
        String sSortDir = params.sSortDir_0 ? params.sSortDir_0 : CommonUtils.SORT_ORDER_DESC
        int iSortingCol = params.iSortCol_0 ? params.getInt('iSortCol_0') : CommonUtils.DEFAULT_PAGINATION_SORT_IDX
        //Search string, use or logic to all fields that required to include
        String sSearch = params.sSearch ? params.sSearch : null
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }
        Section section
        if (params.section) {
            section = Section.read(params.getLong('section'))
        }
        ClassName className
        if (params.className) {
            className = ClassName.read(params.getLong('className'))
        }

        String sortColumn = CommonUtils.getSortColumn(sortColumns, iSortingCol)
        List dataReturns = new ArrayList()
        def c = ClassRoutine.createCriteria()
        def results = c.list(max: iDisplayLength, offset: iDisplayStart) {
            eq("activeStatus", ActiveStatus.ACTIVE)
            if (className) {
                eq('className', className)
            }
            if (section) {
                eq('section', section)
            }
        }
        int totalCount = results.totalCount
        int serial = iDisplayStart;
        if (totalCount > 0) {
            if (sSortDir.equals(CommonUtils.SORT_ORDER_DESC)) {
                serial = (totalCount + 1) - iDisplayStart
            }
            results.each { ClassRoutine obj ->
                if (sSortDir.equals(CommonUtils.SORT_ORDER_ASC)) {
                    serial++
                } else {
                    serial--
                }
                dataReturns.add([DT_RowId: obj.id, 0: obj.className.name, 1: obj.section.name, 2: obj.classPeriod.period, 3: obj.subjectName.name, 4: obj.employee.empID+", "+obj.employee.name, 5: obj.days, 6:''])
            }
        }
        return [totalCount: totalCount, results: dataReturns]
    }

    LinkedHashMap teacherRoutineList(GrailsParameterMap params) {
        int iDisplayStart = params.iDisplayStart ? params.getInt('iDisplayStart') : CommonUtils.DEFAULT_PAGINATION_START
        int iDisplayLength = params.iDisplayLength ? params.getInt('iDisplayLength') : CommonUtils.DEFAULT_PAGINATION_LENGTH
        String sSortDir = params.sSortDir_0 ? params.sSortDir_0 : CommonUtils.SORT_ORDER_DESC
        int iSortingCol = params.iSortCol_0 ? params.getInt('iSortCol_0') : CommonUtils.DEFAULT_PAGINATION_SORT_IDX
        //Search string, use or logic to all fields that required to include
        String sSearch = params.sSearch ? params.sSearch : null
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }
        Employee employee
        if (params.employeeId) {
            employee = Employee.read(params.getLong('employeeId'))
        }
        Section section
        if (params.section) {
            section = Section.read(params.getLong('section'))
        }
        ClassName className
        if (params.className) {
            className = ClassName.read(params.getLong('className'))
        }

        String sortColumn = CommonUtils.getSortColumn(sortColumns, iSortingCol)
        List dataReturns = new ArrayList()
        def c = ClassRoutine.createCriteria()
        def results = c.list(max: iDisplayLength, offset: iDisplayStart) {
            eq("activeStatus", ActiveStatus.ACTIVE)
            if (employee) {
                eq('employee', employee)
            }
            if (className) {
                eq('className', className)
            }
            if (section) {
                eq('section', section)
            }
        }
        int totalCount = results.totalCount
        int serial = iDisplayStart;
        if (totalCount > 0) {
            if (sSortDir.equals(CommonUtils.SORT_ORDER_DESC)) {
                serial = (totalCount + 1) - iDisplayStart
            }
            results.each { ClassRoutine obj ->
                if (sSortDir.equals(CommonUtils.SORT_ORDER_ASC)) {
                    serial++
                } else {
                    serial--
                }
                dataReturns.add([DT_RowId: obj.id, 0: obj.employee.empID+", "+obj.employee.name, 1: obj.className.name, 2: obj.section.name, 3: obj.classPeriod.period, 4: obj.subjectName.name, 5: obj.days, 6:''])
            }
        }
        return [totalCount: totalCount, results: dataReturns]
    }


    def periodClassesByEmployee(ClassPeriod classPeriod, Employee employee, AcademicYear academicYear) {
        return ClassRoutine.findAllByClassPeriodAndEmployeeAndAcademicYearAndActiveStatus(classPeriod, employee, academicYear, ActiveStatus.ACTIVE)
    }
    def periodClassesBySection(ClassPeriod classPeriod, Section section, AcademicYear academicYear) {
        return ClassRoutine.findAllByClassPeriodAndSectionAndAcademicYearAndActiveStatus(classPeriod, section, academicYear, ActiveStatus.ACTIVE)
    }
    boolean isValidDays(List previousDays, List newDays) {
        return previousDays.disjoint(newDays)
    }
    List getRoutineAsEvents(GrailsParameterMap parameterMap) {
        Date sStartFrom = CommonUtils.searchDateFormat(parameterMap.start, true)
        Date sStartEnd = CommonUtils.searchDateFormat(parameterMap.end, false)

        List dataReturns = calenderService.getPublicEvents(sStartFrom, sStartEnd)
        if (!dataReturns) {
            dataReturns = new ArrayList()
        }
        int numberOfDays = sStartEnd.minus(sStartFrom)
        if (numberOfDays <=10) {
            List dayRoutines = new ArrayList()
            //        get class Routine
            String routineType = parameterMap.routineType
            List routineDays
            if (routineType == ROUTINE_TYPE_CLASS) {
                Section section = Section.read(parameterMap.getLong('sectionId'))
                if (section) {
                    routineDays = ClassRoutine.findAllBySectionAndActiveStatus(section, ActiveStatus.ACTIVE)
                }
            } else if (routineType == ROUTINE_TYPE_TEACHER) {
                Employee employee = Employee.read(parameterMap.getLong('employee'))
                routineDays = ClassRoutine.findAllByEmployeeAndActiveStatus(employee, ActiveStatus.ACTIVE)
            } else {
                // dashboard routine
                String userRef = schoolService.getUserRef()
                StdEmpView stdEmpView = StdEmpView.findByStdEmpNo(userRef)
                if (stdEmpView) {
                    if (stdEmpView.objType == "student") {
                        Section section = Student.read(stdEmpView.objId)?.section
                        if (section) {
                            routineDays = ClassRoutine.findAllBySectionAndActiveStatus(section, ActiveStatus.ACTIVE)
                        }
                    } else {
                        Employee employee = Employee.read(stdEmpView.objId)
                        routineDays = ClassRoutine.findAllByEmployeeAndActiveStatus(employee, ActiveStatus.ACTIVE)
                    }
                }
            }
            DateTime startDt = new DateTime(sStartFrom)
            DateTime endDt = new DateTime(sStartEnd)
            int dayOfWeek
            if (routineDays) {
                def weeklyList = commonService.weeklyHolidays()
                DateTime temp2Date = new DateTime(startDt.getMillis());
                boolean isOpenDay = false
                while(temp2Date.compareTo(endDt) <=0 ){
                    dayOfWeek = temp2Date.getDayOfWeek()
                    //check if this is a class day
                    isOpenDay = calenderService.checkIfOpenDay(temp2Date)
                    if (isOpenDay) {
                        if(dayOfWeek ==  DateTimeConstants.FRIDAY ){
                            routineDays.each {routine ->
                                if (routine.days.contains("FRI")) {
                                    dayRoutines.add([clsDay: temp2Date, routine: routine])
                                }
                            }
                        } else if(dayOfWeek ==  DateTimeConstants.SATURDAY){
                            routineDays.each {routine ->
                                if (routine.days.contains("SAT")) {
                                    dayRoutines.add([clsDay: temp2Date, routine: routine])
                                }
                            }
                        } else if(dayOfWeek ==  DateTimeConstants.SUNDAY){
                            routineDays.each {routine ->
                                if (routine.days.contains("SUN")) {
                                    dayRoutines.add([clsDay: temp2Date, routine: routine])
                                }
                            }
                        } else if(dayOfWeek ==  DateTimeConstants.MONDAY){
                            routineDays.each {routine ->
                                if (routine.days.contains("MON")) {
                                    dayRoutines.add([clsDay: temp2Date, routine: routine])
                                }
                            }
                        }else if(dayOfWeek ==  DateTimeConstants.TUESDAY){
                            routineDays.each {routine ->
                                if (routine.days.contains("TUE")) {
                                    dayRoutines.add([clsDay: temp2Date, routine: routine])
                                }
                            }
                        }else if(dayOfWeek ==  DateTimeConstants.WEDNESDAY){
                            routineDays.each {routine ->
                                if (routine.days.contains("WED")) {
                                    dayRoutines.add([clsDay: temp2Date, routine: routine])
                                }
                            }
                        }else if(dayOfWeek ==  DateTimeConstants.THURSDAY){
                            routineDays.each {routine ->
                                if (routine.days.contains("THU")) {
                                    dayRoutines.add([clsDay: temp2Date, routine: routine])
                                }
                            }
                        }
                    }
                    temp2Date = temp2Date.plusDays(1);
                }
            }

            ClassPeriod classPeriod
            ClassRoutine classRoutine
            Date clsDate
            dayRoutines.each{k ->
                clsDate = k.getAt('clsDay').toDate()
                classRoutine = k.getAt('routine')
                classPeriod = classRoutine.classPeriod
                dataReturns.add([ title:classRoutine.subjectName.name, start: CommonUtils.classStartTime(clsDate, classPeriod.startOn),end:CommonUtils.classEndTime(clsDate, classPeriod.startOn, classPeriod.duration),startEditable:false,durationEditable:false,className:'syllabus-event annual-program',allDay:false,tooltip:classRoutine.employee?.name])
            }
        }

        return dataReturns
    }
    public static final String ROUTINE_TYPE_CLASS = "classRoutine"
    public static final String ROUTINE_TYPE_TEACHER = "teacherRoutine"

}
