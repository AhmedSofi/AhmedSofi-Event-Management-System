<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/all.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
<c:set var="pageTitle" value="Order Details" scope="request" />

<div class="container mt-4">
  <c:if test="${order == null}">
    <div class="alert alert-danger">Order not found or an error occurred.</div>
  </c:if>
  <c:if test="${order != null}">
    <div class="card">
      <div class="card-header d-flex justify-content-between align-items-center">
        <div>
          <h2 class="mb-0">Order Details</h2>
          <small class="text-muted">Order ID: ${order.orderId}</small>
        </div>
        <span class="badge
    ${order.status eq 'PAID' ? 'bg-success' :
    order.status eq 'PENDING' ? 'bg-warning' :
    order.status eq 'CANCELLED' ? 'bg-danger' :
    order.status eq 'REFUNDED' ? 'bg-info' : 'bg-secondary'}">
            ${order.status}
        </span>
      </div>

      <div class="card-body">
        <div class="row mb-4">
          <div class="col-md-6">
            <h5>Order Information</h5>
            <p><strong>Date:</strong> ${order.formattedOrderDate}</p>
            <p><strong>Transaction ID:</strong> ${order.transactionId}</p>
            <p><strong>Payment Method:</strong> ${order.paymentMethod}</p>
          </div>
          <div class="col-md-6">
            <h5>Customer Information</h5>
            <p><strong>Name:</strong> ${order.attendee.firstName} ${order.attendee.lastName}</p>
            <p><strong>Email:</strong> ${order.attendee.email}</p>
          </div>
        </div>

        <h5>Order Items</h5>
        <table class="table">
          <thead>
          <tr>
            <th>Ticket</th>
            <th>Event</th>
            <th class="text-center">Price</th>
            <th class="text-center">Quantity</th>
            <th class="text-end">Subtotal</th>
          </tr>
          </thead>
          <tbody>
          <c:forEach items="${order.orderItems}" var="item">
            <tr>
              <td>${item.ticket.ticketType}</td>
              <td>${item.ticket.event.title}</td>
              <td class="text-center">$<fmt:formatNumber value="${item.pricePerUnit}" pattern="#,##0.00"/></td>
              <td class="text-center">${item.quantity}</td>
              <td class="text-end">$<fmt:formatNumber value="${item.finalPrice}" pattern="#,##0.00"/></td>
            </tr>
          </c:forEach>
          </tbody>
          <tfoot>
          <tr>
            <th colspan="4" class="text-end">Total:</th>
            <th class="text-end">$<fmt:formatNumber value="${order.totalAmount}" pattern="#,##0.00"/></th>
          </tr>
          </tfoot>
        </table>

        <div class="text-end mt-4">
          <c:if test="${order.status eq 'PENDING'}">
            <a href="${pageContext.request.contextPath}/checkout?orderId=${order.orderId}" class="btn btn-success me-2">Proceed to Checkout</a>
          </c:if>
          <c:if test="${order.status eq 'PENDING' || order.status eq 'PAID'}">
            <form action="${pageContext.request.contextPath}/orders/${order.orderId}/cancel" method="post" onsubmit="return confirm('Are you sure you want to cancel this order?');" class="d-inline">
              <button type="submit" class="btn btn-danger">Cancel Order</button>
            </form>
          </c:if>
        </div>
      </div>
    </div>

    <div class="text-center mt-3">
      <a href="${pageContext.request.contextPath}/orders" class="btn btn-secondary">Back to Orders</a>
    </div>
  </c:if>
</div>