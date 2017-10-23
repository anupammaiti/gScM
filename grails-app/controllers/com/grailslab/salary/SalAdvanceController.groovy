package com.grailslab.salary

import com.grailslab.CommonUtils
import com.grailslab.enums.PayType
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.hr.Employee
import grails.converters.JSON

class SalAdvanceController {

    def messageSource
    def salAdvanceService

    def index() {
        render(view: '/salary/salAdvanceAmount')
    }

    def save() {
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        String message
        Date advanceDate
        if (params.advanceDate) {
            advanceDate = Date.parse('dd/MM/yyyy', params.advanceDate)
        } else {
            advanceDate = new Date()
        }
        Double currentAmount = params.getDouble("currentAmount")
        Double installAmount = params.getDouble("installmentAmount")
        SalAdvance salAdvance
        Employee employee
        if (params.objId) {
            salAdvance = SalAdvance.get(params.getLong('objId'))
            if (!salAdvance) {
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
                outPut=result as JSON
                render outPut
                return
            }
            employee = salAdvance.employee
            if (params.outStandingAmount) {
                Double outStandingAmount = salAdvance.outStandingAmount
                salAdvance.payType = PayType.PAID
                salAdvance.activeStatus = ActiveStatus.INACTIVE
                SalAdvance newAdvance = new SalAdvance(employee: salAdvance.employee, outStandingAmount: currentAmount + outStandingAmount, currAmount: currentAmount, installmentAmount: installAmount, advanceDate: advanceDate, description: params.description, payType: PayType.DUE)
                newAdvance.save(flush: true)
            } else {
                Double previousAmount = salAdvance.currAmount
                Double amountDiffer = currentAmount - previousAmount
                salAdvance.outStandingAmount = salAdvance.outStandingAmount + amountDiffer
                salAdvance.currAmount = currentAmount
                salAdvance.advanceDate = advanceDate
                salAdvance.description = params.description
                message = "Updated Successfully."
            }
        } else {
            employee = Employee.read(params.getLong('employeeId'))
            SalAdvance previousLoan = SalAdvance.findByEmployeeAndPayTypeAndActiveStatus(employee, PayType.DUE, ActiveStatus.ACTIVE)
            if (previousLoan) {
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, "${employee.name} already have an Advance.")
                outPut=result as JSON
                render outPut
                return
            }
            salAdvance = new SalAdvance(employee: employee, outStandingAmount: currentAmount, currAmount: currentAmount,installmentAmount: installAmount, advanceDate: advanceDate, description: params.description, payType: PayType.DUE)
            message = "Added Successfully."
        }
        if (!salAdvance.save()) {
            def errorList = salAdvance?.errors?.allErrors?.collect { messageSource.getMessage(it, null) }
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,errorList.join(","))
            outPut=result as JSON
            render outPut
            return
        }
        SalSetup salSetup = SalSetup.findByEmployeeAndActiveStatus(employee, ActiveStatus.ACTIVE)
        if(salSetup){
            salSetup.adStatus = true
            salSetup.adsAmount = installAmount
            salSetup.save()
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
        SalAdvance salAdvance = SalAdvance.read(id)
        if (!salAdvance) {
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        Employee employee = Employee.read(salAdvance.employee.id)
        result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
        result.put(CommonUtils.OBJ,salAdvance)
        result.put('employeeName',employee.empID +"-"+ employee.name +"-"+ employee.hrDesignation.name)
        outPut = result as JSON
        render outPut
    }

    def setupList() {
        LinkedHashMap gridData
        String result
        LinkedHashMap resultMap = salAdvanceService.paginateList(params)
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
        SalAdvance salAdvanceRecord = SalAdvance.get(id)
        if (!salAdvanceRecord) {
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }

        def advancePayment = SalAdvanceDetail.findBySalAdvance(salAdvanceRecord)
        if (advancePayment){
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,"Advance installment already paid. You can't delete this. Please contact to admin")
            outPut = result as JSON
            render outPut
            return
        }

        salAdvanceRecord.activeStatus=ActiveStatus.DELETE
        salAdvanceRecord.save(flush: true)
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, " Amount deleted successfully.")
        outPut = result as JSON
        render outPut
    }


}
