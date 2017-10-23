package com.grailslab.report

import com.grailslab.CommonUtils
import com.grailslab.command.OnlineRegistrationRecordCommand
import com.grailslab.enums.ApplicantStatus
import com.grailslab.enums.PrintOptionType
import com.grailslab.gschoolcore.AcademicYear
import com.grailslab.settings.ClassName
import com.grailslab.stmgmt.RegForm
import com.grailslab.stmgmt.RegOnlineRegistration
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef

class OnlineRegistrationReportController {

    def messageSource
    def schoolService
    def jasperService
    def regAdmissionFormService
    def uploadService
    def sequenceGeneratorService

    def applicantFormReport(OnlineRegistrationRecordCommand command) {

        if (command.hasErrors()) {
            def errorList = command?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            flash.message =errorList?.join('\n')
            render(view: '/baseReport/reportNotFound')
            return
        }
        ApplicantStatus applicantStatus = command.applicantStatus
        AcademicYear academicYear = schoolService.schoolAdmissionYear()
        String reportFileName
        String reportLogo = schoolService.reportLogoPath()
        Map paramsMap = new LinkedHashMap()
        paramsMap.put('schoolName', message(code: "app.school.name"))
        paramsMap.put('schoolAddress', message(code: "app.report.address.line1"))
        paramsMap.put('creditLine', message(code: "company.credit.footer"))
        paramsMap.put('REPORT_LOGO', reportLogo)
        paramsMap.put('admissionYear',  academicYear.value)
        paramsMap.put('applicantStatus',  applicantStatus.value)

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

        String outputFileName = "ApplicantList${extension}"
        JasperReportDef reportDef

        Date fromDate = command.fromDate
        Date toDate = command.toDate
        ClassName className = command.className

        if (command.reportType != "applyCount"){
            if (command.reportType == "details") {
                reportFileName = schoolService.reportFileName(CommonUtils.MODULE_ONLINE, APPLICANT_DETAIL_LIST_REPORT_JASPER_FILE)
            } else if (command.reportType == 'short'){
                reportFileName = schoolService.reportFileName(CommonUtils.MODULE_ONLINE, APPLICANT_LIST_REPORT_JASPER_FILE)
            }
            List<RegOnlineRegistration> onlineApplicatList= regAdmissionFormService.regOnlineRegistrationList(className, applicantStatus, fromDate, toDate, academicYear)
            reportDef = new JasperReportDef(
                    name: reportFileName,
                    fileFormat: exportFormat,
                    parameters: paramsMap,
                    reportData: onlineApplicatList
            )
        } else {
            reportFileName = schoolService.reportFileName(CommonUtils.MODULE_ONLINE, APPLICANT_LIST_COUNT_REPORT_JASPER_FILE)
            String sqlParams = ' and reg.academic_year = "'+academicYear.key+'" and reg.applicant_status= "'+applicantStatus.key+'"'
            if (className) {
                sqlParams += ' and reg.class_name_id = '+className.id
            }
            paramsMap.put('sqlParams', sqlParams)

            reportDef = new JasperReportDef(
                    name: reportFileName,
                    fileFormat: exportFormat,
                    parameters: paramsMap
            )
        }

        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        response.contentType = grailsApplication.config.grails.mime.types[mimeType]
        response.setHeader("Content-disposition", "inline;filename=${outputFileName}")
        response.outputStream << report.toByteArray()
    }

    def collectionReport(OnlineRegistrationRecordCommand command) {

        if (command.hasErrors()) {
            def errorList = command?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            flash.message =errorList?.join('\n')
            render(view: '/baseReport/reportNotFound')
            return
        }
        Date fromDate = command.fromDate
        if (!fromDate) {
            fromDate = new Date()
        }

        Date toDate = command.toDate
        if (! toDate) {
            toDate = toDate
        }
        ApplicantStatus applicantStatus = command.applicantStatus
        AcademicYear academicYear = schoolService.schoolAdmissionYear()
        String reportFileName = schoolService.reportFileName(CommonUtils.MODULE_ONLINE, APPLICANT_COLLECTION_REPORT_JASPER_FILE)
        String reportLogo = schoolService.reportLogoPath()
        Map paramsMap = new LinkedHashMap()
        paramsMap.put('REPORT_LOGO', reportLogo)
        paramsMap.put('admissionYear',  academicYear.value)
        paramsMap.put('applicantStatus',  applicantStatus.value)
        paramsMap.put('reportDate',  CommonUtils.getDateRange(fromDate, toDate))

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

        String outputFileName = "ApplicantList${extension}"



        ClassName className = command.className

        List<RegOnlineRegistration> onlineApplicatList= regAdmissionFormService.regOnlineCollectionList(className, applicantStatus, fromDate.clearTime(), toDate.plus(1).clearTime(), academicYear)
        JasperReportDef reportDef  = new JasperReportDef(
                name: reportFileName,
                fileFormat: exportFormat,
                parameters: paramsMap,
                reportData: onlineApplicatList
        )

        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        response.contentType = grailsApplication.config.grails.mime.types[mimeType]
        response.setHeader("Content-disposition", "inline;filename=${outputFileName}")
        response.outputStream << report.toByteArray()
    }

    def printAdmit(String serialNo) {
        if (!serialNo) {
            redirect(controller: 'onlineRegistration', action: 'index')
            return
        }
        RegOnlineRegistration registration = RegOnlineRegistration.findBySerialNo(serialNo)
        if (!registration) {
            redirect(controller: 'onlineRegistration', action: 'index')
            return
        }
        RegForm regForm = RegForm.read(registration.regFormId)
        if (regForm && !registration.applyNo) {
            registration.applyNo = sequenceGeneratorService.nextNumber(regForm.applicationPrefix)
            registration.save(flush: true)
        }


       /* String reportFileName = ADMIT_COLLECTION_REPORT_JASPER_FILE*/

        String  reportFileName = schoolService.reportFileName(CommonUtils.MODULE_ONLINE, ADMIT_COLLECTION_REPORT_JASPER_FILE)
        String reportLogo = schoolService.reportLogoPath()

        Map paramsMap = new LinkedHashMap()
        paramsMap.put('REPORT_LOGO', reportLogo)
        String extension = CommonUtils.PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.PDF_FORMAT
        paramsMap.put('schoolName', message(code: "app.school.name"))
        paramsMap.put('schoolAddress', message(code: "app.report.address.line1"))
        paramsMap.put('serialNo', registration.serialNo)
        paramsMap.put('imageFile', uploadService.getImageInputStream(registration.imagePath))

        String outputFileName = "ApplicantList{extension}"
        JasperReportDef reportDef = new JasperReportDef(
                name: reportFileName,
                fileFormat: exportFormat,
                parameters: paramsMap

        )

        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        response.contentType = grailsApplication.config.grails.mime.types[mimeType]
        response.setHeader("Content-disposition", "inline;filename=${outputFileName}")
        response.outputStream << report.toByteArray()
    }

    def admitCard(Long regOnlineId) {
        if (!regOnlineId) {
            redirect(controller: 'onlineRegistration', action: 'index')
            return
        }

        RegOnlineRegistration registration = RegOnlineRegistration.read(regOnlineId)
        RegForm regForm = RegForm.read(registration.regFormId)
        if (!registration || !regForm) {
            redirect(controller: 'onlineRegistration', action: 'index')
            return
        }

        if (!registration.applyNo) {
            registration.feeAmount = regForm.formPrice
            registration.payment = regForm.formPrice
            registration.invoiceDate = new Date()
            registration.applyNo = sequenceGeneratorService.nextNumber(regForm.applicationPrefix)
            registration.applicantStatus = ApplicantStatus.AdmitCard
            registration.save(flush: true)
        }

        /* String reportFileName = ADMIT_COLLECTION_REPORT_JASPER_FILE*/

        String  reportFileName = schoolService.reportFileName(CommonUtils.MODULE_ONLINE, ADMIT_COLLECTION_REPORT_JASPER_FILE)
        String reportLogo = schoolService.reportLogoPath()

        Map paramsMap = new LinkedHashMap()
        paramsMap.put('REPORT_LOGO', reportLogo)
        String extension = CommonUtils.PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.PDF_FORMAT
        paramsMap.put('schoolName', message(code: "app.school.name"))
        paramsMap.put('schoolAddress', message(code: "app.report.address.line1"))
        paramsMap.put('serialNo', registration.serialNo)
        paramsMap.put('imageFile', uploadService.getImageInputStream(registration.imagePath))

        String outputFileName = "ApplicantList{extension}"
        JasperReportDef reportDef = new JasperReportDef(
                name: reportFileName,
                fileFormat: exportFormat,
                parameters: paramsMap

        )

        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        response.contentType = grailsApplication.config.grails.mime.types[mimeType]
        response.setHeader("Content-disposition", "inline;filename=${outputFileName}")
        response.outputStream << report.toByteArray()

    }


    //Applicant  jasper report

    private static final String ADMIT_COLLECTION_REPORT_JASPER_FILE = 'admitCard.jasper'
    private static final String ADMIT_ALL_COLLECTION_REPORT_JASPER_FILE = 'allAdmitCard.jasper'

    private static final String APPLICANT_LIST_REPORT_JASPER_FILE = 'applicantsList.jasper'// applicant list report
    private static final String APPLICANT_COLLECTION_REPORT_JASPER_FILE = 'applicantsColection.jasper'// applicant Collection report
    private static final String APPLICANT_DETAIL_LIST_REPORT_JASPER_FILE = 'applicantsDetailtList.jasper'// applicant list report
    private static final String APPLICANT_LIST_COUNT_REPORT_JASPER_FILE = 'applicantsCountList.jasper'// applicant count report
}
