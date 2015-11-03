var $map, $menu, $slide1, $slide2, $slide3, $slide4, $prompt, $links, $about;
var $articles, $stories, $dateStories;
var $searchWrapper, $suggestionWrapper, $feedbackWrapper;

var initSelectorCache = function () {
    $map = $("#map");
    $menu = $("#menu");
    $slide1 = $("#slide-map");
    $slide2 = $("#slide-articles");
    $slide3 = $("#slide-stories");
    $slide4 = $("#slide-about");
    $searchWrapper = $(".ui.search", $menu);
    $prompt = $(".prompt", $menu);
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
        var notificationMessage = $("<div class='ui success message cookie-notification' id='cookie-notification'><i class='close icon'></i>" +
                "<div class='header'>Cookie use notification</div>" +
            "Nasza strona internetowa używa plików cookies (tzw. ciasteczka) w celach statystycznych oraz funkcjonalnych. " +
            "Dzięki nim możemy indywidualnie dostosować stronę do twoich potrzeb. Każdy może zaakceptować pliki cookies " +
            "albo ma możliwość wyłączenia ich w przeglądarce, dzięki czemu nie będą zbierane żadne informacje. " +
            "Dowiedz się więcej jak je wyłączyć." +
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
