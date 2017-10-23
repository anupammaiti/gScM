package com.grailslab.settings

import com.grailslab.gschoolcore.BasePersistentObj

class SubjectName extends BasePersistentObj{
	String name
	Integer sortPosition
	String description
	String code
	Boolean isAlternative = false
	Long parentId	//In case of Alternative Subject
	Boolean hasChild	//In case of Parent Subject that have child subjects


	static constraints = {
		description nullable: true
		code nullable: true
		isAlternative nullable: true
		hasChild nullable: true
		parentId nullable: true
	}
}
