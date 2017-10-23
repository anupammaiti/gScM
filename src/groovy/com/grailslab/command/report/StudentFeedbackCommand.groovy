package com.grailslab.command.report
/**
 * Created by Aminul on 2/24/2016.
 */
@grails.validation.Validateable
class StudentFeedbackCommand {
    Long stdClassName
    Long stdSectionName
    Long stdWeekNo
    Long stdRating
    String stdSortedBy
    String stdPrintType


    static constraints = {
        stdWeekNo nullable: true
        stdPrintType nullable: true
    }
}
