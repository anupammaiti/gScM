package com.grailslab.exam

import com.grailslab.CommonUtils
import com.grailslab.enums.ExamStatus
import com.grailslab.enums.GroupName
import com.grailslab.enums.ResultStatus
import com.grailslab.gschoolcore.AcademicYear
import com.grailslab.settings.*
import com.grailslab.stmgmt.Tabulation
import grails.converters.JSON

class ResultController {
    def examService
    def schoolService
    def sectionService
    def classNameService
    def classSubjectsService
    def shiftExamService
    def examScheduleService
    def tabulationService
    def examMarkService
    def studentService
    def tabulationCtService


    def index(){
        render(view: '/exam/result/printResult')
    }

    def analysis(){
        render(view: '/exam/result/resultAnalysis')
    }
    def tabulation(Long id){
        ShiftExam shiftExam
        ClassName className
        def classNameList
        def sectionExamList

        def examNameList = shiftExamService.examNameDropDown()
        if (id) {
            shiftExam = ShiftExam.read(id)
        }
        if (params.class) {
            className = ClassName.read(params.getLong('class'))
        }
        if (shiftExam) {
            classNameList = classNameService.classNameInIdListDD(shiftExam.classIds);
        }
        if (shiftExam && className) {
            sectionExamList = examService.examsForManageTabulation(shiftExam, className);
        }

        render(view: '/exam/result/manageTabulation', model: [shiftExam: shiftExam, className: className, classNameList: classNameList, examNameList: examNameList, sectionExamList: sectionExamList])
    }
    def autoPromotion(Long id){
        ShiftExam shiftExam
        ClassName className
        def classNameList

        def examNameList = shiftExamService.examNameDropDown()
        if (id) {
            shiftExam = ShiftExam.read(id)
        }
        if (params.class) {
            className = ClassName.read(params.getLong('class'))
        }
        if (shiftExam) {
            classNameList = classNameService.classNameInIdListDD(shiftExam.classIds);
        }


        render(view: '/exam/result/autoPromotion', model: [shiftExam: shiftExam, className: className, classNameList: classNameList, examNameList: examNameList])
    }

    def ctTabulation(Long id){
        ShiftExam shiftExam
        ClassName className
        def classNameList
        def examNameList
        def sectionExamList


        if (params.shiftExam) {
            shiftExam = ShiftExam.read(params.getLong('shiftExam'))
        }
        if (params.class) {
            className = ClassName.read(params.getLong('class'))
        }
        if (shiftExam) {
            examNameList = shiftExamService.examNameDropDown(shiftExam.academicYear)
            classNameList = classNameService.classNameInIdListDD(shiftExam.classIds);
        }
        if (shiftExam && className) {
            sectionExamList = examService.examsForManageTabulation(shiftExam, className);
        }

        render(view: '/exam/result/cTmanageTabulation', model: [shiftExam: shiftExam, className: className, classNameList: classNameList, examNameList: examNameList, sectionExamList: sectionExamList])
    }
    def editCt(Long id){
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        Exam exam = Exam.get(id)
        if (!exam) {
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        if(exam.examStatus==ExamStatus.PUBLISHED){
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,"Can't edit. Result already published.")
            outPut = result as JSON
            render outPut
            return
        }
        exam.ctExamStatus = ExamStatus.NEW
        exam.save()
        result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
        result.put(CommonUtils.MESSAGE,"Successfully adjusted to allow edit Tabulation.")
        outPut = result as JSON
        render outPut
    }

    def edit(Long id){
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        Exam exam = Exam.get(id)
        if (!exam) {
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        if(exam.examStatus==ExamStatus.PUBLISHED){
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,"Can't edit. Result already published.")
            outPut = result as JSON
            render outPut
            return
        }
        exam.examStatus = ExamStatus.NEW
        exam.save()
        result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
        result.put(CommonUtils.MESSAGE,"Successfully adjusted to allow edit Tabulation.")
        outPut = result as JSON
        render outPut
    }

    def calculateMeritPosition(){
        if (!request.method.equals('POST')) {
            redirect(controller: 'exam', action: 'index')
            return
        }
        ClassName className = ClassName.read(params.getLong('className'))
        ShiftExam shiftExam = ShiftExam.read(params.getLong('shiftExam'))

        GroupName groupName
        if (params.groupName){
            groupName = GroupName.valueOf(params.groupName)
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut

        if (!className){
            result.put(CommonUtils.IS_ERROR,true)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_ERROR_MESSAGE)
            outPut=result as JSON
            render outPut
            return
        }
        String message
        List<Exam> examList = examService.examListForPreparingResult(shiftExam, className, groupName)
        if (!examList) {
            message = "No section tabulation found for ${className.name}"
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, message)
            outPut=result as JSON
            render outPut
            return
        }
        Boolean tabulationNotCom = false
        int numOfExam = 0

        for (exam in examList) {
            if (exam.examStatus == ExamStatus.NEW) {
                tabulationNotCom = true
                break
            } else if (exam.examStatus == ExamStatus.RESULT) {
                numOfExam++
            }
        }
        if (tabulationNotCom) {
            message = "One or more section tabulation yet not created. Please create tabulation for all section first."
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, message)
            outPut=result as JSON
            render outPut
            return
        }
        if (examList.size() == numOfExam) {
            message = "No Change detect in section tabulations. Class result up to date."
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, message)
            outPut=result as JSON
            render outPut
            return
        }

        def examIds = examList.collect {it.id}

        for (exam in examList) {
            tabulationService.createSectionResult(exam)
            exam.examStatus = ExamStatus.RESULT
            exam.save(flash:true)
        }
        tabulationService.createClassResult(className, examIds, shiftExam.academicYear)

        List<ExamSchedule> examScheduleList
        Double highestMark
        Double averageMark
        for (exam in examList) {
            examScheduleList = examScheduleService.examSchedules(exam)
            for (schedule in examScheduleList) {
                highestMark = examMarkService.getHighestClassExamMark(examIds, exam.examTerm, schedule.subject, className.subjectSba)
                averageMark = examMarkService.getAverageClassExamMark(className.id, examIds, exam.examTerm, schedule.subject, className.subjectSba)
                examScheduleService.updateSchedule(schedule.id, highestMark, averageMark)
            }
        }

        result.put(CommonUtils.IS_ERROR,false)
        result.put(CommonUtils.MESSAGE,"Result Prepared Successfully")
        outPut=result as JSON
        render outPut
        return
    }

    def calculateAutoPromotion(){
        if (!request.method.equals('POST')) {
            redirect(controller: 'exam', action: 'index')
            return
        }
        ClassName className = ClassName.read(params.getLong('className'))
        ShiftExam shiftExam = ShiftExam.read(params.getLong('shiftExam'))

        GroupName groupName
        if (params.groupName){
            groupName = GroupName.valueOf(params.groupName)
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut

        if (!className){
            result.put(CommonUtils.IS_ERROR,true)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_ERROR_MESSAGE)
            outPut=result as JSON
            render outPut
            return
        }
        String message
        List<Exam> examList = examService.examListForPreparingResult(shiftExam, className, groupName)
        if (!examList) {
            message = "No section tabulation found for ${className.name}"
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, message)
            outPut=result as JSON
            render outPut
            return
        }
        Boolean meritPositionNotCom = false
        int numOfExam = 0

        for (exam in examList) {
            if (exam.examStatus != ExamStatus.RESULT) {
                meritPositionNotCom = true
                break
            }
        }
        if (meritPositionNotCom) {
            message = "One or more section Merit position yet not created. Please create metit position for all section first."
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, message)
            outPut=result as JSON
            render outPut
            return
        }

        String academicYear = shiftExam.academicYear.value
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
        def nextClasses = classNameService.nextClasses(className)
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
        def studentList = studentService.byClassName(className, shiftExam.academicYear)
        def tabulationList = Tabulation.findAllByStudentInListAndResultStatusAndExamInList(studentList, ResultStatus.PASSED, examList, [sort: "positionInClass", order: "asc"])
        int numOfSection = sectionList.size()
        int newRoll = 1
        Integer tmpSection = 0
        Section workSection
        for (tabu in tabulationList) {
            if(tmpSection >= numOfSection){
                tmpSection = 0
                newRoll++
            }
            workSection = sectionList[tmpSection]
            tabu.nextSection = workSection.name
            tabu.nextRollNo = newRoll
            tabu.save()
            tmpSection++
        }


        result.put(CommonUtils.IS_ERROR,false)
        result.put(CommonUtils.MESSAGE,"Auto promotion Prepared Successfully")
        outPut=result as JSON
        render outPut
        return
    }

    def calculateCtMeritPosition(){
        if (!request.method.equals('POST')) {
            redirect(controller: 'exam', action: 'index')
            return
        }
        ClassName className = ClassName.read(params.getLong('className'))
        ShiftExam shiftExam = ShiftExam.read(params.getLong('shiftExam'))

        GroupName groupName
        if (params.groupName){
            groupName = GroupName.valueOf(params.groupName)
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut

        if (!className){
            result.put(CommonUtils.IS_ERROR,true)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_ERROR_MESSAGE)
            outPut=result as JSON
            render outPut
            return
        }
        String message
        List<Exam> examList = examService.examListForPreparingResult(shiftExam, className, groupName)
        if (!examList) {
            message = "No section tabulation found for ${className.name}"
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, message)
            outPut=result as JSON
            render outPut
            return
        }
        Boolean tabulationNotCom = false
        int numOfExam = 0

        for (exam in examList) {
            if (exam.ctExamStatus == ExamStatus.NEW) {
                tabulationNotCom = true
                break
            } else if (exam.ctExamStatus == ExamStatus.RESULT) {
                numOfExam++
            }
        }
        if (tabulationNotCom) {
            message = "One or more section tabulation yet not created. Please create tabulation for all section first."
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, message)
            outPut=result as JSON
            render outPut
            return
        }
        if (examList.size() == numOfExam) {
            message = "No Change detect in section tabulations. Class result up to date."
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, message)
            outPut=result as JSON
            render outPut
            return
        }

        def examIds = examList.collect {it.id}

        for (exam in examList) {
            tabulationCtService.createSectionResult(exam)
            exam.ctExamStatus = ExamStatus.RESULT
            exam.save(flash:true)
        }
        tabulationCtService.createClassResult(className, examIds, shiftExam.academicYear)

        result.put(CommonUtils.IS_ERROR,false)
        result.put(CommonUtils.MESSAGE,"Ct Result Prepared Successfully")
        outPut=result as JSON
        render outPut
        return
    }


}
