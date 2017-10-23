package com.grailslab.hr

import com.grailslab.enums.LeaveApplyType
import com.grailslab.enums.LeaveApproveStatus
import com.grailslab.enums.LeavePayType
import com.grailslab.gschoolcore.BasePersistentObj

class LeaveApply extends BasePersistentObj{
    Employee employee
    LeaveName leaveName
    LeaveApplyType applyType
    LeavePayType payType
    Date fromDate
    Date toDate
    Integer numberOfDay
    Date applyDate      // code input.. not from UI
    String reason       //Reason for Leave.
    String remarks      // Optional remarks/Comments like with to without pay leave
    String argentContactNo     //required. Provide a second phone No
    String placeOnLeave
    LeaveApproveStatus approveStatus = LeaveApproveStatus.Draft    //One of Draft,Applied,Approved or Rejected. Returned Application also treat as Draft
    String supportedBy  //employee.empid
    String application
    static constraints = {
        argentContactNo nullable: true
        reason nullable: true
        remarks nullable: true
        supportedBy nullable: true
        placeOnLeave nullable: true
        application nullable: true
        approveStatus nullable: true
        applyDate nullable: true
        payType nullable: true
    }
    static mapping = {
        application type: 'text'
    }
}
