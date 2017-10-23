package com.grailslab.hr

import com.grailslab.CommonUtils
import com.grailslab.command.HrCategoryCommand
import com.grailslab.gschoolcore.ActiveStatus
import grails.converters.JSON
import org.springframework.dao.DataIntegrityViolationException

class HrCategoryController {

    def hrCategoryService
    def messageSource

    def index() {
        LinkedHashMap resultMap = hrCategoryService.hrCategoryPaginateList(params)
        if (!resultMap || resultMap.totalCount == 0) {
            render(view: '/hr/hrCategory', model: [dataReturn: null, totalCount: 0])
            return
        }
        int totalCount = resultMap.totalCount
        render(view: '/hr/hrCategory', model: [dataReturn: resultMap.results, totalCount: totalCount])
    }

    def list() {
        LinkedHashMap gridData
        String result
        LinkedHashMap resultMap = hrCategoryService.hrCategoryPaginateList(params)

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

    def save(HrCategoryCommand command) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        if (command.hasErrors()) {
            def errorList = command?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, errorList?.join('\n'))
            outPut=result as JSON
            render outPut
            return
        }

        HrCategory hrCategory
        if (command.id) {
            hrCategory = HrCategory.get(command.id)
            if (!hrCategory) {
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
                outPut=result as JSON
                render outPut
                return
            }
            hrCategory.properties = command.properties
            result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
            result.put(CommonUtils.MESSAGE,'Data Updated successfully')
        } else {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut=result as JSON
            render outPut
            return
        }

        if(hrCategory.hasErrors() || !hrCategory.save()){
            def errorList = hrCategory?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            result.put(CommonUtils.MESSAGE,errorList?.join('\n'))
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
        }
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
        HrCategory hrCategory = HrCategory.read(id)
        if (!hrCategory) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.OBJ, hrCategory)
        outPut = result as JSON
        render outPut
    }

    def inactive(Long id) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        HrCategory hrCategory = HrCategory.get(id)
        if (!hrCategory) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }

        if(hrCategory.activeStatus.equals(ActiveStatus.INACTIVE)){
            hrCategory.activeStatus=ActiveStatus.ACTIVE
        }else {
            hrCategory.activeStatus=ActiveStatus.INACTIVE
        }
        hrCategory.save(flush: true)
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, 'Status Change Successfully')
        outPut = result as JSON
        render outPut
    }


    def hrStaffCategory() {
        LinkedHashMap resultMap = hrCategoryService.hrStaffCategoryPaginateList(params)
        if (!resultMap || resultMap.totalCount == 0) {
            render(view: '/hr/hrStaffCategory', model: [dataReturn: null, totalCount: 0])
            return
        }
        int totalCount = resultMap.totalCount
        render(view: '/hr/hrStaffCategory', model: [dataReturn: resultMap.results, totalCount: totalCount])
    }

    def listStaff() {
        LinkedHashMap gridData
        String result
        LinkedHashMap resultMap = hrCategoryService.hrStaffCategoryPaginateList(params)

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

    def saveStaff(HrStaffCategory command) {
        if (!request.method.equals('POST')) {
            redirect(action: 'hrStaffCategory')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        if (command.hasErrors()) {
            def errorList = command?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, errorList?.join('\n'))
            outPut=result as JSON
            render outPut
            return
        }
        def refAlreadyExist
        if(command.id){
            refAlreadyExist = HrStaffCategory.findByKeyNameAndIdNotEqual(command.keyName,command.id)
        } else {
            refAlreadyExist = HrStaffCategory.findByKeyName(command.keyName)
        }
        if(refAlreadyExist){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "Name already exist.")
            outPut=result as JSON
            render outPut
            return
        }
        HrStaffCategory hrStaffCategory
        if (command.id) {
            hrStaffCategory = HrStaffCategory.get(command.id)
            if (!hrStaffCategory) {
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
                outPut=result as JSON
                render outPut
                return
            }
            hrStaffCategory.properties = command.properties
            hrStaffCategory.keyName=command.keyName.replaceAll(" ","").toLowerCase()
            result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
            result.put(CommonUtils.MESSAGE,'Data Updated successfully')
        } else {
            hrStaffCategory = new HrStaffCategory(command.properties)
            hrStaffCategory.keyName=command.keyName.replaceAll(" ","").toLowerCase()
            result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
            result.put(CommonUtils.MESSAGE,'Data Added successfully')
        }

        if(hrStaffCategory.hasErrors() || !hrStaffCategory.save()){
            def errorList = hrStaffCategory?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            result.put(CommonUtils.MESSAGE,errorList?.join('\n'))
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
        }
        outPut=result as JSON
        render outPut
    }

    def editStaff(Long id) {
        if (!request.method.equals('POST')) {
            redirect(action: 'hrStaffCategory')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        HrStaffCategory hrStaffCategory = HrStaffCategory.read(id)
        if (!hrStaffCategory) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.OBJ, hrStaffCategory)
        outPut = result as JSON
        render outPut
    }

    def inactiveStaff(Long id) {
        if (!request.method.equals('POST')) {
            redirect(action: 'hrStaffCategory')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        HrStaffCategory hrStaffCategory = HrStaffCategory.get(id)
        if (!hrStaffCategory) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }

        if (hrStaffCategory.activeStatus.equals(ActiveStatus.ACTIVE)){
            hrStaffCategory.activeStatus=ActiveStatus.INACTIVE
        }else {
            hrStaffCategory.activeStatus=ActiveStatus.ACTIVE
        }

        hrStaffCategory.save(flush: true)
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, 'Status Change Successfully')
        outPut = result as JSON
        render outPut
    }

    def staffSorting(Long id){
        HrStaffCategory category=HrStaffCategory.read(id)
        if (!category){
            redirect(action: 'hrStaffCategory')
            return
        }

        def resultMap = hrCategoryService.hrStaffSortList(category)
        render(view: '/hr/hrStaffSorting', model: [employeeList: resultMap])
    }

    def staffSortingSave(){
        Employee employee

        params.sortOrder.each{ employeeData ->

            try {
                employee=Employee.get(Long.parseLong(employeeData.key))
                employee.sortOrder=Integer.parseInt(employeeData.value)
                employee.save()
            }catch (e){
                e.print(employee.empID)
            }
        }
        redirect(action: 'hrStaffCategory')
    }
}
