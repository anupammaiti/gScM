package com.grailslab.command

import com.grailslab.hr.HrCategory

/**
 * Created by Hasnat on 6/27/2015.
 */
@grails.validation.Validateable
class DesignationCommand {
    Long id
    String name
    HrCategory hrCategory
    Integer sortOrder

    static constraints = {
        id nullable: true
    }
}
