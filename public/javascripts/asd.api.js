var api =
{
    'get summary': '/content/summary',
    'get articles': '/content/articles/{ids}',
    'get stories': '/content/stories/{ids}',
    'get article': '/content/article/{id}',
    'get story': '/content/story/{id}',
    'get church revisions': '/content/churches/revisions/{id}'
};

// ss = starStories, sa = starArticles, ds = dateStories, da = dateArticles

var bindAPI = function () {
    $(document).api(
        {
            action: 'get summary',
            on: 'ready',
            onSuccess: applySummary
        }
    );
    //$(".stories").api(
    //    {
    //        action: 'get stories',
    //        on: 'load'
    //    }
    //);
    $("#more-articles-thumb").api(
        {
            action: 'get articles',
            on: 'click',
            urlData: {
                ids: function () {
                    return getNext("sa", articlesRequested)
                }
            },
            onSuccess: populateArticles
        }
    );
    $("#more-stories-thumb").api(
        {
            action: 'get stories',
            on: 'click',
            urlData: {
                ids: function () {
                    return getNext("ss", storiesRequested)
                }
            },
            onSuccess: populateStories
        }
    );
};

var contentCache = {
    ss: {idx: 0},
    sa: {idx: 0},
    ds: {idx: 0},
    da: {idx: 0}
};

//var storiesByStars, articlesByStars, storiesByDate, articlesByDate;
var storiesFirst = 5, articlesFirst = 7, dateStoriesFirst = 6;
var storiesRequested = storiesFirst + 1, articlesRequested = articlesFirst + 1;
var applySummary = function (response) {
    contentCache.ds.ids = response.data["stories"];
    contentCache.da.ids = response.data["articles"];
    contentCache.ss.ids = starsort(contentCache.ds.ids);
    contentCache.sa.ids = starsort(contentCache.da.ids);
    // init first pages
    $articles.api({
        action: "get articles",
        urlData: {ids: getNext("sa", articlesFirst)},
        onSuccess: populateArticles,
        on: 'now'
    });
    $stories.api({
        action: "get stories",
        urlData: {ids: getNext("ss", storiesFirst)},
        onSuccess: populateStories,
        on: 'now'
    });
    $dateStories.api({
        action: "get stories",
        urlData: {ids: getNext("ds", dateStoriesFirst)},
        onSuccess: populateDateStories,
        on: 'now'
    });
};

function getNext(key, q) {
    var res = [];
    for (var i = 0; i < q; i++) {
        if (contentLeft(key) > 0) {
            var id = contentCache[key].ids[contentCache[key].idx + i];
            if (typeof id !== "undefined")
                res.push(id[0]);
        }
        else
            break;
    }
    contentCache[key].idx += q;
    console.log(res.join(','));
    return res.join(",");
}

function contentLeft(key) {
    var left = contentCache[key].ids.length - contentCache[key].idx;
    return left < 0 ? 0 : left;
}

function starsort(arr) {
    var starred = [];
    var notStarred = [];
    for (var item in arr) {
        if (arr[item][1])
            starred.push(arr[item]);
        else
            notStarred.push(arr[item]);
    }
    return shuffle(starred).concat(shuffle(notStarred));
}

function populateArticles(data) {
    var lastItem = null;
    $articles.isotope('remove', $("#more-articles-thumb"));
    $(data.data).each(function (a, item) {
        var cover = item.cover;
        var hover = item.hover;
        var title = item.title;
        var lead = item.lead;
        var id = item.id;
        var $item = $('<div/>').addClass('article thumb').attr('id', 'article_' + id).append(
            $('<div/>').addClass('image face-content').append(
                $('<img/>').addClass('ui image').attr('src', cover)
            )
        ).append(
            $('<div/>').addClass('content face-content').append(
                $('<div/>').addClass('header').html(title)
            ).append(
                $('<div/>').addClass('description').html(lead + ' Wczes')
            )
        ).append(
            $('<div/>').addClass('hover-content').append(
                $('<div/>').addClass('cover')
            ).append(
                $('<div/>').addClass('back-image').css('background-image', 'url(' + hover + ')')
            ).append(
                $('<div/>').addClass('hover-icon').append(
                    $("<img/>").attr('src', '/assets/images/hover-icon.png')
                )
            ).append(
                $('<div/>').addClass('more-button').html('Czytaj')
            )
        ).append(
            $('<div/>').addClass('shameful-underline violet face-content')
        ).append(
            $('<div/>').addClass('shameful-underline white hover-content')
        );
        $articles.append($item);
        $articles.isotope('appended', $item);
        lastItem = $item;
    });
    var $more = $("<div/>").attr('id', 'more-articles-thumb').addClass('article thumb center-more grayish').append(
        $('<div/>').addClass('more-wrapper white-bordered').append(
            $('<div/>').addClass('more').html('Więcej ' + contentLeft('sa'))
        )
    );

    $more.insertAfter(lastItem);
    $articles.isotope('appended', $more);
    $articles.isotope();

    $('.hover-content').hide();
    $(".article").hover(function () {
        // switch content
        var hoverContent = $('.hover-content', $(this));
        if (hoverContent.length > 0) {
            $(this).toggleClass('hovered');
            $('.face-content', $(this)).hide();
            hoverContent.show();
        }
    }, function () {
        var hoverContent = $('.hover-content', $(this));
        if (hoverContent.length > 0) {
            $(this).toggleClass('hovered');
            $('.face-content', $(this)).show();
            hoverContent.hide();
        }
    });

    $("#more-articles-thumb").api(
        {
            action: 'get articles',
            on: 'click',
            urlData: {
                ids: function () {
                    return getNext("sa", articlesRequested)
                }
            },
            onSuccess: populateArticles
        }
    );

}

function populateStories(data) {
    $(data.data).each(function (a, item) {
        var cover = item.cover;
        var title = item.title;
        var lead = item.lead;
        console.log(item + " : " + cover + ", " + title + ", " + lead);
    });
}

function populateDateStories(data) {
    $(data.data).each(function (a, item) {
        var title = item.title;
        var dt = new Date(item.approvedDT);
        console.log(item + " : " + dt + ", " + title);
    });
}

// The de-facto unbiased shuffle algorithm is the Fisher-Yates (aka Knuth) Shuffle.
// from https://github.com/coolaj86/knuth-shuffle

function shuffle(array) {
    var currentIndex = array.length, temporaryValue, randomIndex;
    // While there remain elements to shuffle...
    while (0 !== currentIndex) {
        // Pick a remaining element...
        randomIndex = Math.floor(Math.random() * currentIndex);
        currentIndex -= 1;
        // And swap it with the current element.
        temporaryValue = array[currentIndex];
        array[currentIndex] = array[randomIndex];
        array[randomIndex] = temporaryValue;
    }
    return array;
}

$.fn.api.settings.api = api;
