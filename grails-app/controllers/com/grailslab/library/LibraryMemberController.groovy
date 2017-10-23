package com.grailslab.library

import com.grailslab.CommonUtils
import com.grailslab.command.library.LibraryMemberCommand
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.stmgmt.Registration
import com.grailslab.stmgmt.Student
import grails.converters.JSON
import org.springframework.dao.DataIntegrityViolationException

class LibraryMemberController {
    def messageSource
    def libraryMemberService
    def sequenceGeneratorService

    def index() {
            render(view: '/library/libraryMemberForm')
    }
    def list() {
        LinkedHashMap gridData
        String result
        LinkedHashMap resultMap = libraryMemberService.categoryPaginateList(params)

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
    def getInfoFromStudent(String stdId) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        Registration registration = Registration.findByStudentIDAndActiveStatus(stdId, ActiveStatus.ACTIVE)
        String memberName = registration?.fathersName
        String memberAddress = registration?.presentAddress
        String permanentAddress = registration?.permanentAddress
        String memberMobile = registration?.guardianMobile
        String memberEmail = registration?.email

        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
       // result.put(CommonUtils.OBJ, registration)
        result.put("memberName", memberName)
        result.put("memberAddress", memberAddress)
        result.put("permanentAddress", permanentAddress)
        result.put("memberMobile", memberMobile)
        result.put("memberEmail", memberEmail)
        outPut = result as JSON
        render outPut
    }
    def save(LibraryMemberCommand command){
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        if (command.hasErrors()) {
            def errorList = command?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, errorList?.join('\n'))
            outPut=result as JSON
            render outPut
            return
        }
        LibraryMember libraryMember
        String message
        if (params.objId) {
            libraryMember = LibraryMember.get(params.objId)
            if (!libraryMember) {
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
                outPut=result as JSON
                render outPut
                return
            }
            libraryMember.properties['name','address','memberShipDate','mobile','email','referenceId'] = command.properties
            result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
            message ='Library Member Updated successfully'
        } else {
            libraryMember = new LibraryMember(command.properties)
            libraryMember.memberId = nextMembershipNo()
            result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
            message ='Library Member Added successfully'
        }

        if(libraryMember.hasErrors() || !libraryMember.save()){
            def errorList = libraryMember?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            message = errorList?.join('\n')
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
        }
        result.put(CommonUtils.MESSAGE,message)
        outPut=result as JSON
        render outPut
        return
    }
    def edit(Long id) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        LibraryMember libraryMember = LibraryMember.read(id)
        if (!libraryMember) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        Student student = Student.findByStudentIDAndActiveStatus(libraryMember.referenceId, ActiveStatus.ACTIVE)
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.OBJ, libraryMember)
        result.put("studentName", student?.name)
        outPut = result as JSON
        render outPut
    }

    def delete(Long id) {
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        LibraryMember libraryMember = LibraryMember.get(id)
        if (!libraryMember) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
        }
        try {
            libraryMember.activeStatus=ActiveStatus.INACTIVE
            libraryMember.save(flush: true)
            result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
            result.put(CommonUtils.MESSAGE, "Library Member Deleted Successfully.")
        }
        catch (DataIntegrityViolationException e) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "Library in use")
        }
        outPut = result as JSON
        render outPut
    }
    String nextMembershipNo(){
        String serialNo = sequenceGeneratorService.nextNumber('libraryMembership')
    }

}
