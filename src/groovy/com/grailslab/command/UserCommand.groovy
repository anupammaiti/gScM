package com.grailslab.command

import com.grailslab.enums.MainUserType

/**
 * Created by Hasnat on 6/27/2015.
 */
@grails.validation.Validateable
class UserCommand {
    Long id
    String username
    String password
    Long userRefId
    MainUserType mainUserType

    static constraints = {
        id nullable: true
        username blank: false, unique: true
        password blank: false
        userRefId nullable: true
        mainUserType nullable: true
    }
}
@grails.validation.Validateable
class UserAccessCommand {
    Long userId
    String objId
    String username
    String mainUserType
    String empRole

    static constraints = {
        userId nullable: true
        objId nullable: true
        username blank: false
    }
}
