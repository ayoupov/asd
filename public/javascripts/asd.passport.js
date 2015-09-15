var $pmbottom, $passbtn;

$(document).ready(function () {
    $pmbottom = $("#passportmenu-bottom");
    $passbtn = $('.passport-edit-button');
    $(window).on('resize', fixUI);
    fixUI();

});

function fixUI()
{
    $passbtn.css({left : (($(window).width() - $passbtn.width()) / 2) + "px"});
}