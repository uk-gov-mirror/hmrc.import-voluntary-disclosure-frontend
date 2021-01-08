$(document).ready(function() {

    // back click handle, dependent upon presence of referrer & no host change
    $('#back-link[href="#"],#back-link-secondary[href="#"]').on('click', function(e){
        e.preventDefault();
        window.history.back();
    });

});