package com.grailslab.command

import com.grailslab.settings.ClassName
import com.grailslab.settings.Section

/**
 * Created by Aminul on 2/24/2016.
 */
@grails.validation.Validateable
class AttnRecordDayCommand {
    Long id
    Date startDate
    Date endDate
    ClassName className
    Section sectionName
    Date attendDate
    static constraints = {
        id nullable: true
        startDate nullable: true
        endDate nullable: true
        className nullable: true
        sectionName nullable: true
        attendDate nullable: true
    }
}
