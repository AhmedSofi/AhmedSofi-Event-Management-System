<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/WEB-INF/views/layouts/base.jsp" />

<%
    String username = (String) session.getAttribute("username");
    String role = (String) session.getAttribute("role");
%>
<c:if test="${not empty error}">
    <div class="alert alert-danger" role="alert">
            ${error}
    </div>
</c:if>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>All Venues</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .venue-card-body {
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            flex-wrap: wrap; /* Allow wrapping of content if needed */
        }

        .venue-card-text {
            max-width: 100%; /* Allow the text to take the full width of the available space */
            margin-right: 20px; /* Give some space between the content and buttons */
        }

        .venue-card-footer {
            margin-top: 1rem;
            text-align: right;
            width: 100%;
        }

        .venue-card-footer a {
            display: inline-block;
            margin-top: 0.5rem;
        }
    </style>
</head>
<body class="bg-light">

<div class="container mt-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>Venue List</h2>
        <c:if test="${sessionScope.user.role == 'ORGANIZER'}">
            <a href="${pageContext.request.contextPath}/venues/add" class="btn btn-success" ui_test="add-venue-button">+ Add Venue</a>
        </c:if>
    </div>

    <c:choose>
        <c:when test="${not empty venues}">
            <div class="row">
                <c:forEach var="venue" items="${venues}">
                    <div class="col-md-6">
                        <div class="card mb-4 shadow-sm">
                            <div class="card-body venue-card-body">

                                <div class="venue-card-text">
                                    <h5 class="card-title mb-2">${venue.name}</h5>
                                    <p class="card-text mb-1"><strong>Address:</strong> ${venue.address}</p>
                                    <p class="card-text mb-1"><strong>Capacity:</strong> ${venue.capacity}</p>
                                    <p class="card-text mb-1"><strong>Contact Person:</strong> ${venue.contactPerson}</p>
                                    <p class="card-text mb-1">${venue.contactEmail}</p>
                                    <p class="card-text">${venue.contactPhone}</p>
                                </div>

                                <c:if test="${sessionScope.user.role == 'ORGANIZER'}">
                                    <div class="venue-card-footer">
                                        <a href="${pageContext.request.contextPath}/venues/edit?id=${venue.venueId}" class="btn btn-primary btn-sm me-2">Edit</a>
                                        <a href="${pageContext.request.contextPath}/venues/delete?id=${venue.venueId}"
                                           class="btn btn-danger btn-sm"
                                           onclick="return confirm('Are you sure you want to delete this venue?');">
                                           Delete
                                        </a>
                                    </div>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:when>
        <c:otherwise>
            <div class="alert alert-info">No venues found.</div>
        </c:otherwise>
    </c:choose>
</div>

</body>
</html>
