<%--
  Created by IntelliJ IDEA.
  User: khz
  Date: 2023-10-31
  Time: 09:40
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
  <title>Guessing Game</title>
</head>
<body>
<h1>Guessing Game</h1>
<p>Guess a number between 1 and 100</p>

<% String outcome = session.getAttribute("guess_outcome") == null ?
        "" : (String) session.getAttribute("guess_outcome"); %>

<form action="<%= request.getContextPath() %>/guess" method="POST">

  <%  if(outcome.equals("CORRECT")) {
  %>      <input type="hidden" name="restart" value="true">
  <input type="submit" value="Restart">
  <%  } else {
  %>      <input type="text" name="guess">
  <input type="submit" value="Guess">
  <%    }
  %>
</form>

<p>Number of guesses made:
  <%= session.getAttribute("number_of_guesses") == null ? 0 : session.getAttribute("number_of_guesses") %>
</p>

<p>
  <%  if(outcome.equals("CORRECT")) {
  %>      You guessed correctly!
  <%    } else if(outcome.equals("LOW")) {
  %>      Your guess was too low!
  <%    } else if(outcome.equals("HIGH")) {
  %>      Your guess was too high!
  <%    } else if (outcome.equals("INVALID")){
  %>      Invalid guess!
  <%    }
  %>
</p>

</body>
</html>
