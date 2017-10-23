package com.grailslab.student

import com.grailslab.CommonUtils
import com.grailslab.enums.ExamTerm
import com.grailslab.enums.GroupName
import com.grailslab.enums.LetterGrade
import com.grailslab.enums.ResultStatus
import com.grailslab.gschoolcore.AcademicYear
import com.grailslab.settings.ClassName
import com.grailslab.settings.Exam
import com.grailslab.settings.Section
import com.grailslab.stmgmt.ExamMark
import com.grailslab.stmgmt.Student
import com.grailslab.stmgmt.Tabulation
import com.grailslab.stmgmt.TabulationCt
import grails.transaction.Transactional

class TabulationCtService {
    static transactional = false
    def studentService
    def examMarkService
    def studentSubjectsService

    TabulationCt getTabulation(Exam exam, Student student){
        return TabulationCt.findByExamAndStudent(exam, student)
    }
    TabulationCt entrySubjectMark(ClassName className, Section section, Exam exam, Student student, String fieldName, Double fieldMark){
        TabulationCt tabulation = new TabulationCt(className:className, section:section, exam:exam, examTerm: exam.examTerm, student:student, academicYear:exam.academicYear)
        tabulation."${fieldName}"=fieldMark.round(2)
        tabulation.save()
    }

    @Transactional
    def createCtTabulation(Section section, Exam exam){
        def studentList = studentService.allStudentForTabulation(section)
        def results = TabulationCt.findAllByExamAndStudentInList(exam, studentList)

        def subjectIdList
        int failedSubCounter = 0
        for (tabulation in results) {
            subjectIdList = studentSubjectsService.studentSubjectIds(tabulation.student)
            if (subjectIdList) {
                tabulation.totalObtainMark = examMarkService.getCtExamMark(exam, tabulation.student, subjectIdList)
                failedSubCounter = examMarkService.getCtFailCount(exam, tabulation.student, subjectIdList)
                if (failedSubCounter > 0) {
                    tabulation.resultStatus = ResultStatus.FAILED
                } else {
                    tabulation.resultStatus = ResultStatus.PASSED
                }
                tabulation.failedSubCounter = failedSubCounter
                tabulation.save()
            }
        }
        return true
    }

    def createSectionResult(Exam exam){
        def studentList = studentService.allStudentForTabulation(exam.section)
        def c = TabulationCt.createCriteria()
        def results = c.list() {
            and {
                eq("exam", exam)
                'in'("student", studentList)
            }
            order("failedSubCounter",CommonUtils.SORT_ORDER_ASC)
            order("totalObtainMark",CommonUtils.SORT_ORDER_DESC)
        }
        int position =1
        Double previousTotal = 0
        results.each {tabulation ->
            if(tabulation.totalObtainMark==previousTotal){
                --position
                tabulation.positionInSection = position
                tabulation.sectionStrPosition = CommonUtils.ordinal(position)
                position++
            }else {
                tabulation.positionInSection = position
                tabulation.sectionStrPosition = CommonUtils.ordinal(position)
                previousTotal = tabulation.totalObtainMark
                position++
            }
            tabulation.save()
        }
        return true
    }

    @Transactional
    def createClassResult(ClassName className, List examIds, AcademicYear academicYear){
        def studentList = studentService.byClassName(className, academicYear)
        def c = TabulationCt.createCriteria()
        def results = c.list() {
            createAlias("exam","eXm")
            and {
                eq("className", className)
                'in'('eXm.id',examIds)
                'in'('student',studentList)
            }
            order("failedSubCounter",CommonUtils.SORT_ORDER_ASC)
            order("totalObtainMark",CommonUtils.SORT_ORDER_DESC)
        }
        int position =1
        Double previousTotal = 0
        results.each {tabulation ->
            if(tabulation.totalObtainMark==previousTotal){
                --position
                tabulation.positionInClass = position
                tabulation.classStrPosition = CommonUtils.ordinal(position)
                position++
            }else {
                tabulation.positionInClass = position
                tabulation.classStrPosition = CommonUtils.ordinal(position)
                previousTotal = tabulation.totalObtainMark
                position++
            }
            tabulation.save()
        }
        return true
    }

    def listStudentBasedClsPosition(ClassName className, List examIds, GroupName groupName, int offset, int limit){
        def c = TabulationCt.createCriteria()
        def results = c.list(max: limit, offset: offset) {
            createAlias("exam","eXm")
            createAlias("section","sec")
            and {
                eq("className", className)
                if (groupName) {
                    eq("sec.groupName", groupName)
                }
                'in'('eXm.id',examIds)
            }
            order("positionInClass",CommonUtils.SORT_ORDER_ASC)
            projections {
                property('student')
            }
        }
        return results
    }
}
