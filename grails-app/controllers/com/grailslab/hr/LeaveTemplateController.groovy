package com.grailslab.hr

import com.grailslab.CommonUtils
import com.grailslab.command.LeaveApplicationTemplateCommand
import com.grailslab.gschoolcore.ActiveStatus
import grails.converters.JSON

class LeaveTemplateController {
    def leaveTemplateService
    def messageSource

    def index() {
        render(view: '/leaveMgmt/leaveTemplate')
    }

    def create() {
        render(view: '/leaveMgmt/leaveTemplateCreateOrEdit')
    }

    def list() {
        LinkedHashMap gridData
        String result
        LinkedHashMap resultMap = leaveTemplateService.leaveApplicationPaginateList(params)

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

    def save(LeaveApplicationTemplateCommand command) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        if (command.hasErrors()) {
            render(view: '/leaveMgmt/leaveTemplateCreateOrEdit', model: [command: command])
            return
        }
        int refAlreadyExist
        if(command.id){
            refAlreadyExist = LeaveTemplate.countByApplyTypeAndActiveStatusAndIdNotEqual(command.applyType, ActiveStatus.ACTIVE, command.id)
        } else {
            refAlreadyExist = LeaveTemplate.countByApplyTypeAndActiveStatus(command.applyType, ActiveStatus.ACTIVE)

        }
        if(refAlreadyExist > 0){
            flash.message="Leave name already exist"
            render(view: '/leaveMgmt/leaveTemplateCreateOrEdit')
            return
        }
        LeaveTemplate leaveApplicationTemplate
        if (command.id) {
            leaveApplicationTemplate = LeaveTemplate.get(command.id)
            if (!leaveApplicationTemplate) {
                flash.message=CommonUtils.COMMON_NOT_FOUND_MESSAGE
                render(view: '/leaveMgmt/leaveTemplateCreateOrEdit')
                return
            }
            leaveApplicationTemplate.properties = command.properties
            flash.message="Template saved successfully"
        } else {
            leaveApplicationTemplate = new LeaveTemplate(command.properties)
            flash.message="Template updated successfully"
        }

        leaveApplicationTemplate.save()
        redirect(action: 'index')
    }


    def edit(Long id) {
        LeaveTemplate leaveTemplate = LeaveTemplate.read(id)
        if (!leaveTemplate) {
            flash.message=CommonUtils.COMMON_NOT_FOUND_MESSAGE
            redirect(action: 'index')
            return
        }
        render(view: '/leaveMgmt/leaveTemplateCreateOrEdit', model: [leaveTemplate:leaveTemplate])
    }

    def inactive(Long id) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        LeaveTemplate leaveApplicationTemplate = LeaveTemplate.get(id)
        if (!leaveApplicationTemplate) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }

        if(leaveApplicationTemplate.activeStatus.equals(ActiveStatus.ACTIVE)){
            leaveApplicationTemplate.activeStatus=ActiveStatus.INACTIVE
        }else {
            leaveApplicationTemplate.activeStatus=ActiveStatus.ACTIVE
        }

        leaveApplicationTemplate.save()
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, 'Template deleted Successfully')
        outPut = result as JSON
        render outPut
    }
}
