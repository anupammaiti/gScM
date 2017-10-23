package com.grailslab

import com.grailslab.enums.Shift
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.settings.ClassName
import com.grailslab.settings.LessonWeek
import com.grailslab.settings.Section
import com.grailslab.settings.SubjectName
import com.grailslab.stmgmt.LessonFeedback
import com.grailslab.stmgmt.LessonFeedbackAverage
import com.grailslab.stmgmt.LessonFeedbackDetail
import com.grailslab.stmgmt.Student
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap


class LessonFeedbackService {
    static transactional = false
    def lessonService

        static final String[] sortColumnsBook = ['id','fd.className','fd.section','fd.lessonWeek','st.studentID','st.name','st.rollNo','numOfSubject','average']
        LinkedHashMap feedbaclDetailList(GrailsParameterMap  params){
            int iDisplayStart = params.iDisplayStart ? params.getInt('iDisplayStart') : CommonUtils.DEFAULT_PAGINATION_START
            int iDisplayLength = params.iDisplayLength ? params.getInt('iDisplayLength') : CommonUtils.DEFAULT_PAGINATION_LENGTH
            String sSortDir = params.sSortDir_0 ? params.sSortDir_0 : CommonUtils.SORT_ORDER_DESC
            int iSortingCol = params.iSortCol_0 ? params.getInt('iSortCol_0') : CommonUtils.DEFAULT_PAGINATION_SORT_IDX
            String sSearch = params.sSearch ? params.sSearch : null
            if (sSearch) {
                sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
            }

            Section section
            ClassName className
            LessonWeek lessonWeek

            if(params.className){
                className = ClassName.read(params.getLong("className"))
            }
            if(params.sectionName){
                section = Section.read(params.getLong("sectionName"))
            }

            if(params.weekNo){
                Integer lessonWeekNo = params.getInt("weekNo")
                lessonWeek= lessonService.getWeekByWeekNo(lessonWeekNo, section?.academicYear)
            }
            String sortColumn = CommonUtils.getSortColumn(sortColumnsBook,iSortingCol)
            List dataReturns = new ArrayList()

            def c = LessonFeedbackAverage.createCriteria()
            def results = c.list(max: iDisplayLength, offset: iDisplayStart) {
                createAlias('feedback', 'fd')
                createAlias('student', 'st')
                and {
                    if (className) {
                        eq("fd.className", className)
                    }
                    if (section) {
                        eq("fd.section", section)
                    }
                    if (lessonWeek) {
                        eq("fd.lessonWeek", lessonWeek)
                    }
                }
                if (sSearch) {
                    or {
                        ilike('st.studentid', sSearch)
                        ilike('st.name', sSearch)
                    }
                }
                order(sortColumn, sSortDir)
            }
            int totalCount = results.totalCount
            int serial = iDisplayStart;
            if (totalCount > 0) {
                if (sSortDir.equals(CommonUtils.SORT_ORDER_DESC)) {
                    serial = (totalCount + 1) - iDisplayStart
                }
                LessonFeedback feedback
                Student student
                results.each { LessonFeedbackAverage lfa ->
                    if (sSortDir.equals(CommonUtils.SORT_ORDER_ASC)) {
                        serial++
                    } else {
                        serial--
                    }
                    feedback = lfa.feedback
                    student = lfa.student
                    dataReturns.add([DT_RowId: lfa.id, 0: serial, 1: student.className.name, 2:student.section.name, 3: feedback.lessonWeek.weekNumber, 4:student.studentID, 5: student.name, 6: student.rollNo, 7: lfa.numOfSubject, 8: lfa.average])
                }
            }
            return [totalCount: totalCount, results: dataReturns]
        }


    def addRatingScore(LessonFeedback lessonFeedback, Student student, SubjectName subjectName, Double rating, String comment) {
        LessonFeedbackDetail feedbackDetail = LessonFeedbackDetail.findByFeedbackAndStudentAndSubjectNameAndActiveStatus(lessonFeedback, student, subjectName, ActiveStatus.ACTIVE)
        if (feedbackDetail) {
            editAverage(lessonFeedback, student, feedbackDetail.rating, rating)
            feedbackDetail.rating = rating
            feedbackDetail.comment = comment
        } else {
            feedbackDetail = new LessonFeedbackDetail(feedback:lessonFeedback, student:student, subjectName: subjectName, rating:rating, comment:comment)
            addAverage(lessonFeedback, student, rating)
        }
        feedbackDetail.save()
    }
    def addAverage(LessonFeedback lessonFeedback, Student student, Double rating) {
        LessonFeedbackAverage feedbackAverage = LessonFeedbackAverage.findByFeedbackAndStudent(lessonFeedback, student)
        if (feedbackAverage){
            Double previousTotal = feedbackAverage.numOfSubject * feedbackAverage.average
            int numOfSubject = feedbackAverage.numOfSubject + 1
            feedbackAverage.average = (previousTotal+rating)/numOfSubject
            feedbackAverage.numOfSubject = numOfSubject
        } else {
            feedbackAverage = new LessonFeedbackAverage(feedback: lessonFeedback, student: student, numOfSubject: 1, average: rating)
        }
        feedbackAverage.save()
    }
    def editAverage(LessonFeedback lessonFeedback, Student student, Double oldRating, Double nowRating){
        LessonFeedbackAverage feedbackAverage = LessonFeedbackAverage.findByFeedbackAndStudent(lessonFeedback, student)
        if (feedbackAverage){
            Double previousTotal = feedbackAverage.numOfSubject * feedbackAverage.average
            Double nowTotal = (previousTotal + nowRating) - oldRating
            feedbackAverage.average = nowTotal/feedbackAverage.numOfSubject
            feedbackAverage.save()
        }
    }
    def getFeedbackDetail(LessonFeedback lessonFeedback, SubjectName subjectName){
        if (!lessonFeedback || !subjectName) return null
        def results = LessonFeedbackDetail.findAllByFeedbackAndSubjectNameAndActiveStatus(lessonFeedback, subjectName, ActiveStatus.ACTIVE)

        List dataReturns = new ArrayList()
        Student student
        results.each { obj ->
            student = obj.student
            dataReturns.add([id: student.id, stuId: student.studentID, stuName: student.name, stuRoll: student.rollNo, rating: obj.rating, comment: obj.comment])
        }
        return dataReturns
    }
}
