package com.grailslab

import com.grailslab.enums.AcaYearType
import com.grailslab.enums.CalenderMonths
import com.grailslab.enums.StudentStatus
import com.grailslab.gschoolcore.AcademicYear
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.hr.Employee
import com.grailslab.settings.ClassName
import com.grailslab.settings.School
import com.grailslab.stmgmt.Student
import org.codehaus.groovy.grails.web.context.ServletContextHolder as SCH
import org.json.JSONObject

class SchoolService {
    static transactional = false
    def springSecurityService
    def grailsApplication
    def sequenceGeneratorService
    def studentService

    School defaultSchool(){
        Long DEFAULT_SCHOOL_ID = CommonUtils.DEFAULT_SCHOOL_ID
        School school = School.read(DEFAULT_SCHOOL_ID)
        return school
    }

    def workingYear(ClassName className = null) {
        if (className && className.isCollegeSection) {
             return AcademicYear.Y2017_2018
        } else {
            return AcademicYear.Y2017
        }
    }

    //also need to update MyUserDetails academic Year
    def previousSchoolYear() {
        AcademicYear.Y2016
    }

    def schoolWorkingYear() {
        AcademicYear.Y2017
    }
    AcademicYear schoolAdmissionYear() {
        AcademicYear.Y2017
    }

    def collegeWorkingYear() {
        AcademicYear.Y2017_2018
    }
    def workingYears() {
        [schoolWorkingYear(), collegeWorkingYear()] as List
    }
    String workingYearsStr() {
        return [schoolWorkingYear().key, collegeWorkingYear().key].join(",")
    }

    def stdEmpWorkingYear() {
        return [schoolWorkingYear().key, collegeWorkingYear().key, "all"]
    }

    def academicYears(AcaYearType yearType = null){
        if (!yearType) return AcademicYear.schoolYears()
        if (yearType == AcaYearType.SCHOOL){
            return AcademicYear.schoolYears()
        } else if (yearType == AcaYearType.COLLEGE) {
            return AcademicYear.collegeYears()
        } else {
            return AcademicYear.values()
        }
    }

    public String getUserRef(){
        def principal = springSecurityService?.principal
        String userRefStr = principal.userRef ? principal.userRef : principal.username
        return userRefStr
    }
    String reportLogoPath(){
        String schoolName = grailsApplication.config.grailslab.gschool.running
        String imageFolder = SCH.servletContext.getRealPath('/images') + File.separator
        if (schoolName) {
            imageFolder += schoolName + File.separator
        }
        return imageFolder
    }
    public String runningSchool(){
        return grailsApplication.config.grailslab.gschool.running
    }

    public String reportFileName(String reportName){
        String schoolName = grailsApplication.config.grailslab.gschool.running
        return schoolName+File.separator+reportName
    }
    public String schoolSubreportDir() {
        String schoolName = grailsApplication.config.grailslab.gschool.running
        return SCH.servletContext.getRealPath('/reports') + File.separator+ schoolName + File.separator
    }

    //module wise sub folder for reports
    public String reportFileName(String module, String reportName){
        return module+File.separator+reportName
    }
    public String subreportDir(String module) {
        return SCH.servletContext.getRealPath('/reports') + File.separator+ module + File.separator
    }
    public String subreportDir() {
        return SCH.servletContext.getRealPath('/reports') + File.separator
    }
    public String imageDir() {
        return SCH.servletContext.getRealPath('/images') + File.separator
    }
    public String imageDir(String runningSchool) {
        String imageFolder = SCH.servletContext.getRealPath('/images') + File.separator
        if (runningSchool) {
            imageFolder += runningSchool + File.separator
        }
        return imageFolder
    }
    String nextOnlineFormNo() {
        return sequenceGeneratorService.nextNumber('onlineFormNo')
    }

    /*String nextInvoiceNo(){
        String invoiceNo
        try {
            invoiceNo = sequenceGeneratorService.nextNumber('InvoiceNo')
        }catch (Exception e) {
            e.printStackTrace()
        }
        if(!invoiceNo){
            invoiceNo = 'INV-' + new Date().getTime()
        }
        return invoiceNo
    }*/

    def monthName(){
        return CalenderMonths.values()
    }

    Student loggedInStudent() {
        def principal = springSecurityService?.principal
        String userRefStr = principal.userRef
        if (userRefStr) {
            return Student.findByAcademicYearAndStudentStatusAndStudentID(schoolWorkingYear(), StudentStatus.NEW, userRefStr)
        }
       return null
    }

    Employee loggedInEmployee() {
        def principal = springSecurityService?.principal
        String userRefStr = principal.userRef
        if (userRefStr) {
            return Employee.findByEmpIDAndActiveStatus(userRefStr, ActiveStatus.ACTIVE)
        }
        return null
    }

    def getJsonValue(JSONObject json, String key) {
        if(json.has(key)) {
            return json[key]
        } else {
            return null
        }
    }



}
