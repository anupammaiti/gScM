package com.grailslab.salary

import com.grailslab.enums.SalaryStatus
import com.grailslab.gschoolcore.BasePersistentObj

class BonusMaster extends BasePersistentObj{
    static hasMany = [bonusSheet: BonusSheet]
    SalaryStatus salaryStatus
    String festivalName
    Date joinBefore
    String basedOn
    Double bonusPercentage

    static constraints = {
    }
}
