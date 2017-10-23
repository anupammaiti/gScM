package com.grailslab.open

import com.grailslab.CommonUtils
import com.grailslab.command.OpenTimeLineCommand
import com.grailslab.gschoolcore.ActiveStatus
import grails.converters.JSON

class TimeLineController {
    def messageSource
    def timeLineService

    def index() {
        LinkedHashMap resultMap = timeLineService.timeLinePaginateList(params)
        if (!resultMap || resultMap.totalCount == 0) {
            render(view: '/open/timeLine/timeLine', model: [dataReturn: null, totalCount: 0])
            return
        }
        int totalCount = resultMap.totalCount
        render(view: '/open/timeLine/timeLine', model: [dataReturn: resultMap.results, totalCount: totalCount])
    }

    def list() {
        LinkedHashMap gridData
        String result
        LinkedHashMap resultMap = timeLineService.timeLinePaginateList(params)

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
    def create(){
        render(view: '/open/timeLine/createOrEdit')
    }
    def edit(Long id) {
        OpenTimeLine timeLine = OpenTimeLine.read(id)
        if (!timeLine) {
            flash.message=CommonUtils.COMMON_NOT_FOUND_MESSAGE
            redirect(action: 'index')
            return
        }
        render(view: '/open/timeLine/createOrEdit', model: [timeLine:timeLine])
    }


    def save(OpenTimeLineCommand command) {
        if (!request.method.equals('POST')) {
            flash.message="Request Method not allow here."
            redirect(action: 'index')
            return
        }
        if (command.hasErrors()) {
            def errorList = command?.errors?.allErrors?.collect { messageSource.getMessage(it, null) }
            flash.message=errorList?.join('\n')
            redirect(action: 'index')
            return
        }

        OpenTimeLine timeLine
        if (command.id) {
            timeLine = OpenTimeLine.get(command.id)
            if (!timeLine) {
                flash.message=CommonUtils.COMMON_NOT_FOUND_MESSAGE
                redirect(action: 'index')
                return
            }
            timeLine.properties['title','name','sortOrder','iconClass','description'] = command.properties
            flash.message = 'TimeLine Updated Successfully.'
        } else {
            timeLine = new OpenTimeLine(command.properties)
            flash.message = 'TimeLine Added Successfully.'
        }
        if (timeLine.hasErrors() || !timeLine.save()) {
            def errorList = timeLine?.errors?.allErrors?.collect { messageSource.getMessage(it, null) }
            flash.message = errorList?.join('\n')
            redirect(action: 'index')
            return
        }
        redirect(action: 'index')
    }

    def inactive(Long id) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        OpenTimeLine timeLine = OpenTimeLine.get(id)
        if (!timeLine) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }

        if(timeLine.activeStatus.equals(ActiveStatus.INACTIVE)){
            timeLine.activeStatus=ActiveStatus.ACTIVE
        }else {
            timeLine.activeStatus=ActiveStatus.INACTIVE
        }

        timeLine.save(flush: true)
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, 'Status Change Successfully')
        outPut = result as JSON
        render outPut
    }

    def delete(Long id) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        OpenTimeLine timeLine = OpenTimeLine.get(id)
        if (!timeLine) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }

        timeLine.delete()

        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, 'TimeLine Deleted Successfully')
        outPut = result as JSON
        render outPut
    }
}
