var $map, $menu, $slide1, $slide2, $slide3, $slide4, $prompt, $links, $about;
var $articles, $stories, $dateStories;

var initSelectorCache = function () {
    $map = $("#map");
    $menu = $("#menu");
    $slide1 = $("#slide1");
    $slide2 = $("#slide2");
    $slide3 = $("#slide3");
    $slide4 = $("#slide4");
    $prompt = $(".prompt");
    $links = $("#links");
    $about = $("#about");
    $articles = $(".articles");
    $stories = $(".stories");
    $dateStories = $("#stories-by-date");
};

$(document).ready(function () {
    initSelectorCache();
});
