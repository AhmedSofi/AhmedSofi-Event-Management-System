<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="pageTitle" value="Page Not Found" scope="request" />
<jsp:include page="/WEB-INF/views/layouts/base.jsp" />

<div class="row justify-content-center">
    <c:if test="${not empty error}">
        <div class="alert alert-danger" role="alert">
                ${error}
        </div>
    </c:if>
    <div class="col-md-6 text-center">
        <div class="mt-5">
            <h1 class="display-1 text-primary">404</h1>
            <h2 class="mb-4">Page Not Found</h2>
            <p class="lead">
                The page you are looking for might have been removed, had its name changed, or is temporarily unavailable.
            </p>
            <a href="${pageContext.request.contextPath}/" class="btn btn-primary mt-3">
                Go to Homepage
            </a>
        </div>
    </div>
</div>
