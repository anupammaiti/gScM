package com.grailslab.open

import com.grailslab.CommonUtils
import com.grailslab.command.OpenContentCommand
import com.grailslab.enums.OpenContentType
import com.grailslab.gschoolcore.ActiveStatus
import grails.converters.JSON

class FeatureController {

    def messageSource
    def featureService

    def index() {
        def openContentTypes = OpenContentType.featureContents()
        LinkedHashMap resultMap = featureService.featurePaginateList(params)
        if (!resultMap || resultMap.totalCount == 0) {
            render(view: '/open/feature/allfeature', model: [dataReturn: null, totalCount: 0, openContentTypes: openContentTypes])
            return
        }
        int totalCount = resultMap.totalCount
        render(view: '/open/feature/allfeature', model: [dataReturn: resultMap.results, totalCount: totalCount, openContentTypes: openContentTypes])
    }

    def list() {
        LinkedHashMap gridData
        String result
        LinkedHashMap resultMap = featureService.featurePaginateList(params)

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
        if(!params.featureType){
            redirect(action: 'index')
            return
        }
        OpenContentType openContentType = OpenContentType.valueOf(params.featureType)
        if(!openContentType){
            redirect(action: 'index')
            return
        }
        render(view: '/open/feature/createOrEdit', model: [openContentType:openContentType.key])
    }
    def edit(Long id) {
        OpenContent openContent = OpenContent.read(id)
        if (!openContent) {
            flash.message="Feature not found"
            redirect(action: 'index')
            return
        }
        render(view: '/open/feature/createOrEdit', model: [openContent:openContent])
    }


    def save(OpenContentCommand command) {
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

        OpenContent openContent
        if (command.id) {
            openContent = OpenContent.get(command.id)
            if (!openContent) {
                flash.message=CommonUtils.COMMON_NOT_FOUND_MESSAGE
                redirect(action: 'index')
                return
            }
            openContent.properties['title','sortOrder','iconClass','description'] = command.properties
            flash.message = 'Content Updated Successfully.'
        } else {
            openContent = new OpenContent(command.properties)
            flash.message = 'Content Added Successfully.'
        }
        if (openContent.hasErrors() || !openContent.save()) {
            def errorList = openContent?.errors?.allErrors?.collect { messageSource.getMessage(it, null) }
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
        OpenContent openContent = OpenContent.get(id)
        if (!openContent) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }

        if(openContent.activeStatus.equals(ActiveStatus.INACTIVE)){
            openContent.activeStatus=ActiveStatus.ACTIVE
        }else {
            openContent.activeStatus=ActiveStatus.INACTIVE
        }

        openContent.save(flush: true)
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
        OpenContent openContent = OpenContent.get(id)
        if (!openContent) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }

        openContent.delete()

        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, 'Content Deleted Successfully')
        outPut = result as JSON
        render outPut
    }
}
