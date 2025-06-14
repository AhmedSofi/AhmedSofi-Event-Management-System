<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%-- Assuming you have set pageTitle in the servlet or here --%>
<c:set var="pageTitle" value="Notifications" scope="request"/>
<%-- Include the base layout AFTER setting pageTitle --%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/all.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
<jsp:include page="/WEB-INF/views/layouts/base.jsp"/>

<div class="container mt-4"> <%-- Added some margin top --%>
    <div class="row justify-content-center">
        <div class="col-md-8">
            <div class="card">
                <div class="card-header">
                    <div class="d-flex justify-content-between align-items-center">
                        <h2 class="mb-0">Notifications</h2>
                        <%-- Button Removed --%>
                    </div>
                </div>
                <div class="card-body">
                    <c:choose>
                        <c:when test="${not empty notifications}">
                            <div class="list-group">
                                <c:forEach items="${notifications}" var="notification">
                                    <%-- Added data-notification-id attribute --%>
                                    <a href="#" class="list-group-item list-group-item-action ${!notification.read ? 'list-group-item-primary font-weight-bold' : ''}"
                                       data-notification-id="${notification.notificationId}">
                                        <div class="d-flex w-100 justify-content-between">
                                            <h5 class="mb-1">${notification.title}</h5>
                                            <small class="text-muted"> <%-- Use text-muted for read time --%>
                                                <c:if test="${not empty notification.createdDateUtil}">
                                                    <fmt:formatDate value="${notification.createdDateUtil}" pattern="MMM d, h:mm a"/>
                                                </c:if>
                                            </small>
                                        </div>
                                        <p class="mb-1">${notification.message}</p>
                                            <%-- Removed the explicit "New" text, rely on primary background/bold --%>
                                    </a>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="alert alert-info">
                                You don't have any notifications yet.
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/jquery-3.5.1.min.js"></script>
<script src="${pageContext.request.contextPath}/js/bootstrap.min.js"></script>
<script src="${pageContext.request.contextPath}/js/scripts.js"></script>

<%-- Ensure jQuery is loaded, usually in base.jsp or loaded before this script --%>
<script>
    // Wrap in document ready to ensure elements exist
    $(document).ready(function() {

        // Mark notification as read when clicked
        // Use event delegation for potentially dynamically added items in future
        $('.list-group').on('click', '.list-group-item', function(e) {
            e.preventDefault(); // Prevent default link behavior

            // Only mark as read if it's currently unread
            if ($(this).hasClass('list-group-item-primary')) {
                const notificationId = $(this).data('notification-id'); // Get ID from data attribute
                const clickedItem = $(this); // Store reference to the clicked item

                if (notificationId) {
                    // --- CORRECTED URL and DATA ---
                    $.post('${pageContext.request.contextPath}/notifications/mark-read', { notificationId: notificationId }, function() {
                        // Success: Visually mark as read without full page reload
                        clickedItem.removeClass('list-group-item-primary font-weight-bold');
                        // Optional: update the count in the header badge immediately
                        // updateHeaderBadgeCount(-1); // You would need to implement this function
                    }).fail(function() {
                        // Handle error - maybe show a message
                        console.error("Failed to mark notification " + notificationId + " as read.");
                        alert("Could not mark notification as read. Please try again.");
                    });
                }
            }
        });

        // Mark all notifications as read - CLICK HANDLER REMOVED
        // $('#markAllAsReadBtn').click(function() { ... }); // This block was removed

    });
</script>