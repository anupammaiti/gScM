package com.grailslab.bailyschool.uma

class Role {

	String authority

	static mapping = {
		cache true
	}

	static constraints = {
		authority blank: false, unique: true
	}
    enum AvailableRoles {
        SUPER_ADMIN("ROLE_SUPER_ADMIN"), SCHOOL_HEAD("ROLE_SCHOOL_HEAD"), ADMIN("ROLE_ADMIN"), TEACHER("ROLE_TEACHER"), STUDENT("ROLE_STUDENT"), PARENT("ROLE_PARENT"), ACCOUNTS("ROLE_ACCOUNTS"), HR("ROLE_HR"), LIBRARY("ROLE_LIBRARY"),APPLICANT("ROLE_APPLICANT"),SWITCH_USER("ROLE_SWITCH_USER"),ORGANIZER("ROLE_ORGANIZER")

        String roleId

        private AvailableRoles(String id) {
            this.roleId = id
        }

        String value() {
            roleId
        }
    }
}
