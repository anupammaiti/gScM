package com.grailslab.accounting


import com.grailslab.enums.DueOnType
import com.grailslab.enums.FeeAppliedType
import com.grailslab.enums.FeeIterationType
import com.grailslab.enums.FeeType
import com.grailslab.gschoolcore.BasePersistentObj

class Cafeteria extends BasePersistentObj{
    ChartOfAccount account
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
    static constraints = {
        description nullable: true
    }

}
