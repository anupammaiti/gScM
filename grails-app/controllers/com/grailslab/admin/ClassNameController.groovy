package com.grailslab.admin

import com.grailslab.CommonUtils
import com.grailslab.command.ClassNameCommand
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.settings.ClassName
import grails.converters.JSON

class ClassNameController {


    def classNameService
    def messageSource
    def classSubjectsService
    def attAttendanceService
    def subjectService
    def schoolService
    def hrPeriodService


    def index() {

        def officeHourList = hrPeriodService.periodDropDownList()
        LinkedHashMap resultMap = classNameService.classNamePaginateList(params)

        if (!resultMap || resultMap.totalCount == 0) {
            render(view: '/admin/className/classNameView', model: [dataReturn: null, totalCount: 0, officeHourList: officeHourList])
            return
        }
        int totalCount = resultMap.totalCount
        render(view: '/admin/className/classNameView', model: [dataReturn: resultMap.results, totalCount: totalCount, officeHourList: officeHourList])
    }

    def save(ClassNameCommand classNameCommand) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        if (classNameCommand.hasErrors()) {
            def errorList = classNameCommand?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, errorList?.join('\n'))
            outPut=result as JSON
            render outPut
            return
        }
        ClassName className
        String message
        ClassName ifExistClassName
        if (params.id) {
            className = ClassName.get(classNameCommand.id)
            if (!className) {
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
                outPut=result as JSON
                render outPut
                return
            }
            ifExistClassName=ClassName.findByNameAndActiveStatusAndIdNotEqual(classNameCommand.name,ActiveStatus.ACTIVE,classNameCommand.id)
            if(ifExistClassName) {
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE,'Class Name already Exist')
                outPut=result as JSON
                render outPut
                return
            }
            className.properties = classNameCommand.properties
            if (classNameCommand.isCollegeSection) {
                className.academicYear = schoolService.collegeWorkingYear()
            } else {
                className.isCollegeSection = false
            }


            result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
            message ='Class Updated successfully'
        } else {
            ifExistClassName=ClassName.findByNameAndActiveStatus(classNameCommand.name,ActiveStatus.ACTIVE)
            if(ifExistClassName) {
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE,'Class Name already Exist')
                outPut=result as JSON
                render outPut
                return
            }
            className = new ClassName(classNameCommand.properties)
            if (classNameCommand.isCollegeSection) {
                className.academicYear = schoolService.collegeWorkingYear()
            } else {
                className.isCollegeSection = false
            }
            result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
            message ='Class Added successfully'
        }

        if(className.hasErrors() || !className.save()){
            def errorList = className?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            message = errorList?.join('\n')
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
        }
        result.put(CommonUtils.MESSAGE,message)
        outPut=result as JSON
        render outPut

    }
    def inactive(Long id) {
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        ClassName className = ClassName.get(id)
        if (!className) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
        }

        if(className.activeStatus.equals(ActiveStatus.INACTIVE)){
            className.activeStatus=ActiveStatus.ACTIVE
        } else {
            className.activeStatus=ActiveStatus.INACTIVE
        }

        className.save()
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, "Class Deleted successfully.")
        outPut = result as JSON
        render outPut
    }

    def list() {
        LinkedHashMap gridData
        String result
        LinkedHashMap resultMap =classNameService.classNamePaginateList(params)

        if(!resultMap || resultMap.totalCount== 0){
            gridData = [iTotalRecords: 0, iTotalDisplayRecords: 0, aaData: []]
            result = gridData as JSON
            render result
            return
        }
        int totalCount =resultMap.totalCount
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
        ClassName className = ClassName.read(id)
        if (!className) {
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
        result.put(CommonUtils.OBJ,className)
        outPut = result as JSON
        render outPut
    }
}
