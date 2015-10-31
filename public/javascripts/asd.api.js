var api =
{
    'get summary': '/content/summary',
    'get articles': '/articles/{ids}',
    'get stories': '/stories/{ids}',
    'get article': '/article/{id}',
    'get story': '/story/{id}',
    'get church passport': '/church/passport/{id}',
    'add church story': '/church/story',
    'get church images': '/church/{id}/images',
    'add church images': '/church/images',
    'update passport field': '/church/passport/{field}',
    'search': '/search/{$searchable}/{query}',
    'suggest church': '/church/suggest'
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
    // case of logout and coming back
};

function populateArticles(data) {
    $articles.isotope('remove', $(".extra-article"));
    var lastItem = inhabitThumbs($articles, 'article', data.data);
    var whatsleft = contentLeft('sa');
    //_debug('whatsleft: '  + whatsleft);
    if (whatsleft > 0) {
        var $more = $("<div/>").attr('id', 'more-article-thumb').addClass('extra-article article thumb center-more grayish').append(
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
    //$('.article:not(.extra-articles)').off('click').on('click', function () {
    //    window.open('/article/' + getId($(this)));
    //});
    bindThumbEvents($articles, 'article');
    hackStories();
}

var transitionOnceFlag = false;
function hackStories() {
    // hack to move to stories slide
    if (location.hash == "#slide-stories" && !transitionOnceFlag) {
        transitionOnceFlag = true;
        $("a.slide-stories").click();
    }
}

function appendArticleApi(when) {
    var $more = $(".extra-article");
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

    $stories.isotope('remove', $(".extra-story"));

    var lastItem = inhabitThumbs($stories, 'story', data.data);

    var whatsleft = contentLeft('ss');
    if (whatsleft > 0) {
        var $more = $("<div/>").attr('id', 'more-story-thumb').addClass('extra-story story thumb center-more white').append(
            $('<div/>').addClass('more-wrapper grayish-bordered').append(
                $('<div/>').addClass('more').html('Więcej ' + whatsleft)
            )
        );
        $more.insertAfter(lastItem);
        $stories.isotope('appended', $more);
        appendStoryApi();
    }
    $stories.isotope();
    bindThumbEvents($stories, 'story');
}

function appendStoryApi(when) {
    var $more = $(".extra-story", $stories);
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
        var dtStr = ddmmyy(dt);
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
    $(".item", $dateStories).hover(function () {
        $(this).addClass('active');
    }, function () {
        $(this).removeClass('active');
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
