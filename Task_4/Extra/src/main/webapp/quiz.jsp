<%--
  JSP file for displaying the quiz page, including questions and options.
  Created by IntelliJ IDEA.
  User: khz
  Date: 2023-11-02
  Time: 21:48
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="se.kth.id1212.model.Question" %>

<% String quizSubject = (String) request.getAttribute("quizSubject"); %>
<% Question[] questions = (Question[]) request.getAttribute("questionsAndOptions"); %>
<% String acquiredPoints = (String) request.getAttribute("acquiredPoints"); %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/quiz.css">
    <title>Quiz Page</title>
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
        <p><strong>Question <%= i + 1 %>:</strong> <%= questions[i].questionText() %>
        </p>
        <ul class="answers">
            <% for (String option : questions[i].options()) { %>
            <li>
                <label>
                    <input type="radio" name="question<%= questions[i].id() %>" value="<%= option %>"><%= option %>
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
