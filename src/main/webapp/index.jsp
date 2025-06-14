<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<jsp:include page="/WEB-INF/views/layouts/base.jsp" />

<%
    String username = (String) session.getAttribute("username");
%>
<!DOCTYPE html>
<html>
<head>

<%

    String role = null;

    try {

        role = (String) session.getAttribute("role");
    } catch (Exception e) {
        e.printStackTrace(); // You'll see this in the Tomcat console
        out.println("Session error: " + e.getMessage());
    }
%>
    <meta charset="UTF-8">
    <title>Event Management System</title>
    <style>
        body {
            margin: 0;
            font-family: Arial, sans-serif;
            background-color: #f2f2f2;
        }

        .welcome {
            text-align: center;
            margin-top: 100px;
        }

        .welcome h1 {
            color: #333;
        }
    </style>
</head>
<body>

<div class="welcome">
    <% if (username != null) { %>
        <h1>Welcome, <%= username %> 👋</h1>
    <% } else { %>
        <h1>Welcome to the Event Management System!</h1>
        <p>Please <a href="login">login</a> to access your dashboard.</p>
    <% } %>
</div>

</body>
</html>
