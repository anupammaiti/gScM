package com.grailslab.admin

import com.grailslab.enums.AcaYearType
import com.grailslab.gschoolcore.AcademicYear

class CollegeController {

    def studentService
    def schoolService
    def sectionService
    def classNameService
    def messageSource
    def registrationService
    def studentSubjectsService
    def classSubjectsService
    def index() {
        AcademicYear academicYear = schoolService.collegeWorkingYear()
        def academicYearList = schoolService.academicYears(AcaYearType.COLLEGE)
        params.academicYear = academicYear.key
        LinkedHashMap resultMap = studentService.studentPaginateList(params)
        def classList = classNameService.classNameDropDownList(AcaYearType.COLLEGE)
        int totalCount = resultMap.totalCount
        if (!resultMap || totalCount == 0) {
            render(view: '/admin/studentMgmt/student', model: [academicYearList:academicYearList, workingYear:academicYear, dataReturn: null, totalCount: 0,classList:classList])
            return
        }
        render(view: '/admin/studentMgmt/student', model: [academicYearList:academicYearList, workingYear:academicYear, dataReturn: resultMap.results, totalCount: totalCount,classList:classList])
    }

    def admission() {
        AcademicYear academicYear = schoolService.collegeWorkingYear()
        params.academicYear = academicYear.key
        LinkedHashMap resultMap = studentService.studentPaginateList(params)
        def classList = classNameService.classNameDropDownList(AcaYearType.COLLEGE)
        def newStudents=registrationService.newRegistrationDropDownList()
        int totalCount = resultMap.totalCount
        if (!resultMap || totalCount == 0) {
            render(view: '/admin/studentMgmt/admission', model: [newStudents:newStudents,classList:classList, workingYear:academicYear, dataReturn: null, totalCount: 0])
            return
        }
        render(view: '/admin/studentMgmt/admission', model: [newStudents:newStudents,classList:classList, workingYear:academicYear, dataReturn: resultMap.results, totalCount: totalCount])
    }

    def reAdmission(){
        def academicYearList = schoolService.academicYears(AcaYearType.COLLEGE)
        def classNameList = classNameService.classNameDropDownList(AcaYearType.COLLEGE)
        render(view: '/admin/studentMgmt/reAdmission', model: [classNameList:classNameList, academicYearList: academicYearList])
    }
    def promotion(){
        def academicYearList = schoolService.academicYears(AcaYearType.COLLEGE)
        def classNameList = classNameService.classNameDropDownList(AcaYearType.COLLEGE)
        render(view: '/admin/studentMgmt/promotion', model: [classNameList:classNameList, academicYearList: academicYearList])
    }






}
