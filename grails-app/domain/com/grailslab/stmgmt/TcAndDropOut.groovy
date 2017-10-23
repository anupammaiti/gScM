package com.grailslab.stmgmt


import com.grailslab.enums.TcType
import com.grailslab.gschoolcore.BasePersistentObj

class TcAndDropOut extends  BasePersistentObj{

    Student student
    //for TC or DROP OUT student record
    TcType tcType       //Tc, Dropout, Defaulter, other
    String reason
    String schoolName
    Date releaseDate
    String releaseText

    static constraints = {
        tcType nullable: true
        reason nullable: true
        schoolName nullable: true
        releaseDate nullable: true
        releaseText nullable: true
    }
    static mapping = {
        releaseText type: 'text'
    }
}
