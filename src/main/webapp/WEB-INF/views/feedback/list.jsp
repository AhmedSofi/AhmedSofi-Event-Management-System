<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="pageTitle" value="My Feedback" scope="request" />

<jsp:include page="/WEB-INF/views/layouts/base.jsp" />
<% if (session.getAttribute("error") != null) { %>
<div class="alert alert-danger">
    <%= session.getAttribute("error") %>
</div>
<% session.removeAttribute("error"); %>
<% } %>
<div class="container mt-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>My Feedback</h2>
        <a href="${pageContext.request.contextPath}/feedback/submit" class="btn btn-primary">
            <i class="bi bi-plus-circle me-1"></i> Submit Feedback
        </a>
    </div>

    <h2>My Submitted Feedback</h2>

    <c:if test="${empty feedbacks}">
        <p>You haven't submitted any feedback yet.</p>
    </c:if>

    <c:if test="${not empty feedbacks}">
        <table class="table table-striped">
            <thead>
            <tr>
                <th>Event</th>
                <th>Rating</th>
                <th>Comments</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="feedback" items="${feedbacks}">
                <tr>
                    <td>${feedback.event.title}</td>
                    <td>${feedback.rating}</td>
                    <td>${feedback.comments}</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:if>
</div>