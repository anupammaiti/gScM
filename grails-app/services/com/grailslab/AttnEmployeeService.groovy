package com.grailslab

import com.grailslab.attn.AttnEmpRecord
import com.grailslab.attn.AttnRecordDay
import com.grailslab.hr.HrCategory
import com.grailslab.viewz.AttnEmpPresentView
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

class AttnEmployeeService {
    static transactional = false
    def employeeService

    static final String[] sortColumnsEmp = ['recordDayId','empid','name','designation','inTime','outTime']
    LinkedHashMap empPaginateList(GrailsParameterMap params) {
        int iDisplayStart = params.iDisplayStart ? params.getInt('iDisplayStart') : CommonUtils.DEFAULT_PAGINATION_START
        int iDisplayLength = params.iDisplayLength ? params.getInt('iDisplayLength') : CommonUtils.DEFAULT_PAGINATION_LENGTH
        String sSortDir = params.sSortDir_0 ? params.sSortDir_0 : CommonUtils.SORT_ORDER_ASC
        int iSortingCol = params.iSortCol_0 ? params.getInt('iSortCol_0') : CommonUtils.DEFAULT_PAGINATION_SORT_IDX
        String sSearch = params.sSearch ? params.sSearch : null
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }
        Long recordDateId =params.getLong('recordDateId')
        Long hrCategoryId=params.getLong('hrCategoryType')
        String sortColumn = CommonUtils.getSortColumn(sortColumnsEmp, iSortingCol)
        List dataReturns = new ArrayList()
        def c = AttnEmpPresentView.createCriteria()
        def results = c.list(max: iDisplayLength, offset: iDisplayStart) {
            and {
                eq("recordDayId",recordDateId)
                if(hrCategoryId){
                    eq("hrCategoryId",hrCategoryId)
                }
            }
            if (sSearch) {
                or {
                    ilike('designation', sSearch)
                    ilike('name', sSearch)
                    ilike('empid', sSearch)
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
            results.each { AttnEmpPresentView dailyRecord ->
                if (sSortDir.equals(CommonUtils.SORT_ORDER_ASC)) {
                    serial++
                } else {
                    serial--
                }
                dataReturns.add([DT_RowId: dailyRecord.objId, 0: serial, 1: dailyRecord.empid, 2: dailyRecord.name, 3: dailyRecord.designation, 4: CommonUtils.getUiTimeStr12HrLocal(dailyRecord.inTime),5: CommonUtils.getUiTimeStr12HrLocal(dailyRecord.outTime), 6: dailyRecord.isLate? "Late": "Present", 7: ''])
            }
        }
        return [totalCount: totalCount, results: dataReturns]
    }

    int numberOfTotalAttnEmployee(AttnRecordDay recordDay, HrCategory hrCategory = null){
        if (hrCategory){
            def empIdList = employeeService.employeeIdList(hrCategory)
            return AttnEmpRecord.countByRecordDayAndEmployeeIdInList(recordDay, empIdList)
        } else {
            return AttnEmpRecord.countByRecordDay(recordDay)
        }
    }
    int numberOfTotalLateEmployee(AttnRecordDay recordDay, HrCategory hrCategory = null){
        if (hrCategory){
            def empIdList = employeeService.employeeIdList(hrCategory)
            return AttnEmpRecord.countByRecordDayAndIsLateEntryAndEmployeeIdInList(recordDay, true, empIdList)
        } else {
            return AttnEmpRecord.countByRecordDayAndIsLateEntry(recordDay, true)
        }
    }

    int employeePresentCount(String employeeNo, Date startDate, Date endDate){
        def c = AttnEmpRecord.createCriteria()
        def count = c.list() {
            and {
                eq("empNo", employeeNo)
                between("recordDate", startDate, endDate)
            }
            projections {
                count()
            }
        }
        return count[0]
    }
    int employeeLateCount(String employeeNo, Date startDate, Date endDate){
        def c = AttnEmpRecord.createCriteria()
        def count = c.list() {
            and {
                eq("empNo", employeeNo)
                eq("isLateEntry", true)
                between("recordDate", startDate, endDate)
            }
            projections {
                count()
            }
        }
        return count[0]
    }

}
