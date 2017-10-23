package com.grailslab.salary

import com.grailslab.enums.SalaryStatus
import com.grailslab.enums.YearMonths
import com.grailslab.gschoolcore.BasePersistentObj

class SalMaster extends BasePersistentObj {
    static hasMany = [salarySheet: SalarySheet]
    YearMonths yearMonths
    SalaryStatus salaryStatus
    String footNote

    static constraints = {
        footNote nullable: true
    }
    static mapping = {
        footNote type: 'text'
    }
}
