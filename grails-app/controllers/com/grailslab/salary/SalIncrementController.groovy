package com.grailslab.salary

import com.grailslab.CommonUtils
import com.grailslab.command.SalaryIncrementCommand
import com.grailslab.enums.YearMonths
import com.grailslab.gschoolcore.AcademicYear
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.hr.Employee
import grails.converters.JSON

class SalIncrementController {
    def messageSource
    def salIncrementService

    def index() {
        render(view: '/salary/salIncrement')
    }

    def list() {
        LinkedHashMap gridData
        String result
        LinkedHashMap resultMap = salIncrementService.paginateList(params)
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

    def save(SalaryIncrementCommand command){
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        String message
        SalIncrement salIncrement
        int alreadyUp
        if (params.id) {
            salIncrement = SalIncrement.get(params.id)
            if (!salIncrement || salIncrement.activeStatus != ActiveStatus.ACTIVE) {
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
                outPut=result as JSON
                render outPut
                return
            }

            alreadyUp = SalIncrement.countByEmployeeAndAcademicYearAndYearMonthsAndIdNotEqualAndActiveStatus(salIncrement.employee, command.academicYear, command.yearMonths, salIncrement.id, ActiveStatus.ACTIVE)
            if (alreadyUp > 0) {
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, "${salIncrement.employee.name} salary already increment this month, you may update.")
                outPut=result as JSON
                render outPut
                return
            }

            salIncrement.properties = command.properties
            message="Update Successfully"
        } else {
            Employee employee = command.employee
            alreadyUp = SalIncrement.countByEmployeeAndAcademicYearAndYearMonthsAndActiveStatus(employee, command.academicYear, command.yearMonths, ActiveStatus.ACTIVE)
            if (alreadyUp > 0) {
                    result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                    result.put(CommonUtils.MESSAGE, "${employee.name} salary already increment this month, you may update.")
                    outPut=result as JSON
                    render outPut
                    return
                }
            salIncrement = new SalIncrement(command.properties)
            salIncrement.incrementStatus = "Draft"
            message="Salary increment save Successfully"
        }
        if (salIncrement.hasErrors() || !salIncrement.save()){
            def errorList = salIncrement?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            message = errorList.join(",")
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,message)
            outPut=result as JSON
            render outPut
            return

        }
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE,message)
        outPut=result as JSON
        render outPut
    }

    def edit(Long id) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        SalIncrement salIncrement = SalIncrement.read(id)
        if (!salIncrement) {
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        Employee employee = Employee.read(salIncrement.employee.id)
        result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
        result.put(CommonUtils.OBJ,salIncrement)
        result.put('employeeName',employee.empID +"-"+ employee.name +"-"+ employee.hrDesignation.name)
        outPut = result as JSON
        render outPut
    }

    def delete(Long id) {
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        SalIncrement salIncrement = SalIncrement.get(id)
        if (!salIncrement) {
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        if (salIncrement.incrementStatus == "Confirm") {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "Increment already posted to salary. You can't delete now")
            outPut=result as JSON
            render outPut
            return
        }
        salIncrement.activeStatus = ActiveStatus.DELETE
        salIncrement.save()
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, "Salary Increment entry deleted successfully.")
        outPut = result as JSON
        render outPut
    }

    def confirmIncrement(){
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut

        if (!params.academicYear || !params.yearMonths) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,"Please select Year and Month")
            outPut=result as JSON
            render outPut
            return
        }
        String message

        AcademicYear academicYear = AcademicYear.valueOf(params.academicYear)
        YearMonths yearMonths = YearMonths.valueOf(params.yearMonths)
        LinkedList allRecord = new LinkedList()

        allRecord = SalIncrement.findAllByActiveStatusAndAcademicYearAndYearMonthsAndIncrementStatus(ActiveStatus.ACTIVE, academicYear, yearMonths, "Draft")
        SalSetup salSetup
        for(SalIncrement singleRecord : allRecord){
            salSetup = SalSetup.findByEmployeeAndActiveStatus(singleRecord.employee, ActiveStatus.ACTIVE)
            if (salSetup) {
                if (singleRecord.grossSalary && singleRecord.grossSalary > 0) {
                    salSetup.grossSalary = salSetup.grossSalary + singleRecord.grossSalary
                    salSetup.netPayable = salSetup.netPayable + singleRecord.grossSalary
                }
                salSetup.save()
                singleRecord.incrementStatus = "Confirm"
            }
            singleRecord.save()
        }
        message = "${academicYear} - ${yearMonths} salary increment hasbeen confirm."
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE,message)
        outPut=result as JSON
        render outPut
    }
}
