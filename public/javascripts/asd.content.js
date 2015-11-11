var $contentTitle, $contentCoverImage, $relatedThumbs, $relatedThumbsTitle;

$(document).ready(function () {
    $contentTitle = $('.content-title');
    $contentCoverImage = $('.content-cover-image');
    $relatedThumbs = $('.church-stories');
    $relatedThumbsTitle = $('.passport-stories-title');
    $(window).on('resize', resizeContentFunc);
    // search prompt changes
    $('.prompt').on('focus', changeSearchPrompt).on('focusout', changeSearchPrompt);
    initSearch();
    changeSearchPrompt();
    resizeContentFunc();
    anchorFix();
    galleries();
    thumbs();
    $('.passport-update-button-wrapper').on('click', function(){
        location.href = '/church/' + churchId + '#passportadd';
    })
});

var resizeContentFunc = function () {
    var wwidth = $(window).width();
    $contentTitle.css({
        left: (wwidth - $contentTitle.width()) / 2 + "px",
        top: ($contentCoverImage.height() + 60 - $contentTitle.height() - 100) + "px"
    });
    //var margin = parseFloat($('.content-main-wrapper').css('margin-left'));
    $(".social-links").css({
        'margin-left': '20px'
    });
    $("#menu").css('padding-left', '10px');

};

var contentType = location.pathname.split('/')[1];
var changeSearchPrompt = function () {
    if ($churchPrompt.is(":focus")) {
        switch (contentType) {
            case "article":
                $churchPrompt.attr('placeholder', "Szukaj w artykułach");
                break;
            case "story":
                $churchPrompt.attr('placeholder', "Szukaj we wspomnieniach");
                break;
        }
        $churchPrompt.css('font-size', '11pt');
    }
    else {
        $churchPrompt.attr('placeholder', "");
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
    function changeCaption(isFullwidth, data, $gall) {
        var desc = data.description ? data.description : "";
        var $elem = $(".content-image-caption", $("#gallery_controls_" + $gall.attr("id")));
        if (isFullwidth)
            $elem.css({"margin-left": 0});
        else {
            var marginLeft = $gall.css('margin-left');
            $elem.css({"margin-left": marginLeft});
        }
        $elem.html(desc);
    }

    $(".content-gallery").each(function (a, gallery) {
        var gallOpts = {};
        $.extend(gallOpts, DEFAULT_GALL_OPTS);
        var $gall = $(gallery);
        var h;
        var isFullwidth = $gall.hasClass('full-width');
        _debug($gall);
        _debug(isFullwidth);
        if (isFullwidth) {
            $.extend(gallOpts,
                {
                    gallery_width: "100%",
                    gallery_max_width: "100%"
                });
            h = $gall.css('height');
            if (h && parseFloat(h) > 0)
                $.extend(gallOpts, {gallery_height: h});
        }
        else {
            var w = $gall.css('width');
            $.extend(gallOpts,
                {
                    gallery_width: w,
                    gallery_max_width: w
                    //, slider_bullets_align_hor : 'right',
                    //slider_bullets_offset_hor : 20
                });
            h = $gall.css('height');
            if (h && parseFloat(h) > 0)
                $.extend(gallOpts, {gallery_height: h});
        }
        var $gallery = $("#" + $gall.attr("id"));
        var thisGalleryAPI = $gallery.unitegallery(gallOpts);
        $gallery.append($("<div class='content-gallery-arrow content-gallery-arrow-left'></div><div class='content-gallery-arrow content-gallery-arrow-right'></div>"));
        $(".content-gallery-arrow-left", $gallery).on('click', function () {
            thisGalleryAPI.prevItem();
        });
        $(".content-gallery-arrow-right", $gallery).on('click', function () {
            thisGalleryAPI.nextItem();
        });
        var $lowerElem;
        if (isFullwidth)
            $lowerElem = $("<div class='content-gallery-controls-wrapper content-main-wrapper' id='gallery_controls_" + $gall.attr("id") + "' >" +
                "<div class='content-main'><span class='content-image-caption'></span></div></div>");
        else
            $lowerElem = $("<div class='content-gallery-controls-wrapper' id='gallery_controls_" + $gall.attr("id") + "' >" +
                "<span class='content-image-caption'></span>" +
                "<div class='content-gallery-controls'></div>" +
                "</div>");
        $gallery.after($lowerElem);
        changeCaption(isFullwidth, thisGalleryAPI.getItem(0), $gall);
        thisGalleryAPI.on('item_change', function (num, data) {
            changeCaption(isFullwidth, data, $gall);
        });
    });
}

var relatedMediaIndex, relatedMedia;

function thumbs()
{
    $.ajax({
        url: '/content/related/' + contentId,
        type: 'GET',
        cache: false,
        contentType: false,
        success: populateRelated,
        error: function (errorMessage) {
        }
    });

    function populateRelated(data) {
        relatedMediaIndex = 0;
        relatedMedia = data;
        $relatedThumbs.empty();
        if (relatedMedia && relatedMedia.length > 0) {
            $relatedThumbsTitle.html("Wspomnienia dodane przez użytkowników").show();
            inhabitNext();
            $relatedThumbs.isotope();
            bindThumbEvents($relatedThumbs, 'related');
        }
        else {
            $relatedThumbsTitle.hide();
        }
    }
}

function inhabitNext() {
    if ($(".extra-related", $relatedThumbs).length > 0)
        $relatedThumbs.isotope('remove', $(".extra-related"));
    var lastItem = inhabitThumbs($relatedThumbs, 'related', relatedMedia);
    var visibleStories = $('.thumb', $relatedThumbs).length;
    // | 0 -- gets integer result of division
    $relatedThumbs.css('height', (350 * (((visibleStories - 1) / 4 | 0) + 1)) + 'px');
}
