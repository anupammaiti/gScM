package com.grailslab.salary

import com.grailslab.CommonUtils
import com.grailslab.command.SalaryBonusCommand
import com.grailslab.enums.SalaryStatus
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.hr.Employee
import grails.converters.JSON

import java.text.DateFormat
import java.text.SimpleDateFormat

class SalaryBonusController {
    def messageSource
    def salaryBonusService
    def employeeService
    def salarySetUpService

    def index() {
        render(view: '/salary/bonusSetting')
    }

    def save(SalaryBonusCommand command){
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        String message
        if(command.hasErrors()){
            def errorList = command?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, errorList?.join('\n'))
            outPut=result as JSON
            render outPut
            return
        }
        BonusMaster bonusMaster = new BonusMaster(command.properties)
        bonusMaster.salaryStatus = SalaryStatus.Draft
        message="Bonus save Successfully"
        if(bonusMaster.save()){
            insertBonusSheet(bonusMaster);
        }

        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE,message)
        outPut=result as JSON
        render outPut
    }

    def list() {
        LinkedHashMap gridData
        String result

        LinkedHashMap resultMap = salaryBonusService.paginateList(params)
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

    def disbursement(Long id){
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        if (id) {
            BonusMaster bonusMaster = BonusMaster.get(id)
            if (bonusMaster && bonusMaster.salaryStatus == SalaryStatus.Draft){
                bonusMaster.salaryStatus = SalaryStatus.Disbursement.key
                bonusMaster.save()
            }
        }
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, "Bonus disbursement successfully.")
        outPut = result as JSON
        render outPut
    }

    def regenerate(Long id){
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        if (id) {
            BonusMaster bonusMaster = BonusMaster.get(id)
            if (bonusMaster && bonusMaster.salaryStatus == SalaryStatus.Draft){
                int deleteFlag = BonusSheet.where {bonusMaster == bonusMaster}.deleteAll()
                insertBonusSheet(bonusMaster)
            }
        }
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, "Bonus regenerate successfully.")
        outPut = result as JSON
        render outPut
    }

    def delete(Long id) {
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        BonusMaster bonusMaster = BonusMaster.get(id)
        if (!bonusMaster) {
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        bonusMaster.activeStatus=ActiveStatus.DELETE
        bonusMaster.save(flush: true)
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, "Bonus deleted successfully.")
        outPut = result as JSON
        render outPut
    }
    void insertBonusSheet(BonusMaster bonusMaster){
        Date joinBefore = bonusMaster.joinBefore
        String basedOn = bonusMaster.basedOn
        Double bonusPercentage = bonusMaster.bonusPercentage
        def salarySetUpEmployeeIds = salarySetUpService.salSetupEmpIdList()
        def joinedBeforeList = employeeService.employeeList(joinBefore, salarySetUpEmployeeIds)
        SalSetup salSetup
        Double baseAmount = 0
        Double bonusAmount = 0
        for (employee in joinedBeforeList){
            salSetup = SalSetup.findByEmployeeAndActiveStatus(employee, ActiveStatus.ACTIVE)
            if (salSetup) {
                if (basedOn == "grossSalary"){
                    baseAmount = salSetup.grossSalary
                } else {
                    baseAmount = salSetup.basic
                }
                if (baseAmount) {
                    bonusAmount = baseAmount * bonusPercentage * 0.01
                }
                if (bonusAmount) {
                    new BonusSheet(bonusMaster: bonusMaster, employee: employee, baseAmount:baseAmount, amount: bonusAmount).save()
                }
            }
        }
    }
}
