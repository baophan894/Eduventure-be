USE records;


-- test_types is referenced by tests
-- question_types is referenced by questions

-- tests is referenced by:
--   - test_parts
--   - test_features
--   - test_requirements
--   - test_target_scores
--   - test_reviews
--   - related_tests

-- test_parts is referenced by questions
-- questions is referenced by question_options
-- user is referenced by test_reviews


-- Test Types Table
CREATE TABLE test_types (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

-- Question Types Table
CREATE TABLE question_types (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

-- Tests Table
CREATE TABLE tests (
    id INT AUTO_INCREMENT PRIMARY KEY,
    type INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    cover_img VARCHAR(255),
    views INT DEFAULT 0,
    ratings FLOAT DEFAULT 0,
    review_count INT DEFAULT 0,
    duration INT NOT NULL, -- in seconds
    difficulty VARCHAR(50),
    last_updated DATETIME,
    instructor_name VARCHAR(100),
    instructor_title VARCHAR(100),
    instructor_experience VARCHAR(50),
    instructor_description TEXT,
    FOREIGN KEY (type) REFERENCES test_types(id)
);

-- Test Parts Table
CREATE TABLE test_parts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    test_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    icon VARCHAR(50),
    duration INT NOT NULL, -- in seconds
    description TEXT,
    FOREIGN KEY (test_id) REFERENCES tests(id) ON DELETE CASCADE
);

-- Questions Table
CREATE TABLE questions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    part_id INT NOT NULL,
    type INT NOT NULL,
    title VARCHAR(255),
    question_instruction TEXT,
    answer_instruction TEXT,
    audio_url VARCHAR(255),
    image_url VARCHAR(255),
    reading_passage TEXT,
    correct_answer VARCHAR(255),
    FOREIGN KEY (part_id) REFERENCES test_parts(id) ON DELETE CASCADE,
    FOREIGN KEY (type) REFERENCES question_types(id)
);

-- Question Options Table
CREATE TABLE question_options (
    id INT AUTO_INCREMENT PRIMARY KEY,
    question_id INT NOT NULL,
    option_id VARCHAR(10) NOT NULL,
    text TEXT,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
);

-- Test Features Table
CREATE TABLE test_features (
    id INT AUTO_INCREMENT PRIMARY KEY,
    test_id INT NOT NULL,
    feature TEXT NOT NULL,
    FOREIGN KEY (test_id) REFERENCES tests(id) ON DELETE CASCADE
);

-- Test Requirements Table
CREATE TABLE test_requirements (
    id INT AUTO_INCREMENT PRIMARY KEY,
    test_id INT NOT NULL,
    requirement TEXT NOT NULL,
    FOREIGN KEY (test_id) REFERENCES tests(id) ON DELETE CASCADE
);

-- Test Target Scores Table
CREATE TABLE test_target_scores (
    id INT AUTO_INCREMENT PRIMARY KEY,
    test_id INT NOT NULL,
    score INT NOT NULL,
    FOREIGN KEY (test_id) REFERENCES tests(id) ON DELETE CASCADE
);

-- Test Reviews Table
CREATE TABLE test_reviews (
    id INT AUTO_INCREMENT PRIMARY KEY,
    test_id INT NOT NULL,
    user_id bigint unsigned NOT NULL,
    rating INT NOT NULL,
    review_date DATETIME NOT NULL,
    comment TEXT,
    FOREIGN KEY (test_id) REFERENCES tests(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);

-- Related Tests Table
CREATE TABLE related_tests (
    id INT AUTO_INCREMENT PRIMARY KEY,
    test_id INT NOT NULL,
    related_test_id INT NOT NULL,
    related_test_title VARCHAR(255) NOT NULL,
    FOREIGN KEY (test_id) REFERENCES tests(id) ON DELETE CASCADE
);

-- Insert initial test types
INSERT INTO test_types (name) VALUES
('LISTENING'),
('READING');

-- Insert initial question types
INSERT INTO question_types (name) VALUES
('Single Choice'),
('Multiple Choice'),
('Fill in Blank'),
('Part Instruction'); 