package com.grailslab.report

import com.grailslab.CommonUtils
import com.grailslab.command.ExtraClassReportCommand
import com.grailslab.command.SalAttendanceReportCommand
import com.grailslab.enums.PrintOptionType
import com.grailslab.enums.YearMonths
import com.grailslab.gschoolcore.AcademicYear
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.hr.HrCategory
import com.grailslab.salary.BonusMaster
import com.grailslab.salary.SalMaster
import grails.converters.JSON
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef

class SalaryReportController {
    def classNameService
    def messageSource
    def schoolService
    def sectionService
    def jasperService
    def studentService
    def employeeService


    def index() {
        def hrCategoryList = HrCategory.findAllByActiveStatus(ActiveStatus.ACTIVE,[sort:'sortOrder'])
        render(view: '/salary/salaryReport/salarySetEmpReport',model: [hrCategoryList: hrCategoryList])
    }

    def setUp(){
        String reportFileName = schoolService.reportFileName(CommonUtils.MODULE_SALARY, EMP_SALARY_SET_UP_JASPER_FILE)

        String sqlParam = "emp.active_status='ACTIVE' and sal.active_status='ACTIVE'"
        if (params.hrCategory) {
            sqlParam += " and emp.hr_category_id = ${params.hrCategory}"
        }
        Map paramsMap = new LinkedHashMap()
        paramsMap.put('schoolName', message(code: "app.school.name"))
        paramsMap.put('schoolAddress', message(code: "app.report.address.line1"))
        paramsMap.put('creditLine', message(code: "company.credit.footer"))
        paramsMap.put('REPORT_LOGO', schoolService.reportLogoPath())
        paramsMap.put('sqlParam', sqlParam)

        String extension = CommonUtils.PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.PDF_FORMAT

        if(params.printOptionType && params.printOptionType !=PrintOptionType.PDF.key){
            if(params.printOptionType==PrintOptionType.XLSX.key){
                extension = CommonUtils.XLSX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_XLSX
                exportFormat = JasperExportFormat.XLSX_FORMAT
            } else if(params.printOptionType==PrintOptionType.DOCX.key){
                extension = CommonUtils.DOCX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_DOCX
                exportFormat = JasperExportFormat.DOCX_FORMAT
            }
        }

        String outputFileName = "salary_setup_data${extension}"
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

    def extraClass(ExtraClassReportCommand command){
        String reportFileName = schoolService.reportFileName(CommonUtils.MODULE_SALARY, EMP_SALARY_EXTRA_CLASS_JASPER_FILE)

        //show error if year and month not select

        String sqlParam = " emp.active_status='ACTIVE' and extra.active_status='ACTIVE' "

        if (command.academicYear) {
            sqlParam += " and extra.academic_year='${command.academicYear.key}' "
        }
        if (command.yearMonths) {
            sqlParam += " and extra.year_months='${command.yearMonths.key}' "
        }

        if (command.hrCategory) {
            sqlParam += " and emp.hr_category_id = ${command.hrCategory.id}"
        }
        Map paramsMap = new LinkedHashMap()
        paramsMap.put('REPORT_LOGO', schoolService.reportLogoPath())
        paramsMap.put('yearMonth', "${command.yearMonths.value}, ${command.academicYear.value}")
        paramsMap.put('schoolName', message(code: "app.school.name"))
        paramsMap.put('sqlParam', sqlParam)


        String extension = CommonUtils.PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.PDF_FORMAT

        if(params.printOptionType && params.printOptionType !=PrintOptionType.PDF.key){
            if(params.printOptionType==PrintOptionType.XLSX.key){
                extension = CommonUtils.XLSX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_XLSX
                exportFormat = JasperExportFormat.XLSX_FORMAT
            } else if(params.printOptionType==PrintOptionType.DOCX.key){
                extension = CommonUtils.DOCX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_DOCX
                exportFormat = JasperExportFormat.DOCX_FORMAT
            }
        }

        String outputFileName = "salary_setup_data${extension}"
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

    def attendance(SalAttendanceReportCommand command){
        String reportFileName = schoolService.reportFileName(CommonUtils.MODULE_SALARY, EMP_SALARY_ATTENDANCE_JASPER_FILE)

        String sqlParam = " emp.active_status='ACTIVE' "
        if (command.academicYear) {
            sqlParam += " and attn.academic_year='${command.academicYear.key}' "
        }
        if (command.yearMonths) {
            sqlParam += " and attn.year_months='${command.yearMonths.key}' "
        }
        if (command.hrCategory) {
            sqlParam += " and emp.hr_category_id = ${command.hrCategory.id}"
        }

        Map paramsMap = new LinkedHashMap()
        paramsMap.put('REPORT_LOGO', schoolService.reportLogoPath())
        paramsMap.put('yearMonth', "${command.yearMonths.value}, ${command.academicYear.value}")
        paramsMap.put('schoolName', message(code: "app.school.name"))
        paramsMap.put('sqlParam', sqlParam)


        String extension = CommonUtils.PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.PDF_FORMAT

        if(params.printOptionType && params.printOptionType !=PrintOptionType.PDF.key){
            if(params.printOptionType==PrintOptionType.XLSX.key){
                extension = CommonUtils.XLSX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_XLSX
                exportFormat = JasperExportFormat.XLSX_FORMAT
            } else if(params.printOptionType==PrintOptionType.DOCX.key){
                extension = CommonUtils.DOCX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_DOCX
                exportFormat = JasperExportFormat.DOCX_FORMAT
            }
        }

        String outputFileName = "salary_attn_data${extension}"
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

    def salarySheet(Long id){
        String reportFileName = schoolService.reportFileName(CommonUtils.MODULE_SALARY, EMP_SALARY_MASTER_JASPER_FILE)
        SalMaster salMaster = SalMaster.read(id)
        if (!salMaster) {
            redirect(controller: 'salarySetup', action: 'salMasterSetup')
            return
        }
        String monthName = salMaster.yearMonths.value
        String year = salMaster.academicYear.value

        Map paramsMap = new LinkedHashMap()
        paramsMap.put('REPORT_LOGO', schoolService.reportLogoPath())
        paramsMap.put('yearMonth', "$monthName, $year")
        paramsMap.put('schoolName', message(code: "app.school.name"))
        paramsMap.put('schoolHead', message(code: "app.school.head"))
        paramsMap.put('schoolAddress', message(code: "app.report.address.line1"))
        paramsMap.put('creditLine', message(code: "company.credit.footer"))
        paramsMap.put('salMasterid', id)
        paramsMap.put('footNote', salMaster?.footNote)

        String extension = CommonUtils.PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.PDF_FORMAT

        if(params.printOptionType && params.printOptionType !=PrintOptionType.PDF.key){
            if(params.printOptionType==PrintOptionType.XLSX.key){
                extension = CommonUtils.XLSX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_XLSX
                exportFormat = JasperExportFormat.XLSX_FORMAT
            } else if(params.printOptionType==PrintOptionType.DOCX.key){
                extension = CommonUtils.DOCX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_DOCX
                exportFormat = JasperExportFormat.DOCX_FORMAT
            }
        }

        String outputFileName = "salary_master_data${extension}"
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
    def bonusReport(Long id){
        BonusMaster bonusMaster = BonusMaster.get(id)
        if (!bonusMaster) {
            redirect(controller: 'salarySetup', action: 'salMasterSetup')
            return
        }

        String reportFileName = schoolService.reportFileName(CommonUtils.MODULE_SALARY, 'salBonusStatement')
        if(params.statementType == 'bankStatement'){
            reportFileName = schoolService.reportFileName(CommonUtils.MODULE_SALARY, 'salBonusBankStatement')
        }
        Map paramsMap = new LinkedHashMap()
        paramsMap.put('masterId', bonusMaster.id)
        paramsMap.put('festivalName', bonusMaster.festivalName)
        paramsMap.put('academicYear', bonusMaster.academicYear)
        paramsMap.put('bonusPercentage', bonusMaster.bonusPercentage)
        paramsMap.put('schoolName', message(code: "app.school.name"))
        paramsMap.put('schoolAddress', message(code: "app.report.address.line1"))
        paramsMap.put('creditLine', message(code: "company.credit.footer"))
        paramsMap.put('REPORT_LOGO', schoolService.reportLogoPath())

        String extension = CommonUtils.PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.PDF_FORMAT

        if(params.printOptionType && params.printOptionType !=PrintOptionType.PDF.key){
            if(params.printOptionType==PrintOptionType.XLSX.key){
                extension = CommonUtils.XLSX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_XLSX
                exportFormat = JasperExportFormat.XLSX_FORMAT
            } else if(params.printOptionType==PrintOptionType.DOCX.key){
                extension = CommonUtils.DOCX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_DOCX
                exportFormat = JasperExportFormat.DOCX_FORMAT
            }
        }

        String outputFileName = "bonus_bank_statement${extension}"
        JasperReportDef reportDef = new JasperReportDef(
                name: reportFileName,
                fileFormat: exportFormat,
                parameters: paramsMap
        )
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        response.contentType =grailsApplication.config.grails.mime.types[mimeType]
        response.setHeader("Content-disposition", "inline;filename=${outputFileName}")
        response.outputStream << report.toByteArray()
        return
    }
    def payslip(Long id){
        String reportFileName = schoolService.reportFileName(CommonUtils.MODULE_SALARY, EMP_SALARY_Payslip_JASPER_FILE)
        SalMaster salMaster = SalMaster.read(id)
        if (!salMaster) {
            redirect(controller: 'salarySetup', action: 'salMasterSetup')
            return
        }
        String monthName = salMaster.yearMonths.value
        String year = salMaster.academicYear.value

        Map paramsMap = new LinkedHashMap()
        paramsMap.put('REPORT_LOGO', schoolService.reportLogoPath())
        paramsMap.put('yearMonth', "$monthName, $year")
        paramsMap.put('schoolName', message(code: "app.school.name"))
        paramsMap.put('schoolAddress', message(code: "app.report.address.line1"))
        paramsMap.put('salMasterid', id)

        String extension = CommonUtils.PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.PDF_FORMAT

        if(params.printOptionType && params.printOptionType !=PrintOptionType.PDF.key){
            if(params.printOptionType==PrintOptionType.XLSX.key){
                extension = CommonUtils.XLSX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_XLSX
                exportFormat = JasperExportFormat.XLSX_FORMAT
            } else if(params.printOptionType==PrintOptionType.DOCX.key){
                extension = CommonUtils.DOCX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_DOCX
                exportFormat = JasperExportFormat.DOCX_FORMAT
            }
        }

        String outputFileName = "salary_master_data${extension}"
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
    def bankStatement(Long id){
        String reportFileName = schoolService.reportFileName(CommonUtils.MODULE_SALARY, EMP_SALARY_BANK_STATEMENT_JASPER_FILE)
        SalMaster salMaster = SalMaster.read(id)
        if (!salMaster) {
            redirect(controller: 'salarySetup', action: 'salMasterSetup')
            return
        }
        String monthName = salMaster.yearMonths.value
        String year = salMaster.academicYear.value

        Map paramsMap = new LinkedHashMap()
        paramsMap.put('REPORT_LOGO', schoolService.reportLogoPath())
        paramsMap.put('creditLine', message(code: "company.credit.footer"))
        paramsMap.put('yearMonth', "$monthName, $year")
        paramsMap.put('schoolName', message(code: "app.school.name"))
        paramsMap.put('schoolHead', message(code: "app.school.head"))
        paramsMap.put('schoolAddress', message(code: "app.report.address.line1"))
        paramsMap.put('salMasterid', id)

        String extension = CommonUtils.PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.PDF_FORMAT

        if(params.printOptionType && params.printOptionType !=PrintOptionType.PDF.key){
            if(params.printOptionType==PrintOptionType.XLSX.key){
                extension = CommonUtils.XLSX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_XLSX
                exportFormat = JasperExportFormat.XLSX_FORMAT
            } else if(params.printOptionType==PrintOptionType.DOCX.key){
                extension = CommonUtils.DOCX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_DOCX
                exportFormat = JasperExportFormat.DOCX_FORMAT
            }
        }

        String outputFileName = "salary_master_data${extension}"
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

    def pfStatement(Long id){
        String reportFileName = schoolService.reportFileName(CommonUtils.MODULE_SALARY, EMP_SALARY_PF_STATEMENT_JASPER_FILE)
        SalMaster salMaster = SalMaster.read(id)
        if (!salMaster) {
            redirect(controller: 'salarySetup', action: 'salMasterSetup')
            return
        }
        String monthName = salMaster.yearMonths.value
        String year = salMaster.academicYear.value

        Map paramsMap = new LinkedHashMap()
        paramsMap.put('REPORT_LOGO', schoolService.reportLogoPath())
        paramsMap.put('yearMonth', "$monthName, $year")
        paramsMap.put('schoolName', message(code: "app.school.name"))
        paramsMap.put('schoolHead', message(code: "app.school.head"))
        paramsMap.put('schoolAddress', message(code: "app.report.address.line1"))
        paramsMap.put('salMasterid', id)

        String extension = CommonUtils.PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.PDF_FORMAT

        if(params.printOptionType && params.printOptionType !=PrintOptionType.PDF.key){
            if(params.printOptionType==PrintOptionType.XLSX.key){
                extension = CommonUtils.XLSX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_XLSX
                exportFormat = JasperExportFormat.XLSX_FORMAT
            } else if(params.printOptionType==PrintOptionType.DOCX.key){
                extension = CommonUtils.DOCX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_DOCX
                exportFormat = JasperExportFormat.DOCX_FORMAT
            }
        }

        String outputFileName = "pf_statement${extension}"
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

    def advance(){
        String reportFileName = schoolService.reportFileName(CommonUtils.MODULE_SALARY, EMP_SALARY_ADVANCE_AMOUNT_JASPER_FILE)

        String sqlParam = "emp.active_status='ACTIVE' and emp.active_status='ACTIVE'"
           if (params.hrCategory) {
                sqlParam += " and emp.hr_category_id = ${params.hrCategory}"
            }
        if (params.employee) {
            sqlParam += " and emp.id = ${params.employee}"
        }
        Map paramsMap = new LinkedHashMap()
        paramsMap.put('schoolName', message(code: "app.school.name"))
        paramsMap.put('schoolAddress', message(code: "app.report.address.line1"))
        paramsMap.put('creditLine', message(code: "company.credit.footer"))
        paramsMap.put('REPORT_LOGO', schoolService.reportLogoPath())
        paramsMap.put('sqlParam', sqlParam)


        String extension = CommonUtils.PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.PDF_FORMAT

        if(params.printOptionType && params.printOptionType !=PrintOptionType.PDF.key){
            if(params.printOptionType==PrintOptionType.XLSX.key){
                extension = CommonUtils.XLSX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_XLSX
                exportFormat = JasperExportFormat.XLSX_FORMAT
            } else if(params.printOptionType==PrintOptionType.DOCX.key){
                extension = CommonUtils.DOCX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_DOCX
                exportFormat = JasperExportFormat.DOCX_FORMAT
            }
        }

        String outputFileName = "salary_setup_data${extension}"
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

    def dps(){
        String reportFileName = schoolService.reportFileName(CommonUtils.MODULE_SALARY, EMP_SALARY_DPS_LIST_JASPER_FILE)

        String sqlParam = "emp.active_status='ACTIVE' and emp.active_status='ACTIVE'"
        if (params.hrCategory) {
            sqlParam += " and emp.hr_category_id = ${params.hrCategory}"
        }
        if (params.employee) {
            sqlParam += " and emp.id = ${params.employee}"
        }
        Map paramsMap = new LinkedHashMap()
        paramsMap.put('schoolName', message(code: "app.school.name"))
        paramsMap.put('schoolAddress', message(code: "app.report.address.line1"))
        paramsMap.put('creditLine', message(code: "company.credit.footer"))
        paramsMap.put('REPORT_LOGO', schoolService.reportLogoPath())
        paramsMap.put('sqlParam', sqlParam)

        String extension = CommonUtils.PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.PDF_FORMAT

        if(params.printOptionType && params.printOptionType !=PrintOptionType.PDF.key){
            if(params.printOptionType==PrintOptionType.XLSX.key){
                extension = CommonUtils.XLSX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_XLSX
                exportFormat = JasperExportFormat.XLSX_FORMAT
            } else if(params.printOptionType==PrintOptionType.DOCX.key){
                extension = CommonUtils.DOCX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_DOCX
                exportFormat = JasperExportFormat.DOCX_FORMAT
            }
        }

        String outputFileName = "salary_setup_data${extension}"
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
    def area() {

    }
    def salReports() {
        String reportName = params.reportName
        AcademicYear academicYear
        if (params.academicYear) {
            academicYear = AcademicYear.valueOf(params.academicYear)
        }
        YearMonths yearMonths
        if (params.yearMonths) {
            yearMonths = YearMonths.valueOf(params.yearMonths)
        }
        SalMaster salMaster = SalMaster.findByAcademicYearAndYearMonthsAndActiveStatus(academicYear, yearMonths, ActiveStatus.ACTIVE)
        if(reportName =='ss') {
            redirect(action: 'salarySheet', id: salMaster?.id, params: params)
        } else if(reportName =='bs') {
            redirect(action: 'bankStatement', id: salMaster?.id, params: params)
        } else if(reportName =='pf') {
            redirect(action: 'pfStatement', id: salMaster?.id, params: params)
        } else if(reportName =='paySlip') {
            redirect(action: 'payslip', id: salMaster?.id, params: params)
        } else if(reportName =='area') {
            redirect(action: 'area', params: params)
        } else if(reportName =='xc') {
            redirect(action: 'extraClass', params: params)
        } else if(reportName =='attn') {
            redirect(action: 'attendance', params: params)
        }
    }


    def advanceSalaryReport(){
        render(view: '/salary/advanceSalaryReport/salaryAdvanceReport')

    }

    def pcReport(){
        String reportFileName = schoolService.reportFileName(CommonUtils.MODULE_SALARY, EMP_SALARY_PC_AMOUNT_JASPER_FILE)
        String sqlParam = "emp.active_status='ACTIVE' and emp.active_status='ACTIVE'"

        if (params.hrCategory) {
            sqlParam += " and emp.hr_category_id = ${params.hrCategory}"
        }
        if (params.employee) {
            sqlParam += " and emp.id = ${params.employee}"
        }

        Map paramsMap = new LinkedHashMap()
        paramsMap.put('schoolName', message(code: "app.school.name"))
        paramsMap.put('schoolAddress', message(code: "app.report.address.line1"))
        paramsMap.put('creditLine', message(code: "company.credit.footer"))
        paramsMap.put('REPORT_LOGO', schoolService.reportLogoPath())
        paramsMap.put('sqlParam', sqlParam)


        String extension = CommonUtils.PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.PDF_FORMAT

        if(params.printOptionType && params.printOptionType !=PrintOptionType.PDF.key){
            if(params.printOptionType==PrintOptionType.XLSX.key){
                extension = CommonUtils.XLSX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_XLSX
                exportFormat = JasperExportFormat.XLSX_FORMAT
            } else if(params.printOptionType==PrintOptionType.DOCX.key){
                extension = CommonUtils.DOCX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_DOCX
                exportFormat = JasperExportFormat.DOCX_FORMAT
            }
        }

        String outputFileName = "salary_pc_data${extension}"
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




    private static final String EMP_SALARY_SET_UP_JASPER_FILE = 'setupRecord.jasper' //  salary setup  report
    private static final String EMP_SALARY_ADVANCE_AMOUNT_JASPER_FILE = 'loanAmount.jasper' //  Sal Advance   report
    private static final String EMP_SALARY_DPS_AMOUNT_JASPER_FILE = 'dps.jasper' //  Sal dps amount   report
    private static final String EMP_SALARY_EXTRA_CLASS_JASPER_FILE = 'extraClass.jasper' //   Extra Clas record
    private static final String EMP_SALARY_ATTENDANCE_JASPER_FILE = 'attandance.jasper' //    Salary attendance record
    private static final String EMP_SALARY_MASTER_JASPER_FILE = 'salarySheet.jasper' //    Salary Sheet record
    private static final String EMP_SALARY_Payslip_JASPER_FILE = 'paySlip.jasper' //    Salary Sheet record
    private static final String EMP_SALARY_BANK_STATEMENT_JASPER_FILE = 'bankStatement.jasper' //    Salary Bank Statement report record
    private static final String EMP_SALARY_PF_STATEMENT_JASPER_FILE = 'pfStatement.jasper' //    Salary Bank Statement report record
    private static final String EMP_SALARY_PC_AMOUNT_JASPER_FILE = 'pcAmount.jasper' //    Salary PC report record
    private static final String EMP_SALARY_DPS_LIST_JASPER_FILE = 'dpsListAmount.jasper' //    Salary Dps  report record
   /* private static final String EMP_ADVANCE_LOAN_LIST_JASPER_FILE = 'loanAmount.jasper' //    Salary Loan  report record*/

}
