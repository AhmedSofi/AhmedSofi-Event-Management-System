<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Submit Feedback</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css">
    <style>
        .star-rating i {
            font-size: 1.5rem;
            color: #ccc;
            cursor: pointer;
        }
        .star-rating i.checked {
            color: #ffc107;
        }
    </style>
</head>
<body class="bg-light">
<div class="container mt-5">
    <c:if test="${not empty error}">
        <div class="alert alert-danger" role="alert">
                ${error}
        </div>
    </c:if>


    <div class="card mx-auto" style="max-width: 600px;">
        <div class="card-header bg-primary text-white">
            <h4 class="mb-0">Submit Feedback</h4>
        </div>
        <div class="card-body">
            <form method="post" action="${pageContext.request.contextPath}/feedback">
                <div class="mb-3">
                    <label for="eventId" class="form-label">Select Event</label>
                    <select class="form-select" id="eventId" name="eventId" required>
                        <option value="" disabled selected>Select an event...</option>
                        <c:forEach items="${events}" var="event">
                            <option value="${event.eventId}">${event.title}</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="mb-3">
                    <label class="form-label">Rating</label>
                    <div class="star-rating" id="starRating">
                        <c:forEach begin="1" end="5" var="i">
                            <i class="bi bi-star" data-value="${i}"></i>
                        </c:forEach>
                    </div>
                    <input type="hidden" name="rating" id="rating" required>
                </div>

                <div class="mb-3">
                    <label for="comments" class="form-label">Comments (optional)</label>
                    <textarea class="form-control" id="comments" name="comments" rows="4"></textarea>
                </div>

                <div class="d-flex justify-content-between">
                    <a href="${pageContext.request.contextPath}/profile" class="btn btn-outline-secondary">Cancel</a>
                    <button type="submit" class="btn btn-primary">Submit Feedback</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
    // Star click behavior
    const stars = document.querySelectorAll('#starRating i');
    const ratingInput = document.getElementById('rating');

    stars.forEach(star => {
        star.addEventListener('click', function () {
            const value = parseInt(this.getAttribute('data-value'));
            ratingInput.value = value;

            stars.forEach(s => s.classList.remove('checked'));
            for (let i = 0; i < value; i++) {
                stars[i].classList.add('checked');
            }
        });
    });
</script>
</body>
</html>
