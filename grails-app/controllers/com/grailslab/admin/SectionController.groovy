package com.grailslab.admin

import com.grailslab.CommonUtils
import com.grailslab.command.SectionCommand
import com.grailslab.enums.StudentStatus
import com.grailslab.gschoolcore.AcademicYear
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.settings.Section
import com.grailslab.stmgmt.Student
import grails.converters.JSON

class SectionController {

    def classNameService
    def sectionService
    def schoolService
    def employeeService
    def classRoomService
    def messageSource

    def index() {
        def teacherList = employeeService.teacherDropDownList()
        def classNameList =classNameService.allClassNames()
        def classRoomList = classRoomService.classRoomDropDownList()
        AcademicYear workingYear = schoolService.workingYear()
        def academicYearList = schoolService.academicYears()
        render(view: '/admin/section', model: [academicYearList:academicYearList,workingYear:workingYear, classRoomList:classRoomList,classNameList:classNameList,teacherList:teacherList])
    }

    def save(SectionCommand sectionCommand) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        if (sectionCommand.hasErrors()) {
            def errorList = sectionCommand?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, errorList?.join('\n'))
            outPut = result as JSON
            render outPut
            return
        }

        Section section
        String message
        int ifExistSection
        AcademicYear academicYear = sectionCommand.academicYear
        if (params.id) { //update Currency
            section = Section.get(sectionCommand.id)
            if (!section) {
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
                outPut = result as JSON
                render outPut
                return
            }
            ifExistSection=Section.countByNameAndActiveStatusAndAcademicYearAndClassNameAndIdNotEqual(sectionCommand.name,ActiveStatus.ACTIVE, academicYear, sectionCommand.className, sectionCommand.id)
            if(ifExistSection > 0) {
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE,'Section already Exist')
                outPut=result as JSON
                render outPut
                return
            }
            if (section.academicYear != academicYear) {
                int sectionStudent = Student.countBySectionAndStudentStatus(section, StudentStatus.NEW)
                if(sectionStudent > 0) {
                    result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                    result.put(CommonUtils.MESSAGE,"Can't update Academic year of this section. Please delete all student of this section first")
                    outPut=result as JSON
                    render outPut
                    return
                }
            }

            section.properties = sectionCommand.properties
            section.academicYear = academicYear
            if(!sectionCommand.className.groupsAvailable){
                section.groupName = null
            }
            result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
            message ='Section Updated successfully'
        }else {
            ifExistSection=Section.countByNameAndActiveStatusAndAcademicYearAndClassName(sectionCommand.name,ActiveStatus.ACTIVE, academicYear, sectionCommand.className)
            if(ifExistSection > 0) {
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE,'Section already Exist')
                outPut=result as JSON
                render outPut
                return
            }
            section = new Section(sectionCommand.properties)
            section.academicYear = academicYear
            if(!sectionCommand.className.groupsAvailable){
                section.groupName = null
            }
            result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
            message ='Section Added successfully'
        }
        if(section.hasErrors() || !section.save()){
            def errorList = section?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            message = errorList?.join('\n')
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
        }
        result.put(CommonUtils.MESSAGE, message)
        outPut = result as JSON
        render outPut
        return
    }

  /*  def delete(Long id) {
        LinkedHashMap result = new LinkedHashMap()
        result.put('isError',true)
        String outPut
        Section section = Section.get(id)
        if(section) {
            try {
                section.activeStatus= ActiveStatus.INACTIVE
                section.save(flush: true)
                result.put('isError',false)
                result.put('message',"Section deleted successfully.")
                outPut = result as JSON
                render outPut
                return

            }
            catch(DataIntegrityViolationException e) {
                result.put('isError',true)
                result.put('message',"Section could not deleted. Already in use.")
                outPut = result as JSON
                render outPut
                return
            }

        }
        result.put('isError',true)
        result.put('message',"Section not found")
        outPut = result as JSON
        render outPut
        return
    }*/
    def inactive(Long id) {
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        Section section = Section.get(id)
        if (!section) {
            result.put(CommonUtils
                    .IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }

        if(section.activeStatus.equals(ActiveStatus.INACTIVE)){
            section.activeStatus=ActiveStatus.ACTIVE
        } else {
            section.activeStatus=ActiveStatus.INACTIVE
        }

        section.save()
        result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
        result.put(CommonUtils.MESSAGE,"Section deleted successfully.")
        outPut = result as JSON
        render outPut
    }

    def list() {
        LinkedHashMap gridData
        String result
        LinkedHashMap resultMap =sectionService.sectionPaginateList(params)

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
        Section section = Section.read(id)
        if (!section) {
            result.put('message','Section name not found')
            outPut = result as JSON
            render outPut
            return
        }
        result.put('isError',false)
        result.put('obj',section)
        outPut = result as JSON
        render outPut
    }

     def moveSection() {
         AcademicYear academicYear
         if (params.academicYear) {
             academicYear = AcademicYear.valueOf(params.academicYear)
         } else {
             academicYear = schoolService.previousSchoolYear()
         }
         int year
         AcademicYear nextYear
         if (academicYear?.value.length() == 4) {
             year = Integer.parseInt(academicYear.value)
             year = year +1
             nextYear = AcademicYear.getYearByString(year.toString())
         } else {
             year = Integer.parseInt(academicYear?.value.substring(0, 4))
             int lowYear = year+1
             int highYear = year+2
             nextYear = AcademicYear.getYearByString(lowYear+"-"+highYear)
         }

         if(!nextYear){
             flash.message ="Next year yet not initiated."
             redirect(action: 'index')
             return
         }
         def nextYearSectionList = sectionService.sectionDropDownList(nextYear)
         List sectionIds
         if (nextYearSectionList && nextYearSectionList.size() > 0){
             sectionIds = nextYearSectionList.collect {it.id}
         }

        def sectionList = sectionService.sectionDropDownNotInList(academicYear, sectionIds)
        render(view: '/admin/sectionTransfer', model: [sectionList: sectionList, nextYearSectionList: nextYearSectionList, academicYear:academicYear])
    }

    def saveTransfer() {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        String message

        if (!params.academicYear) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        AcademicYear academicYear = AcademicYear.valueOf(params.academicYear)

        int year
        AcademicYear nextYear
        if (academicYear?.value.length() == 4) {
            year = Integer.parseInt(academicYear.value)
            year = year +1
            nextYear = AcademicYear.getYearByString(year.toString())
        } else {
            year = Integer.parseInt(academicYear?.value.substring(0, 4))
            int lowYear = year+1
            int highYear = year+2
            nextYear = AcademicYear.getYearByString(lowYear+"-"+highYear)
        }

        if(!nextYear){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "${year} not initiated yet for section transfer. Please contact admin")
            outPut = result as JSON
            render outPut
            return
        }

        if (!params.sectionIds){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "Please select section to move next year")
            outPut = result as JSON
            render outPut
            return
        }
        def sectionIdList= params.sectionIds.split(",")

        if (!sectionIdList || sectionIdList.size() == 0) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "Please select section to move next year")
            outPut = result as JSON
            render outPut
            return
        }
        Section oldSection
        int ifExistSection
        int oldSectionCount = 0
        sectionIdList.each { it ->
            oldSection = Section.read(Long.valueOf(it))
            ifExistSection=Section.countByNameAndActiveStatusAndAcademicYearAndClassName(oldSection.name, ActiveStatus.ACTIVE, nextYear, oldSection.className)
            if(ifExistSection == 0) {
                new Section(name: oldSection.name, shift: oldSection.shift, classRoom: oldSection.classRoom, className: oldSection.className, groupName: oldSection.groupName, employee: oldSection.employee, academicYear: nextYear).save()
            } else {
                oldSectionCount++
            }
        }
        if (oldSectionCount > 0) {
            message = "$oldSectionCount sections already exist. Other sections moved successfully"
        } else {
            message = "All sections moved successfully"
        }
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE,message)
        outPut=result as JSON
        render outPut
    }
}
