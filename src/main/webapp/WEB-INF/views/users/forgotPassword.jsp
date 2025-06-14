<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="pageTitle" value="Forgot Password" scope="request" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/all.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
<jsp:include page="/WEB-INF/views/layouts/base.jsp" />

<div class="row justify-content-center">
  <div class="col-md-6">
    <div class="card">
      <div class="card-header">
        <h2 class="card-title">Forgot Password</h2>
      </div>
      <div class="card-body">
        <!-- Show message if exists -->
        <c:if test="${not empty message}">
          <div class="alert alert-success" role="alert">
              ${message}
          </div>
        </c:if>

        <!-- Show error if exists -->
        <c:if test="${not empty error}">
          <div class="alert alert-danger" role="alert">
              ${error}
          </div>
        </c:if>

        <p>Enter your email address to receive password reset instructions.</p>

        <form action="${pageContext.request.contextPath}/forgotPassword" method="post">
          <div class="form-group">
            <label for="email">Email Address</label>
            <input type="email" class="form-control" id="email" name="email" ui_test="forgot-password-email" required>
          </div>

          <div class="form-group">
            <button type="submit" class="btn btn-primary btn-block" ui_test="forgot-password-button">Send Reset Link</button>
          </div>

          <div class="text-center mt-3">
            <a href="${pageContext.request.contextPath}/login" ui_test="back-to-login-button">Back to Login</a>
          </div>
        </form>
      </div>
    </div>
  </div>
</div>