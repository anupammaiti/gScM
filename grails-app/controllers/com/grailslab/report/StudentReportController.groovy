package com.grailslab.report

import com.grailslab.CommonUtils
import com.grailslab.command.report.*
import com.grailslab.enums.PrintOptionType
import com.grailslab.enums.StudentStatus
import com.grailslab.gschoolcore.AcademicYear
import com.grailslab.settings.ClassName
import com.grailslab.settings.School
import com.grailslab.settings.Section
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef

class StudentReportController {
    def messageSource
    def schoolService
    def sectionService
    def jasperService
    def classSubjectsService

    def studentList(StudentListCommand command){
        if (command.hasErrors()) {
            def errorList = command?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            flash.message =errorList?.join('\n')
            render(view: '/baseReport/reportNotFound')
            return
        }
        School workSchool = schoolService.defaultSchool()
        AcademicYear academicYear = command.academicYear?:workSchool.academicYear

        String sectionIds
        List sectionIdList
        def classSections
        if(command.section){
            sectionIds = command.section.id.toString()
        } else if (command.className){
            classSections = sectionService.classSections(command.className)
            sectionIdList = classSections.collect {it.id}
            if (sectionIdList.size()>0){
                sectionIds = sectionIdList.join(',')
            }
        }else {
            classSections = sectionService.allSections(params)
            sectionIdList = classSections.collect {it.id}
            if (sectionIdList.size()>0){
                sectionIds = sectionIdList.join(',')
            }
        }
        if (!sectionIds) {
            sectionIds = "0"
        }
        StudentStatus studentStatus = command.studentStatus?:StudentStatus.NEW


        String reportFileName = STUDENT_LIST_JASPER_FILE
       /* if(params.reportType=="BDAY"){
            reportFileName = STUDENT_BDAY_LIST_JASPER_FILE
        }*/
        Map paramsMap = new LinkedHashMap()
        paramsMap.put('schoolName', message(code: "app.school.name"))
        paramsMap.put('schoolAddress', message(code: "app.report.address.line1"))
        paramsMap.put('creditLine', message(code: "company.credit.footer"))
        paramsMap.put('REPORT_LOGO', schoolService.reportLogoPath())
        paramsMap.put('schoolId', workSchool.id)
        paramsMap.put('academicYear', academicYear.key)
        paramsMap.put('year', academicYear.value)
        paramsMap.put('studentStatus', studentStatus.key)
        paramsMap.put('sectionIDs', sectionIds)

        String extension = CommonUtils.PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.PDF_FORMAT

        if(command.printOptionType && command.printOptionType !=PrintOptionType.PDF){
            if(command.printOptionType==PrintOptionType.XLSX){
                extension = CommonUtils.XLSX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_XLSX
                exportFormat = JasperExportFormat.XLSX_FORMAT
            } else if(command.printOptionType==PrintOptionType.DOCX){
                extension = CommonUtils.DOCX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_DOCX
                exportFormat = JasperExportFormat.DOCX_FORMAT
            }
        }

        String outputFileName = "Student_LIST${extension}"
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

    def studentSingleList(StudentSingleListCommand command){
        if (command.hasErrors()) {
            def errorList = command?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            flash.message =errorList?.join('\n')
            render(view: '/baseReport/reportNotFound')
            return
        }
        School workSchool = schoolService.defaultSchool()
        AcademicYear academicYear = command.academicYear?:workSchool.academicYear

        String sectionIds
        List sectionIdList
        def classSections
        if(command.section){
            sectionIds = command.section.id.toString()
        } else if (command.className){
            classSections = sectionService.classSections(command.className)
            sectionIdList = classSections.collect {it.id}
            if (sectionIdList.size()>0){
                sectionIds = sectionIdList.join(',')
            }
        }else {
            classSections = sectionService.allSections(params)
            sectionIdList = classSections.collect {it.id}
            if (sectionIdList.size()>0){
                sectionIds = sectionIdList.join(',')
            }
        }
        if (!sectionIds) {
            sectionIds = "0"
        }
        StudentStatus studentStatus = command.studentStatus?:StudentStatus.NEW


        String reportFileName = STUDENT_SINGLE_PAGE_JASPER_FILE
        /* if(params.reportType=="BDAY"){
             reportFileName = STUDENT_BDAY_LIST_JASPER_FILE
         }*/
        Map paramsMap = new LinkedHashMap()
        paramsMap.put('schoolName', message(code: "app.school.name"))
        paramsMap.put('schoolAddress', message(code: "app.report.address.line1"))
        paramsMap.put('creditLine', message(code: "company.credit.footer"))
        paramsMap.put('REPORT_LOGO', schoolService.reportLogoPath())
        paramsMap.put('schoolId', workSchool.id)
        paramsMap.put('academicYear', academicYear.key)
        paramsMap.put('year', academicYear.value)
        paramsMap.put('studentStatus', studentStatus.key)
        paramsMap.put('sectionIDs', sectionIds)

        String extension = CommonUtils.PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.PDF_FORMAT

        if(command.printOptionType && command.printOptionType !=PrintOptionType.PDF){
            if(command.printOptionType==PrintOptionType.XLSX){
                extension = CommonUtils.XLSX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_XLSX
                exportFormat = JasperExportFormat.XLSX_FORMAT
            } else if(command.printOptionType==PrintOptionType.DOCX){
                extension = CommonUtils.DOCX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_DOCX
                exportFormat = JasperExportFormat.DOCX_FORMAT
            }
        }

        String outputFileName = "Student_LIST${extension}"
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
    def studentDynamicList(StudentDynamicListCommand command){
        if (command.hasErrors()) {
            def errorList = command?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            flash.message =errorList?.join('\n')
            render(view: '/baseReport/reportNotFound')
            return
        }
        AcademicYear academicYear = command.academicYear ?: schoolService.workingYear()
        String sqlParam = ' and std.academic_year = "' + academicYear.key + '"'
        if (command.className) {
            sqlParam += ' and cName.id=' + command.className.id
        }
        if (command.section) {
            sqlParam += ' and sec.id=' + command.section.id
        }
        if (command.gender) {
            sqlParam += ' and regis.gender = "' + command.gender.key + '"'
        }
        if (command.religion) {
            sqlParam += ' and regis.religion = "' + command.religion.key + '"'
        }
        String reportFileName = STUDENT_DYNAMIC_LIST_JASPER_FILE
        Map paramsMap = new LinkedHashMap()
        paramsMap.put('schoolName', message(code: "app.school.name"))
        paramsMap.put('schoolAddress', message(code: "app.report.address.line1"))
        paramsMap.put('creditLine', message(code: "company.credit.footer"))
        paramsMap.put('REPORT_LOGO', schoolService.reportLogoPath())
        paramsMap.put('academicYear', academicYear.key)
        paramsMap.put('year', academicYear.value)
        paramsMap.put('sqlParam', sqlParam)

        String extension = CommonUtils.PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.PDF_FORMAT

        if(command.printOptionType && command.printOptionType !=PrintOptionType.PDF){
            if(command.printOptionType==PrintOptionType.XLSX){
                extension = CommonUtils.XLSX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_XLSX
                exportFormat = JasperExportFormat.XLSX_FORMAT
            } else if(command.printOptionType==PrintOptionType.DOCX){
                extension = CommonUtils.DOCX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_DOCX
                exportFormat = JasperExportFormat.DOCX_FORMAT
            }
        }

        String outputFileName = "Student_LIST${extension}"
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
    def studentContactList(StudentContactListCommand command){
        if (command.hasErrors()) {
            def errorList = command?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            flash.message =errorList?.join('\n')
            render(view: '/baseReport/reportNotFound')
            return
        }

        AcademicYear academicYear = command.academicYear ?: schoolService.workingYear()
        String sqlParam = ""
        if (command.className) {
            sqlParam += ' and cName.id= ' + command.className.id
        }
        if (command.section) {
            sqlParam += ' and sec.id= ' + command.section.id
        }

        String reportFileName
        if (command.reportType == 'ADDRESS'){
            reportFileName = STUDENT_ADDRESS_LIST_JASPER_FILE
        } else if (command.reportType == 'INVAILD'){
            reportFileName = STUDENT_CONTACT_LIST_JASPER_FILE
        } else {
            reportFileName = STUDENT_CONTACT_LIST_JASPER_FILE
        }
        Map paramsMap = new LinkedHashMap()
        paramsMap.put('schoolName', message(code: "app.school.name"))
        paramsMap.put('schoolAddress', message(code: "app.report.address.line1"))
        paramsMap.put('creditLine', message(code: "company.credit.footer"))
        paramsMap.put('REPORT_LOGO', schoolService.reportLogoPath())
        paramsMap.put('year', academicYear.value)
        paramsMap.put('academicYear', academicYear.key)
        paramsMap.put('sqlParam', sqlParam)

        String extension = CommonUtils.PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.PDF_FORMAT

        if(command.printOptionType && command.printOptionType !=PrintOptionType.PDF){
            if(command.printOptionType==PrintOptionType.XLSX){
                extension = CommonUtils.XLSX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_XLSX
                exportFormat = JasperExportFormat.XLSX_FORMAT
            } else if(command.printOptionType==PrintOptionType.DOCX){
                extension = CommonUtils.DOCX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_DOCX
                exportFormat = JasperExportFormat.DOCX_FORMAT
            }
        }

        String outputFileName = "Student_LIST${extension}"
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

    def studentBirthDayList(StudentBirthDayListCommand command){
        if (command.hasErrors()) {
            def errorList = command?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            flash.message =errorList?.join('\n')
            render(view: '/baseReport/reportNotFound')
            return
        }
        AcademicYear academicYear = command.academicYear ?: schoolService.workingYear()
        String sqlParam = ' and std.academic_year = "' + academicYear.key + '"'
        if (command.className) {
            sqlParam += ' and cName.id=' + command.className.id
        }
        if (command.section) {
            sqlParam += ' and sec.id=' + command.section.id
        }

        String reportFileName = STUDENT_BDAY_LIST_JASPER_FILE
        Map paramsMap = new LinkedHashMap()
        paramsMap.put('schoolName', message(code: "app.school.name"))
        paramsMap.put('schoolAddress', message(code: "app.report.address.line1"))
        paramsMap.put('creditLine', message(code: "company.credit.footer"))
        paramsMap.put('REPORT_LOGO', schoolService.reportLogoPath())
        paramsMap.put('academicYear', academicYear.key)
        paramsMap.put('year', academicYear.value)
        paramsMap.put('sqlParam', sqlParam)

        String extension = CommonUtils.PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.PDF_FORMAT

        if(command.printOptionType && command.printOptionType !=PrintOptionType.PDF){
            if(command.printOptionType==PrintOptionType.XLSX){
                extension = CommonUtils.XLSX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_XLSX
                exportFormat = JasperExportFormat.XLSX_FORMAT
            } else if(command.printOptionType==PrintOptionType.DOCX){
                extension = CommonUtils.DOCX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_DOCX
                exportFormat = JasperExportFormat.DOCX_FORMAT
            }
        }

        String outputFileName = "Student_LIST${extension}"
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
    def classSubject(Long id){
        if (!id) {
            flash.message ="Class not found"
            render(view: '/baseReport/reportNotFound')
            return
        }
        ClassName className = ClassName.read(id)
        if (!className) {
            flash.message ="Class not found"
            render(view: '/baseReport/reportNotFound')
            return
        }

        String reportFileName = CLASS_SUBJECT_JASPER_FILE

        Map paramsMap = new LinkedHashMap()
        paramsMap.put('REPORT_LOGO', schoolService.reportLogoPath())
        paramsMap.put('clsName', className.name)
        paramsMap.put('classId', className.id)

        String extension = CommonUtils.PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.PDF_FORMAT

        /*if(command.printOptionType && command.printOptionType !=PrintOptionType.PDF){
            if(command.printOptionType==PrintOptionType.XLSX){
                extension = CommonUtils.XLSX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_XLSX
                exportFormat = JasperExportFormat.XLSX_FORMAT
            } else if(command.printOptionType==PrintOptionType.DOCX){
                extension = CommonUtils.DOCX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_DOCX
                exportFormat = JasperExportFormat.DOCX_FORMAT
            }
        }*/

        String outputFileName = "${className.name}_subject${extension}"
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
    def studentSubject(Long id){

        if (!id) {
            flash.message ="Section not found"
            render(view: '/baseReport/reportNotFound')
            return
        }
        Section section = Section.read(id)
        if (!section) {
            flash.message =CommonUtils.COMMON_NOT_FOUND_MESSAGE
            render(view: '/baseReport/reportNotFound')
            return
        }
        def compulsorySubjectStr = classSubjectsService.subjectTypeCompulsoryListStr(section.className, section.groupName)

        String reportFileName = STUDENT_SUBJECT_JASPER_FILE

        Map paramsMap = new LinkedHashMap()
        paramsMap.put('REPORT_LOGO', schoolService.reportLogoPath())
        paramsMap.put('sectionId', section.id)
        paramsMap.put('academicYear', schoolService.workingYear().key)
        paramsMap.put('compulsorySubjectStr', compulsorySubjectStr)
       //paramsMap.put('religion', )

        String extension = CommonUtils.PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.PDF_FORMAT

        /*if(command.printOptionType && command.printOptionType !=PrintOptionType.PDF){
            if(command.printOptionType==PrintOptionType.XLSX){
                extension = CommonUtils.XLSX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_XLSX
                exportFormat = JasperExportFormat.XLSX_FORMAT
            } else if(command.printOptionType==PrintOptionType.DOCX){
                extension = CommonUtils.DOCX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_DOCX
                exportFormat = JasperExportFormat.DOCX_FORMAT
            }
        }*/

        String outputFileName = "${section.name}_subject${extension}"
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

    def fesRegistration(Long id){
        if (!id) {
            flash.message ="Class not found"
            render(view: '/baseReport/reportNotFound')
            return
        }
        ClassName className = ClassName.read(id)
        if (!className) {
            flash.message ="Class not found"
            render(view: '/baseReport/reportNotFound')
            return
        }


        String reportFileName = FES_REGISTRATIION_LIST_JASPER_FILE

        Map paramsMap = new LinkedHashMap()
        paramsMap.put('REPORT_LOGO', schoolService.reportLogoPath())
        paramsMap.put('clsName', className.name)
        paramsMap.put('classId', className.id)


        String extension = CommonUtils.PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.PDF_FORMAT

        /*if(command.printOptionType && command.printOptionType !=PrintOptionType.PDF){
            if(command.printOptionType==PrintOptionType.XLSX){
                extension = CommonUtils.XLSX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_XLSX
                exportFormat = JasperExportFormat.XLSX_FORMAT
            } else if(command.printOptionType==PrintOptionType.DOCX){
                extension = CommonUtils.DOCX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_DOCX
                exportFormat = JasperExportFormat.DOCX_FORMAT
            }
        }*/

        String outputFileName = "${className.name}_subject${extension}"
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



    //student List Jasper report
    private static final String STUDENT_LIST_JASPER_FILE = 'studentList.jasper'
    private static final String STUDENT_SINGLE_PAGE_JASPER_FILE = 'studentSingleList.jasper'
    private static final String STUDENT_DYNAMIC_LIST_JASPER_FILE = 'studentDynamicList.jasper'
    private static final String STUDENT_CONTACT_LIST_JASPER_FILE = 'studentContactList.jasper'
    private static final String STUDENT_ADDRESS_LIST_JASPER_FILE = 'studentAddressList.jasper'
    private static final String STUDENT_BDAY_LIST_JASPER_FILE = 'studentListBirthDay.jasper'
    private static final String CLASS_SUBJECT_JASPER_FILE = 'classSubject.jasper'
    private static final String FES_REGISTRATIION_LIST_JASPER_FILE = 'fesRegistrationList.jasper'
    private static final String STUDENT_SUBJECT_JASPER_FILE = 'studentSubject.jasper'

    private static final String APPLICANT_FORM_LIST_REPORT_JASPER_FILE = 'applicantsFormReportList.jasper'// applicant list report
}
