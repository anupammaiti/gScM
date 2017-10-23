package com.grailslab.command

import com.grailslab.settings.ClassName
import com.grailslab.stmgmt.RegForm

/**
 * Created by Hasnat on 6/28/2015.
 */
@grails.validation.Validateable
class OnlineStep1Command {
    RegForm regForm
    ClassName className
    String name
    String fathersName
    String mobile
    Date birthDate

    static constraints = {
    }
}
