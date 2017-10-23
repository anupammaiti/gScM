package com.grailslab.admin

import com.grailslab.CommonUtils
import com.grailslab.command.StudentSubjectCommand
import com.grailslab.enums.SubjectType
import com.grailslab.gschoolcore.AcademicYear
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.settings.*
import com.grailslab.stmgmt.Student
import grails.converters.JSON

class StudentSubjectsController {
    def sectionService
    def classSubjectsService
    def studentSubjectsService
    def subjectService
    def classNameService
    def messageSource
    def schoolService

    def index() {
        def classNameList = classNameService.allClassNames()
        if (!classNameList) {
            render (view: '/admin/studentSubject/studentSubjectPage', model: [dataReturns:null])
            return
        }
        def allSection = sectionService.allSections(params)
        List dataReturns = new ArrayList()
        List subjectNames
        String subjects
        int totalCount = 0
        allSection.each { Section section ->
            totalCount++
            subjectNames =  classSubjectsService.subjectsNameListForClassSubject(section.className, section.groupName)
            if(subjectNames){
                subjects = subjectNames.join(', ')
                dataReturns.add([DT_RowId:section.id, 0: totalCount, 1: section.className?.name, 2:section.name, 3:subjects, 4:''])
            } else {
                dataReturns.add([DT_RowId:section.id, 0: totalCount, 1: section.className?.name, 2:section.name, 3:'No Subject', 4:''])
            }
        }
        render (view: '/admin/studentSubject/studentSubjectPage', model: [classNameList:classNameList, dataReturns:dataReturns, totalCount:totalCount])
    }


    def list() {
        LinkedHashMap gridData
        String result
        ClassName className
        if (params.className){
            className = ClassName.read(params.getLong('className'))
        }
        def allSection
        if ( className) {
            allSection = sectionService.classSections(className)
        } else {
            allSection = sectionService.allSections(params)
        }

        List dataReturns = new ArrayList()
        List subjectNames
        String subjects
        int totalCount = 0
        allSection.each { Section section ->
            totalCount++
            subjectNames =  classSubjectsService.subjectsNameListForClassSubject(section.className, section.groupName)
            if(subjectNames){
                subjects = subjectNames.join(', ')
                dataReturns.add([DT_RowId:section.id, 0: totalCount, 1: section.className?.name, 2:section.name, 3:subjects, 4:''])
            } else {
                dataReturns.add([DT_RowId:section.id, 0: totalCount, 1: section.className?.name, 2:section.name, 3:'No Subject', 4:''])
            }
        }
        if( totalCount== 0){
            gridData = [iTotalRecords: 0, iTotalDisplayRecords: 0, aaData: []]
            result = gridData as JSON
            render result
            return
        }
        gridData = [iTotalRecords: totalCount, iTotalDisplayRecords: totalCount, aaData: dataReturns]
        result = gridData as JSON
        render result
    }

    def subjects(Long id) {
        Section section = Section.read(id)
        if(!section){
            redirect(action: 'index')
        }
        def resultMap =  studentSubjectsService.studentSubjectsList(section)
        def compulsorySubjectStr = classSubjectsService.subjectTypeCompulsoryListStr(section.className, section.groupName)
        render (view: '/admin/studentSubject/studentSubjectMapping', model: [dataReturn: resultMap, className:section.className?.name, sectionName: section.name, sectionId: section.id, compulsorySubjectStr: compulsorySubjectStr])
    }


    def edit(Long id) {
        Section section = Section.read(id)
        if (!section) {
            redirect(action: 'index')
            return
        }
        Long studentId = params.getLong('studentId')

        def studentSubjectList = studentSubjectsService.studentSubjectsListForEdit(section, studentId)
        ClassName className = section.className
        def optComOptions = classSubjectsService.subjectChooseOptComDDList(className, section.groupName)
        def compulsorySubjectStr = classSubjectsService.subjectTypeCompulsoryListStr(className, section.groupName)
        render(view: '/admin/studentSubject/editSubjectMapping', model: [optComOptions:optComOptions, className:className?.name, allowOptionalSubject: className.allowOptionalSubject, sectionName: section.name, sectionId: section.id, compulsorySubjectStr: compulsorySubjectStr, studentSubjectList: studentSubjectList])
    }
    def saveAlternative (){
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut

        Student student = Student.read(params.getLong('id'))
        SubjectName subjectName = SubjectName.read(params.getLong('subjectId'))
        if(!student || !subjectName){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        Boolean added = params.getBoolean('added')
        Boolean isOptional = params.getBoolean('isOptional')
        StudentSubjects studentSubjects
        if(added){
            studentSubjects = StudentSubjects.findByStudentAndSubjectAndActiveStatus(student, subjectName, ActiveStatus.ACTIVE)
            if (studentSubjects){
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, "Subject Already added")
                outPut = result as JSON
                render outPut
                return
            }
            ClassSubjects classSubjects = ClassSubjects.findByActiveStatusAndClassNameAndSubject(ActiveStatus.ACTIVE, student.className, subjectName)
            if (!classSubjects){
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, "Subject is not mapping with class")
                outPut = result as JSON
                render outPut
                return
            }
            if (classSubjects.subjectType == SubjectType.INUSE) {
                ClassSubjects parentSubject = ClassSubjects.read(classSubjects.parentSubId)
                if (!parentSubject) {
                    result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                    result.put(CommonUtils.MESSAGE, "Subject is not allowed for this class")
                    outPut = result as JSON
                    render outPut
                    return
                }
                if (parentSubject.subjectType == SubjectType.ALTERNATIVE) {
                    studentSubjects = StudentSubjects.findByActiveStatusAndStudentAndParentSubId(ActiveStatus.ACTIVE, student, classSubjects.parentSubId)
                    if (studentSubjects){
                        result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                        result.put(CommonUtils.MESSAGE, "Another Subject of same group already added. Please remove that first")
                        outPut = result as JSON
                        render outPut
                        return
                    }
                }
                studentSubjects = new StudentSubjects(student: student, subject: subjectName, subjectType: SubjectType.INUSE, isOptional: isOptional, parentSubId: classSubjects.parentSubId).save()
            } else {
                studentSubjects = new StudentSubjects(student: student, subject: subjectName, subjectType: SubjectType.OPTIONAL, isOptional: isOptional).save()
            }
            result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
            outPut = result as JSON
            render outPut
            return
        }
        studentSubjects = StudentSubjects.findByStudentAndSubjectAndActiveStatus(student, subjectName, ActiveStatus.ACTIVE)
        if (!studentSubjects){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "Subject not added")
            outPut = result as JSON
            render outPut
            return
        }
        studentSubjects.activeStatus = ActiveStatus.DELETE
        studentSubjects.save()
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        outPut=result as JSON
        render outPut
    }
    def commonMapping(Long id){
        ClassName className = ClassName.read(id)
        def classNameList = classNameService.classNameDropDownList()
        def sectionList = sectionService.classSectionsDDList(className)
        render(view: '/admin/studentSubject/commonSubjectMapping', model: [classNameList:classNameList, className: className, sectionList: sectionList])
    }
    def loadStudentCommonMapping(StudentSubjectCommand command){
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        ClassName className = command.className
        if (!className || className.activeStatus != ActiveStatus.ACTIVE) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
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
        AcademicYear academicYear = schoolService.workingYear(className)
        def studentSubjectsList = studentSubjectsService.studentSubjectsListCommonMapping(academicYear, className, command.sectionName, command.gender, command.religion)
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put('studentList', studentSubjectsList)
        result.put('optComOptions', optComOptions)
        result.put('allowOptionalSubject', className.allowOptionalSubject)
        outPut = result as JSON
        render outPut
    }

    def saveCommonMapping(StudentSubjectCommand command){
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        if(!command.subjectName){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        def selectedIds = command.selectedStdId.split(',')
        Student student
        StudentSubjects studentSubjects
        ClassSubjects classSubjects
        selectedIds.each {idx ->
            student = Student.read(idx.toLong())
            if(command.added){
                studentSubjects = StudentSubjects.findByStudentAndSubjectAndActiveStatus(student, command.subjectName, ActiveStatus.ACTIVE)
                if (!studentSubjects){
                    classSubjects = ClassSubjects.findByActiveStatusAndClassNameAndSubject(ActiveStatus.ACTIVE, student.className, command.subjectName)
                    if (classSubjects){
                        if (classSubjects.subjectType == SubjectType.INUSE) {
                            ClassSubjects parentSubject = ClassSubjects.read(classSubjects.parentSubId)
                            if (parentSubject.subjectType == SubjectType.ALTERNATIVE) {
                                studentSubjects = StudentSubjects.findByActiveStatusAndStudentAndParentSubId(ActiveStatus.ACTIVE, student, classSubjects.parentSubId)
                                if (!studentSubjects){
                                    studentSubjects = new StudentSubjects(student: student, subject: command.subjectName, subjectType: SubjectType.INUSE, isOptional: command.isOptional, parentSubId: classSubjects.parentSubId).save()
                                }
                            } else {
                                studentSubjects = new StudentSubjects(student: student, subject: command.subjectName, subjectType: SubjectType.INUSE, isOptional: command.isOptional, parentSubId: classSubjects.parentSubId).save()
                            }

                        } else {
                            studentSubjects = new StudentSubjects(student: student, subject: command.subjectName, subjectType: SubjectType.OPTIONAL, isOptional: command.isOptional).save()
                        }
                    }
                }
            } else {
                studentSubjects = StudentSubjects.findByStudentAndSubjectAndActiveStatus(student, command.subjectName, ActiveStatus.ACTIVE)
                if (studentSubjects){
                    studentSubjects.activeStatus = ActiveStatus.DELETE
                    studentSubjects.save()
                }
            }
        }
        String message = "Subject Removed successfully for selected Students."
        if (command.added) {
            message = "Subject Added successfully for selected Students."
        }
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, message)
        outPut = result as JSON
        render outPut
        return
    }
}
