package com.grailslab.hr

import com.grailslab.CommonUtils
import com.grailslab.command.LeaveApplyCommand
import com.grailslab.enums.LeaveApproveStatus
import com.grailslab.gschoolcore.ActiveStatus
import grails.converters.JSON
import org.joda.time.DateTime

class LeaveApprovalController {

    def leaveService
    def messageSource
    def employeeService
    def schoolService
    def jasperService
    def springSecurityService
    def leaveApplicationService

    def index() {
        def leaveList = leaveService.allLeaveDropDownList()
        render(view: '/leaveMgmt/leaveApplyAdmin',model: [leaveList: leaveList])
    }

    def addLeaveApplication(Long id){
        LeaveApply leaveApply = LeaveApply.read(id)
        if (!leaveApply || leaveApply.activeStatus != ActiveStatus.ACTIVE) {
            flash.message=CommonUtils.COMMON_NOT_FOUND_MESSAGE
            redirect(action: 'index')
            return
        }
        if (leaveApply.approveStatus == LeaveApproveStatus.Approved) {
            redirect(controller: 'leaveReport', action: 'printApplication', id: leaveApply.id)
            return
        }
        String templateText
        if (leaveApply.application) {
            templateText = leaveApply.application
        } else {
            LeaveTemplate leaveTemplate = LeaveTemplate.findByApplyTypeAndActiveStatus(leaveApply.applyType, ActiveStatus.ACTIVE)
            templateText = leaveTemplate?.leaveTemplate
        }

        render(view: '/leaveMgmt/leaveApplication', model: [leaveApply: leaveApply, templateText: templateText])
    }
    def saveLeaveApplication(Long id){
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LeaveApply leaveApply = LeaveApply.get(id)
        leaveApply.application = params.leaveTemplate
        leaveApply.save(flush: true)
        redirect(controller: 'leaveReport', action: 'printApplication', id: leaveApply.id)
    }

    def list() {
        LinkedHashMap gridData
        String result
        LinkedHashMap resultMap = leaveApplicationService.leaveApprovePaginateList(params)

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

    def edit(Long id) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        LeaveApply apply = LeaveApply.read(id)
        if (!apply) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }

        Employee employee = apply.employee
        String supportedBy
        if (apply.supportedBy) {
            Employee replacement = Employee.findByEmpID(apply.supportedBy)
            supportedBy = replacement?.empID + ' - ' + replacement?.name
        }
        String reason = apply?.reason
        result.put('employee', "${employee.empID} - ${employee.name} - ${employee.hrDesignation.name}")
        result.put('supportedBy', supportedBy)
        result.put('leaveName', apply.leaveName?.name)
        result.put('reason', reason)
        result.put('numberOfDay', apply.numberOfDay)
        result.put('argentContactNo', apply.argentContactNo)
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.OBJ, apply)
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
        LeaveApply apply = LeaveApply.get(id)
        if (!apply) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        apply.activeStatus = ActiveStatus.DELETE
        apply.save()
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, 'Inactive Successfully')
        outPut = result as JSON
        render outPut
    }



    def saveApplication(LeaveApplyCommand command) {
        if (!request.method.equals('POST')) {
           // redirect(action: 'leaveApply')
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut

        //   String message
        if (command.hasErrors()) {
            def errorList = command?.errors?.allErrors?.collect { messageSource.getMessage(it, null) }
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, errorList?.join('\n'))
            outPut = result as JSON
            render outPut
            return
        }

        Boolean isSaveSuccess = false
        String message

        LeaveApply leaveApply
        if (command.id) {
            leaveApply = LeaveApply.get(command.id)
            if (!leaveApply) {
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
                outPut = result as JSON
                render outPut
                return
            }
            if (params.approveType) {
                leaveApply.properties = command.properties
                if (params.approveType == "Approve") {
                    leaveApply.approveStatus = LeaveApproveStatus.Approved
                    message = "Leave approved successfully"
                } else {
                    leaveApply.approveStatus = LeaveApproveStatus.Rejected
                    message = "Leave rejected successfully"
                }
                isSaveSuccess = leaveService.saveLeaveApplication(leaveApply)
            } else {
                leaveApply.properties = command.properties
                isSaveSuccess = leaveService.saveLeaveApplication(leaveApply)
                message = "Leave updated successfully"
            }
            result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        } else {
            leaveApply = new LeaveApply(command.properties)
            isSaveSuccess = leaveService.saveLeaveApplication(leaveApply)
            result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
            message = "Leave added successfully"
        }
        if (!isSaveSuccess){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "Your paid leave quota for this year exceeded. Please try unpaid or contact admin")
            outPut = result as JSON
            render outPut
            return
        }
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, message)
        outPut = result as JSON
        render outPut
        return
    }
}

