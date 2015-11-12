var $map, $menu, $slide1, $slide2, $slide3, $slide4, $churchPrompt, $contentPrompt, $links, $about;
var $articles, $stories, $dateStories;
var $searchChurchWrapper, $searchContentWrapper, $suggestionWrapper, $feedbackWrapper;

var initSelectorCache = function () {
    $map = $("#map");
    $menu = $("#menu");
    $slide1 = $("#slide-map");
    $slide2 = $("#slide-articles");
    $slide3 = $("#slide-stories");
    $slide4 = $("#slide-about");
    $searchChurchWrapper = $(".ui.search.church-search", $menu);
    $searchContentWrapper = $(".ui.search.content-search", $menu);
    $churchPrompt = $(".church-search .prompt", $menu);
    $contentPrompt = $(".content-search .prompt", $menu);
    $links = $("#links");
    $about = $("#about");
    $articles = $(".articles");
    $stories = $(".stories");
    $dateStories = $("#stories-by-date");
    $suggestionWrapper = $(".suggestion-wrapper");
    $feedbackWrapper = $(".feedback-wrapper");
};

$(document).ready(function () {
    initSelectorCache();
    // cookie notification
    if (!Cookies.get("cookiesnotified")) {
        var notificationMessage = $("<div class='ui message cookie-notification' id='cookie-notification'><i class='close icon'></i>" +
            "Nasza strona internetowa używa plików cookies (tzw. ciasteczka) w celach statystycznych, funkcjonalnych oraz autoryzacyjnych. " +
            "Dzięki nim możemy indywidualnie dostosować stronę do twoich potrzeb oraz bardziej efektywnie zbierać wspomnienia z budów kościołów. " +
            "Wyłączając ten komunikat akceptujesz używanie plików cookies. " +
            "Każdy ma możliwość wyłączenia ich w przeglądarce, dzięki czemu nie będą zbierane żadne informacje. " +
            "<a href='http://ciasteczka.eu/#jak-wylaczyc-ciasteczka'>Dowiedz się więcej jak je wyłączyć</a>." +
            "</div>");
        $(".wrapper").append(notificationMessage);
        $('.close', notificationMessage).on('click', function()
        {
            Cookies.set("cookiesnotified", "true", {expires: 365});
            notificationMessage.fadeOut(600);
        });
    }
});

var possiblyDesktop, isTouchDevice;

isTouchDevice = 'ontouchstart' in document.documentElement;
possiblyDesktop = !isTouchDevice;
