package com.grailslab.exam

import com.grailslab.CommonUtils
import com.grailslab.bailyschool.uma.Role
import com.grailslab.enums.ExamTerm
import com.grailslab.enums.PrintOptionType
import com.grailslab.gschoolcore.AcademicYear
import com.grailslab.settings.ClassName
import com.grailslab.settings.Exam
import com.grailslab.settings.Section
import com.grailslab.settings.SubjectName
import com.grailslab.stmgmt.AttnStudentSummery
import com.grailslab.stmgmt.PreviousTermMark
import com.grailslab.stmgmt.Student
import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityUtils
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef

class PreviousTermController {

    def studentService
    def previousTermService
    def messageSource
    def shiftExamService
    def classNameService
    def studentSubjectsService
    def schoolService
    def jasperService
    def examService
    def recordDayService
    def attnStudentService


    def index(Long id) {
        Section section = Section.read(id)
        if(!section){
            redirect(controller: 'exam',action: 'entry')
            return
        }
        ExamTerm examTerm = ExamTerm.valueOf(params.examTerm)
        if(!examTerm){
            redirect(controller: 'exam',action: 'entry')
            return
        }
        String breadStr = "${examTerm.value} Total Mark Entry"
        String inputLabel = "Total Term Mark"
        def studentList = studentService.allStudent(section)
        render(view: '/exam/termMark',model: [inputLabel:inputLabel,section:section,examTerm:examTerm,breadStr:breadStr, studentList:studentList,dataReturn: null, totalCount: 0])
    }

    def listTermMark(Long id){
        LinkedHashMap gridData
        String result
        Section section = Section.read(id)
        if(!section){
            gridData = [iTotalRecords: 0, iTotalDisplayRecords: 0, aaData: []]
            result = gridData as JSON
            render result
            return
        }
        Exam exam = Exam.read(params.getLong("examId"))
        int entryFor = examService.examTerm(exam)

        LinkedHashMap resultMap = previousTermService.getPreviousTermList(params,section, entryFor)

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
    def save(PreviousTermCommand command){
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
            outPut=result as JSON
            render outPut
            return
        }
        AttnStudentSummery attnStudentSummery
        String message

        Exam exam = Exam.read(command.examId)
        int entryFor = examService.examTerm(exam)

        //@todo-aminul add logic for 2nd term attendance
        if (command.id) {
            attnStudentSummery = AttnStudentSummery.get(command.id)
            if(!attnStudentSummery){
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
                outPut=result as JSON
                render outPut
                return
            }
            if (entryFor == 1) {
                attnStudentSummery.term1attenDay = command.attendDay
            } else if (entryFor == 2) {
                attnStudentSummery.term2attenDay = command.attendDay
            } else {
                attnStudentSummery.attendDay = command.attendDay
            }

            message = "Total Attendance Updated Successfully"

        }else {
            attnStudentSummery = AttnStudentSummery.findByStudent(command.student)
            if(!attnStudentSummery){
                attnStudentSummery = new AttnStudentSummery(student: command.student)
            }

            if (entryFor == 1) {
                attnStudentSummery.term1attenDay = command.attendDay
            } else if (entryFor == 2) {
                attnStudentSummery.term2attenDay = command.attendDay
            } else {
                attnStudentSummery.attendDay = command.attendDay
            }
            message = "Total Attendance Added Successfully"

        }
        attnStudentSummery.save()
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, message)
        outPut=result as JSON
        render outPut
        return
    }
    def edit(Long id) {

        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }

        LinkedHashMap result = new LinkedHashMap()
        String outPut
        AttnStudentSummery attnStudentSummery = AttnStudentSummery.read(id)
        if (!attnStudentSummery) {
            result.put(CommonUtils.IS_ERROR,true)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_ERROR_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        Exam exam = Exam.read(params.getLong("examId"))
        int entryFor = examService.examTerm(exam)


        Integer obtainDay
        if (entryFor == 1) {
            obtainDay = attnStudentSummery.term1attenDay
        } else if (entryFor == 2) {
            obtainDay = attnStudentSummery.term2attenDay
        } else {
            obtainDay = attnStudentSummery.attendDay
        }


        String studentID=attnStudentSummery.student.name+"("+attnStudentSummery.student.studentID+")"
        result.put(CommonUtils.IS_ERROR,false)
        result.put('obtainDay',obtainDay)
        result.put('studentID',studentID)
//        result.put('stdId',previousTermMark.student.id)
        result.put('preId',attnStudentSummery.id)
        outPut = result as JSON
        render outPut
    }

    def attendance(Long id) {
        def examNameList = shiftExamService.examNameDropDown()

        String layoutName = "moduleExam&ResultLayout"
        if (SpringSecurityUtils.ifAnyGranted(Role.AvailableRoles.TEACHER.value())){
            layoutName = "adminLayout"
        }
        Exam exam=Exam.read(id)
        if(!exam){
            render(view: '/exam/attendanceEntry', layout: layoutName, model: [examNameList: examNameList])
            return
        }
        Section section = exam.section
        ClassName className = exam.className
        int workingDays = className.workingDays?:0
        int entryFor = examService.examTerm(exam)

        def entryStd = previousTermService.attendanceEntryStdIds(section, entryFor)
        def studentList = studentService.allStudentNotInList(section,entryStd)

        LinkedHashMap resultMap = previousTermService.getPreviousTermList(params,section, entryFor)
        if (!resultMap || resultMap.totalCount == 0) {
            render(view: '/exam/attendanceEntry',layout: layoutName,model: [examId: exam.id, examNameList: examNameList, className: className.name, workingDays: workingDays, section:section, studentList:studentList,dataReturn: null, totalCount: 0])
            return
        }
        int totalCount = resultMap.totalCount
        render(view: '/exam/attendanceEntry',layout: layoutName, model: [examId: exam.id, examNameList: examNameList, className: className.name, workingDays: workingDays, section:section, studentList:studentList, dataReturn: resultMap.results, totalCount: totalCount])
    }

    def ctMark(Long id, Long subjectId) {
        def classNameList = classNameService.classNameDropDownList()
        Section section = Section.read(id)
        SubjectName subjectName = SubjectName.read(subjectId)

        String layoutName = "moduleExam&ResultLayout"
        if (SpringSecurityUtils.ifAnyGranted(Role.AvailableRoles.TEACHER.value())){
            layoutName = "adminLayout"
        }
        if(!section || !subjectName){
            render(view: '/exam/attendanceCtEntry',layout: layoutName,model: [classNameList: classNameList])
            return
        }
        def subjectStdIds = studentSubjectsService.subjectStudentIdsList(section, subjectName)
        def entryStd = previousTermService.ctEntryStdIds(section, subjectName, subjectStdIds)
        def studentList = studentSubjectsService.subjectStudentsNotInList(section, subjectName, entryStd)

        render(view: '/exam/attendanceCtEntry',layout: layoutName,model: [classNameList: classNameList, section:section, subjectName:subjectName, studentList:studentList])
    }

    def saveCt(PreviousTermCtCommand command){
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
            outPut=result as JSON
            render outPut
            return
        }

        PreviousTermMark previousTermMark
        String message
        if (command.id) {
            previousTermMark = PreviousTermMark.get(command.id)
            if(!previousTermMark){
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
                outPut=result as JSON
                render outPut
                return
            }
            previousTermMark.ctMark = command.ctMark
            message = "Ct Mark Updated Successfully"

        }else {
            previousTermMark = PreviousTermMark.findByStudentAndSubjectName (command.student, command.subjectName)
            if(!previousTermMark){
                previousTermMark = new PreviousTermMark(student: command.student, subjectName: command.subjectName)
            }
            previousTermMark.ctMark = command.ctMark
            message = "Total Ct Mark Added Successfully"
        }
        previousTermMark.save()
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, message)
        outPut=result as JSON
        render outPut
        return
    }

    def editCt(Long id) {

        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }

        LinkedHashMap result = new LinkedHashMap()
        String outPut
        PreviousTermMark previousTermMark = PreviousTermMark.read(id)
        if (!previousTermMark) {
            result.put(CommonUtils.IS_ERROR,true)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_ERROR_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        def obtainMark = previousTermMark.ctMark
        /*ExamTerm examTerm = ExamTerm.valueOf(params.examTerm)
        if(examTerm){
            if(examTerm == ExamTerm.FIRST_TERM){
                obtainMark = previousTermMark.term1Mark
            }else if(examTerm == ExamTerm.SECOND_TERM){
                obtainMark = previousTermMark.term2Mark
            }
        }else {
            obtainMark = previousTermMark.attendDay
        }*/
        String studentID=previousTermMark.student.name+"("+previousTermMark.student.studentID+")"
        result.put(CommonUtils.IS_ERROR,false)
        result.put('obtainMark',obtainMark)
        result.put('studentID',studentID)
//        result.put('stdId',previousTermMark.student.id)
        result.put('preId',previousTermMark.id)
        outPut = result as JSON
        render outPut
    }


    def ctListTermMark(Long id,Long subjectId){
        LinkedHashMap gridData
        String result
        Section section = Section.read(id)

        if(!section){
            gridData = [iTotalRecords: 0, iTotalDisplayRecords: 0, aaData: []]
            result = gridData as JSON
            render result
            return
        }

        SubjectName subjectName = SubjectName.read(subjectId)
        def subjectStdIds = studentSubjectsService.subjectStudentIdsList(section, subjectName)
        LinkedHashMap resultMap = previousTermService.getPreviousTermCtList(params, section, subjectName, subjectStdIds)

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

    def ctWorkDays(){
        def classNameList = classNameService.allClassNames()
        String layoutName = "moduleExam&ResultLayout"
        if (SpringSecurityUtils.ifAnyGranted(Role.AvailableRoles.TEACHER.value())){
            layoutName = "adminLayout"
        }
        switch (request.method) {
            case 'GET':
                render(view: '/exam/worksDayEntry',layout: layoutName, model: [classNameList: classNameList])
                break
            case 'POST':
                ClassName className
                params.workingDay.each{ classNameId ->
                    try {
                        className = ClassName.get(Long.parseLong(classNameId.key))
                        className.workingDays = Integer.parseInt(classNameId.value)
                        className.save()
                    }catch (e){
                        log.error(e.message)
                    }
                }
                flash.message = "Working Days updated Successfully"
                redirect(action: 'ctMark')
                break
        }

    }

    def workDays(){
        def classNameList = classNameService.allClassNames()
        String layoutName = "moduleExam&ResultLayout"
        if (SpringSecurityUtils.ifAnyGranted(Role.AvailableRoles.TEACHER.value())){
            layoutName = "adminLayout"
        }
        switch (request.method) {
            case 'GET':
                render(view: '/exam/worksDayEntry',layout: layoutName, model: [classNameList: classNameList])
                break
            case 'POST':
                ClassName className
                params.workingDay.each{ classNameId ->
                    try {
                        className = ClassName.get(Long.parseLong(classNameId.key))
                        className.workingDays = Integer.parseInt(classNameId.value)
                        className.save()
                    }catch (e){
                        log.error(e.message)
                    }
                }
                flash.message = "Working Days updated Successfully"
                redirect(action: 'attendance')
                break
        }

        }

    def countWorkingDays(CountWorkingDayCommand command) {
        LinkedHashMap result = new LinkedHashMap()
        String outPut

        if(!command.periodStart || !command.periodEnd){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "Both period start and end day required")
            outPut=result as JSON
            render outPut
            return
        }

        if(command.periodStart >= command.periodEnd){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "Please select valid date range for count")
            outPut=result as JSON
            render outPut
            return
        }
        def classNameList = classNameService.allClassNames()
        Integer workingDayCount = recordDayService.workingDay(command.periodStart, command.periodEnd)
        def stdNameList

        AcademicYear academicYear
        Integer pCount
        AttnStudentSummery attnStudentSummery
        for (className in classNameList) {
            academicYear = schoolService.workingYear(className)
            stdNameList = studentService.classStudentList(className, academicYear)
            for (student in stdNameList){
                pCount = attnStudentService.studentPresentCount(student.studentID, command.periodStart, command.periodEnd)
                attnStudentSummery = AttnStudentSummery.findByStudentAndAcademicYear(student, academicYear)
                if (attnStudentSummery) {
                    attnStudentSummery.term1attenDay = pCount
                } else {
                    attnStudentSummery = new AttnStudentSummery(student: student, term1attenDay: pCount, attendDay: 0, academicYear: academicYear)
                }
                attnStudentSummery.save()
            }
            className.workingDays = workingDayCount
            className.save()
        }

        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, "Period calculate successfully")
        outPut=result as JSON
        render outPut
        return

    }


    def ctMarkReport(Long id){
        if (!id || !params.subjectName){
            redirect(controller: 'previousTerm', action: 'index')
            return
        }
        Section section = Section.read(id)
        SubjectName subjectName = SubjectName.read(params.getLong("subjectName"))
        //subjectName
        def subjectStdIds = studentSubjectsService.subjectStudentIdsList(section, subjectName)

        String reportFileName = CT_MARK_JASPER_FILE

        String sqlParams = " sec.id = ${section.id} AND sb.id = ${subjectName.id} "
        if (subjectStdIds) {
            sqlParams += " and st.id in ("+subjectStdIds.join(",")+") "
        }
        /*if (exam) {
            sqlParams += " and exs.exam_id=${exam.id} "
        }*/


        String reportLogo = schoolService.reportLogoPath()
        Map paramsMap = new LinkedHashMap()
        paramsMap.put('REPORT_LOGO', reportLogo)
        paramsMap.put('sqlParams', sqlParams)

        String extension = CommonUtils.PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.PDF_FORMAT

        PrintOptionType printOptionType = PrintOptionType.PDF
        if (params.printOptionType) {
            printOptionType = PrintOptionType.valueOf(params.printOptionType)
        }
        if(printOptionType && printOptionType !=PrintOptionType.PDF){
            if(printOptionType==PrintOptionType.XLSX){
                extension = CommonUtils.XLSX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_XLSX
                exportFormat = JasperExportFormat.XLSX_FORMAT
            } else if(printOptionType==PrintOptionType.DOCX){
                extension = CommonUtils.DOCX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_DOCX
                exportFormat = JasperExportFormat.DOCX_FORMAT
            }
        }
        String outputFileName = "previous_term_ct${extension}"
        JasperReportDef reportDef = new JasperReportDef(
                name: reportFileName,
                fileFormat: exportFormat,
                parameters: paramsMap
        )
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        response.contentType =grailsApplication.config.grails.mime.types[mimeType]
        response.setHeader("Content-disposition", "inline;filename=${outputFileName}")
        response.outputStream << report.toByteArray()
    }

    def attendanceReport(Long id){
        //ShiftExam shiftExam= ShiftExam.read(id)
        Exam exam = Exam.read(id)
        if (!exam){
            redirect(controller: 'previousTerm', action: 'attendance')
            return
        }
        String reportFileName = ATTENDANCE_JASPER_FILE

        String sqlParams = " sec.id = ${exam.section.id}"
        /*if (exam) {
            sqlParams += " and exs.exam_id=${exam.id} "
        }*/


        String reportLogo = schoolService.reportLogoPath()
        Map paramsMap = new LinkedHashMap()
        paramsMap.put('REPORT_LOGO', reportLogo)
        paramsMap.put('sqlParams', sqlParams)

        String extension = CommonUtils.PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.PDF_FORMAT

        PrintOptionType printOptionType = PrintOptionType.PDF
        if (params.printOptionType) {
            printOptionType = PrintOptionType.valueOf(params.printOptionType)
        }
        if(printOptionType && printOptionType !=PrintOptionType.PDF){
            if(printOptionType==PrintOptionType.XLSX){
                extension = CommonUtils.XLSX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_XLSX
                exportFormat = JasperExportFormat.XLSX_FORMAT
            } else if(printOptionType==PrintOptionType.DOCX){
                extension = CommonUtils.DOCX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_DOCX
                exportFormat = JasperExportFormat.DOCX_FORMAT
            }
        }
        String outputFileName = "period_attendance_report${extension}"
        JasperReportDef reportDef = new JasperReportDef(
                name: reportFileName,
                fileFormat: exportFormat,
                parameters: paramsMap
        )
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        response.contentType =grailsApplication.config.grails.mime.types[mimeType]
        response.setHeader("Content-disposition", "inline;filename=${outputFileName}")
        response.outputStream << report.toByteArray()
    }
    private static final String CT_MARK_JASPER_FILE = 'previousTermCtMark.jasper'
    private static final String ATTENDANCE_JASPER_FILE = 'previousTermAttendance.jasper'
}

class PreviousTermCommand {
    Long id
    Long examId
    Student student
    Integer attendDay

    static constraints = {
        id nullable: true
        student nullable: true
        attendDay nullable: true
        examId nullable: true

    }
}

class PreviousTermCtCommand {
    Long id
    Student student
    Section section
    Double ctMark
    SubjectName subjectName

    static constraints = {
        id nullable: true
        student nullable: true
        ctMark nullable: true
        subjectName nullable: true


    }
}
class CountWorkingDayCommand {
    Date periodStart
    Date periodEnd

    static constraints = {
    }
}