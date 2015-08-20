var $contentTitle;

$(document).ready(function () {
    $contentTitle = $('.content-title');
    $(window).on('resize', resizeContentFunc);
    // search prompt changes
    $('.prompt').on('focus', changeSearchPrompt).on('focusout', changeSearchPrompt);
    changeSearchPrompt();
    resizeContentFunc();
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
