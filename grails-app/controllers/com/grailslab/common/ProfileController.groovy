package com.grailslab.common

import com.grailslab.CommonUtils
import com.grailslab.bailyschool.uma.Role
import com.grailslab.bailyschool.uma.User
import com.grailslab.command.EducationalInfoCommand
import com.grailslab.command.EmpInfoCommand
import com.grailslab.command.EmploymentHistoryCommand
import com.grailslab.command.ResetPasswordCommand
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.hr.*
import com.grailslab.settings.Image
import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityUtils
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.multipart.commons.CommonsMultipartFile

import javax.servlet.http.HttpServletRequest

class ProfileController {
    def employeeService
    def messageSource
    def uploadService
    def springSecurityService
    def passwordEncoder
    def userService

    def index() {
        def loggedUser = springSecurityService.principal
        Employee employee = Employee.findByEmpID(loggedUser.userRef)
        if (!employee) {
            redirect(controller: 'login', action: 'loginSuccess')
            return
        }
        String category = employee?.hrCategory?.name
        def dataJobInfoList = employeeService.dataJobInfoList(employee)
        def academicInfoList = employeeService.academicInfoList(employee)
        String designation = employee.hrDesignation?.name

        if (SpringSecurityUtils.ifAnyGranted(Role.AvailableRoles.SUPER_ADMIN.value())) {
            render(view: 'empProfile', layout: 'adminLayout', model: [employee: employee,category:category, designation: designation, dataJobInfoList: dataJobInfoList, academicInfoList: academicInfoList])
            return
        } else if (SpringSecurityUtils.ifAnyGranted(Role.AvailableRoles.ADMIN.value())) {
            render(view: 'empProfile', layout: 'adminLayout', model: [employee: employee,category:category, designation: designation, dataJobInfoList: dataJobInfoList, academicInfoList: academicInfoList])
            return
        } else if (SpringSecurityUtils.ifAllGranted(Role.AvailableRoles.ACCOUNTS.value())) {
            render(view: 'empProfile', layout: 'moduleCollectionLayout', model: [employee: employee,category:category, designation: designation, dataJobInfoList: dataJobInfoList, academicInfoList: academicInfoList])
            return
        } else if (SpringSecurityUtils.ifAllGranted(Role.AvailableRoles.LIBRARY.value())) {
            render(view: 'empProfile', layout: 'moduleLibraryLayout', model: [employee: employee,category:category, designation: designation, dataJobInfoList: dataJobInfoList, academicInfoList: academicInfoList])
            return
        } else if (SpringSecurityUtils.ifAllGranted(Role.AvailableRoles.HR.value())) {
            render(view: 'empProfile', layout: 'moduleHRLayout', model: [employee: employee,category:category, designation: designation, dataJobInfoList: dataJobInfoList, academicInfoList: academicInfoList])
            return
        } else if (SpringSecurityUtils.ifAllGranted(Role.AvailableRoles.TEACHER.value())) {
            render(view: 'empProfile', layout: 'adminLayout', model: [employee: employee,category:category, designation: designation, dataJobInfoList: dataJobInfoList, academicInfoList: academicInfoList])
            return
        } else if (SpringSecurityUtils.ifAllGranted(Role.AvailableRoles.ORGANIZER.value())) {
            render(view: 'empProfile', layout: 'moduleWebLayout', model: [employee: employee,category:category, designation: designation, dataJobInfoList: dataJobInfoList, academicInfoList: academicInfoList])
            return
        } else if (SpringSecurityUtils.ifAllGranted(Role.AvailableRoles.SCHOOL_HEAD.value())) {
            render(view: 'empProfile', layout: 'adminLayout', model: [employee: employee,category:category, designation: designation, dataJobInfoList: dataJobInfoList, academicInfoList: academicInfoList])
            return
        } else if (SpringSecurityUtils.ifAllGranted(Role.AvailableRoles.STUDENT.value())) {
            render(view: 'empProfile', layout: 'moduleParentsLayout', model: [employee: employee,category:category, designation: designation, dataJobInfoList: dataJobInfoList, academicInfoList: academicInfoList])
            return
        }

    }

    def resetPassword() {
        if (SpringSecurityUtils.ifAnyGranted(Role.AvailableRoles.SUPER_ADMIN.value())) {
            render(view: 'resetPassword', layout: 'adminLayout')
            return
        } else if (SpringSecurityUtils.ifAnyGranted(Role.AvailableRoles.ADMIN.value())) {
            render(view: 'resetPassword', layout: 'adminLayout')
            return
        } else if (SpringSecurityUtils.ifAllGranted(Role.AvailableRoles.ACCOUNTS.value())) {
            render(view: 'resetPassword', layout: 'moduleCollectionLayout')
            return
        } else if (SpringSecurityUtils.ifAllGranted(Role.AvailableRoles.LIBRARY.value())) {
            render(view: 'resetPassword', layout: 'moduleLibraryLayout')
            return
        } else if (SpringSecurityUtils.ifAllGranted(Role.AvailableRoles.HR.value())) {
            render(view: 'resetPassword', layout: 'moduleHRLayout')
            return
        } else if (SpringSecurityUtils.ifAllGranted(Role.AvailableRoles.TEACHER.value())) {
            render(view: 'resetPassword', layout: 'adminLayout')
            return
        } else if (SpringSecurityUtils.ifAllGranted(Role.AvailableRoles.ORGANIZER.value())) {
            render(view: 'resetPassword', layout: 'moduleWebLayout')
            return
        } else if (SpringSecurityUtils.ifAllGranted(Role.AvailableRoles.SCHOOL_HEAD.value())) {
            render(view: 'resetPassword', layout: 'adminLayout')
            return
        } else if (SpringSecurityUtils.ifAllGranted(Role.AvailableRoles.STUDENT.value())) {
            render(view: 'resetPassword', layout: 'moduleParentsLayout')
            return
        }
    }

    def saveResetPassword(ResetPasswordCommand command){
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        if (command.hasErrors()) {
            render(view: 'resetPassword', model: [command: command])
            return
        }
        if (command.newPassword != command.confirmPassword) {
            command.errors.reject('password.wrongConfirm')
            render(view: 'resetPassword', model: [command: command])
            return
        }
        def loggedUser = springSecurityService.principal
        if (!loggedUser) {
            redirect(controller: 'logout')
            return
        }
        User user = User.get(loggedUser.id)
        if (!passwordEncoder.isPasswordValid(user.password, command.oldPassword, null)) {
            command.errors.reject('user.resetpassword.old.notMatch')
            render(view: 'resetPassword', model: [command: command])
            return
        }
        if (passwordEncoder.isPasswordValid(user.password, command.newPassword, null)) {
            command.errors.reject('user.resetpassword.same.old')
            render(view: 'resetPassword', model: [command: command])
            return
        }
        if (!userService.isValidatePassword(command.newPassword)){
            command.errors.reject('user.resetpassword.not.valid')
            render(view: 'resetPassword', model: [command: command])
            return
        }

        user.password = command.newPassword
        if (user.save()){
            SecurityContextHolder.clearContext()
        }

        flash.message="Password Updated successfully. Please login again with your new password"
        redirect(controller: 'login', action: 'auth')
    }

//    employee profile functionalities
    def edit() {
        if (!request.method.equals('POST')) {
            redirect(action: 'profile')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        def loggedUser = springSecurityService.principal
        Employee employee = Employee.findByEmpID(loggedUser.userRef)
        if (!employee) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }

        HrCategory hrCategory = employee.hrCategory
        def designation = HrDesignation.findAllByHrCategoryAndActiveStatus(hrCategory, ActiveStatus.ACTIVE)

        result.put('designation', designation)
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.OBJ, employee)
        outPut = result as JSON
        render outPut
    }

    def personalInfoUpdate(EmpInfoCommand command) {
        if (!request.method.equals('POST')) {
            redirect(action: 'profile')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        if (command.hasErrors()) {
            def errorList = command?.errors?.allErrors?.collect { messageSource.getMessage(it, null) }
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, errorList?.join('\n'))
            outPut = result as JSON
            render outPut
            return
        }
        def loggedUser = springSecurityService.principal
        Employee employee = Employee.findByEmpID(loggedUser.userRef)
        if (!employee) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        employee.properties = command.properties
        HttpServletRequest request = request
        CommonsMultipartFile f = request.getFile("pImage")
        if (!f.empty) {
            try {
                Image image = uploadService.uploadImageWithThumb(request, "pImage", "employee", 450, 350)
                employee.imagePath = image?.identifier
            } catch (Exception e) {
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, e.toString())
                outPut = result as JSON
                render outPut
                return
            }
        }
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, 'Employee Updated successfully')

        if (employee.hasErrors() || !employee.save()) {
            def errorList = employee?.errors?.allErrors?.collect { messageSource.getMessage(it, null) }
            result.put(CommonUtils.MESSAGE, errorList?.join('\n'))
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
        }
        outPut = result as JSON
        render outPut
    }

    def employmentHistorySave(EmploymentHistoryCommand command) {
        if (!request.method.equals('POST')) {
            redirect(action: 'profile')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        if (command.hasErrors()) {
            def errorList = command?.errors?.allErrors?.collect { messageSource.getMessage(it, null) }
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, errorList?.join('\n'))
            outPut = result as JSON
            render outPut
            return
        }
        EmploymentHistory employmentHistory
        String message
        def loggedUser = springSecurityService.principal
        Employee employee = Employee.findByEmpID(loggedUser.userRef)
        if (!employee) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        if (command.id) {
            employmentHistory = EmploymentHistory.get(command.id)
            if (!employmentHistory) {
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
                outPut = result as JSON
                render outPut
                return
            }

            employmentHistory.properties = command.properties
            result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
            message = 'Employment History Updated successfully'
        } else {
            employmentHistory = new EmploymentHistory(command.properties)
            employmentHistory.employee=employee
            result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
            message = 'Employment History Added successfully'
        }

        if (employmentHistory.hasErrors() || !employmentHistory.save()) {
            def errorList = employmentHistory?.errors?.allErrors?.collect { messageSource.getMessage(it, null) }
            message = errorList?.join('\n')
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
        }
        result.put(CommonUtils.MESSAGE, message)
        outPut = result as JSON
        render outPut
    }

    def employmentHistoryEdit(Long id) {
        if (!request.method.equals('POST')) {
            redirect(action: 'profile')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        EmploymentHistory history = EmploymentHistory.read(id)
        if (!history) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.OBJ, history)
        outPut = result as JSON
        render outPut
    }

    def employmentHistoryDelete(Long id) {
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        EmploymentHistory history = EmploymentHistory.get(id)
        if (!history) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
        }
        try {
            history.delete(flush: true)
            result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
            result.put(CommonUtils.MESSAGE, "EmploymentHistory Deleted Successfully.")

        }
        catch (DataIntegrityViolationException e) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "EmploymentHistory already in use. You Can Inactive Reference")
        }
        outPut = result as JSON
        render outPut
    }

    def formEduInfoSave(EducationalInfoCommand command) {
        if (!request.method.equals('POST')) {
            redirect(action: 'profile')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        if (command.hasErrors()) {
            def errorList = command?.errors?.allErrors?.collect { messageSource.getMessage(it, null) }
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, errorList?.join('\n'))
            outPut = result as JSON
            render outPut
            return
        }
        EducationalInfo info
        String message
        def loggedUser = springSecurityService.principal
        Employee employee = Employee.findByEmpID(loggedUser.userRef)
        if (command.id) {
            info = EducationalInfo.get(command.id)
            if (!info) {
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
                outPut = result as JSON
                render outPut
                return
            }

            info.properties = command.properties
            result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
            message = 'Employment Education Updated successfully'
        } else {
            info = new EducationalInfo(command.properties)
            info.employee = employee
            result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
            message = 'Employment Education Added successfully'
        }

        if (info.hasErrors() || !info.save()) {
            def errorList = info?.errors?.allErrors?.collect { messageSource.getMessage(it, null) }
            message = errorList?.join('\n')
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
        }
        result.put(CommonUtils.MESSAGE, message)
        outPut = result as JSON
        render outPut
    }

    def educationalInfoEdit(Long id) {
        if (!request.method.equals('POST')) {
            redirect(action: 'profile')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        EducationalInfo info = EducationalInfo.read(id)
        if (!info) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.OBJ, info)
        outPut = result as JSON
        render outPut
    }

    def educationalInfoDelete(Long id) {
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        EducationalInfo info = EducationalInfo.get(id)
        if (!info) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
        }
        try {
            info.delete(flush: true)
            result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
            result.put(CommonUtils.MESSAGE, "EducationalInfo Deleted Successfully.")

        }
        catch (DataIntegrityViolationException e) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "EducationalInfo already in use. You Can Inactive Reference")
        }
        outPut = result as JSON
        render outPut
    }
    def educationalInfoInactive(Long id) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        EducationalInfo info = EducationalInfo.get(id)
        if (!info) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        info.activeStatus = ActiveStatus.INACTIVE
        info.save(flush: true)
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, 'Inactivated Successfully')
        outPut = result as JSON
        render outPut
    }
    def employmentHistoryInactive(Long id) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        EmploymentHistory history = EmploymentHistory.get(id)
        if (!history) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        history.activeStatus = ActiveStatus.INACTIVE
        history.save(flush: true)
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, 'Inactivated Successfully')
        outPut = result as JSON
        render outPut
    }

}
