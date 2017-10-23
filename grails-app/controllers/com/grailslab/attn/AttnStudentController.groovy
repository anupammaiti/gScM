package com.grailslab.attn

import com.grailslab.CommonUtils
import com.grailslab.command.AttnRecordDayCommand
import com.grailslab.command.AttnStudentCommand
import com.grailslab.command.AttnStudentLateEntryCommand
import com.grailslab.enums.Shift
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.hr.HrPeriod
import com.grailslab.settings.ClassName
import com.grailslab.settings.Section
import com.grailslab.stmgmt.Student
import grails.converters.JSON
import org.joda.time.DateTime
import org.springframework.dao.DataIntegrityViolationException

class AttnStudentController {
    def classNameService
    def attnStudentService
    def examService
    def sectionService
    def classSubjectsService
    def studentSubjectsService
    def grailsApplication
    def recordDayService
    def hrPeriodService
    def studentService
    def schoolService


    def index(Long id) {
        def classList=classNameService.classNameDropDownList()
        AttnRecordDay lastRecord
        if(id) {
            lastRecord = AttnRecordDay.read(id)
        } else {
            def lastStdPresents = AttnStdRecord.last()
            if (lastStdPresents) {
                lastRecord = lastStdPresents.recordDay
            }
        }
        if (!lastRecord) {
            render(view: '/attn/student/stdAttendanceList', model: [dataReturn: null, totalCount: 0, lastRecord:  CommonUtils.getUiDateStrForPicker(new Date()), classList:classList])
            return
        }
        params.put("recordDateId", lastRecord.id)
        LinkedHashMap resultMap = attnStudentService.stdPaginateList(params)
        if (!resultMap || resultMap.totalCount == 0) {
            render(view: '/attn/student/stdAttendanceList', model: [dataReturn: null, totalCount: 0,classList:classList, lastRecord: CommonUtils.getUiDateStrForPicker(lastRecord.recordDate)])
            return
        }
        int totalCount = resultMap.totalCount
        render(view: '/attn/student/stdAttendanceList', model: [dataReturn: resultMap.results, totalCount: totalCount,classList:classList, lastRecord: CommonUtils.getUiDateStrForPicker(lastRecord.recordDate)])
    }

    def manualAttendList(Long id){
        ClassName className = ClassName.read(id)
        def classNameList = classNameService.classNameDropDownList()
        render(view: '/attn/student/manualAttendanceList', model: [classNameList:classNameList, className: className])
    }
    def loadStudentManualAttend(AttnRecordDayCommand command){
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        Date attenDate = command.attendDate
           if (!attenDate) {
                attenDate = new Date().clearTime()
            }

        AttnRecordDay recordDay = recordDayService.recordDayForDevice(attenDate)
        if (!recordDay){
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "Date is not valid")
            outPut = result as JSON
            render outPut
            return
        }
        def studentList = attnStudentService.manualAttendanceList(command.sectionName, recordDay)

        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put('studentList', studentList)
        outPut = result as JSON
        render outPut
    }

    def saveManualAttendence(AttnRecordDayCommand command){
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        AttnRecordDay attnRecordDay
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        int j=1;
        int loadedrowsize=  params.getInt("loadedrow")
         Date now =new Date();
        Date attenDate = command.attendDate
        if (!attenDate) {
            attenDate = new Date().clearTime()
        }
        Section section = command.sectionName
        String classInTime
        if(section.shift == Shift.MORNING) {
            classInTime = grailsApplication.config.grailslab.gschool.morning.inTime
        } else {
            classInTime = grailsApplication.config.grailslab.gschool.day.inTime
        }
        Date inTime =CommonUtils.deviceLogDateStrToDate(attenDate.format("yyyyMMdd"), classInTime)

        if(attenDate <= now.clearTime()){
            attnRecordDay = recordDayService.recordDayForDevice(attenDate)
            if(attnRecordDay){
                int attendanceCount = attnStudentService.attendanceRecordCount(attnRecordDay, section.id)
                if (attendanceCount > 0) {
                    AttnStdRecord.executeUpdate("delete AttnStdRecord c where c.recordDay = :recordDay and c.sectionId = :sectionId", [recordDay:attnRecordDay, sectionId: section.id])
                }

            AttnStdRecord attnStdRecordInfo
            for(int i=0; i<loadedrowsize; i++)  {
                if(params["attanStatus["+j+"]"].equals("1")) {
                    attnStdRecordInfo= new AttnStdRecord()
                    attnStdRecordInfo.stdNo=params["stdNo["+j+"]"]
                    attnStdRecordInfo.studentId=params.getLong(["studentId["+j+"]"])
                    attnStdRecordInfo.sectionId=section.id
                    attnStdRecordInfo.inTime=inTime
                    attnStdRecordInfo.outTime=null;
                    attnStdRecordInfo.isLateEntry=false;
                    attnStdRecordInfo.recordDay=attnRecordDay
                    attnStdRecordInfo.recordDate = attnRecordDay.recordDate
                    attnStdRecordInfo.save()
                }
                j++;
            }
            attnRecordDay.save()
            result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
            result.put(CommonUtils.MESSAGE, "save successfully")
            outPut = result as JSON
        }else{
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "Date is not valid")
            outPut = result as JSON
        }}else{
                result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
                result.put(CommonUtils.MESSAGE, "Date is not valid")
                outPut = result as JSON
        }
        render outPut

    }

    def updateLateEntry(AttnStudentLateEntryCommand command){
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        ClassName clasName = command.className
        def studentList = studentService.byClassName(clasName, schoolService.workingYear(clasName))

        DateTime startDateTime
        DateTime endDateTime = new DateTime(command.endDate).withHourOfDay(23)
        DateTime thisDate
        Date effectiveDate

        HrPeriod hrPeriod = clasName.hrPeriod
        AttnStdRecord attnStdRecord
        for (student in studentList) {
            startDateTime = new DateTime(command.startDate)
            while (startDateTime.isBefore(endDateTime)) {
                thisDate = startDateTime
                effectiveDate = thisDate.toDate()
                attnStdRecord = AttnStdRecord.findByRecordDateAndStdNo(effectiveDate, student.studentID)
                if (attnStdRecord) {
                    attnStdRecord.isLateEntry = hrPeriodService.isLateEntry(attnStdRecord.inTime, hrPeriod?.startTime, hrPeriod?.lateTolerance)
                    attnStdRecord.save()
                }
                startDateTime = startDateTime.plusDays(1)
            }
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        result.put(CommonUtils.IS_ERROR, false)
        result.put(CommonUtils.MESSAGE, "Late Attendance Updated Successfully")
        outPut = result as JSON
        render outPut
        return

    }

    def saveIndividualAttendance(AttnStudentCommand command) {
        if (!request.method.equals('POST')) {
            redirect(action: 'index')
            return
        }
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        String message
        AttnStdRecord attnStdRecord
        Student student

        Date inTimeDate
        Date outTimeDate
        Date recordDate
        if (command.id) {
            attnStdRecord = AttnStdRecord.get(command.id)
            student = Student.read(attnStdRecord.studentId)
            recordDate = attnStdRecord.recordDate
            inTimeDate = CommonUtils.manualAttnDateStrToDate(recordDate, command.inTime)
            if (command.outTime) {
                outTimeDate = CommonUtils.manualAttnDateStrToDate(recordDate, command.outTime)
            }
            attnStdRecord.inTime = inTimeDate
            attnStdRecord.outTime = outTimeDate
            attnStdRecord.remarks = command.reason
            attnStdRecord.isLateEntry = hrPeriodService.isLateEntry(inTimeDate, student.className.hrPeriod?.startTime, student.className.hrPeriod?.lateTolerance)
            message = "Attendance for $student.name updated successfully"
        } else {
            recordDate = command.recordDate
            if (!recordDate) recordDate = new Date().clearTime()
            AttnRecordDay attnRecordDay = recordDayService.recordDayForDevice(recordDate)
            student = Student.read(command.studentId)
            attnStdRecord = AttnStdRecord.findByRecordDayAndStdNo(attnRecordDay, student.studentID)
            if (attnStdRecord) {
                result.put(CommonUtils.IS_ERROR, true)
                result.put(CommonUtils.MESSAGE, "$student.name Attendance for ${CommonUtils.getUiDateStr(recordDate)} already added. You can modify that if require.")
                outPut = result as JSON
                render outPut
                return
            }
            inTimeDate = CommonUtils.manualAttnDateStrToDate(recordDate, command.inTime)
            if (command.outTime) {
                outTimeDate = CommonUtils.manualAttnDateStrToDate(recordDate, command.outTime)
            }
            Boolean isLateEntry = hrPeriodService.isLateEntry(inTimeDate, student.className.hrPeriod?.startTime, student.className.hrPeriod?.lateTolerance)
            attnStdRecord = new AttnStdRecord(recordDay: attnRecordDay, recordDate: attnRecordDay.recordDate, studentId: student.id, stdNo: student.studentID, sectionId: student.section.id, inTime: inTimeDate, outTime: outTimeDate, remarks: command.reason, isLateEntry: isLateEntry)
            message = "Attendance for $student.name added successfully"
        }
        if(!attnStdRecord.save()){
            message = "$student.name already exist"
            result.put(CommonUtils.IS_ERROR, false)
            result.put(CommonUtils.MESSAGE, message)
            outPut = result as JSON
            render outPut
            return
        }else{
            result.put(CommonUtils.IS_ERROR, false)
            result.put(CommonUtils.MESSAGE, message)
            outPut = result as JSON
            render outPut
            return
        }


    }


    def list() {
        LinkedHashMap gridData
        String result
        if (!params.attnRecordDate) {
            gridData = [iTotalRecords: 0, iTotalDisplayRecords: 0, aaData: []]
            result = gridData as JSON
            render result
            return
        }
        Date recordDate = Date.parse('dd/MM/yyyy', params.attnRecordDate)
        AttnRecordDay recordDay = AttnRecordDay.findByActiveStatusAndRecordDate(ActiveStatus.ACTIVE, recordDate.clearTime())
        if(!recordDay) {
            gridData = [iTotalRecords: 0, iTotalDisplayRecords: 0, aaData: []]
            result = gridData as JSON
            render result
            return
        }
        params.put("recordDateId", recordDay.id)
        LinkedHashMap resultMap = attnStudentService.stdPaginateList(params)

        if (!resultMap || resultMap.totalCount == 0) {
            gridData = [iTotalRecords: 0, iTotalDisplayRecords: 0, aaData: []]
            result = gridData as JSON
            render result
            return
        }
        int totalCount = resultMap.totalCount
        gridData = [iTotalRecords: totalCount, iTotalDisplayRecords: totalCount, aaData: resultMap.results]
        result = gridData as JSON
        render result
    }

    def edit(Long id){
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        AttnStdRecord attnStdRecord = AttnStdRecord.read(id)
        if (!attnStdRecord) {
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }
        Student student = Student.read(attnStdRecord.studentId)
        result.put(CommonUtils.IS_ERROR,Boolean.FALSE)
        result.put(CommonUtils.OBJ, attnStdRecord)
        result.put('inTime', CommonUtils.getUiTimeForEdit(attnStdRecord.inTime))
        result.put('outTime', CommonUtils.getUiTimeForEdit(attnStdRecord.outTime))
        result.put('studentName', student.studentID+" - "+student.name)
        result.put('attnDate', attnStdRecord.recordDay?.recordDate)
        outPut = result as JSON
        render outPut
    }

    def delete(Long id) {
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        AttnStdRecord attnStdRecord = AttnStdRecord.get(id)
        if (!attnStdRecord) {
            result.put(CommonUtils.IS_ERROR,Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,CommonUtils.COMMON_NOT_FOUND_MESSAGE)
            outPut = result as JSON
            render outPut
            return
        }

        try {
            attnStdRecord.delete()
        }
        catch (DataIntegrityViolationException e) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, "Attendance could not deleted. Already in use.")
            outPut = result as JSON
            render outPut
            return
        }
        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE, "Attendance deleted successfully.")
        outPut = result as JSON
        render outPut
    }


}
