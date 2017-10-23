package com.grailslab.command

import com.grailslab.accounting.ChartOfAccount
import com.grailslab.enums.DueOnType
import com.grailslab.enums.FeeAppliedType
import com.grailslab.enums.FeeIterationType
import com.grailslab.enums.FeeType
import com.grailslab.settings.ClassName

/**
 * Created by Hasnat on 6/27/2015.
 */
@grails.validation.Validateable
class StationaryItemCommand {
    Long id      //this will reference with First Label account. ie> parent account
    ChartOfAccount account      //this will reference with First Label account. ie> parent account
    String name
    String description
    String code
    FeeType feeType                     //compulsory, optional
    FeeAppliedType feeAppliedType       //all, class, section
    DueOnType dueOnType                 //SalaryAdvance, due, on delivery
    FeeIterationType iterationType      //On Receive, daily, weekly, monthly, yearly

    Double amount = 0       //Used in case of Selling
    Double discount = 0   //in percentage
    Double netPayable = 0
    Integer stockQuantity = 0

    ClassName className
//    Section section

    Boolean quickFee1 = Boolean.FALSE
    Boolean quickFee2 = Boolean.FALSE

    static constraints = {
        id nullable: true
        className nullable: true
        amount nullable: true
        description nullable: true
        discount nullable: true
        netPayable nullable: true
        stockQuantity nullable: true
        quickFee1 nullable: true
        quickFee2 nullable: true
    }
}
