package com.grailslab.hr

import com.grailslab.CommonUtils
import com.grailslab.command.LeaveNameCommand
import com.grailslab.gschoolcore.ActiveStatus
import grails.converters.JSON

class LeaveNameController {

    def messageSource
    def leaveNameService

    def index() {
        render(view: '/leaveMgmt/leaveName')

    }

    def create(long id) {
        render(view: '/leaveMgmt/leaveNameCreateOrEdit')

    }

    def list() {
        LinkedHashMap gridData
        String result
        LinkedHashMap resultMap = leaveNameService.leaveNamePaginateList(params)

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

    def save(LeaveNameCommand command) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        if (command.hasErrors()) {
            render(view: '/leaveMgmt/leaveNameCreateOrEdit', model: [command: command])
            return
        }
        int refAlreadyExist
        if(command.id){
            refAlreadyExist = LeaveName.countByActiveStatusAndNameAndIdNotEqual(ActiveStatus.ACTIVE, command.name, command.id)
        } else {
            refAlreadyExist = LeaveName.countByActiveStatusAndName(ActiveStatus.ACTIVE, command.name)

        }
        if(refAlreadyExist > 0){
            flash.message="Leave name already exist"
            render(view: '/leaveMgmt/leaveNameCreateOrEdit')
            return
        }
        LeaveName leaveName
        if (command.id) {
            leaveName = LeaveName.get(command.id)
            if (!leaveName) {
                flash.message=CommonUtils.COMMON_NOT_FOUND_MESSAGE
                render(view: '/leaveMgmt/leaveNameCreateOrEdit')
                return
            }
            leaveName.properties = command.properties
            flash.message="Leave saved successfully"
        } else {
            leaveName = new LeaveName(command.properties)
            flash.message="Leave updated successfully"

        }
        leaveName.save()
        redirect(action: 'index')
    }


    def edit(Long id) {
        LeaveName leaveName = LeaveName.read(id)
        if (!leaveName) {
            flash.message=CommonUtils.COMMON_NOT_FOUND_MESSAGE
            redirect(action: 'index')
            return
        }
        render(view: '/leaveMgmt/leaveNameCreateOrEdit', model: [leaveName:leaveName])
    }

    def inactive(Long id) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        LeaveName leaveName = LeaveName.get(id)
        if (!leaveName) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }

        if(leaveName.activeStatus.equals(ActiveStatus.ACTIVE)){
            leaveName.activeStatus=ActiveStatus.INACTIVE
        }else {
            leaveName.activeStatus=ActiveStatus.ACTIVE
        }

        leaveName.save()
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, 'Deleted Successfully')
        outPut = result as JSON
        render outPut
    }
}
