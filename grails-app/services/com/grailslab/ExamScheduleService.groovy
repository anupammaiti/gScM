package com.grailslab

import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.settings.Exam
import com.grailslab.settings.ExamSchedule
import com.grailslab.settings.School
import grails.transaction.Transactional

class ExamScheduleService {
    static transactional = false

    @Transactional
    def updateSchedule(Long id, Double highestMark, Double averageMark){
        ExamSchedule examSchedule = ExamSchedule.get(id)
        examSchedule.highestMark = highestMark
        examSchedule.averageMark = averageMark
        examSchedule.save(flush: true)
    }
    LinkedHashMap examSchedulePaginateListCT(Exam exam){
        List dataReturns = new ArrayList()
        def c = ExamSchedule.createCriteria()
        def results = c.list() {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE )
                eq("exam", exam)
                isNotNull("ctExamDate")
            }
        }
                String markEntryStatus
            results.each { ExamSchedule examSchedule ->
                if(examSchedule.isCtMarkInput){
                    markEntryStatus = 'Completed'
                } else {
                    markEntryStatus = 'Not Complete'
                }
                dataReturns.add([ DT_RowId: examSchedule.id, subject: examSchedule.subject.name, ctMark: examSchedule?.ctExamMark, examTime: examSchedule?.ctPeriod?.name, examDate: CommonUtils.getUiDateStr(examSchedule?.ctExamDate),duration:examSchedule?.ctPeriod?.duration +' Minute',status:markEntryStatus,isCtMarkInput:examSchedule?.isCtMarkInput])
        }
        return [results:dataReturns]
    }
    LinkedHashMap examSchedulePaginateListHT(Exam exam){
        List dataReturns = new ArrayList()
        def c = ExamSchedule.createCriteria()
        def results = c.list() {
            createAlias('subject', 'sbt')
            eq("exam", exam)
            eq("activeStatus", ActiveStatus.ACTIVE )
            or{
                and {
                    isNotNull("hallExamDate")
                }
                and {
                    eq("isExtracurricular", true)
                }
            }
            order("sbt.sortPosition", CommonUtils.SORT_ORDER_ASC)
        }
        String markEntryStatus
        results.each { ExamSchedule examSchedule ->
            markEntryStatus=''
            if(examSchedule.isExtracurricular){
                markEntryStatus = examSchedule.isHallMarkInput?'Entry Complete ':'Entry Not Complete'
                dataReturns.add([DT_RowId: examSchedule.id, subject: examSchedule.subject.name,hallMark: examSchedule.hallExamMark, examTime: 'N/A', examDate:'Extracurricular Subject',duration: 'N/A',status:markEntryStatus,isHallMarkInput:examSchedule.isHallMarkInput])
            }else{
                if(examSchedule.ctExamDate){
                    markEntryStatus = examSchedule.isCtMarkInput?'CT Complete, ':'CT Not Complete, '
                }
                markEntryStatus += examSchedule.isHallMarkInput?'Hall Complete ':'Hall Not Complete'
                dataReturns.add([DT_RowId: examSchedule.id, subject: examSchedule.subject.name,hallMark: examSchedule.hallExamMark, examTime: examSchedule?.hallPeriod?.name, examDate:CommonUtils.getUiDateStr(examSchedule?.hallExamDate),duration: examSchedule?.hallPeriod?.duration+ ' Minutes', status:markEntryStatus,isHallMarkInput:examSchedule.isHallMarkInput])
            }
        }
        return [results:dataReturns]
    }

    LinkedHashMap examScheduleList(Exam exam){
        List dataReturns = new ArrayList()
        def c = ExamSchedule.createCriteria()
        def results = c.list() {
            createAlias('subject', 'sbt')
            and {
                eq("activeStatus", ActiveStatus.ACTIVE )
                eq("exam", exam)
            }
            order("sbt.sortPosition", CommonUtils.SORT_ORDER_ASC)

        }
        String ctMark
        String ctEntryStatus
        boolean isCtExam
        String hallMark
        String hallEntryStatus
        Boolean showCtComplete
        Boolean showHallComplete
            results.each { ExamSchedule examSchedule ->
                showCtComplete = false
                showHallComplete = false
                if(examSchedule.isExtracurricular){
                    ctMark =CommonUtils.NOT_APPLICABLE
                    ctEntryStatus =CommonUtils.NOT_APPLICABLE
                    isCtExam = false
                    hallMark =examSchedule.hallExamMark
                    hallEntryStatus =examSchedule.isHallMarkInput?'Completed ':'Not Complete'
                    showHallComplete = !examSchedule.isHallMarkInput
                }else{
                    if(examSchedule.ctExamDate){
                        ctMark =examSchedule.ctExamMark
                        ctEntryStatus =examSchedule.isCtMarkInput?'Completed':'Not Complete'
                        showCtComplete = !examSchedule.isCtMarkInput
                        isCtExam = true
                    } else {
                        ctMark =CommonUtils.NOT_APPLICABLE
                        ctEntryStatus =CommonUtils.NOT_APPLICABLE
                        isCtExam = false
                    }
                    if(examSchedule.hallExamDate){
                        hallMark =examSchedule.hallExamMark
                        hallEntryStatus =examSchedule.isHallMarkInput?'Completed':'Not Complete'
                        showHallComplete = !examSchedule.isHallMarkInput
                    } else {
                        hallMark =CommonUtils.NOT_APPLICABLE
                        hallEntryStatus =CommonUtils.NOT_APPLICABLE
                    }
                }
                dataReturns.add([id: examSchedule.id, subjectName: examSchedule.subject.name,
                                 ctMark: ctMark, ctEntryStatus:ctEntryStatus, isCtExam:isCtExam,
                                 hallMark:hallMark,hallEntryStatus:hallEntryStatus,showCtComplete:showCtComplete, showHallComplete:showHallComplete])
            }
        return [results:dataReturns]
    }

    def ctSchedules(Exam exam){
        def c = ExamSchedule.createCriteria()
        def results = c.list() {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE )
                eq("exam", exam)
                isNotNull("ctExamDate")
            }
        }
        return results
    }

    def ctSchedulesNotInList(Exam exam, def subjectList){
        def c = ExamSchedule.createCriteria()
        def results = c.list() {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE )
                eq("exam", exam)
                not {'in'("subject", subjectList)}
                isNotNull("ctExamDate")
            }
        }
        return results
    }
    def ctClassSchedules(def examIdList){
        def c = ExamSchedule.createCriteria()
        def results = c.list() {
            createAlias('exam', 'xm')
            and {
                eq("activeStatus", ActiveStatus.ACTIVE )
                'in'("xm.id", examIdList)
                isNotNull("ctExamDate")
            }
            projections {
                groupProperty 'subject'
                groupProperty 'ctExamMark'
                groupProperty 'ctPeriod'
                groupProperty 'ctExamDate'
            }
            order ('ctExamDate', 'asc')
        }
        return results
    }

    def hallSchedules(Exam exam){
        def c = ExamSchedule.createCriteria()
        def results = c.list() {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE )
                eq("exam", exam)
                isNotNull("hallExamDate")
            }
        }
        return results
    }
    def htClassSchedules(def examIdList){
        def c = ExamSchedule.createCriteria()
        def results = c.list() {
            createAlias('exam', 'xm')
            and {
                eq("activeStatus", ActiveStatus.ACTIVE )
                'in'("xm.id", examIdList)
                isNotNull("hallExamDate")
            }
            projections {
                groupProperty 'subject'
                groupProperty 'hallExamMark'
                groupProperty 'hallPeriod'
                groupProperty 'hallExamDate'
            }
            order ('hallExamDate', 'asc')
        }
        return results
    }
    def htSchedulesNotInList(Exam exam, def subjectList){
        def c = ExamSchedule.createCriteria()
        def results = c.list() {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE )
                eq("exam", exam)
                not {'in'("subject", subjectList)}
                isNotNull("hallExamDate")
            }
        }
        return results
    }

    def examSchedules(Exam exam){
        def c = ExamSchedule.createCriteria()
        def results = c.list() {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE )
                eq("exam", exam)
            }
        }
        return results
    }

    def ctSchedulesDropDown(Exam exam){
        List dataReturns = new ArrayList()
        def c = ExamSchedule.createCriteria()
        def results = c.list() {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE )
                eq("exam", exam)
                isNotNull("ctExamDate")
            }
        }
        results.each { ExamSchedule schedule ->
            dataReturns.add([id: schedule.id, subjectId: schedule.subject.id, name: schedule.subject.name])
        }
        return dataReturns
    }
    def hallSchedulesDropDown(Exam exam){
        List dataReturns = new ArrayList()
        def c = ExamSchedule.createCriteria()
        def results = c.list() {
                eq("activeStatus", ActiveStatus.ACTIVE )
                eq("exam", exam)
            or{
                and {
                    isNotNull("hallExamDate")
                }
                and {
                    eq("isExtracurricular", true)
                }
            }
        }
        results.each { ExamSchedule schedule ->
            dataReturns.add([id: schedule.id, subjectId: schedule.subject.id, name: schedule.subject.name])
        }
        return dataReturns
    }
}
