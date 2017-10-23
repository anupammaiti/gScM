package com.grailslab

import com.grailslab.enums.LetterGrade
import com.grailslab.settings.ClassName
import com.grailslab.settings.GradePoint
import com.grailslab.settings.School

class GradePointService {
    static transactional = false

    GradePoint getByMark(Double percentageMark, ClassName className) {
        GradePoint gradePoint = GradePoint.findByClassNameAndFromMarkLessThanEqualsAndUpToMarkGreaterThanEquals(className, percentageMark, percentageMark)
        return gradePoint
    }
    LetterGrade getByPoint(Double gPoint) {
        LetterGrade letterGrade
        if(gPoint >= 5){
            letterGrade = LetterGrade.GRADE_A_PLUS
        }else if(gPoint>=4 && gPoint<5){
            letterGrade = LetterGrade.GRADE_A
        }else if(gPoint>=3 && gPoint<4){
            letterGrade = LetterGrade.GRADE_A_MINUS
        }else if(gPoint>=2 && gPoint<3){
            letterGrade = LetterGrade.GRADE_B
        }else if(gPoint>=1 && gPoint<2){
            letterGrade = LetterGrade.GRADE_C
        }else {
            letterGrade = LetterGrade.GRADE_F
        }
        return letterGrade
    }
}
