package com.grailslab.admin

import com.grailslab.CommonUtils
import com.grailslab.command.ClassPeriodCommand
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.settings.ClassPeriod
import grails.converters.JSON
import org.joda.time.DateTime
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

class ClassPeriodController {
    def classPeriodService
    def messageSource

    def index() {
        def resultMap = classPeriodService.periodList()

        if (!resultMap) {
            render(view: '/admin/classPeriod/classPeriod', model: [dataReturn: null])
            return
        }
        render(view: '/admin/classPeriod/classPeriod', model: [dataReturn: resultMap])
    }

    def edit(Long id){
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        ClassPeriod classPeriod = ClassPeriod.read(id)
        if (!classPeriod) {
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
        result.put(CommonUtils.OBJ,classPeriod)
        outPut = result as JSON
        render outPut
    }
    /*def delete(Long id){
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        ClassPeriod classPeriod = ClassPeriod.get(id)
        if (!classPeriod) {
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        try {
            def clsPeriods = ClassPeriod.findAllByActiveStatusAndShiftAndSortPositionGreaterThan(ActiveStatus.ACTIVE, classPeriod.shift,classPeriod.sortPosition)
            if(clsPeriods){
                result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
                result.put(CommonUtils.MESSAGE,"You only can delete last period of ${classPeriod.shift.value}")
                outPut = result as JSON
                render outPut
                return
            }
            classPeriod.delete()
        }
        catch(DataIntegrityViolationException e) {
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,"Period already in use. Please delete reference first")
            outPut = result as JSON
            render outPut
            return
        }
        result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
        result.put(CommonUtils.MESSAGE,"Period deleted successfully.")
        outPut = result as JSON
        render outPut

    }*/
    def inactive(Long id) {
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        ClassPeriod classPeriod = ClassPeriod.get(id)
        if (!classPeriod) {
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        def clsPeriods = ClassPeriod.findAllByActiveStatusAndShiftAndSortPositionGreaterThan(ActiveStatus.ACTIVE, classPeriod.shift,classPeriod.sortPosition)
        if(clsPeriods){
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,"You only can delete last period of ${classPeriod.shift.value}")
            outPut = result as JSON
            render outPut
            return
        }
        if(classPeriod.activeStatus.equals(ActiveStatus.INACTIVE)){
            classPeriod.activeStatus=ActiveStatus.ACTIVE
        } else {
            classPeriod.activeStatus=ActiveStatus.INACTIVE
        }

        classPeriod.save()
        result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
        result.put(CommonUtils.MESSAGE,"Period deleted successfully.")
        outPut = result as JSON
        render outPut
    }
    def save(ClassPeriodCommand command){
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
            outPut = result as JSON
            render outPut
            return
        }
        ClassPeriod classPeriod

        String message
        DateTimeFormatter timeFormatter = DateTimeFormat.forPattern(CommonUtils.UI_TIME_INPUT);
        LocalTime startTime =timeFormatter.parseLocalTime(command.startOn)
        LocalTime endTime = startTime.plusMinutes(command.duration)
        DateTime dummyDateTime = new DateTime().withYear(2014).withMonthOfYear(1).withDayOfMonth(1).withHourOfDay(startTime.getHourOfDay()).withMinuteOfHour(startTime.getMinuteOfHour())
        if(command.id){
            classPeriod = ClassPeriod.get(command.id)
            if(!classPeriod){
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
                outPut = result as JSON
                render outPut
                return
            }
            classPeriod.properties['name', 'startOn','duration'] = command.properties
            message ="Period updated successfully."
            result.put(CommonUtils.IS_ERROR, Boolean.FALSE)

        }else {
            def clsPeriods = ClassPeriod.findAllByShiftAndActiveStatus(command.shift, ActiveStatus.ACTIVE)
            if(clsPeriods){
                ClassPeriod lastPeriod = clsPeriods.last()
                if(lastPeriod.sortPosition>=command.sortPosition){
                    result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                    result.put(CommonUtils.MESSAGE, "Period Already added")
                    outPut = result as JSON
                    render outPut
                    return
                } else if(lastPeriod.sortPosition!=command.sortPosition-1){
                    result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                    result.put(CommonUtils.MESSAGE, "Previous Period not added")
                    outPut = result as JSON
                    render outPut
                    return
                }
            } else {
                if(command.sortPosition!=1){
                    result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                    result.put(CommonUtils.MESSAGE, "First Period not added")
                    outPut = result as JSON
                    render outPut
                    return
                }
            }
            classPeriod = new ClassPeriod(command.properties)
            message ="Period added successfully."
            result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        }
        classPeriod.period = "${startTime.toString(timeFormatter)} - ${endTime.toString(timeFormatter)}"
        classPeriod.startTime = dummyDateTime.toDate()
        if(classPeriod.hasErrors() || !classPeriod.save()){
            def errorList = classPeriod?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            message = errorList?.join('\n')
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
        }
        result.put(CommonUtils.MESSAGE, message)
        outPut = result as JSON
        render outPut
        return
    }
}
