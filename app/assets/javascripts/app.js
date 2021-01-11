$(document).ready(function() {

    // prevent resubmit warning
    if (window.history && window.history.replaceState && typeof window.history.replaceState === 'function') {
        window.history.replaceState(null, null, window.location.href);
    }

    // back click handle, dependent upon presence of referrer & no host change
    $('#back-link[href="#"],#back-link-secondary[href="#"]').on('click', function(e){
        e.preventDefault();
        window.history.back();
    });

});