function showPassportSuggestForm() {
    if ($passportUpdate.is(":visible"))
        $passportUpdate.slideUp(300);
    initSuggestForm($(this));
    var visibleAlready = $passportSuggestForm.is(":visible");
    if (!$passportUpdateButtonWrapper.is(":visible"))
        $passportUpdateButtonWrapper.slideUp(300);
    if (!visibleAlready) {
        $passportSuggestForm.show();
        $passportWrapper.modal('refresh');
        _scrollTo($passportWrapper.parent(), $(".cancel-button", $passportSuggestForm), 500, focusOnField);
    } else {
        _scrollTo($passportWrapper.parent(), $(".cancel-button", $passportSuggestForm), 500, focusOnField);
    }
    $reportMistakesWrapper.hide();

    //focusOnField();
    //$passportWrapper.modal('refresh');
}

function hidePassportSuggestForm() {
    if (!$passportUpdateButtonWrapper.is(":visible")) {
        $passportUpdateButtonWrapper.slideDown(300);
    }
    $(".passport-stories-wrapper").fadeIn(800);
    //$passportUpdateButtonWrapper.show();
    //$passportSuggestForm.fadeOut(500);
    $passportSuggestForm.slideUp(800, function () {
        $passportWrapper.modal('refresh');
        //_scrollTo($passportWrapper.parent(), $("#passportmenu-bottom"), 300);
    });
    $reportMistakesWrapper.show();
    //_scrollTo($passportWrapper.parent(), $passportUpdateButtonWrapper, 900);

}

function focusOnField() {
    $passportWrapper.modal('refresh');
    $("#field-value", $passportSuggestForm).focus();
}

var suggestionDropdown;

function initSuggestForm($elem) {
    var targetField = $elem.parent().data('field');
    //$('select option[value="' + targetField + '"]', $passportSuggestForm).prop('selected', true);
    //$('select', $passportSuggestForm).dropdown('set selected', targetField);
    suggestionDropdown.find("[data-value='" + targetField + "']").trigger("click");
    // init cancel buttons
    $('.cancel-button', $passportSuggestForm).on('click', function () {
        hidePassportSuggestForm();
    });

}

function suggestionUIInit() {
    suggestionDropdown = $('select', $passportSuggestForm).dropdown({
        transition: 'slide down',
        duration: 0,
        onChange: suggestionChangeFunc
    });
}

var suggestionChangeFunc = function (value, text, $choice) {
    var $field = $("#field-value", $passportSuggestForm);
    var field = value;
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
    switch (field) {
        case 'name' :
            placeholderValue = 'Napisz do nas, jeśli zauważyłeś błędy w nazwie kościoła lub znasz nazwę alternatywną';
            break;
        case 'address' :
            placeholderValue = 'Napisz do nas, jeśli zauważyłeś błędy adresowe';
            break;
        case 'years' :
            placeholderValue = 'Pomóż nam uzupełnić lub poprawić daty budowy kościoła';
            break;
        case 'architects' :
            placeholderValue = 'Pomóż nam dowiedzieć się, kto projektował kościół';
            break;
        case 'website' :
            placeholderValue = 'Wpisz aktualny adres strony WWW parafii';
            break;
        case 'other' :
            placeholderValue = 'Podziel się z nami innymi spostrzeżeniami';
            break;
    }
    $field.attr('placeholder', placeholderValue);
};

function adjustSuggestionSettings(settings) {
    settings.action = 'update passport field';
    var fieldName = suggestionDropdown.dropdown('get value');
    settings.urlData = {field: fieldName};
    var data = {};
    var entity;
    var $entityForm = $(".entity-form", $passportSuggestForm);
    entity = $entityForm.data('entity');
    $.extend(data, $entityForm.serializeObject());
    data.entity = entity;
    data[fieldName] = $("#field-value").val();
    settings.data = data;
    return settings;
}


