package com.grailslab.salary

import com.grailslab.CommonUtils
import com.grailslab.command.SalAttendanceCommand
import com.grailslab.enums.LeavePayType
import com.grailslab.enums.SalaryStatus
import com.grailslab.enums.YearMonths
import com.grailslab.gschoolcore.AcademicYear
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.hr.Employee
import grails.converters.JSON
import org.joda.time.DateTime

class SalaryAttendanceController {

    def messageSource
    def salAttendanceService
    def employeeService
    def recordDayService
    def attnEmployeeService
    def salarySetUpService
    def leaveService
    def index() { render(view: '/salary/salaryAttendance') }

    def save(SalAttendanceCommand command){
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }

        LinkedHashMap result = new LinkedHashMap()
        String outPut
        String message
        if (command.hasErrors()) {
            def errorList = command?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, errorList?.join('\n'))
            outPut=result as JSON
            render outPut
            return
        }


        SalAttendance salAttendance
        if (params.id) {
            salAttendance = SalAttendance.get(params.id)
            salAttendance.properties = command.properties
            message="Update Successfully"
        } else {
            salAttendance =SalAttendance.findByEmployeeAndAcademicYearAndYearMonthsAndActiveStatus(command.employee,command.academicYear,command.yearMonths,ActiveStatus.ACTIVE)
           if(salAttendance){
               message="Already Added, You can Edit It."
               result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
               result.put(CommonUtils.MESSAGE,message)
               outPut=result as JSON
               render outPut
               return
           }

            salAttendance = new SalAttendance(command.properties)
            message="Save Successfully"
        }
        if (salAttendance.hasErrors() || !salAttendance.save()){
            def errorList = salAttendance?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            message = errorList?.join('\n')
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
        }
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE,message)
        outPut=result as JSON
        render outPut

    }

    def list() {
        LinkedHashMap gridData
        String result

        LinkedHashMap resultMap = salAttendanceService.paginateList(params)
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

    def edit(Long id) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        SalAttendance salAttendance = SalAttendance.read(id)
        if (!salAttendance) {
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        SalMaster salMaster = SalMaster.findByAcademicYearAndYearMonthsAndSalaryStatusAndActiveStatus(salAttendance.academicYear, salAttendance.yearMonths, SalaryStatus.Disbursement, ActiveStatus.ACTIVE)
        if(salMaster){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,"Salary already disbursed. You can't edit now")
            outPut=result as JSON
            render outPut
            return
        }

        Employee employee = Employee.read(salAttendance.employee.id)
        result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
        result.put(CommonUtils.OBJ,salAttendance)
        result.put('employeeName',employee.empID +"-"+ employee.name +"-"+ employee.hrDesignation.name)
        outPut = result as JSON
        render outPut
    }

    def delete(Long id) {
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        SalAttendance salAttnRecord = SalAttendance.get(id)
        if (!salAttnRecord) {
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        SalMaster salMaster = SalMaster.findByAcademicYearAndYearMonthsAndSalaryStatusAndActiveStatus(salAttnRecord.academicYear, salAttnRecord.yearMonths, SalaryStatus.Disbursement, ActiveStatus.ACTIVE)
        if(salMaster){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,"Salary already disbursed. You can't delete now")
            outPut=result as JSON
            render outPut
            return
        }

        String message
        salAttnRecord.activeStatus=ActiveStatus.DELETE
        salAttnRecord.save(flush: true)
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, " Deleted successfully.")
        outPut = result as JSON
        render outPut
    }



    def loadAttendance(){
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        String message

        AcademicYear academicYear = AcademicYear.valueOf(params.academicYear)
        YearMonths yearMonths = YearMonths.valueOf(params.yearMonths)
        int yearVal = Integer.parseInt(academicYear.value)
        DateTime dateTime= CommonUtils.getDateTime(yearVal, yearMonths.serial)
        if(dateTime.afterNow){
            message = "Invalid Date"
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,message)
            outPut=result as JSON
            render outPut
            return
        }
        int employeeAttn= SalAttendance.countByAcademicYearAndYearMonthsAndActiveStatus(academicYear,yearMonths,ActiveStatus.ACTIVE)

        if(employeeAttn > 0) {
            message = "Already Loaded. Please delete those first"
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,message)
            outPut=result as JSON
            render outPut
            return
        }
            def dateMap = CommonUtils.getFirstAndLastDate(yearVal, yearMonths.serial)
            Date startDate = dateMap.firstDay.clearTime()
            Date endDate = dateMap.lastDate.clearTime()
            Integer pCount
            Integer lateDays
            Integer absentDays
            Integer leaveDays
            Integer unPaidleaveDays
            Integer workingDayCount = recordDayService.workingDay(startDate, endDate)
            if(workingDayCount==0){
                message = "Academic Calender not generated or no working day found"
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE,message)
                outPut=result as JSON
                render outPut
                return
            }
            Integer holiDayCount = recordDayService.holidayCount(startDate, endDate)
            def empNameList = salarySetUpService.salSetupEmpList()
            message = "Save Successfully"
        for (employee in empNameList) {
            pCount = attnEmployeeService.employeePresentCount(employee.empID, startDate, endDate)
            lateDays = attnEmployeeService.employeeLateCount(employee.empID, startDate, endDate)
            leaveDays = leaveService.leaveCount(employee.id, startDate, endDate, LeavePayType.PAID_LEAVE.key)
            unPaidleaveDays = leaveService.leaveCount(employee.id, startDate, endDate, LeavePayType.UN_PAID_LEAVE.key)
            absentDays = workingDayCount - (pCount + leaveDays)
            absentDays = absentDays <0 ? 0 : absentDays
            new SalAttendance(workingDays: workingDayCount, holidays:holiDayCount, yearMonths: yearMonths, employee: employee, presentDays: pCount, lateDays: lateDays, leaveDays: leaveDays, absentDays:absentDays+unPaidleaveDays).save()
            }
            result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
            result.put(CommonUtils.MESSAGE,message)
            outPut=result as JSON
            render outPut
    }

    def attnDeleteAll(){
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        String message

        AcademicYear academicYear = AcademicYear.valueOf(params.academicYear)
        YearMonths yearMonths = YearMonths.valueOf(params.yearMonths)
        SalMaster salMaster = SalMaster.findByAcademicYearAndYearMonthsAndSalaryStatusAndActiveStatus(academicYear, yearMonths, SalaryStatus.Disbursement, ActiveStatus.ACTIVE)
        if(salMaster){
            message="Salary already disbursed. You can't delete now"
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,message)
            outPut=result as JSON
            render outPut
            return
        }
       int deleteFlag = SalAttendance.where {academicYear == academicYear && yearMonths == yearMonths}.deleteAll()
       if(!deleteFlag){
           message = "No Attendance record found for this month"
           result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
           result.put(CommonUtils.MESSAGE,message)
           outPut=result as JSON
           render outPut
           return
       }
        message=" Delete successfully"
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE,message)
        outPut=result as JSON
        render outPut

    }
}
