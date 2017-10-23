package com.grailslab.command

import com.grailslab.enums.PayType

/**
 * Created by Hasnat on 6/28/2015.
 */
@grails.validation.Validateable
class BookReceiveCommand {
    Long id
    Double fine
    PayType payType
    PayType returnType
    Date submissionDate
    static constraints = {
    }
}
