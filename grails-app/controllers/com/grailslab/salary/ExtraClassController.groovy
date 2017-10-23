package com.grailslab.salary

import com.grailslab.CommonUtils
import com.grailslab.command.ExtraClassCommand
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.hr.Employee
import grails.converters.JSON

class ExtraClassController {
    def classNameService
    def messageSource
    def extraClassService

    def index() {
        SalExtraClass salExtraClass = SalExtraClass.first()
        Double rate = salExtraClass?.rate


        render(view: '/salary/extraClass', model: [extraClassRate: rate])
    }

    def save(ExtraClassCommand command){
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


        SalExtraClass extraClass
        if (params.id) {
            //edit block
            extraClass = SalExtraClass.get(params.id)
            extraClass.properties['numOfClass','rate'] = command.properties
            message="Edit Successfully"
        } else {
            extraClass = SalExtraClass.findByAcademicYearAndYearMonthsAndEmployeeAndActiveStatus(command.academicYear,command.yearMonths,command.employee,ActiveStatus.ACTIVE)
          if(extraClass){
              message="Already Added, You can Edit It."
              result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
              result.put(CommonUtils.MESSAGE,message)
              outPut=result as JSON
              render outPut
              return
          }
            extraClass = new SalExtraClass(command.properties)
            message="Save Successfully"
        }
        if (extraClass.hasErrors() || !extraClass.save()){
            def errorList = extraClass?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
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
        SalExtraClass salExtraClass = SalExtraClass.read(id)
        if (!salExtraClass) {
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        Employee employee = Employee.read(salExtraClass.employee.id)
        result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
        result.put(CommonUtils.OBJ,salExtraClass)
        result.put('employeeName',employee.empID +"-"+ employee.name +"-"+ employee.hrDesignation.name)
        outPut = result as JSON
        render outPut
    }
    def delete(Long id) {
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        SalExtraClass extraClassRecord = SalExtraClass.get(id)
        if (!extraClassRecord) {
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        String message
        extraClassRecord.activeStatus=ActiveStatus.DELETE
        extraClassRecord.save(flush: true)
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, "ExtraClass deleted successfully.")
        outPut = result as JSON
        render outPut
    }

    def list() {
        LinkedHashMap gridData
        String result

        LinkedHashMap resultMap = extraClassService.paginateList(params)
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


}
