package com.grailslab.command

import com.grailslab.enums.MainUserType

/**
 * Created by Hasnat on 6/27/2015.
 */
@grails.validation.Validateable
class ResetPasswordCommand {
    String oldPassword
    String newPassword
    String confirmPassword

    static constraints = {
        newPassword size: 5..12
        confirmPassword size: 5..12

    }




}
