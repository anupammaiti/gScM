package com.grailslab.settings


import com.grailslab.enums.GroupName
import com.grailslab.enums.SubjectType
import com.grailslab.gschoolcore.BasePersistentObj

class ClassSubjects extends BasePersistentObj{
    SubjectName subject
    ClassName className
    GroupName groupName
    SubjectType subjectType = SubjectType.COMPULSORY

    Integer fullMark
    Integer ctMark
    Integer ctEffMark
    Integer hallMark
    Integer hallEffMark
    Integer weightOnResult        //in percentage.. that is 100% will be 1, 90% will be .9
    Integer passMark = 0       //in percentage.. that is 100% will be 1, 90% will be .9
    Integer sortOrder       //in percentage.. that is 100% will be 1, 90% will be .9

    Boolean isCtExam = Boolean.TRUE
    Boolean isHallExam = Boolean.TRUE
    Boolean isExtracurricular = Boolean.FALSE   //No pass/ fail
    Boolean isOtherActivity = Boolean.FALSE   //only mark entry for final term

    //new adjustments
    Boolean isHallPractical = Boolean.FALSE
    Integer hallWrittenMark
    Integer hallPracticalMark
    Integer hallObjectiveMark
    Integer hallSbaMark
    Integer hallInput5

    //new adjustments
    Boolean isPassSeparately
    Integer writtenPassMark
    Integer practicalPassMark
    Integer objectivePassMark
    Integer sbaPassMark
    Integer input5PassMark

//    Boolean hasAlternative = Boolean.FALSE
    String alternativeSubIds
    Long parentSubId     //in case of alternative, parent class subject id

    static constraints = {
        fullMark nullable: true
        groupName nullable: true
        isOtherActivity nullable: true
        isHallPractical nullable: true
        hallWrittenMark nullable: true
        hallPracticalMark nullable: true
        hallPracticalMark nullable: true
        hallObjectiveMark nullable: true
        hallSbaMark nullable: true
        subjectType nullable: true
        alternativeSubIds nullable: true
        parentSubId nullable: true
        passMark nullable: true
        sortOrder nullable: true
        hallInput5 nullable: true
        isPassSeparately nullable: true
        writtenPassMark nullable: true
        practicalPassMark nullable: true
        objectivePassMark nullable: true
        sbaPassMark nullable: true
        input5PassMark nullable: true
    }
}
