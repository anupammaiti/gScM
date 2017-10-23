package com.grailslab.report

import com.grailslab.CommonUtils
import com.grailslab.EmployeeLeaveReference
import com.grailslab.enums.LeaveApplyType
import com.grailslab.enums.LeaveApproveStatus
import com.grailslab.enums.LeavePayType
import com.grailslab.enums.PayType
import com.grailslab.enums.PrintOptionType
import com.grailslab.gschoolcore.AcademicYear
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.hr.Employee
import com.grailslab.hr.HrCategory
import com.grailslab.hr.HrDesignation
import com.grailslab.hr.LeaveApply
import com.grailslab.hr.LeaveBalance
import com.grailslab.hr.LeaveName
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.joda.time.DateTime

class LeaveReportController{

    def schoolService
    def jasperService
    def leaveService
    def employeeService

    private static final String LEAVE_APPLICATION_JASPER_FILE = 'leaveApplication.jasper'
    private static final String LEAVE_BY_EMPLOYEE_JASPER_FILE = 'leaveByEmployee.jasper'
    private static final String LEAVE_BY_ALL_EMPLOYEE_JASPER_FILE = 'leaveByAllEmployee.jasper'

    def index() {
        def leaveList = leaveService.allLeaveDropDownList()
        def hrCategoryList = HrCategory.findAllByActiveStatus(ActiveStatus.ACTIVE,[sort:'sortOrder'])
        render(view: '/leave/leaveReport', model: [hrCategoryList: hrCategoryList, leaveList: leaveList])
    }

    def printApplication(Long id){
        LeaveApply apply = LeaveApply.read(id)
        if (!apply || apply.activeStatus != ActiveStatus.ACTIVE) {
            flash.message=CommonUtils.COMMON_NOT_FOUND_MESSAGE
            redirect(controller: 'leaveApproval', action: 'index')
            return
        }
        String applicationBody = apply.application

        String reportFileName = LEAVE_APPLICATION_JASPER_FILE
        Map paramsMap = new LinkedHashMap()

        paramsMap.put('REPORT_LOGO', schoolService.reportLogoPath())
        paramsMap.put('applicationBody', applicationBody)
        paramsMap.put('schoolHead', message(code: "app.school.head"))
        paramsMap.put('schoolName', message(code: "app.school.name"))
        paramsMap.put('schoolAddress', message(code: "app.report.address.line1"))
        paramsMap.put('creditLine', message(code: "company.credit.footer"))


        String extension = CommonUtils.PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.PDF_FORMAT
        String outputFileName = "Application${extension}"

        JasperReportDef reportDef = new JasperReportDef(
                name: reportFileName,
                fileFormat: exportFormat,
                parameters: paramsMap,
              //  reportData: applicationBody
        )
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        response.contentType = grailsApplication.config.grails.mime.types[mimeType]
        response.setHeader("Content-disposition", "inline;filename=${outputFileName}")
        response.outputStream << report.toByteArray()
    }

    def byEmployee(){
        AcademicYear workingYear
        if (params.year) {
            workingYear = AcademicYear.valueOf(params.year)
        } else {
            workingYear = schoolService.workingYear()
        }
        int year = workingYear.value.toInteger()
        DateTime reportDate = new DateTime().withYear(year)
        Date startDate = reportDate.dayOfYear().withMinimumValue().toDate()
        Date endDate = reportDate.dayOfYear().withMaximumValue().toDate()

        List employeeLeaveList = new ArrayList()
        String reportFileName = schoolService.reportFileName(CommonUtils.MODULE_LEAVE, LEAVE_BY_EMPLOYEE_JASPER_FILE)
        Employee employee = Employee.read(params.getLong("employee"))
        if(employee){
            def leaveList = leaveService.listLeave(params.getLong('leaveName'))
            int leaveObtained = 0
            int leaveDue = 0
            for (leaveName in leaveList) {
                leaveObtained = leaveService.leaveConsume(employee.id, leaveName.id, startDate, endDate)
                leaveDue = leaveName.numberOfDay - leaveObtained
                employeeLeaveList.add([leaveName: leaveName.name, allowedDay: leaveName.numberOfDay, obtainLeave: leaveObtained, leaveDue: leaveDue < 0 ? 0:leaveDue])
            }
        }

        Map paramsMap = new LinkedHashMap()
        paramsMap.put('employeeId', employee.empID)
        paramsMap.put('employeeName', employee.name)
        paramsMap.put('empDesignation', employee.hrDesignation?.name)
        paramsMap.put('workingYear', year)
        paramsMap.put('REPORT_LOGO', schoolService.reportLogoPath())
        paramsMap.put('schoolHead', message(code: "app.school.head"))
        paramsMap.put('schoolName', message(code: "app.school.name"))
        paramsMap.put('schoolAddress', message(code: "app.report.address.line1"))
        paramsMap.put('creditLine', message(code: "company.credit.footer"))
//        paramsMap.put('sqlParam', sqlParam)

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
        String outputFileName = "leave_By_Employee${extension}"
        JasperReportDef reportDef = new JasperReportDef(
                name: reportFileName,
                fileFormat: exportFormat,
                parameters: paramsMap,
                reportData: employeeLeaveList
        )
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        response.contentType =grailsApplication.config.grails.mime.types[mimeType]
        response.setHeader("Content-disposition", "inline;filename=${outputFileName}")
        response.outputStream << report.toByteArray()
    }
    def allEmpLeaveReport(){
        AcademicYear workingYear
        if (params.year) {
            workingYear = AcademicYear.valueOf(params.year)
        } else {
            workingYear = schoolService.workingYear()
        }
        int year = workingYear.value.toInteger()
        DateTime reportDate = new DateTime().withYear(year)
        Date startDate = reportDate.dayOfYear().withMinimumValue().toDate()
        Date endDate = reportDate.dayOfYear().withMaximumValue().toDate()

        List employeeLeaveList = new ArrayList()
        String reportFileName = schoolService.reportFileName(CommonUtils.MODULE_LEAVE, LEAVE_BY_ALL_EMPLOYEE_JASPER_FILE)

        int consumeCount = 0
        int dueCount = 0
        LeaveName leaveName = LeaveName.read(params.getLong('leaveName'))
        int numberOfDay = leaveName?.numberOfDay
        HrCategory hrCategory = HrCategory.read(params.getLong('hrCategory'))
        def employeeList = employeeService.employeeList(hrCategory)
        for (employee in employeeList) {
            consumeCount = leaveService.leaveConsume(employee.id, leaveName.id, startDate, endDate)
            dueCount = numberOfDay - consumeCount
            employeeLeaveList.add([empId: employee.empID, name: employee.name, designation: employee.hrDesignation?.name, consumeCount: consumeCount, dueCount: dueCount < 0 ? 0:dueCount])
        }

        Map paramsMap = new LinkedHashMap()
        paramsMap.put('REPORT_LOGO', schoolService.reportLogoPath())
        paramsMap.put('schoolHead', message(code: "app.school.head"))
        paramsMap.put('schoolName', message(code: "app.school.name"))
        paramsMap.put('schoolAddress', message(code: "app.report.address.line1"))
        paramsMap.put('creditLine', message(code: "company.credit.footer"))
        paramsMap.put('leaveName', leaveName.name)
        paramsMap.put('paidLeave', numberOfDay)
        paramsMap.put('workingYear', year)

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
        String outputFileName = "leave_By_Employee${extension}"
        JasperReportDef reportDef = new JasperReportDef(
                name: reportFileName,
                fileFormat: exportFormat,
                parameters: paramsMap,
                reportData: employeeLeaveList
        )
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        response.contentType =grailsApplication.config.grails.mime.types[mimeType]
        response.setHeader("Content-disposition", "inline;filename=${outputFileName}")
        response.outputStream << report.toByteArray()
    }

    /*def leaveReport(Long id) {
        private static final String LEAVE_APPLICATION_JASPER_FILE = 'leaveApplication.jasper'
        LeaveApply apply = LeaveApply.read(id)
        Employee employee = apply.employee
        Date applyDate = apply.applyDate
        Date fromDate = apply.fromDate
        Date toDate = apply.toDate
        String leaveName = apply.leaveName
        String reason = apply.reason
        String empID = employee.empID
        String name = employee.name

        String halfDayLeaveSubject = 'Prayer for half a day leave'
        String advanceLeaveSubject = 'Prayer for leave of absence'
        String approveLeaveSubject = 'Prayer for Approved Absence.'


        String halfDayLeaveBody = 'With due respect,I would like to inform you that I am in need of a half a day leave today ( ' + CommonUtils.getUiDateStr(fromDate) + ' ) afternoon on account of ' + reason
        String advanceLeaveBody = 'With due respect, I would like to inform you that I will not be able to attend at school from ' + CommonUtils.getUiDateStr(fromDate) + ' to ' + CommonUtils.getUiDateStr(toDate) + ' on account of ' + reason
        String approveLeaveBody = 'With due respect,I would like to inform you that I could not be able to present at school from ' + CommonUtils.getUiDateStr(fromDate) + ' to ' + CommonUtils.getUiDateStr(toDate) + ' on account of ' + reason

        String leave2ndPart = 'May I, therefore pray and hope that you would be kind enough to grant my prayer and oblige thereby.'

        String applicationSubject = ''
        String applicationBody = ''
        if (apply.applyType.equals(LeaveApplyType.APPROVED_ABSENCE)) {
            applicationSubject = approveLeaveSubject
            applicationBody = approveLeaveBody
        } else if (apply.applyType.equals(LeaveApplyType.REQUEST_FOR_HALF_DAY)) {
            applicationSubject = halfDayLeaveSubject
            applicationBody = halfDayLeaveBody
        } else if (apply.applyType.equals(LeaveApplyType.REQUEST_FOR_LEAVE)) {
            applicationSubject = advanceLeaveSubject
            applicationBody = advanceLeaveBody
        }

        String reportFileName = LEAVE_APPLICATION_JASPER_FILE
        String reportLogo = schoolService.reportLogoPath()
        Map paramsMap = new LinkedHashMap()
        paramsMap.put('REPORT_LOGO', reportLogo)
        paramsMap.put('applyid', params.id)
        paramsMap.put('applicationSubject', applicationSubject)
        paramsMap.put('applicationBody', applicationBody)
        paramsMap.put('leave2ndPart', leave2ndPart)


        String extension = CommonUtils.PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.PDF_FORMAT

        String outputFileName = "Application${extension}"
        JasperReportDef reportDef = new JasperReportDef(
                name: reportFileName,
                fileFormat: exportFormat,
                parameters: paramsMap
        )
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        response.contentType = grailsApplication.config.grails.mime.types[mimeType]
        response.setHeader("Content-disposition", "inline;filename=${outputFileName}")
        response.outputStream << report.toByteArray()
    }*/
}
