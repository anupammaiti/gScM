package com.grailslab.command

import com.grailslab.enums.HrKeyType

/**
 * Created by Hasnat on 6/27/2015.
 */
@grails.validation.Validateable
class HrCategoryCommand {
    Long id
    String name
    Integer sortOrder
//    HrKeyType hrKeyType

    static constraints = {
        id nullable: true
    }
}

@grails.validation.Validateable
class HrStaffCategoryCommand {
    Long id
    String keyName
    String name
    String description
    Integer sortOrder

    static constraints = {
        id nullable: true
    }
}
