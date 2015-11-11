function initSearch() {
    //console.log('initiating search');
    //console.log($churchPrompt);
    if (contentType == 'article')
        $searchChurchWrapper.data('searchable', 'article');
    else
        $searchChurchWrapper.data('searchable', 'story');

    $searchChurchWrapper.search(
        {
            apiSettings: {
                debug: true,
                verbose: true,
                action: 'search',
                onResponse: function (searchResponse) {
                    //var
                    //    response = searchResponse;
                    //response["action"] =
                    //{
                    //};
                    return false;
                }

            },
            cache : false,
            //debug: true,
            //verbose: true,
            minCharacters: 3,
            //type: 'category',
            maxResults: 5,
            fields: {
                title: 'title',       // result title
                action: 'action',      // "view more" object name
                actionText: 'text',        // "view more" text
                actionURL: 'url'          // "view more" url
            },
            //error: {
            //    serverError: "Server err",
            //    noResults: "sfrservev"
            //},
            templates: {
                message: function(message, type) {
                    return '';
                    //return '<a href="javascript:addNewChurch();" class="action">Nie możesz znaleźć? Dodaj kościół do bazy!</a>';
                }
            }

        }
    );
}