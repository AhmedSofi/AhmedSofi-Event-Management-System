<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="pageTitle" value="Reset Password" scope="request" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/all.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
<jsp:include page="/WEB-INF/views/layouts/base.jsp" />

<div class="row justify-content-center">
  <div class="col-md-6">
    <div class="card">
      <div class="card-header">
        <h2 class="card-title">Reset Password</h2>
      </div>
      <div class="card-body">
        <!-- Show error if exists -->
        <c:if test="${not empty error}">
          <div class="alert alert-danger" role="alert">
              ${error}
          </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/resetPassword" method="post">
          <input type="hidden" name="token" value="${token}">

          <div class="form-group">
            <label for="newPassword">New Password</label>
            <input type="password" class="form-control" id="newPassword" name="newPassword" required>
          </div>

          <div class="form-group">
            <label for="confirmPassword">Confirm Password</label>
            <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" required>
          </div>

          <div class="form-group">
            <button type="submit" class="btn btn-primary btn-block">Reset Password</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</div>