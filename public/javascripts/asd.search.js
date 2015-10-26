function initSearch()
{
    console.log('initiating search');
    console.log($prompt);
    $searchWrapper.search(
        {
            apiSettings: {
                debug : true,
                verbose : true,
                action : 'search',
                onResponse: function(churchResponse) {
                    var
                        response = churchResponse;
                    response["action"] =
                    {
                        url : "javascript:addNewChurch();",
                        text : 'Nie możesz znaleźć? Dodaj kościół do bazy!'
                    };
                    return response;
                }
            },
            debug : true,
            verbose : true,
            minCharacters : 3,
            //type: 'category',
            maxResults : 5,
            fields : {
                title           : 'title',       // result title
                action          : 'action',      // "view more" object name
                actionText      : 'text',        // "view more" text
                actionURL       : 'url'          // "view more" url
            },
            errors: {
                error : {
                    serverError : ""
                }
            }
        }
    );
}
var newChurchInited = false;
function addNewChurch() {
    $suggestionWrapper.modal('show');
    if (!newChurchInited)
        initAddChurch();
}

function initAddChurch() {
    $(".submit.button", $suggestionWrapper).api({
        on: 'click',
        action: 'suggest church',
        method: 'POST',
        onSuccess: function (data) {
            console.log(data);
            togglePassportUpdateForm();
            notifyChurchOk();
        },
        onError : function()
        {
            //togglePassportUpdateForm();
            notifyChurchError();
        },
        serializeForm: true
    });
    newChurchInited = true;
}

function notifyChurchOk()
{
    $("#new-church-success-message").show('slow');
}

function notifyChurchError()
{
    $("#new-church-error-message").show('slow');
}
