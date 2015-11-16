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
    $churchPrompt.on('focus', changeSearchPrompt).on('focusout', changeSearchPrompt);
    // sticky bindings
    $about.on('click', toggleAbout);
    // scroll bindings
    $(window).on('scroll', scrollFunc);
    // and window resize
    //$(window).on('resize', resizeFunc);
    $(window).on('resize', scrollFunc);
    //resizeFunc();
    scrollFunc();

    $(document).on('change keypress', '.required.not-filled', function () {
        $(this).removeClass('not-filled');
        $('label[for=' + $(this).attr('id') + ']').removeClass('not-filled');
    });

    changeSearchPrompt();
    bindAPI();
    initSearch();

    // set focus to search
    //$churchPrompt.focus();
};

var changeSearchPrompt = function () {
    var slide = currentSlide();
    switch (slide) {
        case "map":
            $churchPrompt.parent().show();
            $churchPrompt.parent().css('visibility', 'visible');
            $contentPrompt.parent().hide();
            break;
        case "stories":
        case "articles":
            //$churchPrompt.blur();
            $searchChurchWrapper.search('hide results');
            $churchPrompt.parent().hide();
            $contentPrompt.parent().show();
            break;
        default :
            //$churchPrompt.blur();
            $searchChurchWrapper.search('hide results');
            $churchPrompt.parent().show();
            $churchPrompt.parent().css('visibility', 'hidden');
            $contentPrompt.parent().hide();
    }
    var promptFocused = $('.prompt:focus').length > 0;
    if (promptFocused) {

        if ($churchPrompt.is(":focus")) {
            if (slide == "map") {
                $churchPrompt.attr('placeholder', "Wpisz nazwę lub adres");
                $searchChurchWrapper.data('searchable', 'churches');
            }
        }
        if ($contentPrompt.is(":focus")) {
            switch (slide) {
                case "articles":
                    $contentPrompt.attr('placeholder', "Szukaj w artykułach");
                    $searchContentWrapper.data('searchable', 'article');
                    break;
                case "stories":
                    $contentPrompt.attr('placeholder', "Szukaj we wspomnieniach");
                    $searchContentWrapper.data('searchable', 'story');
                    break;
            }
        }

        $churchPrompt.css('font-size', '11pt');
        $contentPrompt.css('font-size', '11pt');
    }
    else {
        switch (slide) {
            case "map":
                $churchPrompt.attr('placeholder', "");
                break;
            case "articles":
                $contentPrompt.attr('placeholder', "");
                $searchContentWrapper.data('searchable', 'article');
                break;
            case "stories":
                $contentPrompt.attr('placeholder', "");
                $searchContentWrapper.data('searchable', 'story');
                break;
        }
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
    var margin = parseFloat($('.media-container').css('margin-left')) || $('.media-container').offset().left;
    $menu.css({
        left: menuOffsetLeft + 'px',
        top: menuOffsetTop + 'px',
        width: (wwidth - 2 * menuOffsetLeft) + "px"
    });
    // try another story:
    $links.css({
        position: 'absolute',
        left: (margin + 240 - menuOffsetLeft) + 'px',
        top : '14px'
        //width: (wwidth - 2 * menuOffsetLeft) + "px"
    });


    $(".social-links").css({
        'bottom': menuOffsetTop + 'px',
        'margin-left': (margin + 20) + 'px'
    });
    // resize map (take care of position prop of slide!)
    var maptop;
    if($('.buorg').length && $('.buorg').is(':visible'))
        maptop = 0;
    else
        maptop = $menu.height();
    $map.css({
        top: maptop,
        left: 0,
        right: menuOffsetLeft,
        bottom: 0,
        width: (wwidth - 2 * menuOffsetLeft) + "px"
    });
    // shrink about if resolution is low
    if (wwidth < 960 && !$about.hasClass('shrunk'))
        toggleAbout();
    if (wwidth <= 720)
        $churchPrompt.css('width', '160px');
    else
        $churchPrompt.css('width', '224px');
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
    if (slide != "map")
        setSiteMode();
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
        $churchPrompt.focus();
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

function redit($item) {
    $item.addClass("not-filled");
    //$('label[for=' + $item.attr('id') + ']').addClass('not-filled');
}

function validateForm($form) {
    var $fields = $(".required:visible", $form);
    var res = true;
    $fields.each(function (a, item) {
        var $item = $(item);
        if ($item.is('[type=checkbox]')) {
            if (!$item.is(':checked')) {
                redit($item);
                res = false;
            }
        } else {
            var v = $item.val();
            if (!v) {
                res = false;
                redit($item);
            }
        }
    });
    return res;
}
