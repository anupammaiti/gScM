package com.grailslab.admin

import com.grailslab.CommonUtils
import com.grailslab.command.NoticeBoardCommand
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.settings.NoticeBoard
import grails.converters.JSON
import org.springframework.dao.DataIntegrityViolationException

class NoticeBoardController {

    def noticeBoardService
    def messageSource

    def index() {
        LinkedHashMap resultMap = noticeBoardService.noticeBoardPaginateList(params)
        if (!resultMap || resultMap.totalCount == 0) {
            render(view: '/admin/notice/noticeBoard', model: [dataReturn: null, totalCount: 0])
            return
        }
        int totalCount = resultMap.totalCount
        render(view: '/admin/notice/noticeBoard', model: [dataReturn: resultMap.results, totalCount: totalCount])
    }
    def list() {
        LinkedHashMap gridData
        String result
        LinkedHashMap resultMap = noticeBoardService.noticeBoardPaginateList(params)

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

    def save(NoticeBoardCommand command) {
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

        NoticeBoard noticeBoard
        if (command.id) {
            noticeBoard = NoticeBoard.get(command.id)
            if (!noticeBoard) {
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
                outPut=result as JSON
                render outPut
                return
            }
            noticeBoard.properties = command.properties
            result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
            result.put(CommonUtils.MESSAGE,'Data Updated successfully')
        } else {
            noticeBoard = new NoticeBoard(command.properties)
            result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
            result.put(CommonUtils.MESSAGE,'Data Added successfully')
        }

        if(noticeBoard.hasErrors() || !noticeBoard.save()){
            def errorList = noticeBoard?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            result.put(CommonUtils.MESSAGE,errorList?.join('\n'))
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
        }
        outPut=result as JSON
        render outPut
    }

    def edit(Long id) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        NoticeBoard noticeBoard = NoticeBoard.read(id)
        if (!noticeBoard) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.OBJ, noticeBoard)
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
        NoticeBoard noticeBoard = NoticeBoard.get(id)
        if (!noticeBoard) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        noticeBoard.activeStatus=ActiveStatus.INACTIVE
        noticeBoard.save(flush: true)
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, 'Inactivated Successfully')
        outPut = result as JSON
        render outPut
    }

    def delete(Long id) {
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        NoticeBoard noticeBoard = NoticeBoard.get(id)
        if (!noticeBoard) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
        }
        try {
            noticeBoard.delete(flush: true)
            result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
            result.put(CommonUtils.MESSAGE, "Data Deleted Successfully.")

        }
        catch (DataIntegrityViolationException e) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "Data already in use. You Can Inactive Reference")
        }
        outPut = result as JSON
        render outPut
    }
}
