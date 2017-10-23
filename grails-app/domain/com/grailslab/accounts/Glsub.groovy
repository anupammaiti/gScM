package com.grailslab.accounts

import com.grailslab.gschoolcore.BasePersistentObj

class Glsub extends BasePersistentObj{
    Long xaccId
    String  xsub
    String  xdesc

    static constraints = {
    }
}
