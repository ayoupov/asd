function showPassportSuggestForm() {
    if ($passportUpdate.is(":visible"))
        $passportUpdate.slideUp('slow');
    initSuggestForm($(this));
    var visibleAlready = $passportSuggestForm.is(":visible");
    //$passportUpdateButtonWrapper.slideUp('slow', function () {
    //$passportUpdateButtonWrapper.hide(function () {
    $passportUpdateButtonWrapper.hide();
    if (!visibleAlready) {
        $passportSuggestForm.slideDown(800);
        $passportSuggestForm.fadeIn(800, focusOnField);
        _scrollTo($passportWrapper.parent(), $("#field-value", $passportSuggestForm), 900);
    }
    //});
    focusOnField();
    $passportWrapper.modal('refresh');
}

function hidePassportSuggestForm() {
    $(".passport-stories-wrapper").show('slow');
    $passportUpdateButtonWrapper.show();
    //$passportSuggestForm.fadeOut(500);
    $passportSuggestForm.slideUp(800);

    $passportWrapper.modal('refresh');
    //_scrollTo($passportWrapper.parent(), $passportUpdateButtonWrapper, 900);
    _scrollTo($passportWrapper.parent(), $("#passportmenu-bottom"), 900);
}

function focusOnField() {
    $("#field-value", $passportSuggestForm).focus();
}

function initSuggestForm($elem) {
    var targetField = $elem.parent().data('field');
    var $field = $("#field-value", $passportSuggestForm);
    // select option
    $('select option[value="' + targetField + '"]', $passportSuggestForm).prop('selected', true);
    $("select.suggestion-field-select", $passportSuggestForm)
        .off('change')
        .on('change', function () {
            var v;
            var field = $(this).find(':selected').val();
            if (field == "website") {
                v = currentChurch.website;
            } else if (field == "name") {
                v = currentChurch.name;
            } else {
                var $e = $(".passport-data-data", $(".passport-value[data-field=" + field + "]"));
                v = $e.html();
            }
            // fill field value
            $field.val(v);
            var placeholderValue = '';
            switch (field)
            {
                case 'name' : placeholderValue = 'Napisz do nas, jeśli zauważyłeś błędy w nazwie kościoła lub znasz nazwę alternatywną';
                    break;
                case 'address' : placeholderValue = 'Napisz do nas, jeśli zauważyłeś błędy adresowe';
                    break;
                case 'years' : placeholderValue = 'Pomóż nam uzupełnić lub poprawić nieprawidłowe daty budowy kościoła';
                    break;
                case 'architects' : placeholderValue = 'Pomóż nam dowiedzieć się, kto projektował kościół. Napisz do nas, jeśli przypadkiem pominęliśmy jednego z projektantów lub nieprawidłowo napisaliśmy nazwisko';
                    break;
                case 'website' : placeholderValue = 'Napisz do nas, jeśli znasz aktualny adres strony WWW parafii';
                    break;
                case 'other' : placeholderValue = 'Podziel się z nami innymi spostrzeżeniami na temat bazy danych na stronie';
                    break;
            }
            $field.attr('placeholder', placeholderValue);
        }).trigger('change');
}

