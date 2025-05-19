-- Add audio_url column to test_parts table
ALTER TABLE test_parts ADD COLUMN audio_url VARCHAR(255);

-- Copy audio_url values from questions to their corresponding test_parts
UPDATE test_parts tp
SET audio_url = (
    SELECT q.audio_url
    FROM questions q
    WHERE q.part_id = tp.id
    LIMIT 1
);

-- Drop audio_url column from questions table
ALTER TABLE questions DROP COLUMN audio_url; 