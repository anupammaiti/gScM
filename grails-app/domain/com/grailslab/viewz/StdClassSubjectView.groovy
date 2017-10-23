package com.grailslab.viewz

import com.grailslab.enums.SubjectType

class StdClassSubjectView {
    String className
    Long classNameId
    String groupName
    SubjectType subjectType
    Integer weightOnResult
    Integer passMark
    Boolean isExtracurricular
    Boolean isOtherActivity
    String alternativeSubIds
    String alternativeSubNames
    Boolean isCtExam
    Integer ctMark
    Integer ctEffMark
    Boolean isHallExam
    Integer hallMark
    Integer hallEffMark
    Boolean isHallPractical
    Integer hallWrittenMark
    Integer hallPracticalMark
    Integer hallObjectiveMark
    Integer hallSbaMark
    Integer hallInput5
    String subjectName
    Long subjectId
    Integer sortPosition
    String code

    Boolean isPassSeparately
    Integer writtenPassMark
    Integer practicalPassMark
    Integer objectivePassMark
    Integer sbaPassMark
    Integer input5PassMark

    static mapping = {
        table 'v_std_class_subject'
        version false
        cache usage:'read-only'
        className column:'class_name'
        classNameId column:'class_name_id'
        groupName column:'group_name'
        subjectType column:'subject_type'
        weightOnResult column:'weight_on_result'
        passMark column:'pass_mark'
        isExtracurricular column:'is_extracurricular'
        isOtherActivity column:'is_other_activity'
        alternativeSubIds column:'alternative_sub_ids'
        alternativeSubNames column:'alternative_sub_names'
        isCtExam column:'is_ct_exam'
        ctMark column:'ct_mark'
        ctEffMark column:'ct_eff_mark'
        isHallExam column:'is_hall_exam'
        hallMark column:'hall_mark'
        hallEffMark column:'hall_eff_mark'
        isHallPractical column:'is_hall_practical'
        hallWrittenMark column:'hall_written_mark'
        hallPracticalMark column:'hall_practical_mark'
        hallObjectiveMark column:'hall_objective_mark'
        hallSbaMark column:'hall_sba_mark'
        hallInput5 column:'hall_input5'
        subjectName column:'subject_name'
        subjectId column:'subject_id'
        sortPosition column:'sort_position'
        code column:'code'
        isPassSeparately column:'is_pass_separately'
        writtenPassMark column:'written_pass_mark'
        practicalPassMark column:'practical_pass_mark'
        objectivePassMark column:'objective_pass_mark'
        sbaPassMark column:'sba_pass_mark'
        input5PassMark column:'input5pass_mark'
    }

    static constraints = {
    }
}
