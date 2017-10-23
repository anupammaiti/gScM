package com.grailslab

import com.grailslab.gschoolcore.AcademicYear
import grails.plugin.springsecurity.userdetails.GrailsUser
import org.springframework.security.core.GrantedAuthority

/**
 * Created by aminul on 1/25/2015.
 */
class MyUserDetails extends GrailsUser {
    final Long schoolId
    final String userRef
    final String name
    final AcademicYear academicYear
    MyUserDetails(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<GrantedAuthority> authorities, long id, Long schoolId, String userRef, String name) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities, id)
        this.schoolId = schoolId
        this.userRef = userRef
        this.name = name
        this.academicYear=AcademicYear.Y2017
    }
}
