package com.grailslab.command

import com.grailslab.enums.ExamTerm
import com.grailslab.enums.ExamType
import com.grailslab.enums.GroupName
import com.grailslab.enums.OpenContentType
import com.grailslab.enums.Shift
import com.grailslab.open.OpenGallery
import com.grailslab.settings.ClassName

/**
 * Created by Hasnat on 6/27/2015.
 */
@grails.validation.Validateable
class OpenGalleryCommand {
    Long id
    String name
    String description
    Integer sortOrder
    OpenContentType galleryType

    static constraints = {
        id nullable: true
        description nullable: true
    }
}

@grails.validation.Validateable
class GalleryItemCommand {
    Long id
    String itemPath
    OpenContentType galleryType
    OpenGallery openGallery
    String name
    String description
    Integer sortOrder=1


    static constraints = {
        id nullable: true
        description nullable: true
        itemPath nullable: true
    }
}
