package com.grailslab

import com.grailslab.enums.HrKeyType
import com.grailslab.enums.MainUserType
import com.grailslab.enums.SelectionTypes
import com.grailslab.gschoolcore.AcademicYear
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.hr.EducationalInfo
import com.grailslab.hr.Employee
import com.grailslab.hr.EmploymentHistory
import com.grailslab.hr.HrCategory
import com.grailslab.hr.HrDesignation
import com.grailslab.hr.HrPeriod
import com.grailslab.settings.ClassName
import com.grailslab.settings.Image
import com.grailslab.stmgmt.Student
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

class EmployeeService {
    static transactional = false
    def grailsApplication

    static final String[] sortColumns = ['sortOrder','empID', 'name', 'cardNo', 'mobile', 'emailId','designation.name','sortOrder']

    LinkedHashMap employeePaginateList(GrailsParameterMap params) {
        int iDisplayStart = params.iDisplayStart ? params.getInt('iDisplayStart') : CommonUtils.DEFAULT_PAGINATION_START
        int iDisplayLength = params.iDisplayLength ? params.getInt('iDisplayLength') : CommonUtils.DEFAULT_PAGINATION_LENGTH
        String sSortDir = params.sSortDir_0 ? params.sSortDir_0 : CommonUtils.SORT_ORDER_ASC
        int iSortingCol = params.iSortCol_0 ? params.getInt('iSortCol_0') : CommonUtils.DEFAULT_PAGINATION_SORT_IDX
        String sSearch = params.sSearch ? params.sSearch : null
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }

        HrCategory hrCategory
        if (params.hrCategoryType) {
            hrCategory = HrCategory.read(params.getLong('hrCategoryType'))
        }
        ActiveStatus activeStatus = ActiveStatus.ACTIVE
        if (params.activeStatus) {
            activeStatus = ActiveStatus.valueOf(params.activeStatus)
        }
        String sortColumn = CommonUtils.getSortColumn(sortColumns, iSortingCol)
        List dataReturns = new ArrayList()
        def c = Employee.createCriteria()
        def results = c.list(max: iDisplayLength, offset: iDisplayStart) {
            createAlias('hrDesignation', 'designation')
            and {
                eq("activeStatus", activeStatus)
                if (hrCategory) {
                    eq("hrCategory", hrCategory)
                }
            }
            if (sSearch) {
                or {
                    ilike('name', sSearch)
                    ilike('empID', sSearch)
                    ilike('fathersName', sSearch)
                    ilike('emailId', sSearch)
                    ilike('cardNo', sSearch)
                    ilike('mobile', sSearch)
                }
            }
            order(sortColumn, sSortDir)
            order('joiningDate', CommonUtils.SORT_ORDER_ASC)
        }
        int totalCount = results.totalCount
        int serial = iDisplayStart;
        if (totalCount > 0) {
            if (sSortDir.equals(CommonUtils.SORT_ORDER_DESC)) {
                serial = (totalCount + 1) - iDisplayStart
            }
            results.each { Employee employee ->
                if (sSortDir.equals(CommonUtils.SORT_ORDER_ASC)) {
                    serial++
                } else {
                    serial--
                }
                dataReturns.add([DT_RowId: employee.id, 0:serial,1: employee?.empID, 2: employee?.name, 3: employee?.cardNo ?: '', 4: employee?.mobile ?: '', 5: employee.emailId ?: '', 6: employee.hrDesignation ? employee.hrDesignation.name : '',7: employee.hrStaffCategory ?: '', 8: employee.sortOrder, 9:''])
            }
        }
        return [totalCount: totalCount, results: dataReturns]
    }

    def teacherDropDownList() {
        List dataReturns = new ArrayList()
        HrCategory teacherType = HrCategory.findByHrKeyType(HrKeyType.TEACHER)
        def c = Employee.createCriteria()
        def results = c.list() {
            and {
                eq("hrCategory", teacherType)
                eq("activeStatus", ActiveStatus.ACTIVE)
            }
            order("name", CommonUtils.SORT_ORDER_ASC)
        }
        results.each { Employee employee ->
            dataReturns.add([id: employee.id, name: "${employee.empID} - ${employee.name}"])
        }
        return dataReturns
    }

    def allEmployeeDropDownList() {
        List dataReturns = new ArrayList()
        def c = Employee.createCriteria()
        def results = c.list() {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
            }
            order("sortOrder", CommonUtils.SORT_ORDER_ASC)
        }
        results.each { Employee employee ->
            dataReturns.add([id: employee.id, name: "${employee.empID} - ${employee.name}"])
        }
        return dataReturns
    }

    def employeeList(HrCategory hrCategory = null) {
        def c = Employee.createCriteria()
        def results = c.list() {
            and {
                if (hrCategory){
                    eq("hrCategory", hrCategory)
                }
                eq("activeStatus", ActiveStatus.ACTIVE)
            }
            order("sortOrder", CommonUtils.SORT_ORDER_ASC)
        }
        return results
    }

    def employeeList(Date joinedBefore, List<Long> empIdLIst, HrCategory hrCategory = null) {
        def joinedBeforeList
        if (hrCategory){
            joinedBeforeList = Employee.findAllByIdInListAndActiveStatusAndHrCategoryAndJoiningDateLessThan(empIdLIst, ActiveStatus.ACTIVE, hrCategory, joinedBefore)
        } else {
            joinedBeforeList = Employee.findAllByIdInListAndActiveStatusAndJoiningDateLessThan(empIdLIst, ActiveStatus.ACTIVE, joinedBefore)
        }
        return joinedBeforeList
    }

    def employeeIdList(HrCategory hrCategory = null) {
        List dataReturns = new ArrayList()
        def c = Employee.createCriteria()
        def results = c.list() {
            and {
                if (hrCategory){
                    eq("hrCategory", hrCategory)
                }
                eq("activeStatus", ActiveStatus.ACTIVE)
            }
            projections {
                property('id')
            }
        }
        return results
    }

    def allEmployeeWithDesignationList(String sSearch) {
        List dataReturns = new ArrayList()
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }
        def c = Employee.createCriteria()

        def results = c.list() {
            createAlias('hrDesignation', 'designation')
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
            }
            if (sSearch) {
                or {
                    ilike('empID', sSearch)
                    ilike('name', sSearch)
                    ilike('designation.name', sSearch)
                }
            }
            order("empID", CommonUtils.SORT_ORDER_ASC)
        }
        results.each { Employee employee ->
            dataReturns.add([id: employee.id, name: "${employee.empID} - ${employee.name} - ${employee.hrDesignation.name}"])
        }
        return dataReturns
    }
    def allEmployeeDropDownListWithCardNo() {
        List dataReturns = new ArrayList()
        def c = Employee.createCriteria()
        def results = c.list() {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
            }
            order("sortOrder", CommonUtils.SORT_ORDER_ASC)
        }
        results.each { Employee employee ->
            dataReturns.add([id: employee.id, name: "${employee.empID} - ${employee.name} - ${employee.cardNo}"])
        }
        return dataReturns
    }

    def allEmployeeWithList(String sSearch) {
        List dataReturns = new ArrayList()
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }
        def c = Employee.createCriteria()

        def results = c.list() {
            createAlias('hrDesignation', 'designation')
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
            }
            if (sSearch) {
                or {
                    ilike('empID', sSearch)
                    ilike('name', sSearch)
                    ilike('designation.name', sSearch)
                }
            }
            order("sortOrder", CommonUtils.SORT_ORDER_ASC)
        }
        results.each { Employee employee ->
            dataReturns.add([id: employee.id, name: "${employee.empID} - ${employee.name} - ${employee.hrDesignation.name}"])
        }
        return dataReturns
    }


    def step2EmpListForMessage(List<Long> selectionIds, SelectionTypes selectionType = null) {
        String sSearch = "01" + CommonUtils.PERCENTAGE_SIGN
        def c = Employee.createCriteria()
        def results = c.list() {
            createAlias('hrCategory', 'dept')
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
                ilike('mobile', sSearch)
                if (selectionType && selectionType == SelectionTypes.BY_TEACHER) {
                    'in'("id", selectionIds)
                } else {
                    'in'("dept.id", selectionIds)
                }
            }
        }
        List dataReturns = new ArrayList()
        String mobileNo
        results.each { Employee employee ->
            mobileNo = employee.mobile
            if (mobileNo && mobileNo.size() == 11) {
                dataReturns.add([id: employee.id, name: "${employee.empID} - ${employee.name} [${employee?.hrDesignation?.name}]", mobile: employee.mobile])
            }
        }
        return dataReturns
    }

    Employee findByEmpId(String empId) {
        Employee employee = Employee.findByEmpIDAndActiveStatus(empId, ActiveStatus.ACTIVE)
        return employee
    }


    def dataJobInfoList(Employee employee) {
        List dataReturns = new ArrayList()
        def c = EmploymentHistory.createCriteria()
        def results = c.list() {
            and {
                eq("employee", employee)
                eq("activeStatus", ActiveStatus.ACTIVE)
            }
            order("id", CommonUtils.SORT_ORDER_ASC)
        }
        results.each { EmploymentHistory history ->
            dataReturns.add([DT_RowId: history.id, 0: history?.company, 1: history?.jobTitle, 2: CommonUtils.getUiDateStr(history?.joiningDate), 3: CommonUtils.getUiDateStr(history?.endDate), 4: history?.location, 5: ''])
        }
        return dataReturns
    }

    def academicInfoList(Employee employee) {
        List dataReturns = new ArrayList()
        def c = EducationalInfo.createCriteria()
        def results = c.list() {
            and {
                eq("employee", employee)
                eq("activeStatus", ActiveStatus.ACTIVE)
            }
            order("id", CommonUtils.SORT_ORDER_ASC)
        }
        results.each { EducationalInfo info ->
            dataReturns.add([DT_RowId: info.id, 0: info?.certification?.name, 1: info?.institution?.name, 2: info?.majorSubject?.name, 3: CommonUtils.getUiDateStr(info?.passingYear), 4: info?.result, 5: info?.duration, 6: ''])
        }
        return dataReturns
    }

    def employeeCardNoList(){
        def c = Employee.createCriteria()
        def results = c.list() {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
                isNotNull("cardNo")
            }
            projections {
                property('cardNo')
            }
        }
        return results
    }

    def findByEmpCardNumber(String cardNo) {
        Employee.findByCardNoAndActiveStatus(cardNo, ActiveStatus.ACTIVE)
    }

    def employeeHrPeriod(Long id) {
        Employee employee=Employee.read(id)
        if (!employee || !employee.hrPeriod) return null
        return [startTime:employee.hrPeriod.startTime, duration:employee.hrPeriod.duration]
    }

    def numberOfTotalEmployee(HrCategory hrCategory = null){
        if (hrCategory){
            return Employee.countByActiveStatusAndHrCategory(ActiveStatus.ACTIVE, hrCategory)
        } else {
            return Employee.countByActiveStatus(ActiveStatus.ACTIVE)
        }
    }
}