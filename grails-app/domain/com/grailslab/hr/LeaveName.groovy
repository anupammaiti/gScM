package com.grailslab.hr

import com.grailslab.enums.LeavePayType
import com.grailslab.gschoolcore.BasePersistentObj


class LeaveName extends BasePersistentObj{
    String name             //sick Leave
    Integer numberOfDay =0    // 15
    LeavePayType payType     // Pay Casual Non Pay

    static constraints = {
        numberOfDay nullable: true
    }
}
