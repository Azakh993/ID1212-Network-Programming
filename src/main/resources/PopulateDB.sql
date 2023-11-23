-- Create the USERS table
CREATE TABLE USERS
(
    ID       INTEGER PRIMARY KEY,
    USERNAME TEXT NOT NULL,
    PASSWORD TEXT NOT NULL
);

-- Create the QUIZZES table
CREATE TABLE QUIZZES
(
    ID      INTEGER PRIMARY KEY,
    SUBJECT TEXT NOT NULL
);

-- Create the QUESTIONS table
CREATE TABLE QUESTIONS
(
    ID      INTEGER PRIMARY KEY,
    TEXT    TEXT NOT NULL,
    OPTIONS TEXT NOT NULL,
    ANSWER  TEXT NOT NULL
);

-- Create the SELECTOR table to associate quizzes and questions
CREATE TABLE SELECTOR
(
    QUIZ_ID     INTEGER NOT NULL,
    QUESTION_ID INTEGER NOT NULL,
    FOREIGN KEY (QUIZ_ID) REFERENCES QUIZZES (ID),
    FOREIGN KEY (QUESTION_ID) REFERENCES QUESTIONS (ID)
);

-- Create the RESULTS table
CREATE TABLE RESULTS
(
    ID      INTEGER PRIMARY KEY,
    USER_ID INTEGER NOT NULL,
    QUIZ_ID INTEGER NOT NULL,
    SCORE   INTEGER NOT NULL,
    FOREIGN KEY (USER_ID) REFERENCES USERS (ID),
    FOREIGN KEY (QUIZ_ID) REFERENCES QUIZZES (ID)
);

-- Populate the USERS table with demo data
INSERT INTO USERS (USERNAME, PASSWORD)
VALUES ('aaak@kth.se', 'password1'),
       ('ebeshir@kth.se', 'password2'),
       ('test@kth.se', 'password3');

-- Populate the QUIZZES table with demo data
INSERT INTO QUIZZES (SUBJECT)
VALUES ('Math Quiz'),
       ('Science Quiz'),
       ('History Quiz');

-- Populate the QUESTIONS table with demo data
-- You should add at least 9 unique questions and their respective options and answers
INSERT INTO QUESTIONS (TEXT, OPTIONS, ANSWER)
VALUES ('What is 2 + 2?', '3,4,5,6', '4'),
       ('What is the capital of France?', 'London,Berlin,Paris,Madrid', 'Paris'),
       ('Who wrote Romeo and Juliet?', 'Charles Dickens,William Shakespeare,Jane Austen,George Orwell',
        'William Shakespeare'),
       ('What is the chemical symbol for water?', 'H2O,CO2,O2,H2SO4', 'H2O'),
       ('What is the largest planet in our solar system?', 'Earth,Mars,Jupiter,Saturn', 'Jupiter'),
       ('What is the powerhouse of the cell?', 'Nucleus,Mitochondria,Chloroplast,Ribosome', 'Mitochondria'),
       ('Which gas do plants absorb from the atmosphere?', 'Oxygen,Carbon Dioxide,Nitrogen,Methane', 'Carbon Dioxide'),
       ('Who is known as the "Father of Modern Physics"?', 'Isaac Newton,Albert Einstein,Galileo Galilei,Niels Bohr',
        'Albert Einstein'),
       ('What is the chemical symbol for gold?', 'Go,Au,Ag,Fe', 'Au');

-- Populate the SELECTOR table with demo data to associate questions with quizzes
INSERT INTO SELECTOR (QUIZ_ID, QUESTION_ID)
VALUES (1, 1),
       (1, 4),
       (1, 7),
       (2, 2),
       (2, 5),
       (2, 8),
       (3, 3),
       (3, 6),
       (3, 9);

-- Populate the RESULTS table with demo data
-- You can generate random scores for the results
INSERT INTO RESULTS (USER_ID, QUIZ_ID, SCORE)
VALUES (1, 2, 1),
       (1, 3, 2),
       (2, 1, 1),
       (2, 2, 2),
       (2, 3, 3),
       (3, 1, 0),
       (3, 2, 1),
       (3, 3, 0);
