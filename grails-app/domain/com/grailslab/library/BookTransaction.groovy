package com.grailslab.library


import com.grailslab.enums.PayType
import com.grailslab.gschoolcore.BasePersistentObj
import com.grailslab.hr.Employee
import com.grailslab.stmgmt.Student

class BookTransaction extends BasePersistentObj{
    Student student
    Employee employee
    Book book
    Date issueDate
    Date returnDate
    Date submissionDate
    Double fine
    PayType payType =PayType.DUE
    PayType returnType =PayType.DUE

    static constraints = {
        fine nullable: true
        payType nullable: true
        submissionDate nullable: true
        student nullable: true
        employee nullable: true
    }
}
