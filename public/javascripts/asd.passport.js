var $pmbottom, $passbtn;

$(document).ready(function () {
    $pmbottom = $("#passportmenu-bottom");
    $passbtn = $('.passport-edit-button');
    $(window).on('resize', fixUI);
    fixUI();

});

function fixUI()
{
    $passbtn.css({left : (($passportWrapper.width() - $passbtn.width()) / 2) + "px"});
}

function fillPassport(church)
{
    console.log(church);
    $(".church-name").html(church.name);
    $(".church-address").html(church.address.unfolded);
    var website = church.website != null ? church.website : "";
    $(".church-website").html('<a target="_blank" href="' + website + '">'+ website +'</a>');
}
