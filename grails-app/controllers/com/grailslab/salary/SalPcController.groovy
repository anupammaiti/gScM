package com.grailslab.salary

import com.grailslab.CommonUtils
import com.grailslab.enums.PayType
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.hr.Employee
import grails.converters.JSON

class SalPcController {

    def messageSource
    def salPcService


    def index() {
        render(view: '/salary/salPc')
    }
    def save() {
        println(params)
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        String message

        Double installAmount = params.getDouble("installmentAmount")
        Double payableAmount = params.getDouble("payableAmount")
        Double outStandingAmount = params.getDouble("outStandingAmount")

        PayType payType = PayType.DUE
        if((payableAmount - outStandingAmount) <=0 ){
            payType = PayType.PAID
        }
        if (payType == PayType.DUE && installAmount <=0) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,"Please provide installment amount")
            outPut=result as JSON
            render outPut
            return
        }

        SalPc salPc
        if (params.objId) {
            salPc = SalPc.get(params.getLong('objId'))
            if (!salPc) {
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
                outPut=result as JSON
                render outPut
                return
            }

            if((payableAmount - outStandingAmount) <=0 ){
                salPc.payType = PayType.PAID
                installAmount = 0
            }
            salPc.installmentAmount = installAmount
            salPc.payType = payType
            salPc.outStandingAmount = payableAmount - outStandingAmount
            salPc.payableAmount = payableAmount
            salPc.description = params.description
            message = "Updated Successfully."

        } else {
            Employee employee = Employee.read(params.getLong('employeeId'))
            int priviousCount = SalPc.countByEmployeeAndActiveStatus(employee, ActiveStatus.ACTIVE)
            if (priviousCount > 0) {
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, "${employee.name} pc already added")
                outPut=result as JSON
                render outPut
                return
            }
            salPc = new SalPc(employee: employee, outStandingAmount: payableAmount - outStandingAmount,payableAmount: payableAmount, installmentAmount: installAmount,description: params.description, payType: payType)
            message = "Added Successfully."
        }
        if (!salPc.save()) {
            def errorList = salPc?.errors?.allErrors?.collect { messageSource.getMessage(it, null) }
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

    def setupPcList() {
        LinkedHashMap gridData
        String result
        LinkedHashMap resultMap = salPcService.paginateList(params)
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
        SalPc salPc = SalPc.read(id)
        if (!salPc) {
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        Employee employee = Employee.read(salPc.employee.id)
        result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
        result.put(CommonUtils.OBJ,salPc)
        result.put('employeeName',employee.empID +"-"+ employee.name +"-"+ employee.hrDesignation.name)
        outPut = result as JSON
        render outPut

    }

    def delete(Long id){
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        SalPc salPcRecord = SalPc.get(id)
        if (!salPcRecord) {
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }

        /*def advancePayment = SalAdvanceDetail.findBySalAdvance(salAdvanceRecord)
        if (advancePayment){
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,"Advance installment already paid. You can't delete this. Please contact to admin")
            outPut = result as JSON
            render outPut
            return
        }*/

        salPcRecord.activeStatus=ActiveStatus.DELETE
        salPcRecord.save(flush: true)
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, " PC Amount deleted successfully.")
        outPut = result as JSON
        render outPut
    }


}
