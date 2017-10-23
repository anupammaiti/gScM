package com.grailslab.report

import com.grailslab.CommonUtils
import com.grailslab.command.library.*
import com.grailslab.enums.BookStockStatus
import com.grailslab.enums.PrintOptionType
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.library.BookCategory
import com.grailslab.stmgmt.Student
import com.grailslab.viewz.StdEmpView
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef

class LibraryReportController{

    def schoolService
    def studentService
    def employeeService
    def libraryService
    def sectionService
    def jasperService

    private static final String BAR_CODE_PRINT_JASPER_FILE = 'barcodePrint.jasper'
    private static final String BOOK_LIST_JASPER_FILE = 'bookList.jasper'
    private static final String BOOK_SUMMARY_JASPER_FILE = 'bookSummary.jasper'
    private static final String BOOK_TOP_READER_JASPER_FILE = 'bookTopReader.jasper'
    private static final String SOURCE_BOOK_LIST_JASPER_FILE = 'bookListBySource.jasper'
    private static final String BOOK_HISTORY_JASPER_FILE = 'bookHistory.jasper'
    private static final String BOOK_HISTORY_BY_BOOK_JASPER_FILE = 'bookHistoryByBook.jasper'
    private static final String BOOK_TRN_LIST_JASPER_FILE = 'bookTransaction.jasper'
    private static final String BOOK_USER_BY_BORROWER_JASPER_FILE = 'bookUserByBorrower.jasper'
    private static final String BOOK_USER_HISTORY_JASPER_FILE = 'bookUserHistory.jasper'

    def index() {
        def categoryList = BookCategory.findAllByActiveStatus(ActiveStatus.ACTIVE)
        def bookLanguageList = libraryService.allBookLanguage()
        def bookSourceList = libraryService.allBookSource()
        def workingYearList=schoolService.academicYears()
        render(view: '/library/bookReport', model: [categoryList: categoryList, bookLanguageList: bookLanguageList, bookSourceList: bookSourceList, workingYearList: workingYearList])
    }

    def bookHistory(BookHistoryCommand command) {
        String sqlParam
        String reportFileName
        if (command.bookName) {
            reportFileName = schoolService.reportFileName(CommonUtils.MODULE_LIBRARY, BOOK_HISTORY_BY_BOOK_JASPER_FILE)
            sqlParam = " bt.active_status = 'ACTIVE' and book.active_status = 'ACTIVE' and bd.active_status = 'ACTIVE'"+" and book.id='"+ command.bookName +"' "
        } else {
            reportFileName = schoolService.reportFileName(CommonUtils.MODULE_LIBRARY, BOOK_HISTORY_JASPER_FILE)
            sqlParam = " bt.return_type='DUE' and bt.active_status = 'ACTIVE' and book.active_status = 'ACTIVE' and bd.active_status = 'ACTIVE'  "
        }

        if (command.fromDate) {
            String fromDate = CommonUtils.getMysqlQueryDateStr(command.fromDate)
            sqlParam += " and bt.issue_date>='"+ fromDate +"' "
        }
         if (command.toDate) {
             String toDate = CommonUtils.getMysqlQueryDateStr(command.toDate.plus(1))
             sqlParam += " and bt.return_date <'"+ toDate +"' "
         }
        Map paramsMap = new LinkedHashMap()
        paramsMap.put('schoolName', message(code: "app.school.name"))
        paramsMap.put('schoolAddress', message(code: "app.report.address.line1"))
        paramsMap.put('creditLine', message(code: "company.credit.footer"))
        paramsMap.put('REPORT_LOGO', schoolService.reportLogoPath())
        paramsMap.put('sqlParam', sqlParam)

        String extension = CommonUtils.PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.PDF_FORMAT

        if(command.bookHistoryPrintType && command.bookHistoryPrintType !=PrintOptionType.PDF.key){
            if(command.bookHistoryPrintType==PrintOptionType.XLSX.key){
                extension = CommonUtils.XLSX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_XLSX
                exportFormat = JasperExportFormat.XLSX_FORMAT
            } else if(command.bookHistoryPrintType==PrintOptionType.DOCX.key){
                extension = CommonUtils.DOCX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_DOCX
                exportFormat = JasperExportFormat.DOCX_FORMAT
            }
        }
        String outputFileName = "book_history_by_book${extension}"
        JasperReportDef reportDef = new JasperReportDef(
                name: reportFileName,
                fileFormat: exportFormat,
                parameters: paramsMap
        )
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        response.contentType =grailsApplication.config.grails.mime.types[mimeType]
        response.setHeader("Content-disposition", "inline;filename=${outputFileName}")
        response.outputStream << report.toByteArray()
    }
    def userHistory(BookUserCommand command){
        String sqlParam
        String reportFileName
        if (command.stuEmpID ) {
            StdEmpView stdEmpView = StdEmpView.findByStdEmpNo(command.stuEmpID)
            reportFileName = schoolService.reportFileName(CommonUtils.MODULE_LIBRARY, BOOK_USER_BY_BORROWER_JASPER_FILE)
            sqlParam = " bt.active_status = 'ACTIVE' and book.active_status = 'ACTIVE' and bd.active_status = 'ACTIVE'"
            if (stdEmpView && stdEmpView.objType == "student") {
                Student student = Student.findByAcademicYearAndStudentID(command.stdEmpAcademicYr, stdEmpView.stdEmpNo)
                sqlParam += " and bt.student_id ='"+ student?.id +"' "
            } else {
                sqlParam += " and bt.employee_id ='"+ stdEmpView.objId +"' "
            }
        }else {
            reportFileName = schoolService.reportFileName(CommonUtils.MODULE_LIBRARY, BOOK_USER_HISTORY_JASPER_FILE)
            sqlParam = " bt.return_type='DUE' and bt.active_status = 'ACTIVE' and book.active_status = 'ACTIVE' and bd.active_status = 'ACTIVE'  "
        }

        Date userFromDate = command.userFromDate?: new Date()
        if (userFromDate) {
            String fromDate = CommonUtils.getMysqlQueryDateStr(userFromDate.minus(180))
            sqlParam += " and bt.issue_date>='"+ fromDate +"' "
        }
        Date userToDate = command.userToDate?: new Date()
        if (userToDate) {
            String toDate = CommonUtils.getMysqlQueryDateStr(userToDate.plus(1))
            sqlParam += " and bt.return_date <'"+ toDate +"' "
        }
        Map paramsMap = new LinkedHashMap()
        paramsMap.put('schoolName', message(code: "app.school.name"))
        paramsMap.put('schoolAddress', message(code: "app.report.address.line1"))
        paramsMap.put('creditLine', message(code: "company.credit.footer"))
        paramsMap.put('REPORT_LOGO', schoolService.reportLogoPath())
        paramsMap.put('sqlParam', sqlParam)

        String extension = CommonUtils.PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.PDF_FORMAT

        if(command.userHistoryPrintType && command.userHistoryPrintType !=PrintOptionType.PDF.key){
            if(command.userHistoryPrintType==PrintOptionType.XLSX.key){
                extension = CommonUtils.XLSX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_XLSX
                exportFormat = JasperExportFormat.XLSX_FORMAT
            } else if(command.userHistoryPrintType==PrintOptionType.DOCX.key){
                extension = CommonUtils.DOCX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_DOCX
                exportFormat = JasperExportFormat.DOCX_FORMAT
            }
        }
        String outputFileName = "user_history_by_student${extension}"
        JasperReportDef reportDef = new JasperReportDef(
                name: reportFileName,
                fileFormat: exportFormat,
                parameters: paramsMap
        )
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        response.contentType =grailsApplication.config.grails.mime.types[mimeType]
        response.setHeader("Content-disposition", "inline;filename=${outputFileName}")
        response.outputStream << report.toByteArray()
    }
    def transaction(LibraryBookTransactionCommand command){
        String reportFileName = schoolService.reportFileName(CommonUtils.MODULE_LIBRARY, BOOK_TRN_LIST_JASPER_FILE)

          String sqlParam = "emp.active_status='ACTIVE' and sal.active_status='ACTIVE'"
          if (params.hrCategory) {
              sqlParam += " and emp.hr_category_id = ${params.hrCategory}"
          }
        Map paramsMap = new LinkedHashMap()
        paramsMap.put('REPORT_LOGO', schoolService.reportLogoPath())
        paramsMap.put('sqlParam', " ")

        String extension = CommonUtils.PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.PDF_FORMAT

        if(command.transactionPrintType && command.transactionPrintType !=PrintOptionType.PDF.key){
            if(command.transactionPrintType==PrintOptionType.XLSX.key){
                extension = CommonUtils.XLSX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_XLSX
                exportFormat = JasperExportFormat.XLSX_FORMAT
            } else if(command.transactionPrintType==PrintOptionType.DOCX.key){
                extension = CommonUtils.DOCX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_DOCX
                exportFormat = JasperExportFormat.DOCX_FORMAT
            }
        }
        String outputFileName = "book_transaction${extension}"
        JasperReportDef reportDef = new JasperReportDef(
                name: reportFileName,
                fileFormat: exportFormat,
                parameters: paramsMap
        )
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        response.contentType =grailsApplication.config.grails.mime.types[mimeType]
        response.setHeader("Content-disposition", "inline;filename=${outputFileName}")
        response.outputStream << report.toByteArray()
    }
    def sourceBookList(BookSourceListCommand command){
        String reportFileName = schoolService.reportFileName(CommonUtils.MODULE_LIBRARY, SOURCE_BOOK_LIST_JASPER_FILE)

        String sqlParam = " bd.active_status = 'ACTIVE' and book.active_status = 'ACTIVE'"
        if (command.bookSourceReportFor) {
            sqlParam += " and book.stock_status ='"+command.bookSourceReportFor+ "' "
        }
        if (command.bookSourceCategory) {
            sqlParam += " and bc.id='${command.bookSourceCategory}' "
        }
         if (command.bookSourcePublisher) {
             sqlParam += " and bp.id='${command.bookSourcePublisher}' "
         }
         if (command.bookSourceAuthor) {
             sqlParam += " and author.id='${command.bookSourceAuthor}' "
         }
         if (command.bookSource) {
             sqlParam += " and book.source='${command.bookSource}' "
         }
          if (command.bookSourceLanguage) {
              sqlParam += " and bd.language='${command.bookSourceLanguage}' "
          }

        Map paramsMap = new LinkedHashMap()
        paramsMap.put('schoolName', message(code: "app.school.name"))
        paramsMap.put('schoolAddress', message(code: "app.report.address.line1"))
        paramsMap.put('creditLine', message(code: "company.credit.footer"))
        paramsMap.put('REPORT_LOGO', schoolService.reportLogoPath())
        paramsMap.put('sqlParam', sqlParam)

        String extension = CommonUtils.PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.PDF_FORMAT

        if(command.bookSourceListPrintType && command.bookSourceListPrintType !=PrintOptionType.PDF.key){
            if(command.bookSourceListPrintType==PrintOptionType.XLSX.key){
                extension = CommonUtils.XLSX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_XLSX
                exportFormat = JasperExportFormat.XLSX_FORMAT
            } else if(command.bookSourceListPrintType==PrintOptionType.DOCX.key){
                extension = CommonUtils.DOCX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_DOCX
                exportFormat = JasperExportFormat.DOCX_FORMAT
            }
        }
        String outputFileName = "book_list_by_source${extension}"
        JasperReportDef reportDef = new JasperReportDef(
                name: reportFileName,
                fileFormat: exportFormat,
                parameters: paramsMap
        )
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        response.contentType =grailsApplication.config.grails.mime.types[mimeType]
        response.setHeader("Content-disposition", "inline;filename=${outputFileName}")
        response.outputStream << report.toByteArray()
    }
    def bookList(BookListCommand command){
        String reportFileName = schoolService.reportFileName(CommonUtils.MODULE_LIBRARY, BOOK_LIST_JASPER_FILE)

        String sqlParam = " bd.active_status = 'ACTIVE' and book.active_status = 'ACTIVE'"
        if (command.bookReportFor) {
            sqlParam += " and book.stock_status ='"+command.bookReportFor+ "' "
        }
        if (command.bookCategory) {
            sqlParam += " and bc.id='${command.bookCategory}' "
        }
          if (command.bookPublisher) {
              sqlParam += " and bp.id='${command.bookPublisher}' "
          }
          if (command.bookAuthor) {
               sqlParam += " and author.id='${command.bookAuthor}' "
           }
              if (command.bookLanguage) {
                 sqlParam += " and bd.language='${command.bookLanguage}' "
             }
        Map paramsMap = new LinkedHashMap()
        paramsMap.put('schoolName', message(code: "app.school.name"))
        paramsMap.put('schoolAddress', message(code: "app.report.address.line1"))
        paramsMap.put('creditLine', message(code: "company.credit.footer"))
        paramsMap.put('REPORT_LOGO', schoolService.reportLogoPath())
        paramsMap.put('sqlParam', sqlParam)

        String extension = CommonUtils.PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.PDF_FORMAT

        if(command.bookListPrintType && command.bookListPrintType !=PrintOptionType.PDF.key){
            if(command.bookListPrintType==PrintOptionType.XLSX.key){
                extension = CommonUtils.XLSX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_XLSX
                exportFormat = JasperExportFormat.XLSX_FORMAT
            } else if(command.bookListPrintType==PrintOptionType.DOCX.key){
                extension = CommonUtils.DOCX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_DOCX
                exportFormat = JasperExportFormat.DOCX_FORMAT
            }
        }
        String outputFileName = "book_list${extension}"
        JasperReportDef reportDef = new JasperReportDef(
                name: reportFileName,
                fileFormat: exportFormat,
                parameters: paramsMap
        )
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        response.contentType =grailsApplication.config.grails.mime.types[mimeType]
        response.setHeader("Content-disposition", "inline;filename=${outputFileName}")
        response.outputStream << report.toByteArray()
    }

    def bookSummary(){
        String reportFileName = schoolService.reportFileName(CommonUtils.MODULE_LIBRARY, BOOK_SUMMARY_JASPER_FILE)
        int numberOfBook
        int numberOfStock
        int numberOfOut
        int numberOfLost
        int numberOfDamage
        List reportDataParam = new ArrayList()
        String headerText = "Report Type"
        if (params.summaryType == "all"){
             numberOfStock = libraryService.bookCount(null, null, null, null, BookStockStatus.ADDED)
             numberOfOut = libraryService.bookCount(null, null, null, null, BookStockStatus.OUT)
             numberOfLost = libraryService.bookCount(null, null, null, null, BookStockStatus.LOST)
             numberOfDamage = libraryService.bookCount(null, null, null, null, BookStockStatus.DAMAGE)
            numberOfBook = numberOfStock + numberOfOut + numberOfLost + numberOfDamage
            reportDataParam.add([summaryType: "all", numberOfBook: numberOfBook, numberOfStock: numberOfStock, numberOfOut: numberOfOut, numberOfLost: numberOfLost, numberOfDamage: numberOfDamage])
        } else if (params.summaryType == "byCategory"){
            headerText = "Category Name"
            def bookCategoryList = libraryService.bookCategoryList()
            for (bookCategory in bookCategoryList) {
                numberOfStock = libraryService.bookCount(bookCategory, null, null, null, BookStockStatus.ADDED)
                numberOfOut = libraryService.bookCount(bookCategory, null, null, null, BookStockStatus.OUT)
                numberOfLost = libraryService.bookCount(bookCategory, null, null, null, BookStockStatus.LOST)
                numberOfDamage = libraryService.bookCount(bookCategory, null, null, null, BookStockStatus.DAMAGE)
                numberOfBook = numberOfStock + numberOfOut + numberOfLost + numberOfDamage
                reportDataParam.add([summaryType: bookCategory.name, numberOfBook: numberOfBook, numberOfStock: numberOfStock, numberOfOut: numberOfOut, numberOfLost: numberOfLost, numberOfDamage: numberOfDamage])
            }
        } else if (params.summaryType == "byPublisher"){
            headerText = "Publisher Name"
            def bookPublisherList = libraryService.bookPublisherList()
            for (bookPublisher in bookPublisherList) {
                numberOfStock = libraryService.bookCount(null, bookPublisher, null, null, BookStockStatus.ADDED)
                numberOfOut = libraryService.bookCount(null, bookPublisher, null, null, BookStockStatus.OUT)
                numberOfLost = libraryService.bookCount(null, bookPublisher, null, null, BookStockStatus.LOST)
                numberOfDamage = libraryService.bookCount(null, bookPublisher, null, null, BookStockStatus.DAMAGE)
                numberOfBook = numberOfStock + numberOfOut + numberOfLost + numberOfDamage
                reportDataParam.add([summaryType: bookPublisher.name, numberOfBook: numberOfBook, numberOfStock: numberOfStock, numberOfOut: numberOfOut, numberOfLost: numberOfLost, numberOfDamage: numberOfDamage])
            }
        }
        else if (params.summaryType == "byAuthor"){
            headerText = "Author Name"
            def bookAuthorList = libraryService.bookAuthorList()
            for (author in bookAuthorList) {
                numberOfStock = libraryService.bookCount(null, null, author, null, BookStockStatus.ADDED)
                numberOfOut = libraryService.bookCount(null, null, author, null, BookStockStatus.OUT)
                numberOfLost = libraryService.bookCount(null, null, author, null, BookStockStatus.LOST)
                numberOfDamage = libraryService.bookCount(null, null, author, null, BookStockStatus.DAMAGE)
                numberOfBook = numberOfStock + numberOfOut + numberOfLost + numberOfDamage
                reportDataParam.add([summaryType: author.name, numberOfBook: numberOfBook, numberOfStock: numberOfStock, numberOfOut: numberOfOut, numberOfLost: numberOfLost, numberOfDamage: numberOfDamage])
            }
        }

        Map paramsMap = new LinkedHashMap()
        paramsMap.put('schoolName', message(code: "app.school.name"))
        paramsMap.put('schoolAddress', message(code: "app.report.address.line1"))
        paramsMap.put('creditLine', message(code: "company.credit.footer"))
        paramsMap.put('REPORT_LOGO', schoolService.reportLogoPath())
        paramsMap.put('headerText', headerText)

        String extension = CommonUtils.PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.PDF_FORMAT

        PrintOptionType printOptionType = PrintOptionType.PDF
        if(params.printOptionType && params.printOptionType !=PrintOptionType.PDF.key){
            if(params.printOptionType==PrintOptionType.XLSX.key){
                extension = CommonUtils.XLSX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_XLSX
                exportFormat = JasperExportFormat.XLSX_FORMAT
            } else if(params.printOptionType==PrintOptionType.DOCX.key){
                extension = CommonUtils.DOCX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_DOCX
                exportFormat = JasperExportFormat.DOCX_FORMAT
            }
        }
        String outputFileName = "book_summary${extension}"
        JasperReportDef reportDef = new JasperReportDef(
                name: reportFileName,
                fileFormat: exportFormat,
                parameters: paramsMap,
                reportData: reportDataParam
        )
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        response.contentType =grailsApplication.config.grails.mime.types[mimeType]
        response.setHeader("Content-disposition", "inline;filename=${outputFileName}")
        response.outputStream << report.toByteArray()
    }

    def bookTopNReaderList(){
        String reportFileName = schoolService.reportFileName(CommonUtils.MODULE_LIBRARY, BOOK_TOP_READER_JASPER_FILE)
        String sqlParam = " bd.active_status = 'ACTIVE' and book.active_status = 'ACTIVE'"

        Map paramsMap = new LinkedHashMap()
        paramsMap.put('schoolName', message(code: "app.school.name"))
        paramsMap.put('schoolAddress', message(code: "app.report.address.line1"))
        paramsMap.put('creditLine', message(code: "company.credit.footer"))
        paramsMap.put('REPORT_LOGO', schoolService.reportLogoPath())
        paramsMap.put('sqlParam', sqlParam)

        String extension = CommonUtils.PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.PDF_FORMAT

        PrintOptionType printOptionType = PrintOptionType.PDF
        if(params.printOptionType && params.printOptionType !=PrintOptionType.PDF.key){
            if(params.printOptionType==PrintOptionType.XLSX.key){
                extension = CommonUtils.XLSX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_XLSX
                exportFormat = JasperExportFormat.XLSX_FORMAT
            } else if(params.printOptionType==PrintOptionType.DOCX.key){
                extension = CommonUtils.DOCX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_DOCX
                exportFormat = JasperExportFormat.DOCX_FORMAT
            }
        }
        String outputFileName = "book_reader${extension}"
        JasperReportDef reportDef = new JasperReportDef(
                name: reportFileName,
                fileFormat: exportFormat,
                parameters: paramsMap
        )
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        response.contentType =grailsApplication.config.grails.mime.types[mimeType]
        response.setHeader("Content-disposition", "inline;filename=${outputFileName}")
        response.outputStream << report.toByteArray()
    }
    def bookTrnList() {
        String sqlParam = ""
        params.category ? sqlParam = " AND bd.category_id=" + "${params.category}" : ""
        params.section ? sqlParam += " AND s.section_id=" + "${params.section}" : ""
        params.payType ? sqlParam += " AND bt.pay_type=" + "\"${params.payType}\"" : ""
        params.returnType ? sqlParam += " AND bt.return_type=" + "\"${params.returnType}\"" : ""
        params.author ? sqlParam += " AND bd.author_id=" + "${params.author}" : ""
        params.addAuthor ? sqlParam += " AND bd.add_author=" + "\"${params.addAuthor}\"" : ""
        params.language ? sqlParam += " AND bd.language=" + "\"${params.language}\"" : ""
        params.publisher ? sqlParam += " AND bd.publisher=" + "\"${params.publisher}\"" : ""
        params.haveDue ? sqlParam += " AND bt.fine > 0" : ""
        params.employee_id ? sqlParam += " AND bt.employee_id=" + "${params.employee_id}" : ""
        params.student_id ? sqlParam += " AND bt.student_id=" + "${params.student_id}" : ""
        params.bookName ? sqlParam += " AND bd.id=" + "${params.bookName}" : ""
        params.bookTitle ? sqlParam += " AND bd.id=" + "${params.bookTitle}" : ""
        params.book ? sqlParam += " AND b.id=" + "${params.book}" : ""
        if (params.returnDate && params.toDate) {
            sqlParam += " AND bt.return_date <=" + "\"${params.returnDate}\""
        } else if (params.returnDate && !params.toDate) {
            sqlParam += " AND bt.return_date = " + "\"${params.returnDate}\""
        }
        if (params.teacherStudent) {
            if (params.teacherStudent == 'teacher') {
                sqlParam += " AND bt.employee_id!=\"NULL\""
            } else if (params.teacherStudent == 'student') {
                sqlParam += " AND bt.student_id!=\"NULL\""
            }
        }

        String reportFileName = BOOK_TRN_LIST_JASPER_FILE
        String reportLogo = schoolService.reportLogoPath()
        Map paramsMap = new LinkedHashMap()
        paramsMap.put('REPORT_LOGO', reportLogo)
        paramsMap.put('sqlParam', sqlParam)

        String extension = CommonUtils.PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.PDF_FORMAT

        String outputFileName = "Book_Trn_List${extension}"
        JasperReportDef reportDef = new JasperReportDef(
                name: reportFileName,
                fileFormat: exportFormat,
                parameters: paramsMap
        )
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        response.contentType = grailsApplication.config.grails.mime.types[mimeType]
        response.setHeader("Content-disposition", "inline;filename=${outputFileName}")
        response.outputStream << report.toByteArray()
    }
    def printBarcode(BookBarcodeCommand command) {
        String reportFileName = schoolService.reportFileName(CommonUtils.MODULE_LIBRARY, BAR_CODE_PRINT_JASPER_FILE)
        String sqlParam = " book_category.active_status = 'ACTIVE' and author.active_status = 'ACTIVE' and book_publisher.active_status = 'ACTIVE' and book_details.active_status = 'ACTIVE' and book.active_status = 'ACTIVE'"
        command.bookCategoryId ? sqlParam += " AND book_category.id=" + "${command.bookCategoryId}" : ""
        command.bookAuthorId ? sqlParam += " AND author.id=" + "${command.bookAuthorId}" : ""
        command.bookPublisherId ? sqlParam += " AND book_publisher.id=" + "${command.bookPublisherId}" : ""
        command.bookDetailId ? sqlParam += " AND book.id=" + "${command.bookDetailId}" : ""
        command.bookNameId ? sqlParam += " AND book_details.id=" + "${command.bookNameId}" : ""

        String reportLogo = schoolService.reportLogoPath()
        Map paramsMap = new LinkedHashMap()
        paramsMap.put('schoolName', message(code: "app.school.name"))
        paramsMap.put('schoolAddress', message(code: "app.report.address.line1"))
        paramsMap.put('creditLine', message(code: "company.credit.footer"))
        paramsMap.put('REPORT_LOGO', reportLogo)
        paramsMap.put('sqlParam', sqlParam)

        String extension = CommonUtils.PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.PDF_FORMAT
        String outputFileName = "barcode${extension}"
        JasperReportDef reportDef = new JasperReportDef(
                name: reportFileName,
                fileFormat: exportFormat,
                parameters: paramsMap
        )
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        response.contentType = grailsApplication.config.grails.mime.types[mimeType]
        response.setHeader("Content-disposition", "inline;filename=${outputFileName}")
        response.outputStream << report.toByteArray()

    }

}
