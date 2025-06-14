<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Checkout" scope="request" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/all.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
<script src="${pageContext.request.contextPath}/js/jquery-3.5.1.min.js"></script>
<script src="${pageContext.request.contextPath}/js/bootstrap.min.js"></script>
<script src="${pageContext.request.contextPath}/js/scripts.js"></script>
<jsp:include page="/WEB-INF/views/layouts/base.jsp" />
<div class="container mt-4">
    <h1>Checkout</h1>

    <div class="row">
        <div class="col-md-8">
            <div class="card mb-4">
                <div class="card-header">
                    <h5>Order Summary</h5>
                </div>
                <div class="card-body">
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
                        <c:forEach items="${orderItems}" var="item">
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
                            <th class="text-end">$<fmt:formatNumber value="${totalAmount}" pattern="#,##0.00"/></th>
                        </tr>
                        </tfoot>
                    </table>
                </div>
            </div>
        </div>

        <div class="col-md-4">
            <div class="card">
                <div class="card-header">
                    <h5>Payment Information</h5>
                </div>
                <div class="card-body">
                    <form id="paymentForm" action="${pageContext.request.contextPath}/checkout?orderId=${orderId}" method="post">
                        <div class="mb-3">
                            <label class="form-label">Payment Method</label>
                            <div class="form-check">
                                <input class="form-check-input" type="radio" name="paymentMethod" id="creditCard" value="CREDIT_CARD" checked>
                                <label class="form-check-label" for="creditCard">Credit Card</label>
                            </div>
                            <div class="form-check">
                                <input class="form-check-input" type="radio" name="paymentMethod" id="paymentAttendance" value="PAYMENT_ATTENDANCE">
                                <label class="form-check-label" for="paymentAttendance">Payment upon attendance</label>
                            </div>
                        </div>

                        <div id="creditCardFields">
                            <div class="mb-3">
                                <label for="cardNumber" class="form-label">Card Number</label>
                                <input type="text" class="form-control" id="cardNumber" placeholder="1234 5678 9012 3456" maxlength="16" required>
                                <small id="cardTypeDisplay" class="form-text text-muted"></small>
                                <div id="cardNumberError" class="invalid-feedback"></div>
                            </div>
                            <div class="row mb-3">
                                <div class="col-6">
                                    <label for="expiryDate" class="form-label">Expiry Date</label>
                                    <input type="text" class="form-control" id="expiryDate" placeholder="MM/YY" maxlength="5" required>
                                    <div id="expiryDateError" class="invalid-feedback"></div>
                                </div>
                                <div class="col-6">
                                    <label for="cvv" class="form-label">CVV</label>
                                    <input type="text" class="form-control" id="cvv" placeholder="123" maxlength="3" required>
                                    <div id="cvvError" class="invalid-feedback"></div>
                                </div>
                            </div>
                            <div class="mb-3">
                                <label for="cardholderName" class="form-label">Cardholder Name</label>
                                <input type="text" class="form-control" id="cardholderName" name="cardholderName" required>

                            </div>
                        </div>

                        <button type="submit" class="btn btn-primary w-100" id="submitButton">Complete Order</button>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <div class="text-center mt-3">
        <a href="${pageContext.request.contextPath}/events" class="btn btn-secondary">Back To Events</a>
    </div>
</div>

<!-- Loading overlay -->
<div id="loadingOverlay" style="display:none; position:fixed; top:0; left:0; width:100%; height:100%; background-color:rgba(0,0,0,0.5); z-index:9999;">
    <div style="position:absolute; top:50%; left:50%; transform:translate(-50%,-50%); text-align:center; color:white;">
        <div class="spinner-border text-light" role="status">
            <span class="visually-hidden">Loading...</span>
        </div>
        <p class="mt-2">Processing payment, please wait...</p>
    </div>
</div>

<script>
    document.addEventListener('DOMContentLoaded', function() {
        const creditCardRadio = document.getElementById('creditCard');
        const paymentAttendanceRadio = document.getElementById('paymentAttendance');  // Updated variable name
        const creditCardFields = document.getElementById('creditCardFields');
        const cardNumber = document.getElementById('cardNumber');
        const expiryDate = document.getElementById('expiryDate');
        const cvv = document.getElementById('cvv');
        const form = document.getElementById('paymentForm');
        const loadingOverlay = document.getElementById('loadingOverlay');
        const cardTypeDisplay = document.getElementById('cardTypeDisplay');

        function togglePaymentFields() {
            if (creditCardRadio.checked) {
                creditCardFields.style.display = 'block';
                cardNumber.setAttribute('required', 'required');
                expiryDate.setAttribute('required', 'required');
                cvv.setAttribute('required', 'required');
                document.getElementById('cardholderName').setAttribute('required', 'required');
            } else {
                creditCardFields.style.display = 'none';
                cardNumber.removeAttribute('required');
                expiryDate.removeAttribute('required');
                cvv.removeAttribute('required');
                document.getElementById('cardholderName').removeAttribute('required');
            }
        }


        // Call togglePaymentFields on page load to set initial state
        togglePaymentFields();

        // Add event listeners for payment method changes
        creditCardRadio.addEventListener('change', togglePaymentFields);
        paymentAttendanceRadio.addEventListener('change', togglePaymentFields);

        // Credit Card Validation
        cardNumber.addEventListener('input', function(e) {
            this.value = this.value.replace(/\D/g, '');
            validateCreditCard();
        });

        function validateCreditCard() {
            const cardNum = cardNumber.value.replace(/\s/g, '');
            const cardNumberError = document.getElementById('cardNumberError');

            // Check if it's a valid card number (Visa or Mastercard)
            const visaPattern = /^4[0-9]{15}$/;
            const mastercardPattern = /^5[1-5][0-9]{14}$/;

            if (cardNum.length !== 16) {
                cardNumberError.textContent = 'Card number must be 16 digits';
                cardNumber.classList.add('is-invalid');
                return false;
            } else if (visaPattern.test(cardNum)) {
                cardTypeDisplay.textContent = 'Visa';
                cardNumber.classList.remove('is-invalid');
                return true;
            } else if (mastercardPattern.test(cardNum)) {
                cardTypeDisplay.textContent = 'Mastercard';
                cardNumber.classList.remove('is-invalid');
                return true;
            } else {
                cardNumberError.textContent = 'Only Visa and Mastercard are accepted';
                cardNumber.classList.add('is-invalid');
                return false;
            }
        }

        // CVV Validation
        cvv.addEventListener('input', function(e) {
            this.value = this.value.replace(/\D/g, '');

            const cvvError = document.getElementById('cvvError');
            if (this.value.length !== 3) {
                cvvError.textContent = 'CVV must be 3 digits';
                this.classList.add('is-invalid');
            } else {
                this.classList.remove('is-invalid');
            }
        });

        // Expiry Date Formatting and Validation
        expiryDate.addEventListener('input', function(e) {
            let value = this.value.replace(/\D/g, '');

            if (value.length > 2) {
                value = value.substring(0, 2) + '/' + value.substring(2, 4);
            }

            this.value = value;

            const expiryDateError = document.getElementById('expiryDateError');
            if (value.length >= 2) {
                const month = parseInt(value.substring(0, 2));
                if (month < 1 || month > 12) {
                    expiryDateError.textContent = 'Month must be between 1-12';
                    this.classList.add('is-invalid');
                } else {
                    expiryDateError.textContent = '';
                    this.classList.remove('is-invalid');

                    // Validate expiration date is in the future
                    if (value.length === 5) {  // MM/YY format complete
                        validateExpiryDate(value);
                    }
                }
            }
        });


        // Add a function to validate expiry date
        function validateExpiryDate(value) {
            const month = parseInt(value.substring(0, 2));
            const year = 2000 + parseInt(value.substring(3, 5));

            const today = new Date();
            const currentYear = today.getFullYear();
            const currentMonth = today.getMonth() + 1; // JavaScript months are 0-indexed

            const expiryDateError = document.getElementById('expiryDateError');

            if (year < currentYear || (year === currentYear && month <= currentMonth)) {
                expiryDateError.textContent = 'Card expiration date must be after current month';
                expiryDate.classList.add('is-invalid');
                return false;
            } else {
                expiryDateError.textContent = '';
                expiryDate.classList.remove('is-invalid');
                return true;
            }
        }



        // Add a function to validate expiry date
        function validateExpiryDate(value) {
            const month = parseInt(value.substring(0, 2));
            const year = 2000 + parseInt(value.substring(3, 5));

            const today = new Date();
            const currentYear = today.getFullYear();
            const currentMonth = today.getMonth() + 1; // JavaScript months are 0-indexed

            const expiryDateError = document.getElementById('expiryDateError');

            if (year < currentYear || (year === currentYear && month <= currentMonth)) {
                expiryDateError.textContent = 'Card expiration date must be after current month';
                expiryDate.classList.add('is-invalid');
                return false;
            } else {
                expiryDateError.textContent = '';
                expiryDate.classList.remove('is-invalid');
                return true;
            }
        }

// Form Submission
        form.addEventListener('submit', function(e) {
            e.preventDefault();

            if (creditCardRadio.checked) {
                // Validate credit card fields
                const isCardValid = validateCreditCard();
                const isCvvValid = cvv.value.length === 3;
                const expiryValue = expiryDate.value;
                const isExpiryValid = expiryValue.length === 5 && expiryValue.includes('/');
                const isExpiryFuture = isExpiryValid && validateExpiryDate(expiryValue);

                if (!isCardValid || !isCvvValid || !isExpiryValid || !isExpiryFuture) {
                    return;
                }
            }

            // Show loading overlay
            loadingOverlay.style.display = 'block';

            // Set flag in sessionStorage to track form submission
            sessionStorage.setItem('checkoutSubmitted', 'true');

            // Submit the form after short delay
            setTimeout(() => {
                form.submit();
            }, 5000);
        });

    });
</script>