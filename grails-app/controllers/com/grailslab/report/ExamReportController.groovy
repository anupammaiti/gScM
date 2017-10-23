package com.grailslab.report

import com.grailslab.CommonUtils
import com.grailslab.ParamDetailReference
import com.grailslab.ParamReference
import com.grailslab.ResultAnalysisReference
import com.grailslab.ResultClassReference
import com.grailslab.ResultSubjectReference
import com.grailslab.TopSheetReference
import com.grailslab.command.ExamMarkPrintCommand
import com.grailslab.command.ExamScheduleBySectionCommand
import com.grailslab.command.SubjectStudentReportCommand
import com.grailslab.command.report.ResultAnalysisCommand
import com.grailslab.enums.*
import com.grailslab.gschoolcore.AcademicYear
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.settings.*
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef

class ExamReportController{
    def messageSource
    def schoolService
    def jasperService
    def studentService
    def classSubjectsService
    def studentSubjectsService
    def examMarkService
    def tabulationService
    def examService
    def classNameService
    def subjectService

    def examMark(ExamMarkPrintCommand command){
        if (command.hasErrors()) {
            def errorList = command?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            flash.message =errorList?.join('. ')
            render(view: '/baseReport/reportNotFound')
            return
        }
        ExamSchedule examSchedule = command.examSchedule
        String reportFileName
        if(command.inputType =='ctMark') {
            reportFileName = EXAM_MARK_JASPER_FILE
                if(examSchedule.isHallMarkInput){
                    reportFileName = EXAM_MARK_DETAIL_VIEW_JASPER_FILE
                }
        } else {
            reportFileName = EXAM_MARK_JASPER_FILE
            if(examSchedule.isHallPractical) {
                reportFileName = EXAM_MARK_WITH_HALL_PRAC_JASPER_FILE
            }
                if(examSchedule.isHallMarkInput){
                    reportFileName = EXAM_MARK_DETAIL_VIEW_JASPER_FILE
                    if(examSchedule.isHallPractical) {
                        reportFileName = EXAM_MARK_WITH_Hall_PRAC_DETAIL_JASPER_FILE
                    }
                }
        }
        Exam exam = examSchedule.exam

        String reportLogo = schoolService.reportLogoPath()
        Map paramsMap = new LinkedHashMap()
        paramsMap.put('REPORT_LOGO', reportLogo)
        paramsMap.put('scheduleIds', examSchedule.id.toString())
        paramsMap.put('examIds', exam.id.toString())
        paramsMap.put('examName', exam.name)
        paramsMap.put('year', exam.academicYear.value)

        String extension = CommonUtils.PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.PDF_FORMAT
        if(command.printOptionType && command.printOptionType !=PrintOptionType.PDF){
            if(command.printOptionType==PrintOptionType.XLSX){
                extension = CommonUtils.XLSX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_XLSX
                exportFormat = JasperExportFormat.XLSX_FORMAT
            } else if(command.printOptionType==PrintOptionType.DOCX){
                extension = CommonUtils.DOCX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_DOCX
                exportFormat = JasperExportFormat.DOCX_FORMAT
            }
        }

        String outputFileName = "Exam_Mark_${exam?.name}${extension}"
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

    def tabulation(Long id){
        ShiftExam shiftExam = ShiftExam.read(id)
        Exam exam
        if (params.examId){
            exam = Exam.read(params.getLong('examId'))
        }
        if(!shiftExam || !exam){
            flash.message =CommonUtils.COMMON_NOT_FOUND_MESSAGE
            render(view: '/baseReport/reportNotFound')
            return
        }
        if(exam.examStatus == ExamStatus.NEW){
            flash.message ="Result not ready"
            render(view: '/baseReport/reportNotFound')
            return
        }

//        def subIds = classSubjectsService.hallExamSubjectHeaderIds(exam.className)
        int numberOfSubject = exam.numberOfSubject //exam.numberOfSubject      //subIds.size()
        if(numberOfSubject<3 || numberOfSubject>25){
            flash.message ="Tabulation only Supported from 3 to 25 subjects in Class."
            render(view: '/baseReport/reportNotFound')
            return
        }
        String runningSchool = schoolService.runningSchool()

        def subIds = exam.subjectIds.split(',')
        def subjects
        SubjectName subject
        ClassSubjects classSubjects
        ClassName className = exam.className
        def idValue
        Double clsOrSecHighest = 0
        if (runningSchool == CommonUtils.NARAYANGANJ_IDEAL_SCHOOL) {
            def examIds
            if (false/*exam.examStatus == ExamStatus.RESULT || exam.examStatus == ExamStatus.PUBLISHED*/){
                List<Exam> examList = examService.examListForPreparingResult(shiftExam, className)
                examIds = examList.collect {it.id}
            } else {
                examIds = new ArrayList<Long>()
                examIds.add(exam.id)
            }
            subjects = new ParamDetailReference()
            for(int i=0; i<numberOfSubject; i++){
                idValue = subIds[i]
                subject = SubjectName.read(idValue.toLong())
                if(subject){
                    subjects."param${i}Name"= subject.name
                    classSubjects = ClassSubjects.findByClassNameAndSubjectAndActiveStatus(className, subject, ActiveStatus.ACTIVE)
                    if (classSubjects.isHallPractical) {
                        subjects."subject${i}ct" = classSubjects.ctMark
                        subjects."subject${i}ctEff" = classSubjects.ctEffMark
                        subjects."subject${i}hallWri" = classSubjects.hallWrittenMark
                        subjects."subject${i}hallObj" = classSubjects.hallObjectiveMark
                        subjects."subject${i}hallPrac" = classSubjects.hallPracticalMark
                        subjects."subject${i}hallEff" = classSubjects.hallEffMark
                    } else {
                        subjects."subject${i}ct" = classSubjects.ctMark
                        subjects."subject${i}ctEff" = classSubjects.ctEffMark
                        subjects."subject${i}hallWri" = classSubjects.hallMark
                        subjects."subject${i}hallEff" = classSubjects.hallEffMark
                    }
                    subjects."subject${i}highest" = tabulationService.highestMark(examIds, "subject${i}Mark")
                }
            }
            clsOrSecHighest = tabulationService.highestMark(examIds, "totalObtainMark")
        } else if (runningSchool == CommonUtils.NARAYANGANJ_HIGH_SCHOOL) {
            def examIds
            if (false/*exam.examStatus == ExamStatus.RESULT || exam.examStatus == ExamStatus.PUBLISHED*/){
                List<Exam> examList = examService.examListForPreparingResult(shiftExam, className)
                examIds = examList.collect {it.id}
            } else {
                examIds = new ArrayList<Long>()
                examIds.add(exam.id)
            }
            subjects = new ParamDetailReference()
            for(int i=0; i<numberOfSubject; i++){
                idValue = subIds[i]
                subject = SubjectName.read(idValue.toLong())
                if(subject){
                    subjects."param${i}Name"= subject.name
                    classSubjects = ClassSubjects.findByClassNameAndSubjectAndActiveStatus(className, subject, ActiveStatus.ACTIVE)
                    if (classSubjects.isHallPractical) {
                        subjects."subject${i}ct" = classSubjects.ctMark
                        subjects."subject${i}ctEff" = classSubjects.ctEffMark
                        subjects."subject${i}hallWri" = classSubjects.hallWrittenMark
                        subjects."subject${i}hallObj" = classSubjects.hallObjectiveMark
                        subjects."subject${i}hallPrac" = classSubjects.hallPracticalMark
                        subjects."subject${i}hallSba" = classSubjects.hallSbaMark
                        subjects."subject${i}hallEff" = classSubjects.hallEffMark
                    } else {
                        subjects."subject${i}ct" = classSubjects.ctMark
                        subjects."subject${i}ctEff" = classSubjects.ctEffMark
                        subjects."subject${i}hallWri" = classSubjects.hallMark
                        subjects."subject${i}hallEff" = classSubjects.hallEffMark
                    }
                    subjects."subject${i}highest" = tabulationService.highestMark(examIds, "subject${i}Mark")
                }
            }
            clsOrSecHighest = tabulationService.highestMark(examIds, "totalObtainMark")
        } else if (runningSchool == CommonUtils.NARAYANGANJ_ADARSHA_SCHOOL) {
            subjects = new ParamReference()
            for(int i=0; i<numberOfSubject; i++){
                idValue = subIds[i]
                subject = SubjectName.read(idValue.toLong())
                if(subject){
                    subjects."param${i}Name"= subject.name
                }
            }
        } else {
            subjects = new ParamReference()
            for(int i=0; i<numberOfSubject; i++){
                idValue = subIds[i]
                subject = SubjectName.read(idValue.toLong())
                if(subject){
                    classSubjects = ClassSubjects.findByClassNameAndSubjectAndActiveStatus(className, subject, ActiveStatus.ACTIVE)
                    if(classSubjects.weightOnResult<100){
                        subjects."param${i}Name"= "${classSubjects.weightOnResult}% of ${subject.name}"
                    }else {
                        subjects."param${i}Name"= subject.name
                    }
                }
            }
        }
        int numOfPreExam = 1
        String reportFileName
        if(exam.examTerm==ExamTerm.FINAL_TEST){
            if (runningSchool == CommonUtils.NARAYANGANJ_IDEAL_SCHOOL || runningSchool == CommonUtils.NARAYANGANJ_HIGH_SCHOOL) {
                if (className.subjectGroup) {
                    //need to show group in tabulation
                    reportFileName =schoolService.reportFileName("finalTabulationGroup${numberOfSubject}.jasper")
                } else {
                    reportFileName =schoolService.reportFileName("finalTabulationNoGroup${numberOfSubject}.jasper")
                }
            } else if (runningSchool == CommonUtils.NARAYANGANJ_ADARSHA_SCHOOL) {
                reportFileName =schoolService.reportFileName("finalTabulation${numberOfSubject}.jasper")
            } else if (runningSchool == CommonUtils.BAILY_SCHOOL) {
                numOfPreExam = examService.numberOfTermExam(exam.section)
                if(numOfPreExam==1){
                    reportFileName=schoolService.reportFileName("finalTabulation${numberOfSubject}Term1.jasper")
                }else {
                    reportFileName=schoolService.reportFileName("finalTabulation${numberOfSubject}.jasper")
                }
            }
        }else {
            if (runningSchool == CommonUtils.NARAYANGANJ_IDEAL_SCHOOL || runningSchool == CommonUtils.NARAYANGANJ_HIGH_SCHOOL) {
                if (className.subjectGroup) {
                    reportFileName =schoolService.reportFileName("termTabulationGroup${numberOfSubject}.jasper")
                } else {
                    reportFileName =schoolService.reportFileName("termTabulationNoGroup${numberOfSubject}.jasper")
                }
            } else {
                reportFileName =schoolService.reportFileName("termTabulation${numberOfSubject}.jasper")
            }
        }
        String reportLogo = schoolService.reportLogoPath()

        String sortBy = params.sortBy

        String orderByStr = "std.roll_no asc"
        if(exam.examStatus==ExamStatus.RESULT || exam.examStatus==ExamStatus.PUBLISHED){
            if (sortBy == 'secMeritPos') {
                orderByStr = "tabu.position_in_section asc"
            } else if(sortBy == 'clsMeritPos') {
                orderByStr = "tabu.position_in_class asc"
            }
        }
        int studentCount = exam.studentCount?:0
        int attended = exam.attended?:0
        int scoreF = exam.scoreF?:0

        Map paramsMap = new LinkedHashMap()
        paramsMap.put('REPORT_LOGO', reportLogo)
//        paramsMap.put('SUBREPORT_DIR', subReportFolder)
        paramsMap.put('examId', exam?.id)
        paramsMap.put('orderByStr', orderByStr)

        paramsMap.put('className', className.name)
        paramsMap.put('sectionName', exam.section.name)
        paramsMap.put('groupName', exam.section.groupName?.key)
        paramsMap.put('academicYear', exam.academicYear.value)
        paramsMap.put('examName', shiftExam.examName)
        paramsMap.put('schoolName', message(code: "app.school.name"))
        paramsMap.put('schoolAddress', message(code: "app.report.address.line1"))
        paramsMap.put('resultPublishOn', CommonUtils.getUiDateStr(shiftExam.resultPublishOn))
        //exam parametes
        paramsMap.put('numberOfStudent', studentCount)
        paramsMap.put('absentCount', studentCount - attended)
        paramsMap.put('scoreaplus', exam.scoreAPlus)
        paramsMap.put('scorea', exam.scoreA)
        paramsMap.put('scoreaminus', exam.scoreAMinus)
        paramsMap.put('scoreb', exam.scoreB)
        paramsMap.put('scorec', exam.scoreC)
        paramsMap.put('scoref', scoreF)
        paramsMap.put('passCount', studentCount - scoreF)
        paramsMap.put('fail_on1subject', exam.failOn1Subject)
        paramsMap.put('fail_on2subject', exam.failOn2Subject)
        paramsMap.put('fail_on_more_subject', exam.failOnMoreSubject)
        paramsMap.put('clsOrSecHighest', clsOrSecHighest)
        paramsMap.put('periodStart', shiftExam.periodStart)
        paramsMap.put('periodEnd', shiftExam.periodEnd)
        paramsMap.put('workingDays', className.workingDays)
        if(numOfPreExam==1){
            paramsMap.put('term1Name', "Half Yearly")
        }else if(numOfPreExam==2){
            paramsMap.put('term1Name', "1st Term")
            paramsMap.put('term2Name', "2nd Term")
        }
        // put subject name parameter up to 14 subjects
        String paramName
        String paramSub
        if (runningSchool == CommonUtils.NARAYANGANJ_IDEAL_SCHOOL) {
            String paramCtMark
            Integer ctExamMark
            String paramCtEffMark
            Integer ctEffMark
            String paramWriMark
            Integer wriMark
            String paramObjMark
            Integer objMark
            String paramPracMark
            Integer pracMark
            String paramHallEffMark
            Integer hallEffMark

            String paramHighestMark
            Double highestMark

            for(int i=0; i<numberOfSubject; i++){
                paramName = "param${i}Name"
                paramSub = subjects."param${i}Name"
                paramsMap.put(paramName,paramSub)

                paramCtMark = "param${i}CtMark"
                ctExamMark = subjects."subject${i}ct"
                paramsMap.put(paramCtMark,ctExamMark)

                paramCtEffMark = "param${i}CtEffMark"
                ctEffMark = subjects."subject${i}ctEff"
                paramsMap.put(paramCtEffMark,ctEffMark)

                paramWriMark = "param${i}WriMark"
                wriMark = subjects."subject${i}hallWri"
                paramsMap.put(paramWriMark,wriMark)

                paramObjMark = "param${i}ObjMark"
                objMark = subjects."subject${i}hallObj"
                paramsMap.put(paramObjMark,objMark)

                paramPracMark = "param${i}PracMark"
                pracMark = subjects."subject${i}hallPrac"
                paramsMap.put(paramPracMark,pracMark)

                paramHallEffMark = "param${i}HallEffMark"
                hallEffMark = subjects."subject${i}hallEff"
                paramsMap.put(paramHallEffMark,hallEffMark)

                paramHighestMark = "param${i}highest"
                highestMark = subjects."subject${i}highest"
                paramsMap.put(paramHighestMark, highestMark)
            }
        } else if (runningSchool == CommonUtils.NARAYANGANJ_HIGH_SCHOOL) {
            String paramCtMark
            Integer ctExamMark
            String paramCtEffMark
            Integer ctEffMark
            String paramWriMark
            Integer wriMark
            String paramObjMark
            Integer objMark
            String paramPracMark
            Integer pracMark
            String paramSbaMark
            Integer sbaMark
            String paramHallEffMark
            Integer hallEffMark

            String paramHighestMark
            Double highestMark

            for(int i=0; i<numberOfSubject; i++){
                paramName = "param${i}Name"
                paramSub = subjects."param${i}Name"
                paramsMap.put(paramName,paramSub)

                paramCtMark = "param${i}CtMark"
                ctExamMark = subjects."subject${i}ct"
                paramsMap.put(paramCtMark,ctExamMark)

                paramCtEffMark = "param${i}CtEffMark"
                ctEffMark = subjects."subject${i}ctEff"
                paramsMap.put(paramCtEffMark,ctEffMark)

                paramWriMark = "param${i}WriMark"
                wriMark = subjects."subject${i}hallWri"
                paramsMap.put(paramWriMark,wriMark)

                paramObjMark = "param${i}ObjMark"
                objMark = subjects."subject${i}hallObj"
                paramsMap.put(paramObjMark,objMark)

                paramPracMark = "param${i}PracMark"
                pracMark = subjects."subject${i}hallPrac"
                paramsMap.put(paramPracMark,pracMark)

                paramSbaMark = "param${i}SbaMark"
                sbaMark = subjects."subject${i}hallSba"
                paramsMap.put(paramSbaMark,sbaMark)

                paramHallEffMark = "param${i}HallEffMark"
                hallEffMark = subjects."subject${i}hallEff"
                paramsMap.put(paramHallEffMark,hallEffMark)

                paramHighestMark = "param${i}highest"
                highestMark = subjects."subject${i}highest"
                paramsMap.put(paramHighestMark, highestMark)
            }
        } else {
            for(int i=0; i<numberOfSubject; i++){
                paramName = "param${i}Name"
                paramSub = subjects."param${i}Name"
                paramsMap.put(paramName,paramSub)
            }
        }


        String extension = CommonUtils.PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.PDF_FORMAT

        PrintOptionType printOptionType = PrintOptionType.PDF
        if (params.printOptionType) {
            printOptionType = PrintOptionType.valueOf(params.printOptionType)
        }

        if(printOptionType && printOptionType !=PrintOptionType.PDF){
            if(printOptionType==PrintOptionType.XLSX){
                extension = CommonUtils.XLS_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_XLS
                exportFormat = JasperExportFormat.XLS_FORMAT
            } else if(printOptionType==PrintOptionType.DOCX){
                extension = CommonUtils.DOCX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_DOCX
                exportFormat = JasperExportFormat.DOCX_FORMAT
            }
        }
        String outputFileName = "Tabulation_Sheet_${exam.section?.name?.replaceAll(" ","_")}_${exam.examTerm.key}${extension}"
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

    def ctTabulation(Long id){
        ShiftExam shiftExam = ShiftExam.read(id)
        Exam exam
        if (params.examId){
            exam = Exam.read(params.getLong('examId'))
        }
        if(!shiftExam || !exam){
            flash.message =CommonUtils.COMMON_NOT_FOUND_MESSAGE
            render(view: '/baseReport/reportNotFound')
            return
        }
        if(!exam.ctExamStatus || exam.ctExamStatus == ExamStatus.NEW){
            flash.message ="Result not ready"
            render(view: '/baseReport/reportNotFound')
            return
        }

        def subIds = exam.ctSubjectIds.split(',')
        int numberOfSubject = subIds.size()
        if(numberOfSubject<3 || numberOfSubject>25){
            flash.message ="Tabulation only Supported from 3 to 25 subjects in Class."
            render(view: '/baseReport/reportNotFound')
            return
        }

        SubjectName subject
        ClassSubjects classSubjects
        ClassName className = exam.className
        def idValue

        def subjects = new ParamReference()
        for(int i=0; i<numberOfSubject; i++){
            idValue = subIds[i]
            subject = SubjectName.read(idValue.toLong())
            if(subject){
                classSubjects = ClassSubjects.findByClassNameAndSubjectAndActiveStatus(className, subject, ActiveStatus.ACTIVE)
                if(classSubjects.weightOnResult<100){
                    subjects."param${i}Name"= "${classSubjects.weightOnResult}% of ${subject.name}"
                }else {
                    subjects."param${i}Name"= subject.name
                }
            }
        }

        String reportFileName =schoolService.reportFileName(CommonUtils.MODULE_CT,"termCtTabulation${numberOfSubject}.jasper")
        String reportLogo = schoolService.reportLogoPath()

        String sortBy = params.sortBy
        String orderByStr = "std.roll_no asc"
        if(exam.ctExamStatus==ExamStatus.RESULT || exam.ctExamStatus==ExamStatus.PUBLISHED){
            if (sortBy == 'secMeritPos') {
                orderByStr = "tabu.position_in_section asc"
            } else if(sortBy == 'clsMeritPos') {
                orderByStr = "tabu.position_in_class asc"
            }
        }
        int studentCount = exam.studentCount?:0
        int attended = exam.attended?:0

        Map paramsMap = new LinkedHashMap()
        paramsMap.put('REPORT_LOGO', reportLogo)
//        paramsMap.put('SUBREPORT_DIR', subReportFolder)
        paramsMap.put('examId', exam?.id)

        paramsMap.put('className', className.name)
        paramsMap.put('sectionName', exam.section.name)
        paramsMap.put('groupName', exam.section.groupName?.key)
        paramsMap.put('academicYear', exam.academicYear.value)
        paramsMap.put('examName', shiftExam.examName)
        paramsMap.put('schoolName', message(code: "app.school.name"))
        paramsMap.put('schoolAddress', message(code: "app.report.address.line1"))

        paramsMap.put('numberOfStudent', studentCount)
        paramsMap.put('absentCount', studentCount - attended)
        paramsMap.put('orderByStr', orderByStr)

        // put subject name parameter up to 14 subjects
        String paramName
        String paramSub
        for(int i=0; i<numberOfSubject; i++){
            paramName = "param${i}Name"
            paramSub = subjects."param${i}Name"
            paramsMap.put(paramName,paramSub)
        }

        String extension = CommonUtils.PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.PDF_FORMAT

        PrintOptionType printOptionType = PrintOptionType.PDF
        if (params.printOptionType) {
            printOptionType = PrintOptionType.valueOf(params.printOptionType)
        }

        if(printOptionType && printOptionType !=PrintOptionType.PDF){
            if(printOptionType==PrintOptionType.XLSX){
                extension = CommonUtils.XLS_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_XLS
                exportFormat = JasperExportFormat.XLS_FORMAT
            } else if(printOptionType==PrintOptionType.DOCX){
                extension = CommonUtils.DOCX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_DOCX
                exportFormat = JasperExportFormat.DOCX_FORMAT
            }
        }
        String outputFileName = "Tabulation_Sheet_${exam.section?.name?.replaceAll(" ","_")}_${exam.examTerm.key}${extension}"
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

    def topsheet(){
        ShiftExam shiftExam
        if (params.examName){
            shiftExam = ShiftExam.read(params.getLong('examName'))
        }
        ClassName className
        if (params.classname) {
            className = ClassName.read(params.getLong('classname'))
        }
        GroupName groupName
        if (params.groupName){
            groupName = GroupName.valueOf(params.groupName)
        }
        if(!shiftExam){
            flash.message =CommonUtils.COMMON_NOT_FOUND_MESSAGE
            render(view: '/baseReport/reportNotFound')
            return
        }

        Exam exam
        if (params.examId){
            exam = Exam.read(params.examId)
        }

        def studentList
        if (exam) {
            studentList = studentService.allStudentForTabulation(exam.section)
        } else {
            studentList = studentService.byClassName(className, shiftExam.academicYear, groupName)
        }
        List classExamList
        if (exam) {
            classExamList = [exam]
        } else {
            classExamList = examService.classExamList(shiftExam, className, groupName)
            if (classExamList){
                exam = classExamList.first()
            }
        }
        int numberOfSubject = exam.numberOfSubject      //subIds.size()
        if(numberOfSubject < 4 || numberOfSubject > 20){
            flash.message ="Top Sheet only Supported from 4 to 20 subjects in Class."
            render(view: '/baseReport/reportNotFound')
            return
        }

        def subIds = exam.subjectIds.split(',')
        TopSheetReference topSheetReference = new TopSheetReference()
        SubjectName subject
        ClassSubjects classSubjects
        def idValue
        int failCount = 0
        int passCount = 0
        int numberOfExaminee = studentList.size()
        for(int i=0; i<numberOfSubject; i++){
            idValue = subIds[i]
            subject = SubjectName.read(idValue.toLong())
            if(subject){
                classSubjects = ClassSubjects.findByClassNameAndSubjectAndActiveStatus(className, subject, ActiveStatus.ACTIVE)
                if (classSubjects.subjectType == SubjectType.ALTERNATIVE) {
                    passCount = examMarkService.getExamSubjectPassCount(classExamList, studentList, classSubjects.alternativeSubIds)
                    topSheetReference."param${i}absent" = examMarkService.getExamSubjectAbsentCount(classExamList, studentList, classSubjects.alternativeSubIds)
                    topSheetReference."param${i}pass" = passCount
                    topSheetReference."param${i}fail" = numberOfExaminee - passCount
                    topSheetReference."param${i}aPlus" = examMarkService.getExamSubjectLetterGradeCount(classExamList, studentList, classSubjects.alternativeSubIds, LetterGrade.GRADE_A_PLUS.value)
                    topSheetReference."param${i}a" = examMarkService.getExamSubjectLetterGradeCount(classExamList, studentList, classSubjects.alternativeSubIds, LetterGrade.GRADE_A.value)
                    topSheetReference."param${i}aMinus" = examMarkService.getExamSubjectLetterGradeCount(classExamList, studentList, classSubjects.alternativeSubIds, LetterGrade.GRADE_A_MINUS.value)
                    topSheetReference."param${i}b" = examMarkService.getExamSubjectLetterGradeCount(classExamList, studentList, classSubjects.alternativeSubIds, LetterGrade.GRADE_B.value)
                    topSheetReference."param${i}c" = examMarkService.getExamSubjectLetterGradeCount(classExamList, studentList, classSubjects.alternativeSubIds, LetterGrade.GRADE_C.value)
                } else if (classSubjects.subjectType == SubjectType.OPTIONAL) {
                    topSheetReference."param${i}absent" = examMarkService.getExamSubjectAbsentCount(classExamList, studentList, subject)
                    topSheetReference."param${i}pass" = examMarkService.getExamSubjectPassCount(classExamList, studentList, subject)
                    topSheetReference."param${i}fail" = 0
                    topSheetReference."param${i}aPlus" = examMarkService.getExamSubjectLetterGradeCount(classExamList, studentList, subject, LetterGrade.GRADE_A_PLUS.value)
                    topSheetReference."param${i}a" = examMarkService.getExamSubjectLetterGradeCount(classExamList, studentList, subject, LetterGrade.GRADE_A.value)
                    topSheetReference."param${i}aMinus" = examMarkService.getExamSubjectLetterGradeCount(classExamList, studentList, subject, LetterGrade.GRADE_A_MINUS.value)
                    topSheetReference."param${i}b" = examMarkService.getExamSubjectLetterGradeCount(classExamList, studentList, subject, LetterGrade.GRADE_B.value)
                    topSheetReference."param${i}c" = examMarkService.getExamSubjectLetterGradeCount(classExamList, studentList, subject, LetterGrade.GRADE_C.value)
                } else {
                    passCount = examMarkService.getExamSubjectPassCount(classExamList, studentList, subject)
                    topSheetReference."param${i}absent" = examMarkService.getExamSubjectAbsentCount(classExamList, studentList, subject)
                    topSheetReference."param${i}pass" = passCount
                    topSheetReference."param${i}fail" = numberOfExaminee - passCount
                    topSheetReference."param${i}aPlus" = examMarkService.getExamSubjectLetterGradeCount(classExamList, studentList, subject, LetterGrade.GRADE_A_PLUS.value)
                    topSheetReference."param${i}a" = examMarkService.getExamSubjectLetterGradeCount(classExamList, studentList, subject, LetterGrade.GRADE_A.value)
                    topSheetReference."param${i}aMinus" = examMarkService.getExamSubjectLetterGradeCount(classExamList, studentList, subject, LetterGrade.GRADE_A_MINUS.value)
                    topSheetReference."param${i}b" = examMarkService.getExamSubjectLetterGradeCount(classExamList, studentList, subject, LetterGrade.GRADE_B.value)
                    topSheetReference."param${i}c" = examMarkService.getExamSubjectLetterGradeCount(classExamList, studentList, subject, LetterGrade.GRADE_C.value)
                }
                if(classSubjects && classSubjects.weightOnResult<100){
                    topSheetReference."param${i}Name"= "${classSubjects.weightOnResult}% of ${subject.name}"
                }else {
                    topSheetReference."param${i}Name"= subject.name
                }

            }
        }
        String reportFileName ="topsheet${numberOfSubject}.jasper"
        /*if(exam.examTerm==ExamTerm.FIRST_TERM){
            reportFileName ="topsheet${numberOfSubject}.jasper"
        }*/
        String reportLogo = schoolService.reportLogoPath()

        int numberOfSuccess = tabulationService.getNumberOfPassStudentCount(classExamList, studentList)


        Map paramsMap = new LinkedHashMap()
        paramsMap.put('REPORT_LOGO', reportLogo)
        paramsMap.put('SUBREPORT_DIR', schoolService.subreportDir())

        paramsMap.put('examId', exam?.id)
        paramsMap.put('className', exam.className.name)
        paramsMap.put('classNameId', exam.className.id)
        paramsMap.put('sectionName', groupName? groupName.value : exam.section.name)
        paramsMap.put('academicYear', exam.academicYear.value)
        paramsMap.put('examName', exam.shiftExam?.examName)
        paramsMap.put('resultPublishOn', CommonUtils.getUiDateStr(exam.shiftExam.resultPublishOn))
        //exam parametes
        paramsMap.put('numberOfStudent', numberOfExaminee)
        paramsMap.put('numberOfExaminee', numberOfExaminee)
        paramsMap.put('numberOfSuccess', numberOfSuccess)

        // put subject name parameter up to 14 subjects
        String paramName
        String paramSub
        String paramAbsent
        int paramAbsentScore
        String paramPass
        int paramPassScore

        String paramFail
        int paramFailScore

        String paramaPlus
        int paramaPlusScore
        String paramAGrade
        int paramAScore
        String paramaMinus
        int paramaMinusScore

        String paramBGrade
        int paramBScore
        String paramCGrade
        int paramCScore
        for(int i=0; i<numberOfSubject; i++){
            paramName = "param${i}Name"
            paramSub = topSheetReference."param${i}Name"
            paramsMap.put(paramName,paramSub)

            paramAbsent = "param${i}absent"
            paramAbsentScore = topSheetReference."param${i}absent"
            paramsMap.put(paramAbsent, paramAbsentScore)

            paramPass = "param${i}pass"
            paramPassScore = topSheetReference."param${i}pass"
            paramsMap.put(paramPass, paramPassScore)

            paramFail = "param${i}fail"
            paramFailScore = topSheetReference."param${i}fail"
            paramsMap.put(paramFail, paramFailScore)

            paramaPlus = "param${i}aPlus"
            paramaPlusScore = topSheetReference."param${i}aPlus"
            paramsMap.put(paramaPlus, paramaPlusScore)

            paramAGrade = "param${i}a"
            paramAScore = topSheetReference."param${i}a"
            paramsMap.put(paramAGrade, paramAScore)

            paramaMinus = "param${i}aMinus"
            paramaMinusScore = topSheetReference."param${i}aMinus"
            paramsMap.put(paramaMinus, paramaMinusScore)

            paramBGrade = "param${i}b"
            paramBScore = topSheetReference."param${i}b"
            paramsMap.put(paramBGrade, paramBScore)

            paramCGrade = "param${i}c"
            paramCScore = topSheetReference."param${i}c"
            paramsMap.put(paramCGrade, paramCScore)
        }
        PrintOptionType printOptionType
        if (params.printOptionType){
            printOptionType = PrintOptionType.valueOf(params.printOptionType)
        }
        if (!printOptionType) {
            printOptionType = PrintOptionType.PDF
        }

        String extension = CommonUtils.PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.PDF_FORMAT
        if(printOptionType !=PrintOptionType.PDF){
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
        String outputFileName = "Top_Sheet_${exam.section?.name?.replaceAll(" ","_")}${extension}"
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

    def meritList(Long id){
        ShiftExam shiftExam = ShiftExam.read(id)

        Exam exam
        if (params.examId){
            exam = Exam.read(params.getLong('examId'))
        }
        if(!shiftExam){
            flash.message =CommonUtils.COMMON_NOT_FOUND_MESSAGE
            render(view: '/baseReport/reportNotFound')
            return
        }
        ClassName className = ClassName.read(params.getLong('classname'))

        String sqlParam = "exam_id in ( select exam.id from exam inner join section on exam.section_id = section.id where exam.active_status ='ACTIVE' and exam.shift_exam_id = ${id}"

        String reportFileName
        GroupName groupName
        if (params.examId){
            reportFileName ="meritListReport"
            sqlParam += " and exam.section_id = ${exam.section.id} )"

            if (params.sortBy =="rollNo") {
                sqlParam += " order by sec.id, st.roll_no "
            } else if (params.sortBy =="secMeritPos") {
                sqlParam += " order by sec.id, ta.position_in_section "
            } else {
                sqlParam += " order by ta.position_in_class "
            }
        } else {
            reportFileName ="meritListReportByClass"
            if (params.groupName) {
                groupName = GroupName.valueOf(params.groupName)
                sqlParam += " and section.group_name = '"+params.groupName+"'"
            }
            sqlParam += " and exam.class_name_id = ${params.classname} )"

            if (params.sortBy =="rollNo") {
                sqlParam += " order by sec.id, st.roll_no "
            } else if (params.sortBy =="secMeritPos") {
                sqlParam += " order by sec.id, ta.position_in_section "
            } else {
                sqlParam += " order by ta.position_in_class "
            }
        }


       /* AcademicYear academicYear = AcademicYear.valueOf(params.academicYear)
*/
        int clsStdCount = studentService.numberOfClassStudent(className, groupName, shiftExam.academicYear)
        int clsPassCount = tabulationService.classPassStudentCount(shiftExam, className, groupName)
        int clsFailCount = clsStdCount - clsPassCount


        String reportLogo = schoolService.reportLogoPath()

        Map paramsMap = new LinkedHashMap()
        paramsMap.put('REPORT_LOGO', reportLogo)
        paramsMap.put('SUBREPORT_DIR', schoolService.subreportDir())
        paramsMap.put('sqlParam', sqlParam)
        paramsMap.put('examName', shiftExam.examName)
        paramsMap.put('groupName', groupName ? groupName.value : "General")
        paramsMap.put('clsStdCount', clsStdCount)
        paramsMap.put('clsFailCount', clsFailCount)
        paramsMap.put('clsPassCount', clsPassCount)


        PrintOptionType printOptionType
        if (params.printOptionType){
            printOptionType = PrintOptionType.valueOf(params.printOptionType)
        }
        if (!printOptionType) {
            printOptionType = PrintOptionType.PDF
        }

        String extension = CommonUtils.PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.PDF_FORMAT
        if(printOptionType !=PrintOptionType.PDF){
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
        String outputFileName = "MERIT_LIST${extension}"
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

    def meritListCt(Long id){
        ShiftExam shiftExam = ShiftExam.read(id)

        Exam exam
        if (params.examId){
            exam = Exam.read(params.getLong('examId'))
        }
     /*   if(!shiftExam){
            flash.message =CommonUtils.COMMON_NOT_FOUND_MESSAGE
            render(view: '/baseReport/reportNotFound')
            return
        }*/
        ClassName className = ClassName.read(params.getLong('classname'))

        String sqlParam = "exam_id in ( select exam.id from exam inner join section on exam.section_id = section.id where exam.active_status ='ACTIVE' and exam.shift_exam_id = ${id}"

        String reportFileName
        GroupName groupName
        if (params.examId){
            reportFileName =schoolService.reportFileName(CommonUtils.MODULE_CT,"ctMeritListReport.jasper")
            sqlParam += " and exam.section_id = ${exam.section.id} )"

            if (params.sortBy =="rollNo") {
                sqlParam += " order by sec.id, st.roll_no "
            } else if (params.sortBy =="secMeritPos") {
                sqlParam += " order by sec.id, ta.position_in_section "
            } else {
                sqlParam += " order by ta.position_in_class "
            }
        } else {
            reportFileName =schoolService.reportFileName(CommonUtils.MODULE_CT,"ctMeritListReportByClass.jasper")
            if (params.groupName) {
                groupName = GroupName.valueOf(params.groupName)
                sqlParam += " and section.group_name = '"+params.groupName+"'"
            }
            sqlParam += " and exam.class_name_id = ${params.classname} )"

            if (params.sortBy =="rollNo") {
                sqlParam += " order by sec.id, st.roll_no "
            } else if (params.sortBy =="secMeritPos") {
                sqlParam += " order by sec.id, ta.position_in_section "
            } else {
                sqlParam += " order by ta.position_in_class "
            }
        }


        /* AcademicYear academicYear = AcademicYear.valueOf(params.academicYear)
 */
        int clsStdCount = studentService.numberOfClassStudent(className, groupName, shiftExam.academicYear)
        int clsPassCount = 0 //tabulationService.classPassStudentCount(shiftExam, className, groupName)
        int clsFailCount = 0 //clsStdCount - clsPassCount


        String reportLogo = schoolService.reportLogoPath()

        Map paramsMap = new LinkedHashMap()
        paramsMap.put('REPORT_LOGO', reportLogo)
        paramsMap.put('SUBREPORT_DIR', schoolService.subreportDir())
        paramsMap.put('sqlParam', sqlParam)
        paramsMap.put('examName', shiftExam.examName)
        paramsMap.put('groupName', groupName ? groupName.value : "General")
        paramsMap.put('clsStdCount', clsStdCount)
        paramsMap.put('clsFailCount', clsFailCount)
        paramsMap.put('clsPassCount', clsPassCount)


        PrintOptionType printOptionType
        if (params.printOptionType){
            printOptionType = PrintOptionType.valueOf(params.printOptionType)
        }
        if (!printOptionType) {
            printOptionType = PrintOptionType.PDF
        }

        String extension = CommonUtils.PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.PDF_FORMAT
        if(printOptionType !=PrintOptionType.PDF){
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
        String outputFileName = "MERIT_LIST${extension}"
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

    def promotionList(Long id){
        ShiftExam shiftExam = ShiftExam.read(id)

        if(!shiftExam || shiftExam.examTerm != ExamTerm.FINAL_TEST){
            flash.message =CommonUtils.COMMON_NOT_FOUND_MESSAGE
            render(view: '/baseReport/reportNotFound')
            return
        }
        ClassName className = ClassName.read(params.getLong('classname'))

        def nextClasses = classNameService.nextClasses(className)
        if(!nextClasses){
            flash.message = "Nex class not found"
            render(view: '/baseReport/reportNotFound')
            return
        }
        ClassName nextClass = nextClasses.first()
        if(!nextClass){
            flash.message = "Nex class not found"
            render(view: '/baseReport/reportNotFound')
            return
        }
        String sqlParam = " and exam_id in ( select id from exam where active_status ='ACTIVE' and shift_exam_id = ${id} and class_name_id=$className.id)"

        String reportFileName ="meritPromotionList"

        String reportLogo = schoolService.reportLogoPath()

        Map paramsMap = new LinkedHashMap()
        paramsMap.put('REPORT_LOGO', reportLogo)
        paramsMap.put('SUBREPORT_DIR', schoolService.subreportDir())
        paramsMap.put('sqlParam', sqlParam)
        paramsMap.put('examName', shiftExam.examName)
        paramsMap.put('oldClsName', className.name)
        paramsMap.put('nextClassName', nextClass?.name)


        PrintOptionType printOptionType
        if (params.printOptionType){
            printOptionType = PrintOptionType.valueOf(params.printOptionType)
        }
        if (!printOptionType) {
            printOptionType = PrintOptionType.PDF
        }

        String extension = CommonUtils.PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.PDF_FORMAT
        if(printOptionType !=PrintOptionType.PDF){
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
        String outputFileName = "promotion_list${extension}"
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



    def sectionReportCard(Long id){
        ShiftExam shiftExam = ShiftExam.read(id)
        Exam exam
        if (params.examId){
            exam = Exam.read(params.getLong('examId'))
        }
        if(!shiftExam || !exam){
            flash.message =CommonUtils.COMMON_NOT_FOUND_MESSAGE
            render(view: '/baseReport/reportNotFound')
            return
        }
        if(exam.examStatus == ExamStatus.NEW){
            flash.message ="Result not ready"
            render(view: '/baseReport/reportNotFound')
            return
        }
        Section section = exam.section
        ClassName className = exam.className
        int numberOfSubject = exam.numberOfSubject  //subIds.size()
        String nextClassName

        String reportFileName
        String runningSchool = schoolService.runningSchool()
        String termPercentage
        if(exam.examTerm==ExamTerm.FINAL_TEST){
            if (runningSchool == CommonUtils.NARAYANGANJ_ADARSHA_SCHOOL) {
                def nextClasses = classNameService.nextClasses(className)
                if(nextClasses){
                    nextClassName = nextClasses.first().name
                }

                reportFileName=schoolService.reportFileName(FINAL_REPORT_CARD_10LESS_JASPER_FILE)
                if(numberOfSubject >= 10){
                    reportFileName=schoolService.reportFileName(FINAL_REPORT_CARD_10PLUS_JASPER_FILE)
                }
            } else if (runningSchool == CommonUtils.NARAYANGANJ_IDEAL_SCHOOL || runningSchool == CommonUtils.NARAYANGANJ_HIGH_SCHOOL) {
                if (className.subjectGroup){
                    reportFileName=schoolService.reportFileName(FINAL_REPORT_CARD_WITH_GROUP_JASPER_FILE)
                } else {
                    reportFileName=schoolService.reportFileName(FINAL_REPORT_CARD_NO_GROUP_JASPER_FILE)
                }
            } else {
                int termExam = Exam.countByExamTermAndSectionAndActiveStatus(ExamTerm.FIRST_TERM, section, ActiveStatus.ACTIVE)
                termPercentage = "5"
                if (termExam == 2) {
                    termPercentage = "10"
                }
                reportFileName=schoolService.reportFileName(FINAL_REPORT_CARD_10LESS_JASPER_FILE)
                if(numberOfSubject>10 && numberOfSubject<14){
                    reportFileName=schoolService.reportFileName(FINAL_REPORT_CARD_10PLUS_JASPER_FILE)
                }else if(numberOfSubject>=14){
                    reportFileName=schoolService.reportFileName(FINAL_REPORT_CARD_14ANDPLUS_JASPER_FILE)
                }
            }
        }else {
            if (runningSchool == CommonUtils.NARAYANGANJ_ADARSHA_SCHOOL) {
                reportFileName=schoolService.reportFileName(TERM_REPORT_CARD_10LESS_JASPER_FILE)
                if(numberOfSubject >= 10){
                    reportFileName=schoolService.reportFileName(TERM_REPORT_CARD_10PLUS_JASPER_FILE)
                }
            } else if (runningSchool == CommonUtils.NARAYANGANJ_IDEAL_SCHOOL || runningSchool == CommonUtils.NARAYANGANJ_HIGH_SCHOOL) {
                if (className.subjectGroup){
                    reportFileName=schoolService.reportFileName(TERM_REPORT_CARD_WITH_GROUP_JASPER_FILE)
                } else {
                    reportFileName=schoolService.reportFileName(TERM_REPORT_CARD_NO_GROUP_JASPER_FILE)
                }
            } else {
                reportFileName=schoolService.reportFileName(TERM_REPORT_CARD_10LESS_JASPER_FILE)
                if(numberOfSubject>10 && numberOfSubject<14){
                    reportFileName=schoolService.reportFileName(TERM_REPORT_CARD_10PLUS_JASPER_FILE)
                }else if(numberOfSubject>=14){
                    reportFileName=schoolService.reportFileName(TERM_REPORT_CARD_14ANDPLUS_JASPER_FILE)
                }
            }
        }
        String stdIDs
        if(params.student){
            stdIDs = params.student
        } else {
            def studentList = studentService.sectionStudentIdsList(section)
            if(!studentList){
                flash.message ="No student found for the exam."
                render(view: '/baseReport/reportNotFound')
                return
            }
            stdIDs=studentList.join(",")
        }

        String reportLogo = schoolService.reportLogoPath()
        Map paramsMap = new LinkedHashMap()
        paramsMap.put('REPORT_LOGO', reportLogo)
        paramsMap.put('SUBREPORT_DIR', schoolService.schoolSubreportDir())
        paramsMap.put('examId', exam.id)
        paramsMap.put('stdIDs', stdIDs)
        paramsMap.put('className', className.name)
        paramsMap.put('classNameId', className.id)
        paramsMap.put('sectionName', section.name)
        paramsMap.put('groupName', section.groupName?.key)
        paramsMap.put('secTeacher', section?.employee?.name)
        paramsMap.put('academicYear', exam.academicYear.value)
        paramsMap.put('examName', shiftExam?.examName)
        paramsMap.put('schoolName', message(code: "app.school.name"))
        paramsMap.put('schoolAddress', message(code: "app.report.address.line1"))
        paramsMap.put('resultPublishOn', CommonUtils.getUiDateStr(shiftExam.resultPublishOn))
        paramsMap.put('workingDays', className.workingDays)
        if (runningSchool == CommonUtils.BAILY_SCHOOL) {
            ExamSchedule exBP = ExamSchedule.findByExamAndIsExtracurricularAndActiveStatus(exam, true, ActiveStatus.ACTIVE)
            paramsMap.put('extraSubId', exBP?.subject?.id)
            paramsMap.put('termPercentage', termPercentage)
        } else if (runningSchool == CommonUtils.NARAYANGANJ_IDEAL_SCHOOL || runningSchool == CommonUtils.NARAYANGANJ_HIGH_SCHOOL){
            paramsMap.put('numberOfSubject', numberOfSubject%2 == 0 ? numberOfSubject/2 : (numberOfSubject + 1)/2)
            paramsMap.put('durationStr', CommonUtils.getDateRangeStr(shiftExam.periodStart, shiftExam.periodEnd))
        } else if (runningSchool == CommonUtils.NARAYANGANJ_ADARSHA_SCHOOL){
            paramsMap.put('durationStr', CommonUtils.getDateRangeStr(shiftExam.periodStart, shiftExam.periodEnd))
            paramsMap.put('nextClassName', nextClassName)
        }

        String extension = CommonUtils.PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.PDF_FORMAT

        PrintOptionType printOptionType = PrintOptionType.valueOf(params.printOptionType)
        if (!printOptionType) {
            printOptionType = PrintOptionType.PDF
        }
        if(printOptionType != PrintOptionType.PDF){
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
        String outputFileName = "Progress_Report_${shiftExam.examName}_${section.name}${extension}"

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

    def examScheduleBySection(ExamScheduleBySectionCommand command){
        if (command.hasErrors()) {
            def errorList = command?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            flash.message = errorList?.join('. ')
            render(view: '/baseReport/reportNotFound')
            return
        }
        AcademicYear workingYear = schoolService.workingYear(command.exam?.className)
        String examIds
        ShiftExam shiftExam
        AcademicYear academicYear
        ExamTerm examTerm
        if(command.shift && command.examTerm){
            shiftExam= ShiftExam.findByShiftAndExamTermAndAcademicYear(command.shift,command.examTerm, workingYear)
            if(!shiftExam){
                flash.message = "No Schedule available for ${command.shift.value} ${command.examTerm.value} exam."
                render(view: '/baseReport/reportNotFound')
                return
            }

            def exams = Exam.findAllByShiftExam(shiftExam)
            if(!exams){
                flash.message = "No Schedule available for ${shiftExam} ${command.examType.value}."
                render(view: '/baseReport/reportNotFound')
                return
            }
            def examLdList = exams.collect {it.id}
            examIds = examLdList.join(',')
            academicYear=shiftExam.academicYear
            examTerm = command.examTerm
        } else if(command.exam){
            Exam exam = command.exam
            examIds = exam.id.toString()
            shiftExam = exam.shiftExam
            academicYear = exam.academicYear
            examTerm = exam.examTerm
        } else {
            flash.message = "No Schedule available."
            render(view: '/baseReport/reportNotFound')
            return
        }

        String reportFileName = EXAM_SCHEDULE_BY_SECTION_HALL_JASPER_FILE
        if(command.examType==ExamType.CLASS_TEST){
            reportFileName = EXAM_SCHEDULE_BY_SECTION_CT_JASPER_FILE
        }
        String reportLogo = schoolService.reportLogoPath()
        String outputFileName = "section_schedule${CommonUtils.PDF_EXTENSION}"
        Map paramsMap = new LinkedHashMap()
        paramsMap.put('REPORT_LOGO', reportLogo)
        paramsMap.put('academicYear', academicYear.value)
        paramsMap.put('examTerm', examTerm.value)
        paramsMap.put('resultPublishOn', shiftExam.resultPublishOn)
        paramsMap.put('examId', examIds)
        JasperReportDef reportDef = new JasperReportDef(
                name: reportFileName,
                fileFormat: JasperExportFormat.PDF_FORMAT,
                parameters: paramsMap
        )
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        response.contentType =grailsApplication.config.grails.mime.types[CommonUtils.REPORT_FILE_FORMAT_PDF]
        response.setHeader("Content-disposition", "inline;filename=${outputFileName}")
        response.outputStream << report.toByteArray()
    }

    def classSchedule(Long id){
        Section section = Section.read(id)
        if(!section){
            flash.message =CommonUtils.COMMON_NOT_FOUND_MESSAGE
            render(view: '/baseReport/reportNotFound')
            return
        }
        String reportFileName = CLASS_SCHEDULE_JASPER_FILE

        String reportLogo = schoolService.reportLogoPath()
        String outputFileName = "Class_Schedule_${section?.name}${CommonUtils.PDF_EXTENSION}"
        Map paramsMap = new LinkedHashMap()
        paramsMap.put('REPORT_LOGO', reportLogo)
        paramsMap.put('sectionId', section.id)
        paramsMap.put('sectionName', section.name)
        paramsMap.put('secTeacher', section?.employee?.name)

        JasperReportDef reportDef = new JasperReportDef(
                name: reportFileName,
                fileFormat: JasperExportFormat.PDF_FORMAT,
                parameters: paramsMap
        )
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        response.contentType =grailsApplication.config.grails.mime.types[CommonUtils.REPORT_FILE_FORMAT_PDF]
        response.setHeader("Content-disposition", "inline;filename=${outputFileName}")
        response.outputStream << report.toByteArray()
    }
    def subjectStudent(SubjectStudentReportCommand command){
        if (command.hasErrors()) {
            def errorList = command?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            flash.message =errorList?.join('. ')
            render(view: '/baseReport/reportNotFound')
            return
        }
        String reportFileName = SUBJECT_STUDENT_LIST_JASPER_FILE
        SubjectName subjectName = command.subjectName

        Section section = command.section

        String reportLogo = schoolService.reportLogoPath()
        Map paramsMap = new LinkedHashMap()
        paramsMap.put('schoolName', message(code: "app.school.name"))
        paramsMap.put('schoolAddress', message(code: "app.report.address.line1"))
        paramsMap.put('creditLine', message(code: "company.credit.footer"))
        paramsMap.put('REPORT_LOGO', reportLogo)
        paramsMap.put('className', command.className.id)
        paramsMap.put('section', section.id)
        paramsMap.put('subjectName', subjectName.id)

        String extension = CommonUtils.PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.PDF_FORMAT
        if(command.printOptionType && command.printOptionType !=PrintOptionType.PDF){
            if(command.printOptionType==PrintOptionType.XLSX){
                extension = CommonUtils.XLSX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_XLSX
                exportFormat = JasperExportFormat.XLSX_FORMAT
            } else if(command.printOptionType==PrintOptionType.DOCX){
                extension = CommonUtils.DOCX_EXTENSION
                mimeType = CommonUtils.REPORT_FILE_FORMAT_DOCX
                exportFormat = JasperExportFormat.DOCX_FORMAT
            }
        }

        String outputFileName = "StudentList_${command.className.name}_${command.section.name}_${subjectName.name}${extension}"
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

    def markEntryStatus(Long id){

        ShiftExam shiftExam= ShiftExam.read(id)
        Exam exam = Exam.read(params.getLong('exam'))

        String reportFileName = EXAM_MARK_ENTRY_STATUS_JASPER_FILE

        String sqlParams = " ex.shift_exam_id = ${shiftExam.id} "
        if (exam) {
            sqlParams += " and exs.exam_id=${exam.id} "
        }


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
        String outputFileName = "markEntryStatus${extension}"
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

    def resultAnalysis(ResultAnalysisCommand command){
        if (command.hasErrors()) {
            def errorList = command?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            flash.message =errorList?.join('. ')
            render(view: '/baseReport/reportNotFound')
            return
        }



        def publishingExamList
        if (command.examId) {
            publishingExamList = new ArrayList()
            publishingExamList.add(command.examId)
        } else {
            publishingExamList = examService.publishingExamList(command.examName, command.examType, command.className, command.groupName)
        }

        def sectionList

        String reportFileName

        List referenceList = new ArrayList()

        if (command.analysisReportType == "summaryReport") {
            reportFileName = EXAMINATION_RESULT_ANALYSIS_SUMMERY
            Integer maleStudentCount = 0
            Integer malePassCount = 0
            Integer maleaPlusCount = 0
            Integer maleaCount = 0
            Integer maleaMinusCount = 0
            Integer malebCount = 0
            Integer malecCount = 0

            sectionList = publishingExamList.collect {it.section}
            def maleStudentIds = studentService.studentIdsByGender(command.academicYear, sectionList, command.religionName, command.genderName, Gender.MALE)
            if (command.examType == ExamType.HALL_TEST) {
                maleStudentCount = tabulationService.hallStudentCount(publishingExamList, maleStudentIds, null, null)
                if (maleStudentCount > 0) {
                    malePassCount = tabulationService.hallStudentCount( publishingExamList, maleStudentIds, ResultStatus.PASSED, null)
                    maleaPlusCount = tabulationService.hallStudentCount( publishingExamList, maleStudentIds, ResultStatus.PASSED, LetterGrade.GRADE_A_PLUS.value)
                    maleaCount = tabulationService.hallStudentCount( publishingExamList, maleStudentIds, ResultStatus.PASSED, LetterGrade.GRADE_A.value)
                    maleaMinusCount = tabulationService.hallStudentCount( publishingExamList, maleStudentIds, ResultStatus.PASSED, LetterGrade.GRADE_A_MINUS.value)
                    malebCount = tabulationService.hallStudentCount( publishingExamList, maleStudentIds, ResultStatus.PASSED, LetterGrade.GRADE_B.value)
                    malecCount = tabulationService.hallStudentCount( publishingExamList, maleStudentIds, ResultStatus.PASSED, LetterGrade.GRADE_C.value)
                }
            }


            Integer maleFailCount = maleStudentCount - malePassCount
            Integer malefCount = maleFailCount

            Integer femaleStudentCount = 0
            Integer femalePassCount = 0
            Integer femaleaPlusCount = 0
            Integer femaleaCount = 0
            Integer femaleaMinusCount = 0
            Integer femalebCount = 0
            Integer femalecCount = 0

            def femaleStudentIds = studentService.studentIdsByGender(command.academicYear, sectionList, command.religionName, command.genderName, Gender.FEMALE)
            if (command.examType == ExamType.HALL_TEST) {
                femaleStudentCount = tabulationService.hallStudentCount( publishingExamList, femaleStudentIds, null, null)
                if (maleStudentCount > 0) {
                    femalePassCount = tabulationService.hallStudentCount( publishingExamList, femaleStudentIds, ResultStatus.PASSED, null)
                    femaleaPlusCount = tabulationService.hallStudentCount( publishingExamList, femaleStudentIds, ResultStatus.PASSED, LetterGrade.GRADE_A_PLUS.value)
                    femaleaCount = tabulationService.hallStudentCount( publishingExamList, femaleStudentIds, ResultStatus.PASSED, LetterGrade.GRADE_A.value)
                    femaleaMinusCount = tabulationService.hallStudentCount( publishingExamList, femaleStudentIds, ResultStatus.PASSED, LetterGrade.GRADE_A_MINUS.value)
                    femalebCount = tabulationService.hallStudentCount( publishingExamList, femaleStudentIds, ResultStatus.PASSED, LetterGrade.GRADE_B.value)
                    femalecCount = tabulationService.hallStudentCount( publishingExamList, femaleStudentIds, ResultStatus.PASSED, LetterGrade.GRADE_C.value)
                }
            }
            Integer femaleFailCount = femaleStudentCount - femalePassCount
            Integer femalefCount = femaleFailCount

            Integer studentCount = maleStudentCount + femaleStudentCount
            Integer passCount = malePassCount + femalePassCount
            Integer failCount = studentCount - passCount
            Integer aPlusCount = maleaPlusCount + femaleaPlusCount
            Integer aCount = maleaCount + femaleaCount
            Integer aMinusCount = maleaMinusCount + femaleaMinusCount
            Integer bCount = malebCount + femalebCount
            Integer cCount = malecCount + femalecCount
            Integer fCount = failCount

            //by Religion
            Integer muslimStudentCount = 0
            Integer muslimPassCount = 0
            Integer muslimaPlusCount = 0
            Integer muslimaCount = 0
            Integer muslimaMinusCount = 0
            Integer muslimbCount = 0
            Integer muslimcCount = 30

            def muslimStudentIds = studentService.studentIdsByReligion(command.academicYear, sectionList, command.genderName, command.religionName, Religion.ISLAM)
            if (command.examType == ExamType.HALL_TEST) {
                muslimStudentCount = tabulationService.hallStudentCount( publishingExamList, muslimStudentIds, null, null)
                if (muslimStudentCount > 0) {
                    muslimPassCount = tabulationService.hallStudentCount( publishingExamList, muslimStudentIds, ResultStatus.PASSED, null)
                    muslimaPlusCount = tabulationService.hallStudentCount( publishingExamList, muslimStudentIds, ResultStatus.PASSED, LetterGrade.GRADE_A_PLUS.value)
                    muslimaCount = tabulationService.hallStudentCount( publishingExamList, muslimStudentIds, ResultStatus.PASSED, LetterGrade.GRADE_A.value)
                    muslimaMinusCount = tabulationService.hallStudentCount( publishingExamList, muslimStudentIds, ResultStatus.PASSED, LetterGrade.GRADE_A_MINUS.value)
                    muslimbCount = tabulationService.hallStudentCount( publishingExamList, muslimStudentIds, ResultStatus.PASSED, LetterGrade.GRADE_B.value)
                    muslimcCount = tabulationService.hallStudentCount( publishingExamList, muslimStudentIds, ResultStatus.PASSED, LetterGrade.GRADE_C.value)
                }
            }

            Integer muslimFailCount = muslimStudentCount - muslimPassCount
            Integer muslimfCount = muslimFailCount

            Integer hinduStudentCount = 0
            Integer hinduPassCount = 0
            Integer hinduaPlusCount = 0
            Integer hinduaCount = 0
            Integer hinduaMinusCount = 0
            Integer hindubCount = 0
            Integer hinducCount = 0

            def hinduStudentIds = studentService.studentIdsByReligion(command.academicYear, sectionList, command.genderName, command.religionName, Religion.HINDU)
            if (command.examType == ExamType.HALL_TEST) {
                hinduStudentCount = tabulationService.hallStudentCount( publishingExamList, hinduStudentIds, null, null)
                if (hinduStudentCount > 0) {
                    hinduPassCount = tabulationService.hallStudentCount( publishingExamList, hinduStudentIds, ResultStatus.PASSED, null)
                    hinduaPlusCount = tabulationService.hallStudentCount( publishingExamList, hinduStudentIds, ResultStatus.PASSED, LetterGrade.GRADE_A_PLUS.value)
                    hinduaCount = tabulationService.hallStudentCount( publishingExamList, hinduStudentIds, ResultStatus.PASSED, LetterGrade.GRADE_A.value)
                    hinduaMinusCount = tabulationService.hallStudentCount( publishingExamList, hinduStudentIds, ResultStatus.PASSED, LetterGrade.GRADE_A_MINUS.value)
                    hindubCount = tabulationService.hallStudentCount( publishingExamList, hinduStudentIds, ResultStatus.PASSED, LetterGrade.GRADE_B.value)
                    hinducCount = tabulationService.hallStudentCount( publishingExamList, hinduStudentIds, ResultStatus.PASSED, LetterGrade.GRADE_C.value)
                }
            }

            Integer hinduFailCount = hinduStudentCount - hinduPassCount
            Integer hindufCount = hinduFailCount

            Integer otherStudentCount = studentCount - (muslimStudentCount + hinduStudentCount)
            Integer otherPassCount = passCount - (muslimPassCount + hinduPassCount)
            Integer otherFailCount = otherStudentCount - otherPassCount
            Integer otheraPlusCount = aPlusCount - (muslimaPlusCount + hinduaPlusCount)
            Integer otheraCount = aCount - (muslimaCount + hinduaCount)
            Integer otheraMinusCount = aMinusCount - (muslimaMinusCount + hinduaMinusCount)
            Integer otherbCount = bCount - (muslimbCount + hindubCount)
            Integer othercCount = cCount - (muslimcCount + hinducCount)
            Integer otherfCount = otherFailCount

            ResultAnalysisReference analysisReference = new ResultAnalysisReference(studentCount, passCount, failCount, aPlusCount, aCount, aMinusCount, bCount, cCount, fCount,
                    maleStudentCount, malePassCount, maleFailCount, maleaPlusCount, maleaCount, maleaMinusCount, malebCount, malecCount, malefCount,
                    femaleStudentCount, femalePassCount, femaleFailCount, femaleaPlusCount, femaleaCount, femaleaMinusCount, femalebCount, femalecCount, femalefCount,
                    muslimStudentCount, muslimPassCount, muslimFailCount, muslimaPlusCount, muslimaCount, muslimaMinusCount, muslimbCount, muslimcCount, muslimfCount,
                    hinduStudentCount, hinduPassCount, hinduFailCount, hinduaPlusCount, hinduaCount, hinduaMinusCount, hindubCount, hinducCount, hindufCount,
                    otherStudentCount, otherPassCount, otherFailCount, otheraPlusCount, otheraCount, otheraMinusCount, otherbCount, othercCount, otherfCount)
            referenceList.add(analysisReference)

        } else if (command.analysisReportType == "byClass") {
            reportFileName = EXAMINATION_RESULT_ANALYSIS_BYCLASS

            String reportType
            String className
            String sectionName

            Integer passCount
            Integer failCount
            Integer aPlusCount
            Integer aCount
            Integer aMinusCount
            Integer bCount
            Integer cCount
            Integer numberOfStudent
            Integer fCount
            def analysisStudentIds
            Gender gender
            Religion religion
            for (examItem in publishingExamList) {
                reportType = "All"
                className = examItem.className.name
                sectionName = examItem.section.name
                gender = command.genderName
                religion = command.religionName
                analysisStudentIds = studentService.studentListBySection(command.academicYear, examItem.section, gender, religion)
                numberOfStudent = tabulationService.hallStudentCount( publishingExamList, analysisStudentIds, null, null)
                passCount = tabulationService.hallStudentCount( publishingExamList, analysisStudentIds, ResultStatus.PASSED, null)
                failCount = numberOfStudent - passCount
                aPlusCount = tabulationService.hallStudentCount( publishingExamList, analysisStudentIds, ResultStatus.PASSED, LetterGrade.GRADE_A_PLUS.value)
                aCount = tabulationService.hallStudentCount( publishingExamList, analysisStudentIds, ResultStatus.PASSED, LetterGrade.GRADE_A.value)
                aMinusCount = tabulationService.hallStudentCount( publishingExamList, analysisStudentIds, ResultStatus.PASSED, LetterGrade.GRADE_A_MINUS.value)
                bCount = tabulationService.hallStudentCount( publishingExamList, analysisStudentIds, ResultStatus.PASSED, LetterGrade.GRADE_B.value)
                cCount = tabulationService.hallStudentCount( publishingExamList, analysisStudentIds, ResultStatus.PASSED, LetterGrade.GRADE_C.value)
                fCount = failCount
                referenceList.add(new ResultClassReference(reportType, className, sectionName, numberOfStudent, passCount, failCount, aPlusCount, aCount, aMinusCount, bCount, cCount, fCount))

                reportType = "Male"
                gender = Gender.MALE
                analysisStudentIds = studentService.studentListBySection(command.academicYear, examItem.section, gender, religion)
                numberOfStudent = tabulationService.hallStudentCount( publishingExamList, analysisStudentIds, null, null)
                passCount = tabulationService.hallStudentCount( publishingExamList, analysisStudentIds, ResultStatus.PASSED, null)
                failCount = numberOfStudent - passCount
                aPlusCount = tabulationService.hallStudentCount( publishingExamList, analysisStudentIds, ResultStatus.PASSED, LetterGrade.GRADE_A_PLUS.value)
                aCount = tabulationService.hallStudentCount( publishingExamList, analysisStudentIds, ResultStatus.PASSED, LetterGrade.GRADE_A.value)
                aMinusCount = tabulationService.hallStudentCount( publishingExamList, analysisStudentIds, ResultStatus.PASSED, LetterGrade.GRADE_A_MINUS.value)
                bCount = tabulationService.hallStudentCount( publishingExamList, analysisStudentIds, ResultStatus.PASSED, LetterGrade.GRADE_B.value)
                cCount = tabulationService.hallStudentCount( publishingExamList, analysisStudentIds, ResultStatus.PASSED, LetterGrade.GRADE_C.value)
                fCount = failCount
                referenceList.add(new ResultClassReference(reportType, className, sectionName, numberOfStudent, passCount, failCount, aPlusCount, aCount, aMinusCount, bCount, cCount, fCount))

                reportType = "Female"
                gender = Gender.FEMALE
                analysisStudentIds = studentService.studentListBySection(command.academicYear, examItem.section, gender, religion)
                numberOfStudent = tabulationService.hallStudentCount( publishingExamList, analysisStudentIds, null, null)
                passCount = tabulationService.hallStudentCount( publishingExamList, analysisStudentIds, ResultStatus.PASSED, null)
                failCount = numberOfStudent - passCount
                aPlusCount = tabulationService.hallStudentCount( publishingExamList, analysisStudentIds, ResultStatus.PASSED, LetterGrade.GRADE_A_PLUS.value)
                aCount = tabulationService.hallStudentCount( publishingExamList, analysisStudentIds, ResultStatus.PASSED, LetterGrade.GRADE_A.value)
                aMinusCount = tabulationService.hallStudentCount( publishingExamList, analysisStudentIds, ResultStatus.PASSED, LetterGrade.GRADE_A_MINUS.value)
                bCount = tabulationService.hallStudentCount( publishingExamList, analysisStudentIds, ResultStatus.PASSED, LetterGrade.GRADE_B.value)
                cCount = tabulationService.hallStudentCount( publishingExamList, analysisStudentIds, ResultStatus.PASSED, LetterGrade.GRADE_C.value)
                fCount = failCount
                referenceList.add(new ResultClassReference(reportType, className, sectionName, numberOfStudent, passCount, failCount, aPlusCount, aCount, aMinusCount, bCount, cCount, fCount))

                reportType = "Muslim"
                gender = command.genderName
                religion = Religion.ISLAM
                analysisStudentIds = studentService.studentListBySection(command.academicYear, examItem.section, gender, religion)
                numberOfStudent = tabulationService.hallStudentCount( publishingExamList, analysisStudentIds, null, null)
                passCount = tabulationService.hallStudentCount( publishingExamList, analysisStudentIds, ResultStatus.PASSED, null)
                failCount = numberOfStudent - passCount
                aPlusCount = tabulationService.hallStudentCount( publishingExamList, analysisStudentIds, ResultStatus.PASSED, LetterGrade.GRADE_A_PLUS.value)
                aCount = tabulationService.hallStudentCount( publishingExamList, analysisStudentIds, ResultStatus.PASSED, LetterGrade.GRADE_A.value)
                aMinusCount = tabulationService.hallStudentCount( publishingExamList, analysisStudentIds, ResultStatus.PASSED, LetterGrade.GRADE_A_MINUS.value)
                bCount = tabulationService.hallStudentCount( publishingExamList, analysisStudentIds, ResultStatus.PASSED, LetterGrade.GRADE_B.value)
                cCount = tabulationService.hallStudentCount( publishingExamList, analysisStudentIds, ResultStatus.PASSED, LetterGrade.GRADE_C.value)
                fCount = failCount
                referenceList.add(new ResultClassReference(reportType, className, sectionName, numberOfStudent, passCount, failCount, aPlusCount, aCount, aMinusCount, bCount, cCount, fCount))

                reportType = "Hindu"
                gender = command.genderName
                religion = Religion.HINDU
                analysisStudentIds = studentService.studentListBySection(command.academicYear, examItem.section, gender, religion)
                numberOfStudent = tabulationService.hallStudentCount( publishingExamList, analysisStudentIds, null, null)
                passCount = tabulationService.hallStudentCount( publishingExamList, analysisStudentIds, ResultStatus.PASSED, null)
                failCount = numberOfStudent - passCount
                aPlusCount = tabulationService.hallStudentCount( publishingExamList, analysisStudentIds, ResultStatus.PASSED, LetterGrade.GRADE_A_PLUS.value)
                aCount = tabulationService.hallStudentCount( publishingExamList, analysisStudentIds, ResultStatus.PASSED, LetterGrade.GRADE_A.value)
                aMinusCount = tabulationService.hallStudentCount( publishingExamList, analysisStudentIds, ResultStatus.PASSED, LetterGrade.GRADE_A_MINUS.value)
                bCount = tabulationService.hallStudentCount( publishingExamList, analysisStudentIds, ResultStatus.PASSED, LetterGrade.GRADE_B.value)
                cCount = tabulationService.hallStudentCount( publishingExamList, analysisStudentIds, ResultStatus.PASSED, LetterGrade.GRADE_C.value)
                fCount = failCount
                referenceList.add(new ResultClassReference(reportType, className, sectionName, numberOfStudent, passCount, failCount, aPlusCount, aCount, aMinusCount, bCount, cCount, fCount))
            }
        } else {
            reportFileName = EXAMINATION_RESULT_ANALYSIS_BYGRADE

            String examSubjectIds
            if (command.examType == ExamType.HALL_TEST) {
                if (command.subjectName) {
                    examSubjectIds = command.subjectName.id.toString()
                }else {
                    examSubjectIds = publishingExamList.collect {it.subjectIds}.join(",")
                }
                def subjectList = subjectService.allSubjectInList(examSubjectIds)

                String reportType
                String subjectName
                Integer numberOfStudent
                Integer passCount
                Integer failCount
                Integer aPlusCount
                Integer aCount
                Integer aMinusCount
                Integer bCount
                Integer cCount
                Integer fCount

                sectionList = publishingExamList.collect {it.section}
                def allStudentIds = studentService.listStudentForAnalysis(command.academicYear, sectionList, command.genderName, command.religionName)
                def maleStudentIds = studentService.listStudentForAnalysis(command.academicYear, sectionList, Gender.MALE, command.religionName)
                def femaleStudentIds = studentService.listStudentForAnalysis(command.academicYear, sectionList, Gender.FEMALE, command.religionName)
                def muslimStudentIds = studentService.listStudentForAnalysis(command.academicYear, sectionList, command.genderName, Religion.ISLAM)
                def hinduStudentIds = studentService.listStudentForAnalysis(command.academicYear, sectionList, command.genderName, Religion.HINDU)
                for (subjectNam in subjectList) {
                     reportType = "All"
                     subjectName = subjectNam.name
                     numberOfStudent = examMarkService.getExamSubjectResultCount(publishingExamList, allStudentIds, subjectNam, null)
                    if (numberOfStudent > 0){
                        passCount = examMarkService.getExamSubjectResultCount(publishingExamList, allStudentIds, subjectNam, ResultStatus.PASSED)
                        failCount = numberOfStudent - passCount
                        aPlusCount = examMarkService.getExamSubjectLetterGradeCount(publishingExamList, allStudentIds, subjectNam, LetterGrade.GRADE_A_PLUS.value)
                        aCount = examMarkService.getExamSubjectLetterGradeCount(publishingExamList, allStudentIds, subjectNam, LetterGrade.GRADE_A.value)
                        aMinusCount = examMarkService.getExamSubjectLetterGradeCount(publishingExamList, allStudentIds, subjectNam, LetterGrade.GRADE_A_MINUS.value)
                        bCount= examMarkService.getExamSubjectLetterGradeCount(publishingExamList, allStudentIds, subjectNam, LetterGrade.GRADE_B.value)
                        cCount= examMarkService.getExamSubjectLetterGradeCount(publishingExamList, allStudentIds, subjectNam, LetterGrade.GRADE_C.value)
                        fCount = failCount
                        referenceList.add(new ResultSubjectReference(reportType, subjectName, numberOfStudent, passCount, failCount, aPlusCount, aCount, aMinusCount, bCount, cCount, fCount))
                    }


                    reportType = "Male"
                    subjectName = subjectNam.name
                    numberOfStudent = examMarkService.getExamSubjectResultCount(publishingExamList, maleStudentIds, subjectNam, null)
                    if (numberOfStudent > 0){
                        passCount = examMarkService.getExamSubjectResultCount(publishingExamList, maleStudentIds, subjectNam, ResultStatus.PASSED)
                        failCount = numberOfStudent - passCount
                        aPlusCount = examMarkService.getExamSubjectLetterGradeCount(publishingExamList, maleStudentIds, subjectNam, LetterGrade.GRADE_A_PLUS.value)
                        aCount = examMarkService.getExamSubjectLetterGradeCount(publishingExamList, maleStudentIds, subjectNam, LetterGrade.GRADE_A.value)
                        aMinusCount = examMarkService.getExamSubjectLetterGradeCount(publishingExamList, maleStudentIds, subjectNam, LetterGrade.GRADE_A_MINUS.value)
                        bCount= examMarkService.getExamSubjectLetterGradeCount(publishingExamList, maleStudentIds, subjectNam, LetterGrade.GRADE_B.value)
                        cCount= examMarkService.getExamSubjectLetterGradeCount(publishingExamList, maleStudentIds, subjectNam, LetterGrade.GRADE_C.value)
                        fCount = failCount
                        referenceList.add(new ResultSubjectReference(reportType, subjectName, numberOfStudent, passCount, failCount, aPlusCount, aCount, aMinusCount, bCount, cCount, fCount))
                    }

                    reportType = "Female"
                    subjectName = subjectNam.name
                    numberOfStudent = examMarkService.getExamSubjectResultCount(publishingExamList, femaleStudentIds, subjectNam, null)
                    if (numberOfStudent > 0) {
                        passCount = examMarkService.getExamSubjectResultCount(publishingExamList, femaleStudentIds, subjectNam, ResultStatus.PASSED)
                        failCount = numberOfStudent - passCount
                        aPlusCount = examMarkService.getExamSubjectLetterGradeCount(publishingExamList, femaleStudentIds, subjectNam, LetterGrade.GRADE_A_PLUS.value)
                        aCount = examMarkService.getExamSubjectLetterGradeCount(publishingExamList, femaleStudentIds, subjectNam, LetterGrade.GRADE_A.value)
                        aMinusCount = examMarkService.getExamSubjectLetterGradeCount(publishingExamList, femaleStudentIds, subjectNam, LetterGrade.GRADE_A_MINUS.value)
                        bCount= examMarkService.getExamSubjectLetterGradeCount(publishingExamList, femaleStudentIds, subjectNam, LetterGrade.GRADE_B.value)
                        cCount= examMarkService.getExamSubjectLetterGradeCount(publishingExamList, femaleStudentIds, subjectNam, LetterGrade.GRADE_C.value)
                        fCount = failCount
                        referenceList.add(new ResultSubjectReference(reportType, subjectName, numberOfStudent, passCount, failCount, aPlusCount, aCount, aMinusCount, bCount, cCount, fCount))
                    }

                    reportType = "Muslim"
                    subjectName = subjectNam.name
                    numberOfStudent = examMarkService.getExamSubjectResultCount(publishingExamList, muslimStudentIds, subjectNam, null)
                    if (numberOfStudent > 0){
                        passCount = examMarkService.getExamSubjectResultCount(publishingExamList, muslimStudentIds, subjectNam, ResultStatus.PASSED)
                        failCount = numberOfStudent - passCount
                        aPlusCount = examMarkService.getExamSubjectLetterGradeCount(publishingExamList, muslimStudentIds, subjectNam, LetterGrade.GRADE_A_PLUS.value)
                        aCount = examMarkService.getExamSubjectLetterGradeCount(publishingExamList, muslimStudentIds, subjectNam, LetterGrade.GRADE_A.value)
                        aMinusCount = examMarkService.getExamSubjectLetterGradeCount(publishingExamList, muslimStudentIds, subjectNam, LetterGrade.GRADE_A_MINUS.value)
                        bCount= examMarkService.getExamSubjectLetterGradeCount(publishingExamList, muslimStudentIds, subjectNam, LetterGrade.GRADE_B.value)
                        cCount= examMarkService.getExamSubjectLetterGradeCount(publishingExamList, muslimStudentIds, subjectNam, LetterGrade.GRADE_C.value)
                        fCount = failCount
                        referenceList.add(new ResultSubjectReference(reportType, subjectName, numberOfStudent, passCount, failCount, aPlusCount, aCount, aMinusCount, bCount, cCount, fCount))
                    }


                    reportType = "Hindu"
                    subjectName = subjectNam.name
                    numberOfStudent = examMarkService.getExamSubjectResultCount(publishingExamList, hinduStudentIds, subjectNam, null)
                    if (numberOfStudent > 0) {
                        passCount = examMarkService.getExamSubjectResultCount(publishingExamList, hinduStudentIds, subjectNam, ResultStatus.PASSED)
                        failCount = numberOfStudent - passCount
                        aPlusCount = examMarkService.getExamSubjectLetterGradeCount(publishingExamList, hinduStudentIds, subjectNam, LetterGrade.GRADE_A_PLUS.value)
                        aCount = examMarkService.getExamSubjectLetterGradeCount(publishingExamList, hinduStudentIds, subjectNam, LetterGrade.GRADE_A.value)
                        aMinusCount = examMarkService.getExamSubjectLetterGradeCount(publishingExamList, hinduStudentIds, subjectNam, LetterGrade.GRADE_A_MINUS.value)
                        bCount= examMarkService.getExamSubjectLetterGradeCount(publishingExamList, hinduStudentIds, subjectNam, LetterGrade.GRADE_B.value)
                        cCount= examMarkService.getExamSubjectLetterGradeCount(publishingExamList, hinduStudentIds, subjectNam, LetterGrade.GRADE_C.value)
                        fCount = failCount
                        referenceList.add(new ResultSubjectReference(reportType, subjectName, numberOfStudent, passCount, failCount, aPlusCount, aCount, aMinusCount, bCount, cCount, fCount))
                    }
                }
            }
        }



        String reportLogo = schoolService.reportLogoPath()
        Section section = command.examId?.section

        Map paramsMap = new LinkedHashMap()
        paramsMap.put('REPORT_LOGO', reportLogo)
        paramsMap.put('SUBREPORT_DIR', schoolService.subreportDir())
        paramsMap.put('academicYear', command.academicYear?.value)
        paramsMap.put('examName', command.examName?.examName)
        paramsMap.put('examType', command.examType?.value)
        paramsMap.put('className', command.className?.name)
        paramsMap.put('groupName', command.groupName?.value)
        paramsMap.put('sectionName', section?.name)
        paramsMap.put('subjectName', command.subjectName?.name)
        paramsMap.put('religionName', command.religionName?.value)
        paramsMap.put('genderName', command.genderName?.value)


        PrintOptionType printOptionType = command.printOptionType

        String extension = CommonUtils.PDF_EXTENSION
        String mimeType = CommonUtils.REPORT_FILE_FORMAT_PDF
        def exportFormat = JasperExportFormat.PDF_FORMAT
        if(printOptionType !=PrintOptionType.PDF){
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
        String outputFileName = "EXAMINATION_RESULT_ANALYSIS${extension}"
        JasperReportDef reportDef = new JasperReportDef(
                name: reportFileName,
                fileFormat: exportFormat,
                parameters: paramsMap,
                reportData: referenceList
        )
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        response.contentType =grailsApplication.config.grails.mime.types[mimeType]
        response.setHeader("Content-disposition", "inline;filename=${outputFileName}")
        response.outputStream << report.toByteArray()
    }

    //for Exam Mark report
    private static final String EXAM_MARK_JASPER_FILE = 'examMark.jasper'
    private static final String EXAM_MARK_WITH_HALL_PRAC_JASPER_FILE = 'examMarkWithHallPrac.jasper'
    private static final String EXAM_MARK_DETAIL_VIEW_JASPER_FILE = 'examMarkDetailView.jasper'
    private static final String EXAM_MARK_WITH_Hall_PRAC_DETAIL_JASPER_FILE = 'examMarkWithHallPracDetail.jasper'

    //for report card
    private static final String FINAL_REPORT_CARD_14ANDPLUS_JASPER_FILE = 'finalReportCard14Andplus.jasper'
    private static final String FINAL_REPORT_CARD_10PLUS_JASPER_FILE = 'finalReportCard10plus.jasper'
    private static final String FINAL_REPORT_CARD_10LESS_JASPER_FILE = 'finalReportCard10less.jasper'

    private static final String TERM_REPORT_CARD_14ANDPLUS_JASPER_FILE = 'termReportCard14Andplus.jasper'
    private static final String TERM_REPORT_CARD_10PLUS_JASPER_FILE = 'termReportCard10plus.jasper'
    private static final String TERM_REPORT_CARD_10LESS_JASPER_FILE = 'termReportCard10less.jasper'
    private static final String TERM_REPORT_CARD_NO_GROUP_JASPER_FILE = 'termReportCardNoGroup.jasper'
    private static final String FINAL_REPORT_CARD_NO_GROUP_JASPER_FILE = 'finalReportCardNoGroup.jasper'
    private static final String TERM_REPORT_CARD_WITH_GROUP_JASPER_FILE = 'termReportCardWithGroup.jasper'
    private static final String FINAL_REPORT_CARD_WITH_GROUP_JASPER_FILE = 'finalReportCardWithGroup.jasper'

    //tabulation
    private static final String CT_TERM_TABULATION_9_JASPER_FILE = 'termCtTabulation9.jasper'

    private static final String EXAM_SCHEDULE_BY_SECTION_CT_JASPER_FILE = 'sectionCTExamSchedule.jasper'
    private static final String EXAM_SCHEDULE_BY_SECTION_HALL_JASPER_FILE = 'sectionHallExamSchedule.jasper'

    //for Class Schedule
    private static final String CLASS_SCHEDULE_JASPER_FILE = 'classSchedule.jasper'
    //common report variables

    private static final String SUBJECT_STUDENT_LIST_JASPER_FILE = 'subjectStudentMarkEntryList.jasper'
    private static final String EXAM_MARK_ENTRY_STATUS_JASPER_FILE = 'examMarkEntryStatus.jasper'


    // for result analysis
    private static final String EXAMINATION_RESULT_ANALYSIS_SUMMERY = 'examAnalysisReport.jasper'
    private static final String EXAMINATION_RESULT_ANALYSIS_BYCLASS = 'examAnalysisReportByClass.jasper'
    private static final String EXAMINATION_RESULT_ANALYSIS_BYGRADE = 'examAnalysisReportByGrade.jasper'

}