package com.grailslab.admin

import com.grailslab.CommonUtils
import com.grailslab.command.ClassRoutineCommand
import com.grailslab.settings.ClassRoutine
import grails.converters.JSON

class ClassRoutineController {

    def classPeriodService
    def studentService
    def schoolService
    def classRoutineService
    def uploadService
    def messageSource
    def classNameService

    def index() {
        def classNameList = classNameService.allClassNames()
        render(view: '/classRoutine/classRoutine', model: [classNameList: classNameList,])
    }
    def teacherRoutine() { }

    def list() {
        LinkedHashMap gridData
        String result
        String routineType = params.routineType
        LinkedHashMap resultMap
        if(routineType == "teacherRoutine"){
            resultMap = classRoutineService.teacherRoutineList(params)
        }else{
            resultMap = classRoutineService.classRoutineList(params)
        }

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

    def classRoutineCreate(){
        def classNameList = classNameService.allClassNames()
        def classPeriodList = classPeriodService.periodDropDownList()
        render(view: '/classRoutine/classRoutineCreateOrEdit', model: [classNameList: classNameList, classPeriodList: classPeriodList])
    }

    def teacherRoutineCreate(){
        def classNameList = classNameService.allClassNames()
        def classPeriodList = classPeriodService.periodDropDownList()
        render(view: '/classRoutine/teacherRoutineCreateOrEdit', model: [classNameList: classNameList, classPeriodList: classPeriodList])
    }
    def save(ClassRoutineCommand command) {
        if (!request.method.equals('POST')) {
            flash.message="Request Method not allow here."
            return
        }
        ClassRoutine classRoutine
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        if (command.hasErrors()) {
            result.put('message','Please fill the form correctly')
            outPut=result as JSON
            render outPut
            return
        }
        List newDays = command.days.split(",") as List
        List previousDays
        boolean isValidDay = true
        // check period + teacher if any class
        def previousClasses = classRoutineService.periodClassesByEmployee(command.classPeriod, command.employee, command.academicYear)
        for (periodClass in previousClasses) {
            previousDays = periodClass.days.split(",") as List
            isValidDay = classRoutineService.isValidDays(previousDays, newDays)
            if (!isValidDay) break
        }
        if (!isValidDay) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "${command.employee.name} has already added class for ${command.classPeriod.period} on ${command.days}")
            outPut = result as JSON
            render outPut
            return
        }
        // check period + section if any classdef periodClassesByEmployee = classRoutineService.periodClassesByEmployee(command.classPeriod, command.employee, command.academicYear)
        previousClasses = classRoutineService.periodClassesBySection(command.classPeriod, command.section, command.academicYear)
        for (periodClass in previousClasses) {
            previousDays = periodClass.days.split(",") as List
            isValidDay = classRoutineService.isValidDays(previousDays, newDays)
            if (!isValidDay) break
        }
        if (!isValidDay) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "${command.section.name} has already added class for ${command.classPeriod.period} on ${command.days}")
            outPut = result as JSON
            render outPut
            return
        }

        classRoutine = new ClassRoutine(command.properties)
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, 'Class Routine add Successfully')

        classRoutine.save()
        outPut = result as JSON
        render outPut
        return
    }
    def delete(Long id) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        ClassRoutine classRoutine = ClassRoutine.get(id)
        if (!classRoutine) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        classRoutine.delete()
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, 'Class Routine Deleted Successfully')
        outPut = result as JSON
        render outPut
    }

}
