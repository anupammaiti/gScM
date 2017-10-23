package com.grailslab.account

import com.grailslab.CommonUtils
import com.grailslab.accounting.FeeItems
import com.grailslab.accounting.StudentDiscount
import com.grailslab.command.StudentDiscountCommand
import com.grailslab.command.StudentScholarshipCommand
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.stmgmt.Student
import grails.converters.JSON
import org.springframework.dao.DataIntegrityViolationException

class DiscountController {
    def studentDiscountService
    def studentService
    def schoolService
    def messageSource
    def feesService
    def collectionsService
    def index() {
        LinkedHashMap resultMap = studentDiscountService.studentDiscountPaginateList(params)
        if (!resultMap || resultMap.totalCount == 0) {
            render(view: '/collection/discount/discount', model: [dataReturn: null, totalCount: 0])
            return
        }
        int totalCount = resultMap.totalCount
        render(view: '/collection/discount/discount', model: [dataReturn: resultMap.results, totalCount: totalCount])
    }

    def edit(Long id){
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        StudentDiscount discount = StudentDiscount.read(id)
        if (!discount) {
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        FeeItems feeItems = discount.feeItems
        result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
        result.put(CommonUtils.OBJ,discount)
        result.put("stdName",discount.student?.name)
        result.put("feeItemsId",feeItems?.id)
        result.put("feeItemsName","${feeItems.code} - ${feeItems.name} [amount: ${feeItems.amount}, discount: ${feeItems.discount}]")
        outPut = result as JSON
        render outPut
    }
    def delete(Long id){
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        StudentDiscount studentDiscount = StudentDiscount.get(id)
        if(studentDiscount) {
            try {
                studentDiscount.activeStatus = ActiveStatus.INACTIVE
                studentDiscount.save(flush: true)
                result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
                result.put(CommonUtils.MESSAGE,"Discount deleted successfully.")
                outPut = result as JSON
                render outPut
                return

            }

            catch(DataIntegrityViolationException e) {
                result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
                result.put(CommonUtils.MESSAGE,"Discount could not deleted. Already in use.")
                outPut = result as JSON
                render outPut
                return
            }

        }
        result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
        result.put(CommonUtils.MESSAGE,"Discount not found")
        outPut = result as JSON
        render outPut
        return

    }
    def save(StudentDiscountCommand command){
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
            outPut = result as JSON
            render outPut
            return
        }

        StudentDiscount discount
        if(!command.id){
            discount = studentDiscountService.byStudentAndFeeItem(command.student,command.feeItems)
        }
        if(discount){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "Already ${discount.discountPercent}% discount added. You can Edit.")
            outPut = result as JSON
            render outPut
            return
        }


        String message
        if(command.id){
            discount = StudentDiscount.get(command.id)
            if(!discount){
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
                outPut = result as JSON
                render outPut
                return
            }
            discount.properties['discountPercent'] = command.properties
            message ="Discount updated successfully."
            result.put(CommonUtils.IS_ERROR, Boolean.FALSE)

        }else {
            discount = new StudentDiscount(command.properties)
            message ="Discount added successfully."
            result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        }

        if(discount.hasErrors() || !discount.save()){
            def errorList = discount?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            message = errorList?.join('\n')
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
        }
        result.put(CommonUtils.MESSAGE, message)
        outPut = result as JSON
        render outPut
        return
    }

    def list() {
        LinkedHashMap gridData
        String result
        LinkedHashMap resultMap =studentDiscountService.studentDiscountPaginateList(params)

        if(!resultMap || resultMap.totalCount== 0){
            gridData = [iTotalRecords: 0, iTotalDisplayRecords: 0, aaData: []]
            result = gridData as JSON
            render result
            return
        }
        int totalCount =resultMap.totalCount
        gridData = [iTotalRecords: totalCount, iTotalDisplayRecords: totalCount, aaData: resultMap.results]
        result = gridData as JSON
        render result
    }

    def loadFee(Long studentId){
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        Student student = Student.read(studentId)
        if(!student){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }

        def feeList=collectionsService.allFeeItemsByStudentDD(student)
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put('feeList', feeList)
        outPut = result as JSON
        render outPut
        return
    }

    //student scholarship manage
    def scholarship() {
        def resultMap = studentService.studentScholarshipList(params)
//        def studentList=studentService.allStudentWithRollSectionDD()
        if (!resultMap) {
            render(view: '/collection/discount/scholarship', model: [dataReturn: null, studentList:null])
            return
        }
        render(view: '/collection/discount/scholarship', model: [dataReturn: resultMap, studentList:null])
    }


    def saveScholarship(StudentScholarshipCommand command){
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
            outPut = result as JSON
            render outPut
            return
        }

        Student student = command.student
        if(!student) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        student.scholarshipObtain = true
        student.scholarshipType = command.scholarshipType

        String message
        if(student.hasErrors() || !student.save()){
            def errorList = student?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            message = errorList?.join('\n')
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
        }
        result.put(CommonUtils.MESSAGE, message)
        outPut = result as JSON
        render outPut
        return
    }

    def scholarshipDelete(StudentScholarshipCommand command){

        LinkedHashMap result = new LinkedHashMap()
        String outPut

        Student student = command.student
        if(student) {

                student.activeStatus = ActiveStatus.INACTIVE
                student.save(flush: true)
                result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
                result.put(CommonUtils.MESSAGE," deleted successfully.")
                outPut = result as JSON
                render outPut
                return


        }

        result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
        result.put(CommonUtils.MESSAGE," Not found")
        outPut = result as JSON
        render outPut
        return

    }





}
