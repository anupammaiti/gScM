package com.grailslab.attn

import com.grailslab.CommonUtils
import com.grailslab.command.AttnRecordDayCommand
import grails.converters.JSON

class RecordDayController {

    def messageSource
    def recordDayService

    def index() {
        AttnRecordDay lastRecord = AttnRecordDay.last()
        Date lastRecordDate
        if (lastRecord) {
            lastRecordDate = lastRecord.recordDate
        } else {
            lastRecordDate = new Date()
        }
        render(view: '/attn/recordDay/recordDayList', model: [lastRecordDate: lastRecordDate])
    }

    def list() {
        LinkedHashMap gridData
        String result
        LinkedHashMap resultMap = recordDayService.paginateList(params)

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
    def save(AttnRecordDayCommand command) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        String message = ''
        if (command.hasErrors()) {
            def errorList = command?.errors?.allErrors?.collect { messageSource.getMessage(it, null) }
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, errorList?.join(','))
            outPut = result as JSON
            render outPut
            return
        }
        Date dateStart = command.startDate
        Date dateEnd = command.endDate
        recordDayService.addRecordDay(dateStart, dateEnd)

        result.put(CommonUtils.IS_ERROR, false)
        result.put(CommonUtils.MESSAGE, "Added successfully")
        outPut = result as JSON
        render outPut
    }
    def edit(Long id){
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        AttnRecordDay attnRecordDay = AttnRecordDay.read(id)
        if(!attnRecordDay){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            return
        }
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.OBJ, attnRecordDay)
        result.put('attendanceDate', attnRecordDay.recordDate)
        outPut = result as JSON
        render outPut
    }
    def update(){
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        String message = ''
        AttnRecordDay attnRecordDay = AttnRecordDay.get(params.getLong("id"))
        if(!attnRecordDay){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            return
        }
        attnRecordDay.dayType = params.dayType
        attnRecordDay.holidayName = params.workingDay
        attnRecordDay.save()
        result.put(CommonUtils.IS_ERROR, false)
        result.put(CommonUtils.MESSAGE, "Updated successfully")
        outPut = result as JSON
        render outPut
    }

    def reloadData(Long id) {
        if (!request.method == 'POST') {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        AttnRecordDay recordDay = AttnRecordDay.get(id)
        if (!recordDay) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }

        if (recordDay.forceLoad) {
            recordDay.forceLoad = false
        } else {
            recordDay.forceLoad = true
        }
        recordDay.save()
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, 'Record Date load settings updated successfully')
        outPut = result as JSON
        render outPut
    }
}
