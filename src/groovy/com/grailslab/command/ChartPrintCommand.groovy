package com.grailslab.command

import com.grailslab.enums.AccountType
import com.grailslab.enums.PrintOptionType

/**
 * Created by Hasnat on 6/28/2015.
 */
@grails.validation.Validateable
class ChartPrintCommand {
    AccountType accountType
    PrintOptionType printOptionType
    static constraints = {
        accountType nullable: true
        printOptionType nullable: true
    }
}
