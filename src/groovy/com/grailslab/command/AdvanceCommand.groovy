package com.grailslab.command

import com.grailslab.enums.PaymentType
import com.grailslab.hr.Employee

/**
 * Created by Hasnat on 6/27/2015.
 */
@grails.validation.Validateable
class AdvanceCommand {
    Long id
    Employee employee
    Double amount
    Double monthlyPay
    Double paidAmount
    PaymentType paymentType
    Integer numOfInstallment
    Date loanDate
    static constraints = {
        id nullable: true
        monthlyPay nullable: true
        numOfInstallment nullable: true
    }
}
@grails.validation.Validateable
class PcCommand {
    Long id
    Employee employee
    Double amount
    Double monthlyPay
    Double paidAmount
    PaymentType paymentType
    Integer numOfInstallment
    Date pcDate
    static constraints = {
        id nullable: true
        numOfInstallment nullable: true
        monthlyPay nullable: true
    }
}
