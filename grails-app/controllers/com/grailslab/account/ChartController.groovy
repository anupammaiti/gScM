package com.grailslab.account

import com.grailslab.CommonUtils
import com.grailslab.accounting.*
import com.grailslab.command.CafeteriaCommand
import com.grailslab.command.ChartOfAccountsCommand
import com.grailslab.command.FeesCommand
import com.grailslab.command.StationaryItemCommand
import com.grailslab.enums.*
import com.grailslab.gschoolcore.ActiveStatus
import grails.converters.JSON
import org.springframework.dao.DataIntegrityViolationException

class ChartController {

    def chartOfAccountService
    def classNameService
    def messageSource
    def schoolService
    def feeItemsService

    def fees() {
        LinkedHashMap resultMap = chartOfAccountService.listAllItems()
        render(view: '/collection/chart/fees', model: [dataReturn: resultMap.results])
    }

    def expense() {
        LinkedHashMap resultMap = chartOfAccountService.list(AccountType.EXPENSE)
        render(view: '/collection/chart/expense', model: [dataReturn: resultMap.results])
    }

    def asset() {
        LinkedHashMap resultMap = chartOfAccountService.list(AccountType.ASSET)
        render(view: '/collection/chart/asset', model: [dataReturn: resultMap.results])
    }

    def saveAccount(ChartOfAccountsCommand command) {
        if (!request.method.equals('POST')) {
            redirect(action: 'fees')
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
        ChartOfAccount account
        String message
        ChartOfAccount ifExistAccount
        if (command.id) {
            account = ChartOfAccount.get(command.id)
            if (!account) {
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
                outPut = result as JSON
                render outPut
                return
            }
            ifExistAccount = ChartOfAccount.findByCodeAndActiveStatusAndIdNotEqual(command.code, ActiveStatus.ACTIVE, command.id)
            if (ifExistAccount) {
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, 'Chart Of Account Code Already Exist')
                outPut = result as JSON
                render outPut
                return
            }
            account.properties['code', 'name', 'appliedType','scholarshipHead'] = command.properties
            if (account.nodeType == NodeType.FIRST && command.allowSubHead) {
                account.allowChild = true
                account.displayHead = false
            } else {
                account.allowChild = false
                account.displayHead = true
            }
            result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
            message = 'Account Head Updated successfully'
        } else {
            ifExistAccount = ChartOfAccount.findByCodeAndActiveStatus(command.code, ActiveStatus.ACTIVE)
            if (ifExistAccount) {
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, 'Chart Of Account Code Already Exist')
                outPut = result as JSON
                render outPut
                return
            }
            ChartOfAccount paAccount

            if (command.parentId) {
                paAccount = ChartOfAccount.get(command.parentId)
            } else {
                paAccount = ChartOfAccount.findByAccountTypeAndNodeType(command.accountType, NodeType.ROOT)
            }
            if (!paAccount) {
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, 'Parent Account Not Found.')
                outPut = result as JSON
                render outPut
                return
            } else {
                if (!paAccount.hasChild) {
                    paAccount.hasChild = true
                }
            }
            NodeType childNode
            Boolean allowChild = true
            Boolean displayHead = false
            AccountType accountType = command.accountType


            if (paAccount.nodeType == NodeType.ROOT) {
                childNode = NodeType.FIRST
            } else {
                childNode = NodeType.SECOND
                accountType = paAccount.accountType
            }
            if (paAccount.nodeType == NodeType.ROOT && command.allowSubHead) {
                allowChild = true
                displayHead = false
            } else {
                allowChild = false
                displayHead = true
            }

            account = new ChartOfAccount(command.properties)
            account.nodeType = childNode
            account.allowChild = allowChild
            account.parentId = paAccount.id
            account.displayHead = displayHead
            account.accountType = accountType

            result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
            message = 'Account head Added successfully'
        }

        if (account.hasErrors() || !account.save()) {
            def errorList = account?.errors?.allErrors?.collect { messageSource.getMessage(it, null) }
            message = errorList?.join('\n')
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
        }
        result.put(CommonUtils.MESSAGE, message)
        outPut = result as JSON
        render outPut
    }

    def saveExpense(ChartOfAccountsCommand command) {
        if (!request.method.equals('POST')) {
            redirect(action: 'fees')
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
        ChartOfAccount account
        String message
        ChartOfAccount ifExistAccount
        if (command.id) {
            account = ChartOfAccount.get(command.id)
            if (!account) {
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
                outPut = result as JSON
                render outPut
                return
            }
            ifExistAccount = ChartOfAccount.findByCodeAndIdNotEqual(command.code, command.id)
            if (ifExistAccount) {
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, 'Chart Of Account Code Already Exist')
                outPut = result as JSON
                render outPut
                return
            }
            account.properties['code', 'name', 'appliedType'] = command.properties
                if (account.nodeType == NodeType.FIRST && command.allowSubHead) {
                    account.allowChild = true
                    account.displayHead = false
                } else {
                    account.allowChild = false
                    account.displayHead = true
                }
            result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
            message = 'Account Head Updated successfully'
        } else {

            ifExistAccount = ChartOfAccount.findByCode(command.code)
            if (ifExistAccount) {
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, 'Chart Of Account Code Already Exist')
                outPut = result as JSON
                render outPut
                return
            }
            ChartOfAccount paAccount

            if (command.parentId) {
                paAccount = ChartOfAccount.get(command.parentId)
            } else {
                paAccount = ChartOfAccount.findByAccountTypeAndNodeType(command.accountType, NodeType.ROOT)
            }
            if (!paAccount) {
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, 'Parent Account Not Found.')
                outPut = result as JSON
                render outPut
                return
            } else {
                if (!paAccount.hasChild) {
                    paAccount.hasChild = true
                }
            }
            NodeType childNode
            Boolean allowChild = true
            Boolean displayHead = false

            if (paAccount.nodeType == NodeType.ROOT) {
                childNode = NodeType.FIRST
            } else if (paAccount.nodeType == NodeType.FIRST) {
                childNode = NodeType.SECOND
                allowChild = false
            }
                if (paAccount.nodeType == NodeType.ROOT && command.allowSubHead) {
                    allowChild = true
                    displayHead = false
                } else {
                    allowChild = false
                    displayHead = true
                }
            account = new ChartOfAccount(command.properties)
            account.nodeType = childNode
            account.allowChild = allowChild
            account.parentId = paAccount.id
            account.displayHead = displayHead


            result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
            message = 'Account head Added successfully'
        }

        if (account.hasErrors() || !account.save()) {
            def errorList = account?.errors?.allErrors?.collect { messageSource.getMessage(it, null) }
            message = errorList?.join('\n')
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
        }
        result.put(CommonUtils.MESSAGE, message)
        outPut = result as JSON
        render outPut
    }


    def edit(Long id) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        ChartOfAccount account = ChartOfAccount.read(id)
        if (!account) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put('obj', account)
        outPut = result as JSON
        render outPut
    }

    def delete(Long id) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        ChartOfAccount account = ChartOfAccount.get(id)
        if (!account) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }

        ChartOfAccount parentAccount = ChartOfAccount.get(account.parentId)
        if (parentAccount) {
            def childAccount = ChartOfAccount.findByParentIdAndIdNotEqual(parentAccount.id, account.id)
            if (!childAccount) {
                parentAccount.hasChild = Boolean.FALSE
                parentAccount.save()
            }
        }
        account.activeStatus=ActiveStatus.DELETE
        account.save()
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, 'Chart Of Account Deleted Successfully.')
        outPut = result as JSON
        render outPut
    }

    def classFees() {
        def classNameList = classNameService.classNameDropDownList(AcaYearType.ALL)
        if (!classNameList) {
            render(view: '/collection/chart/classFees', model: [dataReturn: null, totalCount: 0, classNameList: null, classFeesList: null])
            return
        }
        def firstClass = classNameList.first()
        params.put('className', firstClass?.id)
        LinkedHashMap resultMap = chartOfAccountService.feesPaginationList(params, FeeAppliedType.CLASS_STD)
        def classFeesList = chartOfAccountService.feesDropDownList(AccountType.feeMenuItems(), FeeAppliedType.CLASS_STD)
        int totalCount = resultMap.totalCount
        if (!resultMap || totalCount == 0) {
            render(view: '/collection/chart/classFees', model: [dataReturn: null, totalCount: 0, classNameList: classNameList, classFeesList: classFeesList])
            return
        }
        render(view: '/collection/chart/classFees', model: [dataReturn: resultMap.results, totalCount: totalCount, classNameList: classNameList, classFeesList: classFeesList])
    }

    def commonFees() {
        LinkedHashMap resultMap = chartOfAccountService.feesPaginationList(params, FeeAppliedType.ALL_STD)
        def commonFeesList = chartOfAccountService.feesDropDownList(AccountType.feeMenuItems(), FeeAppliedType.ALL_STD)
        int totalCount = resultMap.totalCount
        if (!resultMap || totalCount == 0) {
            render(view: '/collection/chart/commonFees', model: [dataReturn: null, totalCount: 0, commonFeesList: commonFeesList])
            return
        }
        render(view: '/collection/chart/commonFees', model: [dataReturn: resultMap.results, totalCount: totalCount, commonFeesList: commonFeesList])
    }

    def manageActivationFees(Long id) {
        Fees activationFee = Fees.read(id)
        if(!activationFee || activationFee.iterationType!=FeeIterationType.MONTHLY) {
            redirect(action: 'classFees')
            return
        }
        def resultMap = feeItemsService.activationFeesList(activationFee)
        if (!resultMap) {
            render(view: '/collection/chart/manageactivationFee', model: [dataReturn: null])
            return
        }
        render(view: '/collection/chart/manageactivationFee', model: [feeId: activationFee.id, dataReturn: resultMap])
    }

    def activeFeeStatus(Long id) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        ItemsDetail itemsDetail = ItemsDetail.get(id)
        if (!itemsDetail) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        FeeItems feeItems = itemsDetail.feeItems
        if (feeItems.feeType != FeeType.ACTIVATION) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "You can't Inactivate ${feeItems.name}.")
            outPut = result as JSON
            render outPut
            return
        }
        String message
        if(itemsDetail.activeStatus.equals(ActiveStatus.INACTIVE)){
            itemsDetail.activeStatus=ActiveStatus.ACTIVE
            message = "Item Activated Successfully"
        }else {
            itemsDetail.activeStatus=ActiveStatus.INACTIVE
            message = "Item inactive Successfully"
        }
        def allActiveItems = feeItems.itemsDetail.sort{it.sortPosition}
        int continuityOrder = 1
        allActiveItems.each {it ->
            if (it.activeStatus == ActiveStatus.ACTIVE) {
                it.continuityOrder = continuityOrder
                continuityOrder++
            }
        }
        feeItems.save(flush: true)
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, message)
        outPut = result as JSON
        render outPut
    }

    def listFees() {
        LinkedHashMap gridData
        String result

        if (!params.feeAppliedType) {
            gridData = [iTotalRecords: 0, iTotalDisplayRecords: 0, aaData: []]
            result = gridData as JSON
            render result
            return
        }
        FeeAppliedType feeAppliedType = FeeAppliedType.valueOf(params.feeAppliedType.toString())
        if (!feeAppliedType) {
            gridData = [iTotalRecords: 0, iTotalDisplayRecords: 0, aaData: []]
            result = gridData as JSON
            render result
            return
        }

        LinkedHashMap resultMap = chartOfAccountService.feesPaginationList(params, feeAppliedType)
        int totalCount = resultMap.totalCount
        if (!resultMap || totalCount == 0) {
            gridData = [iTotalRecords: 0, iTotalDisplayRecords: 0, aaData: []]
            result = gridData as JSON
            render result
            return
        }
        gridData = [iTotalRecords: totalCount, iTotalDisplayRecords: totalCount, aaData: resultMap.results]
        result = gridData as JSON
        render result
    }
    def saveFees(FeesCommand command) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
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
        Fees fees
        def ifAlreadyAdded = null
        def workingYears = schoolService.workingYears()
        if(command.id){
            if (command.feeAppliedType == FeeAppliedType.CLASS_STD) {
                if(command.feeType == FeeType.ACTIVATION) {
                    ifAlreadyAdded = Fees.findAllByAccountAndClassNameAndActiveStatusAndAcademicYearInListAndIdNotEqual(command.account, command.className, ActiveStatus.ACTIVE, workingYears, command.id)
                } else {
                    ifAlreadyAdded = Fees.findAllByAccountAndClassNameAndActiveStatusAndIdNotEqual(command.account, command.className, ActiveStatus.ACTIVE,command.id)
                }
            } else if (command.feeAppliedType == FeeAppliedType.ALL_STD) {
                if(command.feeType == FeeType.ACTIVATION) {
                    ifAlreadyAdded = Fees.findAllByAccountAndFeeAppliedTypeAndActiveStatusAndAcademicYearInListAndIdNotEqual(command.account, FeeAppliedType.ALL_STD, ActiveStatus.ACTIVE, workingYears, command.id)
                } else {
                    ifAlreadyAdded = Fees.findAllByAccountAndFeeAppliedTypeAndActiveStatusAndIdNotEqual(command.account, FeeAppliedType.ALL_STD, ActiveStatus.ACTIVE,command.id)
                }
            }
        }else {
            if (command.feeAppliedType == FeeAppliedType.CLASS_STD) {
                if(command.feeType == FeeType.ACTIVATION) {
                    ifAlreadyAdded = Fees.findAllByAccountAndClassNameAndActiveStatusAndAcademicYearInList(command.account, command.className, ActiveStatus.ACTIVE, workingYears)
                } else {
                    ifAlreadyAdded = Fees.findAllByAccountAndClassNameAndActiveStatus(command.account, command.className, ActiveStatus.ACTIVE)
                }
            } else if (command.feeAppliedType == FeeAppliedType.ALL_STD) {
                if(command.feeType == FeeType.ACTIVATION) {
                    ifAlreadyAdded = Fees.findAllByAccountAndFeeAppliedTypeAndActiveStatusAndAcademicYearInList(command.account, FeeAppliedType.ALL_STD, ActiveStatus.ACTIVE, workingYears)
                } else {
                    ifAlreadyAdded = Fees.findAllByAccountAndFeeAppliedTypeAndActiveStatus(command.account, FeeAppliedType.ALL_STD, ActiveStatus.ACTIVE)
                }

            }
        }

        if (ifAlreadyAdded) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "${command.account.name} already added to ${command.className.name}. You can edit or delete only.")
            outPut = result as JSON
            render outPut
            return
        }

        ChartOfAccount chartOfAccount = command.account
        if (chartOfAccount.accountType == AccountType.INCOME) {
            //fee Items
        } else {
            //stationary items
        }

        String message
        Double discountAmount = 0
        if (command.discount > 0 && command.discount <= 100) {
            discountAmount = command.amount * command.discount * 0.01
        }

        FeeItems feeItems
        ItemsDetail feeDetailItems
        ChartOfAccount account = command.account
        String accName
        ChartOfAccount pAccount

        def errorList
        if (command.id){
            fees=Fees.get(command.id)
            if (!fees){
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
                outPut = result as JSON
                render outPut
                return
            }

            if(fees.iterationType != command.iterationType){
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, "Can't change Iteration Type ${fees.iterationType.value} to ${command.iterationType.value}.")
                outPut = result as JSON
                render outPut
                return
            }

            fees.properties=command.properties
            fees.netPayable = command.amount - discountAmount.round(2)
            if (fees.hasErrors() || !fees.save()){
                errorList = command?.errors?.allErrors?.collect { messageSource.getMessage(it, null) }
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, errorList?.join('\n'))
                outPut = result as JSON
                render outPut
                return
            }
            try {
                //applied for class studentd start
                FeeItems oldItem = FeeItems.findByFees(fees)
                accName = "${account?.name}"
                if(account.nodeType==NodeType.SECOND){
                    pAccount = ChartOfAccount.read(account.parentId)
                    accName = "${pAccount?.name} - ${account.name}"
                }
                if ((command.iterationType == FeeIterationType.RECEIVE) || (command.iterationType == FeeIterationType.YEARLY)) {
                    oldItem.chartOfAccount = account
                    oldItem.name = accName
                    oldItem.code = "${account?.code}"
                    oldItem.amount = fees.amount
                    oldItem.discount =fees.discount
                    oldItem.netPayable =fees.netPayable
                    oldItem.quickFee1 = fees.quickFee1
                    oldItem.quickFee2 = fees.quickFee2
                    oldItem.className = fees.className
                    oldItem.feeType = fees.feeType
                    oldItem.feeAppliedType = fees.feeAppliedType
                    oldItem.dueOnType = fees.dueOnType
                    oldItem.iterationType = fees.iterationType
                } else if (command.iterationType == FeeIterationType.MONTHLY) {
                    oldItem.chartOfAccount = account
                    oldItem.name = accName
                    oldItem.code = "${account?.code}"
                    oldItem.amount = fees.amount
                    oldItem.discount =fees.discount
                    oldItem.netPayable =fees.netPayable
                    oldItem.quickFee1 = fees.quickFee1
                    oldItem.quickFee2 = fees.quickFee2
                    oldItem.className = fees.className
                    oldItem.feeType = fees.feeType
                    oldItem.feeAppliedType = fees.feeAppliedType
                    oldItem.dueOnType = fees.dueOnType
                    oldItem.iterationType = fees.iterationType

                    oldItem.itemsDetail.each {ItemsDetail detail ->
                        detail.amount=fees.amount
                        detail.discount = fees.discount
                        detail.netPayable = fees.netPayable
                        detail.quickFee1 = fees.quickFee1
                        detail.quickFee2 = fees.quickFee2
                        detail.save()
                    }

                } else if (command.iterationType == FeeIterationType.DAILY) {
                    oldItem.chartOfAccount = account
                    oldItem.name = accName
                    oldItem.code = "${account?.code}"
                    oldItem.amount = fees.amount
                    oldItem.discount =fees.discount
                    oldItem.netPayable =fees.netPayable
                    oldItem.quickFee1 = fees.quickFee1
                    oldItem.quickFee2 = fees.quickFee2
                    oldItem.className = fees.className
                    oldItem.feeType = fees.feeType
                    oldItem.feeAppliedType = fees.feeAppliedType
                    oldItem.dueOnType = fees.dueOnType
                    oldItem.iterationType = fees.iterationType

                    oldItem.itemsDetail.each {ItemsDetail detail ->
                        detail.amount=fees.amount
                        detail.discount = fees.discount
                        detail.netPayable = fees.netPayable
                        detail.quickFee1 = fees.quickFee1
                        detail.quickFee2 = fees.quickFee2
                        detail.save()
                    }
                }
                if (oldItem.hasErrors() || !oldItem.save()) {
                    errorList = feeItems?.errors?.allErrors?.collect { messageSource.getMessage(it, null) }
                    message = errorList?.join('\n')
                    result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                    result.put(CommonUtils.MESSAGE, message)
                    outPut = result as JSON
                    render outPut
                    return
                }
            }catch (DataIntegrityViolationException e){
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, e.getMessage())
                outPut = result as JSON
                render outPut
                return
            }
            message ='Class Fees Updated successfully'
        }else {
            fees = new Fees(command.properties)
            message ='Class Fees Create successfully'
            fees.netPayable = command.amount - discountAmount.round(2)

            if (fees.hasErrors() || !fees.save()) {
                errorList = fees?.errors?.allErrors?.collect { messageSource.getMessage(it, null) }
                message = errorList?.join('\n')
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, message)
                outPut = result as JSON
                render outPut
                return
            }

            //applied for class studentd start
            accName = "${account?.name}"
            if(account.nodeType==NodeType.SECOND){
                pAccount = ChartOfAccount.read(account.parentId)
                accName = "${pAccount?.name} - ${account.name}"
            }
            int itemSortPosition =1
            if ((command.iterationType == FeeIterationType.RECEIVE) || (command.iterationType == FeeIterationType.YEARLY)) {
//                if(command.feeType!=FeeType.ACTIVATION){
                    feeItems = new FeeItems(fees: fees, chartOfAccount: account, name: accName, code: "${account.code}",
                            amount: fees.amount, discount: fees.discount, netPayable: fees.netPayable,
                            quickFee1: fees.quickFee1, quickFee2: fees.quickFee2,
                            feeItemType:FeeItemType.FEE, className:fees.className, feeType:fees.feeType,
                            feeAppliedType:fees.feeAppliedType, dueOnType:fees.dueOnType, iterationType:fees.iterationType)
//                }
                //Not implement else case as for now

            } else if (command.iterationType == FeeIterationType.MONTHLY) {
                feeItems = new FeeItems(fees: fees, chartOfAccount: account, name: accName, code: "${account.code}", amount: fees.amount,
                        discount: fees.discount, netPayable: fees.netPayable, quickFee1: fees.quickFee1, quickFee2: fees.quickFee2,
                        feeItemType:FeeItemType.FEE, className:fees.className, feeType:fees.feeType,
                        feeAppliedType:fees.feeAppliedType, dueOnType:fees.dueOnType, iterationType:fees.iterationType)
                if(account.appliedType==FeeAppliedType.CLASS_STD){
                    if(fees.className.isCollegeSection) {
                        YearMonths.collegeMonths().each { monthValue ->
                            feeDetailItems = new ItemsDetail(name:"${monthValue.value}",amount: fees.amount, discount: fees.discount, netPayable: fees.netPayable, quickFee1: fees.quickFee1, quickFee2: fees.quickFee2, sortPosition:itemSortPosition, continuityOrder:itemSortPosition)
                            feeItems.addToItemsDetail(feeDetailItems)
                            itemSortPosition++
                        }
                    } else {
                        YearMonths.values().each { monthValue ->
                            feeDetailItems = new ItemsDetail(name:"${monthValue.value}",amount: fees.amount, discount: fees.discount, netPayable: fees.netPayable, quickFee1: fees.quickFee1, quickFee2: fees.quickFee2, sortPosition:itemSortPosition, continuityOrder:CommonUtils.monthOrder(monthValue))
                            feeItems.addToItemsDetail(feeDetailItems)
                            itemSortPosition++
                        }
                    }
                }else {
                    YearMonths.values().each { monthValue ->
                        feeDetailItems = new ItemsDetail(name:"${monthValue.value}",amount: fees.amount, discount: fees.discount, netPayable: fees.netPayable, quickFee1: fees.quickFee1, quickFee2: fees.quickFee2, sortPosition:itemSortPosition, continuityOrder:CommonUtils.monthOrder(monthValue))
                        feeItems.addToItemsDetail(feeDetailItems)
                        itemSortPosition++
                    }
                }

            } else if (command.iterationType == FeeIterationType.DAILY) {
//                if(command.feeType!=FeeType.ACTIVATION){
                    feeItems = new FeeItems(fees: fees, chartOfAccount: account, name: accName, code: "${account.code}", amount: fees.amount,
                            discount: fees.discount, netPayable: fees.netPayable,
                            quickFee1: fees.quickFee1, quickFee2: fees.quickFee2,
                            feeItemType:FeeItemType.FEE, className:fees.className, feeType:fees.feeType,
                            feeAppliedType:fees.feeAppliedType, dueOnType:fees.dueOnType, iterationType:fees.iterationType)
                    WeekDays.values().each { monthValue ->
                        feeDetailItems = new ItemsDetail(name:"${monthValue.value}",amount: fees.amount, discount: fees.discount, netPayable: fees.netPayable, quickFee1: fees.quickFee1, quickFee2: fees.quickFee2, sortPosition:itemSortPosition, continuityOrder:dayOrder(monthValue))
                        feeItems.addToItemsDetail(feeDetailItems)
                        itemSortPosition++
                    }
//                }
            }
            //applied for class studentd end
            if (feeItems.hasErrors() || !feeItems.save()) {
                errorList = feeItems?.errors?.allErrors?.collect { messageSource.getMessage(it, null) }
                message = errorList?.join('\n')
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, message)
                outPut = result as JSON
                render outPut
                return
            }
        }
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, message)
        outPut = result as JSON
        render outPut
    }
    /*def saveFees(FeesCommand command) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
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
        Fees fees
        //if already added
        def ifAlreadyAdded = null
        def workingYears = schoolService.workingYears()
        if(command.id){
            if (command.feeAppliedType == FeeAppliedType.CLASS_STD) {
                if(command.feeType==FeeType.ACTIVATION) {
                    ifAlreadyAdded = Fees.findAllByAccountAndClassNameAndActiveStatusAndAcademicYearInListAndIdNotEqual(command.account, command.className, ActiveStatus.ACTIVE, workingYears, command.id)
                } else {
                    ifAlreadyAdded = Fees.findAllByAccountAndClassNameAndActiveStatusAndIdNotEqual(command.account, command.className, ActiveStatus.ACTIVE,command.id)
                }
            } else if (command.feeAppliedType == FeeAppliedType.ALL_STD) {
                    if(command.feeType==FeeType.ACTIVATION) {
                        ifAlreadyAdded = Fees.findAllByAccountAndFeeAppliedTypeAndActiveStatusAndAcademicYearInListAndIdNotEqual(command.account, FeeAppliedType.ALL_STD, ActiveStatus.ACTIVE, workingYears, command.id)
                    } else {
                        ifAlreadyAdded = Fees.findAllByAccountAndFeeAppliedTypeAndActiveStatusAndIdNotEqual(command.account, FeeAppliedType.ALL_STD, ActiveStatus.ACTIVE,command.id)
                    }

            }
        }else {
            if (command.feeAppliedType == FeeAppliedType.CLASS_STD) {
                if(command.feeType==FeeType.ACTIVATION) {
                    ifAlreadyAdded = Fees.findAllByAccountAndClassNameAndActiveStatusAndAcademicYearInList(command.account, command.className, ActiveStatus.ACTIVE, workingYears)
                } else {
                    ifAlreadyAdded = Fees.findAllByAccountAndClassNameAndActiveStatus(command.account, command.className, ActiveStatus.ACTIVE)
                }
            } else if (command.feeAppliedType == FeeAppliedType.ALL_STD) {
                if(command.feeType==FeeType.ACTIVATION) {
                    ifAlreadyAdded = Fees.findAllByAccountAndFeeAppliedTypeAndActiveStatusAndAcademicYearInList(command.account, FeeAppliedType.ALL_STD, ActiveStatus.ACTIVE, workingYears)
                } else {
                    ifAlreadyAdded = Fees.findAllByAccountAndFeeAppliedTypeAndActiveStatus(command.account, FeeAppliedType.ALL_STD, ActiveStatus.ACTIVE)
                }

            }
        }


        if (ifAlreadyAdded) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "${command.account.name} already added to ${command.className.name}. You can edit or delete only.")
            outPut = result as JSON
            render outPut
            return
        }
        String message
        Double discountAmount = 0
        if (command.discount > 0 && command.discount <= 100) {
            discountAmount = command.amount * command.discount * 0.01
        }

        FeeItems feeItems
        ItemsDetail feeDetailItems
        ChartOfAccount account = command.account
        String accName
        ChartOfAccount pAccount

        def errorList
        if (command.id){
            fees=Fees.get(command.id)
            if (!fees){
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
                outPut = result as JSON
                render outPut
                return
            }

            if(fees.iterationType != command.iterationType){
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, "Can't change Iteration Type ${fees.iterationType.value} to ${command.iterationType.value}.")
                outPut = result as JSON
                render outPut
                return
            }

            fees.properties=command.properties
            fees.netPayable = command.amount - discountAmount.round(2)
            if (fees.hasErrors() || !fees.save()){
                errorList = command?.errors?.allErrors?.collect { messageSource.getMessage(it, null) }
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, errorList?.join('\n'))
                outPut = result as JSON
                render outPut
                return
            }
            try {
                //applied for class studentd start
                FeeItems oldItem = FeeItems.findByFees(fees)
                accName = "${account?.name}"
                if(account.nodeType==NodeType.SECOND){
                    pAccount = ChartOfAccount.read(account.parentId)
                    accName = "${pAccount?.name} - ${account.name}"
                }
                if ((command.iterationType == FeeIterationType.RECEIVE) || (command.iterationType == FeeIterationType.YEARLY)) {
                    oldItem.chartOfAccount = account
                    oldItem.name = accName
                    oldItem.code = "${account?.code}"
                    oldItem.amount = fees.amount
                    oldItem.discount =fees.discount
                    oldItem.netPayable =fees.netPayable
                    oldItem.quickFee1 = fees.quickFee1
                    oldItem.quickFee2 = fees.quickFee2
                    oldItem.className = fees.className
                    oldItem.feeType = fees.feeType
                    oldItem.feeAppliedType = fees.feeAppliedType
                    oldItem.dueOnType = fees.dueOnType
                    oldItem.iterationType = fees.iterationType
                } else if (command.iterationType == FeeIterationType.MONTHLY) {
                    oldItem.chartOfAccount = account
                    oldItem.name = accName
                    oldItem.code = "${account?.code}"
                    oldItem.amount = fees.amount
                    oldItem.discount =fees.discount
                    oldItem.netPayable =fees.netPayable
                    oldItem.quickFee1 = fees.quickFee1
                    oldItem.quickFee2 = fees.quickFee2
                    oldItem.className = fees.className
                    oldItem.feeType = fees.feeType
                    oldItem.feeAppliedType = fees.feeAppliedType
                    oldItem.dueOnType = fees.dueOnType
                    oldItem.iterationType = fees.iterationType

                    oldItem.itemsDetail.each {ItemsDetail detail ->
                        detail.amount=fees.amount
                        detail.discount = fees.discount
                        detail.netPayable = fees.netPayable
                        detail.quickFee1 = fees.quickFee1
                        detail.quickFee2 = fees.quickFee2
                        detail.save()
                    }

                } else if (command.iterationType == FeeIterationType.DAILY) {
                    oldItem.chartOfAccount = account
                    oldItem.name = accName
                    oldItem.code = "${account?.code}"
                    oldItem.amount = fees.amount
                    oldItem.discount =fees.discount
                    oldItem.netPayable =fees.netPayable
                    oldItem.quickFee1 = fees.quickFee1
                    oldItem.quickFee2 = fees.quickFee2
                    oldItem.className = fees.className
                    oldItem.feeType = fees.feeType
                    oldItem.feeAppliedType = fees.feeAppliedType
                    oldItem.dueOnType = fees.dueOnType
                    oldItem.iterationType = fees.iterationType

                    oldItem.itemsDetail.each {ItemsDetail detail ->
                        detail.amount=fees.amount
                        detail.discount = fees.discount
                        detail.netPayable = fees.netPayable
                        detail.quickFee1 = fees.quickFee1
                        detail.quickFee2 = fees.quickFee2
                        detail.save()
                    }
                }
                if (oldItem.hasErrors() || !oldItem.save()) {
                    errorList = feeItems?.errors?.allErrors?.collect { messageSource.getMessage(it, null) }
                    message = errorList?.join('\n')
                    result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                    result.put(CommonUtils.MESSAGE, message)
                    outPut = result as JSON
                    render outPut
                    return
                }
            }catch (DataIntegrityViolationException e){
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, e.getMessage())
                outPut = result as JSON
                render outPut
                return
            }
            message ='Class Fees Updated successfully'
        }else {
            fees = new Fees(command.properties)
            message ='Class Fees Create successfully'
            fees.netPayable = command.amount - discountAmount.round(2)

            if (fees.hasErrors() || !fees.save()) {
                errorList = fees?.errors?.allErrors?.collect { messageSource.getMessage(it, null) }
                message = errorList?.join('\n')
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, message)
                outPut = result as JSON
                render outPut
                return
            }

            //applied for class studentd start
            accName = "${account?.name}"
            if(account.nodeType==NodeType.SECOND){
                pAccount = ChartOfAccount.read(account.parentId)
                accName = "${pAccount?.name} - ${account.name}"
            }
            int itemSortPosition =1
            if ((command.iterationType == FeeIterationType.RECEIVE) || (command.iterationType == FeeIterationType.YEARLY)) {
//                if(command.feeType!=FeeType.ACTIVATION){
                    feeItems = new FeeItems(fees: fees, chartOfAccount: account, name: accName, code: "${account.code}",
                            amount: fees.amount, discount: fees.discount, netPayable: fees.netPayable,
                            quickFee1: fees.quickFee1, quickFee2: fees.quickFee2,
                            feeItemType:FeeItemType.FEE, className:fees.className, feeType:fees.feeType,
                            feeAppliedType:fees.feeAppliedType, dueOnType:fees.dueOnType, iterationType:fees.iterationType)
//                }
                //Not implement else case as for now

            } else if (command.iterationType == FeeIterationType.MONTHLY) {
                feeItems = new FeeItems(fees: fees, chartOfAccount: account, name: accName, code: "${account.code}", amount: fees.amount,
                        discount: fees.discount, netPayable: fees.netPayable, quickFee1: fees.quickFee1, quickFee2: fees.quickFee2,
                        feeItemType:FeeItemType.FEE, className:fees.className, feeType:fees.feeType,
                        feeAppliedType:fees.feeAppliedType, dueOnType:fees.dueOnType, iterationType:fees.iterationType)
                if(account.appliedType==FeeAppliedType.CLASS_STD){
                    if(fees.className.isCollegeSection) {
                        YearMonths.collegeMonths().each { monthValue ->
                            feeDetailItems = new ItemsDetail(name:"${monthValue.value}",amount: fees.amount, discount: fees.discount, netPayable: fees.netPayable, quickFee1: fees.quickFee1, quickFee2: fees.quickFee2, sortPosition:itemSortPosition, continuityOrder:itemSortPosition)
                            feeItems.addToItemsDetail(feeDetailItems)
                            itemSortPosition++
                        }
                    } else {
                        YearMonths.values().each { monthValue ->
                            feeDetailItems = new ItemsDetail(name:"${monthValue.value}",amount: fees.amount, discount: fees.discount, netPayable: fees.netPayable, quickFee1: fees.quickFee1, quickFee2: fees.quickFee2, sortPosition:itemSortPosition, continuityOrder:CommonUtils.monthOrder(monthValue))
                            feeItems.addToItemsDetail(feeDetailItems)
                            itemSortPosition++
                        }
                    }
                }else {
                    YearMonths.values().each { monthValue ->
                        feeDetailItems = new ItemsDetail(name:"${monthValue.value}",amount: fees.amount, discount: fees.discount, netPayable: fees.netPayable, quickFee1: fees.quickFee1, quickFee2: fees.quickFee2, sortPosition:itemSortPosition, continuityOrder:CommonUtils.monthOrder(monthValue))
                        feeItems.addToItemsDetail(feeDetailItems)
                        itemSortPosition++
                    }
                }

            } else if (command.iterationType == FeeIterationType.DAILY) {
//                if(command.feeType!=FeeType.ACTIVATION){
                    feeItems = new FeeItems(fees: fees, chartOfAccount: account, name: accName, code: "${account.code}", amount: fees.amount,
                            discount: fees.discount, netPayable: fees.netPayable,
                            quickFee1: fees.quickFee1, quickFee2: fees.quickFee2,
                            feeItemType:FeeItemType.FEE, className:fees.className, feeType:fees.feeType,
                            feeAppliedType:fees.feeAppliedType, dueOnType:fees.dueOnType, iterationType:fees.iterationType)
                    WeekDays.values().each { monthValue ->
                        feeDetailItems = new ItemsDetail(name:"${monthValue.value}",amount: fees.amount, discount: fees.discount, netPayable: fees.netPayable, quickFee1: fees.quickFee1, quickFee2: fees.quickFee2, sortPosition:itemSortPosition, continuityOrder:dayOrder(monthValue))
                        feeItems.addToItemsDetail(feeDetailItems)
                        itemSortPosition++
                    }
//                }
            }
            //applied for class studentd end
            if (feeItems.hasErrors() || !feeItems.save()) {
                errorList = feeItems?.errors?.allErrors?.collect { messageSource.getMessage(it, null) }
                message = errorList?.join('\n')
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, message)
                outPut = result as JSON
                render outPut
                return
            }
        }
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, message)
        outPut = result as JSON
        render outPut
    }*/

    def editFees(Long id){

        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        Fees fees = Fees.read(id)

        if (!fees) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.OBJ, fees)
        outPut = result as JSON
        render outPut
    }

    def deleteFees(Long id) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        Fees fees = Fees.get(id)
        if(!fees){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }

        FeeItems oldItem = FeeItems.findByFees(fees)
        if(oldItem){
            oldItem.activeStatus=ActiveStatus.DELETE
            oldItem.save()
        }
        fees.activeStatus=ActiveStatus.DELETE
        fees.save()
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, "Item Deleted successfully.")
        outPut = result as JSON
        render outPut
        return
    }

    //@todo-aminul refactor for Careteria item
    def classStationary() {
        def classNameList = classNameService.classNameDropDownList()
        if (!classNameList) {
            render(view: '/collection/chart/classStationary', model: [dataReturn: null, totalCount: 0, classNameList: null, classFeesList: null])
            return
        }
        def firstClass = classNameList.first()
        params.put('className', firstClass?.id)
        LinkedHashMap resultMap = chartOfAccountService.stationaryPaginationList(params, FeeAppliedType.CLASS_STD)
        def classStationaryList = chartOfAccountService.feesDropDownList(AccountType.cafeteriaItems(), FeeAppliedType.CLASS_STD)
        int totalCount = resultMap.totalCount
        if (!resultMap || totalCount == 0) {
            render(view: '/collection/chart/classStationary', model: [dataReturn: null, totalCount: 0, classNameList: classNameList, classStationaryList: classStationaryList])
            return
        }
        render(view: '/collection/chart/classStationary', model: [dataReturn: resultMap.results, totalCount: totalCount, classNameList: classNameList, classStationaryList: classStationaryList])

    }
    //@todo-aminul refactor for Careteria item
    def commonStationary() {
        LinkedHashMap resultMap = chartOfAccountService.stationaryPaginationList(params, FeeAppliedType.ALL_STD)
        def commonStationaryList = chartOfAccountService.feesDropDownList(AccountType.cafeteriaItems(), FeeAppliedType.ALL_STD)
        int totalCount = resultMap.totalCount
        if (!resultMap || totalCount == 0) {
            render(view: '/collection/chart/commonStationary', model: [dataReturn: null, totalCount: 0, commonStationaryList: commonStationaryList])
            return
        }
        render(view: '/collection/chart/commonStationary', model: [dataReturn: resultMap.results, totalCount: totalCount, commonStationaryList: commonStationaryList])
    }

    def stationaryList() {

        LinkedHashMap gridData
        String result
        if (!params.feeAppliedType) {
            gridData = [iTotalRecords: 0, iTotalDisplayRecords: 0, aaData: []]
            result = gridData as JSON
            render result
            return
        }
        FeeAppliedType feeAppliedType = FeeAppliedType.valueOf(params.feeAppliedType.toString())
        if (!feeAppliedType) {
            gridData = [iTotalRecords: 0, iTotalDisplayRecords: 0, aaData: []]
            result = gridData as JSON
            render result
            return
        }

        LinkedHashMap resultMap = chartOfAccountService.stationaryPaginationList(params, feeAppliedType)

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

        def classNameList = classNameService.classNameDropDownList()
        if (!classNameList) {
            render(view: '/collection/chart/commonStationary', model: [dataReturn: null, totalCount: 0, classNameList: null, classFeesList: null])
            return
        }
    }

    def saveStationary(StationaryItemCommand command) {

        if (!request.method.equals('POST')) {
            redirect(action: 'index')
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

        //if already added
        def ifAlreadyAdded = null
        if(command.id){
            if (command.feeAppliedType == FeeAppliedType.CLASS_STD) {
                ifAlreadyAdded = StationaryItem.findAllByAccountAndClassNameAndNameAndActiveStatusAndIdNotEqual(command.account, command.className,command.name, ActiveStatus.ACTIVE, command.id)
            } else if (command.feeAppliedType == FeeAppliedType.ALL_STD) {
                ifAlreadyAdded = StationaryItem.findAllByAccountAndNameAndActiveStatusAndIdNotEqual(command.account, command.name, ActiveStatus.ACTIVE, command.id)
            }
        }else {
            if (command.feeAppliedType == FeeAppliedType.CLASS_STD) {
                ifAlreadyAdded = StationaryItem.findAllByAccountAndClassNameAndNameAndActiveStatus(command.account, command.className,command.name, ActiveStatus.ACTIVE)
            } else if (command.feeAppliedType == FeeAppliedType.ALL_STD) {
                ifAlreadyAdded = StationaryItem.findAllByAccountAndNameAndActiveStatus(command.account, command.name, ActiveStatus.ACTIVE)
            }
        }


        if (ifAlreadyAdded) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "${command.name} already added. You can edit or delete only.")
            outPut = result as JSON
            render outPut
            return
        }


        StationaryItem item
        String message
        Double discountAmount = 0
        if (command.discount > 0 && command.discount <= 100) {
            discountAmount = command.amount * command.discount * 0.01
        }
        FeeItems feeItems
        ItemsDetail feeDetailItems
        ChartOfAccount account = command.account
        def errorList

        if (command.id) {
            item = StationaryItem.get(command.id)
            if (!item){
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
                outPut = result as JSON
                render outPut
                return
            }

            if(item.iterationType != command.iterationType){
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, "Can't change Iteration Type ${item.iterationType.value} to ${command.iterationType.value}.")
                outPut = result as JSON
                render outPut
                return
            }
            item.properties = command.properties
            item.netPayable = command.amount - discountAmount.round(2)
            if (item.hasErrors() || !item.save()){
                errorList = item?.errors?.allErrors?.collect { messageSource.getMessage(it, null) }
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, errorList?.join('\n'))
                outPut = result as JSON
                render outPut
                return
            }

            try {
                FeeItems oldItem = FeeItems.findByStationaryItem(item)
                if(!oldItem){
                    result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                    result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
                    outPut = result as JSON
                    render outPut
                    return
                }


                if ((command.iterationType == FeeIterationType.RECEIVE) || (command.iterationType == FeeIterationType.YEARLY)) {
                    oldItem.chartOfAccount = account
                    oldItem.name = command?.name
                    oldItem.code = command?.code
                    oldItem.amount = item.amount
                    oldItem.discount =item.discount
                    oldItem.netPayable =item.netPayable
                    oldItem.quickFee1 = item.quickFee1
                    oldItem.quickFee2 = item.quickFee2
                    oldItem.className = item.className
                    oldItem.feeType = item.feeType
                    oldItem.feeAppliedType = item.feeAppliedType
                    oldItem.dueOnType = item.dueOnType
                    oldItem.iterationType = item.iterationType
                } else if (command.iterationType == FeeIterationType.MONTHLY) {
                    oldItem.chartOfAccount = account
                    oldItem.name = command?.name
                    oldItem.code = command?.code
                    oldItem.amount = item.amount
                    oldItem.discount =item.discount
                    oldItem.netPayable =item.netPayable
                    oldItem.quickFee1 = item.quickFee1
                    oldItem.quickFee2 = item.quickFee2
                    oldItem.className = item.className
                    oldItem.feeType = item.feeType
                    oldItem.feeAppliedType = item.feeAppliedType
                    oldItem.dueOnType = item.dueOnType
                    oldItem.iterationType = item.iterationType

                    oldItem.itemsDetail.each {ItemsDetail detail ->
                        detail.amount=item.amount
                        detail.discount = item.discount
                        detail.netPayable = item.netPayable
                        detail.quickFee1 = item.quickFee1
                        detail.quickFee2 = item.quickFee2
                        detail.save()
                    }

                } else if (command.iterationType == FeeIterationType.DAILY) {
                    oldItem.chartOfAccount = account
                    oldItem.name = command?.name
                    oldItem.code = command?.code
                    oldItem.amount = item.amount
                    oldItem.discount =item.discount
                    oldItem.netPayable =item.netPayable
                    oldItem.quickFee1 = item.quickFee1
                    oldItem.quickFee2 = item.quickFee2
                    oldItem.className = item.className
                    oldItem.feeType = item.feeType
                    oldItem.feeAppliedType = item.feeAppliedType
                    oldItem.dueOnType = item.dueOnType
                    oldItem.iterationType = item.iterationType

                    oldItem.itemsDetail.each {ItemsDetail detail ->
                        detail.amount=item.amount
                        detail.discount = item.discount
                        detail.netPayable = item.netPayable
                        detail.quickFee1 = item.quickFee1
                        detail.quickFee2 = item.quickFee2
                        detail.save()
                    }
                }
                if (oldItem.hasErrors() || !oldItem.save()) {
                    errorList = feeItems?.errors?.allErrors?.collect { messageSource.getMessage(it, null) }
                    message = errorList?.join('\n')
                    result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                    result.put(CommonUtils.MESSAGE, message)
                    outPut = result as JSON
                    render outPut
                    return
                }
            }catch (DataIntegrityViolationException e){
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, e.getMessage())
                outPut = result as JSON
                render outPut
                return
            }

            message ='Stationary Fees Updated successfully'
        }else {
            item = new StationaryItem(command.properties)
            message = 'Stationary Fees Create successfully'

            item.netPayable = command.amount - discountAmount.round(2)
            if (item.hasErrors() || !item.save()) {
                errorList = item?.errors?.allErrors?.collect { messageSource.getMessage(it, null) }
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, message)
                outPut = result as JSON
                render outPut
                return
            }



            if ((command.iterationType == FeeIterationType.RECEIVE) || (command.iterationType == FeeIterationType.YEARLY)) {
                feeItems = new FeeItems(stationaryItem: item, chartOfAccount: account, name: command?.name,code: command?.code,
                        amount: item.amount, discount: item.discount, netPayable: item.netPayable,
                        quickFee1: item.quickFee1, quickFee2: item.quickFee2,
                        feeItemType: FeeItemType.STATIONARY, className: item.className, feeType: item.feeType,
                        feeAppliedType: item.feeAppliedType, dueOnType: item.dueOnType, iterationType: item.iterationType)
            } else if (command.iterationType == FeeIterationType.MONTHLY) {
                feeItems = new FeeItems(stationaryItem: item, chartOfAccount: account, name: command?.name,code: command?.code,
                        amount: item.amount, discount: item.discount, netPayable: item.netPayable,
                        quickFee1: item.quickFee1, quickFee2: item.quickFee2,
                        feeItemType: FeeItemType.STATIONARY, className: item.className, feeType: item.feeType,
                        feeAppliedType: item.feeAppliedType, dueOnType: item.dueOnType, iterationType: item.iterationType)
                YearMonths.values().each { monthValue ->
                    feeDetailItems = new ItemsDetail(name: "${monthValue.value}", amount: item.amount, discount: item.discount, netPayable: item.netPayable, quickFee1: item.quickFee1, quickFee2: item.quickFee2, sortPosition: CommonUtils.monthOrder(monthValue))
                    feeItems.addToItemsDetail(feeDetailItems)
                }
            } else if (command.iterationType == FeeIterationType.DAILY) {
                feeItems = new FeeItems(stationaryItem: item, chartOfAccount: account, name:command?.name,code: command?.code,
                        amount: item.amount, discount: item.discount, netPayable: item.netPayable,
                        quickFee1: item.quickFee1, quickFee2: item.quickFee2,
                        feeItemType: FeeItemType.STATIONARY, className: item.className, feeType: item.feeType,
                        feeAppliedType: item.feeAppliedType, dueOnType: item.dueOnType, iterationType: item.iterationType)
                WeekDays.values().each { monthValue ->
                    feeDetailItems = new ItemsDetail(name: "${monthValue.value}", amount: item.amount, discount: item.discount, netPayable: item.netPayable, quickFee1: item.quickFee1, quickFee2: item.quickFee2, sortPosition: dayOrder(monthValue))
                    feeItems.addToItemsDetail(feeDetailItems)
                }
            }


            if (feeItems.hasErrors() || !feeItems.save()) {
                errorList = feeItems?.errors?.allErrors?.collect { messageSource.getMessage(it, null) }
                message = errorList?.join('\n')
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, message)
                outPut = result as JSON
                render outPut
                return
            }
        }
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, message)
        outPut = result as JSON
        render outPut
    }

    def editStationary(Long id) {

        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        StationaryItem item = StationaryItem.read(id)

        if (!item) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.OBJ, item)
        outPut = result as JSON
        render outPut
    }

    def deleteStationary(Long id) {

        LinkedHashMap result = new LinkedHashMap()
        String outPut
        StationaryItem item = StationaryItem.get(id)
        if(!item){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        boolean itemInUse=false
        FeeItems oldItem = FeeItems.findByStationaryItem(item)
        try {
            if(oldItem){
                oldItem.delete(flush: true)
            }
            item.delete(flush: true)
        }catch (DataIntegrityViolationException e){
            itemInUse=true
            item.discard()
            oldItem.discard()
        }
        if (itemInUse){
            StationaryItem feesNew = StationaryItem.get(id)
            feesNew.activeStatus=ActiveStatus.DELETE
            feesNew.save(flush: true)
            oldItem = FeeItems.findByStationaryItem(feesNew)
            if(oldItem){
                oldItem.activeStatus=ActiveStatus.DELETE
                oldItem.save(flush: true)
            }
        }
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, "Item Deleted successfully.")
        outPut = result as JSON
        render outPut
        return
    }


    def saveCafeteria(CafeteriaCommand command){
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
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
        Cafeteria cafeteria
        String message
        FeeItems feeItems

        Double discountAmount = 0
        if (command.discount > 0 && command.discount <= 100) {
            discountAmount = command.amount * command.discount * 0.01
        }
        if(command.id){
            cafeteria = Cafeteria.get(command.id)
            if(!cafeteria){
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
                outPut = result as JSON
                render outPut
                return
            }
            cafeteria.properties['name', 'description','amount','discount'] = command.properties
            cafeteria.netPayable = command.amount - discountAmount.round(2)
            if(cafeteria.hasErrors()){
                def errorList = cafeteria?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
                message = errorList?.join('\n')
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            }
            cafeteria.save()
            feeItems = FeeItems.findAllByCafeteriaAndActiveStatus(cafeteria,ActiveStatus.ACTIVE)
            feeItems.name=cafeteria.name
            feeItems.code=cafeteria.code
            feeItems.amount=cafeteria.amount
            feeItems.discount=cafeteria.discount
            feeItems.netPayable=cafeteria.netPayable
            feeItems.save()
            message ="Item updated successfully."
            result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        }else {
            cafeteria = new Cafeteria(command.properties)
            ChartOfAccount account = ChartOfAccount.findByAccountTypeAndNodeType(AccountType.CAFETERIA,NodeType.ROOT)
            cafeteria.account=account
            cafeteria.feeType=FeeType.ACTIVATION
            cafeteria.feeAppliedType=FeeAppliedType.ALL_STD
            cafeteria.dueOnType=DueOnType.DUE
            cafeteria.iterationType=FeeIterationType.DAILY
            cafeteria.netPayable = command.amount - discountAmount.round(2)
            if(cafeteria.hasErrors()){
                def errorList = cafeteria?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
                message = errorList?.join('\n')
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            }
            Cafeteria savedItem = cafeteria.save()
            if(savedItem){
                feeItems = new FeeItems(cafeteria: savedItem, chartOfAccount: account, name: savedItem.name,code: savedItem.code,
                        amount: savedItem.amount, discount: savedItem.discount, netPayable: savedItem.netPayable,
                        quickFee1: true, quickFee2: false,
                        feeItemType: FeeItemType.CAFETERIA, feeType: savedItem.feeType,
                        feeAppliedType: savedItem.feeAppliedType, dueOnType: savedItem.dueOnType, iterationType: savedItem.iterationType)
                feeItems.save()
            }
            message ="Item added successfully."
            result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        }


        if(cafeteria.hasErrors() || !cafeteria.save()){
            def errorList = cafeteria?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            message = errorList?.join('\n')
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
        }
        result.put(CommonUtils.MESSAGE, message)
        outPut = result as JSON
        render outPut
        return
    }
    def editCafeteria(Long id){
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        Cafeteria cafeteria = Cafeteria.read(id)
        if (!cafeteria) {
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
        result.put(CommonUtils.OBJ,cafeteria)
        outPut = result as JSON
        render outPut
    }
    def deleteCafeteria(Long id){
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        Cafeteria cafeteria = Cafeteria.get(id)
        if (!cafeteria) {
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        try {
            def feeItems = FeeItems.findAllByCafeteriaAndActiveStatus(cafeteria,ActiveStatus.ACTIVE)
            feeItems.each {FeeItems item ->
                item.activeStatus=ActiveStatus.DELETE
                item.save()
            }
            cafeteria.activeStatus=ActiveStatus.DELETE
            cafeteria.save()
        }
        catch(DataIntegrityViolationException e) {
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,"Item already in use. Please delete reference first")
            outPut = result as JSON
            render outPut
            return
        }
        result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
        result.put(CommonUtils.MESSAGE,"Item deleted successfully.")
        outPut = result as JSON
        render outPut
    }


    private Integer dayOrder(WeekDays weekDays) {
        Integer sortPosition =1
        switch (weekDays) {
            case weekDays.SUN:
                sortPosition = 1
                break;
            case weekDays.MON:
                sortPosition =2
                break;
            case weekDays.TUE:
                sortPosition =3
                break;
            case weekDays.WED:
                sortPosition =4
                break;
            case weekDays.THR:
                sortPosition =5
                break;
            case weekDays.FRI:
                sortPosition =6
                break;
            case weekDays.SAT:
                sortPosition =7
                break;

        }
        return sortPosition
    }
}