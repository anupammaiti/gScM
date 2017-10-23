package com.grailslab

import com.grailslab.enums.OpenContentType
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.open.OpenGallery
import com.grailslab.open.OpenGalleryItem
import com.grailslab.settings.Image
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

class GalleryService {
    static transactional = false
    def schoolService
    def grailsApplication

    static final String[] sortColumns = ['id', 'name','sortOrder', 'activeStatus', 'description']

    LinkedHashMap albumPaginateList(GrailsParameterMap params) {
        int iDisplayStart = params.iDisplayStart ? params.getInt('iDisplayStart') : CommonUtils.DEFAULT_PAGINATION_START
        int iDisplayLength = params.iDisplayLength ? params.getInt('iDisplayLength') : CommonUtils.DEFAULT_PAGINATION_LENGTH
        String sSortDir = params.sSortDir_0 ? params.sSortDir_0 : CommonUtils.SORT_ORDER_DESC
        int iSortingCol = params.iSortCol_0 ? params.getInt('iSortCol_0') : CommonUtils.DEFAULT_PAGINATION_SORT_IDX
        String sSearch = params.sSearch ? params.sSearch : null
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }
        String sortColumn = CommonUtils.getSortColumn(sortColumns, iSortingCol)

        OpenContentType galleryType = OpenContentType.PICTURE
        if(params.albumType){
            galleryType = OpenContentType.valueOf(params.albumType)
        }
        List dataReturns = new ArrayList()
        def c = OpenGallery.createCriteria()
        def results = c.list(max: iDisplayLength, offset: iDisplayStart) {
            and {
                eq("galleryType", galleryType)
            }
            if (sSearch) {
                or {
                    ilike('name', sSearch)
                    ilike('description', sSearch)
                }
            }
            order(sortColumn, sSortDir)
        }
        int totalCount = results.totalCount
        int serial = iDisplayStart;
        if (totalCount > 0) {
            if (sSortDir.equals(CommonUtils.SORT_ORDER_DESC)) {
                serial = (totalCount + 1) - iDisplayStart
            }
            results.each { OpenGallery openGallery ->
                if (sSortDir.equals(CommonUtils.SORT_ORDER_ASC)) {
                    serial++
                } else {
                    serial--
                }
                dataReturns.add([DT_RowId: openGallery.id, 0: serial, 1: openGallery.name, 2: openGallery.sortOrder, 3: openGallery.activeStatus?.value, 4: openGallery.description, 5: ''])
            }
        }
        return [totalCount: totalCount, results: dataReturns]
    }

    static final String[] sortColumnsImg = ['sortOrder', 'imageTitle']
    LinkedHashMap itemPaginateList(GrailsParameterMap params) {
        int iDisplayStart = CommonUtils.DEFAULT_PAGINATION_START
        int iDisplayLength = CommonUtils.MAX_PAGINATION_LENGTH
        String sSortDir = CommonUtils.SORT_ORDER_ASC
        int iSortingCol = CommonUtils.DEFAULT_PAGINATION_SORT_IDX
        OpenGallery openGallery
        if(params.album){
            openGallery = OpenGallery.read(params.getLong('album'))
        }
        if(!openGallery){
            return null
        }
        String sortColumn = CommonUtils.getSortColumn(sortColumnsImg, iSortingCol)
        List dataReturns = new ArrayList()
        def c = OpenGalleryItem.createCriteria()
        def results = c.list(max: iDisplayLength, offset: iDisplayStart) {
            and {
                eq("openGallery", openGallery)
            }
            order(sortColumn, sSortDir)
        }
        int totalCount = results.totalCount
        if (totalCount > 0) {
            results.each { OpenGalleryItem galleryItem ->
                dataReturns.add([DT_RowId: galleryItem.id, 0: galleryItem.itemPath, 1: galleryItem.name, 2: galleryItem.sortOrder, 3: galleryItem.activeStatus?.value, 4:galleryItem.description, 5: ''])
            }
        }
        return [totalCount: totalCount, results: dataReturns]
    }

    Boolean deleteImage(String imagePath) {
        Image image = Image.findByIdentifier(imagePath)
        Boolean successStatus = Boolean.FALSE
        if (!image) {
            return successStatus
        }

        def absolutePath = grailsApplication.config.app.uploads.images.filesystemPath + image?.imagePath
        def absolutePathThumb = grailsApplication.config.app.uploads.images.filesystemPath + image?.imagePathThumb
        try {
            File fileImage = new File(absolutePath)
            File fileImageThumb = new File(absolutePathThumb)

            if (fileImage.delete() && fileImageThumb.delete()) {
                image.delete()
                successStatus = Boolean.TRUE
            } else {
                return successStatus
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return successStatus
    }

    def imageList() {
        def c = OpenGallery.createCriteria()
        def results = c.list() {
            and {
                eq("galleryType", OpenContentType.PICTURE)
                eq("activeStatus", ActiveStatus.ACTIVE)
            }
            order("sortOrder", CommonUtils.SORT_ORDER_ASC)
        }
        List dataReturns = new ArrayList()
        OpenGalleryItem imageItem
        results.each { OpenGallery gallery ->
            imageItem = firstImageItem(gallery)
            if(imageItem){
                dataReturns.add([DT_RowId: gallery.id, imagePath: imageItem.itemPath, name: gallery.name, description:gallery.description])
            }
        }
        return dataReturns
    }

    OpenGalleryItem firstImageItem(OpenGallery openGallery) {
        OpenGalleryItem galleryItem = OpenGalleryItem.findByActiveStatusAndOpenGallery(ActiveStatus.ACTIVE, openGallery)
        return galleryItem
    }

    def videoList() {
        def c = OpenGallery.createCriteria()
        def results = c.list() {
            and {
                eq("galleryType", OpenContentType.VIDEO)
                eq("activeStatus", ActiveStatus.ACTIVE)
            }
            order("sortOrder", CommonUtils.SORT_ORDER_ASC)
        }
        List dataReturns = new ArrayList()
        OpenGalleryItem videoItem
        results.each { OpenGallery gallery ->
            videoItem = firstVideoItem(gallery)
            if(videoItem){
                dataReturns.add([DT_RowId: gallery.id, imagePath: videoItem.itemPath, name: gallery.name, description:gallery.description])
            }
        }
        return dataReturns
    }

    OpenGalleryItem firstVideoItem(OpenGallery openGallery) {
        OpenGalleryItem galleryItem = OpenGalleryItem.findByActiveStatusAndOpenGallery(ActiveStatus.ACTIVE, openGallery)
        return galleryItem
    }
}
