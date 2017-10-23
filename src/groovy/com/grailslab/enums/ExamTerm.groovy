package com.grailslab.enums

/**
 * Created by Hasnat on 5/27/2014.
 */
public enum ExamTerm {
    FIRST_TERM("Term Exam"),
    SECOND_TERM("2nd Term"),
    HALF_YEARLY("Half Yearly"),
    FINAL_TEST("Final Exam"),

    final String value

    ExamTerm(String value) {
        this.value = value
    }
    static Collection<ExamTerm> type(){
        return [FIRST_TERM, SECOND_TERM, HALF_YEARLY, FINAL_TEST]
    }
    String toString() { value }
    String getKey() { name() }

}