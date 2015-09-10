var $contentTitle;

$(document).ready(function () {
    $contentTitle = $('.content-title');
    $(window).on('resize', resizeContentFunc);
    // search prompt changes
    $('.prompt').on('focus', changeSearchPrompt).on('focusout', changeSearchPrompt);
    changeSearchPrompt();
    resizeContentFunc();
    anchorFix();
    galleries();
});

var resizeContentFunc = function () {
    var wwidth = $(window).width();
    $contentTitle.css({
        left: (wwidth - $contentTitle.width()) / 2 + "px"
    });
};

var contentType = 'article'; // location.pathname.split('/')[2] ||
var changeSearchPrompt = function () {
    if ($prompt.is(":focus")) {
        switch (contentType) {
            case "article":
                $prompt.attr('placeholder', "Słowo do wyszukiwania (articles)");
                break;
            case "story":
                $prompt.attr('placeholder', "Słowo do wyszukiwania (stories)");
                break;
        }
        $prompt.css('font-size', '11pt');
    } else {
        $prompt.attr('placeholder', "szukaj");
        $prompt.css('font-size', '18pt');
    }
};

function anchorFix() {
    $('a[href*=#]:not([href=#])').click(function () {
        if (location.pathname.replace(/^\//, '') == this.pathname.replace(/^\//, '')
            || location.hostname == this.hostname) {
            var menuOffset = 0;
            var target = $(this.hash);
            target = target.length ? target : $('[name=' + this.hash.slice(1) + ']');
            if (target.length) {
                if (target.hasClass('biblink'))
                    menuOffset = $menu.height();
                $('html,body').animate({
                    scrollTop: target.offset().top - menuOffset
                }, 600);
                return false;
            }
        }
    });
}

var DEFAULT_GALL_OPTS =
{
    gallery_autoplay: true,
    gallery_play_interval: 30000,
    gallery_carousel: true,
    gallery_debug_errors: true
};

function galleries() {
    $(".content-gallery").each(function (a, gallery) {
        var gallOpts = DEFAULT_GALL_OPTS;
        var $gall = $(gallery);
        if ($gall.hasClass('full-width'))
            $.extend(gallOpts,
                {
                    gallery_width: "100%",
                    gallery_max_width: "100%"
                });
        else {
            var w = $gall.css('width');
            $.extend(gallOpts,
                {
                    gallery_width: w,
                    gallery_max_width: w
                });
            var h = $gall.css('height');
            if (h && parseFloat(h) > 0)
                $.extend(gallOpts, {gallery_height: h});
        }
        $gall.unitegallery(gallOpts);
    });
}