var emailEditClick = function () {
    var id = $(this).parent().attr('id');
    $emailWrapper.empty();
    $emailForm = newEmailForm();
    $("#submit", $emailForm).api({
        on: 'click',
        action: 'post update email',
        method: 'POST',
        onSuccess: apiResult,
        serializeForm: true,
        beforeSend : function(settings)
        {
            var name = $("#name", $emailForm).val();
            settings.urlData.name = name;
            settings.data.name = name;
            return settings;
        }
    });
    $("#check", $emailForm).api({
        on: 'click',
        action: 'post check email',
        method: 'POST',
        onSuccess: apiResult,
        serializeForm: true,
        beforeSend : function(settings)
        {
            var name = $("#name", $emailForm).val();
            settings.urlData.name = name;
            settings.data.name = name;
            return settings;
        }
    });
    $emailForm.appendTo($emailWrapper);
    var $emailHint = $("<div class='email-hint'>Allowed template variables: $USERNAME, $ADDEDCHURCH</div>")
    $(".email-visible").show();
    if (id) {
        $emailForm.api({on: 'now', action: "get json email", urlData: {name: id}, onSuccess: fillEmail});
        applySubmit();
    }
};

function newEmailForm() {
    return $("<form class='email-form ui form' method='post'>" +
        "<label for='name'>template name</label><input id='name' name='name' disabled/>" +
        "<label for='subject'>Subject</label><input type='text' id='subject' placeholder='Email subject' name='subject'/>" +
        "<label for='template'>template body</label><textarea id='body' placeholder='Email body' name='body'/>" +
        "<br>" +
        "<input id='submit' type='button' class='ui submit button' value='update'>" +
        "<input id='check' type='button' class='ui button' value='check'>" +
        "</form>");
}

function fillEmail(data) {
    $admpages.hide();
    if (data) {
        $("#name", $emailForm).val(data.name);
        $("#subject", $emailForm).val(data.subject);
        $("#body", $emailForm).val(data.body);
    }
    $emailWrapper.show();
}

//function apiResult(data) {
//    if (data.success)
//        noty({
//            text: 'successful update',
//            timeout: 4000,
//            layout: 'top',
//            type: 'success'
//        });
//    else if (data.error)
//        noty({
//            text: data.error,
//            timeout: 4000,
//            layout: 'top',
//            type: 'error'
//        })
//}
