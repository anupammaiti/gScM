package com.grailslab.exam

import com.grailslab.CommonUtils
import com.grailslab.command.ExamCommand
import com.grailslab.enums.ExamStatus
import com.grailslab.enums.ExamType
import com.grailslab.enums.ScheduleStatus
import com.grailslab.enums.Shift
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.settings.ClassName
import com.grailslab.settings.Exam
import com.grailslab.settings.ShiftExam
import grails.converters.JSON

class ExamController {

    def schoolService
    def examService
    def classNameService
    def messageSource
    def sectionService
    def shiftExamService
    def index() {
        LinkedHashMap resultMap = shiftExamService.examPaginateList(params)
        def classNameList = classNameService.classNameDropDownList();
        if (!resultMap || resultMap.totalCount == 0) {
            render(view: 'index', model: [dataReturn: null, totalCount: 0,classNameList:classNameList])
            return
        }
        int totalCount = resultMap.totalCount
        render(view: 'index', model: [dataReturn: resultMap.results, totalCount: totalCount,classNameList:classNameList])
    }
    def list() {
        LinkedHashMap gridData
        String result

        LinkedHashMap resultMap = shiftExamService.examPaginateList(params)
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
    def classExams(Long id) {
        ShiftExam shiftExam = ShiftExam.read(id)
        if (!shiftExam) {
            redirect(action: 'index')
            return
        }
        def classNameList = classNameService.classNameInIdListDD(shiftExam.classIds);

        Boolean isExamPublished
        Boolean isCtScheduleAdded
        Boolean isCtSchedulePublished

        Boolean isHtScheduleAdded
        Boolean isHtSchedulePublished
        List classExams = new ArrayList()

        for (clsName in classNameList) {
            isExamPublished = examService.isExamPublished(shiftExam, clsName.id)
            isCtScheduleAdded = examService.isExamSchedule(shiftExam, clsName.id, ExamType.CLASS_TEST, [ScheduleStatus.ADDED, ScheduleStatus.PUBLISHED] as List)
            isCtSchedulePublished = examService.isExamSchedule(shiftExam, clsName.id, ExamType.CLASS_TEST, [ScheduleStatus.PUBLISHED] as List)
            isHtScheduleAdded = examService.isExamSchedule(shiftExam, clsName.id, ExamType.HALL_TEST, [ScheduleStatus.ADDED, ScheduleStatus.PUBLISHED] as List)
            isHtSchedulePublished = examService.isExamSchedule(shiftExam, clsName.id, ExamType.HALL_TEST, [ScheduleStatus.PUBLISHED] as List)

            classExams.add([clasId: clsName.id, name: clsName.name,  isExamPublished: isExamPublished, isCtScheduleAdded: isCtScheduleAdded, isCtSchedulePublished: isCtSchedulePublished, isHtScheduleAdded: isHtScheduleAdded, isHtSchedulePublished: isHtSchedulePublished])
        }
        render(view: 'classExams', model: [classExams:classExams, shiftExam: shiftExam])
    }

    def editExams(Long id) {
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        ShiftExam shiftExam = ShiftExam.read(id)
        if (!shiftExam) {
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
        result.put(CommonUtils.OBJ,shiftExam)
        outPut = result as JSON
        render outPut

    }

    def sectionExams(Long id) {
        ShiftExam shiftExam = ShiftExam.read(id)
        if (!shiftExam) {
            redirect(action: 'index')
            return
        }
        ClassName className
        if (params.className) {
            className = ClassName.read(params.className)
        }
        LinkedHashMap resultMap = examService.examPaginateList(params, shiftExam, className)
        def classNameList = classNameService.classNameInIdListDD(shiftExam.classIds);
        List<ExamType> examTypeList = new ArrayList<ExamType>()
        if (shiftExam.isCtExam) {
            examTypeList.add(ExamType.CLASS_TEST)
        }
        if (shiftExam.isHallExam) {
            examTypeList.add(ExamType.HALL_TEST)
        }
        if (!resultMap || resultMap.totalCount == 0) {
            render(view: 'sectionExams', model: [dataReturn: null, totalCount: 0, className: className, classNameList:classNameList, shiftExam: shiftExam, examTypeList:examTypeList])
            return
        }
        int totalCount = resultMap.totalCount
        render(view: 'sectionExams', model: [dataReturn: resultMap.results, totalCount: totalCount, className: className, classNameList:classNameList, shiftExam: shiftExam, examTypeList:examTypeList])
    }

    def entry() {
        def examNameList = shiftExamService.examNameDropDown()
        def classNameList = classNameService.classNameDropDownList()
        render(view: '/exam/markEntrySelection', model: [classNameList:classNameList, examNameList: examNameList])
    }


    def save(ExamCommand command) {
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

        def examSectionList = sectionService.sectionsByClassIds(command.classIds)
        if(!examSectionList){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "No Class found for ${command.examName}.")
            outPut = result as JSON
            render outPut
            return
        }
        List sectionIds = examSectionList.collect {it.id}
        String idsListStr = sectionIds.join(',')

        String message
        Boolean isCtExam = command.isCtExam?:false
        Boolean isHallExam  = command.isHallExam?:false
        ShiftExam shiftExam
        ShiftExam savedExam
        if (command.id) {
            shiftExam = ShiftExam.get(command.id)
            shiftExam.examName = command.examName
            shiftExam.isCtExam = isCtExam
            shiftExam.isHallExam = isHallExam
            shiftExam.examTerm = command.examTerm
            shiftExam.weightOnResult = command.weightOnResult
            shiftExam.classIds = command.classIds
            shiftExam.sectionIds = idsListStr
            shiftExam.examStatus = ExamStatus.NEW
            shiftExam.resultPublishOn = command.resultPublishOn
            shiftExam.periodStart = command.periodStart
            shiftExam.periodEnd = command.periodEnd
            shiftExam.save()
            Exam aExam
            for (section in examSectionList) {
                aExam = Exam.findByShiftExamAndSectionAndActiveStatus(shiftExam, section, ActiveStatus.ACTIVE)
                if(aExam) {
                    aExam.name = command.examName
                    aExam.examTerm = shiftExam.examTerm
                } else {
                    aExam = new Exam(shiftExam: shiftExam, name: command.examName, examTerm: shiftExam.examTerm, section: section, className: section.className)
                }
                aExam.save()
            }

            def deletedExams = examService.examsNotInSectionIdList(shiftExam, sectionIds)
            for (delExam in deletedExams) {
                delExam.activeStatus = ActiveStatus.DELETE
                delExam.save()
            }
            message = "Exam Updated Successfully"
        } else {
            message = "Exam Created Successfully for selected Classes"
            shiftExam =new ShiftExam(examName: command.examName, isCtExam: isCtExam, isHallExam: isHallExam, examTerm: command.examTerm,
                    weightOnResult: command.weightOnResult, classIds: command.classIds,
                    shift: Shift.DAY, sectionIds: idsListStr, examStatus: ExamStatus.NEW, resultPublishOn: command.resultPublishOn, periodStart: command.periodStart, periodEnd: command.periodEnd)
            savedExam = shiftExam.save(flush: true)
            if(savedExam){
                for (section in examSectionList) {
                    new Exam(shiftExam: savedExam, name: command.examName, examTerm: savedExam.examTerm, section: section, className: section.className).save()
                }
            }
        }

        result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
        result.put(CommonUtils.MESSAGE,message)
        outPut=result as JSON
        render outPut
    }

    def deleteShiftExam(Long id) {
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        ShiftExam exam = ShiftExam.get(id)
        if(!exam){
            result.put(CommonUtils.IS_ERROR,true)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        if(exam.examStatus!=ExamStatus.NEW){
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,"Can't delete. Result Already Processing or Published.")
            outPut = result as JSON
            render outPut
            return
        }
        if(exam.examStatus.equals(ExamStatus.NEW)) {
            exam.activeStatus=ActiveStatus.INACTIVE
            exam.save(flush: true)

            result.put(CommonUtils.IS_ERROR,false)
            result.put(CommonUtils.MESSAGE,"Exam Deleted successfully.")
            outPut = result as JSON
            render outPut
            return
        }
        result.put(CommonUtils.IS_ERROR,true)
        result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_ERROR_MESSAGE)
        outPut = result as JSON
        render outPut
        return
    }
    /*def save(ExamCommand examCommand) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        if (examCommand.hasErrors()) {
            def errorList = examCommand?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, errorList?.join('\n'))
            outPut = result as JSON
            render outPut
            return
        }
        def workingYears = schoolService.workingYears()
        ExamTerm examTerm = examCommand.examTerm
        Shift shift = examCommand.shift

        def shiftSectionList = sectionService.allSectionsByShiftAndExamTerm(shift,examTerm)
        if(!shiftSectionList){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "No Section found for ${shift.value} ${examTerm.value}.")
            outPut = result as JSON
            render outPut
            return
        }

        ShiftExam shiftExam= ShiftExam.findByShiftAndExamTermAndAcademicYearInList(shift,examTerm, workingYears)
        if(shiftExam){
            if(shiftExam.examStatus==ExamStatus.PUBLISHED){
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, "${shift.value} ${examTerm.value} exam already published.")
                outPut = result as JSON
                render outPut
                return
            }
        }
        *//*def previousTermExam=Exam.findAllByShiftExamAndExamStatusNotEqualAndAcademicYearAndActiveStatusAndSchoolId(shiftExam, ExamStatus.PUBLISHED,workSchool.academicYear,ActiveStatus.ACTIVE,workSchool.id)
        if(previousTermExam){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "Previous Term Exam for ${shift.value} Section(s) still in Progress. Need to publish the result or delete exam first.")
            outPut = result as JSON
            render outPut
            return
        }*//*

        def sectionIds = shiftSectionList.collect {it.id}
        String idsListStr = sectionIds.join(',')

        List<Exam> examList = new ArrayList<Exam>()
        List<String> addedSectionList = new ArrayList<String>()
        List<String> updatedSectionList = new ArrayList<String>()
        Exam exam
        List<ExamSchedule> oldSchedules
        if(shiftExam){
            shiftSectionList.each {Section section ->
                exam=Exam.findByShiftExamAndSectionAndActiveStatus(shiftExam,section,ActiveStatus.ACTIVE)
                if(!exam){
                    exam = new Exam(shiftExam: shiftExam, examTerm: shiftExam.examTerm, section: section, className: section.className)
                    exam.name = section.name+' ( '+examTerm.value+' )'
                    addedSectionList.add(section.name)
                    examList.add(exam)
                } else {
                    updatedSectionList.add(section.name)
                }
            }

            shiftExam.resultPublishOn = examCommand.resultPublishOn
            shiftExam.save()
        } else {    // first time exam create
            shiftExam =new ShiftExam(shift: shift,examTerm: examTerm,sectionIds: idsListStr,resultPublishOn: examCommand.resultPublishOn).save(flash:true)
            if(shiftExam){
                shiftSectionList.each {Section section ->
                    exam = new Exam(shiftExam: shiftExam, examTerm: shiftExam.examTerm, section: section, className: section.className)
                    exam.name = section.name+' ( '+examTerm.value+' )'
                    addedSectionList.add(section.name)
                    examList.add(exam)
                }
            }
        }
        examList.each {Exam exam1 ->
            exam1.save()
        }
        String sectionNames
        String message=examCommand.shift.value+" "+examTerm.value
        if(addedSectionList.size()>0){
            sectionNames = addedSectionList.join(', ')
            message += " Exam added for <strong>"+ sectionNames+"</strong>."
        }
        if(updatedSectionList.size()>0){
            sectionNames = updatedSectionList.join(', ')
            message += "</br>Result Publish Date Updated of <strong>"+ sectionNames+"</strong>."
        }

        result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
        result.put(CommonUtils.MESSAGE,message)
        outPut=result as JSON
        render outPut
    }*/



    def inactive(Long id) {
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        Exam exam = Exam.get(id)
        if(!exam){
            result.put(CommonUtils.IS_ERROR,true)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        if(exam.examStatus!=ExamStatus.NEW){
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,"Can't delete. Result Already Processing or Published.")
            outPut = result as JSON
            render outPut
            return
        }
        if(exam.examStatus.equals(ExamStatus.NEW)) {
            exam.activeStatus=ActiveStatus.INACTIVE
            exam.save(flush: true)

            result.put(CommonUtils.IS_ERROR,false)
            result.put(CommonUtils.MESSAGE,"Exam Deleted successfully.")
            outPut = result as JSON
            render outPut
            return
        }
        result.put(CommonUtils.IS_ERROR,true)
        result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_ERROR_MESSAGE)
        outPut = result as JSON
        render outPut
        return
    }
}

