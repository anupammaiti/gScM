package com.grailslab

import com.grailslab.bailyschool.uma.User
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.userdetails.GrailsUserDetailsService
import org.springframework.security.core.authority.GrantedAuthorityImpl
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException

/**
 * Created by aminul on 1/25/2015.
 */
class MyUserDetailsService implements GrailsUserDetailsService {

    static final List NO_ROLES = [new GrantedAuthorityImpl(SpringSecurityUtils.NO_ROLE)]

    UserDetails loadUserByUsername(String username, boolean loadRoles) throws UsernameNotFoundException { return loadUserByUsername(username) }

    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User.withTransaction { status ->

            User user = User.findByUsername(username)
            if (!user) throw new UsernameNotFoundException( "User not found $username", username)
            user.lastLogin=new Date()
            user.save()
            def authorities = user.authorities.collect {
                new GrantedAuthorityImpl(it.authority)
            }

            return new MyUserDetails(user.username, user.password, user.enabled,
                    !user.accountExpired, !user.passwordExpired,
                    !user.accountLocked, authorities ?: NO_ROLES, user.id,
                    user.schoolId, user.userRef, user.name)
        }
    }
}