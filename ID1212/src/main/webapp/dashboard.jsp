<%--
  JSP file for displaying the dashboard page.
  Created by IntelliJ IDEA.
  User: khz
  Date: 2023-11-02
  Time: 21:47
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="se.kth.id1212.model.Quiz" %>
<%@ page import="java.util.Map" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" type="text/css" href=${pageContext.request.contextPath}/CSS/dashboard.css>
    <title>Quiz Dashboard</title>
</head>
<body>
<h1>Quiz Dashboard</h1>
<table>
    <tr>
        <th>Subject</th>
        <th>Previous Result</th>
    </tr>

    <%
        HashMap< Quiz, Integer > quizResultMap = (HashMap< Quiz, Integer >) request.getAttribute("quizResultMap");

        for (Map.Entry< Quiz, Integer > entry : quizResultMap.entrySet()) {
            Quiz quiz = entry.getKey();
            Integer result = entry.getValue();
    %>
    <tr>
        <td>
            <%= quiz.subject() %>
            <form action="" method="post">
                <input type="hidden" name="quizID" value="<%= quiz.id() %>">
                <input type="submit" value="Take Quiz">
            </form>
        </td>
        <td><%= (result != null) ? result : "Not Attempted" %>
        </td>
    </tr>
    <%
        }
    %>
</table>
</body>
</html>