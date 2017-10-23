package com.grailslab

import com.grailslab.enums.BookStockStatus
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.library.Book
import com.grailslab.library.BookCategory
import com.grailslab.library.BookDetails
import com.grailslab.library.BookPublisher
import grails.transaction.Transactional
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

class BookStockService {
    static transactional = false

    static final String[] sortColumns = ['id','bookId','bd.name','at.name','price','barcode','stockDate']
    LinkedHashMap addBooksPaginateList(GrailsParameterMap params){
        int iDisplayStart = params.iDisplayStart ? params.getInt('iDisplayStart') : CommonUtils.DEFAULT_PAGINATION_START
        int iDisplayLength = params.iDisplayLength ? params.getInt('iDisplayLength') : CommonUtils.DEFAULT_PAGINATION_LENGTH
        String sSortDir = params.sSortDir_0 ? params.sSortDir_0 : CommonUtils.DEFAULT_PAGINATION_SORT_ORDER
        int iSortingCol = params.iSortCol_0 ? params.getInt('iSortCol_0') : CommonUtils.DEFAULT_PAGINATION_SORT_IDX
        String sSearch = params.sSearch ? params.sSearch : null
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }

        BookCategory bookCategory
        if (params.categoryName) {
            bookCategory = BookCategory.read(params.getLong("categoryName"))
        }
        BookStockStatus bookStockStatus
        if (params.bookStockStatus){
            bookStockStatus = BookStockStatus.valueOf(params.bookStockStatus)
        }
        String sortColumn = CommonUtils.getSortColumn(sortColumns,iSortingCol)
        List dataReturns = new ArrayList()
        def c = Book.createCriteria()
        def results = c.list(max: iDisplayLength, offset: iDisplayStart) {
            createAlias('bookDetails','bd')
            createAlias('bd.author','at')
            and {
                eq("bd.activeStatus",ActiveStatus.ACTIVE)
                eq("activeStatus",ActiveStatus.ACTIVE)
                if (bookCategory) {
                    eq("bd.category",bookCategory)
                }
                if (bookStockStatus) {
                    eq("stockStatus", bookStockStatus)
                }
            }
            if (sSearch) {
                or {
                    ilike('bookId', sSearch)
                    ilike('bd.name', sSearch)
                    ilike('at.name', sSearch)
                    ilike('barcode', sSearch)
                }
            }
            order(sortColumn, sSortDir)
        }
        int totalCount = results.totalCount
        int serial = iDisplayStart;
        BookDetails bookDetail
        if (totalCount > 0) {
            if (sSortDir.equals(CommonUtils.SORT_ORDER_DESC)) {
                serial = (totalCount + 1) - iDisplayStart
            }
            results.each { Book book ->
                bookDetail=book.bookDetails
                if (sSortDir.equals(CommonUtils.SORT_ORDER_ASC)) {
                    serial++
                } else {
                    serial--
                }
                dataReturns.add([DT_RowId: book.id,0: serial, 1: book.bookId, 2: bookDetail.name, 3: bookDetail.author?.name, 4:book.price, 5:book.barcode?book.barcode:'-', 6:CommonUtils.getUiDateStr(book.stockDate), 7:''])
            }
        }
        return [totalCount:totalCount,results:dataReturns]
    }

    def createBookIdList(int bookNumber, BookDetails bookDetails){
        String bookName
        String bookCategory
        String bookAuthor
        String bookIdNameAutCat
        String bookId
        int bookIdInt
        String bookIdIntString
        def ifBookExistList=Book.findAllByBookDetails(bookDetails)

        Book book
        List bookIdList=new ArrayList()
        if (ifBookExistList){
            Book ifBookExist=ifBookExistList.last()
            bookId= ifBookExist.bookId
            bookId = bookId.substring(18)
            bookIdInt=Integer.parseInt(bookId)
            bookIdInt=bookIdInt+1
        }else {
            bookIdInt=1
        }
        bookName=trimFiveCharUpper(bookDetails.name)
        bookCategory=trimFiveCharUpper(bookDetails.category.name)
        bookAuthor=trimFiveCharUpper(bookDetails.author.name)
        bookIdNameAutCat=bookName+'-'+bookAuthor+'-'+bookCategory

        for(int i=1; i<bookNumber+1; i++){
            bookIdIntString = String.format("%05d", bookIdInt)
            bookId=bookIdNameAutCat+'-'+bookIdIntString
            book=Book.findByBookId(bookId)
            while (book) {
                bookIdInt++;
                bookId=bookIdNameAutCat+'-'+bookIdInt
                book=Book.findByBookId(bookId)
            }
            bookIdList.add(bookId)
            bookIdInt=bookIdInt+1
        }
        return bookIdList
    }
    @Transactional
    def saveBooks(Book book,List bookIdList){

        bookIdList.each { String values->
            Book bookSave=new Book()
            bookSave.properties=book.properties
            bookSave.bookId=values
            bookSave.save()
        }
        return Boolean.TRUE
    }

    def trimFiveCharUpper(String str){
        str=str+'OOOOO'
        str = str.replaceAll("\\s+","")
        str = str.substring(0,5)
        str = str.toUpperCase()
        str = str.trim()
        return str
    }

    def bookDetailsTypeAheadList(GrailsParameterMap parameterMap){
        String sSearch = parameterMap.q
        List dataReturns = new ArrayList()
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }
        def c = BookDetails.createCriteria()
        def results = c.list() {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
            }
            if (sSearch) {
                or {
                    ilike('name', sSearch)
                }
            }
            order("name",CommonUtils.SORT_ORDER_ASC)
        }
        for (obj in results) {
            dataReturns.add([id: obj.id, name: obj.name, ])
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
                }
            }
            order("name",CommonUtils.SORT_ORDER_ASC)
        }
        for (obj in results) {
            dataReturns.add([id: obj.id, name: obj.name])
        }
        return dataReturns
    }

    def bookTypeAheadList(GrailsParameterMap parameterMap){
        String sSearch = parameterMap.q
        List dataReturns = new ArrayList()
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }
        def c = Book.createCriteria()
        def results = c.list(max: 20, offset: 0) {
            createAlias('bookDetails','bd')
            and {
                eq("bd.activeStatus", ActiveStatus.ACTIVE)
                eq("activeStatus", ActiveStatus.ACTIVE)
            }
            if (sSearch) {
                or {
                    ilike('bd.name', sSearch)
                    ilike('bookId', sSearch)
                    ilike('barcode', sSearch)
                }
            }
            order("bd.name",CommonUtils.SORT_ORDER_ASC)
        }
        for (obj in results) {
            dataReturns.add([id: obj.id, name: obj.bookDetails.name+" ("+obj.bookId+")"])
        }
        return dataReturns
    }
    def bookIssueTypeAheadList(GrailsParameterMap parameterMap){
        String sSearch = parameterMap.q
        BookStockStatus bookStockStatus = BookStockStatus.ADDED
        if (parameterMap.stockStatus) {
            bookStockStatus = BookStockStatus.valueOf(parameterMap.stockStatus)
        }
        List dataReturns = new ArrayList()
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }
        def c = Book.createCriteria()
        def results = c.list(max: 20, offset: 0) {
            createAlias('bookDetails','bd')
            and {
                eq("bd.activeStatus", ActiveStatus.ACTIVE)
                eq("activeStatus", ActiveStatus.ACTIVE)
                eq("stockStatus", bookStockStatus)
            }
            if (sSearch) {
                or {
                    ilike('bd.name', sSearch)
                    ilike('bookId', sSearch)
                    ilike('barcode', sSearch)
                }
            }
            order("bd.name",CommonUtils.SORT_ORDER_ASC)
        }
        for (obj in results) {
            dataReturns.add([id: obj.id, name: obj.bookDetails.name+" ("+obj.bookId+")"])
        }
        return dataReturns
    }

}

