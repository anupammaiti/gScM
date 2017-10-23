package com.grailslab

import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.settings.NoticeBoard

class NoticeScrollService {
    static transactional = false
    String scrollText() {
        Date toDay = new Date().clearTime()
        def c = NoticeBoard.createCriteria()
        def results = c.list() {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
                eq("keepScroll", Boolean.TRUE)
                le("publish", toDay)
                ge("expire", toDay)
            }
            order("id", CommonUtils.SORT_ORDER_DESC)
        }
        String scrollText = ""
        results.each {NoticeBoard notice ->
            scrollText+="<span style='color: ${notice.scrollColor};'>${notice.scrollText}</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
        }
        return scrollText
    }
}
