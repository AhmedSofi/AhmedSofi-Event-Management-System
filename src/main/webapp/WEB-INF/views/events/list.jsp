<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="pageTitle" value="Events" scope="request" />
<jsp:include page="/WEB-INF/views/layouts/base.jsp" />

<div class="container mt-4">
    <div class="row mb-4">
        <div class="col">
            <h1>Events</h1>
        </div>
    </div>


    <!-- Nav Tabs -->
    <ul class="nav nav-tabs" id="eventTabs" role="tablist">
        <li class="nav-item">
            <button class="nav-link active" id="upcoming-tab" data-bs-toggle="tab" data-bs-target="#upcoming" type="button" role="tab" aria-controls="upcoming" aria-selected="true" ui_test="events-upcoming-tab">
                Upcoming & Current Events
            </button>
        </li>
        <li class="nav-item">
            <button class="nav-link" id="past-tab" data-bs-toggle="tab" data-bs-target="#past" type="button" role="tab" aria-controls="past" aria-selected="false" ui_test="events-past-tab">
                Past Events
            </button>
        </li>
        <li class="nav-item">
            <button class="nav-link" id="canceled-tab" data-bs-toggle="tab" data-bs-target="#canceled" type="button" role="tab" aria-controls="canceled" aria-selected="false" ui_test="events-canceled-tab">
                Canceled Events
            </button>
        </li>
    </ul>

    <!-- Tab Content -->
    <div class="tab-content mt-3" id="eventTabsContent">
        <!-- Upcoming Events -->
        <div class="tab-pane fade show active" id="upcoming" role="tabpanel" aria-labelledby="upcoming-tab">
            <c:choose>
                <c:when test="${not empty upcomingEvents}">
                    <div class="row">
                        <c:forEach items="${upcomingEvents}" var="event">
                            <div class="col-md-4 mb-4">
                                <div class="card h-100 shadow-sm">
                                    <c:if test="${not empty event.imageUrl}">
                                        <img src="${pageContext.request.contextPath}/${event.imageUrl}" class="card-img-top" alt="${event.title}">
                                    </c:if>
                                    <div class="card-body card-link" href="${pageContext.request.contextPath}/events/${event.eventId}">
                                        <h5 class="card-title">
                                            <a href="${pageContext.request.contextPath}/events/${event.eventId}" class="text-decoration-none text-dark">
                                                <c:out value="${event.title}" />
                                            </a>
                                        </h5>
                                        <p class="card-text text-muted">${event.formattedStartDate}</p>
                                        <p class="card-text">
                                            <c:out value="${event.venue != null ? event.venue.name : 'Location TBD'}" />
                                        </p>
                                        <p class="card-text text-truncate">
                                            <c:out value="${event.description}" />
                                        </p>
                                    </div>
                                        <div class="card-footer bg-white">
                                        <span class="badge
                                        ${event.status == 'PUBLISHED' ? 'bg-success' :
                                        event.status == 'DRAFT' ? 'bg-warning text-dark' :
                                        'bg-secondary'}">
                                        <c:out value="${event.status}" />
                                        </span>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="alert alert-info">No upcoming events found.</div>
                </c:otherwise>
            </c:choose>
        </div>


        <!-- Past Events -->
        <div class="tab-pane fade" id="past" role="tabpanel" aria-labelledby="past-tab">
            <c:choose>
                <c:when test="${not empty pastEvents}">
                    <div class="row">
                        <c:forEach items="${pastEvents}" var="event">
                            <div class="col-md-4 mb-4">
                                <div class="card h-100 shadow-sm">
                                    <c:if test="${not empty event.imageUrl}">
                                        <img src="${pageContext.request.contextPath}/${event.imageUrl}" class="card-img-top" alt="${event.title}">
                                    </c:if>
                                    <div class="card-body">
                                        <h5 class="card-title">
                                            <a href="${pageContext.request.contextPath}/events/${event.eventId}" class="text-decoration-none text-dark">
                                                <c:out value="${event.title}" />
                                            </a>
                                        </h5>
                                        <p class="card-text text-muted">${event.formattedStartDate}</p>
                                        <p class="card-text">
                                            <c:out value="${event.venue != null ? event.venue.name : 'Location TBD'}" />
                                        </p>
                                        <p class="card-text text-truncate">
                                            <c:out value="${event.description}" />
                                        </p>
                                    </div>
                                    <div class="card-footer bg-white">
                                        <span class="badge bg-secondary">PAST</span>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="alert alert-info">No past events found.</div>
                </c:otherwise>
            </c:choose>
        </div>

        <!-- Canceled Events -->
        <div class="tab-pane fade" id="canceled" role="tabpanel" aria-labelledby="canceled-tab">
            <c:choose>
                <c:when test="${not empty canceledEvents}">
                    <div class="row">
                        <c:forEach items="${canceledEvents}" var="event">
                            <div class="col-md-4 mb-4">
                                <div class="card h-100 shadow-sm">
                                    <c:if test="${not empty event.imageUrl}">
                                        <img src="${pageContext.request.contextPath}/${event.imageUrl}" class="card-img-top" alt="${event.title}">
                                    </c:if>
                                    <div class="card-body">
                                        <h5 class="card-title">
                                            <a href="${pageContext.request.contextPath}/events/${event.eventId}" class="text-decoration-none text-dark">
                                                <c:out value="${event.title}" />
                                            </a>
                                        </h5>
                                        <p class="card-text text-muted">${event.formattedStartDate}</p>
                                        <p class="card-text">
                                            <c:out value="${event.venue != null ? event.venue.name : 'Location TBD'}" />
                                        </p>
                                        <p class="card-text text-truncate">
                                            <c:out value="${event.description}" />
                                        </p>
                                    </div>
                                    <div class="card-footer bg-white">
                                        <span class="badge bg-danger">CANCELLED</span>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="alert alert-info">No canceled events found.</div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<!-- Make sure Bootstrap JS is loaded -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
