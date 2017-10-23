package com.grailslab

import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.hr.HrPeriod

class HrPeriodService {

    static transactional = false

    def periodList() {
        def c = HrPeriod.createCriteria()
        def results = c.list() {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
            }
            order("startTime", CommonUtils.SORT_ORDER_DESC)
            order("duration", CommonUtils.SORT_ORDER_DESC)
        }
        return results
    }
    def periodDropDownList() {
        List dataReturns = new ArrayList()
        def c = HrPeriod.createCriteria()
        def results = c.list() {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
            }
            order("startTime", CommonUtils.SORT_ORDER_ASC)
            order("duration", CommonUtils.SORT_ORDER_DESC)
        }
        results.each {HrPeriod period ->
            dataReturns.add([id: period.id, name: "${period.name} [${period.periodRange}]"])
        }
        return dataReturns
    }
    Boolean isLateEntry(Date entryTime, Date expectedTime, Integer lateTolarance = 0) {
        if (!entryTime || !expectedTime) return false

        long t1 = entryTime.getHours()*60*60+entryTime.getMinutes()*60;
        long t2 = expectedTime.getHours()*60*60+expectedTime.getMinutes()*60 + lateTolarance * 60;

        return t1 > t2
    }
}
