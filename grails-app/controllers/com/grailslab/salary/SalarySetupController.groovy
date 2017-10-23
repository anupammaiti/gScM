package com.grailslab.salary

import com.grailslab.CommonUtils
import com.grailslab.command.SalConfigurationCommand
import com.grailslab.command.SalarySetupCommand
import com.grailslab.enums.SalaryStatus
import com.grailslab.enums.YearMonths
import com.grailslab.gschoolcore.AcademicYear
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.hr.Employee
import grails.converters.JSON

class SalarySetupController {
    def salarySetUpService
    def messageSource
    def salaryMasterSetupService


   def index() {
        render(view: '/salary/salarySetup')
    }
    def salMasterSetup(){
        render(view:'/salary/masterSetup')
    }


    def masterSetupList() {
        LinkedHashMap gridData
        String result
        LinkedHashMap resultMap = salaryMasterSetupService.paginateList(params)
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
    def footNoteEdit(Long id) {
        if (!request.method.equals('POST')) {
            redirect(action: 'bookIssue')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        String footNote
        SalMaster master = SalMaster.read(id)
        if (!master) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        if(master.footNote){
            footNote=master?.footNote
        }else {
            footNote =""
        }
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.OBJ, master)
        result.put('footNote', footNote)
        result.put('ID', id)
        outPut = result as JSON
        render outPut
    }
    def saveFootNote(){
        if (!request.method.equals('POST')) {
            redirect(action: 'bookIssue')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        String footNote
        SalMaster master = SalMaster.read(params.id)
        if (!params.id) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }

        master.footNote = params.footNote
        master.save(flush: true)
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, "Foot Note Saved successfully")
        outPut = result as JSON
        render outPut
    }

    def masterSetupSave(){
        if (!request.method.equals('POST')) {
            redirect(action: 'salMasterSetup')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        String message
        SalMaster salMaster
        AcademicYear academicYear
        YearMonths yearMonths
        if (params.id) {
            salMaster = SalMaster.get(params.getLong('id'))
            academicYear=salMaster.academicYear
            yearMonths=salMaster.yearMonths
            int deleteFlag = SalarySheet.where {salMaster==salMaster}.deleteAll()
            insertSalarySheet(salMaster, yearMonths, academicYear);
            message="Salary Regenerated Successfully"
            salMaster.save()
        } else {
            academicYear =AcademicYear.valueOf(params.academicYear)
            yearMonths =YearMonths.valueOf(params.yearMonths)
            SalaryStatus salaryStatus =SalaryStatus.Prepared
            salMaster = SalMaster.findByAcademicYearAndYearMonthsAndActiveStatus(academicYear,yearMonths,ActiveStatus.ACTIVE)
            if(salMaster){
                message="Already Added"
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE,message)
                outPut=result as JSON
                render outPut
                return
            }
            salMaster = new SalMaster()
            salMaster.yearMonths=yearMonths
            salMaster.salaryStatus=salaryStatus
            salMaster.academicYear=academicYear
            if (salMaster.save()){
                insertSalarySheet(salMaster, yearMonths, academicYear);
            }
            message="Salary Generate Successfully"
        }

        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE,message)
        outPut=result as JSON
        render outPut
    }

    def disburseSalary(){
        if (!request.method.equals('POST')) {
            redirect(action: 'salMasterSetup')
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

        SalMaster salMaster = SalMaster.findByAcademicYearAndYearMonthsAndSalaryStatusAndActiveStatus(academicYear,yearMonths, SalaryStatus.Prepared, ActiveStatus.ACTIVE)
        if(!salMaster){
            message="No Prepared salary found for disburse. Please Prepare salary first"
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,message)
            outPut=result as JSON
            render outPut
            return
        }
        salaryMasterSetupService.calAdvanceAmount(salMaster,salMaster.yearMonths,salMaster.academicYear)
        salaryMasterSetupService.calPcAmount(salMaster,salMaster.yearMonths,salMaster.academicYear)
        salaryMasterSetupService.calDPSAmount(salMaster,salMaster.yearMonths,salMaster.academicYear)
        salMaster.salaryStatus=SalaryStatus.Disbursement
        message="Salary disbursement Successfully"

        if (!salMaster.save()){
            def errorList = salMaster?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
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

    def masterDelete(Long id) {
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        SalMaster salMaster = SalMaster.get(id)
        if (!salMaster) {
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        if (salMaster.salaryStatus == SalaryStatus.Disbursement) {
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,"Salary Already disbursed.")
            outPut = result as JSON
            render outPut
            return
        }
          salMaster.delete()
        //salMaster.activeStatus=ActiveStatus.DELETE
        //salMaster.save(flush: true)
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, "Master Setup deleted successfully.")
        outPut = result as JSON
        render outPut
    }

    def list() {
        LinkedHashMap gridData
        String result

        LinkedHashMap resultMap = salarySetUpService.paginateList(params)
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


    def save(SalarySetupCommand command){
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
        SalSetup salSetup
        if (params.id) {
            //edit block
            salSetup = SalSetup.get(params.id)
            salSetup.properties = command.properties
            message="Update Successfully"
        } else {
            salSetup = SalSetup.findByEmployeeAndActiveStatus(command.employee, ActiveStatus.ACTIVE)
            if(salSetup){
                message="Already Added, You can Edit It."
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE,message)
                outPut=result as JSON
                render outPut
                return
            }
            salSetup = new SalSetup(command.properties)
            message="Save Successfully"
        }
        if (salSetup.hasErrors() || !salSetup.save()){
            def errorList = salSetup?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            message = errorList?.join('\n')
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
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
        SalSetup salSetup = SalSetup.read(id)
        if (!salSetup) {
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        Employee employee = Employee.read(salSetup.employee.id)

        result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
        result.put(CommonUtils.OBJ,salSetup)
        result.put('employeeName',employee.empID +"-"+ employee.name +"-"+ employee.hrDesignation.name)
        outPut = result as JSON
        render outPut
    }

    def delete(Long id) {
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        SalSetup salSetup = SalSetup.get(id)
        if (!salSetup) {
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        salSetup.activeStatus=ActiveStatus.DELETE
        salSetup.save(flush: true)
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, "Salary Setup deleted successfully.")
        outPut = result as JSON
        render outPut
    }

    def empPreSetupData(Long id){
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        SalAdvance  salAdvance=salarySetUpService.getEmpAdvanceAmount(id)
         if (!salAdvance) {
            result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
            result.put(CommonUtils.OBJ,salAdvance)
            result.put('adOutStandingAmount',0)
            result.put('adInstallmentAmount',0)
            outPut = result as JSON
            render outPut
            return
        }

        result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
        result.put(CommonUtils.OBJ,salAdvance)
        result.put('adOutStandingAmount',salAdvance.outStandingAmount)
        result.put('adInstallmentAmount',salAdvance.installmentAmount)
        outPut = result as JSON
        render outPut
    }

    def salConfigData(){
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        String message
        SalConfiguration salConfiguration = SalConfiguration.first()
        if(!salConfiguration){
            result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }

        result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
        result.put(CommonUtils.OBJ,salConfiguration)
        outPut = result as JSON
        render outPut
    }

    def saveConfig(SalConfigurationCommand command){
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        String message
        SalConfiguration salConfiguration
        if (command.hasErrors()) {
            def errorList = command?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, errorList?.join('\n'))
            outPut=result as JSON
            render outPut
            return
        }
        if (params.id) {
            //edit bolck
            salConfiguration=SalConfiguration.get(params.id)
            salConfiguration.properties=command.properties
            message="Edit Successfully"
         }else{
            salConfiguration = new SalConfiguration(command.properties)
            message="Save Successfully"
            }

          if(salConfiguration.hasErrors() || !salConfiguration.save()){
              def errorList = salConfiguration?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
              message = errorList?.join('\n')
              result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
          }

        result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
        result.put(CommonUtils.OBJ,salConfiguration)
        outPut = result as JSON
        render outPut
    }

    private def insertSalarySheet(SalMaster salMaster,YearMonths yearMonths,AcademicYear academicYear){
        int incrementCount = SalIncrement.countByAcademicYearAndYearMonthsAndIncrementStatus(academicYear,yearMonths, "Confirm")
        SalConfiguration salConfig = SalConfiguration.first()
        List allSetup=salarySetUpService.salarySetupList()
        SalarySheet salarySheet
        for (salSetup in allSetup) {
            salarySheet = new SalarySheet(salMaster: salMaster, yearMonths: yearMonths, academicYear: academicYear)
            if (incrementCount > 0) {
                calIncrementAmount(salSetup.employee, yearMonths, academicYear, salarySheet)
            }
            salarySheet.employee= salSetup.employee
            salarySheet.grossSalary= salSetup.grossSalary
            salarySheet.basic= salSetup.basic
            salarySheet.houseRent= salSetup.houseRent
            salarySheet.medical= salSetup.medical
            salarySheet.inCharge= salSetup.inCharge
            salarySheet.mobileAllowance= salSetup.mobileAllowance
            salarySheet.others= salSetup.others
            salarySheet.area=calAreaAmount(salSetup.employee, yearMonths, academicYear)
            salarySheet.pfStatus= salSetup.pfStatus
            salarySheet.adStatus= salSetup.adStatus
            salarySheet.dpsAmount=salSetup.dpsAmount
            if (salSetup.pfStatus) {
                salarySheet.dpsAmountSchool=caldpsSchoolAmount(salConfig, salSetup.basic, salSetup.grossSalary)
            } else {
                salarySheet.dpsAmountSchool= 0
            }
            salarySheet.adsAmount = salSetup.adsAmount
            calLateAndULFine(salConfig, salSetup.employee, yearMonths, academicYear,salarySheet)
            calExtraClassAndAmount(salSetup.employee,yearMonths,academicYear,salarySheet)
            salarySheet.fine=salSetup.fine
            salarySheet.pc=salSetup.pc
            salarySheet.netPayable= salSetup.netPayable
            salarySheet.save()
        }

    }


    private def calLateAndULFine(SalConfiguration salConfig,Employee employee,YearMonths yearMonths,AcademicYear academicYear,SalarySheet salarySheet){
        Double perdayAmount=0
        SalAttendance salAttendance = SalAttendance.findByEmployeeAndYearMonthsAndAcademicYearAndActiveStatus(employee,yearMonths,academicYear,ActiveStatus.ACTIVE)
        if(!salAttendance){
            salarySheet.lateDays=0
            salarySheet.lateFine=0
            salarySheet.ulDays=0
            salarySheet.ulFine=0
             return
        }
        if(employee.hrCategory.hrKeyType.key.equals("HM")){
            salarySheet.lateDays=0
            salarySheet.lateFine=0
            salarySheet.ulDays=0
            salarySheet.ulFine=0
            return
        }
        int leaveDays = salAttendance.leaveDays > 0 ? salAttendance.leaveDays : 0
        int lateFineDays= salAttendance.lateDays > 0 ? (salAttendance.lateDays/salConfig.lateFineForDays) : 0
        int ulFinedays= salAttendance.absentDays > 0 ? (salAttendance.absentDays - leaveDays) : 0

        if(salConfig.pfCalField.equals("basic") && salarySheet.basic > 0){
            perdayAmount=(salarySheet.basic)/30
        } else if(salarySheet.grossSalary > 0) {
            perdayAmount=(salarySheet.grossSalary)/30
        }
        perdayAmount.round()
        salarySheet.lateDays=salAttendance.lateDays
        salarySheet.lateFine=(perdayAmount * lateFineDays)
        salarySheet.ulDays=ulFinedays
        salarySheet.ulFine=(perdayAmount*ulFinedays)
}

    private void calIncrementAmount(Employee employee, YearMonths yearMonths,AcademicYear academicYear, SalarySheet salarySheet){
        SalIncrement salIncrement = SalIncrement.findByEmployeeAndActiveStatusAndAcademicYearAndYearMonthsAndIncrementStatus(employee, ActiveStatus.ACTIVE, academicYear, yearMonths, "Confirm")
        if (salIncrement && salIncrement.grossSalary > 0) {
            salarySheet.increment = salIncrement.grossSalary
        }
    }

    private def calExtraClassAndAmount(Employee employee,YearMonths yearMonths,AcademicYear academicYear,SalarySheet salarySheet){
        SalExtraClass salExtraClass = SalExtraClass.findByEmployeeAndYearMonthsAndAcademicYearAndActiveStatus(employee,yearMonths,academicYear,ActiveStatus.ACTIVE)
        if(!salExtraClass){
            salarySheet.extraClass=0
            salarySheet.extraClassAmount=0
            return
        }
        salarySheet.extraClass=salExtraClass.numOfClass
        salarySheet.extraClassAmount=(salExtraClass.amount)
    }

    private def calAreaAmount(Employee employee,YearMonths yearMonths,AcademicYear academicYear){
        SalArea salArea = SalArea.findByEmployeeAndYearMonthsAndAcademicYearAndActiveStatus(employee,yearMonths,academicYear,ActiveStatus.ACTIVE)
        if(!salArea){
           return 0
        }
        return   salArea.amount
    }


    private def caldpsSchoolAmount(SalConfiguration salConfig, Double basic, Double gross) {
        Double schoolAmount=0
        if(salConfig.pfCalField.equals("basic") && basic > 0){
            schoolAmount= (basic*salConfig.pfContribution)/100
        } else if(gross > 0){
            schoolAmount= (gross*salConfig.pfContribution)/100
        }
        return schoolAmount.round()
    }
}
