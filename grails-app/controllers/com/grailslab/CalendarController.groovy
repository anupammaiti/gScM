package com.grailslab


import grails.converters.JSON

class CalendarController {
    def calenderService
    def springSecurityService
    def noticeBoardService
    def classNameService
    def classRoutineService

    def index() {
        render (view: '/open/calendar/index')
    }

    def notice() {
        def noticeList = noticeBoardService.noticeBoard()
        render(view: '/open/calendar/notice',model: [noticeList:noticeList])
    }
    def classRoutine() {
        def classNameList = classNameService.allClassNames()
        render(view: '/open/classRoutine', model: [classNameList: classNameList])
    }

    def routineEvents(){
        List publicUrlEvents = classRoutineService.getRoutineAsEvents(params)
        String output = publicUrlEvents as JSON
        render output
    }
    def publicEvents(){
        Date sStartFrom = CommonUtils.searchDateFormat(params.start, true)
        Date sStartEnd = CommonUtils.searchDateFormat(params.end, false)
        List publicUrlEvents = calenderService.getPublicEvents(sStartFrom, sStartEnd)
        String output = publicUrlEvents as JSON
        render output
    }
}
