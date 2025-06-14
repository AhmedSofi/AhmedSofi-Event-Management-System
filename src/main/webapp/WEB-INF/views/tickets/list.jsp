<!-- 

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Tickets for Event - ${event.title}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/all.min.css">
</head>
<body>
    <div class="container mt-4">
        <h2>Tickets for Event: <c:out value="${event.title}" /></h2>
        
        <p>Tickets Count: <c:out value="${fn:length(tickets)}" /></p>
        <c:choose>
            <c:when test="${not empty tickets}">
                <div class="table-responsive">
                    <table class="table table-bordered table-hover">
                        <thead class="thead-light">
                            <tr>
                                <th>Ticket Type</th>
                                <th>Price</th>
                                <th>Quantity Available</th>
                                <th>Sale Start</th>
                                <th>Sale End</th>
                                <th>Description</th>
                                
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${tickets}" var="ticket">
                                <tr>
                                    <td><c:out value="${ticket.ticketType}" /></td>
                                    <td>$<fmt:formatNumber value="${ticket.price}" minFractionDigits="2" /></td>
                                    <td><c:out value="${ticket.quantityAvailable}" /></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty ticket.saleStartDate}">
                                                <fmt:formatDate value="${ticket.saleStartDate}" pattern="yyyy-MM-dd HH:mm"/>
                                            </c:when>
                                            <c:otherwise>N/A</c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty ticket.saleEndDate}">
                                                <fmt:formatDate value="${ticket.saleEndDate}" pattern="yyyy-MM-dd HH:mm"/>
                                            </c:when>
                                            <c:otherwise>N/A</c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td><c:out value="${ticket.description}" /></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:when>
            <c:otherwise>
                <div class="alert alert-info">
                    No tickets available for this event.
                </div>
            </c:otherwise>
        </c:choose>
        <a href="${pageContext.request.contextPath}/events/${event.eventId}" class="btn btn-secondary mt-3">
            Back to Event
        </a>
    </div>
    
    <script src="${pageContext.request.contextPath}/js/jquery-3.5.1.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/bootstrap.min.js"></script>
</body>
</html>

 -->
