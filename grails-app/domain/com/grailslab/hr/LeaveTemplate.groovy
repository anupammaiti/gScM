package com.grailslab.hr

import com.grailslab.enums.LeaveApplyType
import com.grailslab.gschoolcore.BasePersistentObj

class LeaveTemplate extends BasePersistentObj{
    LeaveApplyType applyType
    String leaveTemplate

    static constraints = {
    }

    static mapping = {
        leaveTemplate type: 'text'
    }
}
