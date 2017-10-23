package com.grailslab.hr

import com.grailslab.CommonUtils
import com.grailslab.command.CommonCommand
import com.grailslab.gschoolcore.ActiveStatus
import grails.converters.JSON
import org.springframework.dao.DataIntegrityViolationException

class HrSettingsController {

    def hrSettingsService
    def messageSource

    def board() {
        LinkedHashMap resultMap = hrSettingsService.boardPaginateList(params)
        if (!resultMap || resultMap.totalCount == 0) {
            render(view: '/hr/board', model: [dataReturn: null, totalCount: 0])
            return
        }
        int totalCount = resultMap.totalCount
        render(view: '/hr/board', model: [dataReturn: resultMap.results, totalCount: totalCount])
    }

    def boardList() {
        LinkedHashMap gridData
        String result
        LinkedHashMap resultMap = hrSettingsService.boardPaginateList(params)

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

    def boardSave(CommonCommand command) {
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
        def refAlreadyExist
        if(command.id){
            refAlreadyExist = Board.findByActiveStatusAndNameAndIdNotEqual(ActiveStatus.ACTIVE,command.name,command.id)
        } else {
            refAlreadyExist = Board.findByActiveStatusAndName(ActiveStatus.ACTIVE,command.name)
        }
        if(refAlreadyExist){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "Name already exist.")
            outPut=result as JSON
            render outPut
            return
        }
        Board board
        if (command.id) {
            board = Board.get(command.id)
            if (!board) {
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
                outPut=result as JSON
                render outPut
                return
            }
            board.properties = command.properties
            result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
            result.put(CommonUtils.MESSAGE,'Data Updated successfully')
        } else {
            board = new Board(command.properties)
            result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
            result.put(CommonUtils.MESSAGE,'Data Added successfully')
        }

        if(board.hasErrors() || !board.save()){
            def errorList = board?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            result.put(CommonUtils.MESSAGE,errorList?.join('\n'))
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
        }
        outPut=result as JSON
        render outPut
    }

    def boardEdit(Long id) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        Board board = Board.read(id)
        if (!board) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.OBJ, board)
        outPut = result as JSON
        render outPut
    }

    def boardInactive(Long id) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        Board board = Board.get(id)
        if (!board) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        board.activeStatus=ActiveStatus.INACTIVE
        board.save(flush: true)
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, 'Inactive Successfully')
        outPut = result as JSON
        render outPut
    }

    def boardDelete(Long id) {
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        Board board = Board.get(id)
        if (!board) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
        }
        try {
            board.delete(flush: true)
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

    def certification() {
        LinkedHashMap resultMap = hrSettingsService.certificationPaginateList(params)
        if (!resultMap || resultMap.totalCount == 0) {
            render(view: '/hr/certification', model: [dataReturn: null, totalCount: 0])
            return
        }
        int totalCount = resultMap.totalCount
        render(view: '/hr/certification', model: [dataReturn: resultMap.results, totalCount: totalCount])
    }

    def certificationList() {
        LinkedHashMap gridData
        String result
        LinkedHashMap resultMap = hrSettingsService.certificationPaginateList(params)

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

    def certificationSave(CommonCommand command) {
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
        def refAlreadyExist
        if(command.id){
            refAlreadyExist = HrHrCertification.findByActiveStatusAndNameAndIdNotEqual(ActiveStatus.ACTIVE,command.name,command.id)
        } else {
            refAlreadyExist = HrCertification.findByActiveStatusAndName(ActiveStatus.ACTIVE,command.name)
        }
        if(refAlreadyExist){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "Name already exist.")
            outPut=result as JSON
            render outPut
            return
        }
        HrCertification certification
        if (command.id) {
            certification = HrCertification.get(command.id)
            if (!certification) {
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
                outPut=result as JSON
                render outPut
                return
            }
            certification.properties = command.properties
            result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
            result.put(CommonUtils.MESSAGE,'Data Updated successfully')
        } else {
            certification = new HrCertification(command.properties)
            result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
            result.put(CommonUtils.MESSAGE,'Data Added successfully')
        }

        if(certification.hasErrors() || !certification.save()){
            def errorList = certification?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            result.put(CommonUtils.MESSAGE,errorList?.join('\n'))
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
        }
        outPut=result as JSON
        render outPut
    }

    def certificationEdit(Long id) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        HrCertification certification = HrCertification.read(id)
        if (!certification) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.OBJ, certification)
        outPut = result as JSON
        render outPut
    }

    def certificationInactive(Long id) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        HrCertification certification = HrCertification.get(id)
        if (!certification) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        certification.activeStatus=ActiveStatus.INACTIVE
        certification.save(flush: true)
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, 'Inactive Successfully')
        outPut = result as JSON
        render outPut
    }

    def certificationDelete(Long id) {
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        HrCertification certification = HrCertification.get(id)
        if (!certification) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
        }
        try {
            certification.delete(flush: true)
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

    def institution() {
        LinkedHashMap resultMap = hrSettingsService.institutionPaginateList(params)
        if (!resultMap || resultMap.totalCount == 0) {
            render(view: '/hr/institution', model: [dataReturn: null, totalCount: 0])
            return
        }
        int totalCount = resultMap.totalCount
        render(view: '/hr/institution', model: [dataReturn: resultMap.results, totalCount: totalCount])
    }

    def institutionList() {
        LinkedHashMap gridData
        String result
        LinkedHashMap resultMap = hrSettingsService.institutionPaginateList(params)

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

    def institutionSave(CommonCommand command) {
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
        def refAlreadyExist
        if(command.id){
            refAlreadyExist = Institution.findByActiveStatusAndNameAndIdNotEqual(ActiveStatus.ACTIVE,command.name,command.id)
        } else {
            refAlreadyExist = Institution.findByActiveStatusAndName(ActiveStatus.ACTIVE,command.name)
        }
        if(refAlreadyExist){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "Name already exist.")
            outPut=result as JSON
            render outPut
            return
        }
        Institution institution
        if (command.id) {
            institution = Institution.get(command.id)
            if (!institution) {
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
                outPut=result as JSON
                render outPut
                return
            }
            institution.properties = command.properties
            result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
            result.put(CommonUtils.MESSAGE,'Data Updated successfully')
        } else {
            institution = new Institution(command.properties)
            result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
            result.put(CommonUtils.MESSAGE,'Data Added successfully')
        }

        if(institution.hasErrors() || !institution.save()){
            def errorList = institution?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            result.put(CommonUtils.MESSAGE,errorList?.join('\n'))
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
        }
        outPut=result as JSON
        render outPut
    }

    def institutionEdit(Long id) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        Institution institution = Institution.read(id)
        if (!institution) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.OBJ, institution)
        outPut = result as JSON
        render outPut
    }

    def institutionInactive(Long id) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        Institution institution = Institution.get(id)
        if (!institution) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        institution.activeStatus=ActiveStatus.INACTIVE
        institution.save(flush: true)
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, 'Inactive Successfully')
        outPut = result as JSON
        render outPut
    }

    def institutionDelete(Long id) {
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        Institution institution = Institution.get(id)
        if (!institution) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
        }
        try {
            institution.delete(flush: true)
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
