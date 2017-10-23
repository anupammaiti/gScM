package com.grailslab.settings

import com.grailslab.gschoolcore.BasePersistentObj
import com.grailslab.hr.Employee

class LessonDetails extends  BasePersistentObj{

    Employee employee
    String topics
    String homeWork
    Date examDate
    SubjectName subject
    Lesson lesson

    static belongsTo = [lesson:Lesson]

    static constraints = {
        examDate nullable: true
        homeWork nullable: true, maxSize: 2000
    }
    static mapping = {
        topics type: 'text'
    }
}
