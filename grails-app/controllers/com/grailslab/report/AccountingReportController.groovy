package com.grailslab.report

import com.grailslab.CommonUtils
import com.grailslab.accounts.Glsub
import com.grailslab.enums.PrintOptionType
import com.grailslab.gschoolcore.ActiveStatus
import grails.converters.JSON
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef

class AccountingReportController {

    def schoolService
    def jasperService


    def index() {
        def subACList =Glsub.findAllByActiveStatus(ActiveStatus.ACTIVE)
        render(view: '/accounts/accountingReport', model: [subAccountsList: subACList])
    }
    def voucherReport(){
        String reportFileName = schoolService.reportFileName(CommonUtils.MODULE_ACCOUNTING, VOUCHER_JASPER_FILE)
        String reportLogo = schoolService.reportLogoPath()

        println(params.voucher+" "+params.printOptionType)

        Map paramsMap = new LinkedHashMap()
        paramsMap.put('REPORT_LOGO', reportLogo)
        paramsMap.put('schoolName', message(code: "app.school.name"))
        paramsMap.put('schoolAddress', message(code: "app.report.address.line1"))
        paramsMap.put('creditLine', message(code: "company.credit.footer"))

        String extension = CommonUtils.PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.PDF_FORMAT

        PrintOptionType printOptionType
        if (params.printOptionType) {
            printOptionType = PrintOptionType.valueOf(params.printOptionType)
        }
        if (printOptionType && printOptionType != PrintOptionType.PDF) {
            if (printOptionType == PrintOptionType.XLSX) {
                extension = CommonUtils.XLSX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_XLSX
                exportFormat = JasperExportFormat.XLSX_FORMAT
            } else if (printOptionType == PrintOptionType.DOCX) {
                extension = CommonUtils.DOCX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_DOCX
                exportFormat = JasperExportFormat.DOCX_FORMAT
            }
        }

        String outputFileName = "voucher${extension}"
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
    def voucherByDate(){
        String reportFileName = schoolService.reportFileName(CommonUtils.MODULE_ACCOUNTING, BY_DATE_VOUCHER_JASPER_FILE)
        String reportLogo = schoolService.reportLogoPath()

        Map paramsMap = new LinkedHashMap()
        paramsMap.put('REPORT_LOGO', reportLogo)
        paramsMap.put('schoolName', message(code: "app.school.name"))
        paramsMap.put('schoolAddress', message(code: "app.report.address.line1"))
        paramsMap.put('creditLine', message(code: "company.credit.footer"))

        println(params.startDate+" "+params.endDate+" "+params.printOptionType)

        String extension = CommonUtils.PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.PDF_FORMAT

        PrintOptionType printOptionType
        if (params.printOptionType) {
            printOptionType = PrintOptionType.valueOf(params.printOptionType)
        }
        if (printOptionType && printOptionType != PrintOptionType.PDF) {
            if (printOptionType == PrintOptionType.XLSX) {
                extension = CommonUtils.XLSX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_XLSX
                exportFormat = JasperExportFormat.XLSX_FORMAT
            } else if (printOptionType == PrintOptionType.DOCX) {
                extension = CommonUtils.DOCX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_DOCX
                exportFormat = JasperExportFormat.DOCX_FORMAT
            }
        }

        String outputFileName = "voucher_By_Date${extension}"
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
    def monthlyVoucher(){
        String reportFileName = schoolService.reportFileName(CommonUtils.MODULE_ACCOUNTING, BY_MONTH_VOUCHER_JASPER_FILE)
        String reportLogo = schoolService.reportLogoPath()

        println(params.voucherYearName+" "+params.voucherMonthName+" "+params.printOptionType)

        Map paramsMap = new LinkedHashMap()
        paramsMap.put('REPORT_LOGO', reportLogo)
        paramsMap.put('schoolName', message(code: "app.school.name"))
        paramsMap.put('schoolAddress', message(code: "app.report.address.line1"))
        paramsMap.put('creditLine', message(code: "company.credit.footer"))

        String extension = CommonUtils.PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.PDF_FORMAT

        PrintOptionType printOptionType
        if (params.printOptionType) {
            printOptionType = PrintOptionType.valueOf(params.printOptionType)
        }
        if (printOptionType && printOptionType != PrintOptionType.PDF) {
            if (printOptionType == PrintOptionType.XLSX) {
                extension = CommonUtils.XLSX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_XLSX
                exportFormat = JasperExportFormat.XLSX_FORMAT
            } else if (printOptionType == PrintOptionType.DOCX) {
                extension = CommonUtils.DOCX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_DOCX
                exportFormat = JasperExportFormat.DOCX_FORMAT
            }
        }

        String outputFileName = "monthly_voucher${extension}"
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
    def yearlyVoucher(){
        String reportFileName = schoolService.reportFileName(CommonUtils.MODULE_ACCOUNTING, BY_YEAR_VOUCHER_JASPER_FILE)
        String reportLogo = schoolService.reportLogoPath()

        println(params.voucherSource+" "+params.printOptionType)

        Map paramsMap = new LinkedHashMap()
        paramsMap.put('REPORT_LOGO', reportLogo)
        paramsMap.put('schoolName', message(code: "app.school.name"))
        paramsMap.put('schoolAddress', message(code: "app.report.address.line1"))
        paramsMap.put('creditLine', message(code: "company.credit.footer"))

        String extension = CommonUtils.PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.PDF_FORMAT

        PrintOptionType printOptionType
        if (params.printOptionType) {
            printOptionType = PrintOptionType.valueOf(params.printOptionType)
        }
        if (printOptionType && printOptionType != PrintOptionType.PDF) {
            if (printOptionType == PrintOptionType.XLSX) {
                extension = CommonUtils.XLSX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_XLSX
                exportFormat = JasperExportFormat.XLSX_FORMAT
            } else if (printOptionType == PrintOptionType.DOCX) {
                extension = CommonUtils.DOCX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_DOCX
                exportFormat = JasperExportFormat.DOCX_FORMAT
            }
        }

        String outputFileName = "yearly_voucher${extension}"
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

    def cashOrBankReport(){
        String reportFileName = schoolService.reportFileName(CommonUtils.MODULE_ACCOUNTING, CASH_OR_BANK_JASPER_FILE)
        String reportLogo = schoolService.reportLogoPath()

        println(params.voucherSource+" "+params.startDate+" "+params.endDate+" "+params.printOptionType)

        Map paramsMap = new LinkedHashMap()
        paramsMap.put('REPORT_LOGO', reportLogo)
        paramsMap.put('schoolName', message(code: "app.school.name"))
        paramsMap.put('schoolAddress', message(code: "app.report.address.line1"))
        paramsMap.put('creditLine', message(code: "company.credit.footer"))

        String extension = CommonUtils.PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.PDF_FORMAT

        PrintOptionType printOptionType
        if (params.printOptionType) {
            printOptionType = PrintOptionType.valueOf(params.printOptionType)
        }
        if (printOptionType && printOptionType != PrintOptionType.PDF) {
            if (printOptionType == PrintOptionType.XLSX) {
                extension = CommonUtils.XLSX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_XLSX
                exportFormat = JasperExportFormat.XLSX_FORMAT
            } else if (printOptionType == PrintOptionType.DOCX) {
                extension = CommonUtils.DOCX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_DOCX
                exportFormat = JasperExportFormat.DOCX_FORMAT
            }
        }

        String outputFileName = "cash_bank_report${extension}"
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

    def unpostOutOfBalance(){
        String reportFileName = schoolService.reportFileName(CommonUtils.MODULE_ACCOUNTING, INPUT_OUTPUT_JASPER_FILE)
        String reportLogo = schoolService.reportLogoPath()

        println(params.unpostOutOfBalance+" "+params.startDate+" "+params.endDate+" "+params.printOptionType)

        Map paramsMap = new LinkedHashMap()
        paramsMap.put('REPORT_LOGO', reportLogo)
        paramsMap.put('schoolName', message(code: "app.school.name"))
        paramsMap.put('schoolAddress', message(code: "app.report.address.line1"))
        paramsMap.put('creditLine', message(code: "company.credit.footer"))

        String extension = CommonUtils.PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.PDF_FORMAT

        PrintOptionType printOptionType
        if (params.printOptionType) {
            printOptionType = PrintOptionType.valueOf(params.printOptionType)
        }
        if (printOptionType && printOptionType != PrintOptionType.PDF) {
            if (printOptionType == PrintOptionType.XLSX) {
                extension = CommonUtils.XLSX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_XLSX
                exportFormat = JasperExportFormat.XLSX_FORMAT
            } else if (printOptionType == PrintOptionType.DOCX) {
                extension = CommonUtils.DOCX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_DOCX
                exportFormat = JasperExportFormat.DOCX_FORMAT
            }
        }

        String outputFileName = "unpost_Out_Balance${extension}"
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
    def accountLedger (){
        String reportFileName = schoolService.reportFileName(CommonUtils.MODULE_ACCOUNTING, ACCOUNT_LEDGER_JASPER_FILE)
        String reportLogo = schoolService.reportLogoPath()

        println(params.mainAccountName+" "+params.subAccountName+" "+params.startDate+" "+params.endDate+" "+params.printOptionType)

        Map paramsMap = new LinkedHashMap()
        paramsMap.put('REPORT_LOGO', reportLogo)
        paramsMap.put('schoolName', message(code: "app.school.name"))
        paramsMap.put('schoolAddress', message(code: "app.report.address.line1"))
        paramsMap.put('creditLine', message(code: "company.credit.footer"))

        String extension = CommonUtils.PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.PDF_FORMAT

        PrintOptionType printOptionType
        if (params.printOptionType) {
            printOptionType = PrintOptionType.valueOf(params.printOptionType)
        }
        if (printOptionType && printOptionType != PrintOptionType.PDF) {
            if (printOptionType == PrintOptionType.XLSX) {
                extension = CommonUtils.XLSX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_XLSX
                exportFormat = JasperExportFormat.XLSX_FORMAT
            } else if (printOptionType == PrintOptionType.DOCX) {
                extension = CommonUtils.DOCX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_DOCX
                exportFormat = JasperExportFormat.DOCX_FORMAT
            }
        }

        String outputFileName = "account_ledger${extension}"
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

    def glmstListReport(){
        String reportFileName = schoolService.reportFileName(CommonUtils.MODULE_ACCOUNTING, GLMST_LIST_JASPER_FILE)
        String reportLogo = schoolService.reportLogoPath()

        println(params.printOptionType)

        Map paramsMap = new LinkedHashMap()
        paramsMap.put('REPORT_LOGO', reportLogo)
        paramsMap.put('schoolName', message(code: "app.school.name"))
        paramsMap.put('schoolAddress', message(code: "app.report.address.line1"))
        paramsMap.put('creditLine', message(code: "company.credit.footer"))

        String extension = CommonUtils.PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.PDF_FORMAT

        PrintOptionType printOptionType
        if (params.printOptionType) {
            printOptionType = PrintOptionType.valueOf(params.printOptionType)
        }
        if (printOptionType && printOptionType != PrintOptionType.PDF) {
            if (printOptionType == PrintOptionType.XLSX) {
                extension = CommonUtils.XLSX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_XLSX
                exportFormat = JasperExportFormat.XLSX_FORMAT
            } else if (printOptionType == PrintOptionType.DOCX) {
                extension = CommonUtils.DOCX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_DOCX
                exportFormat = JasperExportFormat.DOCX_FORMAT
            }
        }

        String outputFileName = "glmst_list${extension}"
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
    def financialReport(){
        render(view: '/accounts/financialReport')
    }

    //Jasper report
    private static final String VOUCHER_JASPER_FILE = 'voucher.jasper'
    private static final String GLMST_LIST_JASPER_FILE = 'glmstList.jasper'
    private static final String BY_DATE_VOUCHER_JASPER_FILE = 'byDateVoucher.jasper'
    private static final String BY_MONTH_VOUCHER_JASPER_FILE = 'byMonthVoucher.jasper'
    private static final String BY_YEAR_VOUCHER_JASPER_FILE = 'byYearVoucher.jasper'
    private static final String CASH_OR_BANK_JASPER_FILE = 'cashOrBankReport.jasper'
    private static final String INPUT_OUTPUT_JASPER_FILE = 'unpostOutOfBalance.jasper'
    private static final String ACCOUNT_LEDGER_JASPER_FILE = 'accountLedger.jasper'

}
