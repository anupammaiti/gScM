package com.grailslab.settings

import com.grailslab.gschoolcore.BasePersistentObj


class NoticeBoard extends BasePersistentObj{

    String title
    String details
    String scrollText
    String scrollColor
    String iconClass
    Date publish
    Date expire
    Boolean keepBoard
    Boolean keepScroll
    static constraints = {
        scrollText nullable: true, maxSize: 2000
        title nullable: true, maxSize: 2000
        details nullable: true, maxSize: 2000
        iconClass nullable: true
        scrollColor nullable: true
    }
    static mapping = {
        keepBoard defaultValue: Boolean.FALSE
        keepScroll defaultValue: Boolean.FALSE
    }
}
