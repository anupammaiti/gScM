package com.grailslab.account

import com.grailslab.accounts.GlabDetails
import com.grailslab.accounts.GlabPayment
import com.grailslab.CommonUtils
import com.grailslab.accounts.GlabPayment
import com.grailslab.gschoolcore.ActiveStatus
import grails.converters.JSON
import org.json.JSONArray
import org.json.JSONObject
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef

class GlabPaymentController {

    def glabPaymentService
    def schoolService
    def jasperService
    def accountsService

    def index() {
        render (view: '/accounts/glabPayment')
    }
    def createPayment (Long id) {
        render (view: '/accounts/createGlabPayment')
        return
    }
    def list() {
        LinkedHashMap gridData
        String result
        LinkedHashMap resultMap = glabPaymentService.list(params)

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

    def edit (Long id) {
        GlabPayment glabPayment = GlabPayment.read(id)
        render (view: '/accounts/createGlabPayment',  model: [glabPaymentId: glabPayment.id, glabPaymentList: glabPayment])
    }

    def save() {
        JSONObject saveGlabVoucher = JSON.parse(request.JSON.toString())
        def responseData = glabPaymentService.save(saveGlabVoucher)
        render responseData as JSON
    }
    def deleteDetails () {
        JSONObject deleteId = JSON.parse(request.JSON.toString())
        Long id = Long.valueOf(deleteId.id)
        GlabDetails glabDetails = GlabDetails.get(id)
        glabDetails.activeStatus = ActiveStatus.DELETE
        glabDetails.save()
        def responseData = ['hasError': false, message: "Delete successfully !"]
        render responseData as JSON
    }

    def deletePayment(Long id) {

        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        String message
        GlabPayment glabPayment = GlabPayment.get(id)
        if (!glabPayment) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }

        glabPayment.activeStatus = ActiveStatus.DELETE
        if (!glabPayment.save()) {
            def errorList = glabPayment?.errors?.allErrors?.collect { messageSource.getMessage(it, null) }
            message = errorList?.join('\n')
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            outPut = result as JSON
            render outPut
            return
        }
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, "Payment deleted successfully !")
        outPut = result as JSON
        render outPut
    }

   def paymentDetailsList (Long paymentListId) {
       GlabPayment glabPayment = GlabPayment.read(paymentListId)
       def glabDetails = GlabDetails.findAllByActiveStatusAndPaymentId(ActiveStatus.ACTIVE, glabPayment.id)
       def responseData = [glabDetails: glabDetails]
       render responseData as JSON
   }

   def print(Long id){
       String reportFileName = schoolService.reportFileName(CommonUtils.MODULE_ACCOUNTS, "glabPayment")

       String sqlParam = " gp.active_status = 'ACTIVE' and gd.active_status = 'ACTIVE'"
       GlabPayment glabPayment = GlabPayment.read(id)
       if (!glabPayment) {
           redirect(controller: 'glabPayment', action: 'index')
           return
       }
       if (glabPayment) {
           sqlParam += " and gp.id ='"+id+ "' "
       }
       Map paramsMap = new LinkedHashMap()
       paramsMap.put('schoolName', message(code: "app.school.name"))
       paramsMap.put('companyAddress', message(code: "company.address.line"))
       paramsMap.put('schoolAddress1', message(code: "app.report.address.line1"))
       paramsMap.put('schoolAddress2', message(code: "app.report.address.line2"))
       paramsMap.put('schoolAddress3', message(code: "app.report.address.line3"))
       paramsMap.put('creditLine', message(code: "company.credit.footer"))
       paramsMap.put('REPORT_LOGO', schoolService.imageDir())
       paramsMap.put('sqlParam', sqlParam)

       String extension = CommonUtils.PDF_EXTENSION
       String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
       def exportFormat = JasperExportFormat.PDF_FORMAT

       /*if(params.printOptionType && params.printOptionType !=PrintOptionType.PDF.key){
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

       String outputFileName = "glab_payment${extension}"
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
}
