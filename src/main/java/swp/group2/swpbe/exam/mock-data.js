// Simplified mock data for tests

import { QUESTION_TYPES, TEST_TYPES } from "./test-types";

export const sampleListeningTest = {
  id: "toeic-listening-1",
  type: TEST_TYPES.LISTENING,
  title: "TOEIC Listening Practice Test 1",
  description:
    "Complete TOEIC Listening test simulation with authentic exam format. This test includes four parts: Photographs, Question-Response, Conversations, and Talks, simulating real TOEIC listening conditions.",
  coverImg: "/Test (402 x 256 px)/2.png",
  views: 1245,
  ratings: 4.8,
  reviewCount: 156,
  duration: 45, // TOEIC Listening is about 45 minutes
  difficulty: "Intermediate",
  lastUpdated: "October 15, 2023",
  instructor: {
    name: "David Anderson",
    title: "TOEIC Trainer & Language Expert",
    experience: "10+ years",
    certifications: ["TESOL", "TOEIC Certified Trainer"],
    description:
      "David Anderson is a certified TOEIC trainer with over 10 years of experience in language assessment and test preparation. He has helped numerous students improve their TOEIC scores for career advancement and international job opportunities.",
  },
  parts: [
    {
      name: "Part 1: Photographs",
      icon: "FaCamera",
      duration: 5,
      questions: [
        {
          id: "L1-0",
          type: QUESTION_TYPES.PART_INSTRUCTION,
          title: "PART 1",
          imageUrl: "/TestSimulation/listening/direction.jpg",
          audioUrl: "/TestSimulation/listening/DIRECTION-PART-1.mp3",
        },
        {
          id: "L1-1",
          type: "SINGLE_CHOICE",
          audioUrl: "/TestSimulation/listening/zenlish-1.mp3",
          title: "Doing what",
          questionInstruction: "What is he doing?",
          answerInstruction: "Choose one correct answer",
          options: [
            { id: "A", text: "" },
            { id: "B", text: "" },
            { id: "C", text: "" },
            { id: "D", text: "" },
          ],
          correctAnswer: "B",
          imageUrl: "/TestSimulation/listening/zenlish-1-3.png",
        },
      ],
    },
    {
      name: "Part 2: Question-Response",
      icon: "FaComment",
      duration: 10,
      questions: [
        {
          id: "L1-2",
          type: "SINGLE_CHOICE",
          title: "Purpose of a Call",
          questionInstruction: "What is the purpose of the woman's call?",
          audioUrl: "/TestSimulation/listening/zenlish-2.mp3",
          options: [
            { id: "A", text: "" },
            { id: "B", text: "" },
            { id: "C", text: "" },
            { id: "D", text: "" },
          ],
          correctAnswer: "B",
          imageUrl: "/TestSimulation/listening/zenlish-2-2.png",
        },
      ],
    },
    {
      name: "Part 3: Conversations",
      icon: "FaUsers",
      duration: 15,
      questions: [
        {
          id: "L1-3",
          title: "Understanding a Schedule",
          type: "SINGLE_CHOICE",
          questionInstruction: "When does the course begin?",
          audioUrl: "/TestSimulation/listening/zenlish-3.mp3",
          options: [
            { id: "A", text: "" },
            { id: "B", text: "" },
            { id: "C", text: "" },
            { id: "D", text: "" },
          ],
          correctAnswer: "B",
          imageUrl: "/TestSimulation/listening/zenlish-3-2.png",
        },
      ],
    },
    {
      name: "Part 4: Talks",
      icon: "FaMicrophone",
      duration: 15,
      questions: [
        {
          id: "L1-4",
          title: "Identifying a Location",
          type: "SINGLE_CHOICE",
          questionInstruction:
            "Which image shows the correct location of the campus?",
          audioUrl: "/TestSimulation/listening/zenlish-4.mp3",
          options: [
            { id: "A", text: "" },
            { id: "B", text: "" },
            { id: "C", text: "" },
            { id: "D", text: "" },
          ],
          correctAnswer: "B",
          imageUrl: "/TestSimulation/listening/zenlish-4-2.png",
        },
      ],
    },
  ],
  features: [
    "Authentic TOEIC format and timing",
    "Detailed answer explanations",
    "Practice with real TOEIC-style questions",
    "Performance analysis with score estimation",
    "Audio with native English accents",
  ],
  requirements: [
    "Intermediate English level (B1+)",
    "Headphones for better audio clarity",
    "Quiet environment",
    "45 minutes of uninterrupted practice time",
  ],
  targetScores: [600, 700, 800, 900],
  relatedTests: [
    { id: "toeic-listening-2", title: "TOEIC Listening Practice Test 2" },
    { id: "toeic-reading-1", title: "TOEIC Reading Practice Test 1" },
    { id: "toeic-full-test", title: "TOEIC Full Practice Test" },
  ],
  sampleReviews: [
    {
      name: "Emily Nguyen",
      rating: 5,
      date: "October 2, 2023",
      comment:
        "This test was an excellent TOEIC listening practice. The questions were just like the real exam, and the explanations were clear and helpful!",
    },
    {
      name: "Hiroshi Tanaka",
      rating: 5,
      date: "September 15, 2023",
      comment:
        "A great way to prepare for the TOEIC. The conversations and talks sections were particularly well-designed, and I appreciated the performance analysis feature.",
    },
    {
      name: "Carlos Ramirez",
      rating: 4,
      date: "August 28, 2023",
      comment:
        "The practice test helped me improve my listening skills. However, I wish there were more practice sets included. Still, very useful overall!",
    },
  ],
};

export const sampleReadingTest = {
  id: "toeic-reading-1",
  type: TEST_TYPES.READING,
  title: "TOEIC Reading Practice Test",
  description:
    "Complete TOEIC Reading test simulation with authentic exam format. This test includes three parts: Incomplete Sentences, Text Completion, and Reading Comprehension, simulating real TOEIC reading conditions.",
  coverImg: "/Test (402 x 256 px)/2.png",
  views: 1320,
  ratings: 4.7,
  reviewCount: 142,
  duration: 75 * 60, // 75 minutes in seconds
  difficulty: "Intermediate",
  lastUpdated: "November 10, 2023",
  instructor: {
    name: "Sophia Carter",
    title: "TOEIC Instructor & English Coach",
    experience: "8+ years",
    certifications: ["TESOL", "Cambridge CELTA"],
    description:
      "Sophia Carter is an experienced TOEIC instructor with over 8 years of teaching English as a second language. She specializes in helping students improve their reading comprehension and vocabulary for TOEIC success.",
  },
  parts: [
    {
      id: "P5",
      name: "Part 5: Incomplete Sentences",
      icon: "FaEdit",
      duration: 20 * 60,
      questions: [
        {
          id: "R1-0",
          type: QUESTION_TYPES.PART_INSTRUCTION,
          title: "PART 5",
          imageUrl: "/TestSimulation/reading/reading-part-instruction.png",
        },
        {
          id: "R1-2",
          type: QUESTION_TYPES.FILL_IN_BLANK,
          title: "131.",
          imageUrl: "/TestSimulation/reading/reading-example.png",
          options: [
            { id: "A", text: "employment" },
            { id: "B", text: "construction" },
            { id: "C", text: "referral" },
            { id: "D", text: "security" },
          ],
          correctAnswer: "A",
          readingPassage: null,
        },
      ],
      description:
        "Choose the best word or phrase to complete the sentence based on grammar and vocabulary.",
    },
    {
      id: "P6",
      name: "Part 6: Text Completion",
      icon: "FaFileAlt",
      duration: 15 * 60,
      questions: [
        {
          id: "R3-2",
          type: QUESTION_TYPES.PART_INSTRUCTION,
          title: "PART 6",
          imageUrl: "/TestSimulation/reading/reading-part-instruction.png",
        },
        {
          id: "R1-3",
          type: QUESTION_TYPES.SINGLE_CHOICE,
          title: "132.",
          imageUrl: "/TestSimulation/reading/reading-example.png",
          options: [
            { id: "A", text: "has been applying" },
            { id: "B", text: "applied" },
            { id: "C", text: "will be applied" },
            { id: "D", text: "had applied" },
          ],
          correctAnswer: "A",
          readingPassage: null,
        },
      ],
      description:
        "Fill in the blanks in short passages using correct words or phrases.",
    },
    {
      id: "P7",
      name: "Part 7: Reading Comprehension",
      icon: "FaBook",
      duration: 40 * 60,
      questions: [
        {
          id: "R3-5",
          type: QUESTION_TYPES.PART_INSTRUCTION,
          title: "PART 7",
          imageUrl: "/TestSimulation/reading/reading-part-instruction.png",
        },
        {
          id: "R1-6",
          type: QUESTION_TYPES.MULTIPLE_CHOICE,
          title: "Reading Comprehension",
          questionInstruction:
            "According to the email, what is the main purpose of the new office location?",
          answerInstruction: "Choose all correct answers",
          options: [
            { id: "A", text: "To reduce costs" },
            { id: "B", text: "To improve the working environment" },
            { id: "C", text: "To be closer to clients" },
            { id: "D", text: "To accommodate new employees" },
          ],
          correctAnswer: "B",
          readingPassage: `To: All Staff\nFrom: Human Resources\nSubject: Office Relocation\n\nWe are pleased to announce that our company will be moving to a new office location next month. The new building offers more space and better facilities, which will improve our working environment.`,
        },
      ],
      description:
        "Read various passages such as emails, articles, and advertisements, and answer comprehension questions.",
    },
  ],
  features: [
    "Real TOEIC-style reading passages",
    "Timed practice with exam-like format",
    "Detailed answer explanations",
    "Performance tracking and score estimation",
    "Focus on grammar, vocabulary, and comprehension",
  ],
  requirements: [
    "Intermediate English level (B1+)",
    "A quiet environment for focused reading",
    "75 minutes of uninterrupted practice time",
  ],
  targetScores: [600, 700, 800, 900],
  relatedTests: [
    { id: "toeic-reading-2", title: "TOEIC Reading Practice Test 2" },
    { id: "toeic-listening-1", title: "TOEIC Listening Practice Test 1" },
    { id: "toeic-full-test", title: "TOEIC Full Practice Test" },
  ],
  sampleReviews: [
    {
      name: "Anna Lee",
      rating: 5,
      date: "November 5, 2023",
      comment:
        "This TOEIC Reading test was really helpful. The questions were challenging and well-structured, just like the real exam!",
    },
    {
      name: "Mark Johnson",
      rating: 4,
      date: "October 22, 2023",
      comment:
        "Great practice for the TOEIC. The explanations were detailed, but I wish there were more questions in the text completion section.",
    },
    {
      name: "Chen Wei",
      rating: 5,
      date: "September 30, 2023",
      comment:
        "I liked the variety of reading passages. It really improved my comprehension speed. Highly recommended!",
    },
  ],
};
