package com.grailslab.command.report
/**
 * Created by Aminul on 2/24/2016.
 */
@grails.validation.Validateable
class AverageFeedbackCommand {
    Long avgClassName
    Long avgSectionName
    Long avgWeekNo
    String avgPrintType


    static constraints = {
        avgWeekNo nullable: true
        avgPrintType nullable: true
    }
}
