<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Event Management System - <c:out value="${pageTitle}" /></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
    <style>
        .navbar {
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .notification-badge {
            position: absolute;
            top: 0;
            right: -5px;
            background-color: #dc3545;
            color: white;
            border-radius: 50%;
            padding: 0.2rem 0.5rem;
            font-size: 0.7rem;
            display: none;
        }
        .notification-badge.visible {
            display: inline-block;
        }
        .nav-item-notifications {
            position: relative;
        }
        footer {
            background-color: #f8f9fa;
            padding: 1rem 0;
            margin-top: 2rem;
            text-align: center;
            border-top: 1px solid #e9ecef;
        }
        main {
            min-height: 10vh;
            padding: 2rem 0;
        }
    </style>
</head>
<body>
<header>
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <div class="container">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/">
                <i class="fas fa-calendar-alt me-2"></i>Event Management System
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav"
                    aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav ms-auto">
                    <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/">Home</a></li>
                    <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/events">Events</a></li>
                    <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/venues">Venues</a></li>

                    <c:choose>
                        <c:when test="${not empty sessionScope.user}">
                            <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/profile">Profile</a></li>
                            <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/orders">My Orders</a></li>
                            <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/feedback/my">Feedback</a></li>

                            <!-- Organizer-specific links (now in main menu) -->
                            <c:if test="${sessionScope.user.role == 'ORGANIZER'}">
                                <li class="nav-item">
                                    <a class="nav-link" href="${pageContext.request.contextPath}/events/my-events">
                                        My Events
                                    </a>
                                </li>

                            </c:if>

                            <li class="nav-item nav-item-notifications">
                                <a class="nav-link" href="${pageContext.request.contextPath}/notifications">
                                    <i class="fas fa-bell"></i>
                                    <span id="notification-badge" class="notification-badge"></span>
                                </a>
                            </li>

                            <li class="nav-item">
                                <a class="nav-link" href="${pageContext.request.contextPath}/logout">
                                    <i class="fas fa-sign-out-alt me-1"></i>Logout
                                </a>
                            </li>
                        </c:when>
                        <c:otherwise>
                            <li class="nav-item">
                                <a class="nav-link" href="${pageContext.request.contextPath}/login">
                                    <i class="fas fa-sign-in-alt me-1"></i>Login
                                </a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" href="${pageContext.request.contextPath}/register">
                                    <i class="fas fa-user-plus me-1"></i>Register
                                </a>
                            </li>
                        </c:otherwise>
                    </c:choose>
                </ul>
            </div>
        </div>
    </nav>
</header>

<main class="container">
    <jsp:include page="${contentPage}" />
</main>

<c:if test="${not empty sessionScope.user}">
    <script src="${pageContext.request.contextPath}/js/jquery-3.5.1.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/bootstrap.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/scripts.js"></script>
    <script>
        const notificationBadge = document.getElementById('notification-badge');
        const pollInterval = 3000; // 30 seconds

        function fetchNotificationCount() {
            fetch('${pageContext.request.contextPath}/notifications/poll')
                .then(response => response.json())
                .then(data => {
                    if (data.unreadCount > 0) {
                        notificationBadge.textContent = data.unreadCount;
                        notificationBadge.classList.add('visible');
                    } else {
                        notificationBadge.textContent = '';
                        notificationBadge.classList.remove('visible');
                    }
                })
                .catch(error => {
                    console.error('Error fetching notifications:', error);
                    notificationBadge.textContent = '!';
                    notificationBadge.classList.add('visible');
                    notificationBadge.style.backgroundColor = 'orange';
                });
        }

        fetchNotificationCount();
        setInterval(fetchNotificationCount, pollInterval);
    </script>
</c:if>

</body>
</html>
