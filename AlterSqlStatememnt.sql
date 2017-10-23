
/****
drop table open_class_routine;

************** executed in 2.6.2
alter table calender_event drop foreign key FK_rmi8gvkn05vbviar75sofk24t;
ALTER TABLE calender_event
  DROP COLUMN class_schedule_id;
drop table class_schedule_details;
drop table class_schedule;


***************** executed in 2.5.6
--other then baily school
drop table lesson_feedback_detail;
drop table lesson_feedback_average;
drop table lesson_feedback;
drop table lesson_details;
drop table lesson;
drop table lesson_week;

DROP TABLE IF EXISTS library_config;

ALTER TABLE class_subjects
  DROP COLUMN is_ct_practical,
  DROP COLUMN ct_written_mark,
  DROP  COLUMN ct_objective_mark,
  DROP COLUMN ct_sba_mark;

 ALTER TABLE exam_schedule
 DROP COLUMN is_ct_practical;

  ALTER TABLE exam_mark
  DROP COLUMN ct_objective_mark,
  DROP COLUMN ct_practical_mark,
  DROP  COLUMN ct_written_mark,
  DROP COLUMN ct_sba_mark;

***************** executed in 2.5.4
delete from user_role where user_id in (select id from user where main_user_type !='SuperAdmin' and user_ref not in (select empid from employee where active_status = "ACTIVE"))
delete from user where main_user_type !='SuperAdmin' and user_ref not in (select empid from employee where active_status = "ACTIVE");
delete from user_role where user_id in (select id from user where main_user_type !='SuperAdmin' and user_ref is null);
delete from user where main_user_type !='SuperAdmin' and user_ref is null;
UPDATE user SET password_expired=1 WHERE main_user_type !='SuperAdmin';

SELECT concat("update attn_emp_record set record_date ='",record_date,"' where record_day_id = ",id,";") attnRecord FROM attn_record_day where active_status="ACTIVE";
SELECT concat("update attn_std_record set record_date ='",record_date,"' where record_day_id = ",id,";") attnRecord FROM attn_record_day where active_status="ACTIVE";

***************** executed in 2.5.2
update registration set student_status="ADMISSION" where student_status="NEW" and id in (select distinct registration_id from student where active_status='ACTIVE' and student_status="NEW");
update registration set student_status="DELETED" where student_status not in ("ADMISSION");

***************** executed before 2.3.2

/*
  DROP TABLE IF EXISTS library_config;

  ALTER TABLE employee
  DROP COLUMN department_id,
  DROP COLUMN designation_id;

  desc chart_of_account
  SHOW INDEX FROM chart_of_account
  alter table chart_of_account drop index code;
  DROP INDEX UK_i2y6jwo7xeykvd9i17dcp826x ON chart_of_account
  alter table employee drop foreign key FK_fvanju2gyowte98s1drrw2g2s;

  update class_subjects set active_status = 'ACTIVE' where `active_status` is null;
  ALTER TABLE `class_subjects` CHANGE `hall_viva_mark` `hall_objective_mark` INT(11) NULL DEFAULT NULL;

	RENAME TABLE subject TO subject_name;
*/
