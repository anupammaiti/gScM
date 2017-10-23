package com.grailslab.admin

import com.grailslab.CommonUtils
import com.grailslab.command.LessonPlanCommand
import com.grailslab.settings.ClassName
import com.grailslab.settings.LessonWeek
import com.grailslab.settings.Section
import com.grailslab.settings.SubjectName
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.joda.time.DateTimeConstants
import org.joda.time.LocalDate

class LessonPlanController {
    def jasperService
    def messageSource
    def sectionService
    def lessonService
    def schoolService

    def index(Long id) {
        Section section = Section.read(id)
        if (!section) {
            render(view: 'lessonList')
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
                def lastWeek = lessonWeekList?.first()
                if (lastWeek) {
                    loadWeek = lastWeek.id
                } else {
                    loadWeek = 1
                }
            }
        }
        SubjectName loadSubject = null
        if(params.subjectId){
            loadSubject = SubjectName.read(params.getLong('subjectId'))
            if(!loadSubject){
                redirect(action: 'index')
                return
            }
        }
        ClassName className = section.className
        def subjectList = classSubjectsService.subjectDropDownList(className)
        def result = lessonService.lessonPaginateList(section, loadWeek)

        render(view: 'lessonList', model: [section: section, lessonList: result, subjectList: subjectList,loadSubject:loadSubject,lessonWeekList:lessonWeekList,loadWeek:loadWeek])
    }

    def download(LessonPlanCommand command) {
        if (command.hasErrors()) {
            def errorList = command?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            flash.message = errorList?.join('. ')
            render(view: '/baseReport/reportNotFound')
            return
        }

        Integer weekNum = command.weekNumber
        if(!weekNum){
            LocalDate toDay = new LocalDate()
            if(toDay.getDayOfWeek()==DateTimeConstants.FRIDAY){
                toDay = toDay.minusDays(1)
            }else if(toDay.getDayOfWeek()==DateTimeConstants.SATURDAY){
                toDay = toDay.minusDays(2)
            }

            LessonWeek lessonWeek=lessonService.getWeek(toDay.toDate())
            weekNum=lessonWeek?lessonWeek.weekNumber:1
        }
        String sqlParam = " lesson.section_id=${command.section?.id}"
        if(command.weekNumber){
            sqlParam+=" and lesson.week_number=${command.weekNumber}"
        }else {
            sqlParam+=" and lesson.week_number>=${weekNum}"
        }
        if(command.subject){
            sqlParam+=" and subject.id=${command.subject?.id}"
        }
        String reportFileName = LESSON_PLAN_JASPER_FILE
        String reportLogo = schoolService.reportLogoPath()
        Section section = command.section
        Map paramsMap = new LinkedHashMap()
        paramsMap.put('REPORT_LOGO', reportLogo)
        paramsMap.put('sqlParam', sqlParam)
        paramsMap.put('className', section.className.name)
        paramsMap.put('sectionName', section.name)
        paramsMap.put('sectionId', section.id)

        String extension = CommonUtils.DOCX_EXTENSION   // PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_DOCX   // REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.DOCX_FORMAT         //PDF_FORMAT

        String outputFileName = "lesson_plan_${command.section?.name}${extension}"
        JasperReportDef reportDef = new JasperReportDef(
                name: reportFileName,
                fileFormat: exportFormat,
                parameters: paramsMap
        )

        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        response.contentType =grailsApplication.config.grails.mime.types[mimeType]
        response.setHeader("Content-disposition", "inline;filename=${outputFileName}")
        response.outputStream << report.toByteArray()
    }

    //lesson Plan
    private static final String LESSON_PLAN_JASPER_FILE = 'lessonPlan.jasper'
}
