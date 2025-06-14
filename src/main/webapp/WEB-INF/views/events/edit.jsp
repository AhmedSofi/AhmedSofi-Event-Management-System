<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="pageTitle" value="Edit Event - ${event.title}" scope="request" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/all.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
<jsp:include page="/WEB-INF/views/layouts/base.jsp" />

<div class="row justify-content-center" id="updateEventID">
    <div class="col-md-8">
        <div class="card">
            <div class="card-header">
                <h2 class="card-title">Edit Event</h2>
            </div>
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/events/${event.eventId}" method="post" enctype="multipart/form-data">

                    <div class="form-group">
                        <label for="title">Event Title</label>
                        <input type="text" class="form-control" id="title" name="title"
                               value="<c:out value="${event.title}" />" required>
                    </div>

                    <div class="form-group">
                        <label for="description">Description</label>
                        <textarea class="form-control" id="description" name="description"
                                  rows="4" required><c:out value="${event.description}" /></textarea>
                    </div>

                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label for="startDateTime">Start Date & Time</label>
                                <input type="datetime-local" class="form-control" id="startDateTime"
                                       name="startDateTime" required
                                       value="<c:out value="${event.startDateTime}" />" >
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-group">
                                <label for="endDateTime">End Date & Time</label>
                                <input type="datetime-local" class="form-control" id="endDateTime"
                                       name="endDateTime" required
                                       value="<c:out value="${event.endDateTime}" />" >
                            </div>
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="venueId">Venue</label>
                        <select class="form-control" id="venueId" name="venueId">
                            <option value="">Select a venue...</option>
                            <c:forEach items="${venues}" var="venue">
                                <option value="${venue.venueId}"
                                    ${event.venue != null && event.venue.venueId == venue.venueId ? 'selected' : ''}>
                                    <c:out value="${venue.name}" /> (Capacity: ${venue.capacity})
                                </option>
                            </c:forEach>
                        </select>
                    </div>


                    <div class="form-group">
                                            <label for="eventImage">Event Image</label>
                                            <c:if test="${not empty event.imageUrl}">
                                                <div class="mb-2">
                                                    <img src="${pageContext.request.contextPath}/${event.imageUrl}"
                                                         style="max-width: 200px; max-height: 150px;" class="img-thumbnail" alt="${event.title}">
                                                </div>
                                                <div class="custom-control custom-checkbox mb-2">
                                                    <input type="checkbox" class="custom-control-input" id="removeImage" name="removeImage">
                                                    <label class="custom-control-label" for="removeImage">Remove current image</label>
                                                </div>
                                            </c:if>
                                            <input type="file" class="form-control form-control-sm" id="eventImage" name="eventImage" accept="image/*">
                                            <small class="form-text text-muted">Upload a new image for your event (optional) max size is 10MB</small>
                                        </div>

                    <div class="form-group">
                        <label for="status">Status</label>
                        <select class="form-control" id="status" name="status">
                            <option value="DRAFT" ${event.status == 'DRAFT' ? 'selected' : ''}>Draft</option>
                            <option value="PUBLISHED" ${event.status == 'PUBLISHED' ? 'selected' : ''}>Published</option>
                            <option value="CANCELLED" ${event.status == 'CANCELLED' ? 'selected' : ''}>Cancelled</option>
                        </select>
                    </div>

                    <div class="form-group mt-2">
                        <button type="submit" class="btn btn-primary">Update Event</button>
                        <a href="${pageContext.request.contextPath}/events/${event.eventId}" class="btn btn-secondary">Cancel</a>
                    </div>
                </form>
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
                            <button type="button" class="btn btn-secondary closo btn btn-secondary" data-dismiss="modal"
                                style="margin-left: 10px; background-color: #6c757d; border-color: #6c757d; color: white;"
                                data-dismiss="modal">Close</button>
                        </div>
                    </div>
                </div>
            </div>



            <script>

                document.addEventListener('DOMContentLoaded', function () {
                    // For the edit ticket form
                    const updateEvent = document.getElementById('updateEventID');
                    if (updateEvent) {
                        updateEvent.addEventListener('submit', function (event) {
                            // Get the form that triggered the event
                            const form = event.target;
                            const startDate = new Date(form.querySelector('[name="startDateTime"]').value);
                            const endDate = new Date(form.querySelector('[name="endDateTime"]').value);

                            if (endDate < startDate) {
                                event.preventDefault();
                                showErrorModal('Error: Event end date cannot be before the start date.');
                            }
                        });
                    }
                });


                // Function to show the error modal
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

            <script src="${pageContext.request.contextPath}/js/jquery-3.5.1.min.js"></script>
            <script src="${pageContext.request.contextPath}/js/bootstrap.min.js"></script>
            <script src="${pageContext.request.contextPath}/js/scripts.js"></script>