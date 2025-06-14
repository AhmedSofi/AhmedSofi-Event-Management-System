<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.min.css">

<c:set var="pageTitle" value="Create New Event" />
<jsp:include page="/WEB-INF/views/layouts/base.jsp" />

<div class="container mt-5" id="addEventID">
    <div class="row justify-content-center">
        <div class="col-md-8">
            <div class="card shadow-sm">
                <c:if test="${not empty error}">
                    <div class="alert alert-danger" role="alert">
                        ${error}
                    </div>
                </c:if>

                <div class="card-header bg-dark text-white">
                    <h3>Create New Event</h3>
                </div>
                <div class="card-body">
                    <c:choose>
                        <c:when test="${not empty venues}">
                            <form action="${pageContext.request.contextPath}/events" method="post" enctype="multipart/form-data">
                                <div class="mb-3">
                                    <label for="title" class="form-label">Event Title</label>
                                    <input type="text" class="form-control" id="title" name="title" ui_test="add-event-title" required>
                                </div>

                                <div class="mb-3">
                                    <label for="description" class="form-label">Description</label>
                                    <textarea class="form-control" id="description" name="description" rows="4" required ui_test="add-event-description"></textarea>
                                </div>

                                <div class="mb-3">
                                    <label for="venueId" class="form-label">Choose Venue</label>
                                    <select class="form-select" id="venueId" name="venueId" ui_test="add-event-venue" required>
                                        <option value="">Select a venue...</option>
                                        <c:forEach items="${venues}" var="venue">
                                            <option value="${venue.venueId}">
                                                ${venue.name} (Capacity: ${venue.capacity})
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>

                                <div class="row">
                                    <div class="col-md-6 mb-3">
                                        <label for="startDateTime" class="form-label">Start Date & Time</label>
                                        <input type="datetime-local" class="form-control" id="startDateTime" name="startDateTime" ui_test="add-event-start-date-time" required>
                                    </div>
                                    <div class="col-md-6 mb-3">
                                        <label for="endDateTime" class="form-label">End Date & Time</label>
                                        <input type="datetime-local" class="form-control" id="endDateTime" name="endDateTime"  ui_test="add-event-end-date-time" required>
                                    </div>
                                </div>

                                <div class="mb-3">
                                    <label for="eventImage" class="form-label">Event Image</label>
                                    <input type="file" class="form-control" id="eventImage" name="eventImage" ui_test="add-event-image" placeholder="Upload an image with max size 10MB">
                                </div>

                                <button type="submit" class="btn btn-primary" ui_test="add-event-button">Create Event</button>
                                <a href="${pageContext.request.contextPath}/events" class="btn btn-secondary" ui_test="cancel-add-event-button">Cancel</a>
                            </form>
                        </c:when>
                        <c:otherwise>
                            <div class="alert alert-warning">
                                <strong>No venues found!</strong> Please create a venue before adding events.
                            </div>
                            <a href="${pageContext.request.contextPath}/venues/add" class="btn btn-success" ui_test="add-venue-button">
                                + Create New Venue
                            </a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Error Message Modal -->
<div class="modal fade" id="errorModal" tabindex="-1" role="dialog" aria-labelledby="errorModalLabel"
    aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="errorModalLabel">Error</h5>
                <button type="button" class="closo close border-none"
                    style="color: red; font-size: 1.5rem; opacity: 0.8;" data-dismiss="modal"
                    aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <p id="errorMessage"></p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary closo" data-dismiss="modal"
                    style="margin-left: 10px;">Close</button>
            </div>
        </div>
    </div>
</div>

<!-- Scripts -->
<script src="${pageContext.request.contextPath}/js/jquery-3.5.1.min.js"></script>
<script src="${pageContext.request.contextPath}/js/bootstrap.min.js"></script>
<script src="${pageContext.request.contextPath}/js/scripts.js"></script>

<script>
    document.addEventListener('DOMContentLoaded', function () {
        const createEventForm = document.getElementById('addEventID');
        if (createEventForm) {
            createEventForm.addEventListener('submit', function (event) {
                const startDate = new Date(document.getElementById('startDateTime').value);
                const endDate = new Date(document.getElementById('endDateTime').value);

                if (endDate < startDate) {
                    event.preventDefault();
                    showErrorModal('Error: Event end date cannot be before the start date.');
                }
            });
        }
    });

    function showErrorModal(message) {
        document.getElementById('errorMessage').innerText = message;
        $('#errorModal').modal('show');
    }

    $(document).ready(function () {
        $(".closo").click(function () {
            $("#errorModal").modal("hide");
        });
    });
</script>
