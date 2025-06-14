// Global JavaScript for the application
$(document).ready(function() {
    // Initialize tooltips
    $('[data-toggle="tooltip"]').tooltip();

    // Handle active nav items
    const currentPath = window.location.pathname;
    $('.nav-link').each(function() {
        const linkPath = $(this).attr('href');
        if (currentPath === linkPath ||
            (currentPath.startsWith(linkPath) && linkPath !== '/')) {
            $(this).addClass('active');
        }
    });

    // Auto-dismiss alerts after 5 seconds
    setTimeout(function() {
        $('.alert').alert('close');
    }, 5000);

    // Payment method toggle
    $('input[name="paymentMethod"]').change(function() {
        if ($(this).val() === 'CREDIT_CARD') {
            $('#creditCardFields').show();
        } else {
            $('#creditCardFields').hide();
        }
    });
});
