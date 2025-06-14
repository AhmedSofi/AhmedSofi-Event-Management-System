<%--<!-- <%@ page contentType="text/html;charset=UTF-8" language="java" %>--%>
<%--<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>--%>
<%--<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>--%>
<%--<!DOCTYPE html>--%>
<%--<html lang="en">--%>
<%--<head>--%>
<%--    <title>Ticket Details - #${ticket.ticketId}</title>--%>
<%--    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.min.css">--%>
<%--    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">--%>
<%--</head>--%>
<%--<body>--%>
<%--    <div class="container mt-4">--%>
<%--        <h2>Ticket Details</h2>--%>
<%--        <div class="card mb-4">--%>
<%--            <div class="card-header">--%>
<%--                Ticket #<c:out value="${ticket.ticketId}" />--%>
<%--            </div>--%>
<%--            <div class="card-body">--%>
<%--                <p><strong>Event:</strong> <a href="${pageContext.request.contextPath}/events/${ticket.event.eventId}">${ticket.event.title}</a></p>--%>
<%--                <p><strong>Type:</strong> <c:out value="${ticket.ticketType}" /></p>--%>
<%--                <p><strong>Price:</strong> $<fmt:formatNumber value="${ticket.price}" minFractionDigits="2" /></p>--%>
<%--                <p><strong>Quantity Available:</strong> <c:out value="${ticket.quantityAvailable}" /></p>--%>
<%--                <p><strong>Sale Start Date:</strong> \n  <c:choose>\n    <c:when test=\"${not empty ticket.saleStartDate}\">\n      <fmt:formatDate value=\"${ticket.saleStartDate}\" pattern=\"yyyy-MM-dd HH:mm\" />\n    </c:when>\n    <c:otherwise>N/A</c:otherwise>\n  </c:choose>\n</p>--%>
<%--                <p><strong>Sale End Date:</strong> \n  <c:choose>\n    <c:when test=\"${not empty ticket.saleEndDate}\">\n      <fmt:formatDate value=\"${ticket.saleEndDate}\" pattern=\"yyyy-MM-dd HH:mm\" />\n    </c:when>\n    <c:otherwise>N/A</c:otherwise>\n  </c:choose>\n</p>--%>
<%--                <p><strong>Description:</strong> <c:out value="${ticket.description}" /></p>--%>
<%--            </div>--%>
<%--        </div>--%>
<%--        <a href="${pageContext.request.contextPath}/events/${ticket.event.eventId}" class="btn btn-secondary">Back to Event</a>--%>
<%--    </div>--%>
<%--    --%>
<%--    <script src="${pageContext.request.contextPath}/js/jquery-3.5.1.min.js"></script>--%>
<%--    <script src="${pageContext.request.contextPath}/js/bootstrap.min.js"></script>--%>
<%--</body>--%>
<%--</html> -->--%>
