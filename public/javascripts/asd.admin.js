var $admpages, $editor, $editorWrapper, $uihidden;

var raptorSettings = {
    //autoEnable: true,
    plugins: {
        languageMenu: false,
        //languageMenu: {
        //    locale: "pl",
        //    language : "pl"
        //},
        logo: false,
        tableCreate: false,
        tableDeleteColumn: false,
        tableDeleteRow: false,
        tableInsertColumn: false,
        tableInsertRow: false,
        tableMergeCells: false,
        tableSplitCells: false,
        statistics: false,
        guides: false,
        dockToElement: false,
        dockToScreen: false,
        specialCharacters: false,
        floatLeft: false,
        floatNone: false,
        floatRight: false
    }
};

function initAdmSelectorCache() {
    $editor = $("#editor");
    $editorWrapper = $("#editor-wrapper");
    $uihidden = $(".ui-hidden");
    $admpages = $(".admin-page");
    $revisions = $("#revisions");
}

$(document).ready(function () {
    initAdmSelectorCache();
    $editor.raptor(raptorSettings);
    $editorWrapper.hide();
    $uihidden.hide();
    $admpages.hide();
    if (window.location.hash)
        goAdmPage(window.location.hash);
    else
        goAdmPage("#stats");
    $(".page-changer").on('click', function () {
        var id = $(this).attr('href');
        goAdmPage(id);
    });
    $("tr", $("#articles")).on('click', articleEditClick);
    $("tr", $("#stories")).on('click', storyEditClick);
    $("tr", $("#churches")).on('click', churchRevisionClick);
});

var goAdmPage = function (id) {
    $admpages.hide();
    $editorWrapper.hide();
    $(id).show();
};

function getId(elem) {
    var rawId = elem.attr('id');
    return rawId.substr(rawId.indexOf("_") + 1);
}

var articleEditClick = function () {
    var id = getId($(this));
    $editor.empty();
    $editorWrapper.show();
    if (id == 0) {
        $editor.append(newArticleTemplate());
    } else {
        $editor.api({on: 'now', action: "get article", urlData: {id : id}, onSuccess: fillEditor});
    }
};

var storyEditClick = function () {
    var id = getId($(this));
    $editor.empty();
    $editorWrapper.show();
    if (id == 0) {
        $editor.append(newStoryTemplate());
    } else {
        $editor.api({on: 'now', action: "get story", urlData: {id : id}, onSuccess: fillEditor});
    }
};

function fillInRevs(id) {
    $revisions.api(
        {
            on : 'now',
            onSuccess : renderRevisions,
            urlData : {id : id},
            action : 'get church revisions'
        }
    );
}
var churchRevisionClick = function () {
    var id = getId($(this));
    $uihidden.hide();
    fillInRevs(id);
    $revisions.show();
};

var renderRevisions = function (data)
{
    $revisions.empty();
    // iterate thru versions till last approved
    $(data).each(function(a, item)
    {
        $revisions.append($("<div>").html(item));
    });
};

function newArticleTemplate() {
    return $("<h1 id='title'>Title</h1><div id='authors'>Authors</div>" +
        "<div id='publishDT'>When to publish</div><div id='lead'>Lead</div>" +
        "<div id='text'>Text</div>");
}

function newStoryTemplate() {
    return $("<h1 id='title'>Title</h1><div id='author'>Author</div>" +
        "<div id='publishDT'>When to publish</div><div id='lead'>Lead</div>" +
        "<div id='text'>Text</div>");
}

function fillEditor(data)
{
    $editor.html(data.text);
}
