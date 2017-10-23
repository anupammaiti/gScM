package com.grailslab.command

import com.grailslab.enums.LeaveApplyType
import com.grailslab.enums.LeaveApproveStatus
import com.grailslab.enums.LeavePayType
import com.grailslab.hr.Employee
import com.grailslab.hr.LeaveName

/**
 * Created by Hasnat on 6/27/2015.
 */
@grails.validation.Validateable
class LeaveInfoCommand {
    Long id
    Employee employee
    LeaveName leaveName  //Changeable
    Date fromDate
    Date toDate
    Integer numberOfDay //Changeable
    String remarks
    LeaveApproveStatus approveStatus = LeaveApproveStatus.Approved
    Employee approvedBy
    Date approvedOn      // code input.. not from UI
    LeaveApplyType applyType
    static constraints = {
        remarks nullable: true
        approvedBy nullable: true
        approvedOn nullable: true
        numberOfDay nullable: true
    }
}
