package com.grailslab.command

import com.grailslab.enums.LeaveApplyType
import com.grailslab.enums.LeaveApproveStatus
import com.grailslab.hr.Employee
import com.grailslab.hr.LeaveName

/**
 * Created by Hasnat on 6/27/2015.
 */
@grails.validation.Validateable
class LeaveApplyApproveCommand {
    Long id
    Employee employee       // employee drop down. if Teacher, own selected with no change, admin able to select any employee
    LeaveName leaveName
    Date fromDate
    Date toDate
    LeaveApplyType applyType
    String reason       //Reason for Leave. Required
    String remarks      // Optional remarks/Comments like with to without pay leave
    String placeOnLeave // required. Possible where he will stay on Leave Period
    LeaveApproveStatus approveStatus = LeaveApproveStatus.Approved
    String argentContactNo
    String supportedBy
    Date applyDate


    static constraints = {
        id nullable: true
        remarks nullable: true
        argentContactNo nullable: true
        placeOnLeave nullable: true
        reason nullable: true
        leaveName nullable: true
        supportedBy nullable: true
        applyDate nullable: true

    }
}
