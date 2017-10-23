package com.grailslab

import com.grailslab.accounting.ChartOfAccount
import com.grailslab.accounting.Fees
import com.grailslab.accounting.StationaryItem
import com.grailslab.enums.*
import com.grailslab.gschoolcore.AcademicYear
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.settings.ClassName
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

class ChartOfAccountService {
    static transactional = false
    def schoolService
    static final String[] sortColumns = ['id', 'acc.code', 'feeType', 'amount', 'discount', 'netPayable']

    LinkedHashMap feesPaginationList(GrailsParameterMap params, FeeAppliedType feeAppliedType) {
        int iDisplayStart = params.iDisplayStart ? params.getInt('iDisplayStart') : CommonUtils.DEFAULT_PAGINATION_START
        int iDisplayLength = params.iDisplayLength ? params.getInt('iDisplayLength') : CommonUtils.DEFAULT_PAGINATION_LENGTH
        String sSortDir = params.sSortDir_0 ? params.sSortDir_0 : CommonUtils.SORT_ORDER_ASC
        int iSortingCol = params.iSortCol_0 ? params.getInt('iSortCol_0') : 1
        String sSearch = params.sSearch ? params.sSearch : null
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }
        ClassName className = null
        if (params.className) {
            className = ClassName.read(params.className.toLong())
        }
        def workingYears = schoolService.workingYears()
        String sortColumn = CommonUtils.getSortColumn(sortColumns, iSortingCol)
        List dataReturns = new ArrayList()
        def c = Fees.createCriteria()
        def results = c.list(max: iDisplayLength, offset: iDisplayStart) {
            createAlias('account', 'acc')
            and {
                eq("feeAppliedType", feeAppliedType)
                eq("activeStatus", ActiveStatus.ACTIVE)
                if (feeAppliedType == FeeAppliedType.CLASS_STD && className) {
                    eq("className", className)
                }
                /*or {
                    'in'("academicYear", workingYears)
                    'ne'('feeType', FeeType.ACTIVATION)
                }*/
            }
            if (sSearch) {
                or {
                    ilike('acc.name', sSearch)
                }
            }
            order(sortColumn, sSortDir)
//            order('subjectName.sortPosition', CommonUtils.SORT_ORDER_ASC)
        }
        int totalCount = results.totalCount
        int serial = iDisplayStart;

        if (totalCount > 0) {
            if (sSortDir.equals(CommonUtils.SORT_ORDER_DESC)) {
                serial = (totalCount + 1) - iDisplayStart
            }
            String feeName
            String feeType
            Boolean hasDetailItem = false
            ChartOfAccount account
            ChartOfAccount pAccount
            results.each { Fees fees ->
                if (sSortDir.equals(CommonUtils.SORT_ORDER_ASC)) {
                    serial++
                } else {
                    serial--
                }
                if (fees.iterationType == FeeIterationType.DAILY || fees.iterationType == FeeIterationType.MONTHLY) {
                    hasDetailItem = true
                } else {
                    hasDetailItem = false
                }
                account = fees.account
                feeName = "${account?.code} - ${account?.name}"
                if (account.nodeType == NodeType.SECOND) {
                    pAccount = ChartOfAccount.read(account.parentId)
                    feeName = "${account?.code} - ${pAccount?.name} - ${account?.name}"
                }
                feeType = "${fees.feeType?.value} (${fees.iterationType?.value})"
                dataReturns.add([DT_RowId: fees.id, hasDetailItem: hasDetailItem, 0: serial, 1: feeName, 2: feeType, 3: fees.amount, 4: fees.discount, 5: fees.netPayable, 6: ''])
            }
        }
        return [totalCount: totalCount, results: dataReturns]
    }

    LinkedHashMap stationaryPaginationList(GrailsParameterMap params, FeeAppliedType feeAppliedType) {
        int iDisplayStart = params.iDisplayStart ? params.getInt('iDisplayStart') : CommonUtils.DEFAULT_PAGINATION_START
        int iDisplayLength = params.iDisplayLength ? params.getInt('iDisplayLength') : CommonUtils.DEFAULT_PAGINATION_LENGTH
        String sSortDir = params.sSortDir_0 ? params.sSortDir_0 : CommonUtils.SORT_ORDER_ASC
        int iSortingCol = params.iSortCol_0 ? params.getInt('iSortCol_0') : CommonUtils.DEFAULT_PAGINATION_SORT_IDX
        String sSearch = params.sSearch ? params.sSearch : null
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }
        ClassName className = null
        if (params.className) {
            className = ClassName.read(params.className.toLong())
        }
        String sortColumn = CommonUtils.getSortColumn(sortColumns, iSortingCol)
        List dataReturns = new ArrayList()
        def c = StationaryItem.createCriteria()
        def results = c.list(max: iDisplayLength, offset: iDisplayStart) {
            createAlias('account', 'acc')
//            createAlias('className','clsName')
            and {
                eq("feeAppliedType", feeAppliedType)
                eq("activeStatus", ActiveStatus.ACTIVE)
                if (feeAppliedType == FeeAppliedType.CLASS_STD && className) {
                    eq("className", className)
                }
            }
            if (sSearch) {
                or {
                    ilike('acc.name', sSearch)
                }
            }
            order(sortColumn, sSortDir)
//            order('subjectName.sortPosition', CommonUtils.SORT_ORDER_ASC)
        }
        int totalCount = results.totalCount
        int serial = iDisplayStart;

        if (totalCount > 0) {
            if (sSortDir.equals(CommonUtils.SORT_ORDER_DESC)) {
                serial = (totalCount + 1) - iDisplayStart
            }
            results.each { StationaryItem item ->
                if (sSortDir.equals(CommonUtils.SORT_ORDER_ASC)) {
                    serial++
                } else {
                    serial--
                }
                dataReturns.add([DT_RowId: item.id, 0: serial, 1: "${item.code} - ${item.name}", 2: item.amount, 3: item.discount, 4: item.netPayable, 5: ''])
            }
        }
        return [totalCount: totalCount, results: dataReturns]
    }

    def listAllItems() {
        List dataReturns = new ArrayList()
        def c = ChartOfAccount.createCriteria()
        def results = c.list() {
            and {
                'in'("accountType", AccountType.feeItems())
                eq("activeStatus", ActiveStatus.ACTIVE)
                'ne'("nodeType", NodeType.ROOT)
            }
            order("code", CommonUtils.SORT_ORDER_ASC)
        }
        String treeClassName
        String scholarShip="No"
        results.each { ChartOfAccount account ->
            if (account.nodeType == NodeType.FIRST) {
                treeClassName = "treegrid-${account.id}"
            } else if (account.nodeType == NodeType.SECOND || account.nodeType == NodeType.THIRD) {
                treeClassName = "treegrid-${account.id} treegrid-parent-${account.parentId}"
            } else {
                treeClassName = "treegrid-${account.id}"
            }
            if (account.scholarshipHead) {
                scholarShip = "Yes"
            } else {
                scholarShip = "No"
            }
            dataReturns.add([id: account.id, name: "${account.code} - ${account.name}", accountType: account.accountType?.value, scholarshipHead:scholarShip,code: account.code, nodeType: account?.nodeType?.key, parentId: account.parentId, hasChild: account.hasChild, allowChild: account.allowChild, allowEdit: account.allowEdit, treeClassName: treeClassName])
        }
        return [results: dataReturns]
    }

    def list(AccountType accountType) {
        List dataReturns = new ArrayList()
        def c = ChartOfAccount.createCriteria()
        def results = c.list() {
            and {
                eq("accountType", accountType)
                eq("activeStatus", ActiveStatus.ACTIVE)
                'ne'("nodeType", NodeType.ROOT)
            }
            order("code", CommonUtils.SORT_ORDER_ASC)
        }
        String treeClassName
        results.each { ChartOfAccount account ->
            if (account.nodeType == NodeType.FIRST) {
                treeClassName = "treegrid-${account.id}"
            } else if (account.nodeType == NodeType.SECOND || account.nodeType == NodeType.THIRD) {
                treeClassName = "treegrid-${account.id} treegrid-parent-${account.parentId}"
            } else {
                treeClassName = "treegrid-${account.id}"
            }
            dataReturns.add([id: account.id, name: "${account.code} - ${account.name}", accountType: account.accountType?.value, code: account.code, nodeType: account?.nodeType?.key, parentId: account.parentId, hasChild: account.hasChild, allowChild: account.allowChild, allowEdit: account.allowEdit, treeClassName: treeClassName])
        }
        return [results: dataReturns]
    }

    def accountDropDownList(AccountType accountType, NodeType nodeType = null) {
        if (!nodeType) {
            nodeType = NodeType.THIRD
        }
        def c = ChartOfAccount.createCriteria()
        def results = c.list() {
            and {
                eq("accountType", accountType)
                eq("nodeType", nodeType)
//                eq("activeStatus", ActiveStatus.ACTIVE)
            }
            order('id', CommonUtils.SORT_ORDER_ASC)
        }
        List dataReturns = new ArrayList()
        results.each { ChartOfAccount account ->
            dataReturns.add([id: account.id, name: account.code + ' - ' + account.name])
        }
        return dataReturns
    }

    def feesDropDownList(def accountTypes, FeeAppliedType appliedType) {
        def c = ChartOfAccount.createCriteria()
        def results = c.list() {
            and {
                'in'("accountType", accountTypes)
                'in'("nodeType", [NodeType.FIRST, NodeType.SECOND] as List)
                eq("appliedType", appliedType)
                eq("activeStatus", ActiveStatus.ACTIVE)
                eq("displayHead", Boolean.TRUE)
            }
            order('id', CommonUtils.SORT_ORDER_ASC)
        }
        List dataReturns = new ArrayList()
        String name
        ChartOfAccount pAccount
        results.each { ChartOfAccount account ->
            name = "${account?.code} - ${account?.name}"
            if (account.nodeType == NodeType.SECOND) {
                pAccount = ChartOfAccount.read(account.parentId)
                name = "${account?.code} - ${pAccount?.name} - ${account?.name}"
            }
            dataReturns.add([id: account.id, name: name])
        }
        return dataReturns
    }

    def feesListForCollectionReport() {
        def c = ChartOfAccount.createCriteria()
        def results = c.list() {
            and {
                'in'("accountType", AccountType.feeMenuItems())
                'in'("nodeType", [NodeType.FIRST, NodeType.SECOND] as List)
                eq("activeStatus", ActiveStatus.ACTIVE)
                eq("displayHead", Boolean.TRUE)
            }
            order('code', CommonUtils.SORT_ORDER_ASC)
        }
        List dataReturns = new ArrayList()
        String name
        ChartOfAccount pAccount
        for (account in results){
            name = account.name
            if (account.nodeType == NodeType.SECOND) {
                pAccount = ChartOfAccount.read(account.parentId)
                name = "${pAccount?.name} - ${account?.name}"
            }
            dataReturns.add([id: account.id, account:account, name: name])
        }

        return dataReturns
    }

   /* def feesDropDownList(AccountType accountType, FeeAppliedType appliedType) {
        def c = ChartOfAccount.createCriteria()
        def results = c.list() {
            and {
                eq("accountType", accountType)
                'in'("nodeType", [NodeType.FIRST, NodeType.SECOND] as List)
                eq("appliedType", appliedType)
                eq("activeStatus", ActiveStatus.ACTIVE)
                eq("displayHead", Boolean.TRUE)
            }
            order('id', CommonUtils.SORT_ORDER_ASC)
        }
        List dataReturns = new ArrayList()
        String name
        ChartOfAccount pAccount
        results.each { ChartOfAccount account ->
            name = "${account?.code} - ${account?.name}"
            if (account.nodeType == NodeType.SECOND) {
                pAccount = ChartOfAccount.read(account.parentId)
                name = "${account?.code} - ${pAccount?.name} - ${account?.name}"
            }
            dataReturns.add([id: account.id, name: name])
        }
        return dataReturns
    }*/

    def chartDDListByChart(Long parentId) {
        def c = ChartOfAccount.createCriteria()
        def results = c.list() {
            and {
                eq("parentId", parentId)
                eq("activeStatus", ActiveStatus.ACTIVE)
            }
            order('id', CommonUtils.SORT_ORDER_ASC)
        }
        List dataReturns = new ArrayList()
        String name
        results.each { ChartOfAccount account ->
            name = "${account?.code} - ${account?.name}"
            dataReturns.add([id: account.id, name: name])
        }
        return dataReturns
    }
}
