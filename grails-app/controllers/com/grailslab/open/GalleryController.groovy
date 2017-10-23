package com.grailslab.open

import com.grailslab.CommonUtils
import com.grailslab.command.GalleryItemCommand
import com.grailslab.command.OpenGalleryCommand
import com.grailslab.enums.OpenContentType
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.settings.Image
import grails.converters.JSON
import org.springframework.web.multipart.commons.CommonsMultipartFile

import javax.servlet.http.HttpServletRequest

class GalleryController {

    def galleryService
    def messageSource
    def uploadService

    def index() {
        redirect(action: 'picture')
    }

    def picture(){
        params.albumType = OpenContentType.PICTURE.key
        LinkedHashMap resultMap = galleryService.albumPaginateList(params)

        if (!resultMap || resultMap.totalCount == 0) {
            render(view: '/open/gallery/admin/pictureAlbum', model: [dataReturn: null, totalCount: 0])
            return
        }
        int totalCount = resultMap.totalCount
        render(view: '/open/gallery/admin/pictureAlbum', model: [dataReturn: resultMap.results, totalCount: totalCount])
    }

    def video(){
        params.albumType = OpenContentType.VIDEO.key
        LinkedHashMap resultMap = galleryService.albumPaginateList(params)

        if (!resultMap || resultMap.totalCount == 0) {
            render(view: '/open/gallery/admin/videoAlbum', model: [dataReturn: null, totalCount: 0])
            return
        }
        int totalCount = resultMap.totalCount
        render(view: '/open/gallery/admin/videoAlbum', model: [dataReturn: resultMap.results, totalCount: totalCount])
    }

    def albumList() {
        LinkedHashMap gridData
        String result
        LinkedHashMap resultMap = galleryService.albumPaginateList(params)

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

    def saveGallery(OpenGalleryCommand command) {
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
        OpenGallery openGallery
        String message
        def refAlreadyExist
        if (command.id) {
            refAlreadyExist = OpenGallery.findByNameAndIdNotEqual(command.name, command.id)
        } else {
            refAlreadyExist = OpenGallery.findByName(command.name)
        }
        if (refAlreadyExist) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "Album already exist.")
            outPut = result as JSON
            render outPut
            return
        }

        if (command.id) {
            openGallery = OpenGallery.get(command.id)
            if (!openGallery) {
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
                outPut=result as JSON
                render outPut
                return
            }
            openGallery.properties = command.properties
            openGallery.save(flush: true)
            result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
            message ='Album Updated successfully'
        } else {
            openGallery = new OpenGallery(command.properties)
            result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
            message ='Album Added successfully'
        }

        if(openGallery.hasErrors() || !openGallery.save()){
            def errorList = openGallery?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            message = errorList?.join('\n')
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
        }
        result.put(CommonUtils.MESSAGE,message)
        outPut=result as JSON
        render outPut
    }

    def editGallery(Long id) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        OpenGallery openGallery = OpenGallery.read(id)
        if (!openGallery) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.OBJ, openGallery)
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
        OpenGallery openGallery = OpenGallery.get(id)
        if (!openGallery) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        String successMsg
        if (openGallery.activeStatus.equals(ActiveStatus.ACTIVE)) {
            openGallery.activeStatus = ActiveStatus.INACTIVE
            successMsg = "Album Inactive Successfully."
        } else if (openGallery.activeStatus.equals(ActiveStatus.INACTIVE)) {
            openGallery.activeStatus = ActiveStatus.ACTIVE
            successMsg = "Album Active Successfully"
        }
        openGallery.save(flush: true)
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, successMsg)
        outPut = result as JSON
        render outPut
    }

    def pictureItem(){
        LinkedHashMap resultMap = galleryService.itemPaginateList(params)

        if (!resultMap || resultMap.totalCount == 0) {
            render(view: '/open/gallery/admin/pictureItem', model: [dataReturn: null, totalCount: 0])
            return
        }
        int totalCount = resultMap.totalCount
        render(view: '/open/gallery/admin/pictureItem', model: [dataReturn: resultMap.results, totalCount: totalCount])
    }

    def videoItem(){
        LinkedHashMap resultMap = galleryService.itemPaginateList(params)

        if (!resultMap || resultMap.totalCount == 0) {
            render(view: '/open/gallery/admin/videoItem', model: [dataReturn: null, totalCount: 0])
            return
        }
        int totalCount = resultMap.totalCount
        render(view: '/open/gallery/admin/videoItem', model: [dataReturn: resultMap.results, totalCount: totalCount])
    }

    def itemList() {
        LinkedHashMap gridData
        String result
        LinkedHashMap resultMap = galleryService.itemPaginateList(params)

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

    def saveItem(GalleryItemCommand command) {
        if (!request.method.equals('POST')) {
            redirect(action: 'imageGallery')
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
        OpenGalleryItem galleryItem
        String message
        Image image

        if (command.id) {
            galleryItem = OpenGalleryItem.get(command.id)
            if (!galleryItem) {
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
                outPut=result as JSON
                render outPut
                return
            }
            galleryItem.properties['openGallery','name','description','sortOrder'] = command.properties
            if(command.galleryType==OpenContentType.PICTURE){
                HttpServletRequest request = request
                CommonsMultipartFile f = request.getFile("pImage")
                message ='Picture Updated successfully'
                if (!f.empty) {
                    if (galleryItem.itemPath) {
                        try {
                            Boolean deleteStatus = galleryService.deleteImage(galleryItem.itemPath)
                        } catch (Exception e) {
                            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                            result.put(CommonUtils.MESSAGE, e.toString())
                            outPut = result as JSON
                            render outPut
                            return
                        }
                    }
                    try {
                        image = uploadService.uploadImageWithThumb(request, "pImage", "galleryItem")
                        galleryItem.itemPath = image?.identifier
                    } catch (Exception e) {
                        result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                        result.put(CommonUtils.MESSAGE, e.toString())
                        outPut = result as JSON
                        render outPut
                        return
                    }
                }
            }else {
                message ='Video Link Updated successfully'
                galleryItem.itemPath = command.itemPath
            }
            galleryItem.save(flush: true)
            result.put(CommonUtils.IS_ERROR,Boolean.FALSE)

        } else {
            galleryItem = new OpenGalleryItem(command.properties)
            if(command.galleryType==OpenContentType.PICTURE){
                HttpServletRequest request = request
                message ='Picture Added successfully'
                CommonsMultipartFile f = request.getFile("pImage")
                if (!f.empty) {
                    try {
                        image = uploadService.uploadImageWithThumb(request, "pImage", "galleryItem")
                        galleryItem.itemPath = image?.identifier

                    } catch (Exception e) {
                        result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                        result.put(CommonUtils.MESSAGE, e.toString())
                        outPut = result as JSON
                        render outPut
                        return
                    }
                }
            }else {
                message ='Video Link Added successfully'
                galleryItem.itemPath = command.itemPath
            }
            result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
        }

        if(galleryItem.hasErrors() || !galleryItem.save()){
            def errorList = galleryItem?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            message = errorList?.join('\n')
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
        }
        result.put(CommonUtils.MESSAGE,message)
        outPut=result as JSON
        render outPut
    }

    def editItem(Long id) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        OpenGalleryItem galleryItem = OpenGalleryItem.read(id)
        if (!galleryItem) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.OBJ, galleryItem)
        outPut = result as JSON
        render outPut
    }

    def inactiveItem(Long id) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        OpenGalleryItem galleryItem = OpenGalleryItem.get(id)
        if (!galleryItem) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        String message
        if (galleryItem.activeStatus.equals(ActiveStatus.ACTIVE)) {
            galleryItem.activeStatus = ActiveStatus.INACTIVE
            message="Item Inactivate successfully"
        } else if (galleryItem.activeStatus.equals(ActiveStatus.INACTIVE)) {
            galleryItem.activeStatus = ActiveStatus.ACTIVE
            message="Item activated successfully"
        }
        galleryItem.save(flush: true)
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, message)
        outPut = result as JSON
        render outPut
    }

    /*def deleteItem(Long id) {
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        OpenGalleryItem galleryItem = OpenGalleryItem.get(id)
        if (!galleryItem) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
        }
        try {
            Boolean deleteStatus = galleryService.deleteImage(galleryItem.imagePath)
            galleryItem.delete(flush: true)
            result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
            result.put(CommonUtils.MESSAGE, "Data Deleted Successfully.")

        }
        catch (DataIntegrityViolationException e) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "Data already in use. You Can Inactive Reference")
        }
        outPut = result as JSON
        render outPut
    }*/
}
