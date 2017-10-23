package com.grailslab.admin

import com.grailslab.CommonUtils
import com.grailslab.command.ClassRoomCommand
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.settings.ClassRoom
import grails.converters.JSON

class ClassRoomController {

    def classRoomService

    def index() {
        LinkedHashMap resultMap = classRoomService.classRoomPaginateList(params)

        if (!resultMap || resultMap.totalCount == 0) {
            render(view: '/admin/classRoom', model: [dataReturn: null, totalCount: 0])
            return
        }
        int totalCount = resultMap.totalCount
        render(view: '/admin/classRoom', model: [dataReturn: resultMap.results, totalCount: totalCount])
    }

    def save(ClassRoomCommand classRoomCommand) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        result.put('isError',true)
        String outPut
        if (classRoomCommand.hasErrors()) {
            result.put('message','Please fill the form correctly')
            outPut=result as JSON
            render outPut
            return
        }
        ClassRoom classRoom
        if (params.id) { //update Currency
            classRoom = ClassRoom.get(classRoomCommand.id)
            if (!classRoom) {
                result.put('message','Class room not found')
                outPut=result as JSON
                render outPut
                return
            }
            ClassRoom ifExistClassRoom=ClassRoom.findByNameAndActiveStatusAndIdNotEqual(classRoomCommand.name,ActiveStatus.ACTIVE,classRoomCommand.id)
            if(ifExistClassRoom) {
                result.put('message','Same Class Room already Exist')
                outPut=result as JSON
                render outPut
                return
            }
            classRoom.properties = classRoomCommand.properties
            if (!classRoom.validate()) {
                result.put('message','Please fill the form correctly')
                outPut=result as JSON
                render outPut
                return
            }

            ClassRoom savedClass =classRoom.save(flush: true)
            result.put('isError',false)
            result.put('message','Class room Updated successfully')
            outPut=result as JSON
            render outPut
            return
        }


        classRoom = new ClassRoom(classRoomCommand.properties)
        if (!classRoomCommand.validate()) {
            result.put('message','Please fill the form correctly')
            outPut=result as JSON
            render outPut
            return
        }
        ClassRoom ifExistClassRoom=ClassRoom.findByNameAndActiveStatus(classRoom.name,ActiveStatus.ACTIVE)
        if(ifExistClassRoom) {
            result.put('message','Same Class Room already Exist')
            outPut=result as JSON
            render outPut
            return
        }
        ClassRoom savedCurr = classRoom.save(flush: true)
        if (!savedCurr) {
            result.put('message','Please fill the form correctly')
            outPut=result as JSON
            render outPut
            return
        }
        result.put('isError',false)
        result.put('message','Class room Create successfully')
        outPut=result as JSON
        render outPut

    }

   /* def delete(Long id) {
        LinkedHashMap result = new LinkedHashMap()
        result.put('isError',true)
        String outPut
        ClassRoom classRoom = ClassRoom.get(id)
        if(classRoom) {
            try {
                classRoom.activeStatus= ActiveStatus.INACTIVE
                classRoom.save(flush:true)
                result.put('isError',false)
                result.put('message',"Class room deleted successfully.")
                outPut = result as JSON
                render outPut
                return

            }

            catch(DataIntegrityViolationException e) {
                result.put('isError',true)
                result.put('message',"Class room could not deleted. Already in use.")
                outPut = result as JSON
                render outPut
                return
            }

        }
        result.put('isError',true)
        result.put('message',"Class room not found")
        outPut = result as JSON
        render outPut
        return
    }
*/
    def inactive(Long id) {
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        ClassRoom classRoom = ClassRoom.get(id)
        if (!classRoom) {
            result.put(CommonUtils
                    .IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }

        if(classRoom.activeStatus.equals(ActiveStatus.INACTIVE)){
            classRoom.activeStatus=ActiveStatus.ACTIVE
        } else {
            classRoom.activeStatus=ActiveStatus.INACTIVE
        }

        classRoom.save()
        result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
        result.put(CommonUtils.MESSAGE,"Class Room deleted successfully.")
        outPut = result as JSON
        render outPut
    }
    def list() {
        LinkedHashMap gridData
        String result
        LinkedHashMap resultMap =classRoomService.classRoomPaginateList(params)

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
        ClassRoom classRoom = ClassRoom.read(id)
        if (!classRoom) {
            result.put('message','Class room not found')
            outPut = result as JSON
            render outPut
            return
        }
        result.put('isError',false)
        result.put('obj',classRoom)
        outPut = result as JSON
        render outPut
    }
}
