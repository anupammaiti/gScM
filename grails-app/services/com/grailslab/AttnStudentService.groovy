package com.grailslab

import com.grailslab.attn.AttnRecordDay
import com.grailslab.attn.AttnStdRecord
import com.grailslab.enums.AttendanceType
import com.grailslab.enums.Gender
import com.grailslab.enums.Religion
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.settings.ClassName
import com.grailslab.settings.Section
import com.grailslab.viewz.AttnStdPresentView
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

class AttnStudentService {
    static transactional = false
    def classSubjectsService
    def schoolService
    def subjectService
    def sessionFactory




    static final String[] sortColumnsStdAtt = ['recordDayId','className','stdid','name','rollNo','inTime','outTime']

    LinkedHashMap stdPaginateList(GrailsParameterMap params) {
        int iDisplayStart = params.iDisplayStart ? params.getInt('iDisplayStart') : CommonUtils.DEFAULT_PAGINATION_START
        int iDisplayLength = params.iDisplayLength ? params.getInt('iDisplayLength') : CommonUtils.DEFAULT_PAGINATION_LENGTH
        String sSortDir = params.sSortDir_0 ? params.sSortDir_0 : CommonUtils.SORT_ORDER_ASC
        int iSortingCol = params.iSortCol_0 ? params.getInt('iSortCol_0') : CommonUtils.DEFAULT_PAGINATION_SORT_IDX
        String sSearch = params.sSearch ? params.sSearch : null
        if (sSearch) {
            sSearch = CommonUtils.PERCENTAGE_SIGN + sSearch + CommonUtils.PERCENTAGE_SIGN
        }
        Long recordDateId =params.getLong('recordDateId')
        ClassName className
        if (params.className) {
            className = ClassName.read(params.getLong('className'))
        }
        Section sectionName
        if (params.sectionName) {
            sectionName = Section.read(params.getLong('sectionName'))
        }
        String sortColumn = CommonUtils.getSortColumn(sortColumnsStdAtt, iSortingCol)
        List dataReturns = new ArrayList()
        def c = AttnStdPresentView.createCriteria()
        def results = c.list(max: iDisplayLength, offset: iDisplayStart) {
            and {
                eq("recordDayId",recordDateId)
                if (className) {
                    eq("className",className.name)
                }
                if (sectionName) {
                    eq("sectionName",sectionName.name)
                }
            }
            if (sSearch) {
                or {
                    ilike('name', sSearch)
                    ilike('stdid', sSearch)
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
            results.each { AttnStdPresentView dailyRecord ->
                if (sSortDir.equals(CommonUtils.SORT_ORDER_ASC)) {
                    serial++
                } else {
                    serial--
                }
                dataReturns.add([DT_RowId: dailyRecord.objId, 0: serial, 1: dailyRecord.className+" - "+dailyRecord.sectionName, 2: dailyRecord.stdid, 3: dailyRecord.name, 4: dailyRecord.rollNo, 5: CommonUtils.getUiTimeStr12HrLocal(dailyRecord.inTime),6: CommonUtils.getUiTimeStr12HrLocal(dailyRecord.outTime), 7: dailyRecord.isLate? "Late": "Present", 8: ''])
            }
        }
        return [totalCount: totalCount, results: dataReturns]
    }

    def manualAttendanceList(Section section, AttnRecordDay record) {
        List dataReturns = new ArrayList()
        final session = sessionFactory.currentSession

        int attendanceCount = attendanceRecordCount(record, section.id)

        String query = "SELECT id, name, roll_no, studentid, IF((select id from attn_std_record atr where atr.record_day_id=${record.id} and atr.student_id=student.id) is null, 0, 1) attnStatus FROM student where active_status='ACTIVE' and student_status='NEW' and section_id = ${section.id} order by roll_no asc "
        if (attendanceCount == 0) {
            query = "SELECT id, name, roll_no, studentid, 1 as attnStatus FROM student where active_status='ACTIVE' and student_status='NEW' and section_id = ${section.id} order by roll_no asc "
        }

        final sqlQuery = session.createSQLQuery(query)
        final queryResults = sqlQuery.with {
            list()
        }

        queryResults.collect { resultRow ->
            dataReturns.add([id: resultRow[0], name: resultRow[1], rollNo: resultRow[2],studentid:resultRow[3], attnStatus: resultRow[4]])
        }
        return dataReturns
    }

    def studentAttendanceList(Section section , AttnRecordDay record) {
        def c = AttnStdRecord.createCriteria()
        def results = c.list() {
            and {
                eq("recordDay", record)
                eq("sectionId", section.id)
                projections {
                    property('studentId')
                }
            }
          }

        return results
    }
    def studentAttendanceList(AttnRecordDay record) {
        def c = AttnStdRecord.createCriteria()
        def results = c.list() {
            and {
                eq("recordDay", record)
                projections {
                    property('studentId')
                }
            }
        }

        return results
    }

    int attendanceRecordCount(AttnRecordDay record, Long sectionId){
        def c = AttnStdRecord.createCriteria()
        def count = c.list() {
            and {
                eq("recordDay", record)
                eq("sectionId", sectionId)
            }
            projections {
                count()
            }
        }
        return count[0]
    }
    int studentPresentCount(String studentNo, Date startDate, Date endDate){
        def c = AttnStdRecord.createCriteria()
        def count = c.list() {
            createAlias('recordDay', 'rd')
            and {
                eq("stdNo", studentNo)
                eq("rd.dayType", AttendanceType.Open_Day)
                eq("rd.activeStatus", ActiveStatus.ACTIVE)
                between("rd.recordDate", startDate, endDate)
            }
            projections {
                count()
            }
        }
        return count[0]
    }
    int numberOfAttnStudent(AttnRecordDay recordDay, String className, Gender gender = null){
        if (gender) {
            return AttnStdPresentView.countByRecordDayIdAndClassNameAndGender(recordDay.id, className, gender.key)
        } else {
            return AttnStdPresentView.countByRecordDayIdAndClassName(recordDay.id, className)
        }
    }

    int numberOfAttnStudentCount(AttnRecordDay recordDay, String className, Gender gender = null){
        if (gender) {
            return AttnStdPresentView.countByRecordDayIdAndClassNameAndGender(recordDay.id, className, gender.key)
        } else {
            return AttnStdPresentView.countByRecordDayIdAndClassName(recordDay.id, className)
        }
    }
    int numberOfTotalAttnStudent(AttnRecordDay recordDay, Gender gender = null){
        if (gender) {
            return AttnStdPresentView.countByRecordDayIdAndGender(recordDay.id, gender.key)
        } else {
            return AttnStdPresentView.countByRecordDayId(recordDay.id)
        }
    }
    int numberOfTotalAttnStudent(AttnRecordDay recordDay, Gender gender, Religion religion){
        return AttnStdPresentView.countByRecordDayIdAndGenderAndReligion(recordDay.id, gender.key, religion.key)
    }

}
