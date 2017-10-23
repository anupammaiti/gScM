package com.grailslab
import com.grailslab.enums.ApplicantStatus
import com.grailslab.gschoolcore.AcademicYear
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.settings.ClassName
import com.grailslab.settings.School
import com.grailslab.stmgmt.RegOnlineRegistration
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

class RegAdmissionFormService {

    def springSecurityService
    def messageSource
    def schoolService

    static transactional = false

    static final String[] sortColumns = ['name']


    def regOnlineRegistrationList(ClassName className, ApplicantStatus applicantStatus, Date fromDate, Date toDate, AcademicYear academicYear){
        def c = RegOnlineRegistration.createCriteria()
        def results = c.list() {
            createAlias('className', 'cls')
            and {
                eq("academicYear", academicYear)
                eq("activeStatus", ActiveStatus.ACTIVE)
                if(applicantStatus) {
                    eq("applicantStatus", applicantStatus)
                }
                if (className) {
                    eq("className", className)
                }
                if (fromDate && toDate) {
                    between("dateApplication", fromDate, toDate)
                }
            }
            order("cls.sortPosition",CommonUtils.SORT_ORDER_ASC)
            order("id",CommonUtils.SORT_ORDER_ASC)
        }
        return results
    }
    def regOnlineCollectionList(ClassName className, ApplicantStatus applicantStatus, Date fromDate, Date toDate, AcademicYear academicYear){
        def c = RegOnlineRegistration.createCriteria()
        def results = c.list() {
            createAlias('className', 'cls')
            and {
                eq("academicYear", academicYear)
                eq("activeStatus", ActiveStatus.ACTIVE)
                if(applicantStatus) {

                    eq("applicantStatus", applicantStatus)
                }
                if (className) {
                    eq("className", className)
                }
                between("invoiceDate", fromDate, toDate)
            }
            order("invoiceDate",CommonUtils.SORT_ORDER_ASC)
        }
        return results
    }
}