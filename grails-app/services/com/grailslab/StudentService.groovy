package com.grailslab

import com.grailslab.enums.*
import com.grailslab.gschoolcore.AcademicYear
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.settings.ClassName
import com.grailslab.settings.Exam
import com.grailslab.settings.ExamSchedule
import com.grailslab.settings.Section
import com.grailslab.settings.SubjectName
import com.grailslab.stmgmt.ExamMark
import com.grailslab.stmgmt.Registration
import com.grailslab.stmgmt.Student
import com.grailslab.stmgmt.TcAndDropOut
import grails.transaction.Transactional
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

class StudentService {
    static transactional = false
    def schoolService
    def registrationService
    def studentSubjectsService
    def grailsApplication
    def examService
    def tabulationService
    def sectionService

    static final String[] sortColumns = ['id', 'studentID', 'name', 'rollNo', 'reg.fathersName', 'reg.birthDate', 'reg.mobile', 'section']

    LinkedHashMap studentPaginateList(GrailsParameterMap params) {
        def workingYears = schoolService.workingYears()
        int iDisplayStart = params.iDisplayStart ? params.getInt('iDisplayStart') : CommonUtils.DEFAULT_PAGINATION_START
        int iDisplayLength = params.iDisplayLength ? params.getInt('iDisplayLength') : CommonUtils.DEFAULT_PAGINATION_LENGTH
       /* String sSortDir = params.sSortDir_0 ? params.sSortDir_0 : CommonUtils.DEFAULT_PAGINATION_SORT_ORDER*/
        String sSortDir = params.sSortDir_0 ? params.sSortDir_0 : CommonUtils.SORT_ORDER_ASC
        int iSortingCol = params.iSortCol_0 ? params.getInt('iSortCol_0') : 2
        String sSearch = params.sSearch ? params.sSearch : null
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }

        Section section
        ClassName className
        Shift shift
        Long secId
        Long classId
        Long shiftId
        if(params.sectionName){
            secId = Long.parseLong(params.sectionName)
            section = Section.read(secId)
        }
        if(params.className){
            classId = Long.parseLong(params.className)
            className = ClassName.read(classId)
        }
        if(params.shift){
            shift=Shift.valueOf(params.shift)
        }
        AcademicYear academicYear
        if(params.academicYear){
            academicYear = AcademicYear.valueOf(params.academicYear)
        }

        String sortColumn = CommonUtils.getSortColumn(sortColumns, iSortingCol)
        List dataReturns = new ArrayList()
        def c = Student.createCriteria()
        def results = c.list(max: iDisplayLength, offset: iDisplayStart) {
            createAlias('section', 's')
            createAlias('registration', 'reg')
            and {
                if(academicYear){
                    eq("academicYear", academicYear)
                } else {
                    'in'("academicYear", workingYears)
                }
                eq("studentStatus", StudentStatus.NEW)
                if(section){
                    eq("section", section)
                }
                if(className){
                    eq("className", className)
                }
                if(shift){
                    eq("s.shift", shift)
                }
            }
            if (sSearch) {
                or {
                    ilike('studentID', sSearch)
                    ilike('name', sSearch)
                    ilike('reg.fathersName', sSearch)
                    ilike('reg.mobile', sSearch)
                    ilike('s.name', sSearch)
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
            results.each { Student student ->
                if (sSortDir.equals(CommonUtils.SORT_ORDER_ASC)) {
                    serial++
                } else {
                    serial--
                }
                dataReturns.add([DT_RowId: student.id, registrationId: student.registration?.id, 0: serial, 1: student.studentID,2: student.name, 3: student.rollNo, 4: student.registration?.fathersName, 5:CommonUtils.getUiDateStr(student?.registration?.birthDate), 6: student.registration?.mobile, 7: "${student.className.name} - ${student.section.name}", 8: ''])
            }
        }
        return [totalCount: totalCount, results: dataReturns]
    }

    def studentScholarshipList(GrailsParameterMap params) {
        def workingYears = schoolService.workingYears()
        AcademicYear academicYear
        if(params.academicYear){
            academicYear = AcademicYear.valueOf(params.academicYear)
        }

        def c = Student.createCriteria()
        def results = c.list() {
            and {
                if(academicYear){
                    eq("academicYear", academicYear)
                } else {
                    'in'("academicYear", workingYears)
                }
                eq("studentStatus", StudentStatus.NEW)
                eq("scholarshipObtain", Boolean.TRUE)
            }
            order("lastUpdated", CommonUtils.SORT_ORDER_DESC)
        }

        List dataReturns = new ArrayList()
        int serial = 0;
        results.each { Student student ->
            serial ++
          /*  dataReturns.add([id: student.id,serial:serial, name: "${student.studentID} - ${student.name} -[Roll: ${student.rollNo}]", secCls:" ${student.className.name} - ${student.section.name}", scholarship:student.scholarshipType?.value])*/
            dataReturns.add([DT_RowId: student.id, 0: serial, 1: student.studentID,2:student.name, 3: student.rollNo, 4: student.className.name, 5:student.section.name, 6:student.scholarshipType?.value, 7: ''])
        }
        return dataReturns
    }


    @Transactional
    Student savedSectionTransfer(Student newStudent, Section newSection, Section oldSection) {
        Student savedSt = newStudent.save(flush: true)
        if(savedSt){
            //Handle Update Exam Mark
            Exam nextSectionExam
            def examMarkList
            SubjectName subject
            ExamSchedule examSchedule
            def examList = examService.sectionExamList(oldSection)
            for (exam in examList) {
                nextSectionExam = Exam.findBySectionAndExamTermAndActiveStatus(newSection, exam.examTerm, ActiveStatus.ACTIVE)
                if (nextSectionExam) {
                    examMarkList = ExamMark.findAllByExamAndStudentAndActiveStatus(exam, newStudent, ActiveStatus.ACTIVE)
                    for (examMark in examMarkList) {
                        subject = examMark.subject
                        examSchedule = ExamSchedule.findByExamAndSubjectAndActiveStatus(nextSectionExam, subject, ActiveStatus.ACTIVE)
                        if (examSchedule) {
                            examMark.exam = nextSectionExam
                            examMark.examSchedule = examSchedule
                            examMark.save()
                        }
                    }
                    def tabulation = tabulationService.getTabulation(exam, newStudent)

                    if (tabulation) {
                        tabulation.exam = nextSectionExam
                        tabulation.section = newSection
                    }
                }
            }
        }
        return savedSt
    }

    @Transactional
    boolean saveBatchPromotionByCls(AcademicYear academicYear, Student oldStudent, Section newSection, int rollNo) {
        int countRollNo
        if(oldStudent.promotionStatus != PromotionStatus.PROMOTED){
            new Student(name: oldStudent.name, studentID: oldStudent.studentID, registration: oldStudent.registration, section:newSection, className: newSection.className, rollNo: rollNo, academicYear: academicYear, admissionMonth: YearMonths.JANUARY).save()
            oldStudent.promotionStatus=PromotionStatus.PROMOTED
            oldStudent.save()
        }
        return true
    }


    def allStudentInList(List idList){
        if (!idList) {
            return null
        }
        def workingYears = schoolService.workingYears()
        List dataReturns = new ArrayList()
        def c = Student.createCriteria()
        def results = c.list() {
            and {
                'in'("academicYear", workingYears)
                eq("studentStatus", StudentStatus.NEW)
                'in'("id",idList)
            }
            order("rollNo", CommonUtils.SORT_ORDER_ASC)
        }
        results.each { Student student ->
            dataReturns.add([id: student.id, name: "${student.studentID} - ${student.name} - [Roll: ${student.rollNo}]"])
        }
        return dataReturns
    }
    def allStudent(Section section){
        List dataReturns = new ArrayList()
        AcademicYear academicYear = schoolService.workingYear(section?.className)
        def c = Student.createCriteria()
        def results = c.list() {
            and {
                eq("academicYear", academicYear)
                eq("studentStatus", StudentStatus.NEW)
                eq("section", section)
            }
            order("rollNo", CommonUtils.SORT_ORDER_ASC)
        }
        results.each { Student student ->
            dataReturns.add([id: student.id, name: "${student.studentID} - ${student.name} - [Roll: ${student.rollNo}]"])
        }
        return dataReturns
    }
    def allStudentIds(Section section){
        List dataReturns = new ArrayList()
        AcademicYear academicYear = schoolService.workingYear(section?.className)
        def c = Student.createCriteria()
        def results = c.list() {
            and {
                eq("academicYear", academicYear)
                eq("studentStatus", StudentStatus.NEW)
                eq("section", section)
            }
            projections {
                property('id')
            }
        }
        return results
    }
    def allStudentForTabulation(Section section){
        List dataReturns = new ArrayList()
        def c = Student.createCriteria()
        def results = c.list() {
            and {
                eq("studentStatus", StudentStatus.NEW)
                eq("section", section)
            }
        }
        return results
    }
    def previousClasses(String stdID, Student currStd){
        def c = Student.createCriteria()
        def results = c.list() {
            createAlias("className","cName")
            and {
                eq("studentID", stdID)
                'ne'("studentStatus", StudentStatus.DELETED)
                'ne'("id", currStd.id)
            }
            order("cName.sortPosition", CommonUtils.SORT_ORDER_DESC)
        }

        return results
    }
    def numberOfStudent(Section section, AcademicYear academicYear =null){
        if(!academicYear){
            academicYear = schoolService.workingYear(section?.className)
        }
        return Student.countByAcademicYearAndStudentStatusAndSection(academicYear, StudentStatus.NEW, section)
    }
    def numberOfTotalStudent(AcademicYear academicYear =null){
        if(!academicYear){
            academicYear = schoolService.schoolWorkingYear()
        }
        return Student.countByAcademicYearAndStudentStatus(academicYear, StudentStatus.NEW)
    }
    def numberOfTcStudent(AcademicYear academicYear =null){
        if(!academicYear){
            academicYear = schoolService.schoolWorkingYear()
        }
        return Student.countByAcademicYearAndStudentStatus(academicYear, StudentStatus.TC)
    }

    int numberOfClassStudent(ClassName className, GroupName groupName = null, AcademicYear academicYear =null){
        if(!academicYear){
            academicYear = schoolService.workingYear(className)
        }
        if (groupName) {
            def groupSectionList = sectionService.groupSections(className, groupName, academicYear)
            return Student.countByAcademicYearAndStudentStatusAndActiveStatusAndClassNameAndSectionInList(academicYear, StudentStatus.NEW, ActiveStatus.ACTIVE, className, groupSectionList)
        } else {
            return Student.countByAcademicYearAndStudentStatusAndActiveStatusAndClassName(academicYear, StudentStatus.NEW, ActiveStatus.ACTIVE, className)
        }
    }

    def listStudentByRollForPromotion(ClassName className, AcademicYear academicYear, GroupName groupName, int offset, int limit){
        def groupSectionList
        if (groupName){
            groupSectionList = sectionService.groupSections(className, groupName, academicYear)
        }

        def c = Student.createCriteria()
        def results = c.list(max: limit, offset: offset) {
            and {
                eq("className", className)
                eq("academicYear", academicYear)
                eq("studentStatus", StudentStatus.NEW)
                if (groupName) {
                    'in'("section", groupSectionList)
                }
            }
            order("rollNo", CommonUtils.SORT_ORDER_ASC)
        }
        return results
    }

    //Number of class student for promotion. i.e: promotion status = NEW
    int numberOfClassStudentForPromotion(ClassName className, AcademicYear academicYear, GroupName groupName = null){
        if(!academicYear){
            academicYear = schoolService.workingYear(className)
        }
        if (groupName) {
            def groupSectionList = sectionService.groupSections(className, groupName, academicYear)
            return Student.countByAcademicYearAndStudentStatusAndActiveStatusAndPromotionStatusAndClassNameAndSectionInList(academicYear, StudentStatus.NEW, ActiveStatus.ACTIVE, PromotionStatus.NEW, className, groupSectionList)
        } else {
            return Student.countByAcademicYearAndStudentStatusAndActiveStatusAndPromotionStatusAndClassName(academicYear, StudentStatus.NEW, ActiveStatus.ACTIVE, PromotionStatus.NEW, className)
        }
    }
    def listStudent(AcademicYear academicYear, ClassName className, Section section, Gender gender, Religion religion){
        def c = Student.createCriteria()
        def results = c.list(max: 300, offset: 0) {
            createAlias('registration', 'reg')
            and {
                eq("academicYear", academicYear)
                eq("studentStatus", StudentStatus.NEW)
                eq("className", className)
                if (section) {
                    eq("section", section)
                }
                if (gender) {
                    eq("reg.gender", gender)
                }
                if (religion) {
                    eq("reg.religion", religion)
                }
            }
            order("rollNo", CommonUtils.SORT_ORDER_ASC)
        }
        return results
    }


    def studentIdsByGender(AcademicYear academicYear, List sectionList, Religion religion, Gender paramGender, Gender analysisGender){
        def c = Student.createCriteria()
        def results = c.list() {
            createAlias('registration', 'reg')
            and {
                eq("academicYear", academicYear)
                eq("studentStatus", StudentStatus.NEW)
                if (sectionList) {
                    'in'("section", sectionList)
                }
                if (religion) {
                    eq("reg.religion", religion)
                }
                if (paramGender) {
                    eq("reg.gender", paramGender)
                } else {
                    eq("reg.gender", analysisGender)
                }
            }

        }
        return results
    }

    def studentIdsByReligion(AcademicYear academicYear, List sectionList, Gender gender, Religion paramReligion, Religion analysisReligion){
        def c = Student.createCriteria()
        def results = c.list() {
            createAlias('registration', 'reg')
            and {
                eq("academicYear", academicYear)
                eq("studentStatus", StudentStatus.NEW)
                if (sectionList) {
                    'in'("section", sectionList)
                }
                if (gender) {
                    eq("reg.gender", gender)
                }
                if (paramReligion) {
                    eq("reg.religion", paramReligion)
                } else {
                    eq("reg.religion", analysisReligion)
                }
            }
        }
        return results
    }

    def listStudentForAnalysis(AcademicYear academicYear, List sectionList, Gender gender, Religion religion){
        def c = Student.createCriteria()
        def results = c.list() {
            createAlias('registration', 'reg')
            and {
                eq("academicYear", academicYear)
                eq("studentStatus", StudentStatus.NEW)
                if (sectionList) {
                    'in'("section", sectionList)
                }
                if (gender) {
                    eq("reg.gender", gender)
                }
                if (religion) {
                    eq("reg.religion", religion)
                }

            }
        }
        return results
    }

    def studentListBySection(AcademicYear academicYear, Section section, Gender gender, Religion religion){
        def c = Student.createCriteria()
        def results = c.list() {
            createAlias('registration', 'reg')
            and {
                eq("academicYear", academicYear)
                eq("studentStatus", StudentStatus.NEW)
                eq("section", section)
                if (gender) {
                    eq("reg.gender", gender)
                }
                if (religion) {
                    eq("reg.religion", religion)
                }
            }

        }
        return results
    }

    int numberOfGirlsStudent(ClassName className, AcademicYear academicYear =null){
        if(!academicYear){
            academicYear = schoolService.workingYear(className)
        }
        def c = Student.createCriteria()
        def count = c.list() {
            createAlias('registration', 'reg')
            and {
                eq("academicYear", academicYear)
                eq("className", className)
                eq("studentStatus", StudentStatus.NEW)
                eq("reg.gender", Gender.FEMALE)
                eq("reg.activeStatus", ActiveStatus.ACTIVE)
            }
            projections {
                count()
            }
        }
        return count[0]
    }
    def sectionStudentDDList(Section section){
        List dataReturns = new ArrayList()
        def c = Student.createCriteria()
        def results = c.list() {
            and {
                eq("studentStatus", StudentStatus.NEW)
                eq("section", section)
            }
            order("rollNo", CommonUtils.SORT_ORDER_ASC)
        }
        results.each { Student student ->
            dataReturns.add([id: student.id, name: "${student.studentID} - ${student.name} - [Roll: ${student.rollNo}]"])
        }
        return dataReturns
    }


    def allStudentTypeAheadWithRollSectionDD(String sSearch, AcademicYear academicYear = null){
        List dataReturns = new ArrayList()
        def workingYears = schoolService.workingYears()
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }
        def c = Student.createCriteria()
        def results = c.list() {
            createAlias('className', 'cls')
            createAlias('section', 's')
            createAlias('registration', 'reg')
            and {
                if(academicYear){
                    eq("academicYear", academicYear)
                } else {
                    'in'("academicYear", workingYears)
                }
                eq("studentStatus", StudentStatus.NEW)
            }
            if (sSearch) {
                or {
                    ilike('studentID', sSearch)
                    ilike('name', sSearch)
                    ilike('reg.mobile', sSearch)
                }
            }
            order("cls.sortPosition",CommonUtils.SORT_ORDER_ASC)
            order("s.name",CommonUtils.SORT_ORDER_ASC)
            order("rollNo",CommonUtils.SORT_ORDER_ASC)
        }
        results.each { Student student ->
            dataReturns.add([id: student.id, name: "${student.studentID} - ${student.name} - [${student.rollNo}] - ${student.section.name} - ${student.className.name} - ${student.registration.mobile}"])
        }
        return dataReturns
    }

    //So all student by year
    def allStudentTypeAheadListByStdId(String sSearch, AcademicYear academicYear = null){
        List dataReturns = new ArrayList()
        def workingYears = schoolService.workingYears()
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }
        def c = Student.createCriteria()
        def results = c.list() {
            createAlias('className', 'cls')
            createAlias('section', 's')
            createAlias('registration', 'reg')
            and {
                if(academicYear){
                    eq("academicYear", academicYear)
                } else {
                    'in'("academicYear", workingYears)
                }
                eq("studentStatus", StudentStatus.NEW)
            }
            if (sSearch) {
                or {
                    ilike('studentID', sSearch)
                    ilike('name', sSearch)
                    ilike('reg.mobile', sSearch)
                }
            }
            order("cls.sortPosition",CommonUtils.SORT_ORDER_ASC)
            order("s.name",CommonUtils.SORT_ORDER_ASC)
            order("rollNo",CommonUtils.SORT_ORDER_ASC)
        }
        results.each { Student student ->
            dataReturns.add([id: student.studentID, name: "${student.studentID} - ${student.name} - [${student.rollNo}] - ${student.section.name} - ${student.className.name} - ${student.registration.mobile}"])
        }
        return dataReturns
    }

    def sectionStudentIdsList(Section section){
        def c = Student.createCriteria()
        def results = c.list() {
            and {
                eq("studentStatus", StudentStatus.NEW)
                eq("section", section)
            }
            projections {
                property('id')
            }
            order("rollNo", CommonUtils.SORT_ORDER_ASC)
        }
        return results
    }

    def allStudentNotInList(Section section, List idList){
        AcademicYear academicYear = schoolService.workingYear(section?.className)
        List dataReturns = new ArrayList()
        def c = Student.createCriteria()
        def results = c.list() {
            and {
                eq("academicYear", academicYear)
                eq("studentStatus", StudentStatus.NEW)
                eq("section", section)

                if(idList){
                    not {'in'("id",idList)}
                }
            }
            order("rollNo", CommonUtils.SORT_ORDER_ASC)
        }
        results.each { Student student ->
            dataReturns.add([id: student.id, name: "${student.studentID} - ${student.name} - [Roll: ${student.rollNo}]"])
        }
        return dataReturns
    }

    //re admission logic
    def classStudentsForReAdmission(ClassName className, AcademicYear academicYear){
        List dataReturns = new ArrayList()
        def c = Student.createCriteria()
        def results = c.list() {
            and {
                eq("academicYear", academicYear)
                eq("className", className)
                eq("studentStatus", StudentStatus.NEW)
                eq("promotionStatus", PromotionStatus.NEW)
            }
            order("rollNo", CommonUtils.SORT_ORDER_ASC)
        }
        results.each { Student student ->
            dataReturns.add([id: student.id, name: "${student.studentID} - ${student.name} - [Roll: ${student.rollNo}]"])
        }
        return dataReturns
    }

    def classStudentsList(ClassName className, AcademicYear academicYear = null){
        List dataReturns = new ArrayList()
        if (!academicYear) {
            academicYear = schoolService.workingYear(className)
        }

        def c = Student.createCriteria()
        def results = c.list() {
            and {
                eq("academicYear", academicYear)
                eq("className", className)
                eq("studentStatus", StudentStatus.NEW)
            }
            order("rollNo", CommonUtils.SORT_ORDER_ASC)
        }
        results.each { Student student ->
            dataReturns.add([id: student.id, name: "${student.studentID} - ${student.name} - [Section: ${student.section?.name}, Roll: ${student.rollNo}]"])
        }
        return dataReturns
    }

    def classSectionStudentList(ClassName className, Section section, AcademicYear academicYear = null){
        List dataReturns = new ArrayList()
        if (!academicYear) {
            academicYear = schoolService.workingYear(className)
        }
        def c = Student.createCriteria()
        def results = c.list() {
            createAlias('section', 's')
            and {
                eq("academicYear", academicYear)
                eq("className", className)
                if (section){
                    eq("section", section)
                }
                eq("studentStatus", StudentStatus.NEW)
            }
            order("s.name",CommonUtils.SORT_ORDER_ASC)
            order("rollNo",CommonUtils.SORT_ORDER_ASC)
        }

        return results
    }
    def classStudentList(ClassName className, AcademicYear academicYear = null){
        List dataReturns = new ArrayList()
        if (!academicYear) {
            academicYear = schoolService.workingYear(className)
        }
        def c = Student.createCriteria()
        def results = c.list() {
            and {
                eq("academicYear", academicYear)
                eq("className", className)
                eq("studentStatus", StudentStatus.NEW)
            }
        }

        return results
    }

    def byClassName(ClassName className, AcademicYear academicYear, GroupName groupName = null){
        def c = Student.createCriteria()
        def results = c.list() {
            if (groupName){
                createAlias('section', 'se')
            }
            and {
                eq("academicYear", academicYear)
                eq("className", className)
                if (groupName){
                    eq("se.groupName", groupName)
                }
                eq("studentStatus", StudentStatus.NEW)
            }
            order("section", CommonUtils.SORT_ORDER_ASC)
            order("rollNo", CommonUtils.SORT_ORDER_ASC)
        }
        return results
    }
//    @todo-aminul remove this
    def allStudentDDListTypeAheadForMessaging(GrailsParameterMap parameterMap){
        String sSearch = parameterMap.q
        List dataReturns = new ArrayList()
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }
        def workingYears = schoolService.workingYears()
        def c = Student.createCriteria()
        def results = c.list() {
            and {
                'in'("academicYear", workingYears)
                eq("studentStatus", StudentStatus.NEW)
            }
        }
        results.each { Student student ->
            dataReturns.add([id: student.id, name: "${student.studentID} - ${student.name} - ${student.section.name} - ${student.registration.mobile}"])
        }
        return dataReturns
    }
    def allStudentDDListForMessaging(){
        List dataReturns = new ArrayList()
        def workingYears = schoolService.workingYears()
        def c = Student.createCriteria()
        def results = c.list() {
            and {
                'in'("academicYear", workingYears)
                eq("studentStatus", StudentStatus.NEW)
            }
        }
        results.each { Student student ->
            dataReturns.add([id: student.id, name: "${student.studentID} - ${student.name} - ${student.section.name} - ${student.registration.mobile}"])
        }
        return dataReturns
    }
    def step2StudentListForMessage(List<Long> selectionIds, SelectionTypes selectionType=null){
        String sSearch = "01" + CommonUtils.PERCENTAGE_SIGN
        def c = Student.createCriteria()
        def results = c.list() {
            createAlias('section', 'se')
            createAlias('registration', 'reg')
            and {
                eq("studentStatus", StudentStatus.NEW)
                ilike('reg.mobile', sSearch)
                if (selectionType == SelectionTypes.BY_STUDENT) {
                    'in'("id", selectionIds)
                }else {
                    'in'("se.id", selectionIds)
                }
            }
            order("rollNo", CommonUtils.SORT_ORDER_ASC)
        }
        List dataReturns = new ArrayList()
        Registration registration
        String mobileNo
        results.each { Student student ->
            registration = student.registration
            mobileNo = registration.mobile
            if(mobileNo && mobileNo.size()==11){
                dataReturns.add([id: student.id, name: student.studentID + ' - ' + student.name, mobile: registration.mobile])
            }
        }
        return dataReturns
    }


    //manage TC and DROP OUT cases
    static final String[] tcOrDropDownSortColumns = ['id', 'std.studentID', 'std.name']
    LinkedHashMap tcOrDropOutPaginateList(GrailsParameterMap params) {
        def workingYears = schoolService.workingYears()
        int iDisplayStart = params.iDisplayStart ? params.getInt('iDisplayStart') : CommonUtils.DEFAULT_PAGINATION_START
        int iDisplayLength = params.iDisplayLength ? params.getInt('iDisplayLength') : CommonUtils.DEFAULT_PAGINATION_LENGTH
        String sSortDir = params.sSortDir_0 ? params.sSortDir_0 : CommonUtils.SORT_ORDER_DESC
        int iSortingCol = params.iSortCol_0 ? params.getInt('iSortCol_0') : CommonUtils.DEFAULT_PAGINATION_SORT_IDX
        String sSearch = params.sSearch ? params.sSearch : null
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }
        String sortColumn = CommonUtils.getSortColumn(tcOrDropDownSortColumns, iSortingCol)
        List dataReturns = new ArrayList()

        AcademicYear academicYear
        if(params.academicYear){
            academicYear = AcademicYear.valueOf(params.academicYear)
        }
        TcType tcType = null
        if(params.tcType){
            tcType = TcType.valueOf(params.tcType)
        }
        def c = TcAndDropOut.createCriteria()
        def results = c.list(max: iDisplayLength, offset: iDisplayStart) {
            createAlias("student", "std")
            and {
                if(academicYear){
                    eq("std.academicYear", academicYear)
                } else {
                    'in'("std.academicYear", workingYears)
                }
                eq("std.studentStatus", StudentStatus.TC)
                if(tcType){
                    eq("tcType", tcType)
                }
                eq("activeStatus", ActiveStatus.ACTIVE)
            }
            if (sSearch) {
                or {
                    ilike('std.studentID', sSearch)
                    ilike('std.name', sSearch)
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
            Student student
            results.each { TcAndDropOut dropOut ->
                if (sSortDir.equals(CommonUtils.SORT_ORDER_ASC)) {
                    serial++
                } else {
                    serial--
                }
                student = dropOut.student
                dataReturns.add([DT_RowId: dropOut.id, 0: serial, 1: student.studentID, 2: student.name, 3: student.section.name, 4:dropOut.tcType?.value, 5: CommonUtils.getUiDateStr(dropOut.releaseDate), 6: dropOut.reason, 7: ''])
            }
        }
        return [totalCount: totalCount, results: dataReturns]
    }

    //Fee Item not paid student List
    static final String[] feeItemNotPaidSortColumns = ['section','studentID', 'name', 'rollNo']
    LinkedHashMap feeItemNotPaidPaginateList(GrailsParameterMap params, List<Long> paidStdIds) {
        int iDisplayStart = params.iDisplayStart ? params.getInt('iDisplayStart') : CommonUtils.DEFAULT_PAGINATION_START
        int iDisplayLength = params.iDisplayLength ? params.getInt('iDisplayLength') : CommonUtils.DEFAULT_PAGINATION_LENGTH
        String sSortDir = params.sSortDir_0 ? params.sSortDir_0 : CommonUtils.SORT_ORDER_DESC
        int iSortingCol = params.iSortCol_0 ? params.getInt('iSortCol_0') : CommonUtils.DEFAULT_PAGINATION_SORT_IDX
        String sSearch = params.sSearch ? params.sSearch : null
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }
        String sortColumn = CommonUtils.getSortColumn(feeItemNotPaidSortColumns, iSortingCol)
        List dataReturns = new ArrayList()

        def workingYears = schoolService.workingYears()
        AcademicYear academicYear
        if(params.academicYear){
            academicYear = AcademicYear.valueOf(params.academicYear)
        }
        ClassName className = null
        if(params.className){
            className = ClassName.read(params.className.toLong())
        }
        def c = Student.createCriteria()
        def results = c.list(max: iDisplayLength, offset: iDisplayStart) {
            and {
                if(academicYear){
                    eq("academicYear", academicYear)
                } else {
                    'in'("academicYear", workingYears)
                }
                eq("studentStatus", StudentStatus.NEW)
                if(className){
                    eq("className", className)
                }
                if(paidStdIds){
                    not {'in'("id",paidStdIds)}
                }

            }
            if (sSearch) {
                or {
                    ilike('studentID', sSearch)
                    ilike('name', sSearch)
                }
            }
            order(sortColumn, sSortDir)
        }
        int totalCount = results.totalCount
        if (totalCount > 0) {
            results.each { Student student ->
                dataReturns.add([DT_RowId: student.id, 0: student.section.name, 1: student.studentID, 2: student.name, 3: student.rollNo, 4:'', 5: '', 6: '', 7: '', 8:''])
            }
        }
        return [totalCount: totalCount, results: dataReturns]
    }
    Integer nextRoll(Section section, AcademicYear academicYear = null){
        if(!academicYear){
            academicYear = schoolService.workingYear(section?.className)
        }
        int rollNumber =0
        def listStudent

        String schoolName = schoolService.runningSchool()
        if(schoolName=='nideal'){
            listStudent = Student.findAllByStudentStatusAndAcademicYearAndClassName(StudentStatus.NEW,academicYear,section.className,[sort: "rollNo",order:'desc',max: 1, offset: 0])
        } else {
            listStudent = Student.findAllByStudentStatusAndAcademicYearAndSection(StudentStatus.NEW,academicYear,section,[sort: "rollNo",order:'desc',max: 1, offset: 0])
        }
        if(listStudent){
            rollNumber = listStudent.last().rollNo
        }
        return rollNumber+1
    }

    def allAbsenceStudent(String sSearch,Section section, def idList){
        List dataReturns = new ArrayList()
        AcademicYear academicYear = schoolService.workingYear(section?.className)
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }
        def c = Student.createCriteria()
        def results = c.list() {
            createAlias('className', 'cls')
            createAlias('section', 's')
            createAlias('registration', 'reg')
            and {
                eq("academicYear", academicYear)
                eq("studentStatus", StudentStatus.NEW)
                eq("section", section)
                if(idList){
                    not {'in'("id",idList)}
                }
            }
            if (sSearch) {
                or {
                    ilike('name', sSearch)
                    ilike('reg.mobile', sSearch)
                    ilike('cls.name', sSearch)

                }
            }

            order("cls.sortPosition",CommonUtils.SORT_ORDER_ASC)
            order("s.name",CommonUtils.SORT_ORDER_ASC)
            order("rollNo",CommonUtils.SORT_ORDER_ASC)
        }
        results.each { Student student ->
            dataReturns.add([id: student.id, name: "${student.studentID} - ${student.name} - [${student.rollNo}] - ${student.section.name} - ${student.className.name} - ${student.registration.mobile}"])
        }
        return dataReturns
    }

    def allAbsenceStudent(String sSearch, def idList){
        List dataReturns = new ArrayList()
       if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }
        def c = Student.createCriteria()
        def results = c.list() {
            createAlias('className', 'cls')
            createAlias('section', 's')
            createAlias('registration', 'reg')
            and {
                eq("studentStatus", StudentStatus.NEW)
                if(idList){
                    not {'in'("id",idList)}
                }
            }
            if (sSearch) {
                or {
                    ilike('name', sSearch)
                    ilike('reg.mobile', sSearch)
                    ilike('cls.name', sSearch)
                    ilike('studentID', sSearch)

                }
            }

            order("cls.sortPosition",CommonUtils.SORT_ORDER_ASC)
            order("s.name",CommonUtils.SORT_ORDER_ASC)
            order("rollNo",CommonUtils.SORT_ORDER_ASC)
        }
        results.each { Student student ->
            dataReturns.add([id: student.id, name: "${student.studentID} - ${student.name} - [${student.rollNo}] - ${student.section.name} - ${student.className.name} - ${student.registration.mobile}"])
        }
        return dataReturns
    }
    Student findByStudentId(String studentId, AcademicYear academicYear = null){
        def workingYears
        if (!academicYear) {
            workingYears = schoolService.workingYears()
        }
        if (academicYear) {
            return Student.findByAcademicYearAndStudentStatusAndStudentID(academicYear, StudentStatus.NEW, studentId)
        }
        return Student.findByStudentStatusAndAcademicYearInListAndStudentID(StudentStatus.NEW, workingYears, studentId)
    }
}
