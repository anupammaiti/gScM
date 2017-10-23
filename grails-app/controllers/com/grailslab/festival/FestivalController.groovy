package com.grailslab.festival

import com.grailslab.CommonUtils
import com.grailslab.command.FestivalCommand
import com.grailslab.enums.PrintOptionType
import com.grailslab.gschoolcore.ActiveStatus
import grails.converters.JSON
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef

class FestivalController {

    def festivalService
    def messageSource
    def fesRegistrationService
    def classNameService
    def schoolService
    def jasperService


    def index() {
        LinkedHashMap resultMap = festivalService.paginateList(params)
        if (!resultMap || resultMap.totalCount == 0)

        {
            render(view: '/festival/festivalList',model: [dataReturn: null, totalCount: 0])
            return
        }
        int totalCount = resultMap.totalCount
        render(view: '/festival/festivalList',model: [dataReturn: resultMap.results, totalCount: totalCount])
    }
    def create() {
        render(view: '/festival/festivalCreateOrEdit')

    }
    def list() {
        LinkedHashMap gridData
        String result
        LinkedHashMap resultMap = festivalService.paginateList(params)

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
        FesProgram fesProgram = FesProgram.read(id)
        if (!fesProgram) {
            flash.message=CommonUtils.COMMON_NOT_FOUND_MESSAGE
            redirect(action: 'index')
            return
        }
        List selectedTopics = fesProgram.olympiadTopics?.split(",") as List
        render(view: '/festival/festivalCreateOrEdit', model: [fesProgram: fesProgram, selectedTopics: selectedTopics])
    }

    def save(FestivalCommand command) {
        if (!request.method.equals('POST')) {
            flash.message="Request Method not allow here."
            redirect(action: 'index')
            return
        }
        if (command.hasErrors()) {
            def errorList = command?.errors?.allErrors?.collect { messageSource.getMessage(it, null) }
            flash.message=errorList?.join('\n')
            redirect(action: 'index')
            return
        }

        FesProgram festivalList
        if (command.id) {
            festivalList = FesProgram.get(command.id)
            if (!festivalList) {
                flash.message=CommonUtils.COMMON_NOT_FOUND_MESSAGE
                redirect(action: 'index')
                return
            }
            festivalList.properties = command.properties
            flash.message = 'Program Updated Successfully.'
        } else {
            festivalList = new FesProgram(command.properties)
            flash.message = 'Program Added Successfully.'
        }
        festivalList.save()
        redirect(action: 'index')
    }

    def delete(Long id) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        FesProgram fesProgram = FesProgram.get(id)
        if(!fesProgram){
            result.put(CommonUtils.IS_ERROR,true)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }

        if(fesProgram.activeStatus.equals(ActiveStatus.DELETE)){
            fesProgram.activeStatus=ActiveStatus.ACTIVE
        } else {
            fesProgram.activeStatus=ActiveStatus.DELETE
        }

        fesProgram.save()
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, "Program Deleted successfully.")
        outPut = result as JSON
        render outPut
    }

    def registration(Long id) {
        def fesProgramList = festivalService.festivalDDList()
        if (!id && !fesProgramList) {
            render(view: '/festival/fesRegistrationList',model: [dataReturn: null, totalCount: 0, fesProgramList: fesProgramList])
            return
        }
        if (fesProgramList) {
            id = fesProgramList.first().id
        }
        FesProgram fesProgram = FesProgram.read(id)
        if (!fesProgram) {
            render(view: '/festival/fesRegistrationList',model: [dataReturn: null, totalCount: 0, fesProgramList: fesProgramList])
            return
        }

        LinkedHashMap resultMap = fesRegistrationService.paginateList(params)
        if (!resultMap || resultMap.totalCount == 0) {
            render(view: '/festival/fesRegistrationList',model: [fesProgram: fesProgram, dataReturn: null, totalCount: 0, fesProgramList: fesProgramList])
            return
        }
        int totalCount = resultMap.totalCount
        render(view: '/festival/fesRegistrationList',model: [fesProgram: fesProgram, dataReturn: resultMap.results, totalCount: totalCount, fesProgramList: fesProgramList])
    }
    def fesRegList() {
        LinkedHashMap gridData
        String result
        LinkedHashMap resultMap = fesRegistrationService.paginateList(params)

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
    def fesRegDelete(Long id) {
        if (!request.method.equals('POST')) {
            redirect(action: 'festival')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        FesRegistration fesRegistration = FesRegistration.get(id)
        if(!fesRegistration){
            result.put(CommonUtils.IS_ERROR,true)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }

        if(fesRegistration.activeStatus.equals(ActiveStatus.DELETE)){
            fesRegistration.activeStatus=ActiveStatus.ACTIVE
        } else {
            fesRegistration.activeStatus=ActiveStatus.DELETE
        }

        fesRegistration.save()
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, "Registration Deleted successfully.")
        outPut = result as JSON
        render outPut
    }


    def report(){
        def fesProgramList = festivalService.festivalDDList()
        render(view: '/festival/fesReport',model: [fesProgramList: fesProgramList])
    }
    def registrationTopics(Long id) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        FesProgram fesProgram = FesProgram.read(id)
        LinkedHashMap result = new LinkedHashMap()
        String outPut

        def registrationTopicList = fesRegistrationService.registrationTopicList(fesProgram)

        if(!registrationTopicList){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "No topic added")
            outPut = result as JSON
            render outPut
            return
        }
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put('registrationTopicList', registrationTopicList)
        outPut = result as JSON
        render outPut
        return
    }
    def registrationList(){
        if (!params.fesProgram){
            flash.message="Please select Program name first"
            redirect(action: 'index')
        }
        FesProgram fesProgram = FesProgram.read(params.getLong('fesProgram'))

        String sqlParams = ""
        if (params.topicName) {
            sqlParams = " and olympiad_topics like '%"+params.topicName+"%'"
        }

        String reportFileName = REGISTRATION_LIST_JASPER_FILE
        Map paramsMap = new LinkedHashMap()
        paramsMap.put('REPORT_LOGO', schoolService.reportLogoPath())
        paramsMap.put('fesProgram', fesProgram.id)
        paramsMap.put('programName', fesProgram.name)
        paramsMap.put('sqlParams', sqlParams)



        String extension = CommonUtils.PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.PDF_FORMAT
        String printOptionType = PrintOptionType.PDF

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

        String outputFileName = "Registration_List${extension}"
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

        private static final String REGISTRATION_LIST_JASPER_FILE = 'festivalRegistrationList.jasper'
}
