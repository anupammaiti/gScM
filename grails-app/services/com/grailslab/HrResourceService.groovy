package com.grailslab

import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.hr.HrStaffCategory
import grails.transaction.Transactional

class HrResourceService {
    static transactional = false

    def getStaffCategories() {
        def result = HrStaffCategory.findAllByActiveStatus(ActiveStatus.ACTIVE,[sort:'sortOrder'])
        return result
    }
}
