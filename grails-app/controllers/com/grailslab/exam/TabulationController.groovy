package com.grailslab.exam

import com.grailslab.CommonUtils
import com.grailslab.enums.ExamStatus
import com.grailslab.enums.ExamTerm
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.settings.Exam
import com.grailslab.settings.ExamSchedule
import com.grailslab.settings.Section
import grails.converters.JSON

class TabulationController {
    def tabulationService
    def examService
    def studentService
    def schoolService
    def examScheduleService
    def tabulationCtService

    def index() {}
    def createTabulation(Long id){
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        Exam exam=Exam.get(id)
        if (!exam){
            result.put(CommonUtils.IS_ERROR,true)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_ERROR_MESSAGE)
            outPut=result as JSON
            render outPut
            return
        }
        if(exam.examStatus==ExamStatus.PUBLISHED || exam.examStatus==ExamStatus.RESULT){
            result.put(CommonUtils.IS_ERROR,true)
            result.put(CommonUtils.MESSAGE,'Result already prepared or published')
            outPut=result as JSON
            render outPut
            return
        }

        if(exam.examStatus==ExamStatus.TABULATION){
            result.put(CommonUtils.IS_ERROR,true)
            result.put(CommonUtils.MESSAGE,'Tabulation already Created.')
            outPut=result as JSON
            render outPut
            return
        }
        def examSchedules = examScheduleService.examSchedules(exam)
        if(!examSchedules){
            result.put(CommonUtils.IS_ERROR,true)
            result.put(CommonUtils.MESSAGE,'No Exam Schedule Added')
            outPut=result as JSON
            render outPut
            return
        }
        boolean entryNotComplete = false
        String subjects =''
        for (examSchedule in examSchedules) {
            if (!examSchedule.isHallMarkInput){
                entryNotComplete = true
                subjects += examSchedule.subject.name +', '
            }

        }
        if(entryNotComplete){
            result.put(CommonUtils.IS_ERROR,true)
            result.put(CommonUtils.MESSAGE,'Mark Entry not yet complete of '+subjects)
            outPut=result as JSON
            render outPut
            return
        }
        Section section = exam.section

        if (exam.examTerm == ExamTerm.FIRST_TERM) {
            tabulationService.createSectionTermTabulation(section, exam)
        } else {
            //final term exam
            tabulationService.createSectionFinalTabulation(section, exam)
        }
        exam.studentCount = studentService.numberOfStudent(section)
        exam.examStatus =ExamStatus.TABULATION
        exam.tabulationPreparedDate = new Date()
        exam.save()
        result.put(CommonUtils.IS_ERROR,false)
        result.put(CommonUtils.MESSAGE,'Tabulation Created Successfully.')
        outPut=result as JSON
        render outPut
    }

    def createCtTabulation(Long id){
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        Exam exam=Exam.get(id)
        if (!exam){
            result.put(CommonUtils.IS_ERROR,true)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_ERROR_MESSAGE)
            outPut=result as JSON
            render outPut
            return
        }
        if(exam.ctExamStatus==ExamStatus.PUBLISHED || exam.ctExamStatus==ExamStatus.RESULT){
            result.put(CommonUtils.IS_ERROR,true)
            result.put(CommonUtils.MESSAGE,'Result already prepared or published')
            outPut=result as JSON
            render outPut
            return
        }

        if(exam.ctExamStatus == ExamStatus.TABULATION){
            result.put(CommonUtils.IS_ERROR,true)
            result.put(CommonUtils.MESSAGE,'Tabulation already Created.')
            outPut=result as JSON
            render outPut
            return
        }
        def examSchedules = examScheduleService.ctSchedules(exam)
        if(!examSchedules){
            result.put(CommonUtils.IS_ERROR,true)
            result.put(CommonUtils.MESSAGE,'No Exam Schedule Added')
            outPut=result as JSON
            render outPut
            return
        }
        boolean entryNotComplete = false
        String subjects =''
        for (examSchedule in examSchedules) {
            if (!examSchedule.isCtMarkInput){
                entryNotComplete = true
                subjects += examSchedule.subject.name +', '
            }

        }
        if(entryNotComplete){
            result.put(CommonUtils.IS_ERROR,true)
            result.put(CommonUtils.MESSAGE,'Mark Entry not yet complete of '+subjects)
            outPut=result as JSON
            render outPut
            return
        }
        Section section = exam.section
        tabulationCtService.createCtTabulation(section, exam)

        exam.ctExamStatus =ExamStatus.TABULATION
        exam.save()
        result.put(CommonUtils.IS_ERROR,false)
        result.put(CommonUtils.MESSAGE,'Ct tabulation created successfully.')
        outPut=result as JSON
        render outPut
    }


}