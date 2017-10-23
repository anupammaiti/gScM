package com.grailslab.library

import com.grailslab.CommonUtils
import com.grailslab.command.BookIssueCommand
import com.grailslab.command.BookReceiveCommand
import com.grailslab.enums.BookStockStatus
import com.grailslab.enums.PayType
import com.grailslab.enums.StudentStatus
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.hr.Employee
import com.grailslab.stmgmt.Student
import com.grailslab.viewz.StdEmpView
import grails.converters.JSON
import org.springframework.dao.DataIntegrityViolationException

class BookTransactionController {

    def bookTransactionService
    def studentService
    def employeeService
    def messageSource
    def classNameService
    def schoolService

    def index() {
        redirect(action: 'bookIssue')
    }

    def bookIssue() {
        def workingYearList=schoolService.academicYears()
        render(view: '/library/bookIssue', model: [workingYearList: workingYearList])
    }
    def bookIssueReturn() {
        def workingYearList=schoolService.academicYears()
        render(view: '/library/bookIssueAndReturn', model: [workingYearList: workingYearList])
    }

    def bookIssueList() {
        LinkedHashMap gridData
        String result
        LinkedHashMap resultMap = bookTransactionService.issuePaginateList(params)

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

    def bookIssueSave(BookIssueCommand command) {
        if (!request.method.equals('POST')) {
            redirect(action: 'bookIssue')
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
        BookTransaction transaction
        String message
        StdEmpView stdEmpView
        Student student
        Employee employee
        Book book
        if (command.id) {
            transaction = BookTransaction.get(command.id)
            if (!transaction) {
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
                outPut = result as JSON
                render outPut
                return
            }
            transaction.book = command.book
            if (!command.issueDate){
                transaction.issueDate = new Date()
            } else {
                transaction.issueDate = command.issueDate
            }
            transaction.returnDate = command.returnDate
            transaction.save()
            result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
            result.put(CommonUtils.MESSAGE, "Issue Updated successfully")
            outPut = result as JSON
            render outPut
            return
        } else {
            if (command.book) {
                book = command.book
            } else if (command.bookBarcode){
                book = Book.findByBarcode(command.bookBarcode)
            }
            if(!book){
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, "Please select book for issue")
                outPut = result as JSON
                render outPut
                return
            }
            stdEmpView = StdEmpView.findByStdEmpNo(command.stdEmpId)
            if(stdEmpView.objType == "student") {
                student = Student.findByStudentIDAndAcademicYearAndStudentStatus(command.stdEmpId, command.stdEmpAcademicYr, StudentStatus.NEW)
                if (!student) {
                    result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                    result.put(CommonUtils.MESSAGE, "$command.stdEmpId not found as student in $command.stdEmpAcademicYr.value. Please select year correctly or contact with admin")
                    outPut = result as JSON
                    render outPut
                    return
                }
            } else {
                employee = Employee.read(stdEmpView.objId)
            }
            Date issueDate = command.issueDate
            Date returnDate = command.returnDate
            if (!issueDate){
                issueDate = new Date()
            }
            if (!returnDate){
                returnDate = issueDate.plus(7)
            }
            transaction = new BookTransaction(student: student, employee: employee, book: book, issueDate: issueDate, returnDate: returnDate)
            result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
            message = 'Book Issue Save successfully'

            if (transaction.hasErrors() || !transaction.save()) {
                def errorList = transaction?.errors?.allErrors?.collect { messageSource.getMessage(it, null) }
                message = errorList?.join('\n')
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            }
            book.stockStatus = BookStockStatus.OUT
            book.save()
            result.put(CommonUtils.MESSAGE, message)
            outPut = result as JSON
            render outPut
        }
    }

    def bookReturnSave(BookReceiveCommand command) {
        if (!request.method.equals('POST')) {
            redirect(action: 'bookIssue')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        BookTransaction transaction
        String message
            transaction = BookTransaction.get(command.id)
            if (!transaction) {
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
                outPut = result as JSON
                render outPut
                return
            }
        if(command.submissionDate){
            transaction.submissionDate = command.submissionDate
            transaction.fine = command.fine
        } else {
            transaction.submissionDate = new Date()
            transaction.fine = 0
        }
        transaction.returnType = PayType.PAID
            if (transaction.save(flush: true)){
                Book returnBook = transaction.book
                returnBook.stockStatus = BookStockStatus.ADDED
                returnBook.save()
            }

            result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
            message = 'Book Return successfully'
            result.put(CommonUtils.MESSAGE, message)
            outPut = result as JSON
            render outPut
    }

    def bookReturn(){
        println(params)
        Book book = Book.read(params.getLong('id'))
        BookTransaction bookTransaction = BookTransaction.findByBookAndReturnTypeAndActiveStatus(book, PayType.DUE, ActiveStatus.ACTIVE)
        String issueTo
        if (bookTransaction)

        LinkedHashMap result = new LinkedHashMap()
        String outPut

        result.put("book", params)

        outPut = result as JSON
        render outPut

    }

    def issueEdit(Long id) {
        if (!request.method.equals('POST')) {
            redirect(action: 'bookIssue')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        String borrowerType
        String borrowerName
        String borrowerId
        BookTransaction transaction = BookTransaction.read(id)
        if (!transaction) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        if(transaction.student){
            borrowerType='student'
            borrowerName=transaction.student.studentID+' - '+transaction.student.name
            borrowerId = transaction.student.studentID

        }else {
            borrowerType='teacher'
            borrowerName=transaction.employee.empID+' - '+transaction.employee.name
            borrowerId = transaction.employee.empID
        }
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.OBJ, transaction)
        result.put('borrowerType', borrowerType)
        result.put('borrowerName', borrowerName)
        result.put('borrowerId', borrowerId)
        result.put('borrowerBookName', transaction.book.bookDetails.name)
        result.put('borrowingYear', transaction.academicYear)
        outPut = result as JSON
        render outPut
    }
    def issueReturn(Long id) {
        if (!request.method.equals('POST')) {
            redirect(action: 'bookIssue')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        String borrowerName
        BookTransaction transaction = BookTransaction.read(id)
        if (!transaction) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        if(transaction.student){
            borrowerName=transaction?.student?.studentID+' - '+transaction?.student?.name
        }else {
            borrowerName=transaction?.employee?.empID+' - '+transaction?.employee?.name
        }
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.OBJ, transaction)
        result.put('borrowerName', borrowerName)
        result.put('borrowerBookName', transaction.book.bookDetails.name)
        result.put('bookIssueDate', transaction.issueDate)
        result.put('bookReturnDate', transaction.returnDate)
        outPut = result as JSON
        render outPut
    }
    def issueDelete(Long id) {
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        BookTransaction transaction = BookTransaction.get(id)
        if (!transaction) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
        }
        try {
            transaction.book.stockStatus = BookStockStatus.ADDED
            transaction.activeStatus=ActiveStatus.INACTIVE
            transaction.save(flush: true)
            //transaction.delete(flush: true)
            result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
            result.put(CommonUtils.MESSAGE, "Book Issue Deleted Successfully.")

        }
        catch (DataIntegrityViolationException e) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "Book Issue in use")
        }
        outPut = result as JSON
        render outPut
    }

    def getBookByBarcode(String barcode) {
        if (!request.method.equals('POST')) {
            redirect(action: 'bookIssue')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        String borrowerType
        String borrowerName
        String bookIssue
        Book book = Book.findByBarcode(barcode)
        if (!book) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, 'Book Not Found.')
            outPut = result as JSON
            render outPut
            return
        }
        BookTransaction transaction = BookTransaction.findByBookAndReturnType(book,PayType.DUE)
        if (transaction) {
            if(transaction.student){
                borrowerType='student'
                borrowerName=transaction.student.studentID+' - '+transaction.student.name
                bookIssue='issue'
            }else {
                borrowerType='teacher'
                borrowerName=transaction.employee.empID+' - '+transaction.employee.name
                bookIssue='issue'
            }
        }
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put('book', book)
        result.put('transaction', transaction)
        result.put('borrowerType', borrowerType)
        result.put('borrowerName', borrowerName)
        result.put('bookIssue', bookIssue)
        outPut = result as JSON
        render outPut
    }
}





