package com.grailslab.messaging

import com.grailslab.enums.SmsUseType
import com.grailslab.gschoolcore.BasePersistentObj

class SmsBilling extends BasePersistentObj{

	Integer smsLength
	String message
	Integer numOfSender

	SmsUseType smsUseType = SmsUseType.SEND
	Date billingDate
	Integer numOfSms
	Integer smsBalance
	Double smsPrice
	Date expireDate
	String insertedSmsIds

	static constraints = {
		message(nullable: true, size: 1..479)
		smsLength nullable: true
		numOfSender nullable: true
		billingDate nullable: true
		numOfSms nullable: true
		smsBalance nullable: true
		smsPrice nullable: true
		expireDate nullable: true
		insertedSmsIds nullable: true
	}
}
