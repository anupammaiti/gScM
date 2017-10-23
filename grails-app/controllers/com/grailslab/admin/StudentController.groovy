package com.grailslab.admin

import com.grailslab.CommonUtils
import com.grailslab.accounting.FeePayments
import com.grailslab.attn.AttnRecordDay
import com.grailslab.attn.AttnStdRecord
import com.grailslab.command.*
import com.grailslab.enums.*
import com.grailslab.gschoolcore.AcademicYear
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.settings.ClassName
import com.grailslab.settings.Exam
import com.grailslab.settings.Section
import com.grailslab.settings.ShiftExam
import com.grailslab.stmgmt.Registration
import com.grailslab.stmgmt.Student
import com.grailslab.stmgmt.TcAndDropOut
import grails.converters.JSON

class StudentController {

    def studentService
    def schoolService
    def sectionService
    def classNameService
    def messageSource
    def registrationService
    def studentSubjectsService
    def classSubjectsService
    def examScheduleService
    def shiftExamService
    def examService
    def tabulationService
    def tabulationCtService


    def index() {
        AcademicYear academicYear = schoolService.workingYear()
        def academicYearList = schoolService.academicYears()
        def classList = classNameService.classNameDropDownList()
        render(view: '/admin/studentMgmt/student', model: [academicYearList:academicYearList, workingYear:academicYear, classList:classList])
    }

    def admission() {
        AcademicYear academicYear = schoolService.schoolAdmissionYear()
        def classList = classNameService.classNameDropDownList()

        def newStudents=registrationService.newRegistrationDropDownList()

        render(view: '/admin/studentMgmt/admission', model: [newStudents:newStudents,classList:classList, workingYear:academicYear])
    }

    def admission2() {
        AcademicYear academicYear = schoolService.schoolAdmissionYear()
        LinkedHashMap resultMap = studentService.studentPaginateList(params)
        def classList = classNameService.classNameDropDownList()
        int totalCount = resultMap.totalCount
        if (!resultMap || totalCount == 0) {
            render(view: '/admin/studentMgmt/admission2', model: [classList:classList, workingYear:academicYear, dataReturn: null, totalCount: 0])
            return
        }
        render(view: '/admin/studentMgmt/admission2', model: [classList:classList, workingYear:academicYear, dataReturn: resultMap.results, totalCount: totalCount])
    }

    def report(){
        def classNameList = classNameService.classNameDropDownList()
        render(view: '/admin/studentMgmt/stdMgmtReport', model: [classNameList: classNameList])
    }

    def registrationTypeAheadList(){
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        String sSearch = params.q
        def studentList = registrationService.newRegistrationDropDownList2(sSearch)
        result.put('items', studentList)
        outPut = result as JSON
        render outPut
    }

    def saveAdmission(StudentCommand studentCommand) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        if (studentCommand.hasErrors()) {
            def errorList = studentCommand?.errors?.allErrors?.collect { messageSource.getMessage(it, null) }
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, errorList?.join('\n'))
            outPut = result as JSON
            render outPut
            return
        }
        int stdList = 0
        if (!studentCommand.id){
            stdList=Student.countByAcademicYearAndSectionAndRollNoAndStudentStatus(studentCommand.academicYear,studentCommand.section,studentCommand.rollNo,StudentStatus.NEW)
        }
        if (stdList > 0){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, 'Roll Number Already Exist, Pleas Try Another')
            outPut = result as JSON
            render outPut
            return
        }
        Student student
        Section section=studentCommand.section
        Registration registration
        ClassName className=section.className
        Student savedStudent
        if (params.id) {
            student = Student.get(studentCommand.id)
            if (!student) {
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
                outPut = result as JSON
                render outPut
                return
            }
            registration=student.registration
            student.academicYear=studentCommand.academicYear
            student.section=studentCommand.section
            student.rollNo=studentCommand.rollNo
            student.className = className
            student.admissionMonth=studentCommand.admissionMonth
            if (!student.save()) {
                def errorList = studentCommand?.errors?.allErrors?.collect { messageSource.getMessage(it, null) }
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, errorList?.join('.'))
                outPut = result as JSON
                render outPut
                return
            }
            registration.studentStatus=StudentStatus.ADMISSION
            registration.save()
            result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
            result.put(CommonUtils.MESSAGE,'Student Updated successfully')
            outPut=result as JSON
            render outPut
            return
        }

        student = new Student(studentCommand.properties)
        registration=studentCommand.registration
        student.className = className
        student.name = registration.name
        student.studentID = registration.studentID
        if (!student.validate()) {
            def errorList = student?.errors?.allErrors?.collect { messageSource.getMessage(it, null) }
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, errorList?.join('\n'))
            outPut = result as JSON
            render outPut
            return
        }
        registration.studentStatus=StudentStatus.ADMISSION
        registration.save()
        savedStudent = student.save(flush: true)
        if (!savedStudent) {
            def errorList = savedStudent?.errors?.allErrors?.collect { messageSource.getMessage(it, null) }
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, errorList?.join('\n'))
            outPut = result as JSON
            render outPut
            return
        }

        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE,'Student Create successfully')
        result.put('rollNumber',studentCommand.rollNo+1)
        outPut=result as JSON
        render outPut
    }

    def saveStudent(StudentCommand studentCommand) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        if (studentCommand.hasErrors()) {
            def errorList = studentCommand?.errors?.allErrors?.collect { messageSource.getMessage(it, null) }
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, errorList?.join('\n'))
            outPut = result as JSON
            render outPut
            return
        }

        Section section=studentCommand.section
        Student student
        if (params.id) {
            student = Student.get(studentCommand.id)
            if (!student) {
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
                outPut = result as JSON
                render outPut
                return
            }
            Section oldSection = student.section

            student.section=section
            student.rollNo=studentCommand.rollNo
            student.admissionMonth=studentCommand.admissionMonth
            studentService.savedSectionTransfer(student, section, oldSection)

            result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
            result.put(CommonUtils.MESSAGE,'Student Updated successfully')
            outPut=result as JSON
            render outPut
            return
        }
        result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
        result.put(CommonUtils.MESSAGE,'Student not load correctly. Please contact admin')
        outPut=result as JSON
        render outPut
    }

    def saveReAdmission(StudentReAdmissionCommand command) {
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
        Student oldStudent = command.student
        if(oldStudent.promotionStatus ==PromotionStatus.PROMOTED){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "${oldStudent.name} Already Re Admit")
            outPut = result as JSON
            render outPut
            return
        }

        def stdList=Student.countByStudentStatusAndAcademicYearAndSectionAndRollNo(StudentStatus.NEW,command.academicYear,command.section,command.rollNo)
        if (stdList> 0){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, 'Roll No Already Exist. Pleas Try Another')
            outPut = result as JSON
            render outPut
            return
        }

        Section section=command.section
        Registration registration=command.student.registration
        ClassName className=section.className

        Student newStudent = new Student(name:registration.name, studentID:registration.studentID, registration:registration,section:section,className:className,rollNo:command.rollNo,academicYear: command.academicYear,admissionMonth: command.admissionMonth)
        if (!newStudent.validate()) {
            def errorList = newStudent?.errors?.allErrors?.collect { messageSource.getMessage(it, null) }
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, errorList?.join('\n'))
            outPut = result as JSON
            render outPut
            return
        }
        Student savedStudent = newStudent.save()
        if (!savedStudent) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "Error student re admission")
            outPut = result as JSON
            render outPut
            return
        }


        oldStudent.promotionStatus=PromotionStatus.PROMOTED
        oldStudent.save()

        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE,"${savedStudent.name} promoted successfully")
        result.put('rollNumber',savedStudent.rollNo+1)
        outPut=result as JSON
        render outPut
    }

    def delete(Long id) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        Student student = Student.get(id)
        if (!student || student.studentStatus !=StudentStatus.NEW) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        int studentYear = Integer.valueOf(student.academicYear.value)
        Calendar now = Calendar.getInstance();
        if (studentYear < now.get(Calendar.YEAR)) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "Can't delete old student")
            outPut = result as JSON
            render outPut
            return
        }
        if(student.promotionStatus ==PromotionStatus.PROMOTED){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "Student already promoted.")
            outPut = result as JSON
            render outPut
            return
        }
        // if student paid any fee for current academic year
        def studentPayments = FeePayments.findByStudentAndAcademicYearAndActiveStatus(student, student.academicYear, ActiveStatus.ACTIVE)
        if(studentPayments){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "Student already Paid fee. Please contact admin to delete this student.")
            outPut = result as JSON
            render outPut
            return
        }

        List<Student> previousClasses = studentService.previousClasses(student.studentID, student)
        Registration registration = student.registration
        Student lastStd
        student.studentStatus=StudentStatus.DELETED
        student.save()
        if(previousClasses){
            lastStd = previousClasses.first()
            if(lastStd){
                lastStd.promotionStatus = PromotionStatus.NEW
                lastStd.save()
            }
        }else {
            registration.studentStatus = StudentStatus.NEW
            registration.save()
        }

        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE,"Student deleted successfully.")
        outPut = result as JSON
        render outPut
    }

    def list() {
        LinkedHashMap gridData
        String result
        LinkedHashMap resultMap =studentService.studentPaginateList(params)

        if(!resultMap || resultMap.totalCount== 0){
            gridData = [iTotalRecords: 0, iTotalDisplayRecords: 0, aaData: []]
            result = gridData as JSON
            render result
            return
        }
        int totalCount =resultMap.totalCount
        gridData = [iTotalRecords: totalCount, iTotalDisplayRecords: totalCount, aaData: resultMap.results]
        result = gridData as JSON
        render result
    }


    def edit(Long id) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        Student student = Student.read(id)
        if (!student || student.studentStatus != StudentStatus.NEW) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        int studentYear = Integer.valueOf(student.academicYear.value)
        Calendar now = Calendar.getInstance();
        if (studentYear < now.get(Calendar.YEAR)) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "Can't delete old student")
            outPut = result as JSON
            render outPut
            return
        }

        if(student.promotionStatus ==PromotionStatus.PROMOTED){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "Student already promoted.")
            outPut = result as JSON
            render outPut
            return
        }
        //If any fee payment, then allow rollNo edit only
        String feePaidMsg = "Only RollNo and Section Transfer can update. For class change, please contact admin"
        String studentID=student.studentID
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put('obj',student)
        result.put('studentID',studentID)
        result.put('feePaidMsg',feePaidMsg)
        result.put('studentName',student.name)
        result.put('className',student.className.name)
        result.put('sectionName',student.section.name)
        outPut = result as JSON
        render outPut
    }

    def roleReorder(Long id){
        def academicYearList = schoolService.academicYears(AcaYearType.SCHOOL)
        def classNameList = classNameService.classNameDropDownList()
        switch (request.method) {
            case 'GET':
                ClassName className
                if(id){
                    className = ClassName.read(id)
                }
                AcademicYear academicYear
                if (params.academicYear){
                    academicYear = AcademicYear.valueOf(params.academicYear)
                }else{
                    academicYear = schoolService.schoolWorkingYear()
                }

                def resultMap
                if (className) {
                    resultMap = studentService.byClassName(className, academicYear)
                }
                render(view: '/admin/studentMgmt/roleReorder', model: [resultMap: resultMap, className:className, academicYear:academicYear, classNameList:classNameList, academicYearList:academicYearList])
                break
            case 'POST':
                Student student
                Integer newRollNo
                params.student.each{ studentId ->
                    try {
                        student = Student.get(Long.parseLong(studentId.key))
                        newRollNo = Integer.parseInt(studentId.value)
                        if (newRollNo && student.rollNo != newRollNo) {
                            student.rollNo = newRollNo
                            student.save()
                        }
                    }catch (e){
                        log.error(e.message)
                    }
                }
                flash.message = "Student Roll(s) Updated Successfully"
                render(view: '/admin/studentMgmt/roleReorder', model: [academicYearList:academicYearList, classNameList: classNameList])
                break
        }
    }

    def examStudentList(Long id){
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut

        Exam exam = Exam.read(id)
        String message
        if(!exam || exam.activeStatus != ActiveStatus.ACTIVE){
            message =CommonUtils.COMMON_NOT_FOUND_MESSAGE
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, message)
            outPut = result as JSON
            render outPut
            return
        }
        if(exam.examStatus == ExamStatus.NEW){
            message ="Result not ready"
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, message)
            outPut = result as JSON
            render outPut
            return
        }
        def studentList = studentService.sectionStudentDDList(exam.section)
        if(!studentList){
            message ="No Student for this Section and Academic Year."
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, message)
            outPut = result as JSON
            render outPut
            return
        }
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put('studentList', studentList)
        outPut = result as JSON
        render outPut
        return
    }

    def lastRoll(Long inputSection){
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        if (!inputSection) {
            result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
            result.put('rollNumber', 0)
            outPut = result as JSON
            render outPut
            return
        }

        Section section=Section.read(inputSection)
        AcademicYear inputAcademicYear
        if (params.inputAcademicYear) {
            inputAcademicYear = AcademicYear.valueOf(params.inputAcademicYear)
        } else {
            inputAcademicYear = schoolService.workingYear(section?.className)
        }
        Integer rollNumber = studentService.nextRoll(section,inputAcademicYear)
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put('rollNumber', rollNumber)
        outPut = result as JSON
        render outPut
        return
    }

    def reAdmission(){
        def academicYearList = schoolService.academicYears(AcaYearType.SCHOOL)
        def classNameList = classNameService.classNameDropDownList()
        render(view: '/admin/studentMgmt/reAdmission', model: [classNameList:classNameList, academicYearList: academicYearList])
    }

    def sectionTransfer(){
        def examNameList = shiftExamService.examNameDropDown()
        ShiftExam shiftExam
        if (examNameList) {
            def lastExam = examNameList.last()
            shiftExam = ShiftExam.read(lastExam.id)
        }
        def classNameList = classNameService.classNameInIdListDD(shiftExam?.classIds);
        switch (request.method) {
            case 'GET':
                render(view: '/admin/studentMgmt/sectionTransfer', model: [classNameList: classNameList, examNameList: examNameList, shiftExam:shiftExam])
                break
            case 'POST':
                List sectionList=new ArrayList()
                if (params.sectionIds){
                    if (params.sectionIds instanceof String){
                        sectionList.add(params.sectionIds)
                    }else {
                        sectionList = params.sectionIds
                    }
                }
                ClassName className = ClassName.read(params.getLong('classNameId'))
                ShiftExam shiftExamName = ShiftExam.read(params.getLong('shiftExamId'))

                GroupName groupName
                if (params.groupNameVal){
                    groupName = GroupName.valueOf(params.groupNameVal)
                }
                String examTypeVal = params.examTypeVal

                def clsExamIds = examService.classExamIdList(shiftExam, className, groupName)
                Section newSection
                Section oldSection
                int startNum
                int endNum
                int limitNum
                def transferStdList
                sectionList.each {idx ->
                    newSection = Section.read(Long.valueOf(idx))
                    startNum = Integer.valueOf(params."start$idx")
                    endNum = Integer.valueOf(params."end$idx")
                    if (startNum !=0 && startNum < endNum) {
                        startNum = startNum - 1
                        limitNum = endNum - startNum
                        if (examTypeVal == "byCt") {
                            transferStdList = tabulationCtService.listStudentBasedClsPosition(className, clsExamIds, groupName, startNum, limitNum)
                        } else {
                            transferStdList = tabulationService.listStudentBasedClsPosition(className, clsExamIds, groupName, startNum, limitNum)
                        }

                        for (student in transferStdList) {
                            if (student.section.id != newSection.id){
                                oldSection = student.section
                                student.section=newSection
                                studentService.savedSectionTransfer(student, newSection, oldSection)
                            }
                        }
                    }
                    println("Section Id: "+idx+" start: "+params."start$idx"+" End: "+params."end$idx")
                }
                flash.message = "Student Section Transfer Done Successfully"
                render(view: '/admin/studentMgmt/sectionTransfer', model: [classNameList: classNameList, examNameList: examNameList, shiftExam:shiftExam])
                break
        }
    }

    def batchPromotion(){
        def academicYearList = schoolService.academicYears(AcaYearType.SCHOOL)
        def classNameList = classNameService.classNameDropDownList();
        switch (request.method) {
            case 'GET':
                render(view: '/admin/studentMgmt/batchPromotion', model: [academicYearList :academicYearList, classNameList: classNameList])
                break
            case 'POST':
                List sectionList=new ArrayList()
                if (params.sectionIds){
                    if (params.sectionIds instanceof String){
                        sectionList.add(params.sectionIds)
                    }else {
                        sectionList = params.sectionIds
                    }
                }
                ClassName className = ClassName.read(params.getLong('oldClassId'))

                GroupName groupName
                if (params.groupNameVal){
                    groupName = GroupName.valueOf(params.groupNameVal)
                }
                AcademicYear oldYear
                if (params.oldYear){
                    oldYear = AcademicYear.valueOf(params.oldYear)
                }

                AcademicYear newYear
                if (params.newYear){
                    newYear = AcademicYear.valueOf(params.newYear)
                }

                Section newSection
                Section oldSection
                int startNum
                int endNum
                int limitNum
                def transferStdList
                int rollNo
                sectionList.each {idx ->
                    newSection = Section.read(Long.valueOf(idx))
                    startNum = Integer.valueOf(params."start$idx")
                    endNum = Integer.valueOf(params."end$idx")
                    if (startNum !=0 && startNum < endNum) {
                        rollNo = startNum
                        startNum = startNum - 1
                        limitNum = endNum - startNum
                        transferStdList = studentService.listStudentByRollForPromotion(className, oldYear, groupName, startNum, limitNum)
                        for (student in transferStdList) {
                            if (student.section.id != newSection.id){
                                studentService.saveBatchPromotionByCls(newYear, student, newSection, rollNo)
                                rollNo++
                            }
                        }
                    }
                    println("Section Id: "+idx+" start: "+params."start$idx"+" End: "+params."end$idx")
                }
                flash.message = "Student Batch Promotion Done Successfully"
                render(view: '/admin/studentMgmt/batchPromotion', model: [academicYearList:academicYearList,classNameList: classNameList])
                break
        }
    }

    def batchPromotionByExam(){
        def academicYearList = schoolService.academicYears(AcaYearType.SCHOOL)
        def examNameList = shiftExamService.examNameDropDown()
        def classNameList = classNameService.classNameDropDownList();
        switch (request.method) {
            case 'GET':
                render(view: '/admin/studentMgmt/batchPromotionByExam', model: [academicYearList :academicYearList,classNameList: classNameList, examNameList: examNameList])
                break
                case 'POST':
                    /*println(params)
                    List sectionList=new ArrayList()
                    if (params.sectionIds){
                        if (params.sectionIds instanceof String){
                            sectionList.add(params.sectionIds)
                        }else {
                            sectionList = params.sectionIds
                        }
                    }
                    ClassName className = ClassName.read(params.getLong('classNameId'))
                    ShiftExam shiftExamName = ShiftExam.read(params.getLong('shiftExamId'))

                    GroupName groupName
                    if (params.groupNameVal){
                        groupName = GroupName.valueOf(params.groupNameVal)
                    }

                    def clsExamIds = examService.classExamIdListBatchPromotion(shiftExam, className, groupName)
                    Section newSection
                    Section oldSection
                    int startNum
                    int endNum
                    int limitNum
                    def transferStdList
                    sectionList.each {idx ->
                        newSection = Section.read(Long.valueOf(idx))
                        startNum = Integer.valueOf(params."start$idx")
                        endNum = Integer.valueOf(params."end$idx")
                        if (startNum !=0 && startNum < endNum) {
                            startNum = startNum - 1
                            limitNum = endNum - startNum
                            transferStdList = tabulationService.listStudentBasedClsPosition(className, clsExamIds, groupName, startNum, limitNum)
                            for (student in transferStdList) {
                                if (student.section.id != newSection.id){
                                    oldSection = student.section
                                    student.section=newSection
                                    studentService.savedSectionTransfer(student, newSection, oldSection)
                                }
                            }
                        }
                        println("Section Id: "+idx+" start: "+params."start$idx"+" End: "+params."end$idx")
                    }
                    flash.message = "Student Batch Promotion Done Successfully"
                    render(view: '/admin/studentMgmt/batchPromotion', model: [academicYearList:academicYearList,classNameList: classNameList, examNameList: examNameList, shiftExam:shiftExam])*/
                break
        }
    }

    def promotion(){
        def academicYearList = schoolService.academicYears(AcaYearType.SCHOOL)
        def classNameList = classNameService.classNameDropDownList()
        render(view: '/admin/studentMgmt/promotion', model: [classNameList:classNameList, academicYearList: academicYearList])
    }

    def setSection(Long className){

        LinkedHashMap result = new LinkedHashMap()
        result.put('isError',true)
        String outPut

        ClassName classNameObj=ClassName.get(className)
        List<Section> sectionList = sectionService.classSections(classNameObj)
        String fields=''
        if (sectionList){
            fields=fields+' <option value="">Select section..</option>'
            sectionList.each { Section section ->
                fields=fields+" <option value=${section.id}>${section.name}</option>"
            }
        }else{
            fields=fields+' <option value=""> No Section</option>'
        }
        result.put('isError',false)
        result.put('fields',fields)
        outPut = result as JSON
        render outPut
    }

    def loadReadmission(LoadReAdmissionCommand command){
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        if (command.hasErrors()) {
            def errorList = command?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, errorList?.join('\n'))
            outPut = result as JSON
            render outPut
            return
        }
        def studentList = studentService.classStudentsForReAdmission(command.className,command.academicYear)
        if(!studentList){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "No student found for Re Admission.")
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
            result.put(CommonUtils.MESSAGE, "${year} not initiated yet for re admission")
            outPut = result as JSON
            render outPut
            return
        }
        def nextClasses = classNameService.nextClasses(command.className)
        if(!nextClasses){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "No Class available for re admission")
            outPut = result as JSON
            render outPut
            return
        }
        ClassName nextClass = nextClasses.first()
        if(!nextClass){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "No Class available for re admission")
            outPut = result as JSON
            render outPut
            return
        }
        def sectionList = sectionService.classSections(nextClass, nextYear)
        if(!sectionList){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "No Section available for admission")
            outPut = result as JSON
            render outPut
            return
        }
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put('academicYear',nextYear.key)
        result.put('academicYearStr',nextYear.value)
        result.put('className',nextClass.name)
        result.put('studentList',studentList)
        result.put('sectionList',sectionList)
        outPut = result as JSON
        render outPut
    }
    def loadPromotion(LoadReAdmissionCommand command){
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        if (command.hasErrors()) {
            def errorList = command?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, errorList?.join('\n'))
            outPut = result as JSON
            render outPut
            return
        }
        def studentList = studentService.classStudentsForReAdmission(command.className,command.academicYear)
        if(!studentList){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "No student found for Re Admission.")
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
            result.put(CommonUtils.MESSAGE, "${year} not initiated yet for promotion")
            outPut = result as JSON
            render outPut
            return
        }

        def sectionList = sectionService.sectionDropDownList(nextYear)
        if(!sectionList){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "No Section available for promotion")
            outPut = result as JSON
            render outPut
            return
        }
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put('academicYear',nextYear.key)
        result.put('academicYearStr',nextYear.value)
        result.put('studentList',studentList)
        result.put('sectionList',sectionList)
        outPut = result as JSON
        render outPut
    }

    def loadSectionTransfer(Long id){
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut

        ShiftExam shiftExam = ShiftExam.read(id)
        if (!shiftExam) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "Please select exam name to transfer section")
            outPut = result as JSON
            render outPut
            return
        }
        ClassName className
        if (params.className) {
            className = ClassName.read(params.getLong('className'))
        }
        GroupName groupName
        if (params.groupName) {
            groupName = GroupName.valueOf(params.groupName)
        }
        int numOfStudent = studentService.numberOfClassStudent(className, groupName)


        def sectionList = sectionService.groupSections(className, groupName)
        if(!sectionList){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "No Section available for section transfer")
            outPut = result as JSON
            render outPut
            return
        }
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)

        result.put('numOfStudent',numOfStudent)
        result.put('nameOfCls',className.name)
        result.put('classNameId',className.id)
        result.put('shiftExamId',shiftExam.id)
        result.put('groupNameVal',groupName?.key)
        result.put('sectionList',sectionList)
        outPut = result as JSON
        render outPut
    }

    def loadBatchPromotion(Long id){
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut

        ClassName oldClass
        if (params.className) {
            oldClass = ClassName.read(params.getLong('className'))
        }
        def nextClasses = classNameService.nextClasses(oldClass)
        if(!nextClasses){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "No Class available for re admission")
            outPut = result as JSON
            render outPut
            return
        }
        ClassName newClass = nextClasses.first()
        if(!newClass){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "No Class available for re admission")
            outPut = result as JSON
            render outPut
            return
        }
        GroupName groupName
        if (params.groupName) {
            groupName = GroupName.valueOf(params.groupName)
        }
        AcademicYear academicYear
        if(params.academicYear){
            academicYear = AcademicYear.valueOf(params.academicYear)
        } else {
            academicYear = schoolService.schoolWorkingYear()
        }
        int numOfStudent = studentService.numberOfClassStudentForPromotion(oldClass, academicYear, groupName)
        if(numOfStudent == 0){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "No student found for Promotion.")
            outPut = result as JSON
            render outPut
            return
        }

        int year
        AcademicYear nextYear
        if (academicYear?.value.length() == 4) {
            year = Integer.parseInt(academicYear.value)
            year = year +1
            nextYear = AcademicYear.getYearByString(year.toString())
        } else {
            year = Integer.parseInt(academicYear?.value.substring(0, 4))
            int lowYear = year+1
            int highYear = year+2
            nextYear = AcademicYear.getYearByString(lowYear+"-"+highYear)
        }

        if(!nextYear){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "${year} not initiated yet for promotion")
            outPut = result as JSON
            render outPut
            return
        }

        def sectionList = sectionService.classSectionsDDList(newClass, nextYear)
        if(!sectionList){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "No Section available for promotion")
            outPut = result as JSON
            render outPut
            return
        }

        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put('numOfStudent',numOfStudent)
        result.put('nameOfCls',newClass.name)
        result.put('oldClassId',oldClass.id)
        result.put('newClassId',newClass.id)
        result.put('oldYear',academicYear.key)
        result.put('newYear',nextYear.key)
        result.put('academicYearStr',nextYear.value)
        result.put('groupNameVal',groupName?.key)
        result.put('sectionList',sectionList)
        outPut = result as JSON
        render outPut
    }
    /*def loadRoleReorder(Long id){
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut

        ClassName className = ClassName.read(id)
        if (className) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "Please select class to reorder role")
            outPut = result as JSON
            render outPut
            return
        }

        GroupName groupName
        if (params.groupName) {
            groupName = GroupName.valueOf(params.groupName)
        }
        AcademicYear academicYear
        if(params.academicYear){
            academicYear = AcademicYear.valueOf(params.academicYear)
        } else {
            academicYear = schoolService.schoolWorkingYear()
        }
     *//*   def studentList =

        def sectionList = sectionService.classSectionsDDList(newClass, nextYear)
        int numOfStuden
        if(!sectionList){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "No Section available for promotion")
            outPut = result as JSON
            render outPut
            return
        }
*//*
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        *//*result.put('numOfStudent',numOfStudent)
        result.put('nameOfCls',newClass.name)
        result.put('oldClassId',oldClass.id)
        result.put('newClassId',newClass.id)
        result.put('oldYear',academicYear.key)
        result.put('newYear',nextYear.key)
        result.put('academicYearStr',nextYear.value)*//*
        result.put('groupNameVal',groupName?.key)
//        result.put('sectionList',sectionList)
        outPut = result as JSON
        render outPut
    }*/


    //manage TC and Dropout student
    def tc() {
        AcademicYear academicYear = schoolService.workingYear()
        LinkedHashMap resultMap = studentService.tcOrDropOutPaginateList(params)
        if (!resultMap || resultMap.totalCount == 0) {
            render(view: '/admin/studentMgmt/tcOrDropout', model: [dataReturn: null, totalCount: 0, workingYear:academicYear])
            return
        }
        int totalCount = resultMap.totalCount
        render(view: '/admin/studentMgmt/tcOrDropout', model: [dataReturn: resultMap.results, totalCount: totalCount, workingYear:academicYear])
    }


    def listTcOrDropOut() {
        LinkedHashMap gridData
        String result

        LinkedHashMap resultMap = studentService.tcOrDropOutPaginateList(params)
        if(!resultMap || resultMap.totalCount== 0){
            gridData = [iTotalRecords: 0, iTotalDisplayRecords: 0, aaData: []]
            result = gridData as JSON
            render result
            return
        }
        int totalCount =resultMap.totalCount
        gridData = [iTotalRecords: totalCount, iTotalDisplayRecords: totalCount, aaData: resultMap.results]
        result = gridData as JSON
        render result
    }

    def saveTcOrDropOut(TcAndDropOutCommand command) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        if (command.hasErrors()) {
            def errorList = command?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, errorList?.join('\n'))
            outPut = result as JSON
            render outPut
            return
        }
        TcAndDropOut dropOut
        String message
        dropOut = new TcAndDropOut(command.properties)
        message ="TC issued successfully."
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        if(dropOut.hasErrors()){
            def errorList = dropOut?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            message = errorList?.join('\n')
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, message)
            outPut = result as JSON
            render outPut
            return
        }
        Date releaseDate = new Date()
        if(command.releaseDate){
            releaseDate = command.releaseDate
        }
        Student student= command.student
        student.studentStatus=StudentStatus.TC
        student.save()
        if(!command.releaseText){
            dropOut.releaseText=certificateText(dropOut,student)
        }
        dropOut.releaseDate=releaseDate
        dropOut.save()
        result.put(CommonUtils.MESSAGE, message)
        outPut = result as JSON
        render outPut
        return
    }
    def updateTcOrDropOut(TcAndDropOutCommand command) {
        if (!request.method.equals('POST')) {
            redirect(action: 'tc')
            return
        }

        LinkedHashMap result = new LinkedHashMap()
        String outPut
        if (command.hasErrors()) {
            def errorList = command?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            flash.message = errorList?.join('. ')
            render(view: '/admin/studentMgmt/transferCertificate')
            return
        }
        if(!command.id){
            flash.message = CommonUtils.COMMON_NOT_FOUND_MESSAGE
            render(view: '/admin/studentMgmt/transferCertificate')
            return
        }
        TcAndDropOut dropOut = TcAndDropOut.get(command.id)
        if(!dropOut){
            flash.message = CommonUtils.COMMON_NOT_FOUND_MESSAGE
            render(view: '/admin/studentMgmt/transferCertificate')
            return
        }
        dropOut.properties['tcType','reason','schoolName','releaseDate','releaseText']=command.properties
        Date releaseDate = new Date()
        if(command.releaseDate){
            releaseDate = command.releaseDate
        }
        Student student= dropOut.student
        if(!command.releaseText){
            dropOut.releaseText= certificateText(dropOut,student)
        }
        dropOut.releaseDate=releaseDate
        dropOut.save()
        String tcSubmitButton = params.tcSubmitButton
        if(tcSubmitButton && tcSubmitButton=="updatePrintBtn"){
            redirect(controller: 'report', action: 'printTransferCertificate', params: [tcId:dropOut?.id])
            return
        }
        redirect(action: 'tc')
    }
    def transferCertificate(Long id){
        TcAndDropOut dropOut = TcAndDropOut.read(id)
        if(!dropOut || dropOut.activeStatus!=ActiveStatus.ACTIVE){
            flash.message = "No Transfer Certificate available."
            render(view: '/admin/studentMgmt/transferCertificate')
            return
        }
        render (view: '/admin/studentMgmt/transferCertificate',model: [tcObj:dropOut])
    }
    def deleteTc(Long id) {
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        TcAndDropOut dropOut = TcAndDropOut.get(id)
        if (!dropOut){
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        Student student=dropOut.student
        student.studentStatus=StudentStatus.NEW
        dropOut.activeStatus=ActiveStatus.DELETE

        student.save()
        dropOut.save()
        result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
        result.put(CommonUtils.MESSAGE,"TC deleted Successfully")
        outPut = result as JSON
        render outPut
        return
    }


    def saveIndividualAttendance(AttnRecordDayCommand command) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        AttnStdRecord  attnStdRecord
        AttnRecordDay attnRecordDay
        LinkedHashMap result = new LinkedHashMap()
        String outPut

        try {
            Date now =new Date();
            Date attenDate = params.getDate("releaseDate","dd/MM/yyyy")
            Student student= Student.findById(params.getInt("student"))
            if(student){attnStdRecord = AttnStdRecord.findByStdNo(student.id)}
            else{ attnStdRecord = AttnStdRecord.findByStdNo(params.id)}

            if(!attnStdRecord) {
                Section section = student.section
                Date InTimevalue = params.getDate("inTime", "hh:mm a")
                String classInTime = InTimevalue.format("HHmmss")
                Date inTime = CommonUtils.deviceLogDateStrToDate(attenDate.format("yyyyMMdd"), classInTime)
                attnRecordDay = AttnRecordDay.findByRecordDate(attenDate)
                AttnStdRecord attnStdRecordInfo = new AttnStdRecord()
                attnStdRecordInfo.stdNo = student.rollNo
                attnStdRecordInfo.studentId = student.id
                attnStdRecordInfo.sectionId = section.id
                attnStdRecordInfo.inTime = inTime
                attnStdRecordInfo.outTime = null;
                attnStdRecordInfo.remarks =params.reason
                attnStdRecordInfo.recordDay = attnRecordDay
                if (!attnStdRecordInfo.save()) {
                    result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                    result.put(CommonUtils.MESSAGE, "save fail")
                } else {
                    result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
                    result.put(CommonUtils.MESSAGE, "save successfully")
                }
            }else{

                Date InTimevalue = params.getDate("inTime", "hh:mm a")
                String classInTime = InTimevalue.format("HHmmss")
                Date inTime = CommonUtils.deviceLogDateStrToDate(attenDate.format("yyyyMMdd"), classInTime)
                //attnRecordDay = AttnRecordDay.findByRecordDate(attenDate)
                attnStdRecord.inTime = inTime
                attnStdRecord.remarks=params.reason

                // attnStdRecordInfo.recordDay = attnRecordDay
                if (!attnStdRecord.save()) {
                    result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                    result.put(CommonUtils.MESSAGE, "edit fail")
                } else {
                    result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
                    result.put(CommonUtils.MESSAGE, "edit successfully")
                }
            }

        }catch (Exception e){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "save fail")
        }
        outPut = result as JSON
        render outPut
        return
    }

    private String certificateText(TcAndDropOut dropOut, Student student){
        Registration registration = student.registration
        String textStr ='<p>Certification is hereby issued to <strong><em><u>'+student.name;
        textStr+='</u></em></strong>, Son of <span style="text-decoration: underline;"><em><strong>'+registration.fathersName
        textStr+='</strong></em></span> and <span style="text-decoration: underline;"><em><strong>'+registration.mothersName
        textStr+='</strong></em></span>, <span style="text-decoration: underline;"><em><strong>'+registration.presentAddress
        textStr+='</strong></em></span> that he was a student of class <span style="text-decoration: underline;"><em><strong>'+student.className.name
        textStr+='</strong></em></span>, ID# <span style="text-decoration: underline;"><em><strong>'+student.studentID
        textStr+='</strong></em></span> in Baily School till '+CommonUtils.getUiDateStr(dropOut?.releaseDate)
        textStr+='. He was born in <span style="text-decoration: underline;"><em><strong>'+CommonUtils.getUiDateStr(registration?.birthDate)+'</strong></em></span>.</p>'
        textStr+='<p>&nbsp;</p><p>What is more, he is a student of good etiquette &amp; behavior.</p><p>&nbsp;</p><p>May all success attend his.</p>'
        return textStr;
    }
   /* def roleReorderSave(){
        Employee employee

        params.sortOrder.each{ employeeData ->

            try {
                employee=Employee.get(Long.parseLong(employeeData.key))
                employee.sortOrder=Integer.parseInt(employeeData.value)
                employee.save()
            }catch (e){
                e.print(employee.empID)
            }
        }
        redirect(action: 'hrStaffCategory')
    }*/
}
