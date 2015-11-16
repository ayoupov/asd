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
    var $buoop =        {
        vs: {i: 6, f: 2, o: 9.63, s: 2, c: 10},  // browser versions to notify
        reminder: 48,                   // atfer how many hours should the message reappear
                                        // 0 = show all the time
        reminderClosed: 336,             // if the user closes message it reappears after x hours
        //onshow: function (infos) {
        //},      // callback function after the bar has appeared
        //onclick: function (infos) {
        //},     // callback function if bar was clicked
        onclose: function (infos) {
            scrollFunc();
            window.scrollTo(0,0);
        },
        l: "pl",                        // set a language for the message, e.g. "en"
                                        // overrides the default detection
        text:             "Przeglądarka, której używasz, jest przestarzała. " +
        "Posiada ona udokumentowane luki bezpieczeństwa, inne wady oraz ograniczoną funkcjonalność. " +
        "Tracisz możliwość skorzystania z pełni możliwości oferowanych przez naszą stronę internetową. " +
        "Dowiedz się jak zaktualizować swoją przeglądarkę."
        ,                       // custom notification html text
        // Optionally include up to two placeholders "%s" which will be replaced with the browser version and contents of the link tag. Example: "Your browser (%s) is old.  Please <a%s>update</a>"
        newwindow: true                 // open link in new window/tab
    };
    $buo($buoop, false);
    // cookie notification
    if (!Cookies.get("cookiesnotified")) {
        var notificationMessage = $("<div class='ui message cookie-notification' id='cookie-notification'><img src='/assets/images/close_button.png' class='notification-close-button'>" +
            "Nasza strona internetowa używa plików cookies (tzw. ciasteczka) w celach statystycznych, funkcjonalnych oraz autoryzacyjnych. " +
            "Dzięki nim możemy indywidualnie dostosować stronę do twoich potrzeb oraz bardziej efektywnie zbierać wspomnienia z budów kościołów. " +
            "Wyłączając ten komunikat akceptujesz używanie plików cookies. " +
            "Każdy ma możliwość wyłączenia ich w przeglądarce, dzięki czemu nie będą zbierane żadne informacje. " +
            "<a href='http://ciasteczka.eu/#jak-wylaczyc-ciasteczka'>Dowiedz się jak je wyłączyć</a>. Przeczytaj więcej w <a href='/content/tos_site.html'>regulaminie strony</a>." +
            "</div>");
        $(".message-wrapper").append(notificationMessage);
        $('.notification-close-button', notificationMessage).on('click', function () {
            Cookies.set("cookiesnotified", "true", {expires: 365});
            notificationMessage.fadeOut(600);
        });
    }
});

var possiblyDesktop, isTouchDevice;

//isTouchDevice = 'ontouchstart' in document.documentElement;
isTouchDevice = Modernizr.touch;
possiblyDesktop = !isTouchDevice;
