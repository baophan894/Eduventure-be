USE records;

-- Delete data from tables in reverse order of dependencies
-- First delete from tables that reference other tables
DELETE FROM test_reviews;
DELETE FROM related_tests;
DELETE FROM test_target_scores;
DELETE FROM test_requirements;
DELETE FROM test_features;
DELETE FROM question_options;
DELETE FROM questions;
DELETE FROM test_parts;
DELETE FROM tests;
DELETE FROM question_types;
DELETE FROM test_types;

-- Reset auto-increment counters
ALTER TABLE test_reviews AUTO_INCREMENT = 1;
ALTER TABLE related_tests AUTO_INCREMENT = 1;
ALTER TABLE test_target_scores AUTO_INCREMENT = 1;
ALTER TABLE test_requirements AUTO_INCREMENT = 1;
ALTER TABLE test_features AUTO_INCREMENT = 1;
ALTER TABLE question_options AUTO_INCREMENT = 1;
ALTER TABLE questions AUTO_INCREMENT = 1;
ALTER TABLE test_parts AUTO_INCREMENT = 1;
ALTER TABLE tests AUTO_INCREMENT = 1;
ALTER TABLE question_types AUTO_INCREMENT = 1;
ALTER TABLE test_types AUTO_INCREMENT = 1; 