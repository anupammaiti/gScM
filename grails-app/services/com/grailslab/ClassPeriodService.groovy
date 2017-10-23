package com.grailslab

import com.grailslab.enums.Shift
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.settings.ClassPeriod
import com.grailslab.settings.School

class ClassPeriodService {
    static transactional = false

    def periodList() {
        def c = ClassPeriod.createCriteria()
        def results = c.list() {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
            }
            order("shift", CommonUtils.SORT_ORDER_ASC)
            order("sortPosition", CommonUtils.SORT_ORDER_ASC)
        }
        return results
    }
    def periodDropDownList(Shift shift = null) {
        List dataReturns = new ArrayList()
        def c = ClassPeriod.createCriteria()
        def results = c.list() {
            and {
                if (shift) {
                    eq("shift", shift)
                }
                eq("activeStatus", ActiveStatus.ACTIVE)
            }
            order("shift", CommonUtils.SORT_ORDER_ASC)
            order("sortPosition", CommonUtils.SORT_ORDER_ASC)
        }
        results.each { ClassPeriod classPeriod ->
            dataReturns.add([id: classPeriod.id, name: "$classPeriod.name ($classPeriod.period)"])
        }
        return dataReturns
    }
}
