package com.grailslab.command.library
/**
 * Created by Hasnat on 6/28/2015.
 */
@grails.validation.Validateable
class BookUserCommand {

    String stdEmpAcademicYr
    String stuEmpID
    String userHistoryPrintType
    Date userFromDate
    Date userToDate

    static constraints = {
        userFromDate nullable: true
        stdEmpAcademicYr nullable: true
    }
}
