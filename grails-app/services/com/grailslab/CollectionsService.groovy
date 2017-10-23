package com.grailslab

import com.grailslab.accounting.*
import com.grailslab.enums.*
import com.grailslab.gschoolcore.AcademicYear
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.settings.ClassName
import com.grailslab.settings.Section
import com.grailslab.stmgmt.Student
import grails.transaction.Transactional
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.joda.time.LocalDate

class CollectionsService {
    static transactional = false
    def schoolService

    static final String[] sortColumns = ['id','std.studentID','amount','discount','payment','invoiceDate']
    LinkedHashMap manageCollectionPaginationList(GrailsParameterMap params){
        int iDisplayStart = params.iDisplayStart ? params.getInt('iDisplayStart') : CommonUtils.DEFAULT_PAGINATION_START
        int iDisplayLength = params.iDisplayLength ? params.getInt('iDisplayLength') : CommonUtils.DEFAULT_PAGINATION_LENGTH
        String sSortDir = params.sSortDir_0 ? params.sSortDir_0 : CommonUtils.SORT_ORDER_DESC
        int iSortingCol = params.iSortCol_0 ? params.getInt('iSortCol_0') : CommonUtils.DEFAULT_PAGINATION_SORT_IDX
        String sSearch = params.sSearch ? params.sSearch : null
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }
        ClassName className = null
        if(params.className){
            className = ClassName.read(params.className.toLong())
        }

        DateRangeType dateRangeType = DateRangeType.TODAY
        if(params.dateRange){
            dateRangeType = DateRangeType.valueOf(params.dateRange?.toString())
        }
        LocalDate nowDate = new LocalDate();
        Date fromDate
        Date toDate

        if(!dateRangeType || dateRangeType == DateRangeType.TODAY){
            if (params.startDate) {
                fromDate = CommonUtils.getUiPickerDate(params.startDate)
            } else {
                fromDate = nowDate.toDateTimeAtStartOfDay().toDate()
            }
            if (params.endDate) {
                toDate = CommonUtils.getUiPickerDate(params.endDate)
            } else {
                toDate = nowDate.toDateTimeAtStartOfDay().toDate()
            }
        } else{
            String academicYear = schoolService.schoolWorkingYear().value
            int year = Integer.parseInt(academicYear)
            fromDate = nowDate.withYear(year).withMonthOfYear(1).withDayOfMonth(1).toDateTimeAtStartOfDay().toDate()
            toDate = nowDate.plusDays(7).toDate()
        }

        String sortColumn = CommonUtils.getSortColumn(sortColumns,iSortingCol)
        List dataReturns = new ArrayList()
        def c = Invoice.createCriteria()
        def results = c.list(max: iDisplayLength, offset: iDisplayStart) {
            createAlias('student','std')
            and {
                eq("accountType",AccountType.INCOME)
                eq("activeStatus",ActiveStatus.ACTIVE)
                if(className){
                    eq("std.className",className)
                }
                between('invoiceDate', fromDate, toDate)
            }
            if (sSearch) {
                or {
                    ilike('invoiceNo', sSearch)
                    ilike('std.studentID', sSearch)
                }
            }
            order(sortColumn, sSortDir)
//            order('subjectName.sortPosition', CommonUtils.SORT_ORDER_ASC)
        }
        int totalCount = results.totalCount
        if (totalCount > 0) {
            String stdName
            Student student
            results.each { Invoice invoice ->
                student = invoice?.student
                stdName = "${student?.studentID} - ${student?.name}"
                dataReturns.add([DT_RowId: invoice.id,invoiceNo:invoice.invoiceNo, 0: invoice.invoiceNo,1:stdName, 2:"${student?.className?.name} - ${student?.section?.name}", 3: invoice.amount, 4: invoice.discount, 5: invoice.payment, 6:CommonUtils.getUiDateStr(invoice.invoiceDate), 7: ''])
            }
        }
        return [totalCount:totalCount,results:dataReturns]
    }

    static final String[] feePaySortColumns = ['std.section','std.studentID','std.name','std.rollNo','amount','discount','totalPayment','inv.invoiceNo','inv.invoiceDate']
    LinkedHashMap feePayPaginationList(GrailsParameterMap params){
        int iDisplayStart = params.iDisplayStart ? params.getInt('iDisplayStart') : CommonUtils.DEFAULT_PAGINATION_START
        int iDisplayLength = params.iDisplayLength ? params.getInt('iDisplayLength') : CommonUtils.DEFAULT_PAGINATION_LENGTH
        String sSortDir = params.sSortDir_0 ? params.sSortDir_0 : CommonUtils.SORT_ORDER_ASC
        int iSortingCol = params.iSortCol_0 ? params.getInt('iSortCol_0') : CommonUtils.DEFAULT_PAGINATION_SORT_IDX
        String sSearch = params.sSearch ? params.sSearch : null
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }
        FeeItems feeItems = null
        if(params.feeItems){
            feeItems = FeeItems.read(params.feeItems.toLong())
        }
        ItemsDetail itemDetail =null
        if(params.itemDetail){
            itemDetail = ItemsDetail.read(params.itemDetail.toLong())
        }
        ClassName className = null
        if(params.className){
            className = ClassName.read(params.className.toLong())
        }
        def workingYears = schoolService.workingYears()

        String sortColumn = CommonUtils.getSortColumn(feePaySortColumns,iSortingCol)
        List dataReturns = new ArrayList()
        def c = FeePayments.createCriteria()
        def results = c.list(max: iDisplayLength, offset: iDisplayStart) {
            createAlias('student','std')
            createAlias('invoice','inv')
            and {
                'in'("academicYear", workingYears)
                eq("feeItems", feeItems)
                eq("activeStatus",ActiveStatus.ACTIVE)
                eq("std.className",className)
                if(itemDetail){
                    eq("itemsDetail", itemDetail)
                }
            }
            if (sSearch) {
                or {
                    ilike('inv.invoiceNo', sSearch)
                    ilike('std.studentID', sSearch)
                }
            }
            order(sortColumn, sSortDir)
        }
        int totalCount = results.totalCount
//        int serial = iDisplayStart;
        if (totalCount > 0) {
            String stdName
            Student student
            Invoice invoice
            Section section
            results.each { FeePayments feePayments ->
                student = feePayments?.student
                section = student.section
                invoice = feePayments?.invoice
                dataReturns.add([DT_RowId: feePayments.id, 0: section.name, 1: student.studentID, 2: student.name, 3: student.rollNo, 4: feePayments.amount, 5: feePayments.discount, 6: feePayments.netPayment, 7:invoice.invoiceNo, 8:CommonUtils.getUiDateStr(invoice.invoiceDate)])
            }
        }
        return [totalCount:totalCount,results:dataReturns]
    }

    List<Long> feeItemPaidStudentIdsList(GrailsParameterMap params){
        FeeItems feeItems = null
        if(params.feeItems){
            feeItems = FeeItems.read(params.feeItems.toLong())
        }
        ItemsDetail itemDetail =null
        if(params.itemDetail){
            itemDetail = ItemsDetail.read(params.itemDetail.toLong())
        }
        ClassName className = null
        if(params.className){
            className = ClassName.read(params.className.toLong())
        }

        def workingYears = schoolService.workingYears()
        def c = FeePayments.createCriteria()
        List<Long> feeItemPayStdList = c.list() {
            createAlias('student','std')
            and {
                'in'("academicYear", workingYears)
                eq("feeItems", feeItems)
                eq("activeStatus",ActiveStatus.ACTIVE)
                eq("std.className",className)
                if(itemDetail){
                    eq("itemsDetail", itemDetail)
                }
            }
            projections {
                property "std.id"
            }
        } as List<Long>
        return feeItemPayStdList
    }

    def quickFeeList(ClassName className, String quick, AcademicYear academicYear = null){
        def workingYears = schoolService.workingYears()
        String quickFee =null
        if(quick){
            quickFee="quickFee"+quick
        }

        def c = FeeItems.createCriteria()
        def results = c.list() {
            and {
                if(quickFee){
                    eq(quickFee, true)
                }
                eq("activeStatus", ActiveStatus.ACTIVE)
                or {
                    and {
                        eq("className", className)
                        eq("feeAppliedType", FeeAppliedType.CLASS_STD)
                    }
                    and {
                        eq("feeAppliedType", FeeAppliedType.ALL_STD)
                    }
                }
            }
            order('code', CommonUtils.SORT_ORDER_ASC)
        }
        List dataReturns = new ArrayList()
        results.each { FeeItems items ->
            dataReturns.add([id: items.id, name: "${items.code} - ${items.name}", amount: items.amount, discount: items.discount])
        }
        return dataReturns
    }

    def classFeeDDList(ClassName className, String quick){
        AcademicYear workingYear = schoolService.workingYear(className)
        String quickFee =null
        if(quick){
            quickFee="quickFee"+quick
        }

        def c = FeeItems.createCriteria()
        def results = c.list() {
            if(quickFee){
                eq(quickFee, true)
            }
            eq("activeStatus", ActiveStatus.ACTIVE)
            or {
                eq("academicYear", workingYear)
                'ne'('feeType', FeeType.ACTIVATION)
            }
            or {
                and {
                    eq("className", className)
                    eq("feeAppliedType", FeeAppliedType.CLASS_STD)
                }
                and {
                    eq("feeAppliedType", FeeAppliedType.ALL_STD)
                }

            }
            order('code', CommonUtils.SORT_ORDER_ASC)
        }
        List dataReturns = new ArrayList()
        results.each { FeeItems items ->
            dataReturns.add([id: items.id, name: "${items.code} - ${items.name}"])
        }
        return dataReturns
    }
    def classMonthlyFeeDDList(ClassName className, String quick){
        AcademicYear workingYear = schoolService.workingYear(className)
        String quickFee =null
        if(quick){
            quickFee="quickFee"+quick
        }

        def c = FeeItems.createCriteria()
        def results = c.list() {
            if(quickFee){
                eq(quickFee, true)
            }
            eq("iterationType", FeeIterationType.MONTHLY)
            eq("activeStatus", ActiveStatus.ACTIVE)
            or {
                and {
                    eq("className", className)
                    eq("feeAppliedType", FeeAppliedType.CLASS_STD)
                }
                and {
                    eq("feeAppliedType", FeeAppliedType.ALL_STD)
                }

            }
            order('code', CommonUtils.SORT_ORDER_ASC)
        }
        List dataReturns = new ArrayList()
        results.each { FeeItems items ->
            dataReturns.add([id: items.id, name: "${items.code} - ${items.name}"])
        }
        return dataReturns
    }

    def feeItemDetailsDDList(FeeItems feeItems){
        def workingYears = schoolService.workingYears()
        def c = ItemsDetail.createCriteria()
        def results = c.list() {
            and {
                eq("feeItems", feeItems)
                eq("activeStatus", ActiveStatus.ACTIVE)
            }
            order('sortPosition', CommonUtils.SORT_ORDER_ASC)
        }
        List dataReturns = new ArrayList()
        results.each { ItemsDetail items ->
            dataReturns.add([id: items.id, name: "${items.name}"])
        }
        return dataReturns
    }

    def studentFeeList(Student student, String quick, Integer feePaidCriteria, AcademicYear academicYear = null){
        def workingYears = schoolService.workingYears()
        ClassName className = student.className
        String quickFee =null
        if(quick){
            quickFee="quickFee"+quick
        }
        List<Long> paidFeeItemIds = null
        if(feePaidCriteria && feePaidCriteria ==1){
            paidFeeItemIds = studentSingleItemPaidIds(student)
        }

        def c = FeeItems.createCriteria()
        def results = c.list() {
            if(quickFee){
                eq(quickFee, true)
            }
            if(paidFeeItemIds && paidFeeItemIds.size()>0){
                not {'in'("id",paidFeeItemIds)}
            }
            eq("activeStatus", ActiveStatus.ACTIVE)
            or {
                and {
                    eq("className", className)
                    eq("feeAppliedType", FeeAppliedType.CLASS_STD)
                }
                and {
                    eq("feeAppliedType", FeeAppliedType.ALL_STD)
                }

            }
            order('code', CommonUtils.SORT_ORDER_ASC)
        }
        return results
    }

    def allFeeListForColReport(AcademicYear academicYear, ClassName className){
        def c = FeeItems.createCriteria()
        def results = c.list() {
            eq("activeStatus", ActiveStatus.ACTIVE)
            or {
                and {
                    if (className) {
                        eq("className", className)
                    }
                    eq("feeAppliedType", FeeAppliedType.CLASS_STD)
                }
                and {
                    eq("feeAppliedType", FeeAppliedType.ALL_STD)
                }

            }
            order('code', CommonUtils.SORT_ORDER_ASC)
        }
        return results
    }

    def allFeeItemsByStudentDD(Student student, Integer feePaidCriteria = null){
        ClassName className = student.className
        List<Long> paidFeeItemIds = null
        if(feePaidCriteria && feePaidCriteria ==1){
            paidFeeItemIds = studentSingleItemPaidIds(student)
        }

        def c = FeeItems.createCriteria()
        def results = c.list() {
            if(paidFeeItemIds && paidFeeItemIds.size()>0){
                not {'in'("id",paidFeeItemIds)}
            }
            eq("activeStatus", ActiveStatus.ACTIVE)
            or {
                and {
                    eq("className", className)
                    eq("feeAppliedType", FeeAppliedType.CLASS_STD)
                }
                and {
                    eq("feeAppliedType", FeeAppliedType.ALL_STD)
                }

            }
            order('code', CommonUtils.SORT_ORDER_ASC)
        }
        List dataReturns = new ArrayList()
        results.each { FeeItems items ->
            dataReturns.add([id: items.id, name: "${items.code} - ${items.name} [amount: ${items.amount}, discount: ${items.discount}]"])
        }
        return dataReturns
    }

    @Transactional
    Invoice saveInvoice(Invoice invoice,List<InvoiceDetails> details, List<FeePayments> paymentsList){
        Invoice savedInvoice = invoice.save()
        if(savedInvoice){
            details.each {InvoiceDetails invDetails ->
                invDetails.invoice=savedInvoice
                invDetails.save()
            }
            paymentsList.each {FeePayments feePayments ->
                feePayments.invoice=savedInvoice
                feePayments.save()
            }
            return savedInvoice
        }
        return null
    }


    def studentSingleItemPaidIds(Student student){
        def c = FeePayments.createCriteria()
        def results = c.list() {
            createAlias('feeItems', 'item')
            and {
                eq("student", student)
                eq("activeStatus", ActiveStatus.ACTIVE)
                'in'("item.iterationType", [FeeIterationType.RECEIVE,FeeIterationType.YEARLY] as List)
            }
        }
        return results.collect {it.feeItems.id}
    }
    def paymentItemDetails(Student student, FeeItems feeItems){
        def workingYears = schoolService.workingYears()
        def c = FeePayments.createCriteria()
        def results = c.list() {
            createAlias('itemsDetail', 'detail')
            and {
                eq("student", student)
                eq("feeItems", feeItems)
                eq("activeStatus", ActiveStatus.ACTIVE)

            }
            order('detail.sortPosition', CommonUtils.SORT_ORDER_ASC)
        }
        return results
    }

    Double collectionAmount(AcademicYear academicYear, ClassName className, Date fromDate, Date toDate){
        if (academicYear){
            academicYear = schoolService.schoolWorkingYear()
        }
        def c = FeePayments.createCriteria()
        def results = c.list() {
            createAlias('student', 'st')
            createAlias('invoice', 'inv')
            and {
                eq("academicYear", academicYear)
                eq("st.className", className)
                eq("activeStatus", ActiveStatus.ACTIVE)
                eq("inv.activeStatus", ActiveStatus.ACTIVE)
                between("inv.invoiceDate", fromDate, toDate)
            }
            projections {
                sum('totalPayment')
            }
        }
        return results[0]? results[0] : 0
    }
    Double collectionDueDateAmount(AcademicYear academicYear, ClassName className, Date fromDate, Date toDate, int dueMonth){
        if (academicYear){
            academicYear = schoolService.schoolWorkingYear()
        }
        def c = FeePayments.createCriteria()
        def results = c.list() {
            createAlias('student', 'st')
            createAlias('invoice', 'inv')
            and {
                eq("academicYear", academicYear)
                isNull('itemsDetail')
                eq("st.className", className)
                eq("activeStatus", ActiveStatus.ACTIVE)
                eq("inv.activeStatus", ActiveStatus.ACTIVE)
                between("inv.invoiceDate", fromDate, toDate)
            }
            projections {
                sum('totalPayment')
            }
        }
        Double nonMonthlySum = results[0]? results[0] : 0

        def cc = FeePayments.createCriteria()
        def cresults = cc.list() {
            createAlias('student', 'st')
            createAlias('invoice', 'inv')
            and {
                eq("academicYear", academicYear)
                isNotNull('itemsDetail')
                eq("itemDetailSortPosition", dueMonth)
                eq("st.className", className)
                eq("activeStatus", ActiveStatus.ACTIVE)
                eq("inv.activeStatus", ActiveStatus.ACTIVE)
            }
            projections {
                sum('totalPayment')
            }
        }
        Double monthlySum = cresults[0]? cresults[0] : 0
        return nonMonthlySum + monthlySum
    }

    Double chartTotalAmount(AcademicYear academicYear, ChartOfAccount account, ClassName className, Date fromDate, Date toDate, int dueMonth){
        if (academicYear){
            academicYear = schoolService.schoolWorkingYear()
        }
        def c = FeePayments.createCriteria()
        def results = c.list() {
            createAlias('student', 'st')
            createAlias('invoice', 'inv')
            createAlias('feeItems', 'fee')
            and {
                eq("fee.chartOfAccount", account)
                eq("academicYear", academicYear)
                isNull('itemsDetail')
                if (className){
                    eq("st.className", className)
                }
                eq("activeStatus", ActiveStatus.ACTIVE)
                eq("inv.activeStatus", ActiveStatus.ACTIVE)
                between("inv.invoiceDate", fromDate, toDate)
            }
            projections {
                sum('totalPayment')
            }
        }
        Double nonMonthlySum = results[0]? results[0] : 0

        def cc = FeePayments.createCriteria()
        def cresults = cc.list() {
            createAlias('student', 'st')
            createAlias('invoice', 'inv')
            createAlias('feeItems', 'fee')
            and {
                eq("fee.chartOfAccount", account)
                eq("academicYear", academicYear)
                isNotNull('itemsDetail')
                eq("itemDetailSortPosition", dueMonth)
                if (className){
                    eq("st.className", className)
                }
                eq("activeStatus", ActiveStatus.ACTIVE)
                eq("inv.activeStatus", ActiveStatus.ACTIVE)
            }
            projections {
                sum('totalPayment')
            }
        }
        Double monthlySum = cresults[0]? cresults[0] : 0
        return nonMonthlySum + monthlySum
    }
}
