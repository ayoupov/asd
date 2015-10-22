var api =
{
    'get summary': '/content/summary',
    'get articles': '/articles/{ids}',
    'get stories': '/stories/{ids}',
    'get article': '/article/{id}',
    'get story': '/story/{id}',
    'get church passport' : '/church/passport/{id}',
    'add church story' : '/church/story',
    'get church images' : '/church/{id}/images',
    'add church images' : '/church/images',
    'update passport field' : '/church/passport/{field}',
    'search' : '/search/{$searchable}/{query}',
    'suggest church' : '/church/suggest'
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
    mapInit(response.data['geostats']);
};

function getNext(key, q) {
    var res = [];
    var pushed = 0;
    for (var i = 0; i < q; i++) {
        if (contentLeft(key) > 0) {
            var id = contentCache[key].ids[contentCache[key].idx + i];
            if (typeof id !== "undefined") {
                res.push(id[0]);
                pushed++;
            }
        }
        else
            break;
    }
    contentCache[key].idx += Math.min(q, pushed);
    _debug(res.join(','));
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
    $articles.isotope('remove', $(".extra-articles"));
    //$("#more-articles-thumb").hide();
    $(data.data).each(function (a, item) {
        var cover = (item.coverThumbPath) ? item.coverThumbPath : "";
        var hover = (item.hoverThumbPath) ? item.hoverThumbPath : "";
        var title = item.title;
        var desc = item.coverDescription;
        var id = item.id;
        var $item = $('<div/>').addClass('article thumb').attr('id', 'article_' + id
        )
            .append(
            $('<div/>').addClass('image face-content').append(
                $('<div/>').addClass('face-image').css('background-image', 'url(' + cover + ')')
            )
        ).append(
            $('<div/>').addClass('content face-content').append(
                $('<div/>').addClass('header').html(title)
            ).append(
                $('<div/>').addClass('description').html(desc)
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
    var whatsleft = contentLeft('sa');
    //_debug('whatsleft: '  + whatsleft);
    if (whatsleft > 0) {
        var $more = $("<div/>").attr('id', 'more-articles-thumb').addClass('extra-articles article thumb center-more grayish').append(
            $('<div/>').addClass('more-wrapper white-bordered').append(
                $('<div/>').addClass('more').html('Więcej ' + whatsleft)
            )
        );
        $more.insertAfter(lastItem);
        $articles.isotope('appended', $more);
        //_debug('appended more');
        appendArticleApi();
    }
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
    $('.article:not(.extra-articles)').off('click').on('click', function () {
        window.open('/article/' + getId($(this)));
    });
    hackStories();
}

var transitionOnceFlag = false;
function hackStories()
{
    // hack to move to stories slide
    if (location.hash == "#slide-stories" && !transitionOnceFlag) {
        transitionOnceFlag = true;
        $("a.slide-stories").click();
    }
}

function appendArticleApi(when) {
    var $more = $(".extra-articles");
    $more.api(
        {
            action: 'get articles',
            on: typeof when !== 'undefined' ? when : 'click',
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
    var lastItem = null;
    $stories.isotope('remove', $(".extra-stories"));
    $(data.data).each(function (a, item) {
        var cover = (item.coverThumbPath) ? item.coverThumbPath : "";
        var title = item.title;
        var lead = item.lead;
        var id = item.id;
        var desc = (item.coverDescription) ? item.coverDescription : "";
        var $item = $("<div/>").addClass('story thumb').attr('id', 'story_' + id)
            .append(
            $("<div/>").addClass('image').css({
                    background: 'transparent url(' + cover + ') center center no-repeat'
            }))
            .append(
            $("<div/>").addClass('content').append(
                $("<div/>").addClass('header').html(title)
            ).append(
                $("<div/>").addClass('description').html(desc)
            )
        ).append(
            $("<div/>").addClass('golden shameful-underline')
        );

        $stories.append($item);
        $stories.isotope('appended', $item);
        lastItem = $item;
    });

    var whatsleft = contentLeft('ss');
    if (whatsleft > 0) {
        var $more = $("<div/>").attr('id', 'more-stories-thumb').addClass('extra-stories story thumb center-more white').append(
            $('<div/>').addClass('more-wrapper grayish-bordered').append(
                $('<div/>').addClass('more').html('Więcej ' + whatsleft)
            )
        );
        $more.insertAfter(lastItem);
        $stories.isotope('appended', $more);
        appendStoryApi();
    }
    $stories.isotope();
    $('.story:not(.extra-stories)').off('click').on('click', function () {
        window.open('/story/' + getId($(this)));
    });

}

function appendStoryApi(when) {
    var $more = $(".extra-stories");
    $more.api(
        {
            action: 'get stories',
            on: typeof when !== 'undefined' ? when : 'click',
            urlData: {
                ids: function () {
                    return getNext("ss", storiesRequested)
                }
            },
            onSuccess: populateStories
        }
    );
}

function appendDateStoryApi(when) {
    //var $more = $(".extra-stories");
    //$more.api(
    //    {
    //        action: 'get stories',
    //        on: typeof when !== 'undefined' ? when : 'click',
    //        urlData: {
    //            ids: function () {
    //                return getNext("ss", storiesRequested)
    //            }
    //        },
    //        onSuccess: populateStories
    //    }
    //);
}

function populateDateStories(data) {
    $(data.data).each(function (a, item) {
        var title = item.title;
        var dt = new Date(item.approvedDT);
        var dtStr = ((dt.getDay() < 10) ? '0' : '') + dt.getDay() +
            '/' +
            ((dt.getMonth() < 10) ? '0' : '') + dt.getMonth() + '/' + dt.getUTCFullYear() % 100;
        var $item = $("<div/>").addClass('item')
            .append(
            $("<div/>").addClass('asd-item-wrap')
                .append(
                $("<div/>").addClass('asd-menu-arrow')
                    .append(
                    $("<img>").attr('src', '/assets/images/arrow_selector_menu_golden.png'))
            ))
            .append(
            $("<div/>").addClass('asd-content')
                .append(
                $("<div/>").addClass('asd-header').html(title)
            )
                .append(
                $("<div/>").addClass('asd-date').html(dtStr)
            )
        );

        $dateStories.append($item);
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
