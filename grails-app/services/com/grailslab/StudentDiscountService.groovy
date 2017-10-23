package com.grailslab

import com.grailslab.accounting.FeeItems
import com.grailslab.accounting.StudentDiscount
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.stmgmt.Student
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

class StudentDiscountService {
    static transactional = false
    static final String[] sortColumns = ['id','std.studentID','std.name','std.saction','fItem.code','discountPercent']

    LinkedHashMap studentDiscountPaginateList(GrailsParameterMap params) {
        int iDisplayStart = params.iDisplayStart ? params.getInt('iDisplayStart') : CommonUtils.DEFAULT_PAGINATION_START
        int iDisplayLength = params.iDisplayLength ? params.getInt('iDisplayLength') : CommonUtils.DEFAULT_PAGINATION_LENGTH
        String sSortDir = params.sSortDir_0 ? params.sSortDir_0 : CommonUtils.SORT_ORDER_DESC
        int iSortingCol = params.iSortCol_0 ? params.getInt('iSortCol_0') : CommonUtils.DEFAULT_PAGINATION_SORT_IDX
        //Search string, use or logic to all fields that required to include
        String sSearch = params.sSearch ? params.sSearch : null
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }
        String sortColumn = CommonUtils.getSortColumn(sortColumns, iSortingCol)
        List dataReturns = new ArrayList()
        def c = StudentDiscount.createCriteria()
        def results = c.list(max: iDisplayLength, offset: iDisplayStart) {
            createAlias('student', 'std')
            createAlias('feeItems', 'fItem')
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)

            }
            if (sSearch) {
                or {
                    ilike('std.studentID', sSearch)
                    ilike('std.name', sSearch)
                    ilike('fItem.name', sSearch)
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
            Student student
            FeeItems feeItems
            Double amount =0
            Double discountAmount =0
            Double netPayable =0
            results.each { StudentDiscount studentDiscount ->
                if (sSortDir.equals(CommonUtils.SORT_ORDER_ASC)) {
                    serial++
                } else {
                    serial--
                }
                student = studentDiscount.student
                feeItems = studentDiscount.feeItems
                amount = feeItems?.amount
                discountAmount = amount * studentDiscount.discountPercent * 0.01
                netPayable = amount-discountAmount
                dataReturns.add([DT_RowId: studentDiscount.id,0:serial, 1: "${student.studentID} - ${student.name}", 2: "${student.section?.name} - ${student.className?.name}", 3: "${feeItems.code} - ${feeItems.name}", 4: studentDiscount.discountPercent + ' % ', 5:amount, 6:netPayable.round(2), 7: ''])
            }
        }
        return [totalCount: totalCount, results: dataReturns]
    }
    StudentDiscount byStudentAndFeeItem(Student student, FeeItems feeItems, Long discountId = null){
        StudentDiscount discount = null
        if(discountId){ //update Discount case
            discount = StudentDiscount.findByStudentAndFeeItemsAndActiveStatusAndIdNotEqual(student,feeItems,ActiveStatus.ACTIVE,discountId)
        } else {
            discount = StudentDiscount.findByStudentAndFeeItemsAndActiveStatus(student,feeItems,ActiveStatus.ACTIVE)
        }
        return discount
    }
}