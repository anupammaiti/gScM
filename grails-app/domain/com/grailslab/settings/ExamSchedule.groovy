package com.grailslab.settings

import com.grailslab.gschoolcore.BasePersistentObj


class ExamSchedule extends BasePersistentObj{
    SubjectName subject
    ClassRoom examRoom
    Exam exam
    Double highestMark =0
    Double averageMark =0

    Date ctExamDate
    ExamPeriod ctPeriod
    Boolean isCtMarkInput=Boolean.FALSE

    Date hallExamDate
    ExamPeriod hallPeriod
    Boolean isHallMarkInput=Boolean.FALSE

    Long parentSubId

    Boolean isExtracurricular=Boolean.FALSE
    Boolean isCompulsory
    Double fullMark

    Integer tabulationEffPercentage //weightOnResult
    Integer passMark
    Integer sortOrder


    Double ctExamMark               //ctMark
    Integer ctMarkEffPercentage     //ctEffMark

    Double hallExamMark             //hallMark
    Integer hallMarkEffPercentage   //hallEffMark

    Boolean isHallPractical=Boolean.FALSE
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

    static constraints = {
        ctExamDate(nullable: true)
        ctPeriod(nullable: true)
        ctExamMark(nullable: true)
        hallExamDate(nullable: true)
        hallPeriod(nullable: true)
        hallExamMark(nullable: true)
        fullMark(nullable: true)
        examRoom(nullable: true)
        ctMarkEffPercentage(nullable: true)
        hallMarkEffPercentage(nullable: true)
        tabulationEffPercentage(nullable: true)
        isCompulsory(nullable: true)
        isHallPractical(nullable: true)
        averageMark(nullable: true)

        //newly added
        sortOrder nullable: true
        passMark nullable: true
        parentSubId nullable: true
        isHallPractical nullable: true
        hallWrittenMark nullable: true
        hallPracticalMark nullable: true
        hallObjectiveMark nullable: true
        hallSbaMark nullable: true
        hallInput5 nullable: true

        isPassSeparately nullable: true
        writtenPassMark nullable: true
        practicalPassMark nullable: true
        objectivePassMark nullable: true
        sbaPassMark nullable: true
        input5PassMark nullable: true
    }


}
