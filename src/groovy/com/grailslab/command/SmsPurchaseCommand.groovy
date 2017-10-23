package com.grailslab.command

/**
 * Created by Grailslab on 1/23/2016.
 */
@grails.validation.Validateable
class SmsPurchaseCommand {
	Date billingDate
	Integer numOfSms
	Double smsPrice
	Date expireDate

	static constraints = {
	}
}
