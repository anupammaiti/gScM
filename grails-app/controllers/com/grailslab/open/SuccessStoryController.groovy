package com.grailslab.open

import com.grailslab.CommonUtils
import com.grailslab.command.SuccessStoryCommand
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.settings.Image
import grails.converters.JSON
import org.springframework.web.multipart.commons.CommonsMultipartFile

import javax.servlet.http.HttpServletRequest

class SuccessStoryController {
    def messageSource
    def successStoryService
    def uploadService

    def index() {
        LinkedHashMap resultMap = successStoryService.paginateList(params)
        if (!resultMap || resultMap.totalCount == 0) {
            render(view: '/open/success/successList', model: [dataReturn: null, totalCount: 0])
            return
        }
        int totalCount = resultMap.totalCount
        render(view: '/open/success/successList', model: [dataReturn: resultMap.results, totalCount: totalCount])
    }

    def list() {
        LinkedHashMap gridData
        String result
        LinkedHashMap resultMap = successStoryService.paginateList(params)

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
    def create(){
        render(view: '/open/success/successCreateOrEdit')
    }
    def edit(Long id) {
        OpenSuccessStory successStory = OpenSuccessStory.read(id)
        if (!successStory) {
            flash.message=CommonUtils.COMMON_NOT_FOUND_MESSAGE
            redirect(action: 'index')
            return
        }
        render(view: '/open/success/successCreateOrEdit', model: [successStory:successStory])
    }


    def save(SuccessStoryCommand command) {
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

        OpenSuccessStory successStory
        if (command.id) {
            successStory = OpenSuccessStory.get(command.id)
            if (!successStory) {
                flash.message=CommonUtils.COMMON_NOT_FOUND_MESSAGE
                redirect(action: 'index')
                return
            }
            successStory.properties = command.properties
            flash.message = 'Content Updated Successfully.'
        } else {
            successStory = new OpenSuccessStory(command.properties)
            flash.message = 'Content Added Successfully.'
        }

        //code for save image
        HttpServletRequest request = request
        CommonsMultipartFile f = request.getFile("pImage")
        if (!f.empty) {
            if (successStory.imagePath) {
                try {
                    uploadService.deleteImage(successStory.imagePath)
                } catch (Exception e) {
                    log.error("Delete Image"+ e)
                }
            }
            try {
                Image image = uploadService.uploadImageWithThumb(request, "pImage", "successStory")
                successStory.imagePath=image?.identifier
            } catch (Exception e) {
                log.error("Upload MgmgVoice Image"+ e)
                flash.message=e.getMessage()
                redirect(action: 'index')
                return
            }
        }
        //code for save image
        if (successStory.hasErrors() || !successStory.save()) {
            def errorList = successStory?.errors?.allErrors?.collect { messageSource.getMessage(it, null) }
            flash.message = errorList?.join('\n')
            redirect(action: 'index')
            return
        }
        redirect(action: 'index')
    }

    def inactive(Long id) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        OpenSuccessStory successStory = OpenSuccessStory.get(id)
        if (!successStory) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }

        if(successStory.activeStatus.equals(ActiveStatus.INACTIVE)){
            successStory.activeStatus=ActiveStatus.ACTIVE
        }else {
            successStory.activeStatus=ActiveStatus.INACTIVE
        }

        successStory.save()
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, 'Status Change Successfully')
        outPut = result as JSON
        render outPut
    }

    def delete(Long id) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        OpenSuccessStory successStory = OpenSuccessStory.get(id)
        if (!successStory) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        uploadService.deleteImage(successStory.imagePath)
        successStory.delete()
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, 'Content Deleted Successfully')
        outPut = result as JSON
        render outPut
    }
}
