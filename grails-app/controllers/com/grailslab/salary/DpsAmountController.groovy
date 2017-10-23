package com.grailslab.salary

import com.grailslab.CommonUtils
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.hr.Employee
import grails.converters.JSON

class DpsAmountController {

    def dpsAmountService
    def messageSource


    def index () {
        render( view: '/salary/salaryReport/dps')
    }


    def save() {
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        String message
        Double totalInsAmount = params.getDouble("totalInsAmount")
        Double totalOwnAmount = params.getDouble("totalOwnAmount")
        SalDps salDps
        if (params.id) {
            salDps = SalDps.get(params.getLong('id'))
            if (!salDps) {
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
                outPut=result as JSON
                render outPut
                return
            }
            salDps.totalInsAmount = totalInsAmount
            salDps.totalOwnAmount = totalOwnAmount
            salDps.description = params.reason
            message = "Updated Successfully."
        } else {
            Employee employee = Employee.read(params.getLong('employeeId'))
            salDps = new SalDps(employee: employee, totalInsAmount: totalInsAmount, totalOwnAmount: totalOwnAmount, description: params.reason)
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
        LinkedHashMap resultMap = dpsAmountService.paginateList(params)
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
        String message
        salDpsRecord.activeStatus=ActiveStatus.DELETE
        salDpsRecord.save(flush: true)
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, "  Dps Amount deleted successfully.")
        outPut = result as JSON
        render outPut
    }
}
