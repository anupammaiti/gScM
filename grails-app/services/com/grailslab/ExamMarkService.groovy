package com.grailslab

import com.grailslab.enums.AttendStatus
import com.grailslab.enums.ExamTerm
import com.grailslab.enums.ExamType
import com.grailslab.enums.LetterGrade
import com.grailslab.enums.ResultStatus
import com.grailslab.enums.StudentStatus
import com.grailslab.enums.SubjectType
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.settings.*
import com.grailslab.stmgmt.ExamMark
import com.grailslab.stmgmt.Student
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

class ExamMarkService {
    static transactional = false
    def gradePointService
    def studentSubjectsService
    def subjectService
    def schoolService

    static final String[] sortColumns = ['id','student']
    static final String[] ctMarkSortColumns = ['std.studentID','std.name','std.rollNo','ctObtainMark','hallObtainMark']
    static final String[] hallMarkSortColumns = ['std.studentID','std.name','std.rollNo','ctObtainMark','hallObtainMark']
    static final String[] hallMarkWithPracSortColumns = ['std.studentID','std.name','std.rollNo','hallWrittenMark','hallObjectiveMark','hallPracticalMark','hallSbaMark','ctObtainMark','hallObtainMark']


    def getCtMarkEntryList(GrailsParameterMap params, Section section, ExamSchedule examSchedule,SubjectName subject) {
        int iDisplayStart = params.iDisplayStart ? params.getInt('iDisplayStart') : CommonUtils.DEFAULT_PAGINATION_START
        int iDisplayLength = params.iDisplayLength ? params.getInt('iDisplayLength') : CommonUtils.DEFAULT_PAGINATION_LENGTH
        String sSortDir = params.sSortDir_0 ? params.sSortDir_0 : CommonUtils.DEFAULT_PAGINATION_SORT_ORDER
        int iSortingCol = params.iSortCol_0 ? params.getInt('iSortCol_0') : 2
        //Search string, use or logic to all fields that required to include
        String sSearch = params.sSearch ? params.sSearch : null
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }

        String sortColumn = CommonUtils.getSortColumn(ctMarkSortColumns, iSortingCol)
        List dataReturns = new ArrayList()
        int totalCount =0
        def c = ExamMark.createCriteria()
        def results = c.list(max: iDisplayLength, offset: iDisplayStart) {
            createAlias('student', 'std')
            and {
                eq("activeStatus", ActiveStatus.ACTIVE )
                eq("examSchedule", examSchedule )
                eq("std.studentStatus", StudentStatus.NEW)
                eq("subject", subject )
                'ne'("ctAttendStatus", AttendStatus.NO_INPUT )
            }
            if (sSearch) {
                or {
                    ilike('std.name', sSearch)
                    ilike('std.studentID', sSearch)
                }
            }
            order(sortColumn, sSortDir)
        }
        totalCount = results.totalCount
        Student student
        if (totalCount > 0) {
            String ctMark
            String hallMark
            results.each { ExamMark examMark ->
                if(examMark.ctAttendStatus==AttendStatus.PRESENT){
                    ctMark =examMark.ctObtainMark
                } else {
                    ctMark = examMark.ctAttendStatus.value
                }
                if(examMark.hallAttendStatus==AttendStatus.PRESENT){
                    hallMark =examMark.hallObtainMark
                } else {
                    hallMark = examMark.hallAttendStatus.value
                }
                student = examMark.student
                dataReturns.add([DT_RowId: examMark.id, stdId:student.id, 0: student.studentID, 1: student.name, 2:student.rollNo, 3: ctMark, 4: hallMark, 5: subject.name, 6:''])

            }
        }
        return [totalCount:totalCount,results:dataReturns]
    }

    def getAlreadyCtMarkStudents(ExamSchedule examSchedule, SubjectName subject) {
        def c = ExamMark.createCriteria()
        def results = c.list() {
            createAlias('student', 'std')
            and {
                eq("activeStatus", ActiveStatus.ACTIVE )
                eq("examSchedule", examSchedule )
                eq("std.studentStatus", StudentStatus.NEW)
                eq("subject", subject )
                'ne'("ctAttendStatus", AttendStatus.NO_INPUT )
            }
            projections {
                property('std.id')
            }
        }
    }
    def getAlreadyHallMarkStudents(ExamSchedule examSchedule, SubjectName subject) {
        def c = ExamMark.createCriteria()
        def results = c.list() {
            createAlias('student', 'std')
            and {
                eq("activeStatus", ActiveStatus.ACTIVE )
                eq("examSchedule", examSchedule )
                eq("std.studentStatus", StudentStatus.NEW)
                eq("subject", subject )
                'ne'("hallAttendStatus", AttendStatus.NO_INPUT )
            }
            projections {
                property('std.id')
            }
        }
    }
    def getAllExamMark(ExamSchedule examSchedule, SubjectName subject) {
        def c = ExamMark.createCriteria()
        def results = c.list() {
            createAlias('student', 'std')
            and {
                eq("activeStatus", ActiveStatus.ACTIVE )
                eq("examSchedule", examSchedule )
                eq("std.studentStatus", StudentStatus.NEW)
                eq("subject", subject )
                'ne'("hallAttendStatus", AttendStatus.NO_INPUT )
            }
        }
        return results
    }

    def getAllCtExamMark(ExamSchedule examSchedule, SubjectName subject) {
        def c = ExamMark.createCriteria()
        def results = c.list() {
            createAlias('student', 'std')
            and {
                eq("activeStatus", ActiveStatus.ACTIVE )
                eq("examSchedule", examSchedule )
                eq("std.studentStatus", StudentStatus.NEW)
                eq("subject", subject )
                'ne'("ctAttendStatus", AttendStatus.NO_INPUT )
            }
        }
        return results
    }
    def getStudentResultMarks(Exam exam, Student student) {
        def subjectIdList = studentSubjectsService.studentSubjectIds(student)
        if (!subjectIdList) return null

        def c = ExamMark.createCriteria()
        def results = c.list() {
            createAlias('examSchedule', 'eSch')
            createAlias('subject', 'sub')
            and {
                eq("activeStatus", ActiveStatus.ACTIVE )
                eq("eSch.activeStatus", ActiveStatus.ACTIVE )
                eq("exam", exam )
                'in'("sub.id", subjectIdList )
                eq('student', student)
//                eq('eSch.isExtracurricular', false)
                'ne'("hallAttendStatus", AttendStatus.NO_INPUT )
            }
        }
        return results
    }

    def getCtExamMark(Exam exam, Student student, List subjectIdList) {
        def c = ExamMark.createCriteria()
        def results = c.list() {
            createAlias('examSchedule', 'eSch')
            createAlias('subject', 'sub')
            and {
                eq("activeStatus", ActiveStatus.ACTIVE )
                eq("eSch.activeStatus", ActiveStatus.ACTIVE )
                eq("exam", exam )
                'in'("sub.id", subjectIdList )
                eq('student', student)
                'ne'("ctAttendStatus", AttendStatus.NO_INPUT )
            }
            projections {
                sum('ctObtainMark')
            }
        }
        return results[0]
    }

    def getCtFailCount(Exam exam, Student student, List subjectIdList) {
        def c = ExamMark.createCriteria()
        def results = c.list() {
            createAlias('examSchedule', 'eSch')
            createAlias('subject', 'sub')
            and {
                eq("activeStatus", ActiveStatus.ACTIVE )
                eq("eSch.activeStatus", ActiveStatus.ACTIVE )
                eq("exam", exam )
                eq("ctResultStatus", ResultStatus.FAILED )
                'in'("sub.id", subjectIdList )
                eq('student', student)
                'ne'("ctAttendStatus", AttendStatus.NO_INPUT )
            }
            projections {
                count()
            }
        }
        return results[0]
    }

    def getStudentExamAttendCount(Exam exam, Student student) {
        return ExamMark.countByExamAndStudentAndActiveStatusAndHallAttendStatus(exam, student, ActiveStatus.ACTIVE, AttendStatus.PRESENT)
    }
    def getExamSubjectAbsentCount(List classExamList, def studentList, SubjectName subjectName) {
        return ExamMark.countByExamInListAndStudentInListAndSubjectAndActiveStatusAndHallAttendStatusNotEqual(classExamList, studentList, subjectName, ActiveStatus.ACTIVE, AttendStatus.PRESENT)
    }
    //for alternative subject
    def getExamSubjectAbsentCount(List classExamList, def studentList, String alterSubjectIds) {
        def alterSubjectList = subjectService.allSubjectInList(alterSubjectIds)
        return ExamMark.countByExamInListAndStudentInListAndSubjectInListAndActiveStatusAndHallAttendStatusNotEqual(classExamList, studentList, alterSubjectList, ActiveStatus.ACTIVE, AttendStatus.PRESENT)
    }
    def getExamSubjectPassCount(List classExamList, def studentList, SubjectName subjectName) {
        return ExamMark.countByExamInListAndStudentInListAndSubjectAndActiveStatusAndResultStatus(classExamList, studentList, subjectName, ActiveStatus.ACTIVE, ResultStatus.PASSED)
    }
    //for alternative subject
    def getExamSubjectPassCount(List examList, def studentList, String alterSubjectIds) {
        def alterSubjectList = subjectService.allSubjectInList(alterSubjectIds)
        return ExamMark.countByExamInListAndStudentInListAndSubjectInListAndActiveStatusAndResultStatus(examList, studentList, alterSubjectList, ActiveStatus.ACTIVE, ResultStatus.PASSED)
    }
    def getExamSubjectLetterGradeCount(List classExamList, def studentList, SubjectName subjectName, String lGrade) {
        return ExamMark.countByExamInListAndStudentInListAndSubjectAndActiveStatusAndLGrade(classExamList, studentList, subjectName, ActiveStatus.ACTIVE, lGrade)
    }
    def getExamSubjectResultCount(List classExamList, def studentList, SubjectName subjectName, ResultStatus resultStatus) {
        int resultCount = 0
        if (resultStatus) {
            resultCount = ExamMark.countByExamInListAndStudentInListAndSubjectAndActiveStatusAndResultStatus(classExamList, studentList, subjectName, ActiveStatus.ACTIVE, resultStatus)
        } else {
            resultCount = ExamMark.countByExamInListAndStudentInListAndSubjectAndActiveStatus(classExamList, studentList, subjectName, ActiveStatus.ACTIVE)
        }
        return resultCount
    }
    //for alternative subject
    def getExamSubjectLetterGradeCount(List classExamList, def studentList, String alterSubjectIds, String lGrade) {
        def alterSubjectList = subjectService.allSubjectInList(alterSubjectIds)
        return ExamMark.countByExamInListAndStudentInListAndSubjectInListAndActiveStatusAndLGrade(classExamList, studentList, alterSubjectList, ActiveStatus.ACTIVE, lGrade)
    }


    Long getInputMarksCount(ExamSchedule examSchedule, ExamType examType) {
        def c = ExamMark.createCriteria()
        def results = c.list() {
            createAlias('student', 'std')
            and {
                if (examType == ExamType.CLASS_TEST) {
                    'ne'("ctAttendStatus", AttendStatus.NO_INPUT )
                } else {
                    'ne'("hallAttendStatus", AttendStatus.NO_INPUT )
                }
                eq("activeStatus", ActiveStatus.ACTIVE )
                eq("std.studentStatus", StudentStatus.NEW)
                eq("examSchedule", examSchedule )
            }
            projections {
                count()
            }
        }
        return results[0]
    }
    /*Double getHighestExamMark(ExamSchedule examSchedule, SubjectName subject) {
        def maxNumber = ExamMark.createCriteria().get {
            createAlias('student', 'std')
            and {
                eq("activeStatus", ActiveStatus.ACTIVE )
                eq("std.studentStatus", StudentStatus.NEW)
                eq("examSchedule", examSchedule )
                eq("subject", subject )
            }
            projections {
                max "tabulationMark"
            }
        } as Double
        return maxNumber
    }*/
    Double getHighestClassExamMark(List examIds, ExamTerm examTerm, SubjectName subjectName, Boolean isSbaSubject = false) {
        String runningSchool = schoolService.runningSchool()
        def highestMark = ExamMark.createCriteria().get {
            createAlias('student', 'std')
            createAlias('exam', 'xm')
            and {
                'in'('xm.id',examIds)
                eq("activeStatus", ActiveStatus.ACTIVE )
                eq("std.studentStatus", StudentStatus.NEW)
                eq("subject", subjectName )
            }
            projections {
                if (runningSchool == CommonUtils.NARAYANGANJ_IDEAL_SCHOOL && examTerm == ExamTerm.FINAL_TEST) {
                    max "averageMark"
                } else {
                    if (isSbaSubject) {
                        max "tabulationNosbaMark"
                    } else {
                        max "tabulationMark"
                    }
                }

            }
        } as Double
        return highestMark
    }



    Double getAverageClassExamMark(Long classNameId, List examIds, ExamTerm examTerm, SubjectName subjectName, Boolean isSbaSubject = false) {
        String runningSchool = schoolService.runningSchool()

        def totalCount = studentSubjectsService.subjectStudentCountByCls(classNameId, subjectName)
        if (!totalCount || totalCount == 0) return 0.0

        def totalMark = ExamMark.createCriteria().get {
            createAlias('student', 'std')
            createAlias('exam', 'xm')
            and {
                'in'('xm.id',examIds)
                eq("activeStatus", ActiveStatus.ACTIVE )
                eq("std.studentStatus", StudentStatus.NEW)
                eq("subject", subjectName )
            }
            projections {
                if (runningSchool == CommonUtils.NARAYANGANJ_IDEAL_SCHOOL && examTerm == ExamTerm.FINAL_TEST) {
                    sum "averageMark"
                } else {
                    if (isSbaSubject) {
                        sum "tabulationNosbaMark"
                    } else {
                        sum "tabulationMark"
                    }
                }
            }
        } as Double

        Double average = 0
        if (totalMark) {
            average = totalMark/totalCount
        }

        return average.round(2)
    }


    def getHallMarkEntryList(GrailsParameterMap params, Section section, ExamSchedule examSchedule,SubjectName subject) {
        int iDisplayStart = params.iDisplayStart ? params.getInt('iDisplayStart') : CommonUtils.DEFAULT_PAGINATION_START
        int iDisplayLength = params.iDisplayLength ? params.getInt('iDisplayLength') : CommonUtils.DEFAULT_PAGINATION_LENGTH
        String sSortDir = params.sSortDir_0 ? params.sSortDir_0 : CommonUtils.DEFAULT_PAGINATION_SORT_ORDER
        int iSortingCol = params.iSortCol_0 ? params.getInt('iSortCol_0') : 2
        //Search string, use or logic to all fields that required to include
        String sSearch = params.sSearch ? params.sSearch : null
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }
        Boolean isHallPractical = examSchedule.isHallPractical
        String sortColumn
        if (isHallPractical) {
            sortColumn = CommonUtils.getSortColumn(hallMarkWithPracSortColumns, iSortingCol)
        } else {
            sortColumn = CommonUtils.getSortColumn(hallMarkSortColumns, iSortingCol)
        }
        List dataReturns = new ArrayList()
        int totalCount =0
        def c = ExamMark.createCriteria()
        def results = c.list(max: iDisplayLength, offset: iDisplayStart) {
            createAlias('student', 'std')
            and {
                eq("activeStatus", ActiveStatus.ACTIVE )
                eq("examSchedule", examSchedule )
                eq("subject", subject )
                eq("std.studentStatus", StudentStatus.NEW )
                'ne'("hallAttendStatus", AttendStatus.NO_INPUT )
            }
            if (sSearch) {
                or {
                    ilike('std.name', sSearch)
                    ilike('std.studentID', sSearch)
                }
            }
            order(sortColumn, sSortDir)
        }
        totalCount = results.totalCount
        Student student

        if (totalCount > 0) {
            String hallMark
            String ctMark
            results.each { ExamMark examMark ->
                if(examMark.hallAttendStatus==AttendStatus.PRESENT){
                    hallMark =examMark.hallObtainMark
                } else {
                    hallMark = examMark.hallAttendStatus.value
                }
                if(examMark.ctAttendStatus==AttendStatus.PRESENT){
                    ctMark =examMark.ctObtainMark
                } else {
                    ctMark = examMark.ctAttendStatus.value
                }
                student = examMark.student
                if (isHallPractical) {
                    dataReturns.add([DT_RowId: examMark.id, stdId:student.id, 0:student.studentID, 1: student.name, 2:student.rollNo, 3:examMark.hallWrittenMark, 4: examMark.hallObjectiveMark, 5: examMark.hallPracticalMark,6: examMark.hallSbaMark,7: examMark.hallInput5, 8: ctMark, 9: hallMark, 10 :''])
                }
                else {
                    dataReturns.add([DT_RowId: examMark.id, stdId:student.id, 0:student.studentID, 1: student.name, 2:student.rollNo, 3:ctMark, 4: hallMark, 5: subject.name, 6:''])
                }
            }
        }
        return [totalCount:totalCount,results:dataReturns]
    }

    def markEntryStdIds(ExamSchedule examSchedule, ExamType examType){
        def c = ExamMark.createCriteria()
        def results = c.list() {
            createAlias('student', 'std')
            and {
                eq("activeStatus", ActiveStatus.ACTIVE )
                eq("std.studentStatus", StudentStatus.NEW)
                eq("examSchedule", examSchedule )
                if(examType == ExamType.CLASS_TEST){
                    'ne'("ctAttendStatus", AttendStatus.NO_INPUT )
                }
                if(examType == ExamType.HALL_TEST){
                    'ne'("hallAttendStatus", AttendStatus.NO_INPUT )
                }
            }
            projections {
                property('std.id')
            }
        }
        return results
    }


    def calculateSubjectMark(ClassSubjects classSubjects, Student student, ExamMark examMark, ClassName className, SubjectName subject) {
        Double ctMark =0
        Double ctExamEffMark = 0
        if (classSubjects.isCtExam) {
            Double ctExamMark = classSubjects.ctMark?:0
            Double ctEffMark = classSubjects.ctEffMark?:100
            Double ctObtainMark = examMark.ctObtainMark?:0
            if(ctEffMark ==100){
                ctMark = ctObtainMark
                ctExamEffMark = ctExamMark
            }else {
                ctMark = ctEffMark * ctObtainMark * 0.01
                ctExamEffMark = ctEffMark * ctExamMark * 0.01
            }
        }
        examMark.ctMark = ctMark.round(2)

        // now hall mark
        Double hallExamMark = classSubjects.hallMark?:0
        Double hallEffMark = classSubjects.hallEffMark?:100
        Double hallObtainMark = examMark.hallObtainMark?:0
        Double hallMark =0
        Double hallExamEffMark = 0
        if(hallEffMark ==100){
            hallMark = hallObtainMark
            hallExamEffMark = hallExamMark
        }else {
            hallMark = hallEffMark * hallObtainMark * 0.01
            hallExamEffMark = hallEffMark * hallExamMark * 0.01
        }
        examMark.hallMark = hallMark.round(2)

        //Full Mark
        Double fullMark = ctMark+hallMark
        Double optionalMark = 0
        examMark.fullMark = fullMark.round(2)


        Double totalEffExamMark = ctExamEffMark + hallExamEffMark

        //optional and tabulation mark calculation
        boolean isOptSubject = false
        Double tabulationMark = 0
        Double optionalSubsMark = 0
        Double tabulationEffPercentage = classSubjects.weightOnResult?:100
        if(tabulationEffPercentage ==100){
            tabulationMark = fullMark
        }else {
            tabulationMark =tabulationEffPercentage * fullMark * 0.01
        }
        if(className.allowOptionalSubject){
            if(classSubjects.subjectType != SubjectType.COMPULSORY){
                isOptSubject = studentSubjectsService.isOptional(student, subject)
                if(isOptSubject){
                    optionalSubsMark = totalEffExamMark * 0.4
                    if(fullMark > optionalSubsMark){
                        optionalMark = fullMark - optionalSubsMark
                    }else {
                        optionalMark = 0
                    }
                    if(tabulationEffPercentage ==100){
                        tabulationMark = optionalMark
                    }else {
                        tabulationMark =tabulationEffPercentage * optionalMark * 0.01
                    }
                    examMark.isOptional = true
                }
            }
        }
        examMark.tabulationMark = tabulationMark.round(2)
        //Grade point calculation
        def percentageMark = fullMark * 100 /totalEffExamMark
        GradePoint gradePoint =gradePointService.getByMark(percentageMark, className)
        if(gradePoint){
            examMark.gPoint = gradePoint.gPoint
            examMark.lGrade = gradePoint.lGrade.value
                if(gradePoint.gPoint>0){
                    examMark.resultStatus = ResultStatus.PASSED
                } else {
                    examMark.resultStatus = ResultStatus.FAILED
                }
        }
        return true
    }
    def calculateIdealSubjectMark(ClassSubjects classSubjects, Student student, ExamMark examMark, ClassName className, SubjectName subject) {
        Double ctMark =0
        Double ctExamEffMark = 0
        if (classSubjects.isCtExam) {
            Double ctExamMark = classSubjects.ctMark?:0
            Double ctEffMark = classSubjects.ctEffMark?:100
            Double ctObtainMark = examMark.ctObtainMark?:0
            if(ctEffMark ==100){
                ctMark = ctObtainMark
                ctExamEffMark = ctExamMark
            }else {
                ctMark = ctEffMark * ctObtainMark * 0.01
                ctExamEffMark = ctEffMark * ctExamMark * 0.01
            }
        }
        examMark.ctMark = ctMark.round(2)

        // now hall mark
        Double hallExamMark = classSubjects.hallMark?:0
        Double hallEffMark = classSubjects.hallEffMark?:100
        Double hallObtainMark = examMark.hallObtainMark?:0
        Double hallMark =0
        Double hallExamEffMark = 0
        if(hallEffMark ==100){
            hallMark = hallObtainMark
            hallExamEffMark = hallExamMark
        }else {
            hallMark = hallEffMark * hallObtainMark * 0.01
            hallExamEffMark = hallEffMark * hallExamMark * 0.01
        }
        examMark.hallMark = hallMark.round(2)

        //Full Mark
        Double fullMark = ctMark+hallMark
        Double optionalMark = 0
        examMark.fullMark = fullMark.round(2)


        Double totalEffExamMark = ctExamEffMark + hallExamEffMark

        //optional and tabulation mark calculation
        boolean isOptSubject = false
        Double tabulationMark = 0
        Double optionalSubsMark = 0
        Double tabulationEffPercentage = classSubjects.weightOnResult?:100

        if (classSubjects.isExtracurricular) {
                examMark.isExtraCurricular = true
            }

        def percentageMark = fullMark * 100 /totalEffExamMark
        GradePoint gradePoint =gradePointService.getByMark(percentageMark, className)
        //ignored optional subject for ideal school result
        if(tabulationEffPercentage ==100){
            tabulationMark = fullMark
        }else {
            tabulationMark =tabulationEffPercentage * fullMark * 0.01
        }
        //Grade point calculation
        if(gradePoint){
            examMark.gPoint = gradePoint.gPoint
            examMark.lGrade = gradePoint.lGrade.value
            if(gradePoint.gPoint>0){
                examMark.resultStatus = ResultStatus.PASSED
            } else {
                examMark.resultStatus = ResultStatus.FAILED
            }
        }

        examMark.tabulationMark = tabulationMark.round(2)

        return true
    }
    def calculateIdealFinalSubjectMark(ClassSubjects classSubjects, Student student, ExamMark examMark, ClassName className, SubjectName subject) {
        Double ctMark =0
        Double ctExamEffMark = 0
        if (classSubjects.isCtExam) {
            Double ctExamMark = classSubjects.ctMark?:0
            Double ctEffMark = classSubjects.ctEffMark?:100
            Double ctObtainMark = examMark.ctObtainMark?:0
            if(ctEffMark ==100){
                ctMark = ctObtainMark
                ctExamEffMark = ctExamMark
            }else {
                ctMark = ctEffMark * ctObtainMark * 0.01
                ctExamEffMark = ctEffMark * ctExamMark * 0.01
            }
        }
        examMark.ctMark = ctMark.round(2)

        // now hall mark
        Double hallExamMark = classSubjects.hallMark?:0
        Double hallEffMark = classSubjects.hallEffMark?:100
        Double hallObtainMark = examMark.hallObtainMark?:0
        Double hallMark =0
        Double hallExamEffMark = 0
        if(hallEffMark ==100){
            hallMark = hallObtainMark
            hallExamEffMark = hallExamMark
        }else {
            hallMark = hallEffMark * hallObtainMark * 0.01
            hallExamEffMark = hallEffMark * hallExamMark * 0.01
        }
        examMark.hallMark = hallMark.round(2)

        //Full Mark
        Double fullMark = ctMark+hallMark
        Double optionalMark = 0
        examMark.fullMark = fullMark.round(2)


        Double totalEffExamMark = ctExamEffMark + hallExamEffMark

        //optional and tabulation mark calculation
        boolean isOptSubject = false
        Double tabulationMark = 0
        Double optionalSubsMark = 0
        Double tabulationEffPercentage = classSubjects.weightOnResult?:100

        if (classSubjects.isExtracurricular) {
            examMark.isExtraCurricular = true
        }

        //ignored optional subject for ideal school result
        if(tabulationEffPercentage ==100){
            tabulationMark = fullMark
        } else {
            tabulationMark =tabulationEffPercentage * fullMark * 0.01
        }

        Double hFullMark = 0
        Double hTabulationMark = 0
        Double avgFullMark
        Double avgMark
        if (!classSubjects.isOtherActivity){
            ExamMark halfYearlyMark = ExamMark.findByStudentAndSubjectAndExamNotEqualAndActiveStatus(student, subject, examMark.exam, ActiveStatus.ACTIVE)
            if (halfYearlyMark) {
                hFullMark = halfYearlyMark.fullMark
                hTabulationMark = halfYearlyMark.tabulationMark
            }
            avgFullMark = (fullMark + hFullMark)/2
            avgMark = (tabulationMark + hTabulationMark)/2
        } else {
            avgFullMark = fullMark
            avgMark = tabulationMark
        }
        examMark.averageMark = avgMark
        examMark.halfYearlyMark = hTabulationMark


        def percentageMark = avgFullMark * 100 /totalEffExamMark
        GradePoint gradePoint =gradePointService.getByMark(percentageMark, className)

        //Grade point calculation
        if(gradePoint){
            examMark.gPoint = gradePoint.gPoint
            examMark.lGrade = gradePoint.lGrade.value
            if(gradePoint.gPoint>0){
                examMark.resultStatus = ResultStatus.PASSED
            } else {
                examMark.resultStatus = ResultStatus.FAILED
            }
        }

        examMark.tabulationMark = tabulationMark.round(2)

        return [hTabulationMark, avgMark]
    }
    def calculateIdealFinalGroupSubjectMark(ClassSubjects classSubjects, Student student, ExamMark examMark, ClassName className, SubjectName subject, Exam exam, SubjectName groupSubject, Double groupEffExmMark) {
        Double ctMark =0
        Double ctExamEffMark = 0
        if (classSubjects.isCtExam) {
            Double ctExamMark = classSubjects.ctMark?:0
            Double ctEffMark = classSubjects.ctEffMark?:100
            Double ctObtainMark = examMark.ctObtainMark?:0
            if(ctEffMark ==100){
                ctMark = ctObtainMark
                ctExamEffMark = ctExamMark
            }else {
                ctMark = ctEffMark * ctObtainMark * 0.01
                ctExamEffMark = ctEffMark * ctExamMark * 0.01
            }
        }
        examMark.ctMark = ctMark.round(2)

        // now hall mark
        Double hallExamMark = classSubjects.hallMark?:0
        Double hallEffMark = classSubjects.hallEffMark?:100
        Double hallObtainMark = examMark.hallObtainMark?:0
        Double hallMark =0
        Double hallExamEffMark = 0
        if(hallEffMark ==100){
            hallMark = hallObtainMark
            hallExamEffMark = hallExamMark
        }else {
            hallMark = hallEffMark * hallObtainMark * 0.01
            hallExamEffMark = hallEffMark * hallExamMark * 0.01
        }
        examMark.hallMark = hallMark.round(2)

        //Full Mark
        Double fullMark = ctMark+hallMark
        Double optionalMark = 0
        examMark.fullMark = fullMark.round(2)


        Double totalEffExamMark = ctExamEffMark + hallExamEffMark

        //optional and tabulation mark calculation
        Double tabulationMark = 0
        Double tabulationEffPercentage = classSubjects.weightOnResult?:100
        if(tabulationEffPercentage ==100){
            tabulationMark = fullMark
        }else {
            tabulationMark =tabulationEffPercentage * fullMark * 0.01
        }

        examMark.tabulationMark = tabulationMark.round(2)
        //Group Grade point calculation
        Double subFullMark = tabulationMark
        totalEffExamMark = totalEffExamMark + groupEffExmMark
        Double gTabulationMark = 0
        ExamMark groupSubMark = ExamMark.findByExamAndStudentAndSubjectAndActiveStatus(exam, student, groupSubject, ActiveStatus.ACTIVE)
        if (groupSubMark) {
            fullMark = fullMark + groupSubMark.fullMark
            gTabulationMark = groupSubMark.tabulationMark
        }



        Double hFullMark = 0
        Double hTabulationMark = 0
        ExamMark halfYearlyGroupMark = ExamMark.findByStudentAndSubjectAndExamNotEqualAndActiveStatus(student, groupSubject, examMark.exam, ActiveStatus.ACTIVE)
        if (halfYearlyGroupMark) {
            hFullMark = halfYearlyGroupMark.fullMark
            hTabulationMark = halfYearlyGroupMark.tabulationMark
        }
        Double subjectAvgMark = 0
        Double subjectHalfMark = 0
        ExamMark halfYearlyMark = ExamMark.findByStudentAndSubjectAndExamNotEqualAndActiveStatus(student, subject, examMark.exam, ActiveStatus.ACTIVE)
        if (halfYearlyMark) {
            subjectHalfMark = halfYearlyMark.tabulationMark
            hFullMark += halfYearlyMark.fullMark
            hTabulationMark += halfYearlyMark.tabulationMark
            subjectAvgMark = (subFullMark + subjectHalfMark)/2
        } else {
            subjectAvgMark = subFullMark
        }

        examMark.averageMark = subjectAvgMark
        examMark.halfYearlyMark = subjectHalfMark

        Double avgFullMark = (fullMark + hFullMark)/2
        Double avgMark = (gTabulationMark + tabulationMark + hTabulationMark)/2

        def percentageMark = avgFullMark * 100 /totalEffExamMark

        GradePoint gradePoint =gradePointService.getByMark(percentageMark, className)
        if(gradePoint){
            examMark.gPoint = gradePoint.gPoint
            examMark.lGrade = gradePoint.lGrade.value
            if(gradePoint.gPoint>0){
                examMark.resultStatus = ResultStatus.PASSED
            } else {
                examMark.resultStatus = ResultStatus.FAILED
            }
        }
        //logic for
        return [hTabulationMark, avgMark]
    }
    def calculateIdealGroupSubjectMark(ClassSubjects classSubjects, Student student, ExamMark examMark, ClassName className, SubjectName subject, Exam exam, SubjectName groupSubject, Double groupEffExmMark) {
        Double ctMark =0
        Double ctExamEffMark = 0
        if (classSubjects.isCtExam) {
            Double ctExamMark = classSubjects.ctMark?:0
            Double ctEffMark = classSubjects.ctEffMark?:100
            Double ctObtainMark = examMark.ctObtainMark?:0
            if(ctEffMark ==100){
                ctMark = ctObtainMark
                ctExamEffMark = ctExamMark
            }else {
                ctMark = ctEffMark * ctObtainMark * 0.01
                ctExamEffMark = ctEffMark * ctExamMark * 0.01
            }
        }
        examMark.ctMark = ctMark.round(2)

        // now hall mark
        Double hallExamMark = classSubjects.hallMark?:0
        Double hallEffMark = classSubjects.hallEffMark?:100
        Double hallObtainMark = examMark.hallObtainMark?:0
        Double hallMark =0
        Double hallExamEffMark = 0
        if(hallEffMark ==100){
            hallMark = hallObtainMark
            hallExamEffMark = hallExamMark
        }else {
            hallMark = hallEffMark * hallObtainMark * 0.01
            hallExamEffMark = hallEffMark * hallExamMark * 0.01
        }
        examMark.hallMark = hallMark.round(2)

        //Full Mark
        Double fullMark = ctMark+hallMark
        Double optionalMark = 0
        examMark.fullMark = fullMark.round(2)


        Double totalEffExamMark = ctExamEffMark + hallExamEffMark

        //optional and tabulation mark calculation
        Double tabulationMark = 0
        Double tabulationEffPercentage = classSubjects.weightOnResult?:100
        if(tabulationEffPercentage ==100){
            tabulationMark = fullMark
        }else {
            tabulationMark =tabulationEffPercentage * fullMark * 0.01
        }

        examMark.tabulationMark = tabulationMark.round(2)
        //Group Grade point calculation

        totalEffExamMark = totalEffExamMark + groupEffExmMark
        ExamMark groupSubMark = ExamMark.findByExamAndStudentAndSubjectAndActiveStatus(exam, student, groupSubject, ActiveStatus.ACTIVE)
        fullMark = fullMark + groupSubMark?.fullMark
        def percentageMark = fullMark * 100 /totalEffExamMark
        GradePoint gradePoint =gradePointService.getByMark(percentageMark, className)
        if(gradePoint){
            examMark.gPoint = gradePoint.gPoint
            examMark.lGrade = gradePoint.lGrade.value
            if(gradePoint.gPoint>0){
                examMark.resultStatus = ResultStatus.PASSED
            } else {
                examMark.resultStatus = ResultStatus.FAILED
            }
        }
        //logic for
        return true
    }

    def calculateNhsSubjectMark(ClassSubjects classSubjects, Student student, ExamMark examMark, ClassName className, SubjectName subject) {
        Double ctMark =0
        Double ctExamEffMark = 0
        if (classSubjects.isCtExam) {
            Double ctExamMark = classSubjects.ctMark?:0
            Double ctEffMark = classSubjects.ctEffMark?:100
            Double ctObtainMark = examMark.ctObtainMark?:0
            if(ctEffMark ==100){
                ctMark = ctObtainMark
                ctExamEffMark = ctExamMark
            }else {
                ctMark = ctEffMark * ctObtainMark * 0.01
                ctExamEffMark = ctEffMark * ctExamMark * 0.01
            }
        }
        examMark.ctMark = ctMark.round(2)

        // now hall mark
        Double hallExamMark = classSubjects.hallMark?:0

        Double hallEffMark = classSubjects.hallEffMark?:100
        Double hallObtainMark = examMark.hallObtainMark?:0

        Double hallSbaMark = 0
        Double hallSbaObtainMark = 0
        if (className.subjectSba && classSubjects.hallSbaMark > 0) {
            hallSbaMark = classSubjects.hallSbaMark?:0
            hallSbaObtainMark = examMark.hallSbaMark?:0
            hallExamMark = hallExamMark - hallSbaMark
            hallObtainMark = hallObtainMark - hallSbaObtainMark
        }

        Double hallMark =0
        Double hallExamEffMark = 0
        if(hallEffMark ==100){
            hallMark = hallObtainMark
            hallExamEffMark = hallExamMark
        }else {
            hallMark = hallEffMark * hallObtainMark * 0.01
            hallExamEffMark = hallEffMark * hallExamMark * 0.01
        }
        examMark.hallMark = hallMark.round(2)

        //Full Mark
        Double fullMark = ctMark+hallMark
        Double optionalMark = 0
        examMark.fullMark = fullMark.round(2)


        Double totalEffExamMark = ctExamEffMark + hallExamEffMark

        //optional and tabulation mark calculation
        boolean isOptSubject = false
        Double tabulationMark = 0
        Double tabulationNosbaMark = 0
        Double optionalSubsMark = 0
        Double tabulationEffPercentage = classSubjects.weightOnResult?:100

        if (classSubjects.isExtracurricular) {
            examMark.isExtraCurricular = true
        }

        def percentageMark = fullMark * 100 /totalEffExamMark
        GradePoint gradePoint
        if (classSubjects.passMark && classSubjects.passMark > 0) {
            if (percentageMark < classSubjects.passMark) {
                gradePoint = GradePoint.findByLGrade(LetterGrade.GRADE_F)
            } else {
                gradePoint = gradePointService.getByMark(percentageMark, className)
            }
        } else {
            gradePoint = gradePointService.getByMark(percentageMark, className)
        }

            if(classSubjects.subjectType != SubjectType.COMPULSORY){
                if (classSubjects.isExtracurricular){
                    if(tabulationEffPercentage ==100){
                        tabulationMark = fullMark + hallSbaObtainMark
                        tabulationNosbaMark = fullMark
                    }else {
                        tabulationMark =tabulationEffPercentage * (fullMark + hallSbaObtainMark) * 0.01
                        tabulationNosbaMark =tabulationEffPercentage * fullMark  * 0.01
                    }
                    if(gradePoint){
                        examMark.gPoint = gradePoint.gPoint
                        examMark.lGrade = gradePoint.lGrade.value
                        examMark.resultStatus = ResultStatus.PASSED
                    }
                } else {
                    isOptSubject = studentSubjectsService.isOptional(student, subject)
                    if(isOptSubject){
                        optionalSubsMark = totalEffExamMark * 0.4
                        if(fullMark> optionalSubsMark){
                            optionalMark = fullMark  - optionalSubsMark
                        }else {
                            optionalMark = 0
                        }
                        if(tabulationEffPercentage ==100){
                            tabulationMark = optionalMark
                            tabulationNosbaMark = optionalMark + hallSbaObtainMark
                        }else {
                            tabulationMark =tabulationEffPercentage * (optionalMark + hallSbaObtainMark) * 0.01
                            tabulationNosbaMark =tabulationEffPercentage * optionalMark * 0.01
                        }
                        examMark.isOptional = true
                        //Grade point calculation

                        if(gradePoint){
                            examMark.gPoint = gradePoint.gPoint
                            examMark.lGrade = gradePoint.lGrade.value
                            examMark.resultStatus = ResultStatus.PASSED
                        }
                    } else {
                        if(tabulationEffPercentage ==100){
                            tabulationMark = fullMark + hallSbaObtainMark
                            tabulationNosbaMark = fullMark
                        }else {
                            tabulationMark =tabulationEffPercentage * (fullMark + hallSbaObtainMark) * 0.01
                            tabulationNosbaMark =tabulationEffPercentage * fullMark  * 0.01
                        }
                        //Grade point calculation
                        if(gradePoint){
                            examMark.gPoint = gradePoint.gPoint
                            examMark.lGrade = gradePoint.lGrade.value
                            if(gradePoint.gPoint>0){
                                examMark.resultStatus = ResultStatus.PASSED
                            } else {
                                examMark.resultStatus = ResultStatus.FAILED
                            }
                        }
                    }
                }

            } else {
                if(tabulationEffPercentage ==100){
                    tabulationMark = fullMark + hallSbaObtainMark
                    tabulationNosbaMark = fullMark
                }else {
                    tabulationMark =tabulationEffPercentage * (fullMark + hallSbaObtainMark) * 0.01
                    tabulationNosbaMark =tabulationEffPercentage * fullMark  * 0.01
                }
                //Grade point calculation
                if(gradePoint){
                    examMark.gPoint = gradePoint.gPoint
                    examMark.lGrade = gradePoint.lGrade.value
                    if(gradePoint.gPoint>0){
                        examMark.resultStatus = ResultStatus.PASSED
                    } else {
                        examMark.resultStatus = ResultStatus.FAILED
                    }
                }
            }

        examMark.tabulationMark = tabulationMark.round(2)
        examMark.tabulationNosbaMark = tabulationNosbaMark.round(2)

        return true
    }

    def calculateNhsGroupSubjectMark(ClassSubjects classSubjects, Student student, ExamMark examMark, ClassName className, SubjectName subject, Exam exam, SubjectName groupSubject, Double groupEffExmMark) {
        Double ctMark =0
        Double ctExamEffMark = 0
        if (classSubjects.isCtExam) {
            Double ctExamMark = classSubjects.ctMark?:0
            Double ctEffMark = classSubjects.ctEffMark?:100
            Double ctObtainMark = examMark.ctObtainMark?:0
            if(ctEffMark ==100){
                ctMark = ctObtainMark
                ctExamEffMark = ctExamMark
            }else {
                ctMark = ctEffMark * ctObtainMark * 0.01
                ctExamEffMark = ctEffMark * ctExamMark * 0.01
            }
        }
        examMark.ctMark = ctMark.round(2)

        // now hall mark
        Double hallExamMark = classSubjects.hallMark?:0
        Double hallEffMark = classSubjects.hallEffMark?:100
        Double hallObtainMark = examMark.hallObtainMark?:0
        Double hallMark =0
        Double hallExamEffMark = 0

        Double hallSbaMark = 0
        Double hallSbaObtainMark = 0
        if (className.subjectSba && classSubjects.hallSbaMark > 0) {
            hallSbaMark = classSubjects.hallSbaMark?:0
            hallSbaObtainMark = examMark.hallSbaMark?:0
            hallExamMark = hallExamMark - hallSbaMark
            hallObtainMark = hallObtainMark - hallSbaObtainMark
        }

        if(hallEffMark ==100){
            hallMark = hallObtainMark
            hallExamEffMark = hallExamMark
        }else {
            hallMark = hallEffMark * hallObtainMark * 0.01
            hallExamEffMark = hallEffMark * hallExamMark * 0.01
        }
        examMark.hallMark = hallMark.round(2)

        //Full Mark
        Double fullMark = ctMark+hallMark
        Double optionalMark = 0
        examMark.fullMark = fullMark.round(2)


        Double totalEffExamMark = ctExamEffMark + hallExamEffMark

        //optional and tabulation mark calculation
        Double tabulationMark = 0
        Double tabulationNosbaMark = 0
        Double tabulationEffPercentage = classSubjects.weightOnResult?:100
        if(tabulationEffPercentage ==100){
            tabulationMark = fullMark + hallSbaObtainMark
            tabulationNosbaMark = fullMark
        }else {
            tabulationMark =tabulationEffPercentage * (fullMark + hallSbaObtainMark) * 0.01
            tabulationNosbaMark =tabulationEffPercentage * fullMark  * 0.01
        }

        examMark.tabulationMark = tabulationMark.round(2)
        examMark.tabulationNosbaMark = tabulationNosbaMark.round(2)
        //Group Grade point calculation

        totalEffExamMark = totalEffExamMark + groupEffExmMark
        ExamMark groupSubMark = ExamMark.findByExamAndStudentAndSubjectAndActiveStatus(exam, student, groupSubject, ActiveStatus.ACTIVE)
        if (groupSubMark) {
            fullMark = fullMark + groupSubMark.fullMark
        }

        def percentageMark = fullMark * 100 /totalEffExamMark
        GradePoint gradePoint =gradePointService.getByMark(percentageMark, className)
        if(gradePoint){
            examMark.gPoint = gradePoint.gPoint
            examMark.lGrade = gradePoint.lGrade.value
            if(gradePoint.gPoint>0){
                examMark.resultStatus = ResultStatus.PASSED
            } else {
                examMark.resultStatus = ResultStatus.FAILED
            }
        }
        //logic for
        return true
    }

    def calculateAdarshaSubjectMark(ClassSubjects classSubjects, Student student, ExamMark examMark, ClassName className, ExamSchedule examSchedule) {
        Double ctMark =0
        Double ctExamEffMark = 0
        if (classSubjects.isCtExam && examSchedule.ctExamDate) {
            Double ctExamMark = classSubjects.ctMark ?: 0
            Double ctEffMark = classSubjects.ctEffMark ?: 100
            Double ctObtainMark = examMark.ctObtainMark ?: 0
            if (ctEffMark == 100) {
                ctMark = ctObtainMark
                ctExamEffMark = ctExamMark
            } else {
                ctMark = ctEffMark * ctObtainMark * 0.01
                ctExamEffMark = ctEffMark * ctExamMark * 0.01
            }
        }
        examMark.ctMark = ctMark.round(2)

        // now hall mark
        Double hallExamMark = classSubjects.hallMark?:0
        Double hallEffMark = classSubjects.hallEffMark?:100
        Double hallObtainMark = examMark.hallObtainMark?:0
        Double hallMark =0
        Double hallExamEffMark = 0
        if(hallEffMark ==100){
            hallMark = hallObtainMark
            hallExamEffMark = hallExamMark
        }else {
            hallMark = hallEffMark * hallObtainMark * 0.01
            hallExamEffMark = hallEffMark * hallExamMark * 0.01
        }
        examMark.hallMark = hallMark.round(2)

        //Full Mark
        Double fullMark = ctMark+hallMark
        Double optionalMark = 0
        examMark.fullMark = fullMark.round(2)


        Double totalEffExamMark = ctExamEffMark + hallExamEffMark

        //optional and tabulation mark calculation
        boolean isOptSubject = false
        Double tabulationMark = 0
        Double optionalSubsMark = 0

        Double tabulationEffPercentage = classSubjects.weightOnResult?:100
        if(tabulationEffPercentage ==100){
            tabulationMark = fullMark
        }else {
            tabulationMark =tabulationEffPercentage * fullMark * 0.01
        }

        examMark.tabulationMark = tabulationMark.round(2)
        //Grade point calculation
        def percentageMark = fullMark * 100 /totalEffExamMark
        GradePoint gradePoint =gradePointService.getByMark(percentageMark, className)

        if (classSubjects.isExtracurricular) {
            examMark.isExtraCurricular = true
            if(gradePoint){
                examMark.gPoint = gradePoint.gPoint
                examMark.lGrade = gradePoint.lGrade.value
                examMark.resultStatus = ResultStatus.PASSED
            }
        } else {
            if(gradePoint){
                examMark.gPoint = gradePoint.gPoint
                examMark.lGrade = gradePoint.lGrade.value
                if(gradePoint.gPoint>0){
                    examMark.resultStatus = ResultStatus.PASSED
                } else {
                    examMark.resultStatus = ResultStatus.FAILED
                }
            }
        }


        return true
    }

    def calculateCtSubjectMark(ClassSubjects classSubjects, Student student, ExamMark examMark, ClassName className, SubjectName subject) {
        Double ctObtainMark = examMark.ctObtainMark?:0
        //Grade point calculation
        def percentageMark = (ctObtainMark * 100) /classSubjects.ctMark
        GradePoint gradePoint =gradePointService.getByMark(percentageMark, className)
        if(gradePoint){
            if(gradePoint.gPoint>0){
                examMark.ctResultStatus = ResultStatus.PASSED
            } else {
                examMark.ctResultStatus = ResultStatus.FAILED
            }
        }
        return true
    }
}