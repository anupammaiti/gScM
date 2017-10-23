package com.grailslab.salary

import com.grailslab.CommonUtils
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.hr.Employee
import grails.converters.JSON

class SalDpsController {

    def salDpsService
    def messageSource


    def index () {
        render( view: '/salary/salaryReport/dps')
    }


    def save() {
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        String message
        Double openInsAmount = params.getDouble("openInsAmount")
        Double openOwnAmount = params.getDouble("openOwnAmount")
        SalDps salDps
        if (params.objId) {
            salDps = SalDps.get(params.getLong('objId'))
            if (!salDps) {
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
                outPut=result as JSON
                render outPut
                return
            }
            Double previousOwnAmount = salDps.openOwnAmount
            Double changedOwnAmount = openOwnAmount - previousOwnAmount
            Double previousInsAmount = salDps.openInsAmount
            Double changedInsAmount = openInsAmount - previousInsAmount
            salDps.openInsAmount = openInsAmount
            salDps.totalInsAmount = salDps.totalInsAmount + changedInsAmount
            salDps.openOwnAmount = openOwnAmount
            salDps.totalOwnAmount = salDps.totalOwnAmount + changedOwnAmount
            salDps.description = params.reason
            message = "Updated Successfully."
        } else {
            Employee employee = Employee.read(params.getLong('employeeId'))
            def hasPrevDPS = SalDps.findAllByEmployee(employee)
            if (hasPrevDPS) {
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, 'Employee DPS Account Already Exist.')
                outPut=result as JSON
                render outPut
                return
            }
            salDps = new SalDps(employee: employee, openInsAmount: openInsAmount, totalInsAmount: openInsAmount,openOwnAmount:openOwnAmount,totalOwnAmount:openOwnAmount, description: params.reason)
            result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
            message = "Added Successfully."
        }
        if (!salDps.save()) {
            def errorList = salDps?.errors?.allErrors?.collect { messageSource.getMessage(it, null) }
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,errorList.join(","))
            outPut=result as JSON
            render outPut
            return
        }

        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE,message)
        outPut=result as JSON
        render outPut
    }



    def dpsAmountList() {
        LinkedHashMap gridData
        String result
        LinkedHashMap resultMap = salDpsService.paginateList(params)
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
        SalDps salDps = SalDps.read(id)
        if (!salDps) {
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        Employee employee = Employee.read(salDps.employee.id)
        result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
        result.put(CommonUtils.OBJ,salDps)
        result.put('employeeName',employee.empID +"-"+ employee.name +"-"+ employee.hrDesignation.name)
        outPut = result as JSON
        render outPut
    }

    def delete(Long id) {
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        SalDps salDpsRecord = SalDps.get(id)
        if (!salDpsRecord) {
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        def dpsPayment = SalDpsDetail.findBySalDps(salDpsRecord)
        if (dpsPayment){
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,"Dps amount already transfered to bank account. Please contact to admin")
            outPut = result as JSON
            render outPut
            return
        }

        salDpsRecord.activeStatus=ActiveStatus.DELETE
        salDpsRecord.save(flush: true)
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, "  Dps account deleted successfully.")
        outPut = result as JSON
        render outPut
    }
}
