<%@ page import="java.util.List" %>
<%@ page import="com.ems.models.Event" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="pageTitle" value="My Events" scope="request" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/all.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
<jsp:include page="/WEB-INF/views/layouts/base.jsp" />

<div class="container mt-4">
    <div class="row mb-4">
        <div class="col">
            <h1>My Events</h1>
        </div>
        <div class="col-auto">
            <a href="${pageContext.request.contextPath}/events/new" class="btn btn-primary" ui_test="create-event-button">
                <i class="fas fa-plus"></i> Create New Event
            </a>
        </div>
    </div>

    <c:choose>
        <c:when test="${not empty events}">
            <div class="row">
                <c:forEach items="${events}" var="event">
                    <div class="col-md-4 mb-4">
                        <div class="card h-100 shadow-sm">
                            <!-- <div class="h-50"> -->
                            <c:if test="${not empty event.imageUrl}">
                                <img src="${pageContext.request.contextPath}/${event.imageUrl}" class="card-img-top" alt="${event.title}">
                            </c:if>
                            <!-- </div> -->
                            <div class="card-body">
                                <h5 class="card-title">${event.title}</h5>
                                <p class="card-text text-muted">
                                    <i class="far fa-calendar-alt"></i> ${event.formattedStartDateTime}
                                </p>
                                <p class="card-text text-muted">
                                    <i class="far fa-calendar-alt"></i> ${event.formattedEndDateTime}
                                </p>
                                <p class="card-text text-truncate">${event.description}</p>
                                <span class="badge badge-${event.status == 'PUBLISHED' ? 'success' :
                                    event.status == 'CANCELLED' ? 'danger' : 'warning'}">
                                        ${event.status}
                                </span>
                            </div>
                            <div class="card-footer bg-white">
                                <div class="btn-group btn-group-sm">
                                    <a href="${pageContext.request.contextPath}/events/${event.eventId}"
                                       class="btn btn-outline-primary">
                                        <i class="far fa-eye"></i> View
                                    </a>
                                    <a href="${pageContext.request.contextPath}/events/${event.eventId}/edit"
                                       class="btn btn-outline-secondary">
                                        <i class="far fa-edit"></i> Edit
                                    </a>
                                    <c:if test="${event.status != 'CANCELLED'}">
                                        <a href="${pageContext.request.contextPath}/events/${event.eventId}/cancel"
                                           class="btn btn-outline-danger"
                                           onclick="return confirm('Are you sure you want to cancel this event?')">
                                            <i class="fas fa-ban"></i> Cancel
                                        </a>
                                    </c:if>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:when>
        <c:otherwise>
            <div class="alert alert-info">
                <i class="fas fa-info-circle"></i> You don't have any events yet.
                <a href="${pageContext.request.contextPath}/events/new">Create your first event</a>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<script src="${pageContext.request.contextPath}/js/jquery-3.5.1.min.js"></script>
<script src="${pageContext.request.contextPath}/js/bootstrap.min.js"></script>
<script src="${pageContext.request.contextPath}/js/scripts.js"></script>