package com.grailslab.command

/**
 * Created by Aminul
 */
@grails.validation.Validateable
class SignUpCommand {
    String userId
    String mobileNo
    Date birthDate
    String password

    static constraints = {

    }
}
