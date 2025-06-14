<!-- Existing code for the tickets list -->
<div class="card mb-4">
    <div class="card-header d-flex justify-content-between align-items-center">
        <h5 class="mb-0">Tickets</h5>
        <c:if test="${sessionScope.user.role == 'ORGANIZER'}">
            <button type="button" class="btn btn-success btn-sm" id="addTicketButton">
                Add Ticket
            </button>
        </c:if>
    </div>
    <div class="card-body">
        <c:choose>
            <c:when test="${not empty tickets}">
                <div class="table-responsive">
                    <table class="table table-bordered">
                        <thead>
                            <tr>
                                <th>Type</th>
                                <th>Price</th>
                                <th>Available</th>
                                <th>Description</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${tickets}" var="ticket">
                                <tr>
                                    <td><c:out value="${ticket.ticketType}" /></td>
                                    <td>$<fmt:formatNumber value="${ticket.price}" minFractionDigits="2" /></td>
                                    <td><c:out value="${ticket.quantityAvailable}" /></td>
                                    <td><c:out value="${ticket.description}" /></td>
                                    <td>
                                        <c:if test="${sessionScope.user.role == 'ORGANIZER'}">
                                            <button class="btn btn-sm btn-outline-warning update-ticket-btn" 
                                                    data-ticket-id="${ticket.ticketId}" 
                                                    data-ticket-type="${ticket.ticketType}" 
                                                    data-ticket-price="${ticket.price}" 
                                                    data-ticket-quantity="${ticket.quantityAvailable}" 
                                                    data-ticket-description="${ticket.description}">
                                                Update
                                            </button>
                                            <a href="${pageContext.request.contextPath}/tickets/${ticket.ticketId}/delete" class="btn btn-sm btn-outline-danger">Delete</a>
                                        </c:if>
                                        <c:if test="${ticket.isAvailable() and sessionScope.user != null and sessionScope.user.role == 'ATTENDEE'}">
                                            <button class="btn btn-sm btn-outline-primary buy-ticket-btn" data-ticket-id="${ticket.ticketId}">
                                                Buy
                                            </button>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:when>
            <c:otherwise>
                <p>No tickets available for this event.</p>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<!-- Bootstrap Modal for Updating Ticket -->
<div class="modal fade" id="updateTicketModal" tabindex="-1" role="dialog" aria-labelledby="updateTicketModalLabel" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <form id="updateTicketForm" action="${pageContext.request.contextPath}/tickets/update" method="post">
                <div class="modal-header">
                    <h5 class="modal-title" id="updateTicketModalLabel">Update Ticket</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <input type="hidden" name="ticketId" id="ticketId" />
                    <div class="form-group">
                        <label for="ticketType">Ticket Type</label>
                        <input type="text" class="form-control" id="ticketType" name="ticketType" required>
                    </div>
                    <div class="form-group">
                        <label for="price">Price</label>
                        <input type="number" step="0.01" class="form-control" id="price" name="price" required>
                    </div>
                    <div class="form-group">
                        <label for="quantityAvailable">Quantity Available</label>
                        <input type="number" class="form-control" id="quantityAvailable" name="quantityAvailable" required>
                    </div>
                    <div class="form-group">
                        <label for="description">Description</label>
                        <textarea class="form-control" id="description" name="description" rows="3"></textarea>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">Update Ticket</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Scripts -->
<script src="${pageContext.request.contextPath}/js/jquery-3.5.1.min.js"></script>
<script src="${pageContext.request.contextPath}/js/bootstrap.min.js"></script>
<script src="${pageContext.request.contextPath}/js/scripts.js"></script>
<script>
    $(document).ready(function() {
        // Bind the Update button click event to populate the modal and show it
        $('.update-ticket-btn').click(function() {
            const ticketId = $(this).data('ticket-id');
            const ticketType = $(this).data('ticket-type');
            const ticketPrice = $(this).data('ticket-price');
            const ticketQuantity = $(this).data('ticket-quantity');
            const ticketDescription = $(this).data('ticket-description');

            // Populate the modal fields
            $('#ticketId').val(ticketId);
            $('#ticketType').val(ticketType);
            $('#price').val(ticketPrice);
            $('#quantityAvailable').val(ticketQuantity);
            $('#description').val(ticketDescription);

            // Show the modal
            $('#updateTicketModal').modal('show');
        });
    });
</script>