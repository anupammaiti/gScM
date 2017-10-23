package com.grailslab.accounting


import com.grailslab.enums.AccountType
import com.grailslab.enums.FeeAppliedType
import com.grailslab.enums.NodeType
import com.grailslab.gschoolcore.BasePersistentObj

class ChartOfAccount extends BasePersistentObj{
    String name
    Integer code
    Long parentId
    NodeType nodeType
    Boolean hasChild = false
    AccountType accountType
    FeeAppliedType appliedType
    Boolean allowEdit = true
    Boolean allowChild = true
    Boolean displayHead = false
    Boolean scholarshipHead = false
    static constraints = {
        parentId nullable: true
        appliedType nullable: true
        displayHead nullable: true
        scholarshipHead nullable: true
    }
}
