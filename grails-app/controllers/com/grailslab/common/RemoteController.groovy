package com.grailslab.common

import com.grailslab.CommonUtils
import com.grailslab.attn.AttnRecordDay
import com.grailslab.command.ListExamClassCommand
import com.grailslab.command.ListSectionCommand
import com.grailslab.enums.ExamStatus
import com.grailslab.enums.ExamType
import com.grailslab.enums.ScheduleStatus
import com.grailslab.gschoolcore.AcademicYear
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.settings.ClassName
import com.grailslab.settings.Exam
import com.grailslab.settings.Section
import com.grailslab.settings.ShiftExam
import com.grailslab.stmgmt.Student
import grails.converters.JSON

class RemoteController {
	def studentService
	def attnStudentService
	def sectionService
	def messageSource
	def classNameService
	def shiftExamService
	def classSubjectsService
	def examService
	def examScheduleService
	def employeeService
	def onlineRegistrationService
	def bookStockService
	def libraryService
	def stdEmpService
	def lessonService
	def studentSubjectsService
	def accountingService
	def classRoutineService

	def studentTypeAheadList(){
		LinkedHashMap result = new LinkedHashMap()
		String outPut
		String sSearch = params.q
		AcademicYear academicYear
		if (params.academicYear){
			academicYear = AcademicYear.valueOf(params.academicYear)
		}
		def studentList = studentService.allStudentTypeAheadWithRollSectionDD(sSearch, academicYear)
		result.put('items', studentList)
		outPut = result as JSON
		render outPut
	}
	def studentTypeAheadListWithStdId(){
		LinkedHashMap result = new LinkedHashMap()
		String outPut
		String sSearch = params.q
		AcademicYear academicYear
		if (params.academicYear){
			academicYear = AcademicYear.valueOf(params.academicYear)
		}
		def studentList = studentService.allStudentTypeAheadListByStdId(sSearch, academicYear)
		result.put('items', studentList)
		outPut = result as JSON
		render outPut
	}

	def applicantTypeAheadList(){
		LinkedHashMap result = new LinkedHashMap()
		String outPut
		def studentList = onlineRegistrationService.typeAheadList(params)
		result.put('items', studentList)
		outPut = result as JSON
		render outPut
	}

	def studentEmployeeTypeAheadList(){
		LinkedHashMap result = new LinkedHashMap()
		String outPut
		String sSearch = params.q
		def stdEmpList = stdEmpService.getStdEmpTypeAheadList(sSearch)
		result.put('items', stdEmpList)
		outPut = result as JSON
		render outPut
	}
	def stdEmpGuardianTypeAheadList(){
		LinkedHashMap result = new LinkedHashMap()
		String outPut
		String sSearch = params.q
		def stdEmpList = stdEmpService.getLibraryMemberTypeAheadList(sSearch)
		result.put('items', stdEmpList)
		outPut = result as JSON
		render outPut
	}
	def bookDetailList(){
		LinkedHashMap result = new LinkedHashMap()
		String outPut
		def bookDetailsList = bookStockService.bookDetailsTypeAheadList(params)
		result.put('items', bookDetailsList)
		outPut = result as JSON
		render outPut
	}
	def publisherList(){
		LinkedHashMap result = new LinkedHashMap()
		String outPut
		def bookDetailsList = bookStockService.publisherTypeAheadList(params)
		result.put('items', bookDetailsList)
		outPut = result as JSON
		render outPut
	}
	def bookNameTypeAheadList(){
		LinkedHashMap result = new LinkedHashMap()
		String outPut
		def bookDetailsList = bookStockService.bookTypeAheadList(params)
		result.put('items', bookDetailsList)
		outPut = result as JSON
		render outPut
	}
	def bookIssueNameList(){
		LinkedHashMap result = new LinkedHashMap()
		String outPut
		def bookDetailsList = bookStockService.bookIssueTypeAheadList(params)
		result.put('items', bookDetailsList)
		outPut = result as JSON
		render outPut
	}
	def authorTypeAheadList(){
		LinkedHashMap result = new LinkedHashMap()
		String outPut
		def author = libraryService.authorTypeAheadList(params)
		result.put('items', author)
		outPut = result as JSON
		render outPut
	}
	def publisherTypeAheadList(){
		LinkedHashMap result = new LinkedHashMap()
		String outPut
		def author = libraryService.publisherTypeAheadList(params)
		result.put('items', author)
		outPut = result as JSON
		render outPut
	}


	def studentList(){
		LinkedHashMap result = new LinkedHashMap()
		String outPut
		def studentList = studentService.allStudentDDListTypeAheadForMessaging(params)
		result.put('items', studentList)
		outPut = result as JSON
		render outPut
	}



	def employeeWithDesignationList(){
		LinkedHashMap result = new LinkedHashMap()
		String outPut
		String sSearch = params.q
		def employeeList = employeeService.allEmployeeWithDesignationList(sSearch)
		result.put('items', employeeList)
		outPut = result as JSON
		render outPut
	}

	def employeeList(){
		LinkedHashMap result = new LinkedHashMap()
		String outPut
		String sSearch = params.q
		def employeeList = employeeService.allEmployeeWithList(sSearch)
		result.put('items', employeeList)
		outPut = result as JSON
		render outPut
	}

	def absenceStudentList(){
		LinkedHashMap result = new LinkedHashMap()
		String outPut
		//Section section= Section.read(params.getLong('sectionName'))
		AttnRecordDay attnRecordDay= AttnRecordDay.findByRecordDate(params.getDate("attendDate","dd/MM/yyyy"))
		def absenceStudentList = attnStudentService.studentAttendanceList(attnRecordDay) //attnStudentService.studentAbcAttendanceList(section,attnRecordDay)//
		String sSearch = params.q
		def studentList = studentService.allAbsenceStudent(sSearch,absenceStudentList)
		result.put('items', studentList)
		outPut = result as JSON
		render outPut
	}
	def voucherList(){
		LinkedHashMap result = new LinkedHashMap()
		String outPut
		String sSearch = params.q
		def voucherList = accountingService.allVoucherList(sSearch)
		result.put('items', voucherList)
		outPut = result as JSON
		render outPut
	}

	def listSection(ListSectionCommand command){
		if (!request.method.equals('POST')) {
			redirect(action: 'index')
			return
		}
		LinkedHashMap result = new LinkedHashMap()
		String outPut
		if (command.hasErrors()) {
			def errorList = command?.errors?.allErrors?.collect { messageSource.getMessage(it, null) }
			result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
			result.put(CommonUtils.MESSAGE, errorList?.join('\n'))
			outPut = result as JSON
			render outPut
			return
		}
		def sectionList
		if(command.className){
			Boolean isGroupAvailable = command.className.groupsAvailable
			if(params.groupName){
				sectionList = sectionService.classSectionsDDList(command.className, command.groupName, command.academicYear)
			} else {
				sectionList = sectionService.classSectionsDDList(command.className, command.academicYear)
			}

		}else {
			sectionList = sectionService.sectionDropDownList(command.academicYear)
		}

		if(!sectionList){
			result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
			result.put(CommonUtils.MESSAGE, "No Section added")
			outPut = result as JSON
			render outPut
			return
		}
		result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
		result.put('sectionList', sectionList)
		outPut = result as JSON
		render outPut
		return
	}
	def listClassNameDropDown(){
		if (!request.method.equals('POST')) {
			redirect(action: 'index')
			return
		}
		LinkedHashMap result = new LinkedHashMap()
		String outPut

		def classNameList = classNameService.classNameDropDownList();

		if(!classNameList){
			result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
			result.put(CommonUtils.MESSAGE, "No Class added")
			outPut = result as JSON
			render outPut
			return
		}
		result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
		result.put('classNameList', classNameList)
		outPut = result as JSON
		render outPut
		return
	}



	def classSectionList(ListSectionCommand command){
		if (!request.method.equals('POST')) {
			redirect(action: 'index')
			return
		}
		LinkedHashMap result = new LinkedHashMap()
		String outPut
		if (command.hasErrors()) {
			def errorList = command?.errors?.allErrors?.collect { messageSource.getMessage(it, null) }
			result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
			result.put(CommonUtils.MESSAGE, errorList?.join('\n'))
			outPut = result as JSON
			render outPut
			return
		}
		def sectionList = sectionService.classSectionsDDList(command.className, command.academicYear)

		if(!sectionList){
			result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
			result.put(CommonUtils.MESSAGE, "No Section added")
			outPut = result as JSON
			render outPut
			return
		}
		result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
		result.put('sectionList', sectionList)
		outPut = result as JSON
		render outPut
		return
	}

	def yearExamNameList(ListExamClassCommand command){
		if (!request.method.equals('POST')) {
			redirect(action: 'index')
			return
		}
		LinkedHashMap result = new LinkedHashMap()
		String outPut
		if (command.hasErrors()) {
			def errorList = command?.errors?.allErrors?.collect { messageSource.getMessage(it, null) }
			result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
			result.put(CommonUtils.MESSAGE, errorList?.join('\n'))
			outPut = result as JSON
			render outPut
			return
		}
		def examNameList = shiftExamService.examNameDropDown(command.academicYear)

		if(!examNameList){
			result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
			result.put(CommonUtils.MESSAGE, "No Exam added")
			outPut = result as JSON
			render outPut
			return
		}
		result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
		result.put('examNameList', examNameList)
		outPut = result as JSON
		render outPut
		return
	}
	//Class list for a given shift exam
	def examClassList(ListExamClassCommand command){
		if (!request.method.equals('POST')) {
			redirect(action: 'index')
			return
		}
		LinkedHashMap result = new LinkedHashMap()
		String outPut
		if (command.hasErrors()) {
			def errorList = command?.errors?.allErrors?.collect { messageSource.getMessage(it, null) }
			result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
			result.put(CommonUtils.MESSAGE, errorList?.join('\n'))
			outPut = result as JSON
			render outPut
			return
		}
		def classNameList = classNameService.classNameInIdListDD(command.examName?.classIds);

		if(!classNameList){
			result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
			result.put(CommonUtils.MESSAGE, "No Class added")
			outPut = result as JSON
			render outPut
			return
		}
		result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
		result.put('classNameList', classNameList)
		outPut = result as JSON
		render outPut
		return
	}
	def examAndClassList(ListExamClassCommand command){
		if (!request.method.equals('POST')) {
			redirect(action: 'index')
			return
		}
		LinkedHashMap result = new LinkedHashMap()
		String outPut
		if (command.hasErrors()) {
			def errorList = command?.errors?.allErrors?.collect { messageSource.getMessage(it, null) }
			result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
			result.put(CommonUtils.MESSAGE, errorList?.join('\n'))
			outPut = result as JSON
			render outPut
			return
		}
		def classNameList = classNameService.classNameDropDownList();
		def examNameList = shiftExamService.examNameDropDown(command.academicYear)

		if(!classNameList){
			result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
			result.put(CommonUtils.MESSAGE, "No Class added")
			outPut = result as JSON
			render outPut
			return
		}
		result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
		result.put('classNameList', classNameList)
		result.put('examNameList', examNameList)
		outPut = result as JSON
		render outPut
		return
	}

	//List all exam for a class given Shift exam and class name
	def sectionExamList(ListExamClassCommand command){
		if (!request.method.equals('POST')) {
			redirect(action: 'index')
			return
		}
		LinkedHashMap result = new LinkedHashMap()
		String outPut
		if (command.hasErrors()) {
			def errorList = command?.errors?.allErrors?.collect { messageSource.getMessage(it, null) }
			result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
			result.put(CommonUtils.MESSAGE, errorList?.join('\n'))
			outPut = result as JSON
			render outPut
			return
		}
		def sectionNameList = examService.classExamsDropDownAsSectionName(command.examName, command.className, command.groupName, command.examType);
		if(!sectionNameList){
			result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
			result.put(CommonUtils.MESSAGE, "No Section schedule created or published. Please contact admin")
			outPut = result as JSON
			render outPut
			return
		}
		result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
		result.put('sectionNameList', sectionNameList)
		outPut = result as JSON
		render outPut
		return
	}
	def sectionSubjectList(Long id){
		if (!request.method.equals('POST')) {
			redirect(action: 'index')
			return
		}
		LinkedHashMap result = new LinkedHashMap()
		String outPut
		Section section = Section.read(id)
		if (!section) {
			result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
			result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
			outPut = result as JSON
			render outPut
			return
		}
		def subjectList = classSubjectsService.subjectDropDownList(section.className, section.groupName);
		if(!subjectList){
			result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
			result.put(CommonUtils.MESSAGE, "No Subject added")
			outPut = result as JSON
			render outPut
			return
		}
		result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
		result.put('subjectList', subjectList)
		outPut = result as JSON
		render outPut
		return
	}
	def studentSubjectList(Long id){
		if (!request.method.equals('POST')) {
			redirect(action: 'index')
			return
		}
		LinkedHashMap result = new LinkedHashMap()
		String outPut
		Student student = Student.read(params.getLong("id"))
		if (!student) {
			result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
			result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
			outPut = result as JSON
			render outPut
			return
		}
		def subjectList = studentSubjectsService.studentSubjectDropDownList(student)
		if(!subjectList){
			result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
			result.put(CommonUtils.MESSAGE, "No Subject added")
			outPut = result as JSON
			render outPut
			return
		}
		result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
		result.put('subjectList', subjectList)
		outPut = result as JSON
		render outPut
		return
	}
	def lessonWeekList(Long id){
		if (!request.method.equals('POST')) {
			redirect(action: 'index')
			return
		}
		LinkedHashMap result = new LinkedHashMap()
		String outPut

		Section section
		Student student
		if (id) {
			section = Section.read(id)
		} else if (params.studentId) {
			student = Student.read(params.long('studentId'))
			section = student.section
		}
		if (!section) {
			result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
			result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
			outPut = result as JSON
			render outPut
			return
		}
		def lessonWeekList = lessonService.lessonWeekListForFeedback(section)
		result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
		result.put('lessonWeekList', lessonWeekList)
		outPut = result as JSON
		render outPut
		return
	}

	def examSubjectList(Long id) {
		if (!request.method.equals('POST')) {
			redirect(action: 'index')
			return
		}
		LinkedHashMap result = new LinkedHashMap()
		String outPut
		ShiftExam shiftExam= ShiftExam.read(id)
		Exam exam = Exam.read(params.getLong('exam'))
		if(!exam){
			result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
			result.put(CommonUtils.MESSAGE, CommonUtils.COMMON_NOT_FOUND_MESSAGE)
			outPut = result as JSON
			render outPut
			return
		}

		Section section = exam.section
		if (params.requiredNotNew) {
			if(exam.examStatus == ExamStatus.NEW){
				result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
				result.put(CommonUtils.MESSAGE, "${section.name} ${shiftExam.examName} tabulation yet not processed or published.")
				outPut = result as JSON
				render outPut
				return
			}
		} else {
			if (exam.examStatus != ExamStatus.NEW) {
				result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
				result.put(CommonUtils.MESSAGE, "${section.name} ${shiftExam.examName} tabulation processing or published. Not allow to entry mark.")
				outPut = result as JSON
				render outPut
				return
			}
		}

		ExamType examType = ExamType.valueOf(params.examType)
		def scheduleList
		if(examType==ExamType.CLASS_TEST){
			if(exam.ctSchedule!=ScheduleStatus.PUBLISHED){
				result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
				result.put(CommonUtils.MESSAGE, "Exam Schedule for ${section.name} ${shiftExam.examName} yet not added or published.")
				outPut = result as JSON
				render outPut
				return
			}
			scheduleList = examScheduleService.ctSchedulesDropDown(exam)
			if(!scheduleList){
				result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
				result.put(CommonUtils.MESSAGE, "Exam Schedule for ${section.name} ${shiftExam.examName} yet not added or published.")
				outPut = result as JSON
				render outPut
				return
			}
			result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
			result.put('scheduleList', scheduleList)
			outPut = result as JSON
			render outPut
			return
		} else if(examType==ExamType.HALL_TEST){
			if(exam.hallSchedule!=ScheduleStatus.PUBLISHED){
				result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
				result.put(CommonUtils.MESSAGE, "Exam Schedule for ${section.name} ${shiftExam.examName} yet not added or published.")
				outPut = result as JSON
				render outPut
				return
			}
			scheduleList = examScheduleService.hallSchedulesDropDown(exam)
			if(!scheduleList){
				result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
				result.put(CommonUtils.MESSAGE, "Exam Schedule for ${section.name} ${shiftExam.examName} yet not added or published.")
				outPut = result as JSON
				render outPut
				return
			}
			result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
			result.put('scheduleList', scheduleList)
			outPut = result as JSON
			render outPut
			return
		}
	}
	def loadModal(String modalName){
		render(view: modalName)
	}

	//Calendar Events with classRoutine
	def routineEvents(){
		List publicUrlEvents = classRoutineService.getRoutineAsEvents(params)
		String output = publicUrlEvents as JSON
		render output
	}
}
