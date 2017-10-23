package com.grailslab

class ScrollNoticeTagLib {
    static namespace = "notice"
    def noticeScrollService
    Closure scrollText = { attrs, body ->
        String scrollStr= noticeScrollService.scrollText()
        if(!scrollStr){
            scrollStr="<span style='color: #cccccc;'>No recent notice available</span>"
        }
        out << scrollStr
    }
}
