package com.grailslab.accounts

import com.grailslab.gschoolcore.ActiveStatus

class GlHeader {
    String xvoucher
    String xref
    Date xdate
    Integer xyear
    Integer xper
    Boolean xpostFlag
    String xstatusjv
    String xtermgl
    String xnote
    String xaction
    ActiveStatus activeStatus = ActiveStatus.ACTIVE

    static constraints = {
        xref nullable: true
        xnote nullable: true
        xaction nullable: true
    }
}