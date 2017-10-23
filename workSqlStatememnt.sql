SHOW INDEX FROM book_transaction;
alter table book_transaction drop foreign key FK_oa4xmxvvbbansrybqipb11s6l;
alter table book_transaction drop foreign key FK_uxcco9tuc5yi6ywkoigcbeeh;


update shift_exam set period_start='2016-01-01', period_end='2016-06-21' where id=2
update shift_exam set result_publish_on='2016-09-01' where id=1

select * from user where username='student'	//69
select * from role	//4
select * from user_role where user_id=69
delete from user_role where user_id=69;
delete from user where username='student';

select * from shift_exam where id = 7
update shift_exam set exam_name="Final Exam", class_ids="6,7,8,9,10,11,12,13" where id = 7

select * from class_name where section_id in (37,36,39,38,41,40,42,43,44,46,45,48,47)

select distinct class_name_id from section where id in (37,36,39,38,41,40,42,43,44,46,45,48,47)

update class_name set subject_group = 1 where id not in (1,2,3,4,5,6,7);
/*3. Insert grade point table */
delete from grade_point;
INSERT INTO `grade_point` ( `version`, `academic_year`, `active_status`, `created_by`, `credentials`, `date_created`, `from_mark`, `g_point`, `l_grade`, `later_grade`, `last_updated`, `school_id`, `up_to_mark`, `updated_by`, `class_name_id`) VALUES
(0, 'Y2014', NULL, 'system', 'Magnificent', '2015-03-06 16:45:16', 90, 5, 'GRADE_A_PLUS','A+', '2015-03-06 16:45:16', 10000, 100, NULL, 1),
(0, 'Y2014', NULL, 'system', 'Outstanding', '2015-03-06 16:45:16', 80, 4, 'GRADE_A', 'A','2015-03-06 16:45:16', 10000, 90, NULL, 1),
(0, 'Y2014', NULL, 'system', 'Very Good', '2015-03-06 16:45:16', 70, 3, 'GRADE_A_MINUS','A-', '2015-03-06 16:45:16', 10000, 80, NULL, 1),
(0, 'Y2014', NULL, 'system', 'Good', '2015-03-06 16:45:16', 60, 2, 'GRADE_B', 'B','2015-03-06 16:45:16', 10000, 70, NULL, 1),
(0, 'Y2014', NULL, 'system', 'Satisfactory', '2015-03-06 16:45:16', 40, 1, 'GRADE_C','C', '2015-03-06 16:45:16', 10000, 60, NULL, 1),
(0, 'Y2014', NULL, 'system', 'Unsuccessful', '2015-03-06 16:45:16', 0, 0, 'GRADE_F', 'F(Fail)','2015-03-06 16:45:16', 10000, 40, NULL, 1),


(0, 'Y2014', NULL, 'system', 'Magnificent', '2015-03-06 16:45:16', 90, 5, 'GRADE_A_PLUS','A+', '2015-03-06 16:45:16', 10000, 100, NULL, 2),
(0, 'Y2014', NULL, 'system', 'Outstanding', '2015-03-06 16:45:16', 80, 4, 'GRADE_A', 'A','2015-03-06 16:45:16', 10000, 90, NULL, 2),
(0, 'Y2014', NULL, 'system', 'Very Good', '2015-03-06 16:45:16', 70, 3, 'GRADE_A_MINUS','A-', '2015-03-06 16:45:16', 10000, 80, NULL, 2),
(0, 'Y2014', NULL, 'system', 'Good', '2015-03-06 16:45:16', 60, 2, 'GRADE_B', 'B','2015-03-06 16:45:16', 10000, 70, NULL, 2),
(0, 'Y2014', NULL, 'system', 'Satisfactory', '2015-03-06 16:45:16', 40, 1, 'GRADE_C','C', '2015-03-06 16:45:16', 10000, 60, NULL, 2),
(0, 'Y2014', NULL, 'system', 'Unsuccessful', '2015-03-06 16:45:16', 0, 0, 'GRADE_F', 'F(Fail)','2015-03-06 16:45:16', 10000, 40, NULL, 2),

(0, 'Y2014', NULL, 'system', 'Magnificent', '2015-03-06 16:45:16', 90, 5, 'GRADE_A_PLUS','A+', '2015-03-06 16:45:16', 10000, 100, NULL, 3),
(0, 'Y2014', NULL, 'system', 'Outstanding', '2015-03-06 16:45:16', 80, 4, 'GRADE_A', 'A','2015-03-06 16:45:16', 10000, 90, NULL, 3),
(0, 'Y2014', NULL, 'system', 'Very Good', '2015-03-06 16:45:16', 70, 3, 'GRADE_A_MINUS','A-', '2015-03-06 16:45:16', 10000, 80, NULL, 3),
(0, 'Y2014', NULL, 'system', 'Good', '2015-03-06 16:45:16', 60, 2, 'GRADE_B', 'B','2015-03-06 16:45:16', 10000, 70, NULL, 3),
(0, 'Y2014', NULL, 'system', 'Satisfactory', '2015-03-06 16:45:16', 40, 1, 'GRADE_C','C', '2015-03-06 16:45:16', 10000, 60, NULL, 3),
(0, 'Y2014', NULL, 'system', 'Unsuccessful', '2015-03-06 16:45:16', 0, 0, 'GRADE_F', 'F(Fail)','2015-03-06 16:45:16', 10000, 40, NULL, 3),

(0, 'Y2014', NULL, 'system', 'Magnificent', '2015-03-06 16:45:16', 90, 5, 'GRADE_A_PLUS','A+', '2015-03-06 16:45:16', 10000, 100, NULL, 4),
(0, 'Y2014', NULL, 'system', 'Outstanding', '2015-03-06 16:45:16', 80, 4, 'GRADE_A', 'A','2015-03-06 16:45:16', 10000, 90, NULL, 4),
(0, 'Y2014', NULL, 'system', 'Very Good', '2015-03-06 16:45:16', 70, 3, 'GRADE_A_MINUS','A-', '2015-03-06 16:45:16', 10000, 80, NULL, 4),
(0, 'Y2014', NULL, 'system', 'Good', '2015-03-06 16:45:16', 60, 2, 'GRADE_B', 'B','2015-03-06 16:45:16', 10000, 70, NULL, 4),
(0, 'Y2014', NULL, 'system', 'Satisfactory', '2015-03-06 16:45:16', 40, 1, 'GRADE_C','C', '2015-03-06 16:45:16', 10000, 60, NULL, 4),
(0, 'Y2014', NULL, 'system', 'Unsuccessful', '2015-03-06 16:45:16', 0, 0, 'GRADE_F', 'F(Fail)','2015-03-06 16:45:16', 10000, 40, NULL, 4),


(0, 'Y2014', NULL, 'system', 'Magnificent', '2015-03-06 16:45:16', 90, 5, 'GRADE_A_PLUS','A+', '2015-03-06 16:45:16', 10000, 100, NULL, 5),
(0, 'Y2014', NULL, 'system', 'Outstanding', '2015-03-06 16:45:16', 80, 4, 'GRADE_A', 'A','2015-03-06 16:45:16', 10000, 90, NULL, 5),
(0, 'Y2014', NULL, 'system', 'Very Good', '2015-03-06 16:45:16', 70, 3, 'GRADE_A_MINUS','A-', '2015-03-06 16:45:16', 10000, 80, NULL, 5),
(0, 'Y2014', NULL, 'system', 'Good', '2015-03-06 16:45:16', 60, 2, 'GRADE_B', 'B','2015-03-06 16:45:16', 10000, 70, NULL, 5),
(0, 'Y2014', NULL, 'system', 'Satisfactory', '2015-03-06 16:45:16', 40, 1, 'GRADE_C','C', '2015-03-06 16:45:16', 10000, 60, NULL, 5),
(0, 'Y2014', NULL, 'system', 'Unsuccessful', '2015-03-06 16:45:16', 0, 0, 'GRADE_F', 'F(Fail)','2015-03-06 16:45:16', 10000, 40, NULL, 5),


(0, 'Y2014', NULL, 'system', 'Magnificent', '2015-03-06 16:45:16', 90, 5, 'GRADE_A_PLUS','A+', '2015-03-06 16:45:16', 10000, 100, NULL, 6),
(0, 'Y2014', NULL, 'system', 'Outstanding', '2015-03-06 16:45:16', 80, 4, 'GRADE_A', 'A','2015-03-06 16:45:16', 10000, 90, NULL, 6),
(0, 'Y2014', NULL, 'system', 'Very Good', '2015-03-06 16:45:16', 70, 3, 'GRADE_A_MINUS','A-', '2015-03-06 16:45:16', 10000, 80, NULL, 6),
(0, 'Y2014', NULL, 'system', 'Good', '2015-03-06 16:45:16', 60, 2, 'GRADE_B', 'B','2015-03-06 16:45:16', 10000, 70, NULL, 6),
(0, 'Y2014', NULL, 'system', 'Satisfactory', '2015-03-06 16:45:16', 40, 1, 'GRADE_C','C', '2015-03-06 16:45:16', 10000, 60, NULL, 6),
(0, 'Y2014', NULL, 'system', 'Unsuccessful', '2015-03-06 16:45:16', 0, 0, 'GRADE_F', 'F(Fail)','2015-03-06 16:45:16', 10000, 40, NULL, 6),

(0, 'Y2014', NULL, 'system', 'Magnificent', '2015-03-06 16:45:16', 90, 5, 'GRADE_A_PLUS','A+', '2015-03-06 16:45:16', 10000, 100, NULL, 7),
(0, 'Y2014', NULL, 'system', 'Outstanding', '2015-03-06 16:45:16', 80, 4, 'GRADE_A', 'A','2015-03-06 16:45:16', 10000, 90, NULL, 7),
(0, 'Y2014', NULL, 'system', 'Very Good', '2015-03-06 16:45:16', 70, 3, 'GRADE_A_MINUS','A-', '2015-03-06 16:45:16', 10000, 80, NULL, 7),
(0, 'Y2014', NULL, 'system', 'Good', '2015-03-06 16:45:16', 60, 2, 'GRADE_B', 'B','2015-03-06 16:45:16', 10000, 70, NULL, 7),
(0, 'Y2014', NULL, 'system', 'Satisfactory', '2015-03-06 16:45:16', 40, 1, 'GRADE_C','C', '2015-03-06 16:45:16', 10000, 60, NULL, 7),
(0, 'Y2014', NULL, 'system', 'Unsuccessful', '2015-03-06 16:45:16', 0, 0, 'GRADE_F', 'F(Fail)','2015-03-06 16:45:16', 10000, 40, NULL, 7),


(0, 'Y2014', NULL, 'system', 'Magnificent', '2015-03-06 16:45:16', 90, 5, 'GRADE_A_PLUS','A+', '2015-03-06 16:45:16', 10000, 100, NULL, 8),
(0, 'Y2014', NULL, 'system', 'Outstanding', '2015-03-06 16:45:16', 80, 4, 'GRADE_A', 'A','2015-03-06 16:45:16', 10000, 90, NULL, 8),
(0, 'Y2014', NULL, 'system', 'Very Good', '2015-03-06 16:45:16', 70, 3, 'GRADE_A_MINUS','A-', '2015-03-06 16:45:16', 10000, 80, NULL, 8),
(0, 'Y2014', NULL, 'system', 'Good', '2015-03-06 16:45:16', 60, 2, 'GRADE_B', 'B','2015-03-06 16:45:16', 10000, 70, NULL, 8),
(0, 'Y2014', NULL, 'system', 'Satisfactory', '2015-03-06 16:45:16', 40, 1, 'GRADE_C','C', '2015-03-06 16:45:16', 10000, 60, NULL, 8),
(0, 'Y2014', NULL, 'system', 'Unsuccessful', '2015-03-06 16:45:16', 0, 0, 'GRADE_F', 'F(Fail)','2015-03-06 16:45:16', 10000, 40, NULL, 8),

(0, 'Y2014', NULL, 'system', 'Magnificent', '2015-03-06 16:45:16', 90, 5, 'GRADE_A_PLUS','A+', '2015-03-06 16:45:16', 10000, 100, NULL, 9),
(0, 'Y2014', NULL, 'system', 'Outstanding', '2015-03-06 16:45:16', 80, 4, 'GRADE_A', 'A','2015-03-06 16:45:16', 10000, 90, NULL, 9),
(0, 'Y2014', NULL, 'system', 'Very Good', '2015-03-06 16:45:16', 70, 3, 'GRADE_A_MINUS','A-', '2015-03-06 16:45:16', 10000, 80, NULL, 9),
(0, 'Y2014', NULL, 'system', 'Good', '2015-03-06 16:45:16', 60, 2, 'GRADE_B', 'B','2015-03-06 16:45:16', 10000, 70, NULL, 9),
(0, 'Y2014', NULL, 'system', 'Satisfactory', '2015-03-06 16:45:16', 40, 1, 'GRADE_C','C', '2015-03-06 16:45:16', 10000, 60, NULL, 9),
(0, 'Y2014', NULL, 'system', 'Unsuccessful', '2015-03-06 16:45:16', 0, 0, 'GRADE_F', 'F(Fail)','2015-03-06 16:45:16', 10000, 40, NULL, 9),

(0, 'Y2014', NULL, 'system', 'Magnificent', '2015-03-06 16:45:16', 80, 5, 'GRADE_A_PLUS','A+', '2015-03-06 16:45:16', 10000, 100, NULL, 10),
(0, 'Y2014', NULL, 'system', 'Outstanding', '2015-03-06 16:45:16', 70, 4, 'GRADE_A', 'A','2015-03-06 16:45:16', 10000, 80, NULL, 10),
(0, 'Y2014', NULL, 'system', 'Very Good', '2015-03-06 16:45:16', 60, 3, 'GRADE_A_MINUS','A-', '2015-03-06 16:45:16', 10000, 70, NULL, 10),
(0, 'Y2014', NULL, 'system', 'Good', '2015-03-06 16:45:16', 50, 2, 'GRADE_B', 'B','2015-03-06 16:45:16', 10000, 60, NULL, 10),
(0, 'Y2014', NULL, 'system', 'Satisfactory', '2015-03-06 16:45:16', 33, 1, 'GRADE_C','C', '2015-03-06 16:45:16', 10000, 50, NULL, 10),
(0, 'Y2014', NULL, 'system', 'Unsuccessful', '2015-03-06 16:45:16', 0, 0, 'GRADE_F', 'F(Fail)','2015-03-06 16:45:16', 10000, 33, NULL, 10),


(0, 'Y2014', NULL, 'system', 'Magnificent', '2015-03-06 16:45:16', 90, 5, 'GRADE_A_PLUS','A+', '2015-03-06 16:45:16', 10000, 100, NULL, 11),
(0, 'Y2014', NULL, 'system', 'Outstanding', '2015-03-06 16:45:16', 80, 4, 'GRADE_A', 'A','2015-03-06 16:45:16', 10000, 90, NULL, 11),
(0, 'Y2014', NULL, 'system', 'Very Good', '2015-03-06 16:45:16', 70, 3, 'GRADE_A_MINUS','A-', '2015-03-06 16:45:16', 10000, 80, NULL, 11),
(0, 'Y2014', NULL, 'system', 'Good', '2015-03-06 16:45:16', 60, 2, 'GRADE_B', 'B','2015-03-06 16:45:16', 10000, 70, NULL, 11),
(0, 'Y2014', NULL, 'system', 'Satisfactory', '2015-03-06 16:45:16', 40, 1, 'GRADE_C','C', '2015-03-06 16:45:16', 10000, 60, NULL, 11),
(0, 'Y2014', NULL, 'system', 'Unsuccessful', '2015-03-06 16:45:16', 0, 0, 'GRADE_F', 'F(Fail)','2015-03-06 16:45:16', 10000, 40, NULL, 11),

(0, 'Y2014', NULL, 'system', 'Magnificent', '2015-03-06 16:45:16', 80, 5, 'GRADE_A_PLUS','A+', '2015-03-06 16:45:16', 10000, 100, NULL, 12),
(0, 'Y2014', NULL, 'system', 'Outstanding', '2015-03-06 16:45:16', 70, 4, 'GRADE_A', 'A','2015-03-06 16:45:16', 10000, 80, NULL, 12),
(0, 'Y2014', NULL, 'system', 'Very Good', '2015-03-06 16:45:16', 60, 3, 'GRADE_A_MINUS','A-', '2015-03-06 16:45:16', 10000, 70, NULL, 12),
(0, 'Y2014', NULL, 'system', 'Good', '2015-03-06 16:45:16', 50, 2, 'GRADE_B', 'B','2015-03-06 16:45:16', 10000, 60, NULL, 12),
(0, 'Y2014', NULL, 'system', 'Satisfactory', '2015-03-06 16:45:16', 33, 1, 'GRADE_C','C', '2015-03-06 16:45:16', 10000, 50, NULL, 12),
(0, 'Y2014', NULL, 'system', 'Unsuccessful', '2015-03-06 16:45:16', 0, 0, 'GRADE_F', 'F(Fail)','2015-03-06 16:45:16', 10000, 33, NULL, 12);

/* nhs grading same for all class*/
delete from grade_point;
INSERT INTO `grade_point` ( `version`, `academic_year`, `active_status`, `created_by`, `credentials`, `date_created`, `from_mark`, `g_point`, `l_grade`, `later_grade`, `last_updated`, `school_id`, `up_to_mark`, `updated_by`, `class_name_id`) VALUES
(0, 'Y2014', NULL, 'system', 'Magnificent', '2015-03-06 16:45:16', 80, 5, 'GRADE_A_PLUS','A+', '2015-03-06 16:45:16', 10000, 100, NULL, 1),
(0, 'Y2014', NULL, 'system', 'Outstanding', '2015-03-06 16:45:16', 70, 4, 'GRADE_A', 'A','2015-03-06 16:45:16', 10000, 80, NULL, 1),
(0, 'Y2014', NULL, 'system', 'Very Good', '2015-03-06 16:45:16', 60, 3.5, 'GRADE_A_MINUS','A-', '2015-03-06 16:45:16', 10000, 70, NULL, 1),
(0, 'Y2014', NULL, 'system', 'Good', '2015-03-06 16:45:16', 50, 3, 'GRADE_B', 'B','2015-03-06 16:45:16', 10000, 60, NULL, 1),
(0, 'Y2014', NULL, 'system', 'Satisfactory', '2015-03-06 16:45:16', 40, 2, 'GRADE_C','C', '2015-03-06 16:45:16', 10000, 50, NULL, 1),
(0, 'Y2014', NULL, 'system', 'Unsuccessful', '2015-03-06 16:45:16', 0, 0, 'GRADE_F', 'F(Fail)','2015-03-06 16:45:16', 10000, 40, NULL, 1),

(0, 'Y2014', NULL, 'system', 'Magnificent', '2015-03-06 16:45:16', 80, 5, 'GRADE_A_PLUS','A+', '2015-03-06 16:45:16', 10000, 100, NULL, 2),
(0, 'Y2014', NULL, 'system', 'Outstanding', '2015-03-06 16:45:16', 70, 4, 'GRADE_A', 'A','2015-03-06 16:45:16', 10000, 80, NULL, 2),
(0, 'Y2014', NULL, 'system', 'Very Good', '2015-03-06 16:45:16', 60, 3.5, 'GRADE_A_MINUS','A-', '2015-03-06 16:45:16', 10000, 70, NULL, 2),
(0, 'Y2014', NULL, 'system', 'Good', '2015-03-06 16:45:16', 50, 3, 'GRADE_B', 'B','2015-03-06 16:45:16', 10000, 60, NULL, 2),
(0, 'Y2014', NULL, 'system', 'Satisfactory', '2015-03-06 16:45:16', 40, 2, 'GRADE_C','C', '2015-03-06 16:45:16', 10000, 50, NULL, 2),
(0, 'Y2014', NULL, 'system', 'Unsuccessful', '2015-03-06 16:45:16', 0, 0, 'GRADE_F', 'F(Fail)','2015-03-06 16:45:16', 10000, 40, NULL, 2),

(0, 'Y2014', NULL, 'system', 'Magnificent', '2015-03-06 16:45:16', 80, 5, 'GRADE_A_PLUS','A+', '2015-03-06 16:45:16', 10000, 100, NULL, 3),
(0, 'Y2014', NULL, 'system', 'Outstanding', '2015-03-06 16:45:16', 70, 4, 'GRADE_A', 'A','2015-03-06 16:45:16', 10000, 80, NULL, 3),
(0, 'Y2014', NULL, 'system', 'Very Good', '2015-03-06 16:45:16', 60, 3.5, 'GRADE_A_MINUS','A-', '2015-03-06 16:45:16', 10000, 70, NULL, 3),
(0, 'Y2014', NULL, 'system', 'Good', '2015-03-06 16:45:16', 50, 3, 'GRADE_B', 'B','2015-03-06 16:45:16', 10000, 60, NULL, 3),
(0, 'Y2014', NULL, 'system', 'Satisfactory', '2015-03-06 16:45:16', 40, 2, 'GRADE_C','C', '2015-03-06 16:45:16', 10000, 50, NULL, 3),
(0, 'Y2014', NULL, 'system', 'Unsuccessful', '2015-03-06 16:45:16', 0, 0, 'GRADE_F', 'F(Fail)','2015-03-06 16:45:16', 10000, 40, NULL, 3),

(0, 'Y2014', NULL, 'system', 'Magnificent', '2015-03-06 16:45:16', 80, 5, 'GRADE_A_PLUS','A+', '2015-03-06 16:45:16', 10000, 100, NULL, 4),
(0, 'Y2014', NULL, 'system', 'Outstanding', '2015-03-06 16:45:16', 70, 4, 'GRADE_A', 'A','2015-03-06 16:45:16', 10000, 80, NULL, 4),
(0, 'Y2014', NULL, 'system', 'Very Good', '2015-03-06 16:45:16', 60, 3.5, 'GRADE_A_MINUS','A-', '2015-03-06 16:45:16', 10000, 70, NULL, 4),
(0, 'Y2014', NULL, 'system', 'Good', '2015-03-06 16:45:16', 50, 3, 'GRADE_B', 'B','2015-03-06 16:45:16', 10000, 60, NULL, 4),
(0, 'Y2014', NULL, 'system', 'Satisfactory', '2015-03-06 16:45:16', 40, 2, 'GRADE_C','C', '2015-03-06 16:45:16', 10000, 50, NULL, 4),
(0, 'Y2014', NULL, 'system', 'Unsuccessful', '2015-03-06 16:45:16', 0, 0, 'GRADE_F', 'F(Fail)','2015-03-06 16:45:16', 10000, 40, NULL, 4),

(0, 'Y2014', NULL, 'system', 'Magnificent', '2015-03-06 16:45:16', 80, 5, 'GRADE_A_PLUS','A+', '2015-03-06 16:45:16', 10000, 100, NULL, 5),
(0, 'Y2014', NULL, 'system', 'Outstanding', '2015-03-06 16:45:16', 70, 4, 'GRADE_A', 'A','2015-03-06 16:45:16', 10000, 80, NULL, 5),
(0, 'Y2014', NULL, 'system', 'Very Good', '2015-03-06 16:45:16', 60, 3.5, 'GRADE_A_MINUS','A-', '2015-03-06 16:45:16', 10000, 70, NULL, 5),
(0, 'Y2014', NULL, 'system', 'Good', '2015-03-06 16:45:16', 50, 3, 'GRADE_B', 'B','2015-03-06 16:45:16', 10000, 60, NULL, 5),
(0, 'Y2014', NULL, 'system', 'Satisfactory', '2015-03-06 16:45:16', 40, 2, 'GRADE_C','C', '2015-03-06 16:45:16', 10000, 50, NULL, 5),
(0, 'Y2014', NULL, 'system', 'Unsuccessful', '2015-03-06 16:45:16', 0, 0, 'GRADE_F', 'F(Fail)','2015-03-06 16:45:16', 10000, 40, NULL, 5),

(0, 'Y2014', NULL, 'system', 'Magnificent', '2015-03-06 16:45:16', 80, 5, 'GRADE_A_PLUS','A+', '2015-03-06 16:45:16', 10000, 100, NULL, 6),
(0, 'Y2014', NULL, 'system', 'Outstanding', '2015-03-06 16:45:16', 70, 4, 'GRADE_A', 'A','2015-03-06 16:45:16', 10000, 80, NULL, 6),
(0, 'Y2014', NULL, 'system', 'Very Good', '2015-03-06 16:45:16', 60, 3.5, 'GRADE_A_MINUS','A-', '2015-03-06 16:45:16', 10000, 70, NULL, 6),
(0, 'Y2014', NULL, 'system', 'Good', '2015-03-06 16:45:16', 50, 3, 'GRADE_B', 'B','2015-03-06 16:45:16', 10000, 60, NULL, 6),
(0, 'Y2014', NULL, 'system', 'Satisfactory', '2015-03-06 16:45:16', 40, 2, 'GRADE_C','C', '2015-03-06 16:45:16', 10000, 50, NULL, 6),
(0, 'Y2014', NULL, 'system', 'Unsuccessful', '2015-03-06 16:45:16', 0, 0, 'GRADE_F', 'F(Fail)','2015-03-06 16:45:16', 10000, 40, NULL, 6),


(0, 'Y2014', NULL, 'system', 'Magnificent', '2015-03-06 16:45:16', 80, 5, 'GRADE_A_PLUS','A+', '2015-03-06 16:45:16', 10000, 100, NULL, 7),
(0, 'Y2014', NULL, 'system', 'Outstanding', '2015-03-06 16:45:16', 70, 4, 'GRADE_A', 'A','2015-03-06 16:45:16', 10000, 80, NULL, 7),
(0, 'Y2014', NULL, 'system', 'Very Good', '2015-03-06 16:45:16', 60, 3.5, 'GRADE_A_MINUS','A-', '2015-03-06 16:45:16', 10000, 70, NULL, 7),
(0, 'Y2014', NULL, 'system', 'Good', '2015-03-06 16:45:16', 50, 3, 'GRADE_B', 'B','2015-03-06 16:45:16', 10000, 60, NULL, 7),
(0, 'Y2014', NULL, 'system', 'Satisfactory', '2015-03-06 16:45:16', 40, 2, 'GRADE_C','C', '2015-03-06 16:45:16', 10000, 50, NULL, 7),
(0, 'Y2014', NULL, 'system', 'Unsuccessful', '2015-03-06 16:45:16', 0, 0, 'GRADE_F', 'F(Fail)','2015-03-06 16:45:16', 10000, 40, NULL, 7),


(0, 'Y2014', NULL, 'system', 'Magnificent', '2015-03-06 16:45:16', 80, 5, 'GRADE_A_PLUS','A+', '2015-03-06 16:45:16', 10000, 100, NULL, 8),
(0, 'Y2014', NULL, 'system', 'Outstanding', '2015-03-06 16:45:16', 70, 4, 'GRADE_A', 'A','2015-03-06 16:45:16', 10000, 80, NULL, 8),
(0, 'Y2014', NULL, 'system', 'Very Good', '2015-03-06 16:45:16', 60, 3.5, 'GRADE_A_MINUS','A-', '2015-03-06 16:45:16', 10000, 70, NULL, 8),
(0, 'Y2014', NULL, 'system', 'Good', '2015-03-06 16:45:16', 50, 3, 'GRADE_B', 'B','2015-03-06 16:45:16', 10000, 60, NULL, 8),
(0, 'Y2014', NULL, 'system', 'Satisfactory', '2015-03-06 16:45:16', 40, 2, 'GRADE_C','C', '2015-03-06 16:45:16', 10000, 50, NULL, 8),
(0, 'Y2014', NULL, 'system', 'Unsuccessful', '2015-03-06 16:45:16', 0, 0, 'GRADE_F', 'F(Fail)','2015-03-06 16:45:16', 10000, 40, NULL, 8),

(0, 'Y2014', NULL, 'system', 'Magnificent', '2015-03-06 16:45:16', 80, 5, 'GRADE_A_PLUS','A+', '2015-03-06 16:45:16', 10000, 100, NULL, 9),
(0, 'Y2014', NULL, 'system', 'Outstanding', '2015-03-06 16:45:16', 70, 4, 'GRADE_A', 'A','2015-03-06 16:45:16', 10000, 80, NULL, 9),
(0, 'Y2014', NULL, 'system', 'Very Good', '2015-03-06 16:45:16', 60, 3.5, 'GRADE_A_MINUS','A-', '2015-03-06 16:45:16', 10000, 70, NULL, 9),
(0, 'Y2014', NULL, 'system', 'Good', '2015-03-06 16:45:16', 50, 3, 'GRADE_B', 'B','2015-03-06 16:45:16', 10000, 60, NULL, 9),
(0, 'Y2014', NULL, 'system', 'Satisfactory', '2015-03-06 16:45:16', 40, 2, 'GRADE_C','C', '2015-03-06 16:45:16', 10000, 50, NULL, 9),
(0, 'Y2014', NULL, 'system', 'Unsuccessful', '2015-03-06 16:45:16', 0, 0, 'GRADE_F', 'F(Fail)','2015-03-06 16:45:16', 10000, 40, NULL, 9),

(0, 'Y2014', NULL, 'system', 'Magnificent', '2015-03-06 16:45:16', 80, 5, 'GRADE_A_PLUS','A+', '2015-03-06 16:45:16', 10000, 100, NULL, 10),
(0, 'Y2014', NULL, 'system', 'Outstanding', '2015-03-06 16:45:16', 70, 4, 'GRADE_A', 'A','2015-03-06 16:45:16', 10000, 80, NULL, 10),
(0, 'Y2014', NULL, 'system', 'Very Good', '2015-03-06 16:45:16', 60, 3.5, 'GRADE_A_MINUS','A-', '2015-03-06 16:45:16', 10000, 70, NULL, 10),
(0, 'Y2014', NULL, 'system', 'Good', '2015-03-06 16:45:16', 50, 3, 'GRADE_B', 'B','2015-03-06 16:45:16', 10000, 60, NULL, 10),
(0, 'Y2014', NULL, 'system', 'Satisfactory', '2015-03-06 16:45:16', 40, 2, 'GRADE_C','C', '2015-03-06 16:45:16', 10000, 50, NULL, 10),
(0, 'Y2014', NULL, 'system', 'Unsuccessful', '2015-03-06 16:45:16', 0, 0, 'GRADE_F', 'F(Fail)','2015-03-06 16:45:16', 10000, 40, NULL, 10),


(0, 'Y2014', NULL, 'system', 'Magnificent', '2015-03-06 16:45:16', 80, 5, 'GRADE_A_PLUS','A+', '2015-03-06 16:45:16', 10000, 100, NULL, 11),
(0, 'Y2014', NULL, 'system', 'Outstanding', '2015-03-06 16:45:16', 70, 4, 'GRADE_A', 'A','2015-03-06 16:45:16', 10000, 80, NULL, 11),
(0, 'Y2014', NULL, 'system', 'Very Good', '2015-03-06 16:45:16', 60, 3.5, 'GRADE_A_MINUS','A-', '2015-03-06 16:45:16', 10000, 70, NULL, 11),
(0, 'Y2014', NULL, 'system', 'Good', '2015-03-06 16:45:16', 50, 3, 'GRADE_B', 'B','2015-03-06 16:45:16', 10000, 60, NULL, 11),
(0, 'Y2014', NULL, 'system', 'Satisfactory', '2015-03-06 16:45:16', 40, 2, 'GRADE_C','C', '2015-03-06 16:45:16', 10000, 50, NULL, 11),
(0, 'Y2014', NULL, 'system', 'Unsuccessful', '2015-03-06 16:45:16', 0, 0, 'GRADE_F', 'F(Fail)','2015-03-06 16:45:16', 10000, 40, NULL, 11),


(0, 'Y2014', NULL, 'system', 'Magnificent', '2015-03-06 16:45:16', 80, 5, 'GRADE_A_PLUS','A+', '2015-03-06 16:45:16', 10000, 100, NULL, 12),
(0, 'Y2014', NULL, 'system', 'Outstanding', '2015-03-06 16:45:16', 70, 4, 'GRADE_A', 'A','2015-03-06 16:45:16', 10000, 80, NULL, 12),
(0, 'Y2014', NULL, 'system', 'Very Good', '2015-03-06 16:45:16', 60, 3.5, 'GRADE_A_MINUS','A-', '2015-03-06 16:45:16', 10000, 70, NULL, 12),
(0, 'Y2014', NULL, 'system', 'Good', '2015-03-06 16:45:16', 50, 3, 'GRADE_B', 'B','2015-03-06 16:45:16', 10000, 60, NULL, 12),
(0, 'Y2014', NULL, 'system', 'Satisfactory', '2015-03-06 16:45:16', 40, 2, 'GRADE_C','C', '2015-03-06 16:45:16', 10000, 50, NULL, 12),
(0, 'Y2014', NULL, 'system', 'Unsuccessful', '2015-03-06 16:45:16', 0, 0, 'GRADE_F', 'F(Fail)','2015-03-06 16:45:16', 10000, 40, NULL, 12),

(0, 'Y2014', NULL, 'system', 'Magnificent', '2015-03-06 16:45:16', 80, 5, 'GRADE_A_PLUS','A+', '2015-03-06 16:45:16', 10000, 100, NULL, 13),
(0, 'Y2014', NULL, 'system', 'Outstanding', '2015-03-06 16:45:16', 70, 4, 'GRADE_A', 'A','2015-03-06 16:45:16', 10000, 80, NULL, 13),
(0, 'Y2014', NULL, 'system', 'Very Good', '2015-03-06 16:45:16', 60, 3.5, 'GRADE_A_MINUS','A-', '2015-03-06 16:45:16', 10000, 70, NULL, 13),
(0, 'Y2014', NULL, 'system', 'Good', '2015-03-06 16:45:16', 50, 3, 'GRADE_B', 'B','2015-03-06 16:45:16', 10000, 60, NULL, 13),
(0, 'Y2014', NULL, 'system', 'Satisfactory', '2015-03-06 16:45:16', 40, 2, 'GRADE_C','C', '2015-03-06 16:45:16', 10000, 50, NULL, 13),
(0, 'Y2014', NULL, 'system', 'Unsuccessful', '2015-03-06 16:45:16', 0, 0, 'GRADE_F', 'F(Fail)','2015-03-06 16:45:16', 10000, 40, NULL, 13),

(0, 'Y2014', NULL, 'system', 'Magnificent', '2015-03-06 16:45:16', 80, 5, 'GRADE_A_PLUS','A+', '2015-03-06 16:45:16', 10000, 100, NULL, 14),
(0, 'Y2014', NULL, 'system', 'Outstanding', '2015-03-06 16:45:16', 70, 4, 'GRADE_A', 'A','2015-03-06 16:45:16', 10000, 80, NULL, 14),
(0, 'Y2014', NULL, 'system', 'Very Good', '2015-03-06 16:45:16', 60, 3.5, 'GRADE_A_MINUS','A-', '2015-03-06 16:45:16', 10000, 70, NULL, 14),
(0, 'Y2014', NULL, 'system', 'Good', '2015-03-06 16:45:16', 50, 3, 'GRADE_B', 'B','2015-03-06 16:45:16', 10000, 60, NULL, 14),
(0, 'Y2014', NULL, 'system', 'Satisfactory', '2015-03-06 16:45:16', 40, 2, 'GRADE_C','C', '2015-03-06 16:45:16', 10000, 50, NULL, 14),
(0, 'Y2014', NULL, 'system', 'Unsuccessful', '2015-03-06 16:45:16', 0, 0, 'GRADE_F', 'F(Fail)','2015-03-06 16:45:16', 10000, 40, NULL, 14);

/*2. Reset exam result and tabulation*/
delete from tabulation where exam_id in (select id from exam where shift_exam_id in (4));
update exam set exam_status = 'NEW', hall_schedule='NO_SCHEDULE' where shift_exam_id =4;
update exam set hall_schedule='NO_SCHEDULE' where class_name_id not in (1,2,3);
update exam_schedule set is_hall_mark_input = 0 where is_hall_mark_input = 1 and exam_id in (select id from exam where shift_exam_id in (2));

select * from shift_exam;
delete from tabulation_details where tabulation_id in (select id from tabulation where exam_id in (select id from exam where shift_exam_id in (2)))
delete from tabulation where exam_id in (select id from exam where shift_exam_id in (2));
select * from exam_schedule where is_hall_mark_input = 1 and exam_id in (select id from exam where shift_exam_id in (2));
update exam_schedule set is_hall_mark_input = 0 where is_hall_mark_input = 1 and exam_id in (select id from exam where shift_exam_id in (2));
update exam_schedule set is_ct_mark_input = 0 where is_ct_mark_input = 1 and exam_id in (select id from exam where shift_exam_id in (2));
update exam set exam_status = 'NEW' where shift_exam_id =2;
update exam set exam_status = 'NEW', hall_schedule='ADDED' where shift_exam_id =2 and hall_schedule ='PUBLISHED';
update exam set exam_status = 'NEW', ct_schedule='ADDED' where shift_exam_id =2 and ct_schedule ='PUBLISHED';
/*1. Delete shift Exam*/

delete from exam_mark where exam_id in (select id from exam where shift_exam_id in (5,6));
delete from calender_event where exam_schedule_id in (select id from exam_schedule where exam_id in (select id from exam where shift_exam_id in (5,6)));
delete from exam_schedule where exam_id in (select id from exam where shift_exam_id in (5,6));
delete from exam where shift_exam_id in (5,6);
delete from shift_exam where id in (5,6);

