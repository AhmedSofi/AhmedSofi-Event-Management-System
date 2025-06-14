<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/WEB-INF/views/layouts/base.jsp" />

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>My Profile</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>

<div class="container py-5">
    <div class="row">
        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show mt-3" role="alert">
                    ${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>
        <!-- Profile Sidebar -->
        <div class="col-md-3 text-center">
            <div class="rounded-circle bg-primary text-white d-flex align-items-center justify-content-center mx-auto"
                 style="width: 100px; height: 100px; font-size: 2rem;">
                ${fn:substring(user.firstName, 0, 1)}${fn:substring(user.lastName, 0, 1)}
            </div>
            <h4 class="mt-3">${user.firstName} ${user.lastName}</h4>
            <p class="text-muted">${user.role}</p>
            <button class="btn btn-outline-primary btn-sm w-100 mb-2" data-bs-toggle="modal" data-bs-target="#editProfileModal">Edit Profile</button>
            <button class="btn btn-outline-secondary btn-sm w-100" data-bs-toggle="modal" data-bs-target="#changePasswordModal">Change Password</button>
        </div>

        <!-- Main Content -->
        <div class="col-md-9">
            <!-- Account Info -->
            <div class="card mb-4">
                <div class="card-header fw-bold">Account Details</div>
                <div class="card-body">
                    <p><strong>Username:</strong> ${user.username}</p>
                    <p><strong>Email:</strong> ${user.email}</p>

                </div>
            </div>

            <!-- Activities -->
            <div class="card">
                <div class="card-header fw-bold">My Activities</div>
                <div class="card-body">
                    <!-- Tabs -->
                    <ul class="nav nav-tabs mb-3" id="profileTabs" role="tablist">
                        <li class="nav-item" role="presentation">
                            <button class="nav-link active" id="attending-tab" data-bs-toggle="tab" data-bs-target="#attending" type="button" role="tab">✔️ Attending</button>
                        </li>
                        <li class="nav-item" role="presentation">
                            <button class="nav-link" id="orders-tab" data-bs-toggle="tab" data-bs-target="#orders" type="button" role="tab">🛒 My Orders</button>
                        </li>
                        <li class="nav-item" role="presentation">
                            <button class="nav-link" id="feedback-tab" data-bs-toggle="tab" data-bs-target="#feedback" type="button" role="tab">📝 My Feedback</button>
                        </li>
                    </ul>

                    <!-- Tab Content -->
                    <div class="tab-content" id="profileTabsContent">
                        <!-- Attending -->
                        <div class="tab-pane fade show active" id="attending" role="tabpanel">
                            <c:choose>
                                <c:when test="${not empty events}">
                                    <ul class="list-group">
                                        <c:forEach items="${events}" var="event">
                                            <li class="list-group-item">
                                                <strong>${event.title}</strong>
                                            </li>
                                        </c:forEach>
                                    </ul>
                                </c:when>
                                <c:otherwise>
                                    <p class="text-muted">No events found.</p>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <!-- Orders -->
                        <div class="tab-pane fade" id="orders" role="tabpanel">
                            <c:choose>
                                <c:when test="${not empty orders}">
                                    <table class="table table-striped">
                                        <thead>
                                        <tr>
                                            <th>Order #</th>
                                            <th>Amount</th>
                                            <th>Status</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <c:forEach items="${orders}" var="order">
                                            <tr>
                                                <td>${order.orderId}</td>

                                                <td>$<fmt:formatNumber value="${order.totalAmount}" minFractionDigits="2"/></td>
                                                <td>${order.status}</td>
                                            </tr>
                                        </c:forEach>
                                        </tbody>
                                    </table>
                                </c:when>
                                <c:otherwise>
                                    <p class="text-muted">No orders found.</p>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <!-- Feedback -->
                        <div class="tab-pane fade" id="feedback" role="tabpanel">
                            <c:choose>
                                <c:when test="${not empty feedbacks}">
                                    <ul class="list-group">
                                        <c:forEach items="${feedbacks}" var="fb">
                                            <li class="list-group-item">
                                                <strong>${fb.event.title}</strong> — ${fb.comments}<br/>
                                                <small>Rated: ${fb.rating}/5</small>
                                            </li>
                                        </c:forEach>
                                    </ul>
                                </c:when>
                                <c:otherwise>
                                    <p class="text-muted">No feedback submitted yet.</p>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Edit Profile Modal -->
    <div class="modal fade" id="editProfileModal" tabindex="-1">
        <div class="modal-dialog">
            <form method="post" action="${pageContext.request.contextPath}/editProfile" class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Edit Profile</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3"><label for="firstName" class="form-label">First Name</label><input type="text" id="firstName" name="firstName" class="form-control" value="${user.firstName}"></div>
                    <div class="mb-3"><label for="lastName" class="form-label">Last Name</label><input type="text" id="lastName" name="lastName" class="form-control" value="${user.lastName}"></div>
                    <div class="mb-3"><label for="email" class="form-label">Email</label><input type="email" id="email" name="email" class="form-control" value="${user.email}"></div>
                </div>
                <div class="modal-footer">
                    <button type="submit" class="btn btn-primary">Save Changes</button>
                    <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancel</button>
                </div>
            </form>
        </div>
    </div>

    <!-- Change Password Modal -->
    <div class="modal fade" id="changePasswordModal" tabindex="-1">
        <div class="modal-dialog">
            <form method="post" action="${pageContext.request.contextPath}/changePassword" class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Change Password</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <c:if test="${not empty changePasswordError}">
                        <div class="alert alert-danger">${changePasswordError}</div>
                    </c:if>
                    <c:if test="${not empty passwordMismatchError}">
                        <div class="alert alert-danger">${passwordMismatchError}</div>
                    </c:if>
                    <div class="mb-3"><label for="currentPassword" class="form-label">Current Password</label><input type="password" id="currentPassword" name="currentPassword" class="form-control"></div>
                    <div class="mb-3"><label for="newPassword" class="form-label">New Password</label><input type="password" id="newPassword" name="newPassword" class="form-control"></div>
                    <div class="mb-3"><label for="confirmNewPassword" class="form-label">Confirm New Password</label><input type="password" id="confirmNewPassword" name="confirmNewPassword" class="form-control"></div>
                </div>
                <div class="modal-footer">
                    <button type="submit" class="btn btn-success">Change Password</button>
                    <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancel</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<script>
    window.addEventListener('DOMContentLoaded', () => {
        const shouldOpen = '${openChangePasswordModal}' === 'true';
        if (shouldOpen) {
            const modal = new bootstrap.Modal(document.getElementById('changePasswordModal'));
            modal.show();
        }
    });
</script>

</body>
</html>
