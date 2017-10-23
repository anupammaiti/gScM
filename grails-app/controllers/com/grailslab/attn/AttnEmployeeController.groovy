package com.grailslab.attn

import com.grailslab.CommonUtils
import com.grailslab.command.AttnEmployeeCommand
import com.grailslab.command.AttnEmployeeLateEntryCommand
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.hr.Employee
import com.grailslab.hr.HrCategory
import grails.converters.JSON
import org.apache.commons.lang.exception.ExceptionUtils
import org.joda.time.DateTime

class AttnEmployeeController {
    def attnEmployeeService
    def recordDayService
    def hrPeriodService
    def employeeService

    def index(Long id) {
        AttnRecordDay lastRecord
        if (id) {
            lastRecord = AttnRecordDay.read(id)
        } else {
            def lastEmpPresents = AttnEmpRecord.last()
            if (lastEmpPresents) {
                lastRecord = lastEmpPresents.recordDay
            }
        }

        def hrCategoryList = HrCategory.findAllByActiveStatus(ActiveStatus.ACTIVE, [sort: 'sortOrder'])
        if (!lastRecord) {
            render(view: '/attn/employee/empAttendanceList', model: [dataReturn: null, totalCount: 0, lastRecord: CommonUtils.getUiDateStrForPicker(new Date()), hrCategoryList: hrCategoryList])
            return
        }
        params.put("recordDateId", lastRecord.id)
        LinkedHashMap resultMap = attnEmployeeService.empPaginateList(params)
        if (!resultMap || resultMap.totalCount == 0) {
            render(view: '/attn/employee/empAttendanceList', model: [dataReturn: null, totalCount: 0, lastRecord: CommonUtils.getUiDateStrForPicker(lastRecord.recordDate), hrCategoryList: hrCategoryList])
            return
        }
        int totalCount = resultMap.totalCount
        render(view: '/attn/employee/empAttendanceList', model: [dataReturn: resultMap.results, totalCount: totalCount, lastRecord: CommonUtils.getUiDateStrForPicker(lastRecord.recordDate), hrCategoryList: hrCategoryList])
    }


    def list() {
        LinkedHashMap gridData
        String result
        if (!params.attnRecordDate) {
            gridData = [iTotalRecords: 0, iTotalDisplayRecords: 0, aaData: []]
            result = gridData as JSON
            render result
            return
        }
        Date recordDate = Date.parse('dd/MM/yyyy', params.attnRecordDate)
        AttnRecordDay recordDay = AttnRecordDay.findByActiveStatusAndRecordDate(ActiveStatus.ACTIVE, recordDate.clearTime(), [sort: "recordDate", order: "desc"])
        if (!recordDay) {
            gridData = [iTotalRecords: 0, iTotalDisplayRecords: 0, aaData: []]
            result = gridData as JSON
            render result
            return
        }
        params.put("recordDateId", recordDay.id)
        LinkedHashMap resultMap = attnEmployeeService.empPaginateList(params)
        if (!resultMap || resultMap.totalCount == 0) {
            gridData = [iTotalRecords: 0, iTotalDisplayRecords: 0, aaData: []]
            result = gridData as JSON
            render result
            return
        }
        int totalCount = resultMap.totalCount
        gridData = [iTotalRecords: totalCount, iTotalDisplayRecords: totalCount, aaData: resultMap.results]
        result = gridData as JSON
        render result
    }

    def saveManualAttendance(AttnEmployeeCommand command) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        String message
        AttnEmpRecord attnEmpRecord
        Employee employee
        Boolean isLateEntry

        Date inTimeDate
        Date outTimeDate
        Date recordDate

        if (command.id) {
            attnEmpRecord = AttnEmpRecord.get(command.id)
            employee = Employee.read(attnEmpRecord.employeeId)
            recordDate = attnEmpRecord.recordDay.recordDate
            inTimeDate = CommonUtils.manualAttnDateStrToDate(recordDate, command.inTime)
            if (command.outTime) {
                outTimeDate = CommonUtils.manualAttnDateStrToDate(recordDate, command.outTime)
            }
            isLateEntry = hrPeriodService.isLateEntry(inTimeDate, employee.hrPeriod?.startTime, employee.hrPeriod?.lateTolerance)
            attnEmpRecord.inTime = inTimeDate
            attnEmpRecord.outTime = outTimeDate
            attnEmpRecord.isLateEntry = isLateEntry

            message = "Attendance for $employee.name updated successfully"
        } else {
            recordDate = command.recordDate
            if (!recordDate) recordDate = new Date().clearTime()
            AttnRecordDay attnRecordDay = recordDayService.recordDayForDevice(recordDate)
            employee = Employee.read(command.employeeId)
            attnEmpRecord = AttnEmpRecord.findByRecordDayAndEmpNo(attnRecordDay, employee.empID)
            if (attnEmpRecord) {
                result.put(CommonUtils.IS_ERROR, true)
                result.put(CommonUtils.MESSAGE, "$employee.name Attendance for ${CommonUtils.getUiDateStr(recordDate)} already added. You can modify that if require.")
                outPut = result as JSON
                render outPut
                return
            }
            inTimeDate = CommonUtils.manualAttnDateStrToDate(recordDate, command.inTime)
            if (command.outTime) {
                outTimeDate = CommonUtils.manualAttnDateStrToDate(recordDate, command.outTime)
            }
            isLateEntry = hrPeriodService.isLateEntry(inTimeDate, employee.hrPeriod?.startTime, employee.hrPeriod?.lateTolerance)
            attnEmpRecord = new AttnEmpRecord(recordDay: attnRecordDay, recordDate: attnRecordDay.recordDate, employeeId: employee.id, empNo: employee.empID, inTime: inTimeDate, outTime: outTimeDate, remarks: command.reason, isLateEntry: isLateEntry)
            message = "Attendance for $employee.name added successfully"
        }
        attnEmpRecord.save()
        result.put(CommonUtils.IS_ERROR, false)
        result.put(CommonUtils.MESSAGE, message)
        outPut = result as JSON
        render outPut
        return
    }

    def edit(Long id){
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        AttnEmpRecord attnEmpRecord = AttnEmpRecord.read(id)
        if (!attnEmpRecord) {
            result.put(CommonUtils.IS_ERROR, true)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        Employee employee = Employee.read(attnEmpRecord.employeeId)

        result.put(CommonUtils.OBJ, attnEmpRecord)
        result.put('attnDate', attnEmpRecord.recordDay?.recordDate)
        result.put('inTime', CommonUtils.getUiTimeForEdit(attnEmpRecord.inTime))
        result.put('outTime', CommonUtils.getUiTimeForEdit(attnEmpRecord.outTime))
        result.put('employeeName', employee.empID+" - "+employee.name+" - "+employee.hrDesignation?.name)
        outPut = result as JSON
        render outPut
    }

    def updateLateEntry(AttnEmployeeLateEntryCommand command){
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        def employeeList
        if (command.employeeId) {
            employeeList = new ArrayList()
            employeeList.add(Employee.read(command.employeeId))
        } else {
            employeeList = employeeService.employeeList(command.hrCategory)
        }

        DateTime startDateTime
        DateTime endDateTime = new DateTime(command.endDate).withHourOfDay(23)
        DateTime thisDate
        Date effectiveDate

        AttnEmpRecord attnEmpRecord
        for (employee in employeeList) {
            startDateTime = new DateTime(command.startDate)
            while (startDateTime.isBefore(endDateTime)) {
                thisDate = startDateTime
                effectiveDate = thisDate.toDate()
                attnEmpRecord = AttnEmpRecord.findByRecordDateAndEmpNo(effectiveDate, employee.empID)
                if (attnEmpRecord) {
                    attnEmpRecord.isLateEntry = hrPeriodService.isLateEntry(attnEmpRecord.inTime, employee.hrPeriod?.startTime, employee.hrPeriod?.lateTolerance)
                    attnEmpRecord.save()
                }
                startDateTime = startDateTime.plusDays(1)
            }
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        String message
        result.put(CommonUtils.IS_ERROR, false)
        result.put(CommonUtils.MESSAGE, "Late Attendance Updated Successfully")
        outPut = result as JSON
        render outPut
        return
    }


    def delete(Long id) {
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        AttnEmpRecord attnEmpRecord = AttnEmpRecord.get(id)
        if (!attnEmpRecord) {
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }

        try {
            attnEmpRecord.delete()
            result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
            result.put(CommonUtils.MESSAGE,  "deleted successfully.")
        } catch (Exception e) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,  ExceptionUtils.getRootCauseMessage(e))
        }

        outPut = result as JSON
        render outPut
    }
}