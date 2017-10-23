package com.grailslab.accounting


import com.grailslab.enums.DueOnType
import com.grailslab.enums.FeeAppliedType
import com.grailslab.enums.FeeIterationType
import com.grailslab.enums.FeeType
import com.grailslab.gschoolcore.BasePersistentObj
import com.grailslab.settings.ClassName

class Fees extends BasePersistentObj{
    ChartOfAccount account      //this will reference with First Label account. ie> parent account

    Double amount =0
    Double discount =0     //in percentage
    Double netPayable =0

    //enums
    FeeType feeType                     //compulsory, optional
    FeeAppliedType feeAppliedType       //all, class, section
    DueOnType dueOnType                 //SalaryAdvance, due, on delivery
    FeeIterationType iterationType      //On Receive, daily, weekly, monthly, yearly

    ClassName className

    Boolean quickFee1=Boolean.FALSE
    Boolean quickFee2=Boolean.FALSE
    Boolean quickFee3=Boolean.FALSE    // online fee

    static constraints = {
        className nullable: true
        quickFee3 nullable: true
    }
}
