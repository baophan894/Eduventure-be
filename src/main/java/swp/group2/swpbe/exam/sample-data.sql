USE records;

-- Insert test types
INSERT INTO test_types (name) VALUES
('LISTENING'),
('READING');

-- Insert question types
INSERT INTO question_types (name) VALUES
('Single Choice'),
('Multiple Choice'),
('Fill in Blank'),
('Part Instruction');

-- Insert sample tests
INSERT INTO tests (type, title, description, cover_img, views, ratings, review_count, duration, difficulty, last_updated, instructor_name, instructor_title, instructor_experience, instructor_description) VALUES
(1, 'TOEIC Listening Practice Test 1', 'Complete TOEIC Listening test simulation with authentic exam format. This test includes four parts: Photographs, Question-Response, Conversations, and Talks, simulating real TOEIC listening conditions.', '/Test (402 x 256 px)/2.png', 1245, 4.8, 156, 45, 'Intermediate', '2023-10-15', 'David Anderson', 'TOEIC Trainer & Language Expert', '10+ years', 'David Anderson is a certified TOEIC trainer with over 10 years of experience in language assessment and test preparation. He has helped numerous students improve their TOEIC scores for career advancement and international job opportunities.'),
(2, 'TOEIC Reading Practice Test', 'Complete TOEIC Reading test simulation with authentic exam format. This test includes three parts: Incomplete Sentences, Text Completion, and Reading Comprehension, simulating real TOEIC reading conditions.', '/Test (402 x 256 px)/2.png', 1320, 4.7, 142, 4500, 'Intermediate', '2023-11-10', 'Sophia Carter', 'TOEIC Instructor & English Coach', '8+ years', 'Sophia Carter is an experienced TOEIC instructor with over 8 years of teaching English as a second language. She specializes in helping students improve their reading comprehension and vocabulary for TOEIC success.');

-- Insert test parts for listening test
INSERT INTO test_parts (test_id, name, icon, duration, description) VALUES
(1, 'Part 1: Photographs', 'FaCamera', 5, 'Look at the picture and select the statement that best describes it.'),
(1, 'Part 2: Question-Response', 'FaComment', 10, 'Listen to the question and select the most appropriate response.'),
(1, 'Part 3: Conversations', 'FaUsers', 15, 'Listen to the conversation and answer the questions about it.'),
(1, 'Part 4: Talks', 'FaMicrophone', 15, 'Listen to the talk and answer the questions about it.');

-- Insert test parts for reading test
INSERT INTO test_parts (test_id, name, icon, duration, description) VALUES
(2, 'Part 5: Incomplete Sentences', 'FaEdit', 1200, 'Choose the best word or phrase to complete the sentence based on grammar and vocabulary.'),
(2, 'Part 6: Text Completion', 'FaFileAlt', 900, 'Fill in the blanks in short passages using correct words or phrases.'),
(2, 'Part 7: Reading Comprehension', 'FaBook', 2400, 'Read various passages such as emails, articles, and advertisements, and answer comprehension questions.');

-- Insert questions for listening test
INSERT INTO questions (part_id, type, title, question_instruction, answer_instruction, audio_url, image_url, correct_answer) VALUES
(1, 4, 'PART 1', 'Look at the picture and select the statement that best describes it.', 'Choose one correct answer', '/TestSimulation/listening/DIRECTION-PART-1.mp3', '/TestSimulation/listening/direction.jpg', NULL),
(1, 1, 'Question 1', 'What is he doing?', 'Choose one correct answer', '/TestSimulation/listening/zenlish-1.mp3', '/TestSimulation/listening/zenlish-1-3.png', 'B'),
(2, 1, 'Question 2', 'What is the purpose of the woman''s call?', 'Choose one correct answer', '/TestSimulation/listening/zenlish-2.mp3', '/TestSimulation/listening/zenlish-2-2.png', 'B'),
(3, 1, 'Question 3', 'When does the course begin?', 'Choose one correct answer', '/TestSimulation/listening/zenlish-3.mp3', '/TestSimulation/listening/zenlish-3-2.png', 'B'),
(4, 1, 'Question 4', 'Which image shows the correct location of the campus?', 'Choose one correct answer', '/TestSimulation/listening/zenlish-4.mp3', '/TestSimulation/listening/zenlish-4-2.png', 'B');

-- Insert questions for reading test
INSERT INTO questions (part_id, type, title, question_instruction, answer_instruction, image_url, reading_passage, correct_answer) VALUES
(5, 4, 'PART 5', 'Choose the best word or phrase to complete the sentence.', 'Choose one correct answer', '/TestSimulation/reading/reading-part-instruction.png', NULL, NULL),
(5, 3, 'Question 1', 'Choose the correct word to complete the sentence.', 'Choose one correct answer', '/TestSimulation/reading/reading-example.png', NULL, 'A'),
(6, 4, 'PART 6', 'Fill in the blanks in the text.', 'Choose one correct answer', '/TestSimulation/reading/reading-part-instruction.png', NULL, NULL),
(6, 1, 'Question 2', 'Choose the correct word to complete the text.', 'Choose one correct answer', '/TestSimulation/reading/reading-example.png', NULL, 'A'),
(7, 4, 'PART 7', 'Read the passage and answer the questions.', 'Choose one correct answer', '/TestSimulation/reading/reading-part-instruction.png', NULL, NULL),
(7, 2, 'Question 3', 'According to the email, what is the main purpose of the new office location?', 'Choose all correct answers', '/TestSimulation/reading/reading-example.png', 'To: All Staff\nFrom: Human Resources\nSubject: Office Relocation\n\nWe are pleased to announce that our company will be moving to a new office location next month. The new building offers more space and better facilities, which will improve our working environment.', 'B');

-- Insert question options
INSERT INTO question_options (question_id, option_id, text) VALUES
-- Listening test options
(2, 'A', 'He is reading a book'),
(2, 'B', 'He is typing on a computer'),
(2, 'C', 'He is writing on paper'),
(2, 'D', 'He is using a phone'),
(3, 'A', 'To make a reservation'),
(3, 'B', 'To ask about business hours'),
(3, 'C', 'To complain about service'),
(3, 'D', 'To request information'),
(4, 'A', 'Next Monday'),
(4, 'B', 'Next Wednesday'),
(4, 'C', 'Next Friday'),
(4, 'D', 'Next Saturday'),
(5, 'A', 'The main entrance'),
(5, 'B', 'The library building'),
(5, 'C', 'The student center'),
(5, 'D', 'The parking lot'),
-- Reading test options
(7, 'A', 'employment'),
(7, 'B', 'construction'),
(7, 'C', 'referral'),
(7, 'D', 'security'),
(8, 'A', 'has been applying'),
(8, 'B', 'applied'),
(8, 'C', 'will be applied'),
(8, 'D', 'had applied'),
(10, 'A', 'To reduce costs'),
(10, 'B', 'To improve the working environment'),
(10, 'C', 'To be closer to clients'),
(10, 'D', 'To accommodate new employees');

-- Insert test features
INSERT INTO test_features (test_id, feature) VALUES
(1, 'Authentic TOEIC format and timing'),
(1, 'Detailed answer explanations'),
(1, 'Practice with real TOEIC-style questions'),
(1, 'Performance analysis with score estimation'),
(1, 'Audio with native English accents'),
(2, 'Real TOEIC-style reading passages'),
(2, 'Timed practice with exam-like format'),
(2, 'Detailed answer explanations'),
(2, 'Performance tracking and score estimation'),
(2, 'Focus on grammar, vocabulary, and comprehension');

-- Insert test requirements
INSERT INTO test_requirements (test_id, requirement) VALUES
(1, 'Intermediate English level (B1+)'),
(1, 'Headphones for better audio clarity'),
(1, 'Quiet environment'),
(1, '45 minutes of uninterrupted practice time'),
(2, 'Intermediate English level (B1+)'),
(2, 'A quiet environment for focused reading'),
(2, '75 minutes of uninterrupted practice time');

-- Insert target scores
INSERT INTO test_target_scores (test_id, score) VALUES
(1, 600),
(1, 700),
(1, 800),
(1, 900),
(2, 600),
(2, 700),
(2, 800),
(2, 900);

-- Insert sample reviews
INSERT INTO test_reviews (test_id, user_id, rating, review_date, comment) VALUES
(1, 97, 5, '2023-10-02', 'This test was an excellent TOEIC listening practice. The questions were just like the real exam, and the explanations were clear and helpful!'),
(1, 98, 5, '2023-09-15', 'A great way to prepare for the TOEIC. The conversations and talks sections were particularly well-designed, and I appreciated the performance analysis feature.'),
(1, 102, 4, '2023-08-28', 'The practice test helped me improve my listening skills. However, I wish there were more practice sets included. Still, very useful overall!'),
(2, 103, 5, '2023-11-05', 'This TOEIC Reading test was really helpful. The questions were challenging and well-structured, just like the real exam!'),
(2, 104, 4, '2023-10-22', 'Great practice for the TOEIC. The explanations were detailed, but I wish there were more questions in the text completion section.'),
(2, 105, 5, '2023-09-30', 'I liked the variety of reading passages. It really improved my comprehension speed. Highly recommended!'); 