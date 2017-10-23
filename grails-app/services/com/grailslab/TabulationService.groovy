package com.grailslab

import com.grailslab.enums.AttendStatus
import com.grailslab.enums.ExamTerm
import com.grailslab.enums.GroupName
import com.grailslab.enums.LetterGrade
import com.grailslab.enums.ResultStatus
import com.grailslab.enums.SubjectType
import com.grailslab.gschoolcore.AcademicYear
import com.grailslab.settings.ClassName
import com.grailslab.settings.ClassSubjects
import com.grailslab.settings.Exam
import com.grailslab.settings.Section
import com.grailslab.settings.ShiftExam
import com.grailslab.stmgmt.*
import grails.transaction.Transactional
import org.springframework.web.servlet.i18n.SessionLocaleResolver

class TabulationService {
    static transactional = false
    def messageSource
    def schoolService
    def gradePointService
    def examMarkService
    def examService
    def previousTermService
    def studentService
    SessionLocaleResolver localeResolver

    Double highestMark(List examIds, String fieldName) {
        def highestMark = Tabulation.createCriteria().get {
            createAlias("exam","eXm")
            and {
                'in'('eXm.id',examIds)
            }
            projections {
                max fieldName
            }
        } as Double
    }

    Tabulation getTabulation(Exam exam, Student student){
       return Tabulation.findByExamAndStudent(exam, student)
    }
    TabulationDetails getTabulationDetails(Long tabulationId){
        return TabulationDetails.findByTabulationId(tabulationId)
    }
    TabulationAvgMark getTabulationAvgMark(Long tabulationId){
        return TabulationAvgMark.findByTabulationId(tabulationId)
    }

    TabulationDetails entryTermDetailMark(Long tabulationId, String fieldWritten, String fieldPractical, String fieldObjective, String fieldSba, String fieldInput5, String fieldgp, ExamMark examMark, Boolean isHallPractical){
        TabulationDetails details = new TabulationDetails(tabulationId: tabulationId)
        if (isHallPractical) {
            details."${fieldWritten}"=examMark.hallWrittenMark
            details."${fieldPractical}"=examMark.hallPracticalMark
            details."${fieldObjective}"=examMark.hallObjectiveMark
            details."${fieldSba}"=examMark.hallSbaMark
            details."${fieldInput5}"=examMark.hallInput5
        } else {
            details."${fieldWritten}"=examMark.hallObtainMark
        }
        details."${fieldgp}"=examMark.gPoint
        return details
    }
    TabulationDetails entryFinalDetailMark(Long tabulationId, String fieldWritten, String fieldPractical, String fieldObjective, String fieldSba, String fieldInput5, String fieldgp, ExamMark examMark, Boolean isHallPractical){
        TabulationDetails details = new TabulationDetails(tabulationId: tabulationId)
        if (isHallPractical) {
            details."${fieldWritten}"=examMark.hallWrittenMark
            details."${fieldPractical}"=examMark.hallPracticalMark
            details."${fieldObjective}"=examMark.hallObjectiveMark
            details."${fieldSba}"=examMark.hallSbaMark
            details."${fieldInput5}"=examMark.hallInput5
        } else {
            details."${fieldWritten}"=examMark.hallObtainMark
        }
        details."${fieldgp}"=examMark.gPoint
        return details
    }


    @Transactional
    Tabulation entryTermSubjectMark(ClassName className, Section section, Exam exam, Student student, String fieldName, Double fieldMark){
        Tabulation tabulation = new Tabulation(className:className, section:section, exam:exam, examTerm:ExamTerm.FIRST_TERM, student:student, academicYear:exam.academicYear)
        tabulation."${fieldName}"=fieldMark.round(2)
        tabulation.save()
    }
    @Transactional
    Tabulation entryFinalSubjectMark(ClassName className, Section section, Exam exam, Student student, String fieldName, Double fieldMark){
        Tabulation tabulation = new Tabulation(className:className, section:section, exam:exam, examTerm:ExamTerm.FINAL_TEST, student:student, academicYear:exam.academicYear)
        tabulation."${fieldName}"=fieldMark.round(2)
        tabulation.save()
    }
    Tabulation finalTabulation(ClassName className, Section section, Exam exam, Student student){
        Tabulation tabulation = new Tabulation(className:className, section:section, exam:exam, examTerm:ExamTerm.FINAL_TEST, student:student, academicYear:exam.academicYear)
    }
    Tabulation termTabulation(ClassName className, Section section, Exam exam, ExamTerm examTerm, Student student){
        Tabulation tabulation = new Tabulation(className:className, section:section, exam:exam, examTerm:examTerm, student:student, academicYear:exam.academicYear)
    }


    int getNumberOfPassStudentCount(List classExamList, def studentList) {
        return Tabulation.countByExamInListAndResultStatusAndStudentInList(classExamList, ResultStatus.PASSED, studentList)
    }
    int classPassStudentCount(ShiftExam shiftExam, ClassName className, GroupName groupName) {
        def studentList = studentService.byClassName(className, shiftExam.academicYear, groupName)
        def clsExamIds = examService.classExamList(shiftExam, className, groupName)
        if (!clsExamIds) return 0
        return Tabulation.countByExamInListAndResultStatusAndStudentInList(clsExamIds, ResultStatus.PASSED, studentList)
    }

    @Transactional
    def createSectionTermTabulation(Section section, Exam exam){
        String runningSchool = schoolService.runningSchool()
        def results = examTabulationList(section, exam)

        ClassName className = section.className
        boolean subjectGroup = className.subjectGroup

        Double totalMark
        Double grandTotalMark
        List<ExamMark> studentMarkList
        Student student

        Double gPoint
        Integer numberOfSubject
        Integer failCount
        Double gPointAverage
        LetterGrade letterGrade
        Integer attendSubjectCount

        Integer attended = 0
        AttnStudentSummery attnStudentSummery
        Integer workingDay = className.workingDays?:0
        for (tabulation in results) {
            totalMark = 0
            grandTotalMark = 0

            student = tabulation.student

            gPoint = 0
            numberOfSubject =0
            failCount = 0
            gPointAverage = 0
            studentMarkList = examMarkService.getStudentResultMarks(exam, student)
            for (examMark in studentMarkList) {
                totalMark += examMark.tabulationMark

                if (examMark.isExtraCurricular) {
                    if(examMark.gPoint >0){
                        gPoint+= examMark.gPoint
                        numberOfSubject+=1
                    }
                } else if (examMark.isOptional) {
                    if(examMark.resultStatus==ResultStatus.PASSED && examMark.gPoint >2){
                        gPoint+= examMark.gPoint-2
                    }
                } else {
                    if (subjectGroup) {
                        if (examMark.idxOfSub == 0 || examMark.idxOfSub == 2){
                            //do nothing
                        } else {
                            if(examMark.resultStatus==ResultStatus.PASSED){
                                gPoint+= examMark.gPoint
                                numberOfSubject+=1
                            }else {
                                failCount += 1
                            }
                        }
                    } else {
                        if(examMark.resultStatus==ResultStatus.PASSED){
                            gPoint+= examMark.gPoint
                            numberOfSubject+=1
                        }else {
                            failCount += 1
                        }
                    }

                }
            }
            attendSubjectCount = examMarkService.getStudentExamAttendCount(exam, student)
            if (attendSubjectCount > 0){
                attended++
            }

            if(failCount > 0){
                tabulation.resultStatus = ResultStatus.FAILED
                tabulation.gPoint = 0
                tabulation.lGrade = LetterGrade.GRADE_F.value
                tabulation.failedSubCounter = failCount
            }else {
                gPointAverage = gPoint / numberOfSubject
                if(gPointAverage > 5){
                    gPointAverage = 5
                }
                letterGrade = gradePointService.getByPoint(gPointAverage)
                tabulation.lGrade = letterGrade.value
                tabulation.gPoint = gPointAverage.round(2)
                tabulation.resultStatus = ResultStatus.PASSED
                tabulation.failedSubCounter = failCount
            }
            if (runningSchool == CommonUtils.NARAYANGANJ_ADARSHA_SCHOOL){
                if (workingDay > 0){
                    attnStudentSummery = AttnStudentSummery.findByStudentAndAcademicYear(student, exam.academicYear)
                    if(attnStudentSummery){
                        tabulation.attendanceDay = attnStudentSummery.term1attenDay
                    }
                }
            } else if (runningSchool == CommonUtils.NARAYANGANJ_IDEAL_SCHOOL) {
                attnStudentSummery = AttnStudentSummery.findByStudentAndAcademicYear(student, exam.academicYear)
                if(attnStudentSummery){
                    tabulation.attendanceDay = attnStudentSummery.term1attenDay
                }
            }
            grandTotalMark = totalMark
            tabulation.totalObtainMark = totalMark.round(2)
            tabulation.grandTotalMark = grandTotalMark.round(2)
            tabulation.save()
        }
        //variables used in exam object for statics
        exam.attended = attended
        return true
    }

    @Transactional
    def createSectionFinalTabulation(Section section, Exam exam){
        String runningSchool = schoolService.runningSchool()
        def previousTerms
        Double ctTotalMark    // for Adarsha school only
        def results = examTabulationList(section, exam)

        ClassName className = section.className
        boolean subjectGroup = className.subjectGroup
        Integer workingDay = className.workingDays?:0
        Integer attendDay
        Double attendPercentage
        Double term1Mark
        Double term2Mark
        Double attendanceMark
        AttnStudentSummery attnStudentSummery
        TabulationAvgMark tabulationAvgMark

        Double totalMark
        Double grandTotalMark
        List<ExamMark> studentMarkList
        Student student

        Double gPoint
        Integer numberOfSubject
        Integer failCount
        Double gPointAverage
        LetterGrade letterGrade
        Integer attendSubjectCount

        Integer attended = 0
        for (tabulation in results) {
            attendPercentage = 0
            attendanceMark = 0
            totalMark = 0
            grandTotalMark = 0

            student = tabulation.student

            gPoint = 0
            numberOfSubject =0
            failCount = 0
            gPointAverage = 0

            studentMarkList = examMarkService.getStudentResultMarks(exam, student)

            // calculate previous term mark and attendance mark
            if (runningSchool == CommonUtils.BAILY_SCHOOL){
                for (examMark in studentMarkList) {
                    totalMark += examMark.tabulationMark

                    if (examMark.isExtraCurricular) {
                        if(examMark.gPoint >0){
                            gPoint+= examMark.gPoint
                            numberOfSubject+=1
                        }
                    } else if (examMark.isOptional) {
                        if(examMark.resultStatus==ResultStatus.PASSED && examMark.gPoint >2){
                            gPoint+= examMark.gPoint-2
                        }
                    } else {
                        if (subjectGroup) {
                            if (examMark.idxOfSub == 0 || examMark.idxOfSub == 2){
                                //do nothing
                            } else {
                                if(examMark.resultStatus==ResultStatus.PASSED){
                                    gPoint+= examMark.gPoint
                                    numberOfSubject+=1
                                }else {
                                    failCount += 1
                                }
                            }
                        } else {
                            if(examMark.resultStatus==ResultStatus.PASSED){
                                gPoint+= examMark.gPoint
                                numberOfSubject+=1
                            } else {
                                failCount += 1
                            }
                        }
                    }
                }

                term1Mark = 0
                term2Mark = 0
                attendanceMark = 0
                previousTerms = Tabulation.findAllBySectionAndStudentAndExamTerm(section, student, ExamTerm.FIRST_TERM, [sort: "id", order: "asc"])
                if (previousTerms) {
                    term1Mark = previousTerms[0].totalObtainMark * 0.05
                    if (previousTerms.size() >1) {
                        term2Mark = previousTerms[1].totalObtainMark * 0.05
                    }
                }
                if (workingDay > 0){
                    attnStudentSummery = AttnStudentSummery.findByStudent(student)
                    if(attnStudentSummery){
                        attendDay =  attnDay(attnStudentSummery.term1attenDay) + attnDay(attnStudentSummery.term2attenDay) + attnDay(attnStudentSummery.attendDay)
                        if(attendDay && attendDay>0){
                            attendPercentage = Math.round(attendDay * 100 /workingDay)
                            if(attendPercentage && attendPercentage >=80){
                                attendanceMark = 0.05 * totalMark
                            }
                        }

                    }
                }

                tabulation.term1Mark = term1Mark
                tabulation.term2Mark = term2Mark
                tabulation.attendanceDay = attendDay?:0
                tabulation.attendancePercent = attendPercentage?:0
                tabulation.attendanceMark = attendanceMark?:0
                grandTotalMark = totalMark + term1Mark + term2Mark + attendanceMark
                tabulation.totalObtainMark = totalMark.round(2)
                tabulation.grandTotalMark = grandTotalMark.round(2)
            } else if (runningSchool == CommonUtils.NARAYANGANJ_ADARSHA_SCHOOL){
                for (examMark in studentMarkList) {
                    totalMark += examMark.tabulationMark

                    if (examMark.isExtraCurricular) {
                        if(examMark.gPoint >0){
                            gPoint+= examMark.gPoint
                        }
                    } else {
                        if (subjectGroup) {
                            if (examMark.idxOfSub == 0 || examMark.idxOfSub == 2){
                                //do nothing
                            } else {
                                if(examMark.resultStatus==ResultStatus.PASSED){
                                    gPoint+= examMark.gPoint
                                    numberOfSubject+=1
                                }else {
                                    failCount += 1
                                }
                            }
                        } else {
                            if(examMark.resultStatus==ResultStatus.PASSED){
                                gPoint+= examMark.gPoint
                                numberOfSubject+=1
                            } else {
                                failCount += 1
                            }
                        }
                    }
                }

                term1Mark = 0
                term2Mark = 0
                attendanceMark = 0
                previousTerms = Tabulation.findAllBySectionAndStudentAndExamTerm(section, student, ExamTerm.FIRST_TERM, [sort: "id", order: "asc"])
                if (previousTerms) {
                    term1Mark = previousTerms[0].totalObtainMark
                    if (previousTerms.size() >1) {
                        term2Mark = previousTerms[1].totalObtainMark
                    }
                }
                ctTotalMark = previousTermService.totalCtMark(student)
                if(ctTotalMark && ctTotalMark > 0){
                    ctTotalMark = 0.05 * ctTotalMark
                } else {
                    ctTotalMark = 0
                }

                if (workingDay > 0){
                    attnStudentSummery = AttnStudentSummery.findByStudent(student)
                    if(attnStudentSummery){
                        attendDay =  attnDay(attnStudentSummery.term1attenDay) + attnDay(attnStudentSummery.term2attenDay) + attnDay(attnStudentSummery.attendDay)
                        if(attendDay && attendDay>0){
                            attendPercentage = Math.round(attendDay * 100 /workingDay)
                            if(attendPercentage && attendPercentage >=85){
                                attendanceMark = 0.05 * totalMark
                            }
                        }

                    }
                }

                tabulation.term1Mark = term1Mark
                tabulation.term2Mark = term2Mark
                tabulation.attendanceDay = attendDay?:0
                tabulation.attendancePercent = attendPercentage?:0
                tabulation.attendanceMark = attendanceMark?:0
                tabulation.totalCtMark = ctTotalMark
                grandTotalMark = totalMark + term1Mark + term2Mark + attendanceMark + ctTotalMark
                tabulation.totalObtainMark = totalMark.round(2)
                tabulation.grandTotalMark = grandTotalMark.round(2)
            } else if (runningSchool == CommonUtils.NARAYANGANJ_IDEAL_SCHOOL) {
                for (examMark in studentMarkList) {
                    totalMark += examMark.tabulationMark

                    if (examMark.isExtraCurricular) {
                        if(examMark.gPoint >0){
                            gPoint+= examMark.gPoint
                            numberOfSubject+=1
                        }
                    } else if (examMark.isOptional) {
                        if(examMark.resultStatus==ResultStatus.PASSED && examMark.gPoint >2){
                            gPoint+= examMark.gPoint-2
                        }
                    } else {
                        if (subjectGroup) {
                            if (examMark.idxOfSub == 0 || examMark.idxOfSub == 2){
                                //do nothing
                            } else {
                                if(examMark.resultStatus==ResultStatus.PASSED){
                                    gPoint+= examMark.gPoint
                                    numberOfSubject+=1
                                }else {
                                    failCount += 1
                                }
                            }
                        } else {
                            if(examMark.resultStatus==ResultStatus.PASSED){
                                gPoint+= examMark.gPoint
                                numberOfSubject+=1
                            } else {
                                failCount += 1
                            }
                        }
                    }
                }

                term1Mark = 0
                term2Mark = 0

                previousTerms = Tabulation.findAllBySectionAndStudentAndExamTerm(section, student, ExamTerm.FIRST_TERM, [sort: "id", order: "asc"])
                if (previousTerms) {
                    term1Mark = previousTerms[0].totalObtainMark
                    if (previousTerms.size() >1) {
                        term2Mark = previousTerms[1].totalObtainMark
                    }
                }
                attendanceMark = 0
                if (workingDay > 0){
                    attnStudentSummery = AttnStudentSummery.findByStudent(student)
                    if(attnStudentSummery){
                        attendDay =  attnDay(attnStudentSummery.term1attenDay) + attnDay(attnStudentSummery.term2attenDay) + attnDay(attnStudentSummery.attendDay)
                        if(attendDay && attendDay>0){
                            attendPercentage = Math.round(attendDay * 100 /workingDay)
                            if(attendPercentage && attendPercentage >=80){
                                attendanceMark = 0.05 * totalMark
                            }
                        }
                    }
                }
                tabulationAvgMark = TabulationAvgMark.findByTabulationId(tabulation.id)
                if (tabulationAvgMark) {
                    if(subjectGroup) {
                        grandTotalMark = avgSubMark(tabulationAvgMark.subject1avg) + avgSubMark(tabulationAvgMark.subject3avg) + avgSubMark(tabulationAvgMark.subject4avg) + avgSubMark(tabulationAvgMark.subject5avg) + avgSubMark(tabulationAvgMark.subject6avg) + avgSubMark(tabulationAvgMark.subject7avg) + avgSubMark(tabulationAvgMark.subject8avg) + avgSubMark(tabulationAvgMark.subject9avg) + avgSubMark(tabulationAvgMark.subject10avg) + avgSubMark(tabulationAvgMark.subject11avg) + avgSubMark(tabulationAvgMark.subject12avg) + avgSubMark(tabulationAvgMark.subject13avg) + avgSubMark(tabulationAvgMark.subject14avg) + avgSubMark(tabulationAvgMark.subject15avg) + avgSubMark(tabulationAvgMark.subject16avg) + avgSubMark(tabulationAvgMark.subject17avg) + avgSubMark(tabulationAvgMark.subject18avg) + avgSubMark(tabulationAvgMark.subject19avg) + avgSubMark(tabulationAvgMark.subject20avg)
                    } else {
                        grandTotalMark = avgSubMark(tabulationAvgMark.subject0avg) + avgSubMark(tabulationAvgMark.subject1avg) + avgSubMark(tabulationAvgMark.subject2avg) + avgSubMark(tabulationAvgMark.subject3avg) + avgSubMark(tabulationAvgMark.subject4avg) + avgSubMark(tabulationAvgMark.subject5avg) + avgSubMark(tabulationAvgMark.subject6avg) + avgSubMark(tabulationAvgMark.subject7avg) + avgSubMark(tabulationAvgMark.subject8avg) + avgSubMark(tabulationAvgMark.subject9avg) + avgSubMark(tabulationAvgMark.subject10avg) + avgSubMark(tabulationAvgMark.subject11avg) + avgSubMark(tabulationAvgMark.subject12avg) + avgSubMark(tabulationAvgMark.subject13avg) + avgSubMark(tabulationAvgMark.subject14avg) + avgSubMark(tabulationAvgMark.subject15avg) + avgSubMark(tabulationAvgMark.subject16avg) + avgSubMark(tabulationAvgMark.subject17avg) + avgSubMark(tabulationAvgMark.subject18avg) + avgSubMark(tabulationAvgMark.subject19avg) + avgSubMark(tabulationAvgMark.subject20avg)
                    }

                } else {
                    grandTotalMark = totalMark
                }
                grandTotalMark = grandTotalMark + attendanceMark

                tabulation.term1Mark = term1Mark
                tabulation.term2Mark = term2Mark
                tabulation.attendanceDay = attendDay?:0
                tabulation.attendancePercent = attendPercentage?:0
                tabulation.attendanceMark = attendanceMark?:0

                tabulation.totalObtainMark = totalMark.round(2)
                tabulation.grandTotalMark = grandTotalMark.round(2)
            } else if (runningSchool == CommonUtils.NARAYANGANJ_HIGH_SCHOOL) {
                for (examMark in studentMarkList) {
                    totalMark += examMark.tabulationMark

                    if (examMark.isExtraCurricular) {
                        if(examMark.gPoint >0){
                            gPoint+= examMark.gPoint
//                            numberOfSubject+=1
                        }
                    } else if (examMark.isOptional) {
                        if(examMark.resultStatus==ResultStatus.PASSED && examMark.gPoint >2){
                            gPoint+= examMark.gPoint-2
                        }
                    } else {
                        if (subjectGroup) {
                            if (examMark.idxOfSub == 0 || examMark.idxOfSub == 2){
                                //do nothing
                            } else {
                                if(examMark.resultStatus==ResultStatus.PASSED){
                                    gPoint+= examMark.gPoint
                                    numberOfSubject+=1
                                }else {
                                    failCount += 1
                                }
                            }
                        } else {
                            if(examMark.resultStatus==ResultStatus.PASSED){
                                gPoint+= examMark.gPoint
                                numberOfSubject+=1
                            } else {
                                failCount += 1
                            }
                        }
                    }
                }

                term1Mark = 0
                term2Mark = 0
                attendanceMark = 0
                previousTerms = Tabulation.findAllBySectionAndStudentAndExamTerm(section, student, ExamTerm.FIRST_TERM, [sort: "id", order: "asc"])
                if (previousTerms) {
                    term1Mark = previousTerms[0].totalObtainMark
                    if (previousTerms.size() >1) {
                        term2Mark = previousTerms[1].totalObtainMark
                    }
                }

                if (workingDay > 0){
                    attnStudentSummery = AttnStudentSummery.findByStudent(student)
                    if(attnStudentSummery){
                        attendDay =  attnDay(attnStudentSummery.term1attenDay) + attnDay(attnStudentSummery.term2attenDay) + attnDay(attnStudentSummery.attendDay)
                        /*if(attendDay && attendDay>0){
                            attendPercentage = Math.round(attendDay * 100 /workingDay)
                            if(attendPercentage && attendPercentage >=80){
                                attendanceMark = 0.05 * totalMark
                            }
                        }*/
                    }
                }

                tabulation.term1Mark = term1Mark
                tabulation.term2Mark = term2Mark
                tabulation.attendanceDay = attendDay?:0
                tabulation.attendancePercent = attendPercentage?:0
                tabulation.attendanceMark = 0   //attendanceMark
                grandTotalMark = (totalMark + term1Mark)/2
                tabulation.totalObtainMark = totalMark.round(2)
                tabulation.grandTotalMark = grandTotalMark.round(2)
            }
            attendSubjectCount = examMarkService.getStudentExamAttendCount(exam, student)
            if (attendSubjectCount > 0){
                attended++
            }

            if(failCount > 0){
                tabulation.resultStatus = ResultStatus.FAILED
                tabulation.gPoint = 0
                tabulation.lGrade = LetterGrade.GRADE_F.value
                tabulation.failedSubCounter = failCount
            }else {
                gPointAverage = gPoint / numberOfSubject
                if(gPointAverage > 5){
                    gPointAverage = 5
                }
                letterGrade = gradePointService.getByPoint(gPointAverage)
                tabulation.lGrade = letterGrade.value
                tabulation.gPoint = gPointAverage.round(2)
                tabulation.resultStatus = ResultStatus.PASSED
                tabulation.failedSubCounter = failCount
            }
            tabulation.save()
        }
        //variables used in exam object for statics
        exam.attended = attended
        return true
    }
    //@todo-aminul refactor for final term tabulation
   /* @Transactional
    def createSectionTabulation(Section section,Exam exam){
        def c = Tabulation.createCriteria()
        List results = c.list() {
            and {
                eq("section", section )
                eq("exam", exam)
            }
            order("grandTotalMark",CommonUtils.SORT_ORDER_DESC)
        }
        Integer attended = results.size()
        ClassName className = section.className
        Integer workingDay = className?.workingDays?:0
        Integer attendDay
        Long attendPercentage
        Double attendanceMark
        PreviousTermMark previousTermMark

        Double subject0Mark
        Double subject1Mark
        Double subject2Mark
        Double subject3Mark
        Double subject4Mark
        Double subject5Mark
        Double subject6Mark
        Double subject7Mark
        Double subject8Mark
        Double subject9Mark
        Double subject10Mark
        Double subject11Mark
        Double subject12Mark
        Double subject13Mark
        Double subject14Mark
        Double subject15Mark
        Double subject16Mark
        Double subject17Mark
        Double subject18Mark
        Double subject19Mark
        Double subject20Mark
        Double subject21Mark
        Double subject22Mark
        Double subject23Mark
        Double subject24Mark
        Double subject25Mark
        Double subject26Mark
        Double subject27Mark
        Double subject28Mark
        Double subject29Mark
        Double subject30Mark

        Double term1Mark
        Double term2Mark

        Double totalMark
        Double grandTotalMark
        List<ExamMark> studentMarkList
        Student student

        Double gPoint
        Integer numberOfSubject
        Integer failCount
        Double gPointAverage
        LetterGrade letterGrade
        for (tabulation in results) {
            totalMark = 0
            grandTotalMark = 0

            term1Mark = tabulation.term1Mark?:0
            term2Mark = tabulation.term2Mark?:0

            subject0Mark =tabulation.subject0Mark?:0
            subject1Mark =tabulation.subject1Mark?:0
            subject2Mark =tabulation.subject2Mark?:0
            subject3Mark =tabulation.subject3Mark?:0
            subject4Mark =tabulation.subject4Mark?:0
            subject5Mark =tabulation.subject5Mark?:0
            subject6Mark =tabulation.subject6Mark?:0
            subject7Mark =tabulation.subject7Mark?:0
            subject8Mark =tabulation.subject8Mark?:0
            subject9Mark =tabulation.subject9Mark?:0
            subject10Mark =tabulation.subject10Mark?:0
            subject11Mark =tabulation.subject11Mark?:0
            subject12Mark =tabulation.subject12Mark?:0
            subject13Mark =tabulation.subject13Mark?:0
            subject14Mark =tabulation.subject14Mark?:0
            subject15Mark =tabulation.subject15Mark?:0
            subject16Mark =tabulation.subject16Mark?:0
            subject17Mark =tabulation.subject17Mark?:0
            subject18Mark =tabulation.subject18Mark?:0
            subject19Mark =tabulation.subject19Mark?:0
            subject20Mark =tabulation.subject20Mark?:0
            subject21Mark =tabulation.subject21Mark?:0
            subject22Mark =tabulation.subject22Mark?:0
            subject23Mark =tabulation.subject23Mark?:0
            subject24Mark =tabulation.subject24Mark?:0
            subject25Mark =tabulation.subject25Mark?:0
            subject26Mark =tabulation.subject26Mark?:0
            subject27Mark =tabulation.subject27Mark?:0
            subject28Mark =tabulation.subject28Mark?:0
            subject29Mark =tabulation.subject29Mark?:0
            subject30Mark =tabulation.subject30Mark?:0
            totalMark = subject0Mark+subject1Mark+subject2Mark+subject3Mark+subject4Mark+subject5Mark+subject6Mark+ subject7Mark + subject8Mark + subject9Mark + subject10Mark + subject11Mark + subject12Mark + subject13Mark + subject14Mark+ subject15Mark+ subject16Mark+subject17Mark+subject18Mark+subject19Mark+subject20Mark+subject21Mark+subject22Mark+subject23Mark+subject24Mark+subject25Mark+subject26Mark+subject27Mark+subject28Mark+subject29Mark+subject30Mark
            attendanceMark = 0
            attendPercentage = 0
            attendDay = 0
            student = tabulation.student
            *//*if(exam.examTerm == ExamTerm.FINAL_TEST && workingDay>0){
                previousTermMark = PreviousTermMark.findByStudent(student)
                if(previousTermMark){
                    attendDay = previousTermMark.attendDay
                    if(attendDay && attendDay>0){
                        attendPercentage = Math.round(attendDay * 100 /workingDay)
                        if(attendPercentage && attendPercentage >=80){
                            attendanceMark = 0.05 * totalMark
                        }
                    }

                }
            }*//*
            gPoint = 0
            numberOfSubject =0
            failCount = 0
            gPointAverage = 0
            studentMarkList = examMarkService.getStudentResultMarks(exam,student)
            studentMarkList.each {ExamMark examMark ->
                if(!examMark.isOptional){
                    if(examMark.resultStatus==ResultStatus.PASSED){
                        gPoint+= examMark.gPoint
                        numberOfSubject+=1
                    }else {
                        failCount += 1
                    }
                }else {
                    if(examMark.resultStatus==ResultStatus.PASSED && examMark.gPoint >2){
                        gPoint+= examMark.gPoint-2
                    }
                }
            }
            if(failCount >0){
                tabulation.resultStatus = ResultStatus.FAILED
                tabulation.gPoint = 0
                tabulation.lGrade = LetterGrade.GRADE_F.value
                tabulation.failedSubCounter = failCount
            }else {
                gPointAverage = gPoint / numberOfSubject
                if(gPointAverage > 5){
                    gPointAverage = 5
                }
                letterGrade = gradePointService.getByPoint(gPointAverage)
                tabulation.lGrade = letterGrade.value
                tabulation.gPoint = gPointAverage.round(2)
                tabulation.resultStatus = ResultStatus.PASSED
            }
            grandTotalMark = totalMark + term1Mark + term2Mark + attendanceMark
            tabulation.attendanceMark = attendanceMark.round(2)
            tabulation.attendancePercent = attendPercentage
            tabulation.totalObtainMark = totalMark.round(2)
            tabulation.grandTotalMark = grandTotalMark.round(2)
            tabulation.save()
        }

        //variables used in exam object for statics
        exam.attended = attended
        return true
    }*/
    @Transactional
    def createSectionResult(Exam exam){
        def studentList = studentService.allStudentForTabulation(exam.section)
        def c = Tabulation.createCriteria()
        def results = c.list() {
            and {
                eq("exam", exam)
                'in'("student", studentList)
            }
            order("failedSubCounter",CommonUtils.SORT_ORDER_ASC)
            order("grandTotalMark",CommonUtils.SORT_ORDER_DESC)
        }
        int position =1
        Double previousTotal = 0
        results.each {Tabulation tabulation ->
            if(tabulation.grandTotalMark==previousTotal){
                --position
                tabulation.positionInSection = position
                tabulation.sectionStrPosition = CommonUtils.ordinal(position)
                position++
            }else {
                tabulation.positionInSection = position
                tabulation.sectionStrPosition = CommonUtils.ordinal(position)
                previousTotal = tabulation.grandTotalMark
                position++
            }
            tabulation.save()
        }
        exam.failOn1Subject=Tabulation.countByExamAndStudentInListAndLGradeAndFailedSubCounter(exam, studentList, LetterGrade.GRADE_F.value,1)
        //Number of student fail in 2 subject
        exam.failOn2Subject = Tabulation.countByExamAndStudentInListAndLGradeAndFailedSubCounter(exam, studentList, LetterGrade.GRADE_F.value,2)

        //Number of student fail in more then 2 subject
        exam.failOnMoreSubject = Tabulation.countByExamAndStudentInListAndLGradeAndFailedSubCounterGreaterThan(exam, studentList, LetterGrade.GRADE_F.value,2)

        //Number of student fail in more then 2 subject
        exam.scoreAPlus = Tabulation.countByExamAndStudentInListAndLGrade(exam, studentList, LetterGrade.GRADE_A_PLUS.value)

        //Number of student fail in more then 2 subject
        exam.scoreA = Tabulation.countByExamAndStudentInListAndLGrade(exam, studentList, LetterGrade.GRADE_A.value)

        //Number of student fail in more then 2 subject

        exam.scoreAMinus = Tabulation.countByExamAndStudentInListAndLGrade(exam, studentList, LetterGrade.GRADE_A_MINUS.value)

        //Number of student fail in more then 2 subject

        exam.scoreB = Tabulation.countByExamAndStudentInListAndLGrade(exam, studentList, LetterGrade.GRADE_B.value)

        //Number of student fail in more then 2 subject
        exam.scoreC = Tabulation.countByExamAndStudentInListAndLGrade(exam, studentList, LetterGrade.GRADE_C.value)
        exam.scoreF = Tabulation.countByExamAndStudentInListAndLGrade(exam, studentList, LetterGrade.GRADE_F.value)
        return true
    }
    @Transactional
    def createClassResult(ClassName className, List examIds, AcademicYear academicYear){
        def studentList = studentService.byClassName(className, academicYear)
        def c = Tabulation.createCriteria()
        def results = c.list() {
            createAlias("exam","eXm")
            and {
                eq("className", className)
                'in'('eXm.id',examIds)
                'in'('student',studentList)
            }
            order("failedSubCounter",CommonUtils.SORT_ORDER_ASC)
            order("grandTotalMark",CommonUtils.SORT_ORDER_DESC)
        }
        int position =1
        Double previousTotal = 0
        results.each {Tabulation tabulation ->
            if(tabulation.grandTotalMark==previousTotal){
                --position
                tabulation.positionInClass = position
                tabulation.classStrPosition = CommonUtils.ordinal(position)
                position++
            }else {
                tabulation.positionInClass = position
                tabulation.classStrPosition = CommonUtils.ordinal(position)
                previousTotal = tabulation.grandTotalMark
                position++
            }
            tabulation.save()
        }
        return true
    }



    def listStudentBasedClsPosition(ClassName className, List examIds, GroupName groupName, int offset, int limit){
        def c = Tabulation.createCriteria()
        def results = c.list(max: limit, offset: offset) {
            createAlias("exam","eXm")
            createAlias("section","sec")
            and {
                eq("className", className)
                if (groupName) {
                    eq("sec.groupName", groupName)
                }
                'in'('eXm.id',examIds)
            }
            order("positionInClass",CommonUtils.SORT_ORDER_ASC)
            projections {
                property('student')
            }
        }
        return results
    }



    def tabulationListForSms(List<Long> examIds){
        def c = Tabulation.createCriteria()
        def results = c.list() {
            createAlias('exam', 'xm')
            createAlias('section', 'sec')
            createAlias('student', 'std')
            and {
                'in'("xm.id", examIds)
            }
            order("sec.id",CommonUtils.SORT_ORDER_DESC)
            order("std.rollNo",CommonUtils.SORT_ORDER_DESC)
        }
        List dataReturns = new ArrayList()
        Registration registration
        Student student1
        String mobileNo
        boolean validNumber
        String sectionStr
        String studentStr
        String msgStr
        int numOfMsg
        int msgLength
        String msgStatus
        String senderBrand = messageSource.getMessage('app.message.brand.name', null, localeResolver.defaultLocale)
        results.each { Tabulation tabulation ->
            validNumber = false
            student1 = tabulation.student
            registration = student1?.registration
            mobileNo = registration.mobile
            if(mobileNo){
                if(mobileNo.startsWith('01') && mobileNo.length()==11){
                    validNumber=true
                }else if(mobileNo.startsWith('88') && mobileNo.length()==13){
                    validNumber=true
                }else {
                    validNumber=false
                }
            }
            if(validNumber){
                sectionStr="${tabulation.className?.name} - ${tabulation.section?.name}"
                studentStr="${student1.studentID} - ${student1.name} - ${student1.rollNo}"
                msgStr=buildMsgStr(tabulation.exam.name, student1.name,tabulation.lGrade, tabulation.gPoint, tabulation.grandTotalMark, tabulation.sectionStrPosition, tabulation.classStrPosition, tabulation.resultStatus.value, senderBrand)
                msgLength=msgStr.length()
                if(msgLength>0 && msgLength<160){
                    numOfMsg=1
                }else if(msgLength>=160 && msgLength<320){
                    numOfMsg=2
                }else {
                    numOfMsg=3
                }
                msgStatus=tabulation.sentMessage?"Yes":"No"
                dataReturns.add([id: tabulation.id,sectionStr:sectionStr,studentStr:studentStr,mobileNo:mobileNo,
                                 msgStr:msgStr,numOfMsg:numOfMsg,msgStatus:msgStatus])
            }

        }
        return dataReturns
    }
    def prepareResultSms(List<Long> tabulationIds){
        def c = Tabulation.createCriteria()
        def results = c.list() {
            and {
                'in'("id", tabulationIds)
            }
        }
        List dataReturns = new ArrayList()
        Registration registration
        Student student1
        String mobileNo
        String msgStr
        String senderBrand = messageSource.getMessage('app.message.brand.name', null, localeResolver.defaultLocale)
        results.each { Tabulation tabulation ->
            student1 = tabulation.student
            registration = student1?.registration
            mobileNo = registration.mobile
                msgStr=buildMsgStr(tabulation.exam?.name,student1.name,tabulation.lGrade, tabulation.gPoint, tabulation.grandTotalMark, tabulation.sectionStrPosition, tabulation.classStrPosition, tabulation.resultStatus.value, senderBrand)
                dataReturns.add([tabulationObj: tabulation,mobileNo:mobileNo,msgStr:msgStr])
        }
        return dataReturns
    }
    String buildMsgStr(String examName, String strName,String lGrade, Double gPoint, Double totalMark, String secPo, String clsPo, String resultStatus, String senderBrand){
       return "Exam: ${examName}, Name: ${strName}, Result:${lGrade} (${gPoint}), Total Mark: ${totalMark}, Sec Pos:${secPo}, Class Pos:${clsPo}, Status:${resultStatus}. ${senderBrand}"
    }

    String subjectComment(ExamMark examMark, ClassSubjects classSubjects) {
        if (examMark.hallAttendStatus != AttendStatus.PRESENT) {
            return '0 (Abs)'
        }
        def mark = examMark.tabulationMark.round(1)
        if (classSubjects.subjectType == SubjectType.INUSE && examMark.resultStatus == ResultStatus.FAILED) {
            return  "${mark} (${classSubjects.subject.name.charAt(0)})(F)"
        } else if (classSubjects.subjectType == SubjectType.INUSE) {
            return  "${mark} (${classSubjects.subject.name.charAt(0)})"
        } else if (examMark.resultStatus == ResultStatus.FAILED) {
            return  "${mark} (F)"
        }
        return null
    }
    Integer attnDay (Integer days) {
        if (!days) return 0
        return days
    }
    Double avgSubMark(Double subjectMark) {
        if (!subjectMark) return 0
        return subjectMark
    }
    def examTabulationList(Section section, Exam exam) {
        def studentList = studentService.allStudentForTabulation(section)
        return Tabulation.findAllByExamAndStudentInList(exam, studentList)

    }

    //result analytics
    int hallStudentCount(List publishingExamList, List studentList, ResultStatus resultStatus, String lGrade) {
        if (!publishingExamList || ! studentList) return 0

        int studentCount = 0
        if (!resultStatus && !lGrade){
            studentCount = Tabulation.countByExamInListAndStudentInList(publishingExamList, studentList)
        } else if (lGrade) {
            studentCount = Tabulation.countByExamInListAndStudentInListAndResultStatusAndLGrade(publishingExamList, studentList, resultStatus, lGrade)
        } else {
            studentCount = Tabulation.countByExamInListAndStudentInListAndResultStatus(publishingExamList, studentList, resultStatus)
        }
        return studentCount
    }
}
