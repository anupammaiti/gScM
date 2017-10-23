package com.grailslab.common

import com.grailslab.CommonUtils
import com.grailslab.bailyschool.uma.Role
import com.grailslab.command.LeaveApplyCommand
import com.grailslab.enums.PrintOptionType
import com.grailslab.gschoolcore.AcademicYear
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.hr.Employee
import com.grailslab.hr.LeaveApply
import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityUtils
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.joda.time.DateTime

class LeaveController {
    def leaveService
    def messageSource
    def springSecurityService
    def schoolService
    def jasperService

    def index() {
        def leaveList = leaveService.allLeaveDropDownList()
        if (SpringSecurityUtils.ifAnyGranted(Role.AvailableRoles.SUPER_ADMIN.value())) {
            render(view: 'index', layout: 'adminLayout', model: [leaveList: leaveList])
            return
        } else if (SpringSecurityUtils.ifAnyGranted(Role.AvailableRoles.ADMIN.value())) {
            render(view: 'index', layout: 'adminLayout', model: [leaveList: leaveList])
            return
        } else if (SpringSecurityUtils.ifAllGranted(Role.AvailableRoles.ACCOUNTS.value())) {
            render(view: 'index', layout: 'moduleCollectionLayout', model: [leaveList: leaveList])
            return
        } else if (SpringSecurityUtils.ifAllGranted(Role.AvailableRoles.LIBRARY.value())) {
            render(view: 'index', layout: 'moduleLibraryLayout', model: [leaveList: leaveList])
            return
        } else if (SpringSecurityUtils.ifAllGranted(Role.AvailableRoles.HR.value())) {
            render(view: 'index', layout: 'moduleHRLayout', model: [leaveList: leaveList])
            return
        } else if (SpringSecurityUtils.ifAllGranted(Role.AvailableRoles.TEACHER.value())) {
            render(view: 'index', layout: 'adminLayout', model: [leaveList: leaveList])
            return
        } else if (SpringSecurityUtils.ifAllGranted(Role.AvailableRoles.ORGANIZER.value())) {
            render(view: 'index', layout: 'moduleWebLayout', model: [leaveList: leaveList])
            return
        } else if (SpringSecurityUtils.ifAllGranted(Role.AvailableRoles.SCHOOL_HEAD.value())) {
            render(view: 'index', layout: 'adminLayout', model: [leaveList: leaveList])
            return
        }
    }
    def save(LeaveApplyCommand command) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()

        String outPut
        //   String message
        if (command.hasErrors()) {
            def errorList = command?.errors?.allErrors?.collect { messageSource.getMessage(it, null) }
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, errorList?.join('\n'))
            outPut = result as JSON
            render outPut
            return
        }
        Boolean isSaveSuccess = false
        def principal = springSecurityService.principal
        Employee employee = Employee.findByEmpID(principal.userRef)

        if (!employee){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }

        LeaveApply leaveApply
        if (command.id) {
            leaveApply = LeaveApply.get(command.id)
            if (!leaveApply) {
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
                outPut = result as JSON
                render outPut
                return
            }
            leaveApply.properties['applyDate','leaveName','applyType','approveStatus','payType','fromDate','toDate','argentContactNo','supportedBy'] = command.properties
            leaveApply.employee = employee
            isSaveSuccess = leaveService.saveLeaveApplication(leaveApply)
            result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
            result.put(CommonUtils.MESSAGE, 'Application updated successfully')
        } else {
            leaveApply = new LeaveApply(command.properties)
            leaveApply.employee = employee
            isSaveSuccess = leaveService.saveLeaveApplication(leaveApply)
            result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
            result.put(CommonUtils.MESSAGE, 'Application added successfully')
        }
        if (!isSaveSuccess){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "Your paid leave quota for this year exceeded. Please try unpaid or contact admin")
            outPut = result as JSON
            render outPut
            return
        }
        outPut = result as JSON
        render outPut
    }
    def list() {
        LinkedHashMap gridData
        String result
        LinkedHashMap resultMap = leaveService.leaveEmployeePaginateList(params)

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

    def edit(Long id) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        LeaveApply apply = LeaveApply.read(id)
        if (!apply) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }

        Employee employee = apply.employee
        Employee replacement
        if (apply.supportedBy) {
            replacement = Employee.findByEmpID(apply.supportedBy)
        }
        String employeeDetails = employee?.empID + ' - ' + employee?.name
        String replacementBy = replacement?.empID + ' - ' + replacement?.name
        String leaveName = apply?.leaveName?.name
        String reason = apply?.reason
        String argentContactNo = apply?.argentContactNo
        result.put('employeeDetails', employeeDetails)
        result.put('replacementBy', replacementBy)
        result.put('employee', employee?.name)
        result.put('supportedBy', employee?.name)

        /*result.put('places', places)*/
        result.put('leaveName', leaveName)
        result.put('reason', reason)
        result.put('argentContactNo', argentContactNo)
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.OBJ, apply)
        outPut = result as JSON
        render outPut
    }
    def inactive(Long id) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        LeaveApply apply = LeaveApply.get(id)
        if (!apply) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        apply.activeStatus = ActiveStatus.INACTIVE
        apply.save(flush: true)
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, 'Inactive Successfully')
        outPut = result as JSON
        render outPut
    }


    def leaveBalance(){
        AcademicYear workingYear = schoolService.workingYear()

        int year = workingYear.value.toInteger()
        DateTime reportDate = new DateTime().withYear(year)
        Date startDate = reportDate.dayOfYear().withMinimumValue().toDate()
        Date endDate = reportDate.dayOfYear().withMaximumValue().toDate()

        List employeeLeaveList = new ArrayList()
        String reportFileName = schoolService.reportFileName(CommonUtils.MODULE_LEAVE, EMPLOYEE_LEAVE_BALANCE_JASPER_FILE)

        Employee employee = schoolService.loggedInEmployee()
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

/*        if(params.printOptionType && params.printOptionType !=PrintOptionType.PDF.key){
            if(params.printOptionType==PrintOptionType.XLSX.key){
                extension = CommonUtils.XLSX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_XLSX
                exportFormat = JasperExportFormat.XLSX_FORMAT
            } else if(params.printOptionType==PrintOptionType.DOCX.key){
                extension = CommonUtils.DOCX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_DOCX
                exportFormat = JasperExportFormat.DOCX_FORMAT
            }
        }*/
        String outputFileName = "employee_leave_balance${extension}"
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
    private static final String EMPLOYEE_LEAVE_BALANCE_JASPER_FILE = 'employeeLeaveBalance.jasper'
}
