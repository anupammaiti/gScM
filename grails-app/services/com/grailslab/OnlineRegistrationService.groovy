package com.grailslab

import com.grailslab.enums.ApplicantStatus
import com.grailslab.gschoolcore.AcademicYear
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.settings.ClassName
import com.grailslab.enums.DateRangeType
import com.grailslab.stmgmt.RegOnlineRegistration
import grails.plugin.springsecurity.SpringSecurityUtils
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.joda.time.LocalDate

class OnlineRegistrationService {
    static transactional = false
    def grailsApplication
    def schoolService

    static final String[] sortColumns = ['id','id','formNo', 'name','className', 'fathersName', 'mobile', 'birthDate','applyNo']
    LinkedHashMap applicantsPaginateList(GrailsParameterMap params) {
        int iDisplayStart = params.iDisplayStart ? params.getInt('iDisplayStart') : CommonUtils.DEFAULT_PAGINATION_START
        int iDisplayLength = params.iDisplayLength ? params.getInt('iDisplayLength') : CommonUtils.DEFAULT_PAGINATION_LENGTH
        String sSortDir = params.sSortDir_0 ? params.sSortDir_0 : CommonUtils.SORT_ORDER_DESC
        int iSortingCol = params.iSortCol_0 ? params.getInt('iSortCol_0') : CommonUtils.DEFAULT_PAGINATION_SORT_IDX
        String sSearch = params.sSearch ? params.sSearch : null
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }
        ClassName className = params.className ? ClassName.read(params.className) : null
        ApplicantStatus applicantStatus = ApplicantStatus.Apply
        if (params.applicantStatus){
            applicantStatus = ApplicantStatus.valueOf(params.applicantStatus)
        }
        AcademicYear academicYear = schoolService.schoolAdmissionYear()
        String sortColumn = CommonUtils.getSortColumn(sortColumns, iSortingCol)
        def g = grailsApplication.mainContext.getBean('org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib')
        String imageUrl
        List dataReturns = new ArrayList()
        def c = RegOnlineRegistration.createCriteria()
        def results = c.list(max: iDisplayLength, offset: iDisplayStart) {
            and {
                if (className) {
                    eq("className", className)
                }
                if (applicantStatus) {
                    eq("applicantStatus", applicantStatus)
                }
                eq("academicYear", academicYear)
                eq("activeStatus", ActiveStatus.ACTIVE)
            }
            if (sSearch) {
                or {
                    ilike('name', sSearch)
                    ilike('serialNo', sSearch)
                    ilike('applyNo', sSearch)
                    ilike('fathersName', sSearch)
                    ilike('mobile', sSearch)
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
            boolean hasRight = hasSelectRight()
            results.each { admissionForm ->
                if (sSortDir.equals(CommonUtils.SORT_ORDER_ASC)) {
                    serial++
                } else {
                    serial--
                }
                if (admissionForm.imagePath) {
                    imageUrl = '<img src="' + g.createLink(controller: 'image', action: 'streamImageFromIdentifierThumb', params: ['imagePath': admissionForm?.imagePath], absolute: true) + '" alt="Blank" style="width:25px;height:25px;">'
                } else {
                    imageUrl = '<img src="">'
                }

                dataReturns.add([hasRight: hasRight, applicantStatus: admissionForm.applicantStatus?.key, DT_RowId: admissionForm.id, serialNo:admissionForm.serialNo, applyNo: admissionForm.applyNo, 0: serial, 1: imageUrl, 2:admissionForm.serialNo, 3: admissionForm.name, 4: admissionForm.className?.name, 5: admissionForm.fathersName, 6: admissionForm.mobile, 7:  CommonUtils.getUiDateStr(admissionForm.birthDate),8: admissionForm.applyNo, 9:''])
            }
        }
        return [totalCount: totalCount, results: dataReturns]
    }

    static final String[] sortadmitColumns = ['id','id','formNo', 'name','className', 'fathersName', 'mobile', 'birthDate','applyNo']
    LinkedHashMap applicantsAdmitPaginateList(GrailsParameterMap params) {
        println(params)
        int iDisplayStart = params.iDisplayStart ? params.getInt('iDisplayStart') : CommonUtils.DEFAULT_PAGINATION_START
        int iDisplayLength = params.iDisplayLength ? params.getInt('iDisplayLength') : CommonUtils.DEFAULT_PAGINATION_LENGTH
        String sSortDir = params.sSortDir_0 ? params.sSortDir_0 : CommonUtils.SORT_ORDER_DESC
        int iSortingCol = params.iSortCol_0 ? params.getInt('iSortCol_0') : CommonUtils.DEFAULT_PAGINATION_SORT_IDX
        String sSearch = params.sSearch ? params.sSearch : null
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }
        ClassName className = params.className ? ClassName.read(params.className) : null
        ApplicantStatus applicantStatus = ApplicantStatus.AdmitCard
        if (params.applicantStatus){
            applicantStatus = ApplicantStatus.valueOf(params.applicantStatus)
        }
        AcademicYear academicYear = schoolService.schoolAdmissionYear()

        Date fromDate
        Date toDate

        if (params.startDate) {
            fromDate = CommonUtils.getUiPickerDate(params.startDate)
        } else {
            fromDate = new Date().clearTime()
        }

        if (params.endDate) {
            toDate = CommonUtils.getUiPickerDate(params.endDate, false)
        } else {
            toDate = new Date().plus(1).clearTime()
        }

        String sortColumn = CommonUtils.getSortColumn(sortadmitColumns, iSortingCol)
        List dataReturns = new ArrayList()
        def c = RegOnlineRegistration.createCriteria()
        def results = c.list(max: iDisplayLength, offset: iDisplayStart) {
            and {
                if (className) {
                    eq("className", className)
                }
                if (applicantStatus) {
                    eq("applicantStatus", applicantStatus)
                }
                between("invoiceDate", fromDate, toDate)
                eq("academicYear", academicYear)
                eq("activeStatus", ActiveStatus.ACTIVE)

            }
            if (sSearch) {
                or {
                    ilike('name', sSearch)
                    ilike('serialNo', sSearch)
                    ilike('applyNo', sSearch)
                    ilike('fathersName', sSearch)
                    ilike('mobile', sSearch)
                }
            }
            order("invoiceDate",CommonUtils.SORT_ORDER_DESC)
            order(sortColumn, sSortDir)

        }
        int totalCount = results.totalCount
        int serial = iDisplayStart;
        if (totalCount > 0) {
            if (sSortDir.equals(CommonUtils.SORT_ORDER_DESC)) {
                serial = (totalCount + 1) - iDisplayStart
            }
            boolean hasRight = hasSelectRight()
            results.each { admissionForm ->
                if (sSortDir.equals(CommonUtils.SORT_ORDER_ASC)) {
                    serial++
                } else {
                    serial--
                }
                dataReturns.add([hasRight: hasRight, applicantStatus: admissionForm.applicantStatus?.key, DT_RowId: admissionForm.id, serialNo:admissionForm.serialNo, applyNo: admissionForm.applyNo, 0: serial, 1: admissionForm.className?.name, 2:admissionForm.serialNo, 3: admissionForm.name, 4: admissionForm.fathersName, 5: admissionForm.mobile, 6: CommonUtils.getUiDateStr(admissionForm.birthDate), 7:admissionForm.payment ,8: admissionForm.applyNo, 9:CommonUtils.getUiDateStr(admissionForm.invoiceDate),10:''])
            }
        }
        return [totalCount: totalCount, results: dataReturns]
    }

    static final String[] sortApplicantColumns = ['id','id','className', 'name', 'fathersName', 'mobile', 'birthDate','formNo','applyNo','invoiceNo']
    LinkedHashMap collectionPaginateList(GrailsParameterMap params) {
        int iDisplayStart = params.iDisplayStart ? params.getInt('iDisplayStart') : CommonUtils.DEFAULT_PAGINATION_START
        int iDisplayLength = params.iDisplayLength ? params.getInt('iDisplayLength') : CommonUtils.DEFAULT_PAGINATION_LENGTH
        String sSortDir = params.sSortDir_0 ? params.sSortDir_0 : CommonUtils.SORT_ORDER_DESC
        int iSortingCol = params.iSortCol_0 ? params.getInt('iSortCol_0') : CommonUtils.DEFAULT_PAGINATION_SORT_IDX
        String sSearch = params.sSearch ? params.sSearch : null
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }
        ClassName className = params.className ? ClassName.read(params.className) : null
        ApplicantStatus applicantStatus = ApplicantStatus.Apply
        if (params.applicantStatus){
            applicantStatus = ApplicantStatus.valueOf(params.applicantStatus)
        }
        AcademicYear academicYear = schoolService.schoolAdmissionYear()
        String sortColumn = CommonUtils.getSortColumn(sortApplicantColumns, iSortingCol)
        def g = grailsApplication.mainContext.getBean('org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib')
        String imageUrl
        List dataReturns = new ArrayList()
        def c = RegOnlineRegistration.createCriteria()
        def results = c.list(max: iDisplayLength, offset: iDisplayStart) {
            and {
                if (className) {
                    eq("className", className)
                }
                if (applicantStatus) {
                    eq("applicantStatus", applicantStatus)
                }
                eq("academicYear", academicYear)
                eq("activeStatus", ActiveStatus.ACTIVE)
            }
            if (sSearch) {
                or {
                    ilike('name', sSearch)
                    ilike('serialNo', sSearch)
                    ilike('applyNo', sSearch)
                    ilike('fathersName', sSearch)
                    ilike('mobile', sSearch)
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
            results.each { admissionForm ->
                if (sSortDir.equals(CommonUtils.SORT_ORDER_ASC)) {
                    serial++
                } else {
                    serial--
                }

                dataReturns.add([applicantStatus: admissionForm.applicantStatus?.key, DT_RowId: admissionForm.id, serialNo:admissionForm.serialNo, applyNo: admissionForm.applyNo, 0: serial, 1: admissionForm.invoiceNo, 2:admissionForm.serialNo, 3: admissionForm.name, 4: admissionForm.className?.name, 5: admissionForm.fathersName, 6: admissionForm.mobile, 7:  CommonUtils.getUiDateStr(admissionForm.birthDate),8: admissionForm.applyNo, 9:''])
            }
        }
        return [totalCount: totalCount, results: dataReturns]
    }
    RegOnlineRegistration getRegistration(String refNo) {
        def query = RegOnlineRegistration.where {
            if (refNo.startsWith("SL")){
                activeStatus == ActiveStatus.ACTIVE && serialNo == refNo
            } else {
                activeStatus == ActiveStatus.ACTIVE && applyNo == refNo
            }
        }
        RegOnlineRegistration registration = query.find()
        return registration
    }

    def typeAheadList(GrailsParameterMap parameterMap){
        String sSearch = parameterMap.q
        ApplicantStatus applicantStatus
        if (parameterMap.applicantStatus) {
            applicantStatus = ApplicantStatus.valueOf(parameterMap.applicantStatus)
        }
        ClassName className
        if (parameterMap.className){
            className = ClassName.read(parameterMap.getLong("className"))
        }

        AcademicYear academicYear = schoolService.schoolAdmissionYear()
        if (parameterMap.academicYear){
            academicYear = AcademicYear.valueOf(parameterMap.academicYear)
        }

        List dataReturns = new ArrayList()


        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }
        def c = RegOnlineRegistration.createCriteria()
        def results = c.list() {
            and {
                eq("academicYear", academicYear)
                if (applicantStatus) {
                    eq("applicantStatus", applicantStatus)
                }
                if (className) {
                    eq("className", className)
                }
                eq("activeStatus", ActiveStatus.ACTIVE)
            }
            if (sSearch) {
                or {
                    ilike('serialNo', sSearch)
                    ilike('applyNo', sSearch)
                    ilike('mobile', sSearch)
                    ilike('name', sSearch)
                }
            }
            order("serialNo",CommonUtils.SORT_ORDER_ASC)
        }
        for (obj in results) {
            dataReturns.add([id: obj.id, name: "${obj.serialNo} - ${obj.name} - ${obj.fathersName} - ${obj.mobile}"])
        }
        return dataReturns
    }
    boolean hasSelectRight(){
        if(schoolService.runningSchool() == CommonUtils.NARAYANGANJ_IDEAL_SCHOOL) {
            if (SpringSecurityUtils.ifAnyGranted("ROLE_SCHOOL_HEAD")){
                return true
            }
            return false
        }
        return true
    }

}
