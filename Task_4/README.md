# Laboration 4: Java EE with Tomcat

## Part 1: Number Guessing Game

### Task Description

The first part of the task is to implement the number guessing game from Lab 2 once again, but this time with Tomcat. Each client connecting to the Tomcat server will have its own thread, so you no longer need to handle it manually. Additionally, you can read parameters using `request.getParameter()` and manage sessions using `request.getSession()`. The idea is to see how much of what was done manually in the previous lab is now embedded in Java EE.

The key classes you will need for this task in Java EE are primarily `HttpServlet`, where an instance is dedicated to handling HTTP requests and generating HTTP responses. When you need to track clients between different requests, you will also need `HttpSession`, where data associated with a session is stored (Tomcat uses a cookie named JSESSIONID under the hood to track clients between different requests).

Design should follow the MVC (Model-View-Controller) design pattern with the following components:

- JavaBeans (as Model, reuse `GuessBean` from Lab 2)
- HttpServlet(s) (as Controller)
- JSP pages (as View)

You won't need many lines of code to complete this task compared to Lab 2.

## Part 2: Quiz Application

### Task Description

The second part of the task is to create a quiz application where users can log in with their username/email and password. After logging in, users are presented with the option to take one of the available quizzes, along with a display of their previous results. There is no requirement to have an administrator interface for adding users, quizzes, or questions. Each quiz has a fixed number of multiple-choice questions (checkboxes). You are recommended to use the following database structure:

![Database Structure Diagram](https://github.com/Azakh993/ID1212/blob/main/Task_4/Database-Structure-Diagram.PNG)

Note: Netbeans/Tomcat/Derby is the only environment supported during the labs. It doesn't mean you have to use this combination, but it's up to you to set up a working combination of IDE/application server/DB if you choose other than these. A working combination is available under "Files" on the left.

### Extra Task

Use Jakarta Persistence (JPA) to mirror the `result` objects in the database. Essentially, this means that without SQL code, an `insert` operation should be performed in the `results` table when a new `result` object is created. Updated on December 16th: There is a Netbeans project "L4_JPA" under "Files/L4" that creates two DB-mirrored objects (and deletes one of them). However, it needs to be configured in "persistence.xml" for your DB connection parameters (DB name, username, etc.).

## Note

- If working in pairs, only one should upload the code to avoid plagiarism.
- Upload the code before or during the presentation.
