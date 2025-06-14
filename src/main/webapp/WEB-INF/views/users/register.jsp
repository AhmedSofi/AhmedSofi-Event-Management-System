<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<c:set var="pageTitle" value="Register" scope="request" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/all.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
<jsp:include page="/WEB-INF/views/layouts/base.jsp" />

<div class="row justify-content-center">
    <div class="col-md-8">
        <c:if test="${not empty error}">
            <div class="alert alert-danger" role="alert">
                ${error}
            </div>
        </c:if>
        <div class="card">
            <div class="card-header">
                <h2 class="card-title">Create an Account</h2>
            </div>
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/register" method="post">
                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label for="firstName">First Name</label>
                                <input type="text" class="form-control" id="firstName" name="firstName" ui_test="register-first-name" required>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-group">
                                <label for="lastName">Last Name</label>
                                <input type="text" class="form-control" id="lastName" name="lastName" ui_test="register-last-name" required>
                            </div>
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="email">Email Address</label>
                        <input type="email" class="form-control" id="email" name="email" ui_test="register-email" required>
                    </div>

                    <div class="form-group">
                        <label for="username">Username</label>
                        <input type="text" class="form-control" id="username" name="username" ui_test="register-username" required>
                    </div>

                    <div class="form-group">
                        <label for="password">Password</label>
                        <input type="password" class="form-control" id="password" name="password" ui_test="register-password" required>
                    </div>

                    <div class="form-group">
                        <label for="confirmPassword">Confirm Password</label>
                        <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" ui_test="register-confirm-password" required>
                    </div>

                    <div class="form-group">
                        <label for="role">Account Type</label>
                        <select class="form-control" id="role" name="role" ui_test="register-role" required>
                            <option value="">Select account type...</option>
                            <option value="ATTENDEE" ui_test="register-attendee">Attendee</option>
                            <option value="ORGANIZER" ui_test="register-organizer">Event Organizer</option>
                        </select>
                    </div>

                    <div class="form-group form-check">
                        <input type="checkbox" class="form-check-input" id="termsAgreement" name="termsAgreement" ui_test="register-terms" required>
                        <label class="form-check-label" for="termsAgreement">
                            I agree to the 
                            <button id="term" type="button" class="btn btn-link p-0 align-baseline" ui_test="register-terms-button">Terms and Conditions</button>
                        </label>
                    </div>

                    <div class="form-group">
                        <button type="submit" class="btn btn-primary btn-block" ui_test="register-button">Register</button>
                    </div>

                    <div class="text-center">
                        <a href="${pageContext.request.contextPath}/login" ui_test="register-login-link">Already have an account? Login</a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<!-- JS Includes -->
<script src="${pageContext.request.contextPath}/js/jquery-3.5.1.min.js"></script>
<script src="${pageContext.request.contextPath}/js/bootstrap.min.js"></script>
<script src="${pageContext.request.contextPath}/js/scripts.js"></script>

<!-- Modal & Terms Script -->
<script>
    document.addEventListener('DOMContentLoaded', function() {
        const termsButton = document.getElementById('term');
        if (!document.getElementById('termsModal')) createTermsModal();
        termsButton.addEventListener('click', function(e) {
            e.preventDefault();
            showTermsModal();
        });

        function createTermsModal() {
            const modalHTML = `
                <div class="modal fade" id="termsModal" tabindex="-1" role="dialog" aria-labelledby="termsModalLabel" aria-hidden="true">
                    <div class="modal-dialog modal-lg" role="document">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title" id="termsModalLabel">Terms and Conditions</h5>
                            </div>
                            <div class="modal-body">
                                <h4>1. Acceptance of Terms</h4>
                                <p>By accessing and using this service, you accept and agree to be bound by the terms and provisions of this agreement.</p>
                                <h4>2. User Accounts</h4>
                                <p>Provide accurate information and be responsible for account activity.</p>
                                <h4>3. Privacy Policy</h4>
                                <p>Your use is governed by our Privacy Policy.</p>
                                <h4>4. User Content</h4>
                                <p>You are responsible for content you post or share.</p>
                                <h4>5. Termination</h4>
                                <p>We reserve the right to suspend or terminate your account for violations.</p>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-primary btn-agree" data-dismiss="modal">I Agree</button>
                            </div>
                        </div>
                    </div>
                </div>`;
            const modalContainer = document.createElement('div');
            modalContainer.innerHTML = modalHTML;
            document.body.appendChild(modalContainer);

            const agreeButton = modalContainer.querySelector('.btn-agree');
            if (agreeButton) {
                agreeButton.addEventListener('click', function() {
                    const checkbox = document.getElementById('termsAgreement');
                    checkbox.checked = true;
                });
            }

            const closeBtn = document.createElement('button');
            closeBtn.type = 'button';
            closeBtn.className = 'custom-close-btn';
            closeBtn.innerHTML = '<i class="fas fa-times"></i>';
            closeBtn.style.cssText = `
                position: absolute; top: 10px; right: 10px; background: none;
                border: none; font-size: 1.25rem; color: #000; opacity: 0.5;
                cursor: pointer; z-index: 1051;`;
            closeBtn.addEventListener('click', hideTermsModal);
            closeBtn.addEventListener('mouseover', () => closeBtn.style.opacity = '1');
            closeBtn.addEventListener('mouseout', () => closeBtn.style.opacity = '0.5');

            const modalContent = modalContainer.querySelector('.modal-content');
            modalContent.style.position = 'relative';
            modalContent.appendChild(closeBtn);
        }

        function showTermsModal() {
            const modal = document.getElementById('termsModal');
            if (window.jQuery && jQuery.fn.modal) {
                $(modal).modal('show');
            } else {
                modal.style.display = 'block';
                modal.classList.add('show');
                document.body.classList.add('modal-open');
                if (!document.querySelector('.modal-backdrop')) {
                    const backdrop = document.createElement('div');
                    backdrop.className = 'modal-backdrop fade show';
                    document.body.appendChild(backdrop);
                }
            }
        }

        function hideTermsModal() {
            const modal = document.getElementById('termsModal');
            if (window.jQuery && jQuery.fn.modal) {
                $(modal).modal('hide');
            } else {
                modal.style.display = 'none';
                modal.classList.remove('show');
                document.body.classList.remove('modal-open');
                const backdrop = document.querySelector('.modal-backdrop');
                if (backdrop) backdrop.remove();
            }
        }
    });
</script>
