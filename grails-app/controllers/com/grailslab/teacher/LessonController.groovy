package com.grailslab.teacher

import com.grailslab.CommonUtils
import com.grailslab.command.LessonCommand
import com.grailslab.command.LessonUpdateCommand
import com.grailslab.enums.ExamTerm
import com.grailslab.hr.Employee
import com.grailslab.settings.*
import grails.converters.JSON
import org.joda.time.DateTimeConstants
import org.joda.time.LocalDate

class LessonController {

    def lessonService
    def classSubjectsService
    def sectionService
    def employeeService
    def messageSource
    def springSecurityService

    def index(){
        render (view: '/common/dashboard',layout: 'moduleLessonAndFeedbackLayout')
    }
    def lessonDetail() {
        def sectionList = sectionService.allSections(params)
        render(view: '/teacher/lesson/lesson', model: [sectionList:sectionList])
    }
    def add(){
        def sectionList = sectionService.sectionDropDownList()
        render(view: '/teacher/lesson/add', model: [sectionList:sectionList])
    }
    def feedback(Long id) {
        LessonDetails lessonDetails = LessonDetails.read(id)
        if (!lessonDetails) {
            redirect(controller: 'lesson',action: 'index')
            return
        }
        Lesson lessonObj = lessonDetails?.lesson
        String lessonDate = CommonUtils.getUiDateStr(lessonObj.lessonDate)
        Section section = lessonObj.section
        render(view: '/teacher/lesson/feedback', model: [sectionList:sectionList, lessonDate:lessonDate, subjectName:lessonDetails?.subject?.name])
    }

    
    def save(LessonCommand command) {

        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        if (command.hasErrors()) {
            def errorList = command?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, errorList?.join('\n'))
            outPut = result as JSON
            render outPut
            return
        }
        LessonWeek lessonWeek = lessonService.getWeek(command.lessonDate)
        if(!lessonWeek){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "Lesson Date is not valid (Sun - Thu) or week not initiated. Please contact admin.")
            outPut = result as JSON
            render outPut
            return
        }
        LessonDetails lessonDetails
        Lesson lesson = lessonService.getLesson(command.section, command.lessonDate)
        if(lesson){
            def details = lesson.lessonDetails.findAll {it.subject == command.subject}
            if(details){
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, "Lesson already added. Please edit lesson if require.")
                outPut = result as JSON
                render outPut
                return
            }
            lessonDetails = new LessonDetails(employee:command.employee,topics:command.topics,homeWork:command.homeWork,examDate:command.examDate,subject:command.subject)
            lesson.addToLessonDetails(lessonDetails)
        }else {
            lesson = new Lesson(examTerm:command.examTerm, section:command.section,lessonDate:command.lessonDate)
            lessonDetails = new LessonDetails(employee:command.employee,topics:command.topics,homeWork:command.homeWork,examDate:command.examDate,subject:command.subject)
            lesson.addToLessonDetails(lessonDetails)
            lesson.weekNumber = lessonWeek.weekNumber
            lesson.weekStartDate = lessonWeek.startDate
            lesson.weekEndDate = lessonWeek.endDate
        }

        String message ="Lesson Added successfully."
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        if(lesson.hasErrors() || !lesson.save()){
            def errorList = lesson?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            message = errorList?.join('\n')
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
        }
        result.put(CommonUtils.MESSAGE, message)
        outPut = result as JSON
        render outPut

    }

    
    def delete(Long id, Long lessonDetailId) {

        LinkedHashMap result = new LinkedHashMap()
        String outPut
        Lesson lesson1 = Lesson.get(id)
        if(!lesson1){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_ERROR_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        if(lesson1.lessonDetails.size()==1){
            lesson1.delete(flush: true)
            result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
            result.put(CommonUtils.MESSAGE, "Lesson deleted successfully.")
            outPut = result as JSON
            render outPut
            return
        }
        LessonDetails lessonDetails= lesson1.lessonDetails.find { it.id == lessonDetailId}
        if(lessonDetails){
            lesson1.removeFromLessonDetails(lessonDetails)
            lessonDetails.delete()
        }
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, "Lesson deleted successfully.")
        outPut = result as JSON
        render outPut
        return
    }

    def edit(Long id) {
        LessonDetails lessonDetails = LessonDetails.read(id)
        if (!lessonDetails) {
            redirect(controller: 'lesson',action: 'index')
            return
        }
        Lesson lessonObj = lessonDetails?.lesson
        Section section = lessonObj.section
        ClassName className = section.className
        def subjectList = classSubjectsService.subjectDropDownList(className)
        render(view: '/teacher/lesson/editLesson',model: [section: section, subjectList:subjectList, lessonObj:lessonObj,lessonDetails:lessonDetails])
    }

    
    def create(Long id) {
        Section section = Section.read(id)
        if (!section) {
            redirect(controller: 'lesson', action: 'index')
            return
        }
        ExamTerm examTerm = null
        if(params.examTerm){
            examTerm = ExamTerm.valueOf(params.examTerm)
        }
        SubjectName subject
        if(params.subject){
            subject = SubjectName.read(params.getLong('subject'))
        }
        Date lessonDate = new Date()
        if (params.lessonDate) {
            lessonDate = params.date('lessonDate','dd/MM/yyyy')
        }
        def principal = springSecurityService.principal
        ClassName className = section.className
        Employee workingTeacher=employeeService.findByEmpId(principal?.userRef)
        def subjectList = classSubjectsService.subjectDropDownList(className)
        def sectionList = sectionService.sectionDropDownList()
        def result = lessonService.todayWorkingLessonList(section, principal?.username)
        render(view: '/teacher/lesson/createLesson', model: [workingTeacher :workingTeacher, section: section, subject: subject, lessonList: result, subjectList: subjectList,examTerm:examTerm, sectionList:sectionList, lessonDate: lessonDate])
    }


    def update(LessonUpdateCommand command) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        if (command.hasErrors()) {
            def errorList = command?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, errorList?.join('\n'))
            outPut = result as JSON
            render outPut
            return
        }
        Lesson lesson1 = Lesson.get(command.lessonId)
        if(!lesson1){
            result.put(CommonUtils.IS_ERROR, true)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_ERROR_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        LessonWeek lessonWeek = lessonService.getWeek(command.lessonDate)
        if(!lessonWeek){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "Lesson Date is not valid (Sun - Thu) or week not initiated. Please contact admin.")
            outPut = result as JSON
            render outPut
            return
        }

        def alreadyAdded = lesson1.lessonDetails.findAll {it.id != command.lessonDetailId && it.subject == command.subject}
        if(alreadyAdded){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "${command.subject.name} Lesson already added. Please edit lesson if require.")
            outPut = result as JSON
            render outPut
            return
        }
        def principal = springSecurityService.principal
        String username = principal?.username
        LessonDetails oldDetail
        if(lesson1.lessonDate.clearTime()==command.lessonDate.clearTime()){
            oldDetail= lesson1.lessonDetails.find { it.id == command.lessonDetailId}
            if(oldDetail){
                lesson1.removeFromLessonDetails(oldDetail)
                oldDetail.delete()
            }
            oldDetail = new LessonDetails(employee:command.employee,topics:command.topics,homeWork:command.homeWork,examDate:command.examDate,subject:command.subject)
            lesson1.addToLessonDetails(oldDetail)
            lesson1.updatedBy=username
            String message ="Lesson Updated successfully."
            result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
            if(lesson1.hasErrors() || !lesson1.save()){
                def errorList = lesson1?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
                message = errorList?.join('\n')
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            }
            result.put(CommonUtils.MESSAGE, message)
            outPut = result as JSON
            render outPut
            return
        }
        Section section = lesson1.section
        if(lesson1.lessonDetails.size()==1){
            lesson1.delete()
        }else {
            oldDetail= lesson1.lessonDetails.find { it.id == command.lessonDetailId}
            if(oldDetail){
                lesson1.removeFromLessonDetails(oldDetail)
                oldDetail.delete()
                lesson1.updatedBy=username
                lesson1.save()
            }
        }

        LessonDetails lessonDetails
        Lesson lesson = lessonService.getLesson(section, command.lessonDate)
        if(lesson){
            oldDetail = lesson.lessonDetails.findAll {it.subject == command.subject}
            if(oldDetail){
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, "Lesson already added. Please edit lesson if require.")
                outPut = result as JSON
                render outPut
                return
            }
            lessonDetails = new LessonDetails(employee:command.employee,topics:command.topics,homeWork:command.homeWork,examDate:command.examDate,subject:command.subject)
            lesson.addToLessonDetails(lessonDetails)
        }else {
            lesson = new Lesson(examTerm:command.examTerm, section:section,lessonDate:command.lessonDate)
            lesson.weekNumber = lessonWeek.weekNumber
            lesson.weekStartDate = lessonWeek.startDate
            lesson.weekEndDate = lessonWeek.endDate

            lessonDetails = new LessonDetails(employee:command.employee,topics:command.topics,homeWork:command.homeWork,examDate:command.examDate,subject:command.subject)
            lesson.addToLessonDetails(lessonDetails)
        }

        lesson.updatedBy=username
        String message ="Lesson Added successfully."
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        if(lesson.hasErrors() || !lesson.save()){
            def errorList = lesson?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            message = errorList?.join('\n')
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
        }
        result.put(CommonUtils.MESSAGE, message)
        outPut = result as JSON
        render outPut
    }

    
    def lessonPlan(Long id) {
        Section section = Section.read(id)
        if (!section) {
            redirect(controller: 'lesson', action: 'index')
            return
        }
        def lessonWeekList = lessonService.lessonWeeksList(section)
        Integer loadWeek = null
        if(params.weekNo){
            loadWeek = params.getInt("weekNo")
        } else {
            LocalDate toDay = new LocalDate()
            if(toDay.getDayOfWeek()==DateTimeConstants.FRIDAY){
                toDay = toDay.minusDays(1)
            }else if(toDay.getDayOfWeek()==DateTimeConstants.SATURDAY){
                toDay = toDay.minusDays(2)
            }
            LessonWeek lessonWeek=lessonService.getWeek(toDay.toDate())
            if (lessonWeek) {
                loadWeek = lessonWeek.weekNumber
            } else {
                def lastWeek = lessonWeekList
                if (lastWeek) {
                    loadWeek = lastWeek.first().id
                } else {
                    loadWeek = 1
                }
            }
        }
        SubjectName loadSubject = null
        if(params.subjectId){
            loadSubject = SubjectName.read(params.getLong('subjectId'))
        }
        ClassName className = section.className
        def subjectList = classSubjectsService.subjectDropDownList(className)
        def result = lessonService.lessonPaginateList(section, loadWeek)

        render(view: '/teacher/lesson/lessonList', model: [section: section, lessonList: result, subjectList: subjectList,loadSubject:loadSubject,lessonWeekList:lessonWeekList,loadWeek:loadWeek])
    }


}


