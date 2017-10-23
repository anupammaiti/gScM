package com.grailslab.salary

import com.grailslab.CommonUtils
import com.grailslab.command.SalAreaSetupCommand
import com.grailslab.enums.YearMonths
import com.grailslab.gschoolcore.AcademicYear
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.hr.Employee
import grails.converters.JSON

class AreaSetupController {
    def areaSetupService

    def index() {
        render(view: '/salary/areaSetup')
    }


    def list() {
        LinkedHashMap gridData
        String result

        LinkedHashMap resultMap = areaSetupService.paginateList(params)
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

    def save(SalAreaSetupCommand command){
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        String message
        SalArea salArea
        AcademicYear academicYear
        YearMonths yearMonths

        if (command.hasErrors()) {
            def errorList = command?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, errorList?.join('\n'))
            outPut=result as JSON
            render outPut
            return
        }

        if (params.id) {
            salArea = SalArea.get(params.id)
            salArea.academicYear=command.academicYear
            salArea.yearMonths=command.yearMonths
            salArea.amount=command.amount
            message="Edit Successfully"

        } else {
            salArea = SalArea.findByEmployeeAndAcademicYearAndYearMonthsAndActiveStatus(command.employee, command.academicYear,command.yearMonths,ActiveStatus.ACTIVE)
            if(salArea){
                message="Already Added"
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE,message)
                outPut=result as JSON
                render outPut
                return
            }
            salArea = new SalArea(command.properties)

            message="Save Successfully"
        }
        if (!salArea.save()){
            def errorList = salArea?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            message = errorList?.join('\n')
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
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
         SalArea salArea = SalArea.read(id)
        if (!salArea) {
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        Employee employee = Employee.read(salArea.employee.id)
        result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
        result.put(CommonUtils.OBJ,salArea)
        result.put('employeeName',employee.empID +"-"+ employee.name +"-"+ employee.hrDesignation.name)
        outPut = result as JSON
        render outPut
    }

    def delete(Long id) {
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        SalArea salArea = SalArea.get(id)
        if (!salArea) {
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        salArea.activeStatus=ActiveStatus.DELETE
        salArea.save(flush: true)
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, "Area Setup deleted successfully.")
        outPut = result as JSON
        render outPut
    }
}
