package com.grailslab.open

import com.grailslab.gschoolcore.BasePersistentObj


class FaqQuestion extends BasePersistentObj{

//    FAQCategory category
    String name
    String answers
    Integer sortPosition=1
    static belongsTo = [faqCategory :FaqCategory]
    static constraints = {
    }
    static mapping = {
        answers type: 'text'
    }
}
