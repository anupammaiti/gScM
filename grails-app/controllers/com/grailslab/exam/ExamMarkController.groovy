package com.grailslab.exam

import com.grailslab.CommonUtils
import com.grailslab.enums.*
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.settings.*
import com.grailslab.stmgmt.*
import grails.converters.JSON
import org.apache.commons.lang3.ArrayUtils

class ExamMarkController {
    def schoolService
    def examMarkService
    def messageSource
    def studentSubjectsService
    def studentService
    def tabulationService
    def tabulationCtService
    def examScheduleService
    def examService
    def springSecurityService
    def classSubjectsService
    def classNameService
    def shiftExamService

    def index() {
    }

    def markCtComplete(Long id){
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        ExamSchedule examSchedule = ExamSchedule.get(id)
        if(!examSchedule){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        Exam exam = examSchedule.exam
        if(exam.examStatus == ExamStatus.PUBLISHED){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, 'Result Already Published')
            outPut = result as JSON
            render outPut
            return
        }
        if(examSchedule.isHallMarkInput){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, 'Mark Already added to Tabulation')
            outPut = result as JSON
            render outPut
            return
        }
        if(examSchedule.isCtMarkInput){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, 'Already mark completed')
            outPut = result as JSON
            render outPut
            return
        }

        ClassName className = exam.className
        Section section = exam.section
        SubjectName subject = examSchedule.subject

        ClassSubjects classSubjects = ClassSubjects.findByClassNameAndSubjectAndActiveStatus(className, subject, ActiveStatus.ACTIVE)
        if(!classSubjects){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "Subject not added properly.")
            outPut = result as JSON
            render outPut
            return
        }
        def studentList
        if (classSubjects.subjectType != SubjectType.COMPULSORY) {
            int numOfSubjectStudent = studentSubjectsService.subjectStudentCount(section, subject)
            if (numOfSubjectStudent == 0) {
                examSchedule.isCtMarkInput = true
                examSchedule.save()
                result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
                result.put(CommonUtils.MESSAGE, 'Successfully mark completed')
                outPut = result as JSON
                render outPut
                return
            }
        }

        def markEntryStdIds = examMarkService.markEntryStdIds(examSchedule, ExamType.CLASS_TEST)
        if (!markEntryStdIds) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, 'No mark input')
            outPut = result as JSON
            render outPut
            return
        }
        def examSubjectIds = exam.ctSubjectIds.split(',')
        int idxOfSub = -1

        ClassSubjects psubject
        if (classSubjects.subjectType == SubjectType.COMPULSORY) {
            idxOfSub = ArrayUtils.indexOf(examSubjectIds, subject.id.toString())
            studentList = studentService.allStudentNotInList(section,markEntryStdIds)
        } else if (classSubjects.subjectType == SubjectType.OPTIONAL) {
            idxOfSub = ArrayUtils.indexOf(examSubjectIds, subject.id.toString())
            studentList = studentSubjectsService.markEntryStudentsExcludingList(section, subject, markEntryStdIds)
        } else if (classSubjects.subjectType == SubjectType.INUSE) {
            psubject = ClassSubjects.read(classSubjects.parentSubId)
            idxOfSub = ArrayUtils.indexOf(examSubjectIds, psubject?.subject.id.toString())
            studentList = studentSubjectsService.markEntryStudentsExcludingList(section, subject, markEntryStdIds)
        }

        if(idxOfSub == -1){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, 'Subject not correctly listed in Class Subjects.')
            outPut = result as JSON
            render outPut
            return
        }

        if (studentList.size() > 0) {
            def notCompleteStd = studentList.first()
            def notCompleSize = studentList.size()
            String message = "Please complete all mark entry first"
            if(notCompleteStd){
                if(notCompleSize>1){
                    message = "${notCompleteStd?.name} and more ${notCompleSize -1} student mark entry yet to complete"
                }else {
                    message = "${notCompleteStd?.name} mark entry yet to complete"
                }
            }
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, message)
            outPut = result as JSON
            render outPut
            return
        }
        if (classSubjects.subjectType == SubjectType.ALTERNATIVE) {
            examSchedule.ctMarkEffPercentage= psubject.ctEffMark
        } else {
            examSchedule.ctMarkEffPercentage= classSubjects.ctEffMark
        }

        def allExamMarks = examMarkService.getAllCtExamMark(examSchedule,subject)
        Student student
        String fieldName = "subject${idxOfSub}Mark"
        TabulationCt tabulationCt
        for (examMark in allExamMarks) {
            student = examMark.student
            examMarkService.calculateCtSubjectMark(psubject?:classSubjects, student, examMark, className, subject)
            tabulationCt = tabulationCtService.getTabulation(exam, student)
            if(tabulationCt){
                tabulationCt."${fieldName}"= examMark.ctObtainMark.round(2)
                tabulationCt.save()
            }else {
                tabulationCt = tabulationCtService.entrySubjectMark(className, section, exam, student, fieldName, examMark.ctObtainMark.round(2))
            }
            examMark.save()
        }
        examSchedule.isCtMarkInput = true
        examSchedule.save()
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, 'Successfully mark completed')
        outPut = result as JSON
        render outPut
        return
    }

    def markEntry(Long id) {
        Exam exam=Exam.read(id)
        def examNameList = shiftExamService.examNameDropDown()
        if(!exam){
            render(view: '/exam/examMark/markEntry', model: [dataReturn: null, examNameList: examNameList])
            return
        }
        ShiftExam shiftExam = exam.shiftExam
        LinkedHashMap resultMap = examScheduleService.examScheduleList(exam)
        def classNameList = classNameService.classNameInIdListDD(shiftExam.classIds);
        def sectionNameList = examService.classExamsDropDownAsSectionName(shiftExam, exam.className, null, 'working');
        render(view: '/exam/examMark/markEntry', model: [dataReturn: resultMap.results, exam:exam, classNameList: classNameList, examNameList: examNameList, shiftExam:shiftExam, sectionNameList:sectionNameList])
    }




    def specialMarkEntry(Long id){
        if (!request.method.equals('POST')) {
            redirect(controller: 'exam', action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut

        ExamSchedule examSchedule = ExamSchedule.get(id)
        if(!examSchedule){
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }

        Exam exam = examSchedule.exam
        SubjectName subject = examSchedule.subject

        ClassSubjects classSubjects = ClassSubjects.findByClassNameAndSubjectAndActiveStatus(exam.className, subject, ActiveStatus.ACTIVE)
        if (classSubjects.subjectType == SubjectType.INUSE) {
            classSubjects = ClassSubjects.read(classSubjects.parentSubId)
        }

        if (!classSubjects || !classSubjects.isExtracurricular) {
            result.put(CommonUtils.MESSAGE, "Only Extra corricular subject's are allowed to entry for all student.")
            outPut = result as JSON
            render outPut
            return
        }

        ExamType examType = ExamType.valueOf(params.examType)
        Double markObtain = params.getDouble('markObtain')

        if (examType == ExamType.CLASS_TEST) {
            if(examSchedule.isCtMarkInput){
                result.put(CommonUtils.MESSAGE, "CT Mark entry already completed.")
                outPut = result as JSON
                render outPut
                return
            }
            if (classSubjects.ctMark < markObtain) {
                result.put(CommonUtils.MESSAGE, "Invalid mark entry. Please enter upto ${classSubjects.ctMark}")
                outPut = result as JSON
                render outPut
                return
            }
        } else {
            if(examSchedule.isHallMarkInput){
                result.put(CommonUtils.MESSAGE, "Mark entry already completed.")
                outPut = result as JSON
                render outPut
                return
            }
            if (classSubjects.hallMark < markObtain) {
                result.put(CommonUtils.MESSAGE, "Invalid mark entry. Please enter upto ${classSubjects.hallMark}")
                outPut = result as JSON
                render outPut
                return
            }
        }

        def studentList

        if(classSubjects.subjectType == SubjectType.COMPULSORY){
            studentList = studentService.allStudent(exam.section)
        } else {
            def subjectStudents = studentSubjectsService.subjectStudentsExcludingList(exam.section,subject,null)
            studentList = studentService.allStudentInList(subjectStudents)
        }
        if (!studentList || studentList.size()==0) {
            result.put(CommonUtils.MESSAGE, "No Student found for mark entry")
            outPut = result as JSON
            render outPut
            return
        }

        AttendStatus attendStatus = AttendStatus.PRESENT
        ExamMark examMark
        Student student

        if (examType == ExamType.CLASS_TEST) {
            for (stds in studentList) {
                student = Student.read(stds.id)
                examMark = ExamMark.findByExamScheduleAndStudentAndActiveStatus(examSchedule, student, ActiveStatus.ACTIVE)
                if (examMark) {
                    examMark.ctObtainMark = markObtain
                    examMark.ctAttendStatus = attendStatus
                } else {
                    examMark = new ExamMark(ctAttendStatus: attendStatus, ctObtainMark: markObtain, student:student, exam: exam, examSchedule: examSchedule, subject:subject)
                }
                examMark.save()
            }
        } else {
            for (stds in studentList) {
                student = Student.read(stds.id)
                examMark = ExamMark.findByExamScheduleAndStudentAndActiveStatus(examSchedule, student, ActiveStatus.ACTIVE)
                if (examMark) {
                    examMark.hallObtainMark = markObtain
                    examMark.hallAttendStatus = attendStatus
                } else {
                    examMark = new ExamMark(hallAttendStatus: attendStatus, hallObtainMark: markObtain, student:student, exam: exam, examSchedule: examSchedule, subject:subject)
                }
                examMark.save()
            }
        }

        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, "Exam Mark inserted successfully for all students")
        outPut = result as JSON
        render outPut
        return
    }

    def markHallComplete(Long id){
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        ExamSchedule examSchedule = ExamSchedule.get(id)
        if(!examSchedule){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, 'Exam Schedule Not found')
            outPut = result as JSON
            render outPut
            return
        }
        Exam exam = examSchedule.exam
        if(exam.examStatus==ExamStatus.PUBLISHED){
            result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
            result.put(CommonUtils.MESSAGE, 'Result Already Published')
            outPut = result as JSON
            render outPut
            return
        }
        if(examSchedule.isHallMarkInput){
            result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
            result.put(CommonUtils.MESSAGE, 'Already mark completed. No changes detect to update.')
            outPut = result as JSON
            render outPut
            return
        }
        if(examSchedule.ctExamDate && !examSchedule.isCtMarkInput){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, 'CT Mark input not completed')
            outPut = result as JSON
            render outPut
            return
        }

        ClassName className = exam.className
        Section section = exam.section
        SubjectName subject = examSchedule.subject

        ClassSubjects classSubjects = ClassSubjects.findByClassNameAndSubjectAndActiveStatus(className, subject, ActiveStatus.ACTIVE)
        boolean isExBpSubject = classSubjects.isExtracurricular
        def studentList
        if (classSubjects.subjectType != SubjectType.COMPULSORY) {
            int numOfSubjectStudent = studentSubjectsService.subjectStudentCount(section, subject)
            if (numOfSubjectStudent == 0) {
                examSchedule.highestMark = 0
                examSchedule.averageMark = 0
                examSchedule.isHallMarkInput = true
                examSchedule.isExtracurricular = isExBpSubject
                examSchedule.save()
                result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
                result.put(CommonUtils.MESSAGE, 'No student taken this subject. Successfully mark completed')
                outPut = result as JSON
                render outPut
                return
            }
        }

        def markEntryStdIds = examMarkService.markEntryStdIds(examSchedule, ExamType.HALL_TEST)
        if (!markEntryStdIds) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, 'No mark input')
            outPut = result as JSON
            render outPut
            return
        }

        def examSubjectIds = exam.subjectIds.split(',')     //classSubjectsService.hallExamSubjectHeaderIds(exam.className)
        int idxOfSub = -1


        ClassSubjects psubject
        if (classSubjects.subjectType == SubjectType.COMPULSORY) {
            idxOfSub = ArrayUtils.indexOf(examSubjectIds, subject.id.toString())
            studentList = studentService.allStudentNotInList(section, markEntryStdIds)
        } else if (classSubjects.subjectType == SubjectType.OPTIONAL) {
            idxOfSub = ArrayUtils.indexOf(examSubjectIds, subject.id.toString())
            studentList = studentSubjectsService.markEntryStudentsExcludingList(section, subject, markEntryStdIds)
        } else if (classSubjects.subjectType == SubjectType.INUSE) {
            psubject = ClassSubjects.read(classSubjects.parentSubId)
            isExBpSubject = psubject.isExtracurricular
            idxOfSub = ArrayUtils.indexOf(examSubjectIds, psubject?.subject.id.toString())
            studentList = studentSubjectsService.markEntryStudentsExcludingList(section, subject, markEntryStdIds)
        }

        if (studentList.size() > 0) {
            def notCompleteStd = studentList.first()
            def notCompleSize = studentList.size()
            String message = "Please complete all mark entry first"
            if(notCompleteStd){
                if(notCompleSize>1){
                    message = "${notCompleteStd?.name} and more ${notCompleSize -1} student mark entry yet to complete"
                }else {
                    message = "${notCompleteStd?.name} mark entry yet to complete"
                }
            }
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, message)
            outPut = result as JSON
            render outPut
            return
        }

        boolean subjectGroup = className.subjectGroup
        boolean subjectSba = className.subjectSba
        SubjectName groupSubject
        Double groupEffExmMark = 0
        if (subjectGroup && (idxOfSub == 1 || idxOfSub == 3)){
            Long groupSubId
            if (idxOfSub == 1){
                groupSubId = Long.valueOf(examSubjectIds[0])
            } else if (idxOfSub == 3) {
                groupSubId = Long.valueOf(examSubjectIds[2])
            }
            groupSubject = SubjectName.read(groupSubId)
            if (!ExamSchedule.findByActiveStatusAndExamAndSubjectAndIsHallMarkInput(ActiveStatus.ACTIVE, exam, groupSubject, true)) {
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, "${groupSubject.name} mark entry yet not mark as completed. Please complete it first.")
                outPut = result as JSON
                render outPut
                return
            }
            ClassSubjects groupClsSubject = ClassSubjects.findByActiveStatusAndClassNameAndSubject(ActiveStatus.ACTIVE, className, groupSubject)
            Double gCtExamEffMark = 0
            if (groupClsSubject.isCtExam) {
                Double ctExamMark = groupClsSubject.ctMark?:0
                Double ctEffMark = groupClsSubject.ctEffMark?:100
                if(ctEffMark ==100){
                    gCtExamEffMark = ctExamMark
                }else {
                    gCtExamEffMark = ctEffMark * ctExamMark * 0.01
                }
            }
            Double gHallExamEffMark = 0
            Double hallExamMark = groupClsSubject.hallMark?:0
            Double hallSbaMark = groupClsSubject.hallSbaMark?:0
            Double hallEffMark = groupClsSubject.hallEffMark?:100

            hallExamMark = hallExamMark - hallSbaMark
            if(hallEffMark ==100){
                gHallExamEffMark = hallExamMark
            }else {
                gHallExamEffMark = hallEffMark * hallExamMark * 0.01
            }
            groupEffExmMark = gCtExamEffMark + gHallExamEffMark
        }

        def allExamMarks = examMarkService.getAllExamMark(examSchedule,subject)
        String runningSchool = schoolService.runningSchool()
        Student student
        String fieldName = "subject${idxOfSub}Mark"
        String hallFieldName = "subject${idxOfSub}hall"
        String hallObtainName = "subject${idxOfSub}hallObtain"
        String ctFieldName = "subject${idxOfSub}ct"
        String ctaFieldName = "subject${idxOfSub}cta"
        if (runningSchool == CommonUtils.NARAYANGANJ_ADARSHA_SCHOOL) {
            Tabulation tabulation
            TabulationDetails details

            String fieldWritten = "subject${idxOfSub}Written"
            String fieldPractical = "subject${idxOfSub}Practical"
            String fieldObjective = "subject${idxOfSub}Objective"
            String fieldSba = "subject${idxOfSub}Sba"
            String fieldInput5 = "subject${idxOfSub}Input5"
            String fieldAvg = "subject${idxOfSub}avg"
            String fieldgp = "subject${idxOfSub}gp"
            String fieldComment = "subject${idxOfSub}Comment"

            for (examMark in allExamMarks) {
                student = examMark.student
                tabulation = tabulationService.getTabulation(exam, student)
                if(exam.examTerm == ExamTerm.FIRST_TERM || exam.examTerm == ExamTerm.SECOND_TERM || exam.examTerm == ExamTerm.HALF_YEARLY){ // all term exams
                    examMarkService.calculateAdarshaSubjectMark(psubject?:classSubjects, student, examMark, className, examSchedule)
                    if(tabulation){
                        tabulation."${fieldName}"=examMark.tabulationMark.round(2)
                        tabulation.save()
                    }else {
                        tabulation = tabulationService.entryTermSubjectMark(className, section, exam, student, fieldName, examMark.tabulationMark)
                    }
                    details = tabulationService.getTabulationDetails(tabulation?.id)
                    if(details){
                        if (examSchedule.isHallPractical){
                            details."${fieldWritten}"=examMark.hallWrittenMark
                            details."${fieldPractical}"=examMark.hallPracticalMark
                            details."${fieldObjective}"=examMark.hallObjectiveMark
                            details."${fieldSba}"=examMark.hallSbaMark
                            details."${fieldInput5}"=examMark.hallInput5
                        } else {
                            details."${fieldWritten}"=examMark.hallObtainMark
                        }
                        details."${fieldgp}"=examMark.gPoint
                        details."${fieldComment}"=tabulationService.subjectComment(examMark, classSubjects)
                        details.save()
                    }else {
                        details = tabulationService.entryTermDetailMark(tabulation.id, fieldWritten, fieldPractical, fieldObjective, fieldSba, fieldInput5, fieldgp, examMark, examSchedule.isHallPractical)
                        details."${fieldComment}"=tabulationService.subjectComment(examMark, classSubjects)
                        details.save()
                    }
                } else if (exam.examTerm == ExamTerm.FINAL_TEST){
                    //firnl term tabulation on else block
                    examMarkService.calculateAdarshaSubjectMark(psubject?:classSubjects, student, examMark, className, examSchedule)
                    if(tabulation){
                        tabulation."${fieldName}"=examMark.tabulationMark.round(2)
                        tabulation.save()
                    }else {
                        tabulation = tabulationService.entryFinalSubjectMark(className, section, exam, student, fieldName, examMark.tabulationMark)
                    }
                    details = tabulationService.getTabulationDetails(tabulation?.id)
                    if(details){
                        if (examSchedule.isHallPractical){
                            details."${fieldWritten}"=examMark.hallWrittenMark
                            details."${fieldPractical}"=examMark.hallPracticalMark
                            details."${fieldObjective}"=examMark.hallObjectiveMark
                            details."${fieldSba}"=examMark.hallSbaMark
                            details."${fieldInput5}"=examMark.hallInput5
                        } else {
                            details."${fieldWritten}"=examMark.hallObtainMark
                        }
                        details."${fieldgp}"=examMark.gPoint
                        details."${fieldComment}"=tabulationService.subjectComment(examMark, classSubjects)
                        details.save()
                    }else {
                        details = tabulationService.entryFinalDetailMark(tabulation.id, fieldWritten, fieldPractical, fieldObjective, fieldSba, fieldInput5, fieldgp, examMark, examSchedule.isHallPractical)
                        details."${fieldComment}"=tabulationService.subjectComment(examMark, classSubjects)
                        details.save()
                    }
                }

                examMark.idxOfSub = idxOfSub
                examMark.save(flush: true)
            }

        } else if (runningSchool == CommonUtils.NARAYANGANJ_IDEAL_SCHOOL) {
            Tabulation tabulation
            TabulationDetails details
            TabulationAvgMark tabulationAvgMark

            String fieldAvg = "subject${idxOfSub}avg"
            String fieldTerm = "subject${idxOfSub}term"
            Double termMark, avgMark

            String fieldWritten = "subject${idxOfSub}Written"
            String fieldPractical = "subject${idxOfSub}Practical"
            String fieldObjective = "subject${idxOfSub}Objective"
            String fieldSba = "subject${idxOfSub}Sba"
            String fieldInput5 = "subject${idxOfSub}Input5"
            String fieldgp = "subject${idxOfSub}gp"

            for (examMark in allExamMarks) {
                student = examMark.student
                if(exam.examTerm == ExamTerm.FIRST_TERM || exam.examTerm == ExamTerm.SECOND_TERM || exam.examTerm == ExamTerm.HALF_YEARLY){ // all term exams
                    if (subjectGroup && (idxOfSub == 1 || idxOfSub == 3)){
                        examMarkService.calculateIdealGroupSubjectMark(psubject?:classSubjects, student, examMark, className, subject, exam, groupSubject, groupEffExmMark)
                    } else {
                        examMarkService.calculateIdealSubjectMark(psubject?:classSubjects, student, examMark, className, subject)
                    }
                    tabulation = tabulationService.getTabulation(exam, student)
                    if(!tabulation){
                        tabulation = tabulationService.termTabulation(className, section, exam, exam.examTerm, student)
                    }
                    tabulation."${ctFieldName}"=examMark.ctObtainMark
                    tabulation."${fieldName}"=examMark.tabulationMark
                    tabulation.save()
                    /*if(tabulation){
                        tabulation."${fieldName}"=examMark.tabulationMark.round(2)
                        tabulation.save()
                    }else {
                        tabulation = tabulationService.entryTermSubjectMark(className, section, exam, student, fieldName, examMark.tabulationMark)
                    }*/
                    details = tabulationService.getTabulationDetails(tabulation?.id)
                    if(details){
                        if (examSchedule.isHallPractical){
                            details."${fieldWritten}"=examMark.hallWrittenMark
                            details."${fieldPractical}"=examMark.hallPracticalMark
                            details."${fieldObjective}"=examMark.hallObjectiveMark
                            details."${fieldSba}"=examMark.hallSbaMark
                            details."${fieldInput5}"=examMark.hallInput5
                        } else {
                            details."${fieldWritten}"=examMark.hallObtainMark
                        }
                        details."${fieldgp}"=examMark.gPoint
                        details.save()
                    }else {
                        details = tabulationService.entryTermDetailMark(tabulation?.id, fieldWritten, fieldPractical, fieldObjective, fieldSba, fieldInput5, fieldgp, examMark, examSchedule.isHallPractical)
                        details.save()
                    }
                } else if(exam.examTerm == ExamTerm.FINAL_TEST){
                    if (subjectGroup && (idxOfSub == 1 || idxOfSub == 3)){
                        (termMark, avgMark) = examMarkService.calculateIdealFinalGroupSubjectMark(psubject?:classSubjects, student, examMark, className, subject, exam, groupSubject, groupEffExmMark)
                    } else {
                        (termMark, avgMark) =  examMarkService.calculateIdealFinalSubjectMark(psubject?:classSubjects, student, examMark, className, subject)
                    }
                    tabulation = tabulationService.getTabulation(exam, student)
                    if(!tabulation){
                        tabulation = tabulationService.finalTabulation(className, section, exam, student)
                    }

                    tabulation."${ctFieldName}"=examMark.ctObtainMark
                    tabulation."${fieldName}"=examMark.tabulationMark
                    tabulation.save()

                    details = tabulationService.getTabulationDetails(tabulation?.id)
                    if(details){
                        if (examSchedule.isHallPractical){
                            details."${fieldWritten}"=examMark.hallWrittenMark
                            details."${fieldPractical}"=examMark.hallPracticalMark
                            details."${fieldObjective}"=examMark.hallObjectiveMark
                            details."${fieldSba}"=examMark.hallSbaMark
                            details."${fieldInput5}"=examMark.hallInput5
                        } else {
                            details."${fieldWritten}"=examMark.hallObtainMark
                        }
                        details."${fieldgp}"=examMark.gPoint
                        details.save()
                    }else {
                        details = tabulationService.entryFinalDetailMark(tabulation?.id, fieldWritten, fieldPractical, fieldObjective, fieldSba, fieldInput5, fieldgp, examMark, examSchedule.isHallPractical)
                        details.save()
                    }
                    tabulationAvgMark = tabulationService.getTabulationAvgMark(tabulation?.id)
                    if (!tabulationAvgMark) {
                        tabulationAvgMark = new TabulationAvgMark(tabulationId: tabulation?.id)
                    }
                    tabulationAvgMark."${fieldAvg}"=avgMark?.round(2)
                    tabulationAvgMark."${fieldTerm}"= termMark
                    tabulationAvgMark."${hallFieldName}"= examMark.hallMark
                    tabulationAvgMark."${hallObtainName}"= examMark.hallObtainMark
                    tabulationAvgMark."${ctaFieldName}"= examMark.ctMark
                    tabulationAvgMark.save()
                }
                //need to handle firnl term tabulation on else block
                examMark.idxOfSub = idxOfSub
                examMark.save(flush: true)
            }
        } else if (runningSchool == CommonUtils.NARAYANGANJ_HIGH_SCHOOL) {
            Tabulation tabulation
            TabulationDetails details
            TabulationAvgMark tabulationAvgMark

            String fieldAvg = "subject${idxOfSub}avg"
            String fieldTerm = "subject${idxOfSub}term"
            Double termMark, avgMark

            String fieldWritten = "subject${idxOfSub}Written"
            String fieldPractical = "subject${idxOfSub}Practical"
            String fieldObjective = "subject${idxOfSub}Objective"
            String fieldSba = "subject${idxOfSub}Sba"
            String fieldInput5 = "subject${idxOfSub}Input5"
            String fieldgp = "subject${idxOfSub}gp"

            for (examMark in allExamMarks) {
                student = examMark.student
                if(exam.examTerm == ExamTerm.FIRST_TERM || exam.examTerm == ExamTerm.SECOND_TERM || exam.examTerm == ExamTerm.HALF_YEARLY){ // all term exams
                    if (subjectGroup && (idxOfSub == 1 || idxOfSub == 3)){
                        examMarkService.calculateNhsGroupSubjectMark(psubject?:classSubjects, student, examMark, className, subject, exam, groupSubject, groupEffExmMark)
                    } else {
                        examMarkService.calculateNhsSubjectMark(psubject?:classSubjects, student, examMark, className, subject)
                    }
                    tabulation = tabulationService.getTabulation(exam, student)
                    if(tabulation){
                        tabulation."${fieldName}"= subjectSba ? examMark.tabulationNosbaMark : examMark.tabulationMark
                        tabulation.save()
                    }else {
                        tabulation = tabulationService.entryTermSubjectMark(className, section, exam, student, fieldName, subjectSba ? examMark.tabulationNosbaMark : examMark.tabulationMark)
                    }
                    details = tabulationService.getTabulationDetails(tabulation?.id)
                    if(details){
                        if (examSchedule.isHallPractical){
                            details."${fieldWritten}"=examMark.hallWrittenMark
                            details."${fieldPractical}"=examMark.hallPracticalMark
                            details."${fieldObjective}"=examMark.hallObjectiveMark
                            details."${fieldSba}"=examMark.hallSbaMark
                            details."${fieldInput5}"=examMark.hallInput5
                        } else {
                            details."${fieldWritten}"=examMark.hallObtainMark
                        }
                        details."${fieldgp}"=examMark.gPoint
                        details.save()
                    }else {
                        details = tabulationService.entryTermDetailMark(tabulation?.id, fieldWritten, fieldPractical, fieldObjective, fieldSba, fieldInput5, fieldgp, examMark, examSchedule.isHallPractical)
                        details.save()
                    }
                } else if(exam.examTerm == ExamTerm.FINAL_TEST){
                    if (subjectGroup && (idxOfSub == 1 || idxOfSub == 3)){
                        examMarkService.calculateNhsGroupSubjectMark(psubject?:classSubjects, student, examMark, className, subject, exam, groupSubject, groupEffExmMark)
                    } else {
                        examMarkService.calculateNhsSubjectMark(psubject?:classSubjects, student, examMark, className, subject)
                    }
                    tabulation = tabulationService.getTabulation(exam, student)
                    if(!tabulation){
                        tabulation = tabulationService.finalTabulation(className, section, exam, student)
                    }
                    tabulation."${ctFieldName}"=examMark.ctObtainMark
                    tabulation."${fieldName}"=subjectSba ? examMark.tabulationNosbaMark : examMark.tabulationMark
                    tabulation.save()

                    details = tabulationService.getTabulationDetails(tabulation?.id)
                    if(details){
                        if (examSchedule.isHallPractical){
                            details."${fieldWritten}"=examMark.hallWrittenMark
                            details."${fieldPractical}"=examMark.hallPracticalMark
                            details."${fieldObjective}"=examMark.hallObjectiveMark
                            details."${fieldSba}"=examMark.hallSbaMark
                            details."${fieldInput5}"=examMark.hallInput5
                        } else {
                            details."${fieldWritten}"=examMark.hallObtainMark
                        }
                        details."${fieldgp}"=examMark.gPoint
                        details.save()
                    }else {
                        details = tabulationService.entryFinalDetailMark(tabulation?.id, fieldWritten, fieldPractical, fieldObjective, fieldSba, fieldInput5, fieldgp, examMark, examSchedule.isHallPractical)
                        details.save()
                    }
                    /*tabulationAvgMark = tabulationService.getTabulationAvgMark(tabulation?.id)
                    if (!tabulationAvgMark) {
                        tabulationAvgMark = new TabulationAvgMark(tabulationId: tabulation?.id)
                    }
                    tabulationAvgMark."${fieldAvg}"=avgMark?.round(2)
                    tabulationAvgMark."${fieldTerm}"= termMark
                    tabulationAvgMark.save()*/
                }
                //need to handle firnl term tabulation on else block
                examMark.idxOfSub = idxOfSub
                examMark.save(flush: true)
            }
        } else if (runningSchool == CommonUtils.BAILY_SCHOOL){
            Tabulation tabulation
            for (examMark in allExamMarks) {
                student = examMark.student
                examMarkService.calculateSubjectMark(psubject?:classSubjects, student, examMark, className, subject)
                tabulation = tabulationService.getTabulation(exam, student)
                if(exam.examTerm == ExamTerm.FIRST_TERM || exam.examTerm == ExamTerm.SECOND_TERM || exam.examTerm == ExamTerm.HALF_YEARLY){ // all term exams
                    if(tabulation){
                        tabulation."${fieldName}"=examMark.tabulationMark.round(2)
                        tabulation.save()
                    }else {
                        tabulationService.entryTermSubjectMark(className, section, exam, student, fieldName, examMark.tabulationMark)
                    }
                } else if (exam.examTerm == ExamTerm.FINAL_TEST) {
                        if(tabulation){
                            tabulation."${fieldName}"=examMark.tabulationMark.round(2)
                            tabulation.save()
                        } else {
                        tabulation = tabulationService.entryFinalSubjectMark(className, section, exam, student, fieldName, examMark.tabulationMark)
                    }
                }
                examMark.idxOfSub = idxOfSub
                examMark.save(flush: true)
            }
        }
        //highest and averageMark calcualted on Merit Position calculatation
        examSchedule.highestMark = 0
        examSchedule.averageMark = 0
        examSchedule.isHallMarkInput = true
        examSchedule.isExtracurricular = isExBpSubject
        examSchedule.save()
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, 'Successfully mark completed')
        outPut = result as JSON
        render outPut
        return
    }

    def deleteHallMark(Long id){
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        ExamSchedule examSchedule = ExamSchedule.get(id)
        if(!examSchedule){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, 'Exam Schedule Not found')
            outPut = result as JSON
            render outPut
            return
        }
        Exam exam = examSchedule.exam
        if(exam.examStatus==ExamStatus.PUBLISHED){
            result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
            result.put(CommonUtils.MESSAGE, 'Result Already Published')
            outPut = result as JSON
            render outPut
            return
        }
        if(examSchedule.isHallMarkInput){
            result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
            result.put(CommonUtils.MESSAGE, "Already mark completed. Please edit first any one input to delete all")
            outPut = result as JSON
            render outPut
            return
        }
        SubjectName subject = examSchedule.subject
        def markEntryStdIds = examMarkService.markEntryStdIds(examSchedule, ExamType.HALL_TEST)
        if (!markEntryStdIds) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, 'No mark input')
            outPut = result as JSON
            render outPut
            return
        }

        def allExamMarks = examMarkService.getAllExamMark(examSchedule, subject)
        for (examMark in allExamMarks) {
            examMark.hallAttendStatus = AttendStatus.NO_INPUT
            examMark.save()
        }

        examSchedule.isHallMarkInput = false
        examSchedule.save()
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, 'Deleted all exam mark input Successfully')
        outPut = result as JSON
        render outPut
        return
    }
    def deleteCtMark(Long id){
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        ExamSchedule examSchedule = ExamSchedule.get(id)
        if(!examSchedule){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, 'Exam Schedule Not found')
            outPut = result as JSON
            render outPut
            return
        }
        Exam exam = examSchedule.exam
        if(exam.examStatus==ExamStatus.PUBLISHED){
            result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
            result.put(CommonUtils.MESSAGE, 'Result Already Published')
            outPut = result as JSON
            render outPut
            return
        }
        if(examSchedule.isCtMarkInput){
            result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
            result.put(CommonUtils.MESSAGE, "Already mark completed. Please edit first any one input to delete all")
            outPut = result as JSON
            render outPut
            return
        }

        SubjectName subject = examSchedule.subject
        def markEntryStdIds = examMarkService.markEntryStdIds(examSchedule, ExamType.CLASS_TEST)
        if (!markEntryStdIds) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, 'No mark input')
            outPut = result as JSON
            render outPut
            return
        }

        def allExamMarks = examMarkService.getAllCtExamMark(examSchedule, subject)
        for (examMark in allExamMarks) {
            examMark.ctAttendStatus = AttendStatus.NO_INPUT
            examMark.save()
        }

        examSchedule.isCtMarkInput = false
        examSchedule.save()
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, 'Deleted all CT exam mark input Successfully')
        outPut = result as JSON
        render outPut
        return
    }


}


