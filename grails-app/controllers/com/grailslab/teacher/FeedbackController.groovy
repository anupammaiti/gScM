package com.grailslab.teacher

import com.grailslab.command.FeedbackCommand
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.settings.ClassName
import com.grailslab.settings.LessonWeek
import com.grailslab.settings.Section
import com.grailslab.settings.SubjectName
import com.grailslab.stmgmt.LessonFeedback
import com.grailslab.stmgmt.LessonFeedbackDetail
import com.grailslab.stmgmt.Student
import grails.converters.JSON

class FeedbackController {
    def classNameService
    def lessonFeedbackService
    def sectionService
    def classSubjectsService
    def lessonService
    def studentSubjectsService
    def messageSource

    def index() {
        def classNameList = classNameService.classNameDropDownList()
        render (view: '/teacher/lesson/feedback', model: [classNameList: classNameList])
    }
    def list(){
        LinkedHashMap gridData
        String result
        LinkedHashMap resultMap = lessonFeedbackService.feedbaclDetailList(params)

        if (!resultMap || resultMap.totalCount == 0) {
            gridData = [iTotalRecords: 0, iTotalDisplayRecords: 0, aaData: []]
            result = gridData as JSON
            render result
            return
        }
        int totalCount = resultMap.totalCount
        gridData = [iTotalRecords: totalCount, iTotalDisplayRecords: totalCount, aaData: resultMap.results]
        result = gridData as JSON
        render result
    }
    def addFeedback(){
        def classNameList = classNameService.classNameDropDownList()
        if (!params.className) {
            render (view: '/teacher/lesson/addFeedback', model: [classNameList: classNameList, feedBackList: null])
            return
        }
        if (!params.section || !params.subject || !params.weekNo){
            flash.message ="Please select Subject and weekNo for feedback"
            render (view: '/teacher/lesson/addFeedback', model: [classNameList: classNameList, feedBackList: null])
            return
        }
        def sectionList
        ClassName className = ClassName.read(params.getLong('className'))
        if (className) {
            sectionList = sectionService.classSectionsDDList(className)
        }
        Section section = Section.read(params.getLong('section'))

        def subjectNameList
        if (section) {
            subjectNameList = classSubjectsService.subjectDropDownList(className, section.groupName)
        }
        SubjectName subjectName = SubjectName.read(params.getLong('subject'))
        LessonWeek lessonWeek = LessonWeek.findByWeekNumberAndAcademicYearAndActiveStatus(params.getLong('weekNo'), section.academicYear, ActiveStatus.ACTIVE)

        def lessonList
        LessonFeedback lessonFeedback
        def subjectStdDetailList
        def lessonWeekList
        if (lessonWeek && subjectName) {
            lessonWeekList = lessonService.lessonWeekListForFeedback(section)
            lessonList = lessonService.lessonPlanForFeedback(section, lessonWeek.weekNumber)
            lessonFeedback = LessonFeedback.findBySectionAndLessonWeekAndActiveStatus(section, lessonWeek, ActiveStatus.ACTIVE)
            subjectStdDetailList = lessonFeedbackService.getFeedbackDetail(lessonFeedback, subjectName)
        }
        if (!subjectStdDetailList){
            subjectStdDetailList = studentSubjectsService.subjectStudentDetailList(section, subjectName)
        }
        render (view: '/teacher/lesson/addFeedback', model: [lessonList: lessonList, classNameList: classNameList, fclassName:className, sectionList:sectionList, fsectionName:section, subjectNameList:subjectNameList, fSubjectId: subjectName,fWeekNo: lessonWeek, lessonWeekList:lessonWeekList, feedBackList: subjectStdDetailList])
    }
    def saveFeedback(FeedbackCommand command) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        if (command.hasErrors()) {
            def errorList = command?.errors?.allErrors?.collect { messageSource.getMessage(it, null) }
            flash.message = errorList?.join('. ')
            render(view: '/teacher/lesson/addFeedback')
            return
        }
        def studentIdList = command.studentIds.split(',')
        Double rating
        String comment
        Student student
        ClassName className = command.fclassName
        Section section = command.fSectionId
        LessonWeek lessonWeek = command.fWeekNo
        SubjectName subjectName = command.fSubjectId
        LessonFeedback lessonFeedback = LessonFeedback.findBySectionAndLessonWeekAndActiveStatus(section, lessonWeek, ActiveStatus.ACTIVE)
        if (lessonFeedback) {
            if (LessonFeedbackDetail.countByFeedbackAndSubjectName(lessonFeedback, subjectName)== 0) {
                lessonFeedback.numOfSubject = lessonFeedback.numOfSubject+1
                lessonFeedback.save()
            }
        } else {
            lessonFeedback = new LessonFeedback(className: className, section: section, lessonWeek: lessonWeek, numOfSubject: 1).save()
        }
        studentIdList.each {String idx ->
            student = Student.read(Long.parseLong(idx))
            rating = params.getDouble("feedback${idx}")
            comment = params."comment${idx}"
            lessonFeedbackService.addRatingScore(lessonFeedback, student, subjectName, rating?:0, comment)
        }
        redirect(action: 'addFeedback')
    }
}
