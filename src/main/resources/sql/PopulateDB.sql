CREATE TABLE USERS
(
    ID       INTEGER            NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    USERNAME VARCHAR(32) UNIQUE NOT NULL,
    PASSWORD VARCHAR(32)        NOT NULL
);

CREATE TABLE QUIZZES
(
    ID      INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    SUBJECT VARCHAR(64) NOT NULL
);

CREATE TABLE QUESTIONS
(
    ID      INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    TEXT    VARCHAR(64)  NOT NULL,
    OPTIONS VARCHAR(255) NOT NULL,
    ANSWER  VARCHAR(64)  NOT NULL
);

CREATE TABLE SELECTOR
(
    QUIZ_ID     INTEGER NOT NULL REFERENCES QUIZZES (ID),
    QUESTION_ID INTEGER NOT NULL REFERENCES QUESTIONS (ID)
);

CREATE TABLE RESULTS
(
    ID      INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    USER_ID INTEGER NOT NULL REFERENCES USERS (ID),
    QUIZ_ID INTEGER NOT NULL REFERENCES QUIZZES (ID),
    SCORE   INTEGER NOT NULL
);

INSERT INTO USERS (USERNAME, PASSWORD)
VALUES ('aaak@kth.se', 'password1'),
       ('ebeshir@kth.se', 'password2'),
       ('test@kth.se', 'password3');

INSERT INTO QUIZZES (SUBJECT)
VALUES ('Math Quiz'),
       ('Science Quiz'),
       ('History Quiz');

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

INSERT INTO RESULTS (USER_ID, QUIZ_ID, SCORE)
VALUES (1, 2, 1),
       (1, 3, 2),
       (2, 1, 1),
       (2, 2, 2),
       (2, 3, 3),
       (3, 1, 0),
       (3, 2, 1),
       (3, 3, 0);
