package com.grailslab.admin

import com.grailslab.CommonUtils
import com.grailslab.command.*
import com.grailslab.enums.AcaYearType
import com.grailslab.enums.SelectionTypes
import com.grailslab.enums.Shift
import com.grailslab.enums.SmsUseType
import com.grailslab.gschoolcore.AcademicYear
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.messaging.SmsBilling
import com.grailslab.messaging.SmsDraft
import com.grailslab.settings.ClassName
import com.grailslab.settings.ShiftExam
import grails.converters.JSON

class MessagingController {

    def schoolService
    def studentService
    def sectionService
    def classNameService
    def registrationService
    def messagingService
    def messageSource
    def employeeService
    def hrCategoryService
    def examService
    def tabulationService
    def shiftExamService


    def index() {
        def academicYearList = schoolService.academicYears(AcaYearType.ALL)
        LinkedHashMap resultMap = messagingService.messagePaginateList(params)

        if (!resultMap || resultMap.totalCount == 0) {
            render(view: '/admin/messaging/index', model: [academicYearList:academicYearList, dataReturn: null, totalCount: 0])
            return
        }
        int totalCount = resultMap.totalCount
        render(view: '/admin/messaging/index', model: [academicYearList:academicYearList, dataReturn: resultMap.results, totalCount: totalCount])
    }
    def smsList() {
        LinkedHashMap gridData
        String result
        LinkedHashMap resultMap =messagingService.messagePaginateList(params)

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

    def quickSms() {
        render (view: '/admin/messaging/quickSms')
    }

    def step1() {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        SelectionTypes selectionType = SelectionTypes.BY_CLASS
        if(params.selectionType){
            selectionType = SelectionTypes.valueOf(params.selectionType)
        }
        AcademicYear academicYear
        if (params.academicYear) {
            academicYear = AcademicYear.valueOf(params.academicYear)
        }

        List dataList
        if (selectionType == SelectionTypes.BY_SHIFT) {
            def shiftList = Arrays.asList(Shift.values())
            List dataReturns = new ArrayList()
            shiftList.each { Shift shift ->
                dataReturns.add([id: shift.key, name: shift.value])
            }
            render(view: '/admin/messaging/step1', model: [academicYear: academicYear, draftSmsId:params.draftSmsId, dataList: dataReturns, selectionType: selectionType.key, title: "Select Shift(s)"])
            return
        } else if (selectionType == SelectionTypes.BY_CLASS) {
            dataList = classNameService.classNameDropDownList()
            render(view: '/admin/messaging/step1', model: [academicYear: academicYear, draftSmsId:params.draftSmsId, dataList: dataList, selectionType: selectionType.key, title: "Select Class(s)"])
            return
        } else if (selectionType == SelectionTypes.BY_SECTION) {
            dataList = sectionService.sectionDropDownList(academicYear)
            render(view: '/admin/messaging/step1', model: [academicYear: academicYear, draftSmsId:params.draftSmsId, dataList: dataList, selectionType: selectionType.key, title: "Select Section(s)"])
            return
        } else if (selectionType == SelectionTypes.BY_TEACHER) {
            dataList = hrCategoryService.hrCategoryDropDownList()
            render(view: '/admin/messaging/step1', model: [draftSmsId:params.draftSmsId, dataList: dataList, selectionType: selectionType.key, title: "Select Category(s)"])
            return
        } else if (selectionType == SelectionTypes.BY_STUDENT) {
            render(view: '/admin/messaging/step1', model: [academicYear: academicYear, draftSmsId:params.draftSmsId, selectionType: selectionType.key, title: "Select Student(s)"])
            return
        }
        flash.message = 'Selection Type Not Found'
        redirect(action: 'index')
    }

    def step2() {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        List studentList
        List<Long> selectionIds
        List checkedList=new ArrayList()
        if (params.checkedId){
            if (params.checkedId instanceof String){
                checkedList.add(params.checkedId)
            }else {
                checkedList = params.checkedId
            }
        }else {
            flash.message = 'Invalid selection'
            redirect(action: 'index')
            return
        }
        if(!params.selectionType){
            flash.message = 'Please select any categoty'
            redirect(action: 'index')
        }
        SelectionTypes selectionType = SelectionTypes.valueOf(params.selectionType)
        if(!selectionType){
            flash.message = 'Please select any categoty'
            redirect(action: 'index')
        }
        AcademicYear academicYear
        if (params.academicYear) {
            academicYear = AcademicYear.valueOf(params.academicYear)
        }

        String draftMessage
        if(params.draftSmsId){
            Long draftId = params.getLong('draftSmsId')
            SmsDraft smsDraft = SmsDraft.read(draftId)
            draftMessage = smsDraft?.message
        }

        String selectListHeader="Select Student(s)"

        if (selectionType == SelectionTypes.BY_SHIFT) {
            List<Shift> shiftList = checkedList.collect { it as Shift }
            selectionIds = sectionService.sectionIdsByShift(shiftList, academicYear)
            studentList =studentService.step2StudentListForMessage(selectionIds)
            render(view: '/admin/messaging/step2', model: [draftMessage:draftMessage, studentList:studentList,selectListHeader:selectListHeader,selectionType:selectionType])
            return
        } else if (selectionType == SelectionTypes.BY_CLASS) {
            List<Long> classIdList = checkedList.collect { it as Long }
            selectionIds = sectionService.sectionIdsByClassId(classIdList, academicYear)
            studentList =studentService.step2StudentListForMessage(selectionIds)
            render(view: '/admin/messaging/step2', model: [draftMessage:draftMessage, studentList:studentList,selectListHeader:selectListHeader,selectionType:selectionType])
            return
        } else if (selectionType == SelectionTypes.BY_SECTION) {
            selectionIds = checkedList.collect { it as Long }
            studentList =studentService.step2StudentListForMessage(selectionIds)
            render(view: '/admin/messaging/step2', model: [draftMessage:draftMessage, studentList:studentList,selectListHeader:selectListHeader,selectionType:selectionType])
            return
        } else if (selectionType == SelectionTypes.BY_TEACHER) {
            selectListHeader="Select Teacher & Other(s)"
            List<Long> deptIdList = checkedList.collect { it as Long }
            studentList =employeeService.step2EmpListForMessage(deptIdList,null)
            render(view: '/admin/messaging/step2', model: [draftMessage:draftMessage, studentList:studentList,selectListHeader:selectListHeader,selectionType:selectionType])
            return
        } else if (selectionType == SelectionTypes.BY_STUDENT) {
            selectionIds = checkedList.collect { it as Long }
            studentList =studentService.step2StudentListForMessage(selectionIds, SelectionTypes.BY_STUDENT)
            render(view: '/admin/messaging/step2', model: [draftMessage:draftMessage, studentList:studentList,selectListHeader:selectListHeader,selectionType:selectionType])
            return
        }
        flash.message = 'Selection Type Not Found'
        redirect(action: 'index')
    }

    def list() {
        LinkedHashMap gridData
        String result
        LinkedHashMap resultMap = studentService.studentPaginateList(params)

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
    def drafts() {
        LinkedHashMap resultMap = messagingService.draftPaginateList(params)

        if (!resultMap || resultMap.totalCount == 0) {
            render(view: '/admin/messaging/draftList', model: [dataReturn: null, totalCount: 0])
            return
        }
        int totalCount = resultMap.totalCount
        render(view: '/admin/messaging/draftList', model: [dataReturn: resultMap.results, totalCount: totalCount])
    }

    def draftSms(){
        render(view: '/admin/messaging/draftMessage')
    }

    def draftList() {
        LinkedHashMap gridData
        String result
        LinkedHashMap resultMap =messagingService.draftPaginateList(params)

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

    def save(SmsDraftCommand command) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }

        if (command.hasErrors()) {
            def errorList = command?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            flash.message= errorList?.join(', ')
            redirect(action: 'index')
            return
        }
        SmsDraft smsDraft
        if (command.id) {
            smsDraft = SmsDraft.get(command.id)
            if (!smsDraft) {
                flash.message= CommonUtils.COMMON_NOT_FOUND_MESSAGE
                redirect(action: 'index')
                return
            }
            smsDraft.properties = command.properties
            flash.message ='Draft updated Successfully'
        } else {
            smsDraft = new SmsDraft(command.properties)
            flash.message ='Draft Added Successfully'
        }
        smsDraft.numOfSms = messagingService.smsLength(command.message)

        if(smsDraft.hasErrors() || !smsDraft.save()){
            def errorList = smsDraft?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            flash.message= errorList?.join(',')
            redirect(action: 'index')
            return
        }
        redirect(action: 'drafts')
    }

    def edit(Long id) {
        SmsDraft smsDraft = SmsDraft.read(id)
        if (!smsDraft) {
            flash.message=CommonUtils.COMMON_NOT_FOUND_MESSAGE
            redirect(action: 'drafts')
            return
        }
        render(view: '/admin/messaging/draftMessage', model: [smsDraft:smsDraft])
    }

    def delete(Long id) {
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        SmsDraft draft = SmsDraft.get(id)
        if(draft) {
            draft.activeStatus = ActiveStatus.DELETE
            draft.save()
            result.put(CommonUtils.IS_ERROR, false)
            result.put(CommonUtils.MESSAGE, "Message Draft deleted successfully.")
            outPut = result as JSON
            render outPut
            return
        }
        result.put(CommonUtils.IS_ERROR,true)
        result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_ERROR_MESSAGE)
        outPut = result as JSON
        render outPut
        return
    }

    def sendMessage(SmsCommand command) {
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
        def numberOfSelection = command.selectIds.split(',')
        List<String> mobileNos=new ArrayList<String>()
        numberOfSelection.each {idx ->
            mobileNos.add(params."mobileNos_${idx}")
        }
        Integer numOfSender = mobileNos.size()
        String mobileNoStr = mobileNos.join(',')

        LinkedHashMap resultMap = messagingService.sendMessages(command.message,mobileNoStr, numOfSender)
        outPut = resultMap as JSON
        render outPut
    }

    def sendQuickSms(QuickSmsCommand command) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        if (command.hasErrors()) {
            def errorList = command?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, errorList?.join(','))
            outPut=result as JSON
            render outPut
            return
        }
        def numberOfSelection = command.phoneNumbers.split(',')
        Integer numOfSender = numberOfSelection.size()

        LinkedHashMap resultMap = messagingService.sendMessages(command.textMessage,command.phoneNumbers, numOfSender)
        outPut = resultMap as JSON
        render outPut

    }
    def result(Long id){
        ShiftExam shiftExam
        ClassName className
        def classNameList
        def sectionExamList
        def examNameList = shiftExamService.examNameDropDown()
        if (id) {
            shiftExam = ShiftExam.read(id)
        }
        if (params.class) {
            className = ClassName.read(params.getLong('class'))
        }
        if (shiftExam) {
            classNameList = classNameService.classNameInIdListDD(shiftExam.classIds);
        }
        if (shiftExam) {
            sectionExamList = examService.examsForSmsResult(shiftExam, className);
        }

        render(view: '/admin/messaging/result', model: [examNameList: examNameList, shiftExam: shiftExam, className: className, classNameList: classNameList, sectionExamList: sectionExamList])
    }

    def students(){
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        List<Long> examIds
        List checkedList=new ArrayList()
        if (params.checkedId){
            if (params.checkedId instanceof String){
                checkedList.add(params.checkedId)
            }else {
                checkedList = params.checkedId
            }
        }else {
            flash.message = 'Invalid selection'
            redirect(action: 'index')
            return
        }

        examIds = checkedList.collect { it as Long }
        List studentList =tabulationService.tabulationListForSms(examIds)
        if(!studentList){
            flash.message = 'No student available'
            redirect(action: 'index')
            return
        }
        render(view: '/admin/messaging/resultStdList', model: [studentList:studentList])
    }
    def sendResultMessage(ResultMsgCommand command) {
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
        List<Long> tabulationIds = command.tabulationIds.split(',').collect { it as Long }
        List studentList = tabulationService.prepareResultSms(tabulationIds)
        if(!studentList){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "No valid number found")
            outPut=result as JSON
            render outPut
            return
        }
        int totalStudent = studentList.size()
        def selectedTabulation
        Integer totalSms = 0
        Integer smsLength = 0
        for (int i=0; i<totalStudent; i++) {
            selectedTabulation = studentList[i]
            smsLength = messagingService.smsLength(selectedTabulation.msgStr)
            totalSms += smsLength
        }
        LinkedHashMap checkResult = messagingService.checkIfSmsSendable(totalSms)

        Boolean isError = (Boolean) checkResult.isError
        if(isError) {
            outPut = checkResult as JSON
            render outPut
            return
        }
        LinkedHashMap resultSms
        String resMessage ="Message Sent Successfully."
        String exMessage =" "
        Integer errorCount = 0
        for (int j=0; j<totalStudent; j++) {
            selectedTabulation = studentList[j]
            resultSms = messagingService.sendResultMessages(selectedTabulation.msgStr, selectedTabulation.mobileNo)
            isError = (Boolean) resultSms.isError
            if(isError) {
                errorCount += 1
                exMessage += resultSms.message
            }
        }
        if (errorCount > 0) {
            if (errorCount == totalStudent) {
                result.put(CommonUtils.IS_ERROR,true)
                result.put('message',"Can't send sms now due to internal error. Please contact to admin")
                outPut = result as JSON
                render outPut
                return
            } else {
                resMessage = "Message Sent Successfully but some are failed. Reasons: ${exMessage}"
            }
        }
        SmsBilling lastBilling = SmsBilling.last()
        int smsBalance = 0
        if(lastBilling){
            smsBalance = lastBilling.smsBalance
        }

        Integer currBalance = smsBalance - totalSms
        new SmsBilling(smsLength: smsLength, message:"SMS Result to selected Students",
                numOfSender: totalStudent, smsUseType: SmsUseType.SEND, billingDate: new Date(),
                numOfSms:totalSms, smsBalance: currBalance, smsPrice: messagingService.smsPrice(totalSms), expireDate: lastBilling?.expireDate).save()

        result.put(CommonUtils.IS_ERROR,false)
        result.put('message',resMessage)
        outPut = result as JSON
        render outPut
    }

    def smsPurchase(SmsPurchaseCommand command) {
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
        Integer smsBalance = 0
        SmsBilling lastBilling = SmsBilling.last()
        if(lastBilling && lastBilling.smsBalance > 0) {
            smsBalance = lastBilling.smsBalance
        }
        SmsBilling smsBilling = new SmsBilling(command.properties)
        smsBilling.smsUseType = SmsUseType.BUY
        smsBilling.smsBalance = smsBalance + command.numOfSms

        result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
        String message ='You have purchased SMS successfully.'

        SmsBilling savedBilling = smsBilling.save()
        if (!savedBilling) {
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            message = "Problem to purchase SMS now. Please contact to admin"
            outPut=result as JSON
            render outPut
            return
        }
        result.put(CommonUtils.MESSAGE,message)
        outPut=result as JSON
        render outPut
    }
}

