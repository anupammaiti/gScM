package com.grailslab

import com.grailslab.enums.ExamType
import com.grailslab.enums.GroupName
import com.grailslab.enums.SubjectType
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.settings.ClassName
import com.grailslab.settings.ClassSubjects
import com.grailslab.settings.Exam
import com.grailslab.settings.ExamSchedule
import com.grailslab.settings.Section
import com.grailslab.settings.SubjectName
import com.grailslab.viewz.StdClassSubjectView
import grails.transaction.Transactional
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

class ClassSubjectsService {
    static transactional = false
    def subjectService
    def examScheduleService

    @Transactional
    ClassSubjects saveMapping(ClassSubjects obj, Boolean isEdit){
        ClassSubjects savedObj = obj.save(flush: true)
        if (savedObj && (obj.subjectType == SubjectType.ALTERNATIVE)) {
            def alterSubList = subjectService.allSubjectInList(obj.alternativeSubIds)
            if (isEdit) {
                ClassSubjects ifDeletedBefore
                List<Long> alterSubListIds = alterSubList.collect {it.id}
                def preAddedSubList = ClassSubjects.findAllByParentSubIdAndSubjectType(savedObj.id, SubjectType.INUSE)
                for (addedInUse in preAddedSubList) {
                    if (!alterSubListIds.contains(addedInUse.subject.id)) {
                        if (addedInUse.activeStatus == ActiveStatus.ACTIVE){
                            addedInUse.activeStatus = ActiveStatus.DELETE
                            addedInUse.save()
                        }
                    } else {
                        addedInUse.isCtExam = obj.isCtExam
                        addedInUse.ctMark = obj.ctMark
                        addedInUse.ctEffMark = obj.ctEffMark
                        addedInUse.isHallExam = obj.isHallExam
                        addedInUse.hallMark = obj.hallMark
                        addedInUse.hallEffMark = obj.hallEffMark
                        addedInUse.isHallPractical = obj.isHallPractical
                        addedInUse.hallWrittenMark = obj.hallWrittenMark
                        addedInUse.hallPracticalMark = obj.hallPracticalMark
                        addedInUse.hallObjectiveMark = obj.hallObjectiveMark
                        addedInUse.hallSbaMark = obj.hallSbaMark
                        addedInUse.hallInput5 = obj.hallInput5
                        addedInUse.isPassSeparately = obj.isPassSeparately
                        addedInUse.writtenPassMark = obj.writtenPassMark
                        addedInUse.practicalPassMark = obj.practicalPassMark
                        addedInUse.objectivePassMark = obj.objectivePassMark
                        addedInUse.sbaPassMark = obj.sbaPassMark
                        addedInUse.input5PassMark = obj.input5PassMark
                        addedInUse.sortOrder = obj.sortOrder
                        addedInUse.weightOnResult = obj.weightOnResult
                        addedInUse.groupName = obj.groupName
                        addedInUse.isExtracurricular = obj.isExtracurricular
                        addedInUse.isOtherActivity = obj.isOtherActivity

                        addedInUse.save()
                    }
                }
                alterSubListIds = preAddedSubList.collect {it.subject?.id}
                for (newSubAdded in alterSubList) {
                    if (!alterSubListIds.contains(newSubAdded.id)) {
                        new ClassSubjects(subject:newSubAdded, groupName:obj.groupName, className:obj.className, subjectType: SubjectType.INUSE, parentSubId: savedObj.id,
                                fullMark: obj.fullMark, ctMark: obj.ctMark, ctEffMark: obj.ctEffMark, hallMark: obj.hallMark, hallEffMark: obj.hallEffMark,
                                weightOnResult: obj.weightOnResult,isCtExam: obj.isCtExam, isHallExam: obj.isHallExam, isExtracurricular: obj.isExtracurricular,
                                isOtherActivity: obj.isOtherActivity, isHallPractical: obj.isHallPractical, hallWrittenMark: obj.hallWrittenMark,
                                hallPracticalMark: obj.hallPracticalMark, hallObjectiveMark: obj.hallObjectiveMark,hallSbaMark: obj.hallSbaMark,hallInput5: obj.hallInput5,
                                sortOrder: obj.sortOrder, isPassSeparately: obj.isPassSeparately, writtenPassMark: obj.writtenPassMark, practicalPassMark: obj.practicalPassMark,
                                objectivePassMark: obj.objectivePassMark, sbaPassMark: obj.sbaPassMark, input5PassMark: obj.input5PassMark).save()
                    } else {
                        ifDeletedBefore = ClassSubjects.findBySubjectAndClassNameAndActiveStatusNotEqualAndParentSubId(newSubAdded, obj.className, ActiveStatus.ACTIVE, savedObj.id)
                        if (ifDeletedBefore) {
                            ifDeletedBefore.activeStatus = ActiveStatus.ACTIVE
                            ifDeletedBefore.save()
                        }
                    }
                }

                //handle edit case
            } else {
                for (subject in alterSubList) {
                    new ClassSubjects(subject:subject, groupName:obj.groupName, className:obj.className, subjectType: SubjectType.INUSE, parentSubId: savedObj.id,
                            fullMark: obj.fullMark, ctMark: obj.ctMark, ctEffMark: obj.ctEffMark, hallMark: obj.hallMark, hallEffMark: obj.hallEffMark,
                            weightOnResult: obj.weightOnResult, isCtExam: obj.isCtExam, isHallExam: obj.isHallExam, isExtracurricular: obj.isExtracurricular,
                            isOtherActivity: obj.isOtherActivity, isHallPractical: obj.isHallPractical, hallWrittenMark: obj.hallWrittenMark,
                            hallPracticalMark: obj.hallPracticalMark, hallObjectiveMark: obj.hallObjectiveMark, hallSbaMark: obj.hallSbaMark,hallInput5: obj.hallInput5,
                            sortOrder: obj.sortOrder, isPassSeparately: obj.isPassSeparately, writtenPassMark: obj.writtenPassMark, practicalPassMark: obj.practicalPassMark,
                            objectivePassMark: obj.objectivePassMark, sbaPassMark: obj.sbaPassMark, input5PassMark: obj.input5PassMark).save()
                }
            }
        }
        savedObj
    }

    static final String[] sortColumns = ['sortPosition','subjectName','subjectType']
    LinkedHashMap classSubjectsPaginateList(GrailsParameterMap params,ClassName className){
        int iDisplayStart = params.iDisplayStart ? params.getInt('iDisplayStart') : CommonUtils.DEFAULT_PAGINATION_START
        int iDisplayLength = params.iDisplayLength ? params.getInt('iDisplayLength') : CommonUtils.DEFAULT_PAGINATION_LENGTH
        String sSortDir = params.sSortDir_0 ? params.sSortDir_0 : CommonUtils.SORT_ORDER_ASC
        int iSortingCol = params.iSortCol_0 ? params.getInt('iSortCol_0') : CommonUtils.DEFAULT_PAGINATION_SORT_IDX
        String sSearch = params.sSearch ? params.sSearch : null
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }
        String sortColumn = CommonUtils.getSortColumn(sortColumns,iSortingCol)
        List dataReturns = new ArrayList()
        def c = StdClassSubjectView.createCriteria()
        def results = c.list(max: iDisplayLength, offset: iDisplayStart) {
            and {
                eq("classNameId",className.id)
                'ne'("subjectType",SubjectType.INUSE)
            }
            if (sSearch) {
                or {
                    ilike('subjectName', sSearch)
                }
            }
            order(sortColumn, sSortDir)
        }
        int totalCount = results.totalCount
        int serial = iDisplayStart;
        String subjectType
        String ctExam
        String hallExam
        String examMark
        String subjectName
        String passMark
        String sortOrder
        if (totalCount > 0) {
            if (sSortDir.equals(CommonUtils.SORT_ORDER_DESC)) {
                serial = (totalCount + 1) - iDisplayStart
            }
            results.each { StdClassSubjectView classSubjects ->
                ctExam = 'No Class Test'
                if (classSubjects.isCtExam) {
                    ctExam = "Written: "+classSubjects.ctMark
                }
                hallExam = 'No Hall Test'
                if (classSubjects.isHallExam) {
                    if (classSubjects.isHallPractical) {
                        hallExam = "Written: "+classSubjects.hallWrittenMark
                        if (classSubjects.hallObjectiveMark && classSubjects.hallObjectiveMark > 0){
                            hallExam += ", Objective: "+classSubjects.hallObjectiveMark
                        }
                        if (classSubjects.hallPracticalMark && classSubjects.hallPracticalMark > 0){
                            hallExam += ", Practical: "+classSubjects.hallPracticalMark
                        }
                        if (classSubjects.hallSbaMark && classSubjects.hallSbaMark > 0){
                            hallExam += ", SBA: "+classSubjects.hallSbaMark
                        }

                        if (classSubjects.hallInput5 && classSubjects.hallInput5 > 0){
                            hallExam += ", Input5: "+classSubjects.hallInput5
                        }

                    } else {
                        hallExam = "Written: "+classSubjects.hallMark
                    }
                }
                if(classSubjects.isOtherActivity){
                    examMark="N/A"
                }else if (classSubjects.hallEffMark.equals(100)){
                    examMark=classSubjects?.isExtracurricular?'Extra Curricular, Mark '+classSubjects.hallMark:'CT Mark '+classSubjects.ctMark+', Hall Mark '+classSubjects.hallMark
                }else{
                    examMark=classSubjects?.isExtracurricular?'Extra Curricular, Mark '+classSubjects.hallMark:'CT Mark (Effective '+classSubjects.ctEffMark+'%) '+classSubjects.ctMark+', Hall Mark (Effective '+classSubjects.hallEffMark +'%) '+classSubjects.hallMark
                }
                if (sSortDir.equals(CommonUtils.SORT_ORDER_ASC)) {
                    serial++
                } else {
                    serial--
                }
                subjectName = classSubjects.subjectName
                if (classSubjects.subjectType == SubjectType.ALTERNATIVE) {
                    subjectName += " [ "+classSubjects.alternativeSubNames+" ]"
                }
                subjectType = classSubjects.subjectType?.value
                if(classSubjects.groupName){
                    subjectType += " [ "+ classSubjects.groupName +" ]"
                }
                // passMark = classSubjects.passMark? classSubjects.passMark +' %': ""

                dataReturns.add([DT_RowId: classSubjects.id, 0: serial, 1: subjectName, 2: subjectType,3:ctExam, 4: hallExam, 5: examMark, 6: classSubjects.weightOnResult +' %', 7: ''])


            }

        }
        return [totalCount:totalCount,results:dataReturns]
    }

    def classSubjectsList(ClassName className){
        String sSortDir = CommonUtils.SORT_ORDER_ASC
        String sortColumn = 'sortPosition'
        def c = StdClassSubjectView.createCriteria()
        def results = c.list() {
            and {
                eq("classNameId",className.id)
                'ne'("subjectType",SubjectType.INUSE)
            }
            order(sortColumn, sSortDir)
        }
        return results
    }

    //refactored
    def subjectsIdListForClassSubject(ClassName className, GroupName groupName = null) {
        def c = StdClassSubjectView.createCriteria()
        def results = c.list() {
            eq("classNameId", className.id)
            'ne'("subjectType",SubjectType.INUSE)
            if(groupName){
                or {
                    and {
                        isNull("groupName")
                    }
                    and {
                        eq("groupName", groupName.key)
                    }
                }
            }
            projections {
                property('subjectId')
            }
            order("sortPosition", CommonUtils.SORT_ORDER_ASC)
        }
        return results
    }

    def availableSubjectsToAddClass(ClassName className, GroupName groupName = null) {
        def addedSubjectIdList
        def subjectList
        if (className.groupsAvailable) {
            addedSubjectIdList =  subjectsIdListForClassSubject(className, groupName)
            if(addedSubjectIdList && addedSubjectIdList.size>0){
                subjectList = subjectService.allMainSubjectNotInListDropDown(addedSubjectIdList)
            }else {
                subjectList = subjectService.allMainSubjectDropDownList()
            }
        } else {
            addedSubjectIdList =  subjectsIdListForClassSubject(className)
            if(addedSubjectIdList && addedSubjectIdList.size>0){
                subjectList = subjectService.allMainSubjectNotInListDropDown(addedSubjectIdList)
            }else {
                subjectList = subjectService.allMainSubjectDropDownList()
            }
        }
        subjectList
    }

    //refactor done
    def subjectTypeCompulsoryListStr(ClassName className, GroupName groupName=null) {
        def c = StdClassSubjectView.createCriteria()
        def results = c.list() {
            if(groupName){
                eq("classNameId", className.id)
                or {
                    and {
                        eq("subjectType", SubjectType.COMPULSORY)
                        isNull("groupName")
                    }
                    and {
                        eq("subjectType", SubjectType.COMPULSORY)
                        eq("groupName", groupName.key)
                    }
                }
            } else {
                and {
                    eq("classNameId", className.id)
                    eq("subjectType", SubjectType.COMPULSORY)
                }
            }
            projections {
                property 'subjectName', 'name'
            }
            order("sortPosition", CommonUtils.SORT_ORDER_ASC)
        }
        results?.join(', ')
    }
    //refactor done
    def subjectChooseOptComDDList(ClassName className, GroupName groupName=null) {
        List dataReturns = new ArrayList()
        def c = StdClassSubjectView.createCriteria()
        def results = c.list() {
            eq("classNameId", className.id)
            'in'("subjectType", SubjectType.chooseOptCom() as List)
            if(groupName){
                or {
                    and {
                        isNull("groupName")
                    }
                    and {
                        eq("groupName", groupName.key)
                    }
                }
            }
            order("sortPosition", CommonUtils.SORT_ORDER_ASC)
        }
        for (classSub in results) {
            dataReturns.add([id: classSub.subjectId, name: classSub.subjectName])
        }
        dataReturns
    }
    //refactor done
    def subjectDropDownList(ClassName className, GroupName groupName=null) {
        List dataReturns = new ArrayList()
        def c = StdClassSubjectView.createCriteria()
        def results = c.list() {
            eq("classNameId", className.id)
            'in'("subjectType", SubjectType.classUse() as List)
            if(groupName){
                or {
                    and {
                        isNull("groupName")
                    }
                    and {
                        eq("groupName", groupName.key)
                    }
                }
            }
            order("sortPosition", CommonUtils.SORT_ORDER_ASC)
        }
        for (classSub in results) {
            dataReturns.add([id: classSub.subjectId, name: classSub.subjectName])
        }
        dataReturns
    }


    def subjectLessonDropDownList(ClassName className, GroupName groupName=null) {
        List dataReturns = new ArrayList()
        def c = StdClassSubjectView.createCriteria()
        def results = c.list() {
            eq("classNameId", className.id)
            'in'("subjectType", SubjectType.classUse() as List)
            if(groupName){
                or {
                    and {
                        isNull("groupName")
                    }
                    and {
                        eq("groupName", groupName.key)
                    }
                }
            }
            order("sortPosition", CommonUtils.SORT_ORDER_ASC)
        }
        for (classSub in results) {
            dataReturns.add([id: classSub.subjectId, name: classSub.subjectName])
        }
        dataReturns
    }

    //refactor done
    def subjectsNameListForClassSubject(ClassName className, GroupName groupName = null) {
        def c = StdClassSubjectView.createCriteria()
        def results = c.list() {
            eq("classNameId", className.id)
            'ne'("subjectType",SubjectType.INUSE)
            if(groupName){
                or {
                    and {
                        isNull("groupName")
                    }
                    and {
                        eq("groupName", groupName.key)
                    }
                }
            }
            projections {
                property('subjectName')
            }
            order("sortPosition", CommonUtils.SORT_ORDER_ASC)
        }
        return results
    }

    def ctSubjects(ClassName className, boolean isOtherActivity, GroupName groupName=null){
        def c = StdClassSubjectView.createCriteria()
        def results = c.list() {
            eq("classNameId", className.id)
            'in'("subjectType", SubjectType.classUse() as List)
            if (!isOtherActivity){
                eq("isOtherActivity", isOtherActivity)
            }
            eq("isCtExam", true)
            if(groupName){
                or {
                    and {
                        isNull("groupName")
                    }
                    and {
                        eq("groupName", groupName.key)
                    }
                }
            }
            order("sortPosition", CommonUtils.SORT_ORDER_ASC)
        }
        List ctSubjects = new ArrayList()
        boolean alreadyAdded = false
        for (clsSubject in results) {
            alreadyAdded = ctSubjects.find { it.subjectId == clsSubject.subjectId }
            if (!alreadyAdded) {
                ctSubjects.add([subjectId: clsSubject.subjectId, subjectName:clsSubject.subjectName, ctMark: clsSubject.ctMark])
            }
        }
        return ctSubjects
    }

    def isCtExamSubject(SubjectName subjectName, ClassName className, GroupName groupName=null){
        def c = StdClassSubjectView.createCriteria()
        def count = c.list() {
            'in'("subjectType", SubjectType.classUse() as List)
            if(groupName){
                eq("classNameId", className.id)
                eq("subjectId", subjectName.id)
                or {
                    and {
                        eq("isCtExam", true)
                        isNull("groupName")
                    }
                    and {
                        eq("isCtExam", true)
                        eq("groupName", groupName.key)
                    }
                }
            }else {
                and {
                    eq("classNameId", className.id)
                    eq("isCtExam", true)
                }
            }
            projections {
                count()
            }
        }
        return count[0]
    }


    def htSubjects(ClassName className, boolean isOtherActivity, GroupName groupName=null){
        def c = StdClassSubjectView.createCriteria()
        def results = c.list() {
            eq("classNameId", className.id)
            'in'("subjectType", SubjectType.classUse() as List)
            if (!isOtherActivity){
                eq("isOtherActivity", isOtherActivity)
            }
            eq("isHallExam", true)
            if(groupName){
                or {
                    and {
                        isNull("groupName")
                    }
                    and {
                        eq("groupName", groupName.key)
                    }
                }
            }
            order("sortPosition", CommonUtils.SORT_ORDER_ASC)
        }
        List htSubjects = new ArrayList()
        boolean alreadyAdded = false
        for (clsSubject in results) {
            alreadyAdded = htSubjects.find { it.subjectId == clsSubject.subjectId }
            if (!alreadyAdded) {
                htSubjects.add([subjectId: clsSubject.subjectId, subjectName:clsSubject.subjectName, hallMark: clsSubject.hallMark])
            }
        }
        return htSubjects
    }

    def isHtExamSubject(SubjectName subjectName, ClassName className, GroupName groupName=null){
        def c = StdClassSubjectView.createCriteria()
        def count = c.list() {
            'in'("subjectType", SubjectType.classUse() as List)
            if(groupName){
                eq("classNameId", className.id)
                eq("subjectId", subjectName.id)
                or {
                    and {
                        eq("isHallExam", true)
                        isNull("groupName")
                    }
                    and {
                        eq("isHallExam", true)
                        eq("groupName", groupName.key)
                    }
                }
            }else {
                and {
                    eq("classNameId", className.id)
                    eq("isHallExam", true)
                }
            }
            projections {
                count()
            }
        }
        return count[0]
    }

    def htSubjectsNotInList(ClassName className, List subjectIds, boolean isOtherActivity, GroupName groupName = null){
        def c = StdClassSubjectView.createCriteria()
        def results = c.list() {
            'in'("subjectType", SubjectType.classUse() as List)
            if (!isOtherActivity) {
                eq("isOtherActivity", isOtherActivity)
            }
            eq("classNameId", className.id)
            eq("isHallExam", true)
            if(subjectIds){
                not {'in'("subjectId",subjectIds)}
            }
            if(groupName){
                or {
                    and {
                        isNull("groupName")
                    }
                    and {
                        eq("groupName", groupName.key)
                    }
                }
            }
            order("sortPosition", CommonUtils.SORT_ORDER_ASC)
        }
        return results
    }

    /*def ctSubjectsNotInList(ClassName className, List subjectIds, GroupName groupName=null){
        def c = StdClassSubjectView.createCriteria()
        def results = c.list() {
            'in'("subjectType", SubjectType.classUse() as List)
            eq("classNameId", className.id)
            if(subjectIds){
                not {'in'("subjectId",subjectIds)}
            }
            if(groupName){
                or {
                    and {
                        eq("isCtExam", true)
                        isNull("groupName")
                    }
                    and {
                        eq("isCtExam", true)
                        eq("groupName", groupName.key)
                    }
                }
            }else {
                and {
                    eq("isCtExam", true)
                }
            }
            order("sortPosition", CommonUtils.SORT_ORDER_ASC)
        }
        return results
    }*/
    def ctSubjectsNotInList(ClassName className, List subjectIds, boolean isOtherActivity, GroupName groupName=null){
        def c = StdClassSubjectView.createCriteria()
        def results = c.list() {
            'in'("subjectType", SubjectType.classUse() as List)
            if (!isOtherActivity) {
                eq("isOtherActivity", isOtherActivity)
            }
            eq("classNameId", className.id)
            eq("isCtExam", true)
            if(subjectIds){
                not {'in'("subjectId",subjectIds)}
            }
            if(groupName){
                or {
                    and {
                        isNull("groupName")
                    }
                    and {
                        eq("groupName", groupName.key)
                    }
                }
            }
            order("sortPosition", CommonUtils.SORT_ORDER_ASC)
        }
        results
    }
    /*def extracurricularSubjects(ClassName className){
        def c = ClassSubjects.createCriteria()
        def results = c.list() {
            and {
                eq("activeStatus", ActiveStatus.ACTIVE )
                eq("className", className)
                eq("isExtracurricular", true)
            }
        }
        return results
    }*/

    def hallExamSubjectHeaderIds(ClassName className, boolean isOtherActivity, GroupName groupName=null){
        def c = StdClassSubjectView.createCriteria()
        def results = c.list() {
            'in'("subjectType", SubjectType.withOptional() as List)
            if (!isOtherActivity) {
                eq("isOtherActivity", isOtherActivity)
            }
            eq("classNameId", className.id)
            eq("isHallExam", true)
            if(groupName){
                or {
                    and {
                        isNull("groupName")
                    }
                    and {
                        eq("groupName", groupName.key)
                    }
                }
            }
            order("sortPosition", CommonUtils.SORT_ORDER_ASC)
            projections {
                property "subjectId"
            }
        } as List
        results
    }
    def ctExamSubjectHeaderIds(ClassName className, GroupName groupName=null){
        def c = StdClassSubjectView.createCriteria()
        def results = c.list() {
            'in'("subjectType", SubjectType.withOptional() as List)
            eq("classNameId", className.id)
            eq("isCtExam", true)
            if(groupName){
                or {
                    and {
                        isNull("groupName")
                    }
                    and {
                        eq("groupName", groupName.key)
                    }
                }
            }
            order("sortPosition", CommonUtils.SORT_ORDER_ASC)
            projections {
                property "subjectId"
            }
        } as List
        results
    }

    def syncClassSubjectDetails(Exam exam){
        def examScheduleList = examScheduleService.examSchedules(exam)
        ClassSubjects classSubject
        Double ctExamMark
        Double ctMark
        Integer ctEffMark

        Double hallExamMark
        Double hallMark
        Integer hallEffMark

        for (ExamSchedule schedule: examScheduleList){
            classSubject = get(exam.className, exam.section.groupName, schedule.subject)
            if (classSubject) {
                schedule.isCompulsory = classSubject.subjectType == SubjectType.COMPULSORY ? true : false
                ctExamMark = classSubject.ctMark?:0
                ctEffMark = classSubject.ctEffMark?:0
                if (classSubject.isCtExam) {
                    if (ctEffMark == 100) {
                        ctMark = classSubject.ctMark
                    } else {
                        ctMark = ctExamMark * 0.01 * ctEffMark
                    }
                } else {
                    ctMark = 0
                }

                hallExamMark = classSubject.hallMark?:0
                hallEffMark = classSubject.hallEffMark?:0
                if (classSubject.isHallExam) {
                    if (hallEffMark == 100) {
                        hallMark = hallExamMark
                    } else {
                        hallMark = hallExamMark * 0.01 * hallEffMark
                    }
                } else {
                    hallMark = 0
                }

                schedule.ctExamMark = ctExamMark
                schedule.hallExamMark = hallExamMark
                schedule.fullMark = ctMark + hallMark
                schedule.tabulationEffPercentage = classSubject.weightOnResult

                schedule.parentSubId = classSubject.parentSubId
                schedule.isExtracurricular= classSubject.isExtracurricular
                schedule.passMark = classSubject.passMark
                schedule.sortOrder = classSubject.sortOrder

                schedule.ctMarkEffPercentage = ctEffMark

                schedule.hallMarkEffPercentage = hallEffMark

                if (classSubject.isHallPractical) {
                    schedule.isHallPractical = classSubject.isHallPractical
                    schedule.hallWrittenMark = classSubject.hallWrittenMark
                    schedule.hallPracticalMark = classSubject.hallPracticalMark
                    schedule.hallObjectiveMark = classSubject.hallObjectiveMark
                    schedule.hallSbaMark = classSubject.hallSbaMark
                    schedule.hallInput5 = classSubject.hallInput5
                } else {
                    schedule.isHallPractical = false
                    schedule.hallWrittenMark = 0
                    schedule.hallPracticalMark = 0
                    schedule.hallObjectiveMark = 0
                    schedule.hallSbaMark = 0
                    schedule.hallInput5 = 0
                }
                if (classSubject.isPassSeparately) {
                    schedule.isPassSeparately = true
                    schedule.writtenPassMark = classSubject.writtenPassMark
                    schedule.practicalPassMark = classSubject.practicalPassMark
                    schedule.objectivePassMark = classSubject.objectivePassMark
                    schedule.sbaPassMark = classSubject.sbaPassMark
                    schedule.input5PassMark = classSubject.input5PassMark
                } else {
                    schedule.isPassSeparately = false
                    schedule.writtenPassMark = 0
                    schedule.practicalPassMark = 0
                    schedule.objectivePassMark = 0
                    schedule.sbaPassMark = 0
                    schedule.input5PassMark = 0
                }
                schedule.save()
            }
        }
    }

    ClassSubjects get(ClassName className, GroupName groupName, SubjectName subjectName){
        if (groupName) {
            return ClassSubjects.findByClassNameAndGroupNameAndSubject(className, groupName, subjectName)
        }
        return ClassSubjects.findByClassNameAndSubject(className, subjectName)
    }
}
