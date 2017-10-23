package com.grailslab.exam

import com.grailslab.CommonUtils
import com.grailslab.command.AddClassScheduleCommand
import com.grailslab.command.ExamScheduleCommand
import com.grailslab.enums.ExamStatus
import com.grailslab.enums.ExamTerm
import com.grailslab.enums.ExamType
import com.grailslab.enums.ScheduleStatus
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.settings.*
import grails.converters.JSON

class ExamScheduleController {

    def examScheduleService
    def classSubjectsService
    def examPeriodService
    def schoolService
    def sectionService
    def examMarkService
    def examService
    def messageSource

    def index() { }

    def ctSchedule(Long id) {
        Exam exam=Exam.read(id)
        if(!exam){
            redirect(controller: 'exam',action: 'index')
        }
        Section section =exam.section
        ClassName className=section.className

        boolean isOtherActivity = false
        if (exam.examTerm == ExamTerm.FINAL_TEST) {
            isOtherActivity = true
        }

        def examPeriodList = examPeriodService.periodDropDownList(ExamType.CLASS_TEST)
        def classSubjectsForCT = classSubjectsService.ctSubjects(className, isOtherActivity, section.groupName)

        if(exam.ctSchedule==ScheduleStatus.NO_SCHEDULE){
            render(view: '/exam/addCTSchedule',model: [examPeriodList:examPeriodList,ctSubjects:classSubjectsForCT, exam:exam, className:className])
            return
        }
        def ctSchedules = examScheduleService.ctSchedules(exam)
        def subjectIds
        if(ctSchedules){
            subjectIds = ctSchedules.collect {it.subject.id}
        }
        if(subjectIds){
            classSubjectsForCT = classSubjectsService.ctSubjectsNotInList(className, subjectIds, isOtherActivity, section.groupName)

            if(classSubjectsForCT){
                render(view: '/exam/addCTSchedule',model: [examPeriodList:examPeriodList,ctSubjects:classSubjectsForCT, exam:exam, className:className])
                return
            }
        }

        LinkedHashMap resultMap = examScheduleService.examSchedulePaginateListCT(exam)
        if (!resultMap){
            render(view: '/exam/viewCTSchedule', model: [examPeriodList:examPeriodList,dataReturn: null,exam:exam ,className:className])
            return
        }else {
            render(view: '/exam/viewCTSchedule', model: [examPeriodList:examPeriodList, dataReturn: resultMap.results,exam:exam,className:className])
            return
        }
    }

    def htSchedule(Long id) {
        Exam exam=Exam.read(id)
        if(!exam){
            redirect(controller: 'exam',action: 'index')
        }
        Section section = exam.section
        ClassName className=section.className

        boolean isOtherActivity = false
        if (exam.examTerm == ExamTerm.FINAL_TEST) {
            isOtherActivity = true
        }

        def examPeriodList = examPeriodService.periodDropDownList(ExamType.HALL_TEST)
        def classSubjectsForHT = classSubjectsService.htSubjects(className, isOtherActivity, section.groupName)
        if(exam.hallSchedule==ScheduleStatus.NO_SCHEDULE){
            render(view: '/exam/addHTSchedule',model: [examPeriodList:examPeriodList,htSubjects:classSubjectsForHT, exam:exam, className:className])
            return
        }
        def htSchedules = examScheduleService.hallSchedules(exam)
        def subjectIds
        if(htSchedules){
            subjectIds = htSchedules.collect {it.subject.id}
        }
        if(subjectIds){
            classSubjectsForHT = classSubjectsService.htSubjectsNotInList(className, subjectIds, isOtherActivity, section.groupName)

            if(classSubjectsForHT){
                render(view: '/exam/addHTSchedule',model: [examPeriodList:examPeriodList,htSubjects:classSubjectsForHT, exam:exam, className:className])
                return
            }
        }
        LinkedHashMap resultMap = examScheduleService.examSchedulePaginateListHT(exam)
        if (!resultMap){
            render(view: '/exam/viewHTSchedule', model: [examPeriodList:examPeriodList,dataReturn: null, exam:exam ,className:className])
        return
        }else {
            render(view: '/exam/viewHTSchedule', model: [examPeriodList:examPeriodList,dataReturn: resultMap.results, exam:exam,className:className])
            return
        }
    }
    def classSchedule(AddClassScheduleCommand command){
        String viewName = '/exam/addClassCTSchedule'
        if(command.examType==ExamType.HALL_TEST){
            viewName = '/exam/addClassHallSchedule'
        }
        if (command.hasErrors()) {
            def errorList = command?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            flash.message= errorList?.join(', ')
            render(view: viewName)
            return
        }

        ShiftExam shiftExam = command.shiftExam
        if(shiftExam.examStatus==ExamStatus.PUBLISHED){
            flash.message= "${shiftExam.examName} result already published."
            render(view: viewName)
            return
        }

        boolean isOtherActivity = false
        if (shiftExam.examTerm == ExamTerm.FINAL_TEST) {
            isOtherActivity = true
        }

        ClassName clsName = command.className
        def classExams = examService.classExamIdList(shiftExam, clsName)
        if(!classExams){
            flash.message= "${clsName.name} is not added for ${shiftExam.examName}. Please add the class to exam first"
            render(view: viewName)
            return
        }
        ExamType examType = command.examType
        String examIds= classExams.join(',')
        def examPeriodList = examPeriodService.periodDropDownList(examType)
        def classSubjects

        def scheduleList
        boolean isExamPublished = examService.isExamPublished(shiftExam, clsName.id)

        def subjectIds
        boolean isSchedulePublished
        boolean isScheduleAdded
        if(examType==ExamType.CLASS_TEST){
            scheduleList = examScheduleService.ctClassSchedules(classExams)
            if (isExamPublished) {
                //render view Mode no edit option
                classSubjects = new ArrayList()
                for(schedule in scheduleList) {
                    if (!classSubjects.find { it.subjectId == schedule[0].id }) {
                        classSubjects.add([subjectId: schedule[0].id, subjectName: schedule[0].name, ctMark: schedule[1], ctPeriod: schedule[2]?.id, examDate: CommonUtils.getUiDateStrForCalenderDateEdit(schedule[3])])
                    }

                }
                viewName = '/exam/viewCTSchedule'
                render(view: viewName,model: [isExamPublished: true, classSubjects:classSubjects, className:clsName, examName: shiftExam.examName, shiftExamId: shiftExam.id])
                return
            }

            isSchedulePublished = examService.isExamSchedule(shiftExam, clsName.id, ExamType.CLASS_TEST, [ScheduleStatus.PUBLISHED] as List)
            if (isSchedulePublished && !command.forceEdit) {
                //render view Mode edit option
                classSubjects = new ArrayList()
                for(schedule in scheduleList) {
                    if (!classSubjects.find { it.subjectId == schedule[0].id }) {
                        classSubjects.add([subjectId: schedule[0].id, subjectName: schedule[0].name, ctMark: schedule[1], ctPeriod: schedule[2]?.id, examDate: CommonUtils.getUiDateStrForCalenderDateEdit(schedule[3])])
                    }
                }

                viewName = '/exam/viewCTSchedule'
                render(view: viewName,model: [isExamPublished: false, classSubjects:classSubjects, className:clsName, examName: shiftExam.examName, shiftExamId: shiftExam.id])
                return
            }
            isScheduleAdded = examService.isExamSchedule(shiftExam, clsName.id, ExamType.CLASS_TEST, [ScheduleStatus.ADDED, ScheduleStatus.PUBLISHED] as List)
            if (isScheduleAdded) {
                if(scheduleList){
                    subjectIds = scheduleList.collect {it[0].id}
                }
                if(subjectIds){
                    classSubjects = classSubjectsService.ctSubjectsNotInList(clsName, subjectIds, isOtherActivity)
                }
                if(classSubjects){
                    render(view: viewName,model: [isNewSubject: true, isEditSchedule: false, examPeriodList:examPeriodList,classSubjects:classSubjects, examIds:examIds, className:clsName, examName: shiftExam.examName, shiftExamId: shiftExam.id])
                    return
                }
                classSubjects = new ArrayList()
                for(schedule in scheduleList) {
                    if (!classSubjects.find { it.subjectId == schedule[0].id }) {
                        classSubjects.add([subjectId: schedule[0].id, subjectName: schedule[0].name, ctMark: schedule[1], ctPeriod: schedule[2]?.id, examDate: CommonUtils.getUiDateStrForCalenderDateEdit(schedule[3])])
                    }
                }
                render(view: viewName,model: [isEditSchedule: true, examPeriodList:examPeriodList,classSubjects:classSubjects, examIds:examIds, className:clsName, examName: shiftExam.examName, shiftExamId: shiftExam.id])
                return
            } else {
                classSubjects = classSubjectsService.ctSubjects(clsName, isOtherActivity)
                render(view: viewName, model: [isEditSchedule: false, examPeriodList:examPeriodList, classSubjects:classSubjects, examIds:examIds, className:clsName, examName: shiftExam.examName, shiftExamId: shiftExam.id])
            }
        } else if(examType==ExamType.HALL_TEST){
            scheduleList = examScheduleService.htClassSchedules(classExams)
            if (isExamPublished) {
                //render view Mode no edit option
                classSubjects = new ArrayList()
                for(schedule in scheduleList) {
                    if (!classSubjects.find { it.subjectId == schedule[0].id }) {
                        classSubjects.add([subjectId: schedule[0].id, subjectName: schedule[0].name, hallMark: schedule[1], htPeriod: schedule[2]?.id, examDate: CommonUtils.getUiDateStrForCalenderDateEdit(schedule[3])])
                    }
                }
                viewName = '/exam/viewHTSchedule'
                render(view: viewName, model: [isExamPublished: true, examPeriodList:examPeriodList,classSubjects:classSubjects, examIds:examIds, className:clsName, examName: shiftExam.examName, shiftExamId: shiftExam.id])
                return
            }
            isSchedulePublished = examService.isExamSchedule(shiftExam, clsName.id, ExamType.HALL_TEST, [ScheduleStatus.PUBLISHED] as List)
            if (isSchedulePublished && !command.forceEdit) {
                //render view Mode edit option
                classSubjects = new ArrayList()
                for(schedule in scheduleList) {
                    if (!classSubjects.find { it.subjectId == schedule[0].id }) {
                        classSubjects.add([subjectId: schedule[0].id, subjectName: schedule[0].name, hallMark: schedule[1], htPeriod: schedule[2]?.id, examDate: CommonUtils.getUiDateStrForCalenderDateEdit(schedule[3])])
                    }
                }
                viewName = '/exam/viewHTSchedule'
                render(view: viewName, model: [examPeriodList:examPeriodList,classSubjects:classSubjects, examIds:examIds, className:clsName, examName: shiftExam.examName, shiftExamId: shiftExam.id])
                return
            }
            isScheduleAdded = examService.isExamSchedule(shiftExam, clsName.id, ExamType.HALL_TEST, [ScheduleStatus.ADDED, ScheduleStatus.PUBLISHED] as List)
            if (isScheduleAdded) {
                if(scheduleList){
                    subjectIds = scheduleList.collect {it[0].id}
                }
                if(subjectIds){
                    classSubjects = classSubjectsService.htSubjectsNotInList(clsName, subjectIds, isOtherActivity)
                }
                if(classSubjects){
                    render(view: viewName,model: [isNewSubject: true, isEditSchedule: false, examPeriodList:examPeriodList,classSubjects:classSubjects, examIds:examIds, className:clsName, examName: shiftExam.examName, shiftExamId: shiftExam.id])
                    return
                }
                classSubjects = new ArrayList()
                for(schedule in scheduleList) {
                    if (!classSubjects.find { it.subjectId == schedule[0].id }) {
                        classSubjects.add([subjectId: schedule[0].id, subjectName: schedule[0].name, hallMark: schedule[1], htPeriod: schedule[2]?.id, examDate: CommonUtils.getUiDateStrForCalenderDateEdit(schedule[3])])
                    }
                }
                render(view: viewName,model: [isEditSchedule: true, examPeriodList:examPeriodList,classSubjects:classSubjects, examIds:examIds, className:clsName, examName: shiftExam.examName, shiftExamId: shiftExam.id])
                return
            } else {
                classSubjects = classSubjectsService.htSubjects(clsName, isOtherActivity)
                render(view: viewName,model: [isEditSchedule: false, examPeriodList:examPeriodList,classSubjects:classSubjects, examIds:examIds, className:clsName, examName: shiftExam.examName, shiftExamId: shiftExam.id])
            }
        }
    }

    def saveCT(Long examId) {
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        Exam exam = Exam.get(examId)
        if (!exam) {
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        def subjectIds =params.subjectIds
        if(!subjectIds){
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,"No Subject to add Schedule")
            outPut = result as JSON
            render outPut
            return
        }
        List examSubjectIds = new ArrayList()
        if(subjectIds instanceof String){
            examSubjectIds.add(subjectIds)
        }else {
            examSubjectIds = subjectIds as List
        }

        Double htMark
        Long subId
        Date ctExamDate
        Double ctExamMark
        def ctMarkInput
        ExamPeriod ctPeriod
        def ctPeriodInput
        SubjectName subject
        def inputCtDate
        ExamSchedule examSchedule

        //for all section schedule
        String subjectFieldName
        ExamPeriod examPeriod
        ShiftExam shiftExam =exam.shiftExam
        def sectionIds = shiftExam.sectionIds.split(',')
        int idxOfSection
        examSubjectIds.each {String idx ->
            subId=Long.parseLong(idx)
            subject=SubjectName.read(subId)
            if (subject){
                inputCtDate =params."examDate${subId}"
                if(inputCtDate){
                    ctExamDate= Date.parse('dd/MM/yyyy', inputCtDate)
                }
                ctPeriodInput =params."examPeriod${subId}"
                if(ctPeriodInput){
                    ctPeriod = ExamPeriod.read(ctPeriodInput.toLong())
                }
                ctMarkInput =params."examMark${subId}"
                if(ctMarkInput){
                    ctExamMark=Double.parseDouble(ctMarkInput)
                }

                examSchedule=ExamSchedule.findBySubjectAndExamAndActiveStatus(subject,exam, ActiveStatus.ACTIVE)
                if(examSchedule){
                    examSchedule.ctExamDate=ctExamDate
                    examSchedule.ctPeriod=ctPeriod
                    examSchedule.ctExamMark=ctExamMark
                    examSchedule.subject=subject
                    examSchedule.exam=exam
                    htMark = 0
                    if(examSchedule.hallExamMark){
                        htMark=examSchedule.hallExamMark
                    }
                    examSchedule.fullMark=htMark+ctExamMark
                    if (examSchedule.validate()) {
                        examSchedule.save(flush: true)
                    }
                }else {
                    examSchedule= new ExamSchedule(ctExamDate:ctExamDate,ctPeriod:ctPeriod,ctExamMark:ctExamMark,subject:subject,exam:exam,fullMark:ctExamMark)
                    if (examSchedule.validate()) {
                        examSchedule.save(flush: true)
                    }
                }
            }
        }
        exam.ctSchedule=ScheduleStatus.ADDED
        exam.save()
        result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
        result.put(CommonUtils.MESSAGE,'Exam Schedule Added Successfully')
        result.put('examId',examId)
        outPut = result as JSON
        render outPut
    }

    def saveClassCT(String examIds) {
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        List<String> items = Arrays.asList(examIds.split("\\s*,\\s*"))

        Exam exam
        def subjectIds
        List examSubjectIds
        def deletedSchedules
        Double htMark
        Long subId
        Date ctExamDate
        Double ctExamMark
        def ctMarkInput
        ExamPeriod ctPeriod
        def ctPeriodInput
        SubjectName subject
        def inputCtDate
        ExamSchedule examSchedule

        //for all section schedule
        String subjectFieldName
        ExamPeriod examPeriod
        boolean isNewSubject = false
        if (params.isNewSubject && params.getBoolean("isNewSubject") == true){
            isNewSubject = true
        }
        int count = 0
        items.each {strId ->
            exam = Exam.get(Long.valueOf(strId))
            if (!exam) {
                result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
                result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
                outPut = result as JSON
                render outPut
                return
            }
            subjectIds =params.subjectIds
            if(!subjectIds){
                result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
                result.put(CommonUtils.MESSAGE,"No Subject to add Schedule")
                outPut = result as JSON
                render outPut
                return
            }
            examSubjectIds = new ArrayList()
            if(subjectIds instanceof String){
                subId=Long.parseLong(subjectIds)
                subject=SubjectName.read(subId)
                examSubjectIds.add(subject)
            }else {
                def selectSubjects = subjectIds as List
                selectSubjects.each {String idx ->
                    subId=Long.parseLong(idx)
                    subject=SubjectName.read(subId)
                    examSubjectIds.add(subject)
                }
            }
            if (examSubjectIds) {
                //check if a subject removed from class subject
                if (!isNewSubject) {
                    deletedSchedules = examScheduleService.ctSchedulesNotInList(exam, examSubjectIds)
                    for (schedule in deletedSchedules) {
                        schedule.activeStatus = ActiveStatus.DELETE
                        schedule.save()
                    }
                }

                for (ctSubject in examSubjectIds) {
                    subId = ctSubject.id
                    inputCtDate =params."examDate${subId}"
                    if(inputCtDate){
                        ctExamDate= Date.parse('dd/MM/yyyy', inputCtDate)
                    }
                    ctPeriodInput =params."examPeriod${subId}"
                    if(ctPeriodInput){
                        ctPeriod = ExamPeriod.read(ctPeriodInput.toLong())
                    }
                    ctMarkInput =params."examMark${subId}"
                    if(ctMarkInput){
                        ctExamMark=Double.parseDouble(ctMarkInput)
                    }
                    count = classSubjectsService.isCtExamSubject(ctSubject, exam.className, exam.section.groupName)
                    if (count > 0) {
                        examSchedule=ExamSchedule.findBySubjectAndExamAndActiveStatus(ctSubject, exam, ActiveStatus.ACTIVE)
                        if(examSchedule){
                            examSchedule.ctExamDate=ctExamDate
                            examSchedule.ctPeriod=ctPeriod
                            examSchedule.ctExamMark=ctExamMark
                            examSchedule.subject=ctSubject
                            examSchedule.exam=exam
                            htMark = 0
                            if(examSchedule.hallExamMark){
                                htMark=examSchedule.hallExamMark
                            }
                            examSchedule.fullMark=htMark+ctExamMark
                            if (examSchedule.validate()) {
                                examSchedule.save()
                            }
                        }else {
                            examSchedule= new ExamSchedule(ctExamDate:ctExamDate,ctPeriod:ctPeriod,ctExamMark:ctExamMark,subject:ctSubject, exam:exam,fullMark:ctExamMark)
                            if (examSchedule.validate()) {
                                examSchedule.save()
                            }
                        }
                    }
                }
            }
            exam.examStatus = ExamStatus.NEW
            exam.ctSchedule=ScheduleStatus.ADDED
            exam.save()
        }
        result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
        result.put(CommonUtils.MESSAGE,'Exam Schedule Added Successfully')
        outPut = result as JSON
        render outPut
    }

    def saveClassHT(String examIds) {
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        List<String> items = Arrays.asList(examIds.split("\\s*,\\s*"))

        Long examId
        Exam exam
        def subjectIds
        List examSubjectIds
        def deletedSchedules
        //for all section schedule
        String subjectFieldName
        ExamPeriod examPeriod
        ShiftExam shiftExam
        def sectionIds
        int idxOfSection

        Long subId
        Date htExamDate
        Double htExamMark
        def htMarkInput
        ExamPeriod htPeriod
        def htPeriodInput
        SubjectName subject
        def inputHtDate
        ExamSchedule examSchedule

        Double ctMark
        int count = 0

        boolean isNewSubject = false
        if (params.isNewSubject && params.getBoolean("isNewSubject") == true){
            isNewSubject = true
        }
        items.each {strId ->
            examId = strId.toLong()
            exam = Exam.get(examId)
            if (!exam) {
                result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
                result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
                outPut = result as JSON
                render outPut
                return
            }
            subjectIds =params.subjectIds
            if(!subjectIds){
                result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
                result.put(CommonUtils.MESSAGE,"No Subject to add Schedule")
                outPut = result as JSON
                render outPut
                return
            }

            examSubjectIds = new ArrayList()
            if(subjectIds instanceof String){
                subId=Long.parseLong(subjectIds)
                subject=SubjectName.read(subId)
                examSubjectIds.add(subject)
            }else {
                def selectSubjects = subjectIds as List
                selectSubjects.each {String idx ->
                    subId=Long.parseLong(idx)
                    subject=SubjectName.read(subId)
                    examSubjectIds.add(subject)
                }
            }
            if (examSubjectIds) {
                //check if a subject removed from class subject
                if (!isNewSubject) {
                    deletedSchedules = examScheduleService.htSchedulesNotInList(exam, examSubjectIds)
                    for (schedule in deletedSchedules) {
                        schedule.activeStatus = ActiveStatus.DELETE
                        schedule.save()
                    }
                }

                for (htSubject in examSubjectIds) {
                    subId = htSubject.id
                    inputHtDate =params."examDate${subId}"
                    if(inputHtDate){
                        htExamDate= Date.parse('dd/MM/yyyy', inputHtDate)
                    }
                    htPeriodInput =params."examPeriod${subId}"
                    if(htPeriodInput){
                        htPeriod = ExamPeriod.read(htPeriodInput.toLong())
                    }
                    htMarkInput =params."examMark${subId}"
                    if(htMarkInput){
                        htExamMark=Double.parseDouble(htMarkInput)
                    }
                    count = classSubjectsService.isHtExamSubject(htSubject, exam.className, exam.section.groupName)
                    if (count > 0) {
                        examSchedule=ExamSchedule.findBySubjectAndExamAndActiveStatus(htSubject, exam, ActiveStatus.ACTIVE)
                        if(examSchedule){
                            examSchedule.hallExamDate=htExamDate
                            examSchedule.hallPeriod=htPeriod
                            examSchedule.hallExamMark=htExamMark
                            examSchedule.subject=htSubject
                            examSchedule.exam=exam
                            ctMark = 0
                            if(examSchedule.ctExamMark){
                                ctMark=examSchedule.ctExamMark
                            }
                            examSchedule.fullMark=ctMark+htExamMark
                            if (examSchedule.validate()) {
                                examSchedule.save()
                            }
                        }else {
                            examSchedule= new ExamSchedule(exam:exam,subject:htSubject,hallPeriod: htPeriod,hallExamMark:htExamMark,hallExamDate:htExamDate, fullMark: htExamMark)
                            if (examSchedule.validate()) {
                                examSchedule.save()
                            }
                        }
                    }
                }
            }
            exam.hallSchedule = ScheduleStatus.ADDED
            exam.save()
        }
        result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
        result.put(CommonUtils.MESSAGE,'Exam Schedule Added Successfully')
        outPut = result as JSON
        render outPut
    }

    def saveHT(Long examId) {
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        Exam exam = Exam.get(examId)
        if (!exam) {
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        def subjectIds =params.subjectIds
        if(!subjectIds){
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,"No Subject to add Schedule")
            outPut = result as JSON
            render outPut
            return
        }
        List examSubjectIds = new ArrayList()
        if(subjectIds instanceof String){
            examSubjectIds.add(subjectIds)
        }else {
            examSubjectIds = subjectIds as List
        }

        //for all section schedule
        String subjectFieldName
        ExamPeriod examPeriod
        ShiftExam shiftExam =exam.shiftExam
        def sectionIds = shiftExam.sectionIds.split(',')
        int idxOfSection

        Long subId
        Date htExamDate
        Double htExamMark
        def htMarkInput
        ExamPeriod htPeriod
        def htPeriodInput
        SubjectName subject
        def inputHtDate
        ExamSchedule examSchedule

        Double ctMark

        examSubjectIds.each {String idx ->
            subId=Long.parseLong(idx)
            subject=SubjectName.read(subId)
            if (subject){
                inputHtDate =params."examDate${subId}"
                if(inputHtDate){
                    htExamDate= Date.parse('dd/MM/yyyy', inputHtDate)
                }
                htPeriodInput =params."examPeriod${subId}"
                if(htPeriodInput){
                    htPeriod = ExamPeriod.read(htPeriodInput.toLong())
                }
                htMarkInput =params."examMark${subId}"
                if(htMarkInput){
                    htExamMark=Double.parseDouble(htMarkInput)
                }

                examSchedule=ExamSchedule.findBySubjectAndExamAndActiveStatus(subject,exam, ActiveStatus.ACTIVE)
                if(examSchedule){
                    examSchedule.hallExamDate=htExamDate
                    examSchedule.hallPeriod=htPeriod
                    examSchedule.hallExamMark=htExamMark
                    examSchedule.subject=subject
                    examSchedule.exam=exam
                    ctMark = 0
                    if(examSchedule.ctExamMark){
                        ctMark=examSchedule.ctExamMark
                    }
                    examSchedule.fullMark=ctMark+htExamMark
                    if (examSchedule.validate()) {
                        examSchedule.save()
                    }
                }else {
                    examSchedule= new ExamSchedule(exam:exam,subject:subject,hallPeriod: htPeriod,hallExamMark:htExamMark,hallExamDate:htExamDate, fullMark: htExamMark)
                    if (examSchedule.validate()) {
                        examSchedule.save()
                    }
                }
            }
            //for all section Exam Schedule
        }
        ClassName className=exam.className

        exam.hallSchedule = ScheduleStatus.ADDED
        exam.save()

        result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
        result.put(CommonUtils.MESSAGE,'Hall Schedule Added Successfully')
        result.put('examId',examId)
        outPut = result as JSON
        render outPut
    }

    def editCT(Long id) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        ExamSchedule examSchedule = ExamSchedule.read(id)
        if (!examSchedule) {
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        Exam exam = examSchedule.exam
        boolean ctPublish = false;
        if(exam.ctSchedule==ScheduleStatus.PUBLISHED){
            ctPublish = true;
        }
        result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
        result.put(CommonUtils.OBJ,examSchedule)
        result.put('objIsCTPublish',ctPublish)
        result.put('subName',examSchedule.subject.name)
        outPut = result as JSON
        render outPut
    }

    def editHT(Long id) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        ExamSchedule examSchedule = ExamSchedule.read(id)
        if (!examSchedule) {
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }

        Exam exam = examSchedule.exam
        boolean htPublish = false;
        if(exam.hallSchedule==ScheduleStatus.PUBLISHED){
            htPublish = true;
        }
        result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
        result.put(CommonUtils.OBJ,examSchedule)
        result.put('objIsHTPublish',htPublish)
        result.put('subName',examSchedule.subject.name)
        outPut = result as JSON
        render outPut
    }

    def updateCT(ExamScheduleCommand examScheduleCommand){
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        if (examScheduleCommand.hasErrors()) {
            def errorList = examScheduleCommand?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, errorList?.join('\n'))
            outPut = result as JSON
            render outPut
            return
        }
        ExamSchedule examSchedule = ExamSchedule.get(examScheduleCommand.id)
        if (!examSchedule) {
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        //first update examSchedule for all Section
        Exam exam = examSchedule.exam



        //update examSchedule for all section complete
        if(examSchedule.isCtMarkInput){
            examSchedule.properties['ctExamDate','ctPeriod']=examScheduleCommand.properties
        }else {
            examSchedule.properties['ctExamDate','ctPeriod','ctExamMark']=examScheduleCommand.properties
        }
        Double htMark = 0
        if(examSchedule.hallExamMark){
            htMark=examSchedule.hallExamMark
        }
        examSchedule.fullMark=htMark+examScheduleCommand.ctExamMark
        if (!examSchedule.validate()) {
            def errorList = examSchedule?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, errorList?.join('\n'))
            outPut = result as JSON
            render outPut
            return
        }
        ExamSchedule savedSchedule =examSchedule.save()
        exam.ctSchedule = ScheduleStatus.ADDED
        exam.save()
        /*if(savedSchedule){
            if(exam.ctSchedule==ScheduleStatus.PUBLISHED){
                calenderEventService.saveCtScheduleEvents(exam, savedSchedule,true)
            }
            result.put(CommonUtils.IS_ERROR,false)
            result.put(CommonUtils.MESSAGE,'Exam schedule Updated successfully')
            outPut=result as JSON
            render outPut
            return
        }*/
        result.put(CommonUtils.IS_ERROR,false)
        result.put(CommonUtils.MESSAGE,'CT schedule Updated successfully')
        outPut=result as JSON
        render outPut
        return

    }

    def updateHT(ExamScheduleCommand examScheduleCommand){
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        ExamSchedule examSchedule = ExamSchedule.get(examScheduleCommand.id)
        if (!examSchedule) {
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        //first update examSchedule for all Section
        Exam exam = examSchedule.exam

        if(examSchedule.isHallMarkInput){
            examSchedule.properties['hallExamDate','hallPeriod']=examScheduleCommand.properties
        }else {
            examSchedule.properties['hallExamDate','hallPeriod','hallExamMark']=examScheduleCommand.properties
        }
        Double ctMark = 0
        if(examSchedule.ctExamMark){
            ctMark=examSchedule.ctExamMark
        }
        examSchedule.fullMark=ctMark+examScheduleCommand.hallExamMark
        if (!examSchedule.validate()) {
            result.put(CommonUtils.IS_ERROR,true)
            result.put(CommonUtils.MESSAGE,'Please fill out form correctly')
            outPut=result as JSON
            render outPut
            return
        }
        ExamSchedule savedSchedule =examSchedule.save()
        exam.hallSchedule = ScheduleStatus.ADDED
        exam.save()
        /*if(savedSchedule){
            if(exam.hallSchedule==ScheduleStatus.PUBLISHED){
                calenderEventService.saveHallScheduleEvents(exam, savedSchedule,true)
            }
            result.put(CommonUtils.IS_ERROR,false)
            result.put(CommonUtils.MESSAGE,'Exam schedule Updated successfully')
            outPut=result as JSON
            render outPut
            return
        }*/
        result.put(CommonUtils.IS_ERROR,false)
        result.put(CommonUtils.MESSAGE,'Exam schedule Updated successfully')
        outPut=result as JSON
        render outPut
        return
    }

    def deleteSchedule(Long id){
        println(params)
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        ExamSchedule examSchedule = ExamSchedule.get(id)
        if (!examSchedule) {
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        Exam exam = examSchedule.exam
        if(exam.examStatus!=ExamStatus.NEW){
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,"Can not be delete as Exam ${exam.examStatus.value}.")
            outPut = result as JSON
            render outPut
            return
        }

        if(examSchedule.isCtMarkInput || examSchedule.isHallMarkInput){
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,"Can not delete as either CT or Hall mark entry completed.")
            outPut = result as JSON
            render outPut
            return
        }
        ExamType examType = ExamType.valueOf(params.examType)
        Long inputCount = examMarkService.getInputMarksCount(examSchedule, examType)
        if (inputCount > 0) {
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,"There are ${inputCount} entries for this schedule. Please delete all mark entries first")
            outPut = result as JSON
            render outPut
            return
        }

        if (examType == ExamType.CLASS_TEST) {
            if (examSchedule.hallExamDate) {
                examSchedule.ctExamDate = null
            } else {
                examSchedule.activeStatus = ActiveStatus.DELETE
            }
        } else {
            examSchedule.activeStatus = ActiveStatus.DELETE
        }
        examSchedule.save()
        exam.ctSchedule = ScheduleStatus.ADDED
        exam.hallSchedule = ScheduleStatus.ADDED
        exam.save()
        /*try {
            def examMarks = examMarkService.getScheduleMarks(examSchedule)
            examMarks.each {ExamMark examMark ->
                examMark.activeStatus = ActiveStatus.DELETE
                examMark.save()
            }
            def calenderEventList = calenderEventService.deleteExamScheduleEvents(examSchedule)
            examSchedule.activeStatus = ActiveStatus.DELETE
            examSchedule.save()
            exam.ctSchedule = ScheduleStatus.ADDED
            exam.hallSchedule = ScheduleStatus.ADDED
            exam.save()

        }
        catch(DataIntegrityViolationException e) {
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,"Mark already inserted. Please delete reference first")
            outPut = result as JSON
            render outPut
            return
        }*/
        result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
        result.put(CommonUtils.MESSAGE,"Schedule deleted successfully.")
        outPut = result as JSON
        render outPut

    }

    def publishCTSchedule(Long id){
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        ShiftExam shiftExam = ShiftExam.read(id)
        if (!shiftExam){
            result.put(CommonUtils.IS_ERROR,true)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut=result as JSON
            render outPut
            return
        }
        ClassName className = ClassName.read(params.getLong('className'))
        def classExams = examService.classExams(className, shiftExam)
        if(!classExams){
            result.put(CommonUtils.IS_ERROR,true)
            result.put(CommonUtils.MESSAGE,"No Exam found. Please contact with admin")
            outPut=result as JSON
            render outPut
            return
        }
        def allSubject
        String subjectIds
        for (exam in classExams) {
            if(exam.examStatus == ExamStatus.NEW && exam.ctSchedule == ScheduleStatus.ADDED){
                allSubject = classSubjectsService.ctExamSubjectHeaderIds(className, exam.section.groupName)
                if(allSubject){
                    subjectIds = allSubject.join(',')
                }
                exam.ctSubjectIds = subjectIds
                exam.ctSchedule =ScheduleStatus.PUBLISHED
                exam.save()
            }
            classSubjectsService.syncClassSubjectDetails(exam)
        }
        //@todo-aminul add calendar events when calendar is ready
        /*def examSchedules = examScheduleService.ctSchedules(exam)
        examSchedules.each { ExamSchedule examSchedule ->
            calenderEventService.saveCtScheduleEvents(exam, examSchedule,false)
        }*/
//        def allSubject = classSubjectsService.ctExamSubjectHeaderIds(exam.className, exam.section.groupName)


        result.put(CommonUtils.IS_ERROR,false)
        result.put(CommonUtils.MESSAGE,'Schedule Published successfully')
        outPut=result as JSON
        render outPut
        return
    }

    def syncClassSubject(Long id){
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        ShiftExam shiftExam = ShiftExam.read(id)
        if (!shiftExam){
            result.put(CommonUtils.IS_ERROR,true)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut=result as JSON
            render outPut
            return
        }
        ClassName className = ClassName.read(params.getLong('className'))
        boolean isExamPublished = examService.isExamPublished(shiftExam, className.id)
        if(isExamPublished){
            result.put(CommonUtils.IS_ERROR,true)
            result.put(CommonUtils.MESSAGE,"Result already published. You cann't change mark settings now.")
            outPut= result as JSON
            render outPut
            return
        }

        def classExams = examService.classExams(className, shiftExam)
        if(!classExams){
            result.put(CommonUtils.IS_ERROR,true)
            result.put(CommonUtils.MESSAGE,"No Exam found. Please contact with admin")
            outPut= result as JSON
            render outPut
            return
        }

        for (exam in classExams) {
            classSubjectsService.syncClassSubjectDetails(exam)
        }

        result.put(CommonUtils.IS_ERROR,false)
        result.put(CommonUtils.MESSAGE,'exam schedule syncronized with class subject successfully')
        outPut= result as JSON
        render outPut
        return

    }

    def publishHTSchedule(Long id){
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        ShiftExam shiftExam = ShiftExam.read(id)
        if (!shiftExam){
            result.put(CommonUtils.IS_ERROR,true)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut=result as JSON
            render outPut
            return
        }
        ClassName className = ClassName.read(params.getLong('className'))
        def classExams = examService.classExams(className, shiftExam)
        if(!classExams){
            result.put(CommonUtils.IS_ERROR,true)
            result.put(CommonUtils.MESSAGE,"No Exam found. Please contact with admin")
            outPut=result as JSON
            render outPut
            return
        }

        //@todo-aminul add this logic if calendar implementation complete
        /*def examSchedules = examScheduleService.hallSchedules(exam)
        examSchedules.each { ExamSchedule examSchedule ->
            totalExamMark+= examSchedule.fullMark
            calenderEventService.saveHallScheduleEvents(exam, examSchedule,false)
        }*/
        Double totalExamMark = 0
        boolean isOtherActivity = false
        if (shiftExam.examTerm == ExamTerm.FINAL_TEST) {
            isOtherActivity = true
        }

        def allSubject
        String subjectIds
        for (exam in classExams) {
            if(exam.examStatus == ExamStatus.NEW && exam.hallSchedule != ScheduleStatus.NO_SCHEDULE){
                allSubject = classSubjectsService.hallExamSubjectHeaderIds(className, isOtherActivity, exam.section.groupName)
                if(allSubject){
                    subjectIds = allSubject.join(',')
                }
                exam.hallSchedule=ScheduleStatus.PUBLISHED
                exam.subjectIds = subjectIds
                exam.numberOfSubject = allSubject.size()
                exam.totalExamMark = totalExamMark
                exam.save(flush: true)
            }
            classSubjectsService.syncClassSubjectDetails(exam)
        }

        result.put(CommonUtils.IS_ERROR,false)
        result.put(CommonUtils.MESSAGE,'Schedule Published successfully')
        outPut=result as JSON
        render outPut
        return
    }

}
