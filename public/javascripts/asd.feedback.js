var feedbackInited = false;
function addFeedback() {
    $("input[type=text],textarea", $feedbackWrapper).val('');
    $feedbackWrapper.modal('show');
    if (!feedbackInited)
        initFeedback();
}

function initFeedback() {
    $(".feedback-submit", $feedbackWrapper).api({
        on: 'click',
        action: 'send feedback',
        method: 'POST',
        onSuccess: function (data) {
            console.log(data);
            notifyFeedbackOk();
        },
        onError: function () {
            //showPassportUpdateForm();
            notifyFeedbackError();
        },
        serializeForm: true,
        beforeSend: function (settings) {
            if (validateForm($(this).closest('form')))
                return settings;
            return false;
        }
    });
    feedbackInited = true;
    $(".close-button").off('click').on('click', function () {
        $feedbackWrapper.modal('hide');
    });
    if (userAuthed) {
        if (userName)
            $(".uploaded-by").val(userName).prop('disabled', true);
        $(".hide-authed").hide();
    }
}
