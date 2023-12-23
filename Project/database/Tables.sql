-- Drop existing tables if they exist
DROP TABLE IF EXISTS RESERVATIONS;
DROP TABLE IF EXISTS BOOKING_LISTS;
DROP TABLE IF EXISTS USER_COURSE_REGISTRATIONS;
DROP TABLE IF EXISTS COURSES;
DROP TABLE IF EXISTS USERS;

-- Create the USERS table
CREATE TABLE USERS
(
    ID       SERIAL PRIMARY KEY,
    USERNAME VARCHAR(32) UNIQUE NOT NULL,
    PASSWORD VARCHAR NOT NULL
);

-- Create the COURSES table
CREATE TABLE COURSES
(
    ID   VARCHAR(15) PRIMARY KEY,
    NAME VARCHAR(32) NOT NULL
);

-- Create the USER_COURSE_REGISTRATIONS table
CREATE TABLE USER_COURSE_REGISTRATIONS
(
    PRIMARY KEY (USER_ID, COURSE_ID),
    USER_ID   INTEGER NOT NULL REFERENCES USERS (ID),
    COURSE_ID VARCHAR(32) NOT NULL REFERENCES COURSES (ID),
    ADMIN     BOOLEAN NOT NULL
);

-- Create the BOOKING_LISTS table
CREATE TABLE BOOKING_LISTS
(
    ID          SERIAL PRIMARY KEY,
    COURSE_ID   VARCHAR(32) NOT NULL REFERENCES COURSES (ID),
    USER_ID     INTEGER NOT NULL REFERENCES USERS (ID),
    DESCRIPTION VARCHAR(64) NOT NULL,
    LOCATION    VARCHAR(32) NOT NULL,
    TIME        TIMESTAMP NOT NULL,
    INTERVAL    INTEGER NOT NULL,
    MAX_SLOTS   INTEGER NOT NULL
);

-- Create the RESERVATIONS table
CREATE TABLE RESERVATIONS
(
    ID          SERIAL PRIMARY KEY,
    LIST_ID     INTEGER NOT NULL REFERENCES BOOKING_LISTS (ID),
    USER_ID     INTEGER NOT NULL REFERENCES USERS (ID),
    SEQUENCE_ID INTEGER NOT NULL
);