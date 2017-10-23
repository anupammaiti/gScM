package com.grailslab


import com.grailslab.enums.ExamStatus
import com.grailslab.enums.ExamTerm
import com.grailslab.enums.ExamType
import com.grailslab.enums.GroupName
import com.grailslab.enums.ScheduleStatus
import com.grailslab.gschoolcore.AcademicYear
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.settings.ClassName
import com.grailslab.settings.Exam
import com.grailslab.settings.ExamSchedule
import com.grailslab.settings.School
import com.grailslab.settings.Section
import com.grailslab.settings.ShiftExam
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

class ExamService {
    static transactional = false
    def schoolService
    static final String[] sortColumns = ['cName.sortPosition','s.name','examTerm','sExam.resultPublishOn']
    LinkedHashMap examPaginateList(GrailsParameterMap params, ShiftExam shiftExam, ClassName className){
        int iDisplayStart = params.iDisplayStart ? params.getInt('iDisplayStart') : CommonUtils.DEFAULT_PAGINATION_START
        int iDisplayLength = params.iDisplayLength ? params.getInt('iDisplayLength') : CommonUtils.DEFAULT_PAGINATION_LENGTH
        String sSortDir = params.sSortDir_0 ? params.sSortDir_0 : CommonUtils.SORT_ORDER_ASC
        int iSortingCol = params.iSortCol_0 ? params.getInt('iSortCol_0') : CommonUtils.DEFAULT_PAGINATION_SORT_IDX
        String sSearch = params.sSearch ? params.sSearch : null
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }

        List dataReturns = new ArrayList()
        def workingYears = schoolService.workingYears()
        def c = Exam.createCriteria()
        def results = c.list(max: iDisplayLength, offset: iDisplayStart) {
            and {
                'in'("academicYear", workingYears)
                eq("activeStatus", ActiveStatus.ACTIVE)
                eq("examStatus", ExamStatus.NEW)
                eq("shiftExam", shiftExam)
                if (className) {
                    eq("className", className)
                }
            }
            order('section', sSortDir)
        }
        int totalCount = results.totalCount
        String schedule
        Section section
        if (totalCount > 0) {
            for (exam in results) {
                section = exam.section
                schedule = 'CT '+exam.ctSchedule.value+', Hall '+exam.hallSchedule.value
                dataReturns.add([DT_RowId: exam.id, ctSchedule: exam.ctSchedule.key, hallSchedule:exam.hallSchedule.key,  0: section.className.name, 1: section.name, 2: schedule, 3:''])
            }
        }
        return [totalCount:totalCount,results:dataReturns]
    }


    def examListForPreparingResult(ShiftExam shiftExam, ClassName className, GroupName groupName = null){
        def c = Exam.createCriteria()
        def results = c.list() {
            if (groupName) {
                createAlias('section','sec')
            }
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
                eq("className", className)
                eq("shiftExam", shiftExam)
                if (groupName) {
                    eq("sec.groupName", groupName)
                }
            }
        }
        return results
    }
    def classExams(ClassName className, ShiftExam shiftExam){
        def c = Exam.createCriteria()
        def results = c.list() {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
                eq("className", className)
                eq("shiftExam", shiftExam)
                eq("examStatus", ExamStatus.NEW)
            }
        }
        return results
    }
    Boolean isExamPublished(ShiftExam shiftExam, Long className){
        def c = Exam.createCriteria()
        def count = c.list() {
            createAlias('className','cls')
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
                eq("cls.id", className)
                eq("shiftExam", shiftExam)
                'in'("examStatus", [ExamStatus.RESULT, ExamStatus.PUBLISHED] as List)
            }
            projections {
                count()
            }
        }
        if (count[0] > 0) {
            return true
        }
        return false
    }

    Boolean isExamSchedule(ShiftExam shiftExam, Long classNameId, ExamType examType, List scheduleStatus){
        def c = Exam.createCriteria()
        def count = c.list() {
            createAlias('className','cls')
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
                eq("cls.id", classNameId)
                eq("shiftExam", shiftExam)
                if (examType == ExamType.CLASS_TEST) {
                    'in'("ctSchedule", scheduleStatus)
                } else {
                    'in'("hallSchedule", scheduleStatus)
                }
            }
            projections {
                count()
            }
        }
        if (count[0] > 0) {
            return true
        }
        return false
    }

    def sectionExamList(Section section){
        def c = Exam.createCriteria()
        def results = c.list() {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
                eq("section", section)
            }
        }
        return results
    }

    def classExamIdList(ShiftExam shiftExam, ClassName className, GroupName groupName = null){
        def c = Exam.createCriteria()
        def results = c.list() {
            createAlias('section','sec')
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
                eq("className", className)
                eq("shiftExam", shiftExam)
                if (groupName) {
                    eq("sec.groupName", groupName)
                }
            }
            projections {
                property('id')
            }
        }
        return results
    }

    def classExamIdListBatchPromotion(ShiftExam shiftExam, ClassName className, GroupName groupName = null){
        def c = Exam.createCriteria()
        def results = c.list() {
            createAlias('section','sec')
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
                eq("className", className)
                eq("shiftExam", shiftExam)
                if (groupName) {
                    eq("sec.groupName", groupName)
                }
            }
            projections {
                property('id')
            }
        }
        return results
    }
    def classExamList(ShiftExam shiftExam, ClassName className, GroupName groupName = null){
        def c = Exam.createCriteria()
        def results = c.list() {
            createAlias('section','sec')
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
                eq("className", className)
                eq("shiftExam", shiftExam)
                if (groupName) {
                    eq("sec.groupName", groupName)
                }
            }
        }
        return results
    }

    def examsNotInSectionIdList(ShiftExam shiftExam, List sectionIds){
        def c = Exam.createCriteria()
        def results = c.list() {
            createAlias('section','sec')
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
                eq("shiftExam", shiftExam)
                not{'in'("sec.id", sectionIds)}
            }
        }
        return results
    }
    def examsForManageTabulation(ShiftExam shiftExam, ClassName className){
        def c = Exam.createCriteria()
        def results = c.list() {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
                eq("className", className)
                eq("shiftExam", shiftExam)
                'in'("examStatus", ExamStatus.resultWorkingList())
            }
        }
        return results
    }

    def examsForSmsResult(ShiftExam shiftExam, ClassName className){
        def c = Exam.createCriteria()
        def results = c.list() {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
                eq("shiftExam", shiftExam)
                if (className){
                    eq("className", className)
                }
                eq("examStatus", ExamStatus.RESULT)
            }
        }
        return results
    }

    def classExamsDropDownAsSectionName(ShiftExam shiftExam, ClassName className, GroupName groupName, String examType = 'new'){
        def c = Exam.createCriteria()
        def results = c.list() {
            if (groupName) {
                createAlias('section','sec')
            }
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
                eq("className", className)
                eq("shiftExam", shiftExam)
                if (groupName) {
                    eq("sec.groupName", groupName)
                }
                if (examType == 'new') {
                    eq("examStatus", ExamStatus.NEW)
                } else if(examType == 'working') {
                    'in'("examStatus", ExamStatus.resultWorkingList())
                } else if (examType == 'publishing'){
                    'in'("examStatus", ExamStatus.resultPublishingList())
                } else if (examType == 'published') {
                    eq("examStatus", ExamStatus.PUBLISHED)
                }
            }
        }
        List dataReturns = new ArrayList()
        for (exam in results) {
            dataReturns.add([id: exam.id, name: exam.section.name])
        }
        return dataReturns
    }


    Exam getExam(ShiftExam shiftExam, Section section){
        Exam exam = Exam.findByShiftExamAndSectionAndActiveStatus(shiftExam,section,ActiveStatus.ACTIVE)
        return exam
    }
    int examTerm(Exam exam) {
        if (exam.examTerm == ExamTerm.FINAL_TEST) return 0
        int termCount = Exam.countByExamTermAndSectionAndActiveStatusAndHallScheduleNotEqual(ExamTerm.FIRST_TERM, exam.section, ActiveStatus.ACTIVE, ScheduleStatus.NO_SCHEDULE)
        if (termCount < 2) return 1
        termCount = Exam.countByExamTermAndSectionAndActiveStatusAndHallScheduleNotEqualAndIdGreaterThan(ExamTerm.FIRST_TERM, exam.section, ActiveStatus.ACTIVE, ScheduleStatus.NO_SCHEDULE, exam.id)
        if (termCount == 0 ) return 2
        return 1
    }
    int numberOfTermExam(Section section) {
        Exam.countByExamTermAndSectionAndActiveStatusAndHallScheduleNotEqual(ExamTerm.FIRST_TERM, section, ActiveStatus.ACTIVE, ScheduleStatus.NO_SCHEDULE)
    }

    //exam ids for result analytics
    def publishingExamList(ShiftExam shiftExam, ExamType examType, ClassName className, GroupName groupName){
        def c = Exam.createCriteria()
        def results = c.list() {
            createAlias('className','cls')
            if (groupName) {
                createAlias('section','sec')
            }
            and {
                eq("shiftExam", shiftExam)
                eq("activeStatus", ActiveStatus.ACTIVE)
                if (className) {
                    eq("className", className)
                }
                if (groupName) {
                    eq("sec.groupName", groupName)
                }
                if(examType && examType == ExamType.CLASS_TEST) {
                    'in'("ctExamStatus", ExamStatus.resultWorkingList())
                } else {
                    'in'("examStatus", ExamStatus.resultWorkingList())
                }
            }
            order("cls.sortPosition", CommonUtils.SORT_ORDER_ASC)
        }
        return results
    }
    def sectionListFromExamIds(List publishingExamIds){
        def c = Exam.createCriteria()
        def results = c.list() {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE)
                'in'("id", publishingExamIds)
            }
            projections {
                property('section')
            }
        }
        return results
    }
}
