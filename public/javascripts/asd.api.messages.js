function notifyFeedbackOk() {
    $("#feedback-success-message").fadeIn('slow').delay(5000).fadeOut('fast', function () {
        $feedbackWrapper.modal('hide');
    });
}

function notifyFeedbackError() {
    $("#feedback-error-message").show('slow');
}

function notifyOk(message, callback) {
    if ($passportSuggestForm.is(":visible")) {

        var $ssm = $("#suggestion-success-message");
        $(".message-header", $passportSuggestForm).html('Twój komentarz został wysłany!');
        message = 'Dziękujemy za pomoc, czytamy wszystkie komentarze i na bierząco aktualizujemy dane';
        notifyUpdateResult($ssm, message, callback);

    } else {

        var part = $(".ui.tab.active form").data('entity');
        if (part == 'story') {

            var $nssm = $("#new-story-success-message");
            $(".message-header", $nssm).html('Twoje wspomnienie zostało wysłane!');
            message = 'Moderator przeczyta ja<br>i opublikuje w ciągu 48 godzin';
            notifyUpdateResult($nssm, message, callback);

        } else {

            var $ism = $("#images-success-message");
            $(".message-header", $ism).html(currentImageCounts['images'] == 1 ? 'Twoje zdjęcie zostało wysłane!' : 'Twoje zdjęcia zostały wysłane!');
            message = 'Moderator zweryfikuje je i opublikuje w ciągu 48 godzin';
            notifyUpdateResult($ism, message, callback);
        }
    }
}

function notifyError(message, callback) {
    notifyUpdateResult($("#new-story-error-message"), message, callback);
}

function notifySuggestionOk(message, callback) {
    notifySuggestResult($("#suggestion-success-message"), message, callback);
}

function notifySuggestionError(message, callback) {
    notifySuggestResult($("#suggestion-error-message"), message, callback);
}


function notifyUpdateResult($elem, message, callback) {
    var $wrapIn = $(".ui.tab.active", $passportUpdate);
    $(".message-text", $elem).html(message);
    $elem.css({
        top: ($wrapIn.height() / 2 - 89) + "px"
    }).one('click', function () {
        $(this).finish();
    })
        .fadeIn('slow').delay(5000).fadeOut('slow', callback);
}

function notifySuggestResult($elem, message, callback) {
    var $wrapIn = $passportSuggestForm;
    $(".message-text", $elem).html(message);
    $elem.css({
        top: ($wrapIn.height() / 2 - 89) + "px"
    }).one('click', function () {
        $(this).finish();
    })
        .fadeIn('slow').delay(5000).fadeOut('slow', callback);
}

function notifyChurchOk() {
    $("#new-church-success-message").fadeIn('slow').delay(5000).fadeOut('fast', function () {
        $suggestionWrapper.modal('hide');
    });
}

function notifyChurchError() {
    $("#new-church-error-message").show('slow');
}
