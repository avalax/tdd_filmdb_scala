$( document ).ready(function() {
    $(".js-delete").on( "click", function(event) {
        event.preventDefault();
        $.ajax({
            url: $(this).attr('href'),
            type: 'DELETE',
            success: function(data) {
                window.location.reload();
            }
        });
    });
    $('.js-year').datepicker({
        yearRange: '-100:+10',
        changeYear: true,
        showButtonPanel: true,
        dateFormat: 'yy',
        onClose: function(dateText, inst) {
            var year = $("#ui-datepicker-div .ui-datepicker-year :selected").val();
            $(this).datepicker('setDate', new Date(year, 1));
        }
    });
    $(".date-picker-year").focus(function () {
        $(".ui-datepicker-month").hide();
    });

});
