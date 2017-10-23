package com.grailslab


import com.grailslab.enums.PayType
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.hr.Employee
import com.grailslab.library.Book
import com.grailslab.library.BookDetails
import com.grailslab.library.BookTransaction
import com.grailslab.stmgmt.Student
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.hibernate.Criteria

class BookTransactionService {
    static transactional = false

    static final String[] sortColumns = ['id','st.name','bk.bookId','bd.name','bda.name','issueDate','returnDate']
    LinkedHashMap issuePaginateList(GrailsParameterMap params){
        int iDisplayStart = params.iDisplayStart ? params.getInt('iDisplayStart') : CommonUtils.DEFAULT_PAGINATION_START
        int iDisplayLength = params.iDisplayLength ? params.getInt('iDisplayLength') : CommonUtils.DEFAULT_PAGINATION_LENGTH
        String sSortDir = params.sSortDir_0 ? params.sSortDir_0 : CommonUtils.DEFAULT_PAGINATION_SORT_ORDER
        int iSortingCol = params.iSortCol_0 ? params.getInt('iSortCol_0') : CommonUtils.DEFAULT_PAGINATION_SORT_IDX
        String sSearch = params.sSearch ? params.sSearch : null
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }
        String sortColumn = CommonUtils.getSortColumn(sortColumns,iSortingCol)
        List dataReturns = new ArrayList()
        def c = BookTransaction.createCriteria()
        def results = c.list(max: iDisplayLength, offset: iDisplayStart) {
            createAlias('student', 'st' , Criteria.LEFT_JOIN)
            createAlias('employee', 'em',Criteria.LEFT_JOIN)
            createAlias('book', 'bk',Criteria.LEFT_JOIN)
            createAlias('bk.bookDetails', 'bd',Criteria.LEFT_JOIN)
            createAlias('bd.author', 'bda',Criteria.LEFT_JOIN)
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
                eq("returnType", PayType.DUE)
            }
            if (sSearch) {
                or {
                    ilike('bk.bookId', sSearch)
                    ilike('bk.barcode', sSearch)
                    ilike('st.studentID', sSearch)
                    ilike('em.empID', sSearch)
                    ilike('bd.name', sSearch)
                    ilike('bda.name', sSearch)
                }
            }
            //     order(sortColumn, sSortDir)
            order(sortColumn, sSortDir)
        }
        Book books
        BookDetails bookDetails
        Student student
        Employee employee
        String issueDate
        String returnDate
        String borrower=''
        int totalCount = results.totalCount
        int serial = iDisplayStart;
        if (totalCount > 0) {
            if (sSortDir.equals(CommonUtils.SORT_ORDER_DESC)) {
                serial = (totalCount + 1) - iDisplayStart
            }
            for (transaction in results) {
                if (sSortDir.equals(CommonUtils.SORT_ORDER_ASC)) {
                    serial++
                } else {
                    serial--
                }
                if (transaction.student){
                    student=transaction.student
                    borrower= student?.studentID +' - '+student?.name
                }else {
                    employee = transaction.employee
                    borrower= employee?.empID+' - '+employee?.name +' - '+employee?.hrDesignation?.name
                }
                books=transaction.book
                bookDetails=books.bookDetails
                issueDate= transaction.issueDate ? CommonUtils.getUiDateStr(transaction.issueDate):''
                returnDate= transaction.returnDate ? CommonUtils.getUiDateStr(transaction.returnDate):''
                dataReturns.add([DT_RowId: transaction.id, 0:serial, 1:borrower, 2: books.bookId, 3: bookDetails.name, 4: bookDetails.author?.name, 5: issueDate, 6: returnDate, 7:''])
            }

        }
        return [totalCount:totalCount,results:dataReturns]
    }

}
