package com.grailslab.settings


import com.grailslab.enums.ExamStatus
import com.grailslab.enums.ExamTerm
import com.grailslab.enums.ScheduleStatus
import com.grailslab.gschoolcore.BasePersistentObj

class Exam extends BasePersistentObj {
    ShiftExam shiftExam
    ExamTerm examTerm       //duplicate for usability
    Section section
    ClassName className
    String name
    String subjectIds
    String ctSubjectIds
    Integer numberOfSubject =0
    Double totalExamMark = 0
    ScheduleStatus ctSchedule = ScheduleStatus.NO_SCHEDULE
    ScheduleStatus hallSchedule = ScheduleStatus.NO_SCHEDULE
    ExamStatus examStatus = ExamStatus.NEW
    ExamStatus ctExamStatus
    Date ctSchedulePublishedDate
    Date hallSchedulePublishedDate
    Date tabulationPreparedDate
    Integer studentCount = 0
    Integer attended = 0
    Integer failOn1Subject = 0
    Integer failOn2Subject = 0
    Integer failOnMoreSubject = 0
    Integer scoreAPlus = 0
    Integer scoreA = 0
    Integer scoreAMinus = 0
    Integer scoreB = 0
    Integer scoreC = 0
    Integer scoreF = 0

    static constraints = {
        subjectIds(nullable: true)
        ctSubjectIds(nullable: true)
        ctSchedulePublishedDate(nullable: true)
        hallSchedulePublishedDate(nullable: true)
        tabulationPreparedDate(nullable: true)
        scoreF nullable: true
        ctExamStatus nullable: true
    }

    String toString() {
        return this.name
    }

}
