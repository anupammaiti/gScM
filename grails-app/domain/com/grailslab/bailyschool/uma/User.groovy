package com.grailslab.bailyschool.uma

import com.grailslab.enums.MainUserType

class User {

	transient springSecurityService

	String username
	String password
	boolean enabled = true
	boolean accountExpired
	boolean accountLocked
	boolean passwordExpired
    Long schoolId
    String userRef
    String name
    MainUserType mainUserType
    Date lastLogin


	static transients = ['springSecurityService']

	static constraints = {
		username blank: false, unique: true
		password blank: false
        userRef nullable: true
        name nullable: true
        mainUserType nullable: true
        lastLogin nullable: true
	}

	static mapping = {
		password column: '`password`'
	}

	Set<Role> getAuthorities() {
		UserRole.findAllByUser(this).collect { it.role }
	}

	def beforeInsert() {
		encodePassword()
	}

	def beforeUpdate() {
		if (isDirty('password')) {
			encodePassword()
		}
	}

	protected void encodePassword() {
		password = springSecurityService?.passwordEncoder ? springSecurityService.encodePassword(password) : password
	}
}
