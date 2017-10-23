package com.grailslab.command

import com.grailslab.enums.PrintOptionType

/**
 * Created by Hasnat on 6/28/2015.
 */
@grails.validation.Validateable
class CollectionSummaryPrintCommand {
    PrintOptionType printOptionType
    static constraints = {
        printOptionType nullable: true
    }
}
