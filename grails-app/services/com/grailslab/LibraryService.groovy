package com.grailslab

import com.grailslab.enums.BookStockStatus
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.library.*
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

class LibraryService {
    static transactional = false
    def bookTransactionService

    static final String[] sortColumnsBook = ['id','name','at.name','ct.name','bp.name']
    LinkedHashMap bookDetailPaginateList(GrailsParameterMap params){
        int iDisplayStart = params.iDisplayStart ? params.getInt('iDisplayStart') : CommonUtils.DEFAULT_PAGINATION_START
        int iDisplayLength = params.iDisplayLength ? params.getInt('iDisplayLength') : CommonUtils.DEFAULT_PAGINATION_LENGTH
        String sSortDir = params.sSortDir_0 ? params.sSortDir_0 : CommonUtils.SORT_ORDER_ASC
        int iSortingCol = params.iSortCol_0 ? params.getInt('iSortCol_0') : CommonUtils.DEFAULT_PAGINATION_SORT_IDX
        String sSearch = params.sSearch ? params.sSearch : null
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }
        BookCategory bookCategory
        if (params.categoryName) {
            bookCategory = BookCategory.read(params.getLong("categoryName"))
        }
        String sortColumn = CommonUtils.getSortColumn(sortColumnsBook,iSortingCol)
        List dataReturns = new ArrayList()
        def c = BookDetails.createCriteria()
        def results = c.list(max: iDisplayLength, offset: iDisplayStart) {
            createAlias('category', 'ct')
            createAlias('author', 'at')
            createAlias('bookPublisher', 'bp')
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
                if (bookCategory) {
                    eq("category",bookCategory)
                }
            }
            if (sSearch) {
                or {
                    ilike('name', sSearch)
                    ilike('at.name', sSearch)
                    ilike('ct.name', sSearch)
                    ilike('bp.name', sSearch)
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
            results.each { BookDetails bookDetails ->
                if (sSortDir.equals(CommonUtils.SORT_ORDER_ASC)) {
                    serial++
                } else {
                    serial--
                }
                dataReturns.add([DT_RowId: bookDetails.id, 0: serial, 1: bookDetails.name, 2:bookDetails.author.name,3: bookDetails.bookPublisher?.name?:"-", 4:''])
            }
        }
        return [totalCount:totalCount,results:dataReturns]
    }

    def allBookCategoryDropDownList(){
        def c = BookCategory.createCriteria()
        def results = c.list() {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
            }
        }
        List dataReturns = new ArrayList()
        results.each { BookCategory category ->
            dataReturns.add([id: category.id, name: category.name])
        }
        return dataReturns
    }
    def bookCategoryList(){
        def c = BookCategory.createCriteria()
        def results = c.list() {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
            }
        }
        return results
    }

    def bookPublisherList(){
        def c = BookPublisher.createCriteria()
        def results = c.list() {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
            }
        }
        return results
    }

    def bookAuthorList(){
        def c = Author.createCriteria()
        def results = c.list() {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
            }
        }
        return results
    }



    def allBookLanguage(){
        List dataReturns = new ArrayList()
        def results = BookDetails.withCriteria() {
            projections {
                distinct("language")
            }
        }
        for (obj in results) {
            dataReturns.add([name: obj])
        }
        return dataReturns
    }
    def allBookSource(){
        List dataReturns = new ArrayList()
        def results = Book.withCriteria() {
            projections {
                distinct("source")
            }
        }
        for (obj in results) {
            dataReturns.add([name: obj])
        }
        return dataReturns
    }
    def authorTypeAheadList(GrailsParameterMap parameterMap){
        String sSearch = parameterMap.q
        List dataReturns = new ArrayList()
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }
        def c = Author.createCriteria()
        def results = c.list() {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
            }
            if (sSearch) {
                or {
                    ilike('name', sSearch)
                    ilike('country', sSearch)
                }
            }
            order("name",CommonUtils.SORT_ORDER_ASC)
        }
        String name
        for (obj in results) {
            name = obj.name
            if (obj.country) {
                name = name+", "+ obj.country
            }
            dataReturns.add([id: obj.id, name: name])
        }
        return dataReturns
    }
    def publisherTypeAheadList(GrailsParameterMap parameterMap){
        String sSearch = parameterMap.q
        List dataReturns = new ArrayList()
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }
        def c = BookPublisher.createCriteria()
        def results = c.list() {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
            }
            if (sSearch) {
                or {
                    ilike('name', sSearch)
                    ilike('country', sSearch)
                }
            }
            order("name",CommonUtils.SORT_ORDER_ASC)
        }
        String name
        for (obj in results) {
            name = obj.name
            if (obj.country) {
                name = name+", "+ obj.country
            }
            dataReturns.add([id: obj.id, name: name])
        }
        return dataReturns
    }
    int bookCount(BookCategory bookCategory, BookPublisher publisher, Author author, String language, BookStockStatus stockStatus) {
        def totalBook = Book.createCriteria().count() {
            createAlias('bookDetails', 'bd')
            and {
                eq("bd.activeStatus", ActiveStatus.ACTIVE)
                eq("activeStatus", ActiveStatus.ACTIVE)
                if (stockStatus){
                    eq("stockStatus", stockStatus)
                }
                if (bookCategory){
                    eq("bd.category", bookCategory)
                }
                if (author){
                    eq("bd.author", author)
                }
                if (publisher){
                    eq("bd.bookPublisher", publisher)
                }
                if (language){
                    eq("bd.language", language)
                }
            }
        }
        return totalBook
    }
}
