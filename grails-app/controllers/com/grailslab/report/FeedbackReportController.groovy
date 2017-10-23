package com.grailslab.report

import com.grailslab.CommonUtils
import com.grailslab.command.report.AverageFeedbackCommand
import com.grailslab.command.report.FeedbackByStudentCommand
import com.grailslab.command.report.StudentFeedbackCommand
import com.grailslab.command.report.SubWiseFeedbackCommand
import com.grailslab.enums.PrintOptionType
import com.grailslab.settings.LessonWeek
import com.grailslab.stmgmt.Student
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef

class FeedbackReportController {
    def classNameService
    def schoolService
    def jasperService

    private static final String FEEDBACK_REPORT_JASPER_FILE = 'subjectWiseFeedback.jasper'
    private static final String STUDENT_RATING_REPORT_JASPER_FILE = 'studentsFeedback.jasper'
    private static final String AVERAGE_REPORT_JASPER_FILE = 'averageFeedback.jasper'
    private static final String STUDENT_FEEDBACK_REPORT_JASPER_FILE = 'feedbackByStudent.jasper'
    def index() {
        render (view: '/common/dashboard', layout:'moduleLessonAndFeedbackLayout')
    }
    def report(){
        def classNameList = classNameService.classNameDropDownList();
        render (view: '/teacher/lesson/feedbackReport', model: [classNameList: classNameList])
    }

    def subjectFeedback(SubWiseFeedbackCommand command) {

        String sqlParam = " lfd.active_status = 'ACTIVE' and std.active_status = 'ACTIVE' and sub.active_status = 'ACTIVE' and cls.active_status =  'ACTIVE' and sec.active_status = 'ACTIVE' and lfedbk.active_status = 'ACTIVE' and lw.active_status = 'ACTIVE'"
       String reportFileName = schoolService.reportFileName(CommonUtils.MODULE_FEEDBACK, FEEDBACK_REPORT_JASPER_FILE)
        if (command.className) {
            sqlParam += " and cls.id='"+ command.className +"' "
        }
        if (command.sectionName) {
            sqlParam += " and sec.id='"+ command.sectionName +"' "
        }
        if (command.subjectName) {
            sqlParam += " and sub.id ='"+ command.subjectName +"' "
        }
        if (command.weekNo) {
            sqlParam += " and lw.week_number ='"+ command.weekNo +"' "
        }
        if (command.sortedBy) {
            sqlParam += " order by "+ command.sortedBy+";"
        }
        Map paramsMap = new LinkedHashMap()
        paramsMap.put('REPORT_LOGO', schoolService.reportLogoPath())
        paramsMap.put('sqlParam', sqlParam)

        String extension = CommonUtils.PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.PDF_FORMAT
        if(command.feedbackPrintType && command.feedbackPrintType !=PrintOptionType.PDF.key){
            if(command.feedbackPrintType==PrintOptionType.XLSX.key){
                extension = CommonUtils.XLSX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_XLSX
                exportFormat = JasperExportFormat.XLSX_FORMAT
            } else if(command.feedbackPrintType==PrintOptionType.DOCX.key){
                extension = CommonUtils.DOCX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_DOCX
                exportFormat = JasperExportFormat.DOCX_FORMAT
            }
        }
        String outputFileName = "subjectwise_feedback${extension}"
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
    def studentsFeedback(StudentFeedbackCommand command){
        String sqlParam = " std.active_status = 'ACTIVE' and cls.active_status =  'ACTIVE' and sec.active_status = 'ACTIVE' and lf.active_status = 'ACTIVE' and lw.active_status = 'ACTIVE'"
        String reportFileName = schoolService.reportFileName(CommonUtils.MODULE_FEEDBACK, STUDENT_RATING_REPORT_JASPER_FILE)
        if (command.stdClassName) {
            sqlParam += " and cls.id='"+ command.stdClassName +"' "
        }
        if (command.stdSectionName) {
            sqlParam += " and sec.id='"+ command.stdSectionName +"' "
        }
        if (command.stdWeekNo) {
            sqlParam += " and lw.week_number ='"+ command.stdWeekNo +"' "
        }
        if (command.stdRating) {
            sqlParam += " and lfa.average <='"+ command.stdRating +"' "
        }
        if (command.stdSortedBy) {
            sqlParam += " order by "+ command.stdSortedBy
        }
        LessonWeek lessonWeek = LessonWeek.findByWeekNumber(command.stdWeekNo)
        Map paramsMap = new LinkedHashMap()
        paramsMap.put('REPORT_LOGO', schoolService.reportLogoPath())
        paramsMap.put("weekStart", lessonWeek?.startDate)
        paramsMap.put("weekEnd", lessonWeek?.endDate)
        paramsMap.put('sqlParam', sqlParam)

        String extension = CommonUtils.PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.PDF_FORMAT
        if(command.stdPrintType && command.stdPrintType !=PrintOptionType.PDF.key){
            if(command.stdPrintType==PrintOptionType.XLSX.key){
                extension = CommonUtils.XLSX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_XLSX
                exportFormat = JasperExportFormat.XLSX_FORMAT
            } else if(command.stdPrintType==PrintOptionType.DOCX.key){
                extension = CommonUtils.DOCX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_DOCX
                exportFormat = JasperExportFormat.DOCX_FORMAT
            }
        }
        String outputFileName = "students_feedback${extension}"
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
    def averageFeedback(AverageFeedbackCommand command) {
        String sqlParam = " std.active_status = 'ACTIVE' and cn.active_status =  'ACTIVE' and sec.active_status = 'ACTIVE' and lf.active_status = 'ACTIVE' and lw.active_status = 'ACTIVE'"
        String reportFileName = schoolService.reportFileName(CommonUtils.MODULE_FEEDBACK, AVERAGE_REPORT_JASPER_FILE)
        if (command.avgClassName) {
            sqlParam += " and cn.id='"+ command.avgClassName +"' "
        }
        if (command.avgSectionName) {
            sqlParam += " and sec.id='"+ command.avgSectionName +"' "
        }
        if (command.avgWeekNo) {
            sqlParam += " and lw.week_number ='"+ command.avgWeekNo +"' "
        }
        Map paramsMap = new LinkedHashMap()
        paramsMap.put('REPORT_LOGO', schoolService.reportLogoPath())
        paramsMap.put('sqlParam', sqlParam)

        String extension = CommonUtils.PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.PDF_FORMAT
        if(command.avgPrintType && command.avgPrintType !=PrintOptionType.PDF.key){
            if(command.avgPrintType==PrintOptionType.XLSX.key){
                extension = CommonUtils.XLSX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_XLSX
                exportFormat = JasperExportFormat.XLSX_FORMAT
            } else if(command.avgPrintType==PrintOptionType.DOCX.key){
                extension = CommonUtils.DOCX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_DOCX
                exportFormat = JasperExportFormat.DOCX_FORMAT
            }
        }
        String outputFileName = "average_feedback${extension}"
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
    def feedbackByStudent(FeedbackByStudentCommand command) {
        Student student=Student.read(command.studentName)
        if(!student){
            flash.message="Student not found."
            redirect(action: 'index')
            return
        }
        String sqlParam = " lfd.active_status = 'ACTIVE' and sub.active_status = 'ACTIVE' and lfedbk.active_status = 'ACTIVE' and lw.active_status = 'ACTIVE'"
        String reportFileName = schoolService.reportFileName(CommonUtils.MODULE_FEEDBACK, STUDENT_FEEDBACK_REPORT_JASPER_FILE)
        if (command.studentName) {
            sqlParam += " and lfd.student_id='"+ student.id +"' "
        }
        if (command.stuSubjectName) {
            sqlParam += " and sub.id='"+ command.stuSubjectName +"' "
        }
        if (command.stuWeekNo) {
            sqlParam += " and lw.week_number ='"+ command.stuWeekNo +"' "
        }
        Map paramsMap = new LinkedHashMap()
        paramsMap.put('REPORT_LOGO', schoolService.reportLogoPath())
        paramsMap.put('sqlParam', sqlParam)
        paramsMap.put('studentName', student.name)
        paramsMap.put('studentClass', student.className?.name)
        paramsMap.put('studentSection', student.section?.name)
        paramsMap.put('studentRollNo', student.rollNo)
        paramsMap.put('stdId', student.studentID)

        String extension = CommonUtils.PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.PDF_FORMAT
        if(command.stuPrintType && command.stuPrintType !=PrintOptionType.PDF.key){
            if(command.stuPrintType==PrintOptionType.XLSX.key){
                extension = CommonUtils.XLSX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_XLSX
                exportFormat = JasperExportFormat.XLSX_FORMAT
            } else if(command.stuPrintType==PrintOptionType.DOCX.key){
                extension = CommonUtils.DOCX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_DOCX
                exportFormat = JasperExportFormat.DOCX_FORMAT
            }
        }
        String outputFileName = "feedback_by_student${extension}"
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
}
