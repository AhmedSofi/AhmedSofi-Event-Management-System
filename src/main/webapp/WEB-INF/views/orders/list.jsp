<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/all.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
<c:set var="pageTitle" value="My Orders" scope="request" />
<jsp:include page="/WEB-INF/views/layouts/base.jsp" />

<div class="container mt-4">
  <h1>My Orders</h1>

  <c:if test="${not empty sessionScope.message}">
    <div class="alert alert-success">
        ${sessionScope.message}
      <c:remove var="message" scope="session" />
    </div>
  </c:if>

  <c:choose>
    <c:when test="${empty orders}">
      <div class="alert alert-info">
        You don't have any orders yet. <a href="${pageContext.request.contextPath}/events">Browse events</a> to purchase tickets.
      </div>
    </c:when>
    <c:otherwise>
      <div class="list-group">
        <c:forEach items="${orders}" var="order" varStatus="loop">
          <a href="${pageContext.request.contextPath}/orders/${order.orderId}" class="list-group-item list-group-item-action">
            <div class="d-flex justify-content-between align-items-center">
              <div>
                <h5>Order ${loop.index + 1}</h5>
                <small class="text-muted">ID: ${order.orderId}</small>
                <p class="mb-1">Date: ${order.formattedOrderDate}</p>
                <p class="mb-1">Items: ${order.orderItems.size()}</p>
              </div>
              <div class="text-end">
                <h5>$<fmt:formatNumber value="${order.totalAmount}" pattern="#,##0.00"/></h5>
                <span class="badge
    ${order.status eq 'PAID' ? 'bg-success' :
    order.status eq 'PENDING' ? 'bg-warning' :
    order.status eq 'CANCELLED' ? 'bg-danger' :
    order.status eq 'REFUNDED' ? 'bg-info' : 'bg-secondary'}">
                    ${order.status}
                </span>
              </div>
            </div>
          </a>
        </c:forEach>

      </div>
    </c:otherwise>
  </c:choose>
</div>



<script src="${pageContext.request.contextPath}/js/jquery-3.5.1.min.js"></script>
<script src="${pageContext.request.contextPath}/js/bootstrap.min.js"></script>
<script src="${pageContext.request.contextPath}/js/scripts.js"></script>