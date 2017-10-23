package com.grailslab.hr

import com.grailslab.CommonUtils
import com.grailslab.command.HrPeriodCommand
import grails.converters.JSON
import org.joda.time.DateTime
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.springframework.dao.DataIntegrityViolationException

class HrPeriodController {

    def hrPeriodService
    def messageSource

    def index() {
        def resultMap = hrPeriodService.periodList()

        if (!resultMap) {
            render(view: '/hr/hrPeriod', model: [dataReturn: null])
            return
        }
        render(view: '/hr/hrPeriod', model: [dataReturn: resultMap])
    }

    def edit(Long id){
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        HrPeriod hrPeriod = HrPeriod.read(id)
        if (!hrPeriod) {
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
        result.put(CommonUtils.OBJ,hrPeriod)
        outPut = result as JSON
        render outPut
    }
    def delete(Long id){
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        HrPeriod hrPeriod = HrPeriod.get(id)
        if (!hrPeriod) {
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        try {
            hrPeriod.delete()
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

    }
    def save(HrPeriodCommand command){
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
        HrPeriod hrPeriod

        String message
        DateTimeFormatter timeFormatter = DateTimeFormat.forPattern(CommonUtils.UI_TIME_INPUT);
        LocalTime startTime =timeFormatter.parseLocalTime(command.startOn)
        LocalTime endTime = startTime.plusMinutes(command.duration)
        DateTime dummyDateTime = new DateTime().withYear(2014).withMonthOfYear(1).withDayOfMonth(1).withHourOfDay(startTime.getHourOfDay()).withMinuteOfHour(startTime.getMinuteOfHour())
        if(command.id){
            hrPeriod = HrPeriod.get(command.id)
            if(!hrPeriod){
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
                outPut = result as JSON
                render outPut
                return
            }
            hrPeriod.properties['name','startOn','duration','lateTolerance'] = command.properties
            message ="Period updated successfully."
            result.put(CommonUtils.IS_ERROR, Boolean.FALSE)

        }else {
            hrPeriod = new HrPeriod(command.properties)
            message ="Period added successfully."
            result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        }
        hrPeriod.periodRange = "${startTime.toString(timeFormatter)} - ${endTime.toString(timeFormatter)}"
        hrPeriod.startTime = dummyDateTime.toDate()
        if(hrPeriod.hasErrors() || !hrPeriod.save()){
            def errorList = hrPeriod?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            message = errorList?.join('\n')
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
        }
        result.put(CommonUtils.MESSAGE, message)
        outPut = result as JSON
        render outPut
        return
    }
}
