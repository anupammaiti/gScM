package com.grailslab.account

import com.grailslab.CommonUtils
import com.grailslab.accounts.GlDetails
import com.grailslab.accounts.GlHeader
import com.grailslab.accounts.GlabPayment
import com.grailslab.accounts.Glmst
import com.grailslab.accounts.Glsub
import com.grailslab.accounts.TransactionCode

import com.grailslab.gschoolcore.ActiveStatus
import grails.converters.JSON
import org.json.JSONObject

class AccountsController {

    def employeeService
    def springSecurityService
    def accountsService
    def messageSource

    def index() {
        render (view: '/common/dashboard', layout:'moduleAccountsLayout')
    }
    def chart() {
        def glmstList = accountsService.glmstList()
        render (view: '/accounts/chartOfAccounts', model: [glmstList: glmstList])
    }

    def saveChart() {
        JSONObject chartOfAccount = JSON.parse(request.JSON.toString())

        def responseData
        String xacctype = chartOfAccount.typeSelected
        String xmsttype = "Revenue"
        if (xacctype == 'Asset' || xacctype == 'Liability'){
            xmsttype = "Balance Sheet"
        }
        Glmst glmst
        Glmst savedGlmst
        int isExist
        if(chartOfAccount.accountId == 0) {
            isExist = Glmst.countByXaccAndActiveStatus(chartOfAccount.accountNumber, ActiveStatus.ACTIVE)
            if (isExist > 0) {
                responseData = ['hasError': true, 'errorMsg': 'Account No already exist']
                render responseData as JSON
                return
            }
            glmst = new Glmst(xacc: chartOfAccount.accountNumber, xdesc: chartOfAccount.accountDescription, xmsttype: xmsttype, xacctype:xacctype, xaccusage: chartOfAccount.usageSelected, xaccsource: chartOfAccount.sourceSelected)
            savedGlmst = glmst.save()
            responseData = ['hasError': false, 'errorMsg': '', accountId: savedGlmst.id]
        } else {
            Long accountId = Long.valueOf(chartOfAccount.accountId)
            isExist = Glmst.countByXaccAndActiveStatusAndIdNotEqual(chartOfAccount.accountNumber, ActiveStatus.ACTIVE, accountId)
            if (isExist > 0) {
                responseData = ['hasError': true, 'errorMsg': 'Account No already exist!']
                render responseData as JSON
                return
            }
            glmst = Glmst.get(accountId)
            glmst.xacc = chartOfAccount.accountNumber
            glmst.xdesc = chartOfAccount.accountDescription
            glmst.xacctype = xacctype
            glmst.xmsttype = xmsttype
            glmst.xaccusage = chartOfAccount.usageSelected
            glmst.xaccsource = chartOfAccount.sourceSelected
            savedGlmst = glmst.save()
            responseData = ['hasError': false, 'errorMsg': '', accountId: savedGlmst.id]
        }
        render responseData as JSON
    }

    def deleteChart(){
        JSONObject accountDeleted = JSON.parse(request.JSON.toString())
        Glmst glmst = Glmst.get(Long.valueOf(accountDeleted.accountId))
        glmst.activeStatus=ActiveStatus.DELETE
        glmst.save()
        def responseData = ['hasError': false, 'errorMsg': '', successMsg:"Account deleted successfully"]
        render responseData as JSON
    }

    def deleteSubAccount(){
        JSONObject subAccountDeleted = JSON.parse(request.JSON.toString())
        Long subAccountId = Long.valueOf(subAccountDeleted.subAccountId)
        Glsub glsub = Glsub.get(subAccountId)
        glsub.activeStatus = ActiveStatus.DELETE
        glsub.save()
        def responseData = ['hasError': false, 'errorMsg': '', successMsg:"sub account deleted successfully"]
        render responseData as JSON
    }

    def saveSubAccount() {
        JSONObject saveSub = JSON.parse(request.JSON.toString())
        Glsub glsub
        Glsub savedAccount
        def responseData
        int isExist
        if(saveSub.subAccountId == 0) {
            isExist = Glsub.countByXsubAndActiveStatus(saveSub.xsub, ActiveStatus.ACTIVE)
            if (isExist > 0){
                responseData = [hasError: true, errorMsg: 'Account No already exist!']
                render responseData as JSON
                return
            }
            glsub = new Glsub(xsub: saveSub.xsub, xdesc: saveSub.xdesc, xaccId: saveSub.xaccId)
            savedAccount = glsub.save()
            responseData = ['hasError': false, 'errorMsg': '', subAccountId: savedAccount.id]
        }else {
            Long subAccountId = Long.valueOf(saveSub.subAccountId)
            isExist = Glsub.countByXsubAndActiveStatusAndIdNotEqual(saveSub.xsub, ActiveStatus.ACTIVE, subAccountId)
            if (isExist > 0){
                responseData = [hasError: true, errorMsg: 'Account No already exist!']
                render responseData as JSON
                return
            }
            glsub = Glsub.get(saveSub.subAccountId)
            glsub.xsub = saveSub.xsub
            glsub.xdesc = saveSub.xdesc
            glsub.xaccId = saveSub.xaccId
            savedAccount = glsub.save()
            responseData = ['hasError': false, 'errorMsg': '', subAccountId: savedAccount.id]
        }
        render responseData as JSON
    }

    def getAccountList(){
        JSONObject subAccountList = JSON.parse(request.JSON.toString())
        def glsubList = Glsub.findAllByXaccIdAndActiveStatus(subAccountList.mainAccountId, ActiveStatus.ACTIVE)
        def jsonGlSubList = new ArrayList<Map>()
        for(Glsub glsub : glsubList) {
            jsonGlSubList.add([subAccountId: glsub.id, xsub: glsub.xsub, xdesc: glsub.xdesc, xaccId: glsub.xaccId])
        }
        render jsonGlSubList as JSON
    }

    def voucher (){
        render (view: '/accounts/voucher')
    }
    def voucherList() {
        LinkedHashMap gridData
        String result
        LinkedHashMap resultMap = accountsService.voucherPaginateList(params)

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
    def saveVoucher() {
        JSONObject json = JSON.parse(request.JSON.toString())
        def responseData = accountsService.saveVoucher(json)
        render responseData as JSON
    }
    def voucherEdit(Long id) {
        if (!request.method.equals('GET')) {
            redirect(action: 'voucher')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        GlHeader glHeader = GlHeader.read(id)
        if (!glHeader) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.OBJ, glHeader)
        outPut = result as JSON
        render outPut
    }
    def getSubAccountList(){
        Long accountId = params.long('accountId')
        def glsubList = Glsub.findAllByXaccIdAndActiveStatus(accountId, ActiveStatus.ACTIVE)
        def jsonGlSubList = new ArrayList<Map>()
        for(Glsub glsub : glsubList) {
            jsonGlSubList.add([subAccountId: glsub.id, xsub: glsub.xsub+' - '+glsub.xdesc])
        }
        def responseData = [jsonGlSubList: jsonGlSubList]
        render responseData as JSON
    }

   /* def createVoucher (Long id) {
        GlHeader glHeader = GlHeader.read(id)
        def glmstList = accountsService.glmstList()
        if (!glHeader) {
            render (view: '/accounts/createVoucher',  model: [glmstList: glmstList, glHeader: null, gldetailsList: null])
            return
        }
        def gldetailsList = GlDetails.findAllByXvoucherAndActiveStatus(glHeader.xvoucher, ActiveStatus.ACTIVE)
        if(!gldetailsList) {
            render (view: '/accounts/createVoucher',  model: [glmstList: glmstList, glHeader: null, gldetailsList: null])
            return
        }
        List glDetailList = new ArrayList()
        String xSubStr
        Glsub xsubObj
        for (glDetail in gldetailsList) {
            if (glDetail.xsub){
                xsubObj = Glsub.findByXsubAndActiveStatus(glDetail.xsub, ActiveStatus.ACTIVE)
                xSubStr = xsubObj? xsubObj.xsub + " - "+xsubObj.xdesc : ""
            } else {
                xSubStr = ""
            }
            glDetailList.add([id: glDetail.id, xsub:xSubStr, xpayType: glDetail.xpayType, xcheque: glDetail.xcheque, xchequeDate: glDetail.xchequeDate,
                              xremarks: glDetail.xremarks, xvoucher: glDetail.xvoucher, xacc: glDetail.xacc, xproject: glDetail.xproject,
                              xcur: glDetail.xcur, xexch: glDetail.xexch, xprime: glDetail.xprime, xbase: glDetail.xbase,
                              activeStatus: glDetail.activeStatus.value])
        }

        render (view: '/accounts/createVoucher',  model: [glmstList: glmstList, glHeader: glHeader, gldetailsList: glDetailList])
    }*/


    // this is my

    /*def createVoucher (Long id) {
        GlHeader glHeader = GlHeader.read(id)
        def glmstList = accountsService.glmstList()
        if (!glHeader) {
            render (view: '/accounts/createVoucher',  model: [glmstList: glmstList, glHeader: null, gldetailsList: null])
            return
        }
        def gldetailsItemList = GlDetails.findAllByXvoucherAndActiveStatus(glHeader.xvoucher, ActiveStatus.ACTIVE)

        if(!gldetailsItemList) {
            render (view: '/accounts/createVoucher',  model: [glmstList: glmstList, glHeader: null, gldetailsList: null])
            return
        }
        List glDetailList = new ArrayList()
        String xSubStr
        Glsub xsubObj
        for (glDetail in gldetailsItemList) {
            if (glDetail.xsub){
                xsubObj = Glsub.findByXsubAndActiveStatus(glDetail.xsub, ActiveStatus.ACTIVE)
                xSubStr = xsubObj? xsubObj.xsub + " - "+xsubObj.xdesc : ""
            } else {
                xSubStr = ""
            }
            glDetailList.add([xsub:glDetail.xsub, xpayType: glDetail.xpayType, xcheque: glDetail.xcheque, xchequeDate: glDetail.xchequeDate,
                              xremarks: glDetail.xremarks, xvoucher: glDetail.xvoucher, xacc: glDetail.xacc, xproject: glDetail.xproject,
                              xcur: glDetail.xcur, xexch: glDetail.xexch, xprime: glDetail.xprime, xbase: glDetail.xbase,
                              activeStatus: glDetail.activeStatus.value])
        }
        render (view: '/accounts/createVoucher',  model: [glmstList: glmstList, glHeader: glHeader, gldetailsList: glDetailList])
    }*/


    def createVoucher (Long id) {
        GlHeader glHeader = GlHeader.read(id)
        def glmstList = accountsService.glmstList()
        if (!glHeader) {
            render (view: '/accounts/createVoucher',  model: [glmstList: glmstList, glHeader: null, gldetailsList: null])
            return
        }
        def gldetailsList = GlDetails.findAllByXvoucherAndActiveStatus(glHeader.xvoucher, ActiveStatus.ACTIVE)
        if(!gldetailsList) {
            render (view: '/accounts/createVoucher',  model: [glmstList: glmstList, glHeader: null, gldetailsList: null])
            return
        }
        render (view: '/accounts/createVoucher',  model: [glmstList: glmstList, glHeader: glHeader, gldetailsList: gldetailsList])
    }

    def transaction (){
        render(view: '/accounts/transaction')
    }

    def transactionSave(){
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        TransactionCode transactionCode
        Double startForm = params.getDouble("startForm")
        if (params.id){
            transactionCode = TransactionCode.get(params.long('id'))
            transactionCode.name = params.transactionName
            transactionCode.type = params.transactionType
            transactionCode.startForm = startForm
         }else {
            transactionCode = new TransactionCode(name: params.transactionName, type: params.transactionType, startForm:startForm)
        }
         if(transactionCode.save(flush: true)){
              result.put("message", "Save Successfully")
              outPut = result as JSON
              render outPut
              return
          }
            result.put("message", "Save fail")
            outPut = result as JSON
            render outPut

    }

    def list() {
        LinkedHashMap gridData
        String result
        LinkedHashMap resultMap = accountsService.transactionList(params)

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

    def editTransaction (Long id) {
        println params
        String output
        LinkedHashMap result = new LinkedHashMap()
        result.put('isError', false)
        TransactionCode transactionCode = TransactionCode.read(id)
        result.put('obj', transactionCode)
        output = result as JSON
        render output

    }

    def deleteTransaction (Long id) {
        LinkedHashMap result = new LinkedHashMap()
        String output
        TransactionCode transactionCode = TransactionCode.get(id)

        if(transactionCode.activeStatus.equals(ActiveStatus.INACTIVE)){
            transactionCode.activeStatus=ActiveStatus.ACTIVE
        } else {
            transactionCode.activeStatus=ActiveStatus.INACTIVE
        }
        transactionCode.save()
        result.put(CommonUtils.MESSAGE,"Transaction deleted successfully.")
        output = result as JSON
        render output
    }


}
