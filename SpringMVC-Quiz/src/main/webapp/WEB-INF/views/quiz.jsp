<%--
  JSP file for displaying the quiz page, including questions and options.
  Created by IntelliJ IDEA.
  User: khz
  Date: 2023-11-02
  Time: 21:48
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="se.kth.id1212.springquiz.model.Question" %>

<% String quizSubject = (String) request.getAttribute("quizSubject"); %>
<% Question[] questions = (Question[]) request.getAttribute("questionsAndOptions"); %>
<% String acquiredPoints = (String) request.getAttribute("acquiredPoints"); %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Quiz Page</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
        }

        h1 {
            text-align: center;
        }

        .question {
            border: 1px solid #ccc;
            padding: 10px;
            margin-bottom: 20px;
            background-color: #f9f9f9;
        }

        .question p {
            margin: 0;
        }

        .answers {
            list-style-type: none;
            padding: 0;
        }

        .answers li {
            margin: 5px 0;
        }

        label {
            cursor: pointer;
        }

        input[type="radio"] {
            margin-right: 5px;
        }
    </style>
</head>
<body>
<h1><%= quizSubject %>
</h1>
<% if (acquiredPoints != null) { %>
<%= "<h2> Your score is: " + acquiredPoints + " points.</h2>" %>
<% } %>
<form action="" method="post">
    <% for (int i = 0; i < questions.length; i++) { %>
    <div class="question">
        <p><strong>Question <%= i + 1 %>:</strong> <%= questions[i].getText() %>
        </p>
        <ul class="answers">
            <% for (String option : questions[i].getOptions().split(",")) { %>
            <li>
                <label>
                    <input type="radio" name="question<%= questions[i].getId() %>" value="<%= option %>"><%= option %>
                </label>
            </li>
            <% } %>
        </ul>
    </div>
    <% } %>
    <input type="submit" value="Submit Quiz">
</form>
<form action="" method="post">
    <input type="submit" name="exit" value="Return to Dashboard">
</form>
</body>
</html>
