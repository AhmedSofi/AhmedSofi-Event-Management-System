<!-- <%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Create Ticket for Event - ${event.title}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
    <div class="container mt-4">
        <h2>Create Ticket for Event: <c:out value="${event.title}" /></h2>
        <form action="${pageContext.request.contextPath}/tickets/create" method="post">
            <input type="hidden" name="eventId" value="${event.eventId}" />
            <div class="form-group">
                <label for="ticketType">Ticket Type</label>
                <input type="text" class="form-control" id="ticketType" name="ticketType" required>
            </div>
            <div class="form-group">
                <label for="price">Price</label>
                <input type="number" step="0.01" class="form-control" id="price" name="price" required>
            </div>
            <div class="form-group">
                <label for="quantityAvailable">Quantity Available</label>
                <input type="number" class="form-control" id="quantityAvailable" name="quantityAvailable" required>
            </div>
            <div class="form-group">
                <label for="description">Description</label>
                <textarea class="form-control" id="description" name="description" rows="3"></textarea>
            </div>
            <div class="form-group">
                <label for="saleStartDate">Sale Start Date</label>
                <input type="datetime-local" class="form-control" id="saleStartDate" name="saleStartDate">
            </div>
            <div class="form-group">
                <label for="saleEndDate">Sale End Date</label>
                <input type="datetime-local" class="form-control" id="saleEndDate" name="saleEndDate">
            </div>
            <button type="submit" class="btn btn-primary">Add Ticket</button>
            <a href="${pageContext.request.contextPath}/events/${event.eventId}" class="btn btn-secondary">Cancel</a>
        </form>
    </div>
    
    <script src="${pageContext.request.contextPath}/js/jquery-3.5.1.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/bootstrap.min.js"></script>
</body>
</html> -->
