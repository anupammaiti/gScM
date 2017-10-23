package com.grailslab.command


import com.grailslab.enums.OpenContentType
import com.grailslab.settings.ClassName

/**
 * Created by Hasnat on 10/11/2015.
 */
@grails.validation.Validateable
class MgmtVoiceCommand {
    Long id
    OpenContentType openContentType
    String title
    String name
    String iconClass
    String description
    Integer sortOrder

    static constraints = {
        id nullable: true
        openContentType nullable: true
    }
}

@grails.validation.Validateable
class SuccessStoryCommand {
    Long id
    String name
    String description
    Integer sortOrder

    static constraints = {
        id nullable: true
    }
}

@grails.validation.Validateable
class SliderImageCommand {
    Long id
    Integer sortIndex
    String title
    String description

    static constraints = {
        id nullable: true
        id description: true
    }
}

@grails.validation.Validateable
class FestivalCommand {
    String name
    Date startDate
    Date endDate
    String olympiadTopics        //comma separated OlympiadType keys
    String helpContact
    Date regOpenDate
    Date regCloseDate
    Long id

    static constraints = {
        id nullable: true
    }

}

@grails.validation.Validateable
class FesRegistrationCommand {
    String name
    String instituteName
    ClassName className
    Integer rollNo
    String contactNo
    String olympiadTopics

    //2nd student information for scientific paper/project
    Boolean scientificPaper
    String spProjectName
    String spStudentName
    ClassName spClassName
    Integer spRollNo
    String spContactNo

    //2nd student information for slide show presentation
    Boolean slideShow
    String ssProjectName
    String ssStudentName
    ClassName ssClassName
    Integer ssRollNo
    String ssContactNo

    static constraints = {
        scientificPaper nullable: true
        slideShow nullable: true
        spProjectName nullable: true
        spStudentName nullable: true
        spClassName nullable: true
        spRollNo nullable: true
        spContactNo nullable: true

        ssProjectName nullable: true
        ssStudentName nullable: true
        ssClassName nullable: true
        ssRollNo nullable: true
        ssContactNo nullable: true
    }

}