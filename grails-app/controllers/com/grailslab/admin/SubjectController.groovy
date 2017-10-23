package com.grailslab.admin

import com.grailslab.CommonUtils
import com.grailslab.command.SubjectCommand
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.settings.SubjectName
import grails.converters.JSON

class SubjectController {

    def subjectService

    def index() {
        LinkedHashMap resultMap = subjectService.subjectPaginateList(params)

        if (!resultMap || resultMap.totalCount == 0) {
            render(view: '/admin/subject', model: [dataReturn: null, totalCount: 0])
            return
        }
        int totalCount = resultMap.totalCount
        render(view: '/admin/subject', model: [dataReturn: resultMap.results, totalCount: totalCount])
    }
    def sortOrder() {
        def subjectList = subjectService.allSubjectList()
        render(view: '/admin/subjectSorting', model: [subjectList: subjectList])
    }

    def subjectSortingSave(){
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }

        SubjectName subjectName
        params.subject.each{idx ->
            subjectName = SubjectName.get(Long.valueOf(idx))
            subjectName.name =params."subjectName$idx"
            subjectName.code = params."code$idx"
            subjectName.sortPosition = params.getInt("sortPosition$idx")
            subjectName.save()
        }
        redirect(action: 'index')
    }


    def save(SubjectCommand subjectCommand) {
        println params

        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        result.put('isError',true)
        String outPut
        if (subjectCommand.hasErrors()) {
            result.put('message','Please fill the form correctly')
            outPut=result as JSON
            render outPut
            return
        }
        SubjectName subject
        if (params.id) { //update Currency
            subject = SubjectName.get(subjectCommand.id)
            if (!subject) {
                result.put('message','Subject not found')
                outPut=result as JSON
                render outPut
                return
            }
            SubjectName ifExistSubjectName=SubjectName.findByNameAndActiveStatusAndIdNotEqual(subjectCommand.name,ActiveStatus.ACTIVE,subjectCommand.id)
            if(ifExistSubjectName) {
                result.put('message','Same Subject already Exist')
                outPut=result as JSON
                render outPut
                return
            }
            subject.properties = subjectCommand.properties
            if (!subjectCommand.hasChild){
                subject.hasChild = false
            }
            if (subjectCommand.parentId) {
                subject.isAlternative = true
            } else {
                subject.isAlternative = false
            }
            if (!subject.validate()) {
                result.put('message','Please fill the form correctly')
                outPut=result as JSON
                render outPut
                return
            }

            SubjectName savedClass =subject.save(flush: true)
            result.put('isError',false)
            result.put('message','Subject Updated successfully')
            outPut=result as JSON
            render outPut
            return
        }
        subject = new SubjectName(subjectCommand.properties)
        if (!subjectCommand.hasChild){
            subject.hasChild = false
        }
        if (subjectCommand.parentId) {
            subject.isAlternative = true
        } else {
            subject.isAlternative = false
        }

        if (!subjectCommand.validate()) {
            result.put('message','Please fill the form correctly')
            outPut=result as JSON
            render outPut
            return
        }
        SubjectName ifExistSubjectName=SubjectName.findByNameAndActiveStatus(subjectCommand.name,ActiveStatus.ACTIVE)
        if(ifExistSubjectName) {
            result.put('message','Same Subject already Exist')
            outPut=result as JSON
            render outPut
            return
        }
        SubjectName savedCurr = subject.save(flush: true)
        if (!savedCurr) {
            result.put('message','Please fill the form correctly')
            outPut=result as JSON
            render outPut
            return
        }
        result.put('isError',false)
        result.put('message','Subject Create successfully')
        outPut=result as JSON
        render outPut

    }

   /* def delete(Long id) {
        LinkedHashMap result = new LinkedHashMap()
        result.put('isError',true)
        String outPut
        SubjectName subject = SubjectName.get(id)
        if(subject) {
            try {
                subject.activeStatus=ActiveStatus.INACTIVE
                subject.save(flush:true)
                result.put('isError',false)
                result.put('message',"Subject deleted successfully.")
                outPut = result as JSON
                render outPut
                return

            }

            catch(DataIntegrityViolationException e) {
                result.put('isError',true)
                result.put('message',"Subject could not deleted. Already in use.")
                outPut = result as JSON
                render outPut
                return
            }

        }
        result.put('isError',true)
        result.put('message',"Subject not found")
        outPut = result as JSON
        render outPut
        return
    }*/

    def inactive(Long id) {
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        SubjectName subject = SubjectName.get(id)
        if (!subject) {
            result.put(CommonUtils
                    .IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }

        if(subject.activeStatus.equals(ActiveStatus.INACTIVE)){
            subject.activeStatus=ActiveStatus.ACTIVE
        } else {
            subject.activeStatus=ActiveStatus.INACTIVE
        }

        subject.save()
        result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
        result.put(CommonUtils.MESSAGE,"Subject deleted successfully.")
        outPut = result as JSON
        render outPut
    }


    def list() {
        LinkedHashMap gridData
        String result
        LinkedHashMap resultMap =subjectService.subjectPaginateList(params)

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

    def childlist(Long id) {
        LinkedHashMap gridData
        String result

        SubjectName subjectName = SubjectName.read(id)
        if(!subjectName){
            gridData = [iTotalRecords: 0, iTotalDisplayRecords: 0, aaData: []]
            result = gridData as JSON
            render result
            return
        }
        LinkedHashMap resultMap = subjectService.childPaginateList(params, subjectName)

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




    def edit(Long id) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        result.put('isError',true)
        String outPut
        SubjectName subject = SubjectName.read(id)
        if (!subject) {
            result.put('message','Subject not found')
            outPut = result as JSON
            render outPut
            return
        }
        result.put('isError',false)
        result.put('obj',subject)
        outPut = result as JSON
        render outPut
    }

    def addSubject(Long id) {
        SubjectName subjectName = SubjectName.read(id)
        if (!subjectName) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap resultMap = subjectService.childPaginateList(params, subjectName)

        if (!resultMap || resultMap.totalCount == 0) {
            render(view: '/admin/addSubject', model: [dataReturn: null, totalCount: 0, subjectName: subjectName])
            return
        }
        int totalCount = resultMap.totalCount
        render(view: '/admin/addSubject', model: [subjectName: subjectName, dataReturn: resultMap.results, totalCount: totalCount])
    }
}
