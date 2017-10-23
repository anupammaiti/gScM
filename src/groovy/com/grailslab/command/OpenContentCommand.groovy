package com.grailslab.command

import com.grailslab.enums.OpenContentType

/**
 * Created by Hasnat on 10/11/2015.
 */
@grails.validation.Validateable
class OpenContentCommand {
    Long id
    OpenContentType openContentType
    String title
    String iconClass
    String description
    Integer sortOrder

    static constraints = {
        id nullable: true
        openContentType nullable: true
    }
}

@grails.validation.Validateable
class OpenTimeLineCommand {
    Long id
    String title
    String name
    String iconClass
    String description
    Integer sortOrder

    static constraints = {
        id nullable: true
    }
}

@grails.validation.Validateable
class OpenQuickLinkCommand {
    Long id
    String displayName
    String urlType
    String linkUrl
    String iconClass
    Integer sortIndex

    static constraints = {
        id nullable: true
    }
}

@grails.validation.Validateable
class InvoiceDayCommand {
    Long id
    Date invoiceDate

    static constraints = {
        id nullable: true
    }
}

