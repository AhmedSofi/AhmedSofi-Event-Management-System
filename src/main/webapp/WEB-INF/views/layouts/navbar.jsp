<!-- navbar.jsp -->

<%
    String username = (String) session.getAttribute("username");
    String role = (String) session.getAttribute("role");
%>
<head>
    <meta charset="UTF-8">
    <title>Event Management System - My Profile</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>


<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container">
        <a class="navbar-brand" href="/EMS/home">Event Management System</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav ms-auto">
                <li class="nav-item"><a class="nav-link" href="/EventManagementSystemV2/">Home</a></li>
                <li class="nav-item"><a class="nav-link" href="/EventManagementSystemV2/events">Events</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/venues">Venues</a></li>

                <li class="nav-item"><a class="nav-link" href="/EventManagementSystemV2/profile">Profile</a></li>
                <li class="nav-item"><a class="nav-link" href="/EventManagementSystemV2/logout">Logout</a></li>
                <a class="nav-link" href="${pageContext.request.contextPath}/feedback/my">feedback</a>
            </ul>
        </div>
    </div>
</nav>


<!-- Bootstrap JS -->
<script src="${pageContext.request.contextPath}/js/jquery-3.5.1.min.js"></script>
<script src="${pageContext.request.contextPath}/js/bootstrap.min.js"></script>
<script src="${pageContext.request.contextPath}/js/scripts.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

