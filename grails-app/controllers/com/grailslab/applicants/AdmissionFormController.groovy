package com.grailslab.applicants

import com.grailslab.CommonUtils
import com.grailslab.command.RegFormCommand
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.settings.ClassName
import com.grailslab.stmgmt.RegForm
import com.grailslab.stmgmt.RegOnlineRegistration
import grails.converters.JSON
import org.joda.time.DateTime

class AdmissionFormController {

    def classNameService
    def admissionFormService
    def sequenceGeneratorService

    def index() {
        LinkedHashMap resultMap = admissionFormService.paginateList(params)
        if (!resultMap || resultMap.totalCount == 0) {
            render(view: '/admissionForm/admissionFormList', model: [dataReturn: null, totalCount: 0])
            return
        }
        int totalCount = resultMap.totalCount
        render(view: '/admissionForm/admissionFormList', model: [dataReturn: resultMap.results, totalCount: totalCount])

    }


    def create(long id) {
        def classList = classNameService.classNameDropDownList()
         render(view: '/admissionForm/admissionCreateOrEdit' , model: [ classList:classList])

    }

    def list() {
        LinkedHashMap gridData
        String result
        LinkedHashMap resultMap = admissionFormService.paginateList(params)

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

    def save(RegFormCommand command){
        switch (request.method) {
            case 'GET':
                redirect(action: 'create')
                break
            case 'POST':
                def classList
                if (command.hasErrors()) {
                    classList = classNameService.classNameDropDownList()
                    render(view: '/admissionForm/admissionCreateOrEdit', model: [classList: classList, command: command])
                    return
                }
                DateTime regCloseDateTime = new DateTime(command.regCloseDate).plusHours(23).plusMinutes(59)
                Date toBirthDate
                if (command.toBirthDate) {
                    DateTime birthCloseDateTime = new DateTime(command.toBirthDate).plusHours(23).plusMinutes(59)
                    toBirthDate = birthCloseDateTime.toDate()
                }
                ClassName pName
                RegForm regForm
                if (command.regFormId) {
                    RegForm regFormedit = RegForm.get(command.regFormId)
                    regFormedit.properties["regOpenDate","fromBirthDate","instruction","applyType","formPrice"] = command.properties
                    regFormedit.regCloseDate = regCloseDateTime.toDate()
                    regFormedit.toBirthDate = toBirthDate
                    regFormedit.save(flush: true)
                    redirect(action: 'index')
                    return

                } else {
                    //Form Serial No
                    String formSeqkey = "SL${command.academicYear.key}"
                    if (RegForm.countByAcademicYearAndActiveStatus(command.academicYear, ActiveStatus.ACTIVE) == 0) {
                        sequenceGeneratorService.initSequence(formSeqkey, null, null, 0, 'SL%05d')
                        sequenceGeneratorService.nextNumber(formSeqkey)
                    }
                    //Form submit no
                    String initial = message(code: 'app.school.initial.name')
                    String classSeqKey
                    sequenceGeneratorService.initSequence(initial+command.academicYear.key, null, null, 0, "$initial%05d")
                    def classIds = command.schoolClassIds.split(",")
                    RegForm previousAdded
                    classIds.each {idx ->
                        pName = ClassName.read(Long.valueOf(idx))
                        classSeqKey = initial+pName.sortPosition+command.academicYear.key
                        sequenceGeneratorService.initSequence(classSeqKey, null, null, 0, "$initial$pName.sortPosition%04d")
                        sequenceGeneratorService.nextNumber(classSeqKey)
                        regForm = RegForm.findByClassNameAndAcademicYearAndActiveStatus(pName, command.academicYear, ActiveStatus.ACTIVE)
                        if (regForm) {
                            regForm.properties = command.properties
                            regForm.regCloseDate = regCloseDateTime.toDate()
                            regForm.toBirthDate = toBirthDate
                            if (!regForm.serialPrefix){
                                regForm.serialPrefix = formSeqkey
                                regForm.applicationPrefix = classSeqKey
                            }
                        } else {
                            regForm = new RegForm(command.properties)
                            regForm.regCloseDate = regCloseDateTime.toDate()
                            regForm.toBirthDate = toBirthDate
                            regForm.className = pName
                            regForm.serialPrefix = formSeqkey
                            regForm.applicationPrefix = classSeqKey
                        }
                        regForm.save(flush: true)
                    }
                    redirect(action:'index')
                    break
                }
        }
    }

    def edit(Long id) {
        RegForm regAdmissionForm = RegForm.read(id)
        if (!regAdmissionForm) {
            flash.message=CommonUtils.COMMON_NOT_FOUND_MESSAGE
            redirect(action: 'index')
            return
        }
        render(view: '/admissionForm/admissionCreateOrEdit', model: [regAdmissionForm:regAdmissionForm])
    }

    def delete(Long id) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        RegForm regForm = RegForm.get(id)
        if(!regForm){
            result.put(CommonUtils.IS_ERROR,true)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        int appliedCount = RegOnlineRegistration.countByRegFormIdAndActiveStatus(regForm.id, ActiveStatus.ACTIVE)
        if (appliedCount > 0) {
            result.put(CommonUtils.IS_ERROR,true)
            result.put(CommonUtils.MESSAGE,"$appliedCount applicant already applied. To delete application form, you need to delete them first")
            outPut = result as JSON
            render outPut
            return
        }

        if(regForm.activeStatus.equals(ActiveStatus.DELETE)){
            regForm.activeStatus=ActiveStatus.ACTIVE
        } else {
            regForm.activeStatus=ActiveStatus.DELETE
        }

        regForm.save()
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, "Program Deleted successfully.")
        outPut = result as JSON
        render outPut
    }
}
