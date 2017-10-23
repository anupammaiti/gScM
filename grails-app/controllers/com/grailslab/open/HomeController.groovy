package com.grailslab.open

import com.grailslab.CommonUtils
import com.grailslab.bailyschool.uma.Role
import com.grailslab.bailyschool.uma.User
import com.grailslab.bailyschool.uma.UserRole
import com.grailslab.command.ListSectionCommand
import com.grailslab.enums.HrKeyType
import com.grailslab.enums.OpenCont
import com.grailslab.enums.OpenContentType
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.hr.Employee
import com.grailslab.hr.HrCategory
import com.grailslab.hr.HrStaffCategory
import com.grailslab.settings.ClassName
import com.grailslab.settings.LessonWeek
import com.grailslab.settings.Section
import com.grailslab.settings.SubjectName
import grails.converters.JSON
import org.joda.time.DateTimeConstants
import org.joda.time.LocalDate

class HomeController {

    def employeeService
    def messageSource
    def galleryService
    def hrResourceService
    def grailsApplication
    def timeLineService
    def lessonService
    def classSubjectsService
    def classNameService
    def schoolService
    def sectionService

    def shiftExamService
    def examService
    def studentService
    def classRoutineService
    def quickLinkService

    def index() {
        def allHomeQuickLinks = quickLinkService.allHomeQuickLinks()
        def featureContent = OpenContent.findAllByOpenContentTypeAndActiveStatus(OpenContentType.Feature_Content,ActiveStatus.ACTIVE, [max: 4,sort: "sortOrder"])
        def homeVoice = OpenMgmtVoice.findAllByOpenContentTypeAndActiveStatus(OpenContentType.voice,ActiveStatus.ACTIVE, [max: 3,sort: "sortOrder"])
        def homeCarousel = OpenSliderImage.findAllByActiveStatus(ActiveStatus.ACTIVE, [max: 8])
        def successStory = OpenSuccessStory.findAllByActiveStatus(ActiveStatus.ACTIVE, [max: 20])
        def quickLink = OpenQuickLink.findAllByActiveStatus(ActiveStatus.ACTIVE, [max: 15,sort: "sortIndex"])
        render(view: '/open/home', model: [featureContent:featureContent,homeVoice:homeVoice,homeCarousel:homeCarousel,successStory:successStory,quickLink:quickLink, allHomeQuickLinks:allHomeQuickLinks])
    }

    def ourSchool() {
        def schoolContent = OpenContent.findAllByOpenContentTypeAndActiveStatus(OpenContentType.About_School,ActiveStatus.ACTIVE, [max: 1,sort: "sortOrder"])
        def facilityContent = OpenContent.findAllByOpenContentTypeAndActiveStatus(OpenContentType.School_Facilities,ActiveStatus.ACTIVE, [max: 1,sort: "sortOrder"])
        def homeCarousel = OpenSliderImage.findAllByActiveStatus(ActiveStatus.ACTIVE, [max: 8])
        render(view: '/open/aboutUs/ourSchool', model: [schoolContent: schoolContent, facilityContent: facilityContent,homeCarousel:homeCarousel])
    }

    def faq() {
        def faqList = FaqCategory.findAllByOpenContAndActiveStatus(OpenCont.HOME_FAQ,ActiveStatus.ACTIVE,[sort: "sortPosition"])
        render(view: '/open/aboutUs/faq', model: [faqList: faqList])
    }

    def link() {
       /* def linkList = FaqCategory.findAllByOpenContAndActiveStatus(OpenCont.HOME_LINKS,ActiveStatus.ACTIVE,[sort: "sortPosition"])*/
        def galleryQuickLinks = quickLinkService.allGalleryQuickLinks()
        render(view: '/open/gallery/links', model: [galleryQuickLinks:galleryQuickLinks])
    }

    def managingCommittee() {
        def managingCommittee = OpenMgmtVoice.findAllByOpenContentTypeAndActiveStatus(OpenContentType.Mgmt_Committee,ActiveStatus.ACTIVE, [max: 20,sort: "sortOrder"])
        render(view: '/open/aboutUs/managingCommittee', model: [managingCommittee: managingCommittee])
    }


    def resources() {
        def staffCategories = hrResourceService.getStaffCategories()

        HrStaffCategory loadCategory
        String keyType
        if(params.stype){
            keyType = params.stype
            loadCategory = HrStaffCategory.findByKeyName(keyType)
        }
        if(!loadCategory && staffCategories){
            loadCategory = staffCategories.first()
        }

        Employee headMaster
        Employee assistantHeadMaster
        List<HrCategory> hrCategoryList = HrCategory.findAllByHrKeyTypeInList([HrKeyType.AHM,HrKeyType.OSTAFF, HrKeyType.TEACHER, HrKeyType.SSTAFF] as List, [sort: "sortOrder"])

        List hrResource = new ArrayList()
        if(loadCategory){
            String staffType = "%${loadCategory.keyName}%"
            headMaster = Employee.findByActiveStatusAndHrStaffCategoryIlikeAndSortOrder(ActiveStatus.ACTIVE, staffType, 1, [sort: "sortOrder"])
            assistantHeadMaster = Employee.findByActiveStatusAndHrStaffCategoryIlikeAndSortOrder(ActiveStatus.ACTIVE, staffType, 2, [sort: "sortOrder"])
            def hrList
            for(hrCategory in hrCategoryList) {
                hrList = Employee.findAllByHrCategoryAndActiveStatusAndHrStaffCategoryIlikeAndSortOrderGreaterThan(hrCategory, ActiveStatus.ACTIVE, staffType, 2, [sort: "sortOrder"])
                if (hrList.size()> 0 ){
                    hrResource.add([hrCategory: hrCategory, hrList:hrList])
                }
            }
        }
        render(view: '/open/aboutUs/teacherStuff_multiList', model: [
                headMaster:headMaster, assistantHeadMaster:assistantHeadMaster,
                hrResource:hrResource, staffCategories: staffCategories,stype:loadCategory?.keyName, sname:loadCategory?.name
        ])
    }



    def timeline() {
        def timelineList = timeLineService.showTimeLine()
        render(view: '/open/timeLine/show', model: [timelineList: timelineList])
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

    def lessonPlan(Long id){
        Section section = Section.read(id)
        def classNameList = classNameService.classNameDropDownList()
        if (!section) {
            render(view: '/open/lessonPlan', model: [classNameList: classNameList])
            return
        }
        def lessonWeekList = lessonService.lessonWeeksList(section)
        Integer loadWeek = null
        if(params.weekNo){
            loadWeek = params.getInt("weekNo")
        } else {
            LocalDate toDay = new LocalDate()
            if(toDay.getDayOfWeek()==DateTimeConstants.FRIDAY){
                toDay = toDay.minusDays(1)
            }else if(toDay.getDayOfWeek()==DateTimeConstants.SATURDAY){
                toDay = toDay.minusDays(2)
            }
            LessonWeek lessonWeek=lessonService.getWeek(toDay.toDate())
            if (lessonWeek) {
                loadWeek = lessonWeek.weekNumber
            } else {
                def lastWeek = lessonWeekList?.first()
                if (lastWeek) {
                    loadWeek = lastWeek.id
                } else {
                    loadWeek = 1
                }
            }
        }
        SubjectName loadSubject = null
        if(params.subjectId){
            loadSubject = SubjectName.read(params.getLong('subjectId'))
        }
        ClassName className = section.className

        def sectionList = sectionService.sectionDropDownList()
        def subjectList = classSubjectsService.subjectDropDownList(className)
        def result = lessonService.lessonPaginateList(section, loadWeek)

        render(view: '/open/lessonPlan', model: [sectionList:sectionList,classNameList: classNameList, lessonList: result, subjectList: subjectList,loadSubject:loadSubject,lessonWeekList:lessonWeekList,loadWeek:loadWeek])
    }

    def save() {
        LinkedHashMap result = new LinkedHashMap()
        String outPut
        if (!request.method.equals('POST')) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, 'Invalid Request')
            outPut=result as JSON
            render outPut
            return
        }

        User user=new User()
        user.name=params.name
        user.username=params.username
        user.password=params.password
        user.userRef=params.username
        user.schoolId=10000
        def roleApplicant = Role.findByAuthority(Role.AvailableRoles.APPLICANT.value())

        User existUser=User.findByUsername(user.username)
        if (existUser) {

            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, 'User name Already exist')
            outPut=result as JSON
            render outPut
            return
        }

        if (user.hasErrors()) {
            def errorList = user?.errors?.allErrors?.collect{messageSource.getMessage(it,null)}
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE, errorList?.join('\n'))
            outPut=result as JSON
            render outPut
            return
        }

        if (!user.save()) {
            result.put(CommonUtils.IS_ERROR, Boolean.TRUE)
            result.put(CommonUtils.MESSAGE,'User Save Failed')
            outPut=result as JSON
            render outPut
            return
        }

        if (!UserRole.findByUserAndRole(user, roleApplicant)) {
            UserRole.create(user, roleApplicant, true)
        }

        result.put(CommonUtils.IS_ERROR, Boolean.FALSE)
        result.put(CommonUtils.MESSAGE,'User Save Successful Please Login')
        outPut=result as JSON
        render outPut
        return
    }
    def headingDetails(Long id){
        OpenContent openContent = OpenContent.read(id)
        if(!openContent){
           redirect(action: 'index')
            return
        }
        render(view: '/open/aboutUs/headingDetails',model: [openContent:openContent,])
    }

    def image(){
        List dataReturns=galleryService.imageList()
        render(view: '/open/gallery/image',model: [imageList:dataReturns])

    }

    def imageSlide(Long album){
        if (!album){
            redirect(action: 'image')
            return
        }
        OpenGallery pictureGallery=OpenGallery.read(album)
        if(!pictureGallery){
            render(view: '/open/gallery/imageSlide',model: [imageList:null,albumTitle:"Album Not Found"])
            return
        }
        String albumTitle=pictureGallery.name
        def imageList=OpenGalleryItem.findAllByOpenGalleryAndActiveStatus(pictureGallery, ActiveStatus.ACTIVE, [max: 20, sort: "sortOrder", order: "asc"])
        render(view: '/open/gallery/imageSlide',model: [imageList:imageList,albumTitle:albumTitle])
    }

    def video(){
        println params
        List dataReturns = galleryService.videoList()
        render(view: '/open/gallery/video',model: [videoList:dataReturns])

    }
    def videoSlide(Long album){
        if (!album){
            redirect(action: 'image')
            return
        }
        OpenGallery videoGallery=OpenGallery.read(album)
        if(!videoGallery){
            render(view: '/open/gallery/videoSlide',model: [imageList:null,albumTitle:"Album Not Found"])
            return
        }
        String albumTitle=videoGallery.name
        def videoList=OpenGalleryItem.findAllByOpenGalleryAndActiveStatus(videoGallery, ActiveStatus.ACTIVE, [max: 20, sort: "sortOrder", order: "asc"])
        render(view: '/open/gallery/videoSlide',model: [videoList:videoList,albumTitle:albumTitle])
    }
}
