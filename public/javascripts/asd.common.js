var $map, $menu, $slide1, $slide2, $slide3, $slide4, $prompt, $links, $about, $passportWrapper;
var $articles, $stories, $dateStories, $newStoryForm, $newStoryGallery, $passportGalleryWrapper;

var initSelectorCache = function () {
    $map = $("#map");
    $menu = $("#menu");
    $slide1 = $("#slide-map");
    $slide2 = $("#slide-articles");
    $slide3 = $("#slide-stories");
    $slide4 = $("#slide-about");
    $prompt = $(".prompt");
    $links = $("#links");
    $about = $("#about");
    $articles = $(".articles");
    $stories = $(".stories");
    $dateStories = $("#stories-by-date");
    $passportWrapper = $(".passport-wrapper");
    $newStoryForm = $(".passport-new-story");
    $newStoryGallery = $(".story-cover-gallery");
    $passportGalleryWrapper = $(".passport-image-wrapper");
};

$(document).ready(function () {
    initSelectorCache();
});
