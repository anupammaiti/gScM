package com.grailslab.accounting


import com.grailslab.enums.DueOnType
import com.grailslab.enums.FeeAppliedType
import com.grailslab.enums.FeeIterationType
import com.grailslab.enums.FeeType
import com.grailslab.gschoolcore.BasePersistentObj
import com.grailslab.settings.ClassName

class StationaryItem extends BasePersistentObj{
    ChartOfAccount account      //this will reference with First Label account. ie> parent account
    String name
    String description
    Integer code
    FeeType feeType                     //compulsory, optional
    FeeAppliedType feeAppliedType       //all, class, section
    DueOnType dueOnType                 //SalaryAdvance, due, on delivery
    FeeIterationType iterationType      //On Receive, daily, weekly, monthly, yearly

    Double amount =0        //Used in case of Selling
    Double discount =0     //in percentage
    Double netPayable =0
    Integer stockQuantity = 0

    ClassName className
//    Section section

    Boolean quickFee1=Boolean.FALSE
    Boolean quickFee2=Boolean.FALSE

    static constraints = {
        className nullable: true
        description nullable: true
    }
}
