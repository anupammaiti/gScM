package com.grailslab.open

import com.grailslab.gschoolcore.BasePersistentObj

class OpenQuickLink extends BasePersistentObj{
    String displayName
    String urlType
    String linkUrl
    String iconClass
    Integer sortIndex = 1

    static constraints = {
        linkUrl url: true
    }
}
