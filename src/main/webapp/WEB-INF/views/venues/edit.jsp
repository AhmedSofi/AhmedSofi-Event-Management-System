<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/WEB-INF/views/layouts/navbar.jsp" />

<div class="container mt-5">
    <div class="card mx-auto" style="max-width: 600px;">
        <div class="card-header bg-warning text-dark">
            <h4 class="mb-0">Edit Venue</h4>
        </div>
        <div class="card-body">
            <form method="post" action="${pageContext.request.contextPath}/venues/edit">
                <input type="hidden" name="venueId" value="${venue.venueId}" />

                <div class="mb-3">
                    <label class="form-label">Venue Name</label>
                    <input type="text" name="name" class="form-control" value="${venue.name}" ui_test="edit-venue-name" required>
                </div>

                <div class="mb-3">
                    <label class="form-label">Address</label>
                    <input type="text" name="address" class="form-control" value="${venue.address}" ui_test="edit-venue-address" required>
                </div>

                <div class="mb-3">
                    <label class="form-label">Capacity</label>
                    <input type="number" name="capacity" class="form-control" value="${venue.capacity}" ui_test="edit-venue-capacity" required>
                </div>

                <div class="mb-3">
                    <label class="form-label">Contact Person</label>
                    <input type="text" name="contactPerson" class="form-control" value="${venue.contactPerson}" ui_test="edit-venue-contact-person">
                </div>

                <div class="mb-3">
                    <label class="form-label">Phone</label>
                    <input type="text" name="contactPhone" class="form-control" value="${venue.contactPhone}" ui_test="edit-venue-contact-phone">
                </div>

                <div class="mb-3">
                    <label class="form-label">Email</label>
                    <input type="email" name="contactEmail" class="form-control" value="${venue.contactEmail}" ui_test="edit-venue-contact-email">
                </div>

                <button type="submit" class="btn btn-warning">Update Venue</button>
                <a href="${pageContext.request.contextPath}/venues" class="btn btn-outline-secondary ms-2" ui_test="cancel-venue-button">Cancel</a>
            </form>
        </div>
    </div>
</div>
