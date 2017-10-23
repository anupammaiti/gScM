package com.grailslab

import com.grailslab.enums.SelectionTypes
import com.grailslab.enums.Shift
import com.grailslab.enums.SmsUseType
import com.grailslab.enums.StudentStatus
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.messaging.SmsBilling
import com.grailslab.messaging.SmsDraft
import com.grailslab.settings.Section
import com.grailslab.stmgmt.Registration
import com.grailslab.stmgmt.Student
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

import java.util.regex.Pattern

class MessagingService {
    static transactional = false
    def grailsApplication
    PowerSmsService powerSmsService

        static final String[] sortColumns = ['billingDate','smsUseType','message','numOfSender','totalSms','smsBalance','expireDate','createdBy']
        LinkedHashMap messagePaginateList(GrailsParameterMap params){
            int iDisplayStart = params.iDisplayStart ? params.getInt('iDisplayStart') : CommonUtils.DEFAULT_PAGINATION_START
            int iDisplayLength = params.iDisplayLength ? params.getInt('iDisplayLength') : CommonUtils.DEFAULT_PAGINATION_LENGTH
            String sSortDir = params.sSortDir_0 ? params.sSortDir_0 : CommonUtils.SORT_ORDER_DESC
            int iSortingCol = params.iSortCol_0 ? params.getInt('iSortCol_0') : CommonUtils.DEFAULT_PAGINATION_SORT_IDX
            //Search string, use or logic to all fields that required to include
            String sSearch = params.sSearch ? params.sSearch : null
            if (sSearch) {
                sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
            }
            String sortColumn = CommonUtils.getSortColumn(sortColumns,iSortingCol)
            List dataReturns = new ArrayList()
            def c = SmsBilling.createCriteria()
            def results = c.list(max: iDisplayLength, offset: iDisplayStart) {
                and {
                    eq("activeStatus", ActiveStatus.ACTIVE)
                }
                if (sSearch) {
                    or {
                        ilike('message', sSearch)
                    }
                }
                order(sortColumn, sSortDir)
            }
            int totalCount = results.totalCount
//            int serial = iDisplayStart;
            Integer totalSms
            if (totalCount > 0) {
                /*if (sSortDir.equals(CommonUtils.SORT_ORDER_DESC)) {
                    serial = (totalCount + 1) - iDisplayStart
                }*/
                results.each { SmsBilling smsBilling ->
                    /*if (sSortDir.equals(CommonUtils.SORT_ORDER_ASC)) {
                        serial++
                    } else {
                        serial--
                    }*/
                    if (smsBilling.smsUseType == SmsUseType.SEND) {
                        totalSms = smsBilling.numOfSender * smsBilling.smsLength
                    } else {
                        totalSms = smsBilling.numOfSms
                    }
                    dataReturns.add([DT_RowId: smsBilling.id, 0: CommonUtils.getUiDateTimeStr(smsBilling.billingDate), 1:smsBilling.smsUseType?.value, 2: smsBilling.message, 3:smsBilling.numOfSender,4:totalSms, 5:smsBilling.smsBalance,6:CommonUtils.getUiDateStr(smsBilling.expireDate), 7: smsBilling.createdBy])
                }
            }
            return [totalCount:totalCount,results:dataReturns]
        }

    static final String[] sortDraftColumns = ['id','name','message','numOfSms']
    LinkedHashMap draftPaginateList(GrailsParameterMap params){
        int iDisplayStart = params.iDisplayStart ? params.getInt('iDisplayStart') : CommonUtils.DEFAULT_PAGINATION_START
        int iDisplayLength = params.iDisplayLength ? params.getInt('iDisplayLength') : CommonUtils.DEFAULT_PAGINATION_LENGTH
        String sSortDir = params.sSortDir_0 ? params.sSortDir_0 : CommonUtils.SORT_ORDER_DESC
        int iSortingCol = params.iSortCol_0 ? params.getInt('iSortCol_0') : CommonUtils.DEFAULT_PAGINATION_SORT_IDX
        //Search string, use or logic to all fields that required to include
        String sSearch = params.sSearch ? params.sSearch : null
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }
        String sortColumn = CommonUtils.getSortColumn(sortDraftColumns,iSortingCol)
        List dataReturns = new ArrayList()
        def c = SmsDraft.createCriteria()
        def results = c.list(max: iDisplayLength, offset: iDisplayStart) {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
            }
            if (sSearch) {
                or {
                    ilike('name', sSearch)
                    ilike('message', sSearch)
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
            results.each { SmsDraft draft ->
                if (sSortDir.equals(CommonUtils.SORT_ORDER_ASC)) {
                    serial++
                } else {
                    serial--
                }
                dataReturns.add([DT_RowId: draft.id, 0: serial, 1: draft.name, 2: draft.message, 3: draft.numOfSms, 4:''])
            }
        }
        return [totalCount:totalCount,results:dataReturns]
    }



    def studentList(GrailsParameterMap params, List checkedList, SelectionTypes types) {
        List dataReturns = new ArrayList()
        List<Long> longList

        if (types == SelectionTypes.BY_SHIFT) {
            longList = parseLongShiftToClass(checkedList)
        } else if (types == SelectionTypes.BY_SECTION) {
            longList = parseLong(checkedList)
        } else if (types == SelectionTypes.BY_CLASS) {
            longList = parseLong(checkedList)
        } else if (types == SelectionTypes.BY_STUDENT) {
            longList = parseLong(checkedList)
        }

        def c = Student.createCriteria()
        def results = c.list() {
            createAlias('section', 'se')
            createAlias('className', 'cn')
            and {
                eq("studentStatus", StudentStatus.NEW)
                if (types == SelectionTypes.BY_SHIFT) {
                    'in'("se.id", longList)
                } else if (types == SelectionTypes.BY_SECTION) {
                    'in'("se.id", longList)
                } else if (types == SelectionTypes.BY_CLASS) {
                    'in'("cn.id", longList)
                } else if (types == SelectionTypes.BY_STUDENT) {
                    'in'("id", longList)
                }
            }

        }
        Registration registration
        results.each { Student student ->
            registration = student.registration
            dataReturns.add([id: student.id, name: student.studentID + ' - ' + student.name, mobile: registration.mobile])
        }
        return dataReturns
    }

    def studentListFromIDS(String ids) {
        List dataReturns = new ArrayList()
        def c = Student.createCriteria()
//        List longList=ids.split(',') as List<Long>
        List idsList=ids.split(',')
        List longList = parseLong(idsList)
        def results = c.list() {
            and {
                    'in'("id", longList)
            }

        }
        Registration registration
        results.each { Student student ->
            registration = student.registration
            dataReturns.add([id: student.id, name: student.studentID + ' - ' + student.name, mobile: registration.mobile])
        }
        return dataReturns
    }

    List<Long> parseLong(List list) {
        List<Long> longs = new ArrayList<Long>();
        Long id
        try {
            list.each { ids ->
                id = Long.parseLong(ids)
                longs.add(id)
            }
        } catch (Exception e) {
            print(e)
        }
        return longs
    }

    List<Long> parseLongShiftToClass(List shiftKeyList) {
        List<Long> longList = new ArrayList<Long>()
        long id
        Boolean dayShift = false
        Boolean morningShift = false
        shiftKeyList.each { shift ->
            if (shift == Shift.MORNING.key) {
                morningShift = true
                print('morningShift')
            } else if (shift == Shift.DAY.key) {
                dayShift = true
                print('dayShift')
            }
        }
        List dataReturns = new ArrayList()
        def c = Section.createCriteria()
        def results = c.list() {
            and {
                or {
                    if (dayShift)
                        eq("shift", Shift.DAY)
                    if (morningShift)
                        eq("shift", Shift.MORNING)
                }
            }

        }
        results.each { Section section ->
            longList.add(section.id)
        }
        return longList
    }
    LinkedHashMap sendMessages(String messageText, String mobileNos, Integer numOfSender){
        //First check if SMS feature activated
        SmsBilling lastBilling = SmsBilling.last()
        if(!lastBilling) {
            return [isError: Boolean.TRUE, message: "SMS feature yet not activated. Please contact with Admin"]
        }
        Integer smsBalance = lastBilling.smsBalance
        if(smsBalance == 0) {
            return [isError: Boolean.TRUE, message: "No available balance to send SMS. Please contact with Admin to purchase SMS"]
        }

        if(lastBilling.expireDate.before(new Date().clearTime())) {
            return [isError: Boolean.TRUE, message: "SMS balance expired. Please contact with Admin to purchased SMS"]
        }
        Integer messageLength = smsLength(messageText)
        Integer totalSms = messageLength * numOfSender

        if(totalSms > smsBalance) {
            return [isError: Boolean.TRUE, message: "You have only ${smsBalance} SMS in your balance. Please contact Admin to purchase more SMS"]
        }

        def response = powerSmsService.sendSMSPost(messageText, mobileNos)

        if(response.isError){
            return [isError: Boolean.TRUE, message: response.errorMessage]
        }
        String insertedSmsIds = response.insertedSmsIds
        Integer currBalance = smsBalance - totalSms
        new SmsBilling(insertedSmsIds: insertedSmsIds, smsLength: messageLength, message:messageText,
                numOfSender: numOfSender, smsUseType: SmsUseType.SEND, billingDate: new Date(),
                numOfSms:totalSms, smsBalance: currBalance, smsPrice: smsPrice(totalSms), expireDate: lastBilling.expireDate).save()

        return [isError: Boolean.FALSE, message: "Message Sent Successfully"]
    }

    LinkedHashMap checkIfSmsSendable(Integer totalSms){
        //First check if SMS feature activated
        SmsBilling lastBilling = SmsBilling.last()
        if(!lastBilling) {
            return [isError: Boolean.TRUE, message: "SMS feature yet not activated. Please contact with Admin"]
        }
        Integer smsBalance = lastBilling.smsBalance
        if(smsBalance == 0) {
            return [isError: Boolean.TRUE, message: "No available balance to send SMS. Please contact with Admin to purchase SMS"]
        }

        if(lastBilling.expireDate.before(new Date().clearTime())) {
            return [isError: Boolean.TRUE, message: "SMS balance expired. Please contact with Admin to purchased SMS"]
        }

        if(totalSms > smsBalance) {
            return [isError: Boolean.TRUE, message: "You have only ${smsBalance} SMS in your balance. Please contact Admin to purchase more SMS"]
        }
        return [isError: Boolean.FALSE]
    }

    LinkedHashMap sendResultMessages(String messageText, String mobileNos){
        def response = powerSmsService.sendSMSPost(messageText, mobileNos)
        if(response.isError){
            return [isError: Boolean.TRUE, message: response.errorMessage]
        }
        return [isError: Boolean.FALSE]
    }

    int smsLength(String message){
        if (!message) return 0
        int charLength = message.length()
        Integer smsLength = 0
        if (isAlphaNumeric(message)) {
            smsLength = charLength/160
        } else {
            smsLength = charLength/70
        }
        return smsLength+1
    }
    public boolean isAlphaNumeric(String s){
        return Pattern.matches(REGEX_PATTERN, s);
    }

    Double smsPrice(Integer numOfSms){
        Double perSmsPrice = 0.45
        return perSmsPrice * numOfSms
    }
    private static final String REGEX_PATTERN = "[\\p{ASCII}]+";
}