<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Add Venue</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<div class="container mt-5">
    <div class="card mx-auto" style="max-width: 600px;">
        <div class="card-header bg-primary text-white">
            <h4 class="mb-0">Add New Venue</h4>
        </div>
        <div class="card-body">
            <form method="post" action="${pageContext.request.contextPath}/venues/add">
                <div class="mb-3">
                    <label class="form-label">Venue Name</label>
                    <input type="text" name="name" class="form-control" ui_test="add-venue-name" required>
                </div>
                <div class="mb-3">
                    <label class="form-label">Address</label>
                    <input type="text" name="address" class="form-control" ui_test="add-venue-address" required>
                </div>
                <div class="mb-3">
                    <label class="form-label">Capacity</label>
                    <input type="number" name="capacity" class="form-control" min="1" max="100000" ui_test="add-venue-capacity" required>
                </div>
                <div class="mb-3">
                    <label class="form-label">Contact Person</label>
                    <input type="text" name="contactPerson" class="form-control" ui_test="add-venue-contact-person">
                </div>
                <div class="mb-3">
                    <label class="form-label">Phone</label>
                    <input type="text" name="contactPhone" class="form-control" ui_test="add-venue-contact-phone" pattern="^[+]?[0-9]{10,15}$" title="Enter a valid phone number (10-15 digits)"
                    >
                </div>
                <div class="mb-3">
                    <label class="form-label">Email</label>
                    <input type="email" name="contactEmail" class="form-control" ui_test="add-venue-contact-email">
                </div>
                <button type="submit" class="btn btn-primary" ui_test="submit-venue-button">Add Venue</button>
            </form>

            <script>
                document.getElementById('addVenueForm').addEventListener('submit', function(e) {
                    const form = e.target;
                    const capacityField = form.querySelector('input[name="capacity"]');
                    const capacity = parseInt(capacityField.value, 10);

                    if (isNaN(capacity) || capacity < 1 || capacity > 100000) {
                        alert('Please enter a valid capacity between 1 and 100,000.');
                        e.preventDefault(); // Prevent the form from being submitted
                    }
                });
            </script>

        </div>
    </div>
</div>
</body>
</html>

