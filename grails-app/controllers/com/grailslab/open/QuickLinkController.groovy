package com.grailslab.open

import com.grailslab.CommonUtils
import com.grailslab.command.OpenQuickLinkCommand
import com.grailslab.enums.LinkType
import com.grailslab.gschoolcore.ActiveStatus
import grails.converters.JSON

class QuickLinkController {

    def messageSource
    def quickLinkService

    def index() {
        LinkedHashMap resultMap = quickLinkService.paginateList(params)
        def quickLinkType = LinkType.linkTypeContents()
        if (!resultMap || resultMap.totalCount == 0) {
            render(view: '/open/quickLink/quickLink', model: [dataReturn: null, totalCount: 0, quickLinkType:quickLinkType])
            return
        }
        int totalCount = resultMap.totalCount
        render(view: '/open/quickLink/quickLink', model: [dataReturn: resultMap.results, totalCount: totalCount, quickLinkType:quickLinkType])
    }

    def list() {
        LinkedHashMap gridData
        String result
        LinkedHashMap resultMap = quickLinkService.paginateList(params)

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

    def save(OpenQuickLinkCommand command) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
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
        OpenQuickLink quickLink
        String message
        if (command.id) {
            quickLink = OpenQuickLink.get(command.id)
            if (!quickLink) {
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
                outPut = result as JSON
                render outPut
                return
            }
            quickLink.properties = command.properties
            result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
            message = 'Quick Link Updated successfully'
        } else {
            quickLink = new OpenQuickLink(command.properties)
            result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
            message = 'Quick Link Added successfully'
        }
        if (quickLink.hasErrors() || !quickLink.save()) {
            def errorList = quickLink?.errors?.allErrors?.collect { messageSource.getMessage(it, null) }
            result.put(CommonUtils.MESSAGE, errorList?.join('\n'))
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            return 
        }
        result.put(CommonUtils.MESSAGE, message)
        outPut = result as JSON
        render outPut
    }

    def edit(Long id) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        OpenQuickLink quickLink = OpenQuickLink.read(id)
        if (!quickLink) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.OBJ, quickLink)
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
        OpenQuickLink quickLink = OpenQuickLink.get(id)
        if (!quickLink) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }

        if(quickLink.activeStatus.equals(ActiveStatus.INACTIVE)){
            quickLink.activeStatus=ActiveStatus.ACTIVE
        }else {
            quickLink.activeStatus=ActiveStatus.INACTIVE
        }

        quickLink.save(flush: true)
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, 'Quick Link Status Changed Successfully')
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
        OpenQuickLink quickLink = OpenQuickLink.get(id)
        if (!quickLink) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        quickLink.delete()
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, 'Quick Link Deleted Successfully')
        outPut = result as JSON
        render outPut
    }
}
