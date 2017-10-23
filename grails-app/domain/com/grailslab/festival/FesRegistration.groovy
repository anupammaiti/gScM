package com.grailslab.festival
import com.grailslab.gschoolcore.BasePersistentObj
import com.grailslab.settings.ClassName

class FesRegistration extends BasePersistentObj {
    FesProgram fesProgram
    String serialNo

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
        spProjectName nullable: true
        spStudentName nullable: true
        spClassName nullable: true
        spRollNo nullable: true
        spContactNo nullable: true

        slideShow nullable: true
        ssProjectName nullable: true
        ssStudentName nullable: true
        ssClassName nullable: true
        ssRollNo nullable: true
        ssContactNo nullable: true


    }
}
