package com.grailslab.command
@grails.validation.Validateable
class SalConfigurationCommand {
    Double  extraClassRate
    Integer lateFineForDays
    Double  pfContribution
    String  pfCalField


    static constraints = {
    }
}
