package com.grailslab.accounts

import com.grailslab.gschoolcore.BasePersistentObj

class Glmst extends BasePersistentObj{
    String xacc
    String xdesc
    String xacctype
    String xaccusage
    String xaccsource
    String xaccgroup
    String xmsttype

    static constraints = {
        xdesc nullable: true
        xacctype nullable: true
        xaccusage nullable: true
        xaccsource nullable: true
        xaccgroup nullable: true
        xmsttype nullable: true
    }

}
