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

class TeacherController {
    def employeeService
    def messageSource
    def uploadService
    def springSecurityService
    def classSubjectsService
    def classNameService
    def lessonService
    def studentSubjectsService
    def lessonFeedbackService
    def sectionService


    def index() {
        render (view: '/common/dashboard', layout:'adminLayout')
    }








}
