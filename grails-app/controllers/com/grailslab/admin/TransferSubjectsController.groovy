package com.grailslab.admin

import com.grailslab.CommonUtils
import com.grailslab.command.AddTransferSubjectCommand
import com.grailslab.command.TransferSubjectCommand
import com.grailslab.enums.AcaYearType
import com.grailslab.gschoolcore.AcademicYear
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.settings.ClassName
import com.grailslab.settings.StudentSubjects
import com.grailslab.stmgmt.Student
import grails.converters.JSON

class TransferSubjectsController {
    def schoolService
    def classNameService
    def classSubjectsService
    def studentSubjectsService
    def studentService

    def index() {
        def academicYearList = schoolService.academicYears(AcaYearType.SCHOOL)
        def classNameList = classNameService.classNameDropDownList()
        render(view: '/admin/studentSubject/transferSubjectPage', model: [classNameList:classNameList, academicYearList: academicYearList])

    }

    def loadSubjects(TransferSubjectCommand command){
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        ClassName className = command.className
        if (!command.academicYear || !className) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "Please select Academic year class name to load student subjects")
            outPut = result as JSON
            render outPut
            return
        }

        def optComOptions = classSubjectsService.subjectChooseOptComDDList(className, command.sectionName?.groupName)
        if (!optComOptions) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "${className.name} has No Optional/Alternative subject to map.")
            outPut = result as JSON
            render outPut
            return
        }
        def studentList = studentService.listStudent(command.academicYear, className, command.sectionName, command.gender, command.religion)
        List studentSubjectsList = new ArrayList()
        String subjects
        def results
        int serial = 1
        for (student in studentList) {
            results = studentSubjectsService.alternativeSubject(student)
            subjects = results? results.collect {it.subject.name}.join(", "):""
            studentSubjectsList.add([id: student.id, serial: serial++, name: student.name, studentId: student.studentID, rollNo: student.rollNo, subjects: subjects])
        }
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put('studentList', studentSubjectsList)
        result.put('optComOptions', optComOptions)
        result.put('allowOptionalSubject', className.allowOptionalSubject)
        outPut = result as JSON
        render outPut
    }
    def saveStuTransferedSubject(AddTransferSubjectCommand command){
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        if(!command.selectedStdId){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "Please select student to transfer subject")
            outPut = result as JSON
            render outPut
            return
        }

        String academicYear = command.academicYear.value
        int year
        AcademicYear nextYear
        if (academicYear.length() == 4) {
            year = Integer.parseInt(academicYear)
            year = year +1
            nextYear = AcademicYear.getYearByString(year.toString())
        } else {
            year = Integer.parseInt(academicYear.substring(0, 4))
            int lowYear = year+1
            int highYear = year+2
            nextYear = AcademicYear.getYearByString(lowYear+"-"+highYear)
        }
        if(!nextYear){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "${year} not initiated yet. Please contact admin")
            outPut = result as JSON
            render outPut
            return
        }
        def selectedIds = command.selectedStdId.split(',')
        Student student
        Student newStudent
        def oldSubjects
        long count=0

        List<String> notFoundStudentIds = new ArrayList()
        selectedIds.each {idx ->
            student = Student.read(idx.toLong())
            newStudent = studentService.findByStudentId(student.studentID, nextYear)
            if (newStudent) {
                oldSubjects = studentSubjectsService.alternativeSubject(student, true)
                for (studentSub in oldSubjects) {
                    count++
                    if(StudentSubjects.countByActiveStatusAndStudentAndSubject(ActiveStatus.ACTIVE, newStudent, studentSub.subject) == 0){
                        new StudentSubjects(student: newStudent, subject: studentSub.subject, subjectType: studentSub.subjectType, isOptional: studentSub.isOptional, parentSubId: studentSub.parentSubId, academicYear: nextYear).save()
                    }
                }
            } else {
                notFoundStudentIds.add(student.studentID)
            }
        }
        String message = "Subjects transfered successfully for "+count+" selected Students."

        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, message)
        outPut = result as JSON
        render outPut
        return
    }
}
