package com.grailslab

import com.grailslab.enums.ExamType
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.settings.ExamPeriod
import com.grailslab.settings.School

class ExamPeriodService {
    static transactional = false

    def periodList() {
        def c = ExamPeriod.createCriteria()
        def results = c.list() {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
            }
            order("examType", CommonUtils.SORT_ORDER_DESC)
            order("startTime", CommonUtils.SORT_ORDER_DESC)
            order("duration", CommonUtils.SORT_ORDER_DESC)
        }
        return results
    }
    def periodDropDownList(ExamType examType = null) {
        List dataReturns = new ArrayList()
        def c = ExamPeriod.createCriteria()
        def results = c.list() {
            and {
                if(examType){
                    eq("examType", examType)
                    eq("activeStatus", ActiveStatus.ACTIVE)
                }
            }
            order("startTime", CommonUtils.SORT_ORDER_ASC)
            order("duration", CommonUtils.SORT_ORDER_DESC)
        }
        results.each { period ->
            dataReturns.add([id: period.id, name: period.name])
        }
        return dataReturns
    }

}
