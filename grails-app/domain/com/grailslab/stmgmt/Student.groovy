package com.grailslab.stmgmt


import com.grailslab.enums.PromotionStatus
import com.grailslab.enums.ScholarshipType
import com.grailslab.enums.StudentStatus
import com.grailslab.enums.YearMonths
import com.grailslab.gschoolcore.BasePersistentObj
import com.grailslab.settings.ClassName
import com.grailslab.settings.Section

class Student extends BasePersistentObj{

    String name     //@todo-remove
    String studentID //@todo-remove

    Registration registration
    Section section
    ClassName className
    Integer rollNo
    Boolean scholarshipObtain = false
    Double scholarshipPercent = 0   //@todo-remove
    ScholarshipType scholarshipType //@todo-remove

    Integer positionInSection
    Integer positionInClass
    Boolean autoPromotion
    StudentStatus studentStatus = StudentStatus.NEW         //NEW - to a class, TC - in case of TcAndDropout Events, DELETED - delete from class and return to previous class
    PromotionStatus promotionStatus = PromotionStatus.NEW
    YearMonths admissionMonth = YearMonths.JANUARY



    static constraints = {
        autoPromotion nullable: true
        positionInSection nullable: true
        positionInClass nullable: true
        scholarshipObtain nullable: true
        scholarshipPercent nullable: true
        admissionMonth nullable: true
        scholarshipType nullable: true

    }
    static mapping = {
        promotionStatus defaultValue: PromotionStatus.NEW
        autoPromotion defaultValue: Boolean.FALSE
        sort rollNo: "asc" // or "desc"
    }
}
