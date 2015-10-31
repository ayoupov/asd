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
        left: (wwidth - $contentTitle.width()) / 2 + "px",
        top: (560 - $contentTitle.height() - 100) + "px"
    });
    //var margin = parseFloat($('.content-main-wrapper').css('margin-left'));
    $(".social-links").css({
        'margin-left' : '20px'
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
    gallery_autoplay: false,
    gallery_carousel: true,
    gallery_debug_errors: true,
    slider_control_zoom: false,
    slider_enable_arrows: false
};

function galleries() {
    $(".content-gallery").each(function (a, gallery) {
        var gallOpts = DEFAULT_GALL_OPTS;
        var $gall = $(gallery);
        if ($gall.hasClass('full-width')) {
            $.extend(gallOpts,
                {
                    gallery_width: "100%",
                    gallery_max_width: "100%"
                });
            var h = $gall.css('height');
            if (h && parseFloat(h) > 0)
                $.extend(gallOpts, {gallery_height: h});
        }
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
        var $gallery = $("#" + $gall.attr("id"));
        var thisGalleryAPI = $gallery.unitegallery(gallOpts);
        $gallery.append($("<div class='content-gallery-arrow content-gallery-arrow-left'></div><div class='content-gallery-arrow content-gallery-arrow-right'></div>"));
        $(".content-gallery-arrow", $gallery).hover(function () {
            $(this).addClass('hover');
        },function () {
            $(this).removeClass('hover');
        });
        $(".content-gallery-arrow-left", $gallery).on('click', function(){
            thisGalleryAPI.prevItem();
        });
        $(".content-gallery-arrow-right", $gallery).on('click', function(){
            thisGalleryAPI.nextItem();
        });
    });
}