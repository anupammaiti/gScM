package com.grailslab.teacher

import com.grailslab.CommonUtils
import com.grailslab.bailyschool.uma.Role
import com.grailslab.command.CtExamMarkCommand
import com.grailslab.command.HallExamMarkCommand
import com.grailslab.enums.AttendStatus
import com.grailslab.enums.ExamStatus
import com.grailslab.enums.ExamType
import com.grailslab.enums.SubjectType
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.settings.*
import com.grailslab.stmgmt.ExamMark
import com.grailslab.stmgmt.Student
import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityUtils

class MarkEntryController {
    def shiftExamService
    def examMarkService
    def studentService
    def studentSubjectsService
    def messageSource
    def springSecurityService

    def index() {
        def examNameList = shiftExamService.examNameDropDown()
        String layoutName = "moduleExam&ResultLayout"
        if (SpringSecurityUtils.ifAnyGranted(Role.AvailableRoles.TEACHER.value())){
            layoutName = "adminLayout"
        }
        render (view: 'index', layout: layoutName, model: [examNameList: examNameList])
    }
    def addCtMark(Long id) {
        ExamSchedule examSchedule = ExamSchedule.get(id)
        if(!examSchedule){
            flash.message = "Exam Schedule Not Fount. Please contact admin."
            redirect(action: 'index')
            return
        }
        Exam exam = examSchedule.exam
        if(exam.examStatus != ExamStatus.NEW){
            flash.message = "Mark Entry not Allowed. Please contact admin."
            redirect(action: 'index')
            return
        }

        ClassName className = exam.className
        Section section = exam.section
        SubjectName subject = examSchedule.subject

        //sink exam mark update
        ClassSubjects classSubjects = ClassSubjects.findByClassNameAndSubjectAndActiveStatus(className, subject, ActiveStatus.ACTIVE)
        if(!classSubjects){
            flash.message = "Subject is not added to class. Please contact admin."
            redirect(action: 'index')
            return
        }
        if (classSubjects.subjectType == SubjectType.INUSE) {
            classSubjects = ClassSubjects.read(classSubjects.parentSubId)
        }
        Boolean scheduleChanged = false
        if(classSubjects.ctMark != examSchedule.ctExamMark.toInteger()){
            examSchedule.ctExamMark = classSubjects.ctMark
            scheduleChanged = true
        }
        String viewName = "ctMarkEntry"
        String layoutName = "moduleExam&ResultLayout"
        if (SpringSecurityUtils.ifAnyGranted(Role.AvailableRoles.TEACHER.value())){
            layoutName = "adminLayout"
        }

        if (scheduleChanged) {
            examSchedule.save()
        }
        def examNameList = shiftExamService.examNameDropDown()

        LinkedHashMap resultMap = examMarkService.getCtMarkEntryList(params, section, examSchedule, subject)
        int totalCount = resultMap.totalCount
        def markEntryResult = resultMap.results
        def studentList
        def ctMarkInputStudentIds
        if(classSubjects.subjectType == SubjectType.COMPULSORY){
            if (totalCount == 0) {
                studentList = studentService.allStudent(section)
                render(view: viewName, layout: layoutName, model: [examNameList:examNameList,  examSchedule:examSchedule, className:className?.name, section:section?.name, examTerm:exam.shiftExam?.examTerm.value, subject:subject?.name, examDate:CommonUtils.getUiDateStr(examSchedule.ctExamDate), 'studentList': studentList, dataReturn: null, totalCount: 0])
                return
            }
            ctMarkInputStudentIds = examMarkService.getAlreadyCtMarkStudents(examSchedule,subject)
            studentList = studentService.allStudentNotInList(section,ctMarkInputStudentIds)
            render(view: viewName, layout: layoutName, model: [examNameList:examNameList, examSchedule:examSchedule,className:className?.name,section:section?.name, examTerm:exam.shiftExam?.examTerm.value,subject:subject?.name, examDate:CommonUtils.getUiDateStr(examSchedule.ctExamDate),'studentList': studentList, dataReturn:markEntryResult , totalCount: totalCount])
            return
        }

        //code to get optional subject students
        def subjectStudents
        if (totalCount == 0) {
            subjectStudents = studentSubjectsService.subjectStudentsExcludingList(section,subject,null)
            studentList = studentService.allStudentInList(subjectStudents)
            render(view: viewName, layout: layoutName, model: [examNameList:examNameList, examSchedule:examSchedule,className:className?.name,section:section?.name, examTerm:exam.shiftExam?.examTerm.value,subject:subject?.name, examDate:CommonUtils.getUiDateStr(examSchedule.ctExamDate),'studentList': studentList, dataReturn: null, totalCount: 0])
            return
        }
        ctMarkInputStudentIds = examMarkService.getAlreadyCtMarkStudents(examSchedule,subject)
        subjectStudents = studentSubjectsService.subjectStudentsExcludingList(section,subject,ctMarkInputStudentIds)
        studentList = studentService.allStudentInList(subjectStudents)
        render(view: viewName, layout: layoutName, model: [examNameList:examNameList, examSchedule:examSchedule,className:className?.name,section:section?.name, examTerm:exam.shiftExam?.examTerm.value,subject:subject?.name, examDate:CommonUtils.getUiDateStr(examSchedule.ctExamDate),'studentList': studentList, dataReturn:markEntryResult , totalCount: totalCount])
    }
    def listCtMark(Long examScheduleId) {
        LinkedHashMap gridData
        String result

        ExamSchedule examSchedule = ExamSchedule.read(examScheduleId)
        if(!examSchedule){
            gridData.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result = gridData as JSON
            render result
        }
        Exam exam = examSchedule.exam
        ClassName className = exam.className
        Section section = exam.section
        SubjectName subject = examSchedule.subject
        LinkedHashMap resultMap = examMarkService.getCtMarkEntryList(params, section, examSchedule, subject)
        int totalCount = resultMap.totalCount
        if (totalCount == 0) {
            gridData = [iTotalRecords: 0, iTotalDisplayRecords: 0, aaData: []]
            result = gridData as JSON
            render result
            return
        }
        gridData = [iTotalRecords: totalCount, iTotalDisplayRecords: totalCount, aaData: resultMap.results]
        result = gridData as JSON
        render result
    }
    def saveCtMark(CtExamMarkCommand markCommand){
        if (!request.method.equals('POST')) {
            redirect(controller: 'exam', action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        if (markCommand.hasErrors()) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            def errorList = markCommand?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            result.put(CommonUtils.MESSAGE, errorList?.join('\n'))
            outPut = result as JSON
            render outPut
            return
        }

        ExamSchedule examSchedule = markCommand.examScheduleId
        ExamMark examMark
        Double ctObtainMark = 0
        Double ctWrittenMark = 0
        Double ctPracticalMark = 0
        Double ctObjectiveMark = 0
        Double ctSbaMark = 0
        AttendStatus attendStatus

        Exam exam = examSchedule.exam
        SubjectName subject = examSchedule.subject

        String message
        if(markCommand.id){
            examMark = ExamMark.get(markCommand.id)
            if(!examMark){
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_ERROR_MESSAGE)
                outPut = result as JSON
                render outPut
                return
            }
            attendStatus = markCommand.attendStatus
            if(attendStatus == AttendStatus.PRESENT){
                ctObtainMark =markCommand.markObtain
            }
            examMark.ctAttendStatus = attendStatus
            examMark.ctObtainMark = ctObtainMark
            if(examSchedule.isCtMarkInput){
                examSchedule.isCtMarkInput = false
                if(examSchedule.isHallMarkInput){
                    examSchedule.isHallMarkInput = false
                }
                examSchedule.save()
            }
            message = "Mark Successfully Updated."
        }else {
            Student student = markCommand.studentId
            if(!student){
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, "No student found to add mark")
                outPut = result as JSON
                render outPut
                return
            }
            attendStatus = markCommand.attendStatus
            if(attendStatus == AttendStatus.PRESENT){
                ctObtainMark =markCommand.markObtain
            }
            examMark = ExamMark.findByExamScheduleAndStudentAndActiveStatus(examSchedule,student, ActiveStatus.ACTIVE)
            if(examMark){
                examMark.ctObtainMark = ctObtainMark
                examMark.ctAttendStatus = attendStatus
            } else {
                examMark = new ExamMark(ctAttendStatus:attendStatus, ctObtainMark:ctObtainMark, student:student, exam: exam, examSchedule: examSchedule, subject:subject)
            }
            message = "Mark Successfully Added."
        }

        if(examMark.hasErrors() || !examMark.save(flush: true)){
            def errorList = examMark?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, errorList?.join('\n'))
            outPut = result as JSON
            render outPut
            return
        }
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, message)
        outPut = result as JSON
        render outPut
        return
    }

    def edit(Long id) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        ExamMark examMark = ExamMark.read(id)
        if (!examMark) {
            result.put(CommonUtils.IS_ERROR,true)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_ERROR_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        def principal = springSecurityService.principal
        if (!(SpringSecurityUtils.ifAnyGranted("ROLE_SUPER_ADMIN, ROLE_ADMIN")  || examMark.createdBy == 'admin'  || examMark.updatedBy == 'admin' || principal.username == examMark.createdBy || principal.username == examMark.updatedBy)){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "You are not authenticated to edit this mark entry. Either ADMIN or ${examMark.createdBy} or ${examMark.updatedBy} can edit this. Please contact with admin.")
            outPut = result as JSON
            render outPut
            return
        }
        String studentID= examMark.student.studentID+" - "+examMark.student.name+" - [Roll: "+examMark.student.rollNo+"]"
        result.put(CommonUtils.IS_ERROR,false)
        result.put('obj',examMark)
        result.put('studentID',studentID)
        outPut = result as JSON
        render outPut
    }

    def delete(Long id) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        ExamMark examMark = ExamMark.get(id)
        if (!examMark) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        def principal = springSecurityService.principal
        if (!(SpringSecurityUtils.ifAnyGranted("ROLE_SUPER_ADMIN, ROLE_ADMIN")  || examMark.createdBy == 'admin'  || examMark.updatedBy == 'admin' || principal.username == examMark.createdBy || principal.username == examMark.updatedBy)){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "You are not authenticated to edit this mark entry. Either ADMIN or ${examMark.createdBy} or ${examMark.updatedBy} can edit this. Please contact with admin.")
            outPut = result as JSON
            render outPut
            return
        }
        ExamSchedule examSchedule = examMark.examSchedule
        String examType = params.examType
        if (examType && examType == ExamType.CLASS_TEST.key) {
            if (examMark.hallAttendStatus && examMark.hallAttendStatus == AttendStatus.NO_INPUT) {
                examMark.activeStatus = ActiveStatus.DELETE
            } else {
                examMark.ctAttendStatus = AttendStatus.NO_INPUT
            }
            if (examSchedule.isCtMarkInput) {
                examSchedule.isCtMarkInput = false
                examSchedule.isHallMarkInput = false
                examSchedule.save()
            }
        } else {
            if (examMark.ctAttendStatus && examMark.ctAttendStatus == AttendStatus.NO_INPUT) {
                examMark.activeStatus = ActiveStatus.DELETE
            } else {
                examMark.hallAttendStatus = AttendStatus.NO_INPUT
            }
            if (examSchedule.isHallMarkInput) {
                examSchedule.isHallMarkInput = false
                examSchedule.save()
            }
        }
        examMark.save()
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE,"Mark Entry deleted successfully.")
        outPut = result as JSON
        render outPut
    }

    //hall Mark Entry
    def addHallMark(Long id){
        ExamSchedule examSchedule = ExamSchedule.get(id)
        if(!examSchedule){
            flash.message = "Exam Schedule Not Fount. Please contact admin."
            redirect(action: 'index')
            return
        }
        Exam exam = examSchedule.exam
        if(exam.examStatus != ExamStatus.NEW){
            flash.message = "Exam Schedule Not Fount. Please contact admin."
            redirect(action: 'index')
            return
        }

        ClassName className = exam.className
        Section section = exam.section
        SubjectName subject = examSchedule.subject

        //sink exam mark update
        ClassSubjects classSubjects = ClassSubjects.findByClassNameAndSubjectAndActiveStatus(className, subject, ActiveStatus.ACTIVE)
        if(!classSubjects){
            flash.message = "Class Subject Not found. Please contact admin."
            redirect(action: 'index')
            return
        }
        if (classSubjects.subjectType == SubjectType.INUSE) {
            classSubjects = ClassSubjects.read(classSubjects.parentSubId)
        }
        Boolean scheduleChanged = false
        if(classSubjects.hallMark != examSchedule.hallExamMark?.toInteger()){
            examSchedule.hallExamMark = classSubjects.hallMark
            scheduleChanged = true
        }
//        @todo-aminul Need to remove logic with exam schedule but introduce class subject
        String viewName = "hallMarkEntry"
        String layoutName = "moduleExam&ResultLayout"
        if (SpringSecurityUtils.ifAnyGranted(Role.AvailableRoles.TEACHER.value())){
            layoutName = "adminLayout"
        }

        if (classSubjects.isHallPractical) {
            viewName = "hallMarkWithPracticalEntry"
            examSchedule.isHallPractical = true
            scheduleChanged = true
        } else {
            examSchedule.isHallPractical = false
            scheduleChanged = true
        }

        if (scheduleChanged) {
            examSchedule.save()
        }
        def examNameList = shiftExamService.examNameDropDown()

        LinkedHashMap resultMap = examMarkService.getHallMarkEntryList(params, section, examSchedule, subject)
        int totalCount = resultMap.totalCount
        def markEntryResult = resultMap.results
        def studentList
        def ctMarkInputStudentIds

        if(classSubjects.subjectType == SubjectType.COMPULSORY){
            if (totalCount == 0) {
                studentList = studentService.allStudent(section)
                render(view: viewName, layout: layoutName, model: [examNameList:examNameList, hallWrittenMark: classSubjects.hallWrittenMark, hallObjectiveMark: classSubjects.hallObjectiveMark, hallPracticalMark: classSubjects.hallPracticalMark,hallSbaMark: classSubjects.hallSbaMark,hallInput5: classSubjects.hallInput5,className: className.name, examSchedule:examSchedule, section:section?.name, examTerm:exam.shiftExam?.examTerm.value, subject:subject?.name, examDate:CommonUtils.getUiDateStr(examSchedule.hallExamDate),'studentList': studentList, dataReturn: null, totalCount: 0])
                return
            }
            ctMarkInputStudentIds = examMarkService.getAlreadyHallMarkStudents(examSchedule, subject)
            studentList = studentService.allStudentNotInList(section,ctMarkInputStudentIds)
            render(view: viewName, layout: layoutName, model: [examNameList:examNameList, hallWrittenMark: classSubjects.hallWrittenMark, hallObjectiveMark: classSubjects.hallObjectiveMark, hallPracticalMark: classSubjects.hallPracticalMark,hallSbaMark: classSubjects.hallSbaMark,hallInput5: classSubjects.hallInput5,className: className.name, examSchedule:examSchedule, section:section?.name, examTerm:exam.shiftExam?.examTerm.value, subject:subject?.name, examDate:CommonUtils.getUiDateStr(examSchedule.hallExamDate),'studentList': studentList, dataReturn:markEntryResult , totalCount: totalCount])
            return
        }
        //code to get optional subject students
        def subjectStudents
        if (totalCount == 0) {
            subjectStudents = studentSubjectsService.subjectStudentsExcludingList(section, subject, null)
            studentList = studentService.allStudentInList(subjectStudents)
            render(view: viewName, layout: layoutName, model: [examNameList:examNameList, hallWrittenMark: classSubjects.hallWrittenMark, hallObjectiveMark: classSubjects.hallObjectiveMark, hallPracticalMark: classSubjects.hallPracticalMark,hallSbaMark: classSubjects.hallSbaMark,hallInput5: classSubjects.hallInput5,className: className.name, examSchedule:examSchedule, section:section?.name, examTerm:exam.shiftExam?.examTerm.value, subject:subject?.name, examDate:CommonUtils.getUiDateStr(examSchedule.hallExamDate),'studentList': studentList, dataReturn: null, totalCount: 0])
            return
        }
        ctMarkInputStudentIds = examMarkService.getAlreadyHallMarkStudents(examSchedule,subject)
        subjectStudents = studentSubjectsService.subjectStudentsExcludingList(section,subject,ctMarkInputStudentIds)
        studentList = studentService.allStudentInList(subjectStudents)
        render(view: viewName, layout: layoutName, model: [examNameList:examNameList, hallWrittenMark: classSubjects.hallWrittenMark, hallObjectiveMark: classSubjects.hallObjectiveMark, hallPracticalMark: classSubjects.hallPracticalMark, hallSbaMark: classSubjects.hallSbaMark,hallInput5: classSubjects.hallInput5, className: className.name, examSchedule:examSchedule,section:section?.name, examTerm:exam.shiftExam?.examTerm.value, subject:subject?.name, examDate:CommonUtils.getUiDateStr(examSchedule.hallExamDate),'studentList': studentList, dataReturn:markEntryResult , totalCount: totalCount])
    }
    def listHallMark(Long examScheduleId) {
        LinkedHashMap gridData
        String result

        ExamSchedule examSchedule = ExamSchedule.read(examScheduleId)
        if(!examSchedule){
            gridData.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result = gridData as JSON
            render result
        }
        Exam exam = examSchedule.exam
        ClassName className = exam.className
        Section section = exam.section
        SubjectName subject = examSchedule.subject
        LinkedHashMap resultMap = examMarkService.getHallMarkEntryList(params, section, examSchedule, subject)
        int totalCount = resultMap.totalCount
        if (totalCount == 0) {
            gridData = [iTotalRecords: 0, iTotalDisplayRecords: 0, aaData: []]
            result = gridData as JSON
            render result
            return
        }
        gridData = [iTotalRecords: totalCount, iTotalDisplayRecords: totalCount, aaData: resultMap.results]
        result = gridData as JSON
        render result
    }

    def saveHallMark(HallExamMarkCommand markCommand){
        if (!request.method.equals('POST')) {
            redirect(controller: 'exam', action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut

        if (markCommand.hasErrors()) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            def errorList = markCommand?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            result.put(CommonUtils.MESSAGE, errorList?.join('\n'))
            outPut = result as JSON
            render outPut
            return
        }

        ExamSchedule examSchedule = markCommand.examScheduleId
        ExamMark examMark
        Double hallObtainMark = 0
        Double hallWrittenMark = 0
        Double hallPracticalMark = 0
        Double hallObjectiveMark = 0
        Double hallSbaMark = 0
        Double hallInput5 = 0
        AttendStatus attendStatus

        Exam exam = examSchedule.exam
        SubjectName subject = examSchedule.subject

        String message
        if(markCommand.id){
            examMark = ExamMark.get(markCommand.id)
            if(!examMark){
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_ERROR_MESSAGE)
                outPut = result as JSON
                render outPut
                return
            }

            attendStatus = markCommand.attendStatus
            if(attendStatus == AttendStatus.PRESENT){
                hallObtainMark =markCommand.markObtain
                if (examSchedule.isHallPractical) {
                    hallWrittenMark = markCommand.hallWrittenMark
                    hallPracticalMark = markCommand.hallPracticalMark
                    hallObjectiveMark = markCommand.hallObjectiveMark
                    hallSbaMark = markCommand.hallSbaMark
                    hallInput5 = markCommand.hallInput5
                }
            }
            examMark.hallAttendStatus = attendStatus
            examMark.hallObtainMark = hallObtainMark
            examMark.hallWrittenMark = hallWrittenMark
            examMark.hallPracticalMark = hallPracticalMark
            examMark.hallObjectiveMark = hallObjectiveMark
            examMark.hallSbaMark = hallSbaMark
            hallInput5 = markCommand.hallInput5

            if(examSchedule.isHallMarkInput){
                examSchedule.isHallMarkInput = false
                if(examSchedule.isCtMarkInput){
                    examSchedule.isHallMarkInput = false
                }
                examSchedule.save()
            }
            message = "Mark Successfully Updated."
        }else {
            Student student = markCommand.studentId
            if(!student){
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, "No student found to add mark")
                outPut = result as JSON
                render outPut
                return
            }
            attendStatus = markCommand.attendStatus
            if(attendStatus == AttendStatus.PRESENT){
                hallObtainMark =markCommand.markObtain
                if (examSchedule.isHallPractical) {
                    hallWrittenMark = markCommand.hallWrittenMark
                    hallPracticalMark = markCommand.hallPracticalMark
                    hallObjectiveMark = markCommand.hallObjectiveMark
                    hallSbaMark = markCommand.hallSbaMark
                    hallInput5 = markCommand.hallInput5
                }
            }

            examMark = ExamMark.findByExamScheduleAndStudentAndActiveStatus(examSchedule, student, ActiveStatus.ACTIVE)
            if(examMark){
                examMark.hallObtainMark = hallObtainMark
                examMark.hallAttendStatus = attendStatus
                examMark.hallWrittenMark = hallWrittenMark
                examMark.hallPracticalMark = hallPracticalMark
                examMark.hallObjectiveMark = hallObjectiveMark
                examMark.hallSbaMark = hallSbaMark
                examMark.hallInput5 = hallInput5
            } else {
                examMark = new ExamMark(hallInput5:hallInput5, hallSbaMark:hallSbaMark,hallObjectiveMark:hallObjectiveMark, hallPracticalMark:hallPracticalMark, hallWrittenMark:hallWrittenMark,
                        hallAttendStatus:attendStatus, hallObtainMark:hallObtainMark, student:student, exam: exam,
                        examSchedule: examSchedule, subject:subject)
            }
            message = "Mark Successfully Added."
        }

        if(examMark.hasErrors() || !examMark.save(failOnError:true)){
            def errorList = examMark?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, errorList?.join('\n'))
            outPut = result as JSON
            render outPut
            return
        }
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, message)
        outPut = result as JSON
        render outPut
        return
    }


}
