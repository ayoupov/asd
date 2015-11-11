function initSearch() {
    //console.log('initiating search');
    //console.log($churchPrompt);
    var defaultAction = {
        url: "javascript:addNewChurch();",
        text: 'Dodaj kościół'
    };

    $searchContentWrapper.search(
        {
            apiSettings: {
                action: 'search'
            },
            //debug: true,
            //verbose: true,
            minCharacters: 4,
            maxResults: 5,
            fields: {
                title: 'title',       // result title
                action: 'action',      // "view more" object name
                actionText: 'text',        // "view more" text
                actionURL: 'url'          // "view more" url
            },
            error: {
                serverError: ""
            }
            //,
            //templates: {
            //    message: function(message, type) {
            //        return '<a href="javascript:addNewChurch();" class="action">Dodaj kościół</a>';
            //    }
            //}
        }
    );

    $searchChurchWrapper.search(
        {
            apiSettings: {
                //debug: true,
                //verbose: true,
                action: 'search',
                onResponse: function (churchResponse) {
                    var response = churchResponse;
                        response["action"] = defaultAction;
                    return response;
                }
            },
            //debug: true,
            //verbose: true,
            minCharacters: 4,
            maxResults: 5,
            fields: {
                title: 'title',       // result title
                action: 'action',      // "view more" object name
                actionText: 'text',        // "view more" text
                actionURL: 'url'          // "view more" url
            },
            error: {
                serverError: ""
            },
            templates: {
                message: function(message, type) {
                    return '<a href="javascript:addNewChurch();" class="action">Dodaj kościół</a>';
                }
            }
        }
    );
}

var newChurchInited = false;
function addNewChurch() {
    $("input[type=text]",$suggestionWrapper).val('');
    $suggestionWrapper.modal('show');
    if (!newChurchInited)
        initAddChurch();
}

function initAddChurch() {
    $(".new-church-submit", $suggestionWrapper).api({
        on: 'click',
        action: 'suggest church',
        method: 'POST',
        onSuccess: function (data) {
            console.log(data);
            notifyChurchOk();
        },
        onError: function () {
            //showPassportUpdateForm();
            notifyChurchError();
        },
        beforeSend: function (settings) {
            if (validateForm($(this).closest('form')))
                return settings;
            return false;
        },
        serializeForm: true
    });
    $(".close-button").off('click').on('click', function () {
        $suggestionWrapper.modal('hide');
    });

    newChurchInited = true;
}
