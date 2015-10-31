function currentSlide() {
    var top = $(window).scrollTop();
    var prev = $slide1.height();
    if (top <= prev)
        return "map";
    if (top <= (prev = prev + $slide2.height()))
        return "articles";
    if (top <= (prev = prev + $slide3.height()))
        return "stories";
    if (top <= (prev = prev + $slide4.height()))
        return "about";
}

var uiInit = function () {
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

    // links transitions
    $('a[href*=#]:not([href=#])').click(function () {
        if (location.pathname.replace(/^\//, '') == this.pathname.replace(/^\//, '')
            || location.hostname == this.hostname) {

            var target = $(this.hash);
            target = target.length ? target : $('[name=' + this.hash.slice(1) + ']');
            if (target.length) {
                $('html,body').animate({
                    scrollTop: target.offset().top
                }, 600);
                return false;
            }
        }
    });
    // search prompt changes
    //$('.prompt').on('focus', changeSearchPrompt).on('focusout', changeSearchPrompt);
    $prompt.on('focus', changeSearchPrompt).on('focusout', changeSearchPrompt);
    // sticky bindings
    $about.on('click', toggleAbout);
    // scroll bindings
    $(window).on('scroll', scrollFunc);
    // and window resize
    $(window).on('resize', resizeFunc);
    changeSearchPrompt();
    bindAPI();
    initSearch();
    // set focus to search
    //$prompt.focus();
};

var changeSearchPrompt = function () {
    var slide = currentSlide();
    if ($prompt.is(":focus")) {
        switch (slide) {
            case "map":
                $prompt.attr('placeholder', "Wpisz nazwę lub adres");
                $searchWrapper.data('searchable', 'churches');
                break;
            case "articles":
                $prompt.attr('placeholder', "Szukaj w artykułach");
                $searchWrapper.data('searchable', 'article');
                break;
            case "stories":
                $prompt.attr('placeholder', "Szukaj we wspomnieniach");
                $searchWrapper.data('searchable', 'story');
                break;
        }
        $prompt.css('font-size', '11pt');
    } else {
        switch (slide) {
            case "map":
                //$prompt.attr('placeholder', "znajdź swój kościół");
                $prompt.attr('placeholder', "");
                break;
            default :
                //$prompt.attr('placeholder', "szukaj");
                $prompt.attr('placeholder', "");
                break;
        }
        if (!$prompt.val())
            $prompt.css('font-size', '18pt');
    }
    switch (slide)
    {
        case "map":
        case "stories":
        case "articles":
            $prompt.parent().css('visibility', 'visible');
            break;
        default :
            $prompt.parent().css('visibility', 'hidden');
    }
};

var scrollFunc = function (event) {
    // adjust menu
    var menuHeight = $menu.height();
    var scrollPos = $(this).scrollTop();
    var slide1Border = $slide1.css('border-width');
    if (scrollPos >= menuOffsetDefault) {
        menuOffsetTop = 0;
    }
    else {
        menuOffsetTop = menuOffsetDefault;
        //$menu.css({'border-top' : 'none'});
    }
    if (scrollPos >= $slide1.height()) {
        menuOffsetLeft = 0;
        $('.left.item', $menu).css('padding-left', '10px');
        $('.right.item', $menu).css('padding-right', '10px');
        //toggleAbout('hide');
    }
    else {
        menuOffsetLeft = menuOffsetDefault;
        $('.left.item', $menu).css('padding-left', 0);
        $('.right.item', $menu).css('padding-right', 0);
    }
    changeSearchPrompt();
    followMenu();
    // resizeFunc also changes visibility of golden sticky on slide1
    resizeFunc();
};

var menuOffsetDefault = 10;
var menuOffsetTop = menuOffsetDefault, menuOffsetLeft = menuOffsetDefault; // should depend on current slide

var resizeFunc = function () {
    // resize nav
    var wwidth = $(window).width();
    $menu.css({
        left: menuOffsetLeft + 'px',
        top: menuOffsetTop + 'px',
        width: (wwidth - 2 * menuOffsetLeft) + "px"
    });
    var margin = parseFloat($('.media-container').css('margin-left'));
    $(".social-links").css({
        'bottom': menuOffsetTop + 'px',
        'margin-left' : (margin + 20) + 'px'
    });
    // resize map (take care of position prop of slide!)
    $map.css({
        top: $menu.height(),
        left: 0,
        right: menuOffsetLeft,
        bottom: 0,
        width: (wwidth - 2 * menuOffsetLeft) + "px"
    });
    // shrink about if resolution is low
    if (wwidth < 960 && !$about.hasClass('shrunk'))
        toggleAbout();
    if (wwidth <= 720)
        $prompt.css('width', '160px');
    else
        $prompt.css('width', '224px');
    // center transition arrows
    var $stransition = $('.slide-transition');
    var stransition = $stransition.width();
    $stransition.css({left: (wwidth - stransition) / 2});
    $(".slide-stories-left-filler").width($dateStories.width() + $dateStories.offset().left);
};

var followMenu = function () {
    var slide = currentSlide();
    $('a', $links).removeClass('active');
    $(".slide-" + slide, $links).addClass('active');
};

var prevWidth = 50;
var toggleAbout = function (action) {
    var $arrow = $('.arrow-right img', $about);
    var $content = $('.about-content', $about);
    var $shrunkContent = $('.about-shrunk-content', $about);
    if ($about.hasClass('shrunk')) {
        $arrow.removeClass('rot').rotate({
            duration: 200,
            angle: 180,
            animateTo: 1
        });
    } else {
        $arrow.addClass('rot').rotate({
            duration: 200,
            angle: 0,
            animateTo: 180
        });
        $prompt.focus();
    }
    $shrunkContent.toggle(200);
    $content.toggle(200);
    var t = $about.width();
    $about.animate({width: prevWidth}, 200);
    prevWidth = t;
    $about.toggleClass('shrunk');
};

var stickify = function () {
    $("ui.sticky").sticky(
        {
            bottomOffset: 10,
            context: '#slide1'
        }
    );
};

var isotopeThumbs = function () {
    $articles.isotope(
        {
            itemSelector: '.article',
            layoutMode: 'fitRows'
        });
    $stories.isotope(
        {
            itemSelector: '.story',
            layoutMode: 'fitRows'
        });
};

function openContent(contentType, contentId) {
    console.log('api: {' + contentType + ' : ' + contentId + "}");
}