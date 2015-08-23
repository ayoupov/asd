var $admpages, $editors, $wrappers, $revisions,
    $articleEditor, $storyEditor, $churchEditor,
    $articleWrapper, $storyWrapper,
    $articleForm, $storyForm;

var apiExtension =
{
    'get church revisions': '/content/churches/revisions/{id}',
    'get json article': '/article/{id}.json',
    'get json story': '/story/{id}.json'
};

var raptorSettings = {
    autoEnable: true,
    plugins: {
        classMenu: {
            classes: {
                'Main content': 'content-main',
                'Note': 'content-note',
                'Quote': 'content-quote-stub',
                'Golgen underline': 'golden content-underline',
                'Afterwords': 'content-afterwards-stub'
            }
        },
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
    },
    bind: {
        'cancel': function () {
            restorePage();
        }
    }
};

var articleDefaultSettings = {
    //bind: {
    //    'cancel' :
    //}
};


var $prevPage;
function restorePage() {
    goAdmPage($prevPage);
}

function initAdmSelectorCache() {
    $admpages = $(".admin-page");
    $revisions = $("#revisions");
    $editors = $(".editor-content");
    $wrappers = $(".editor-wrapper");
    $articleEditor = $(".article-editor");
    $storyEditor = $(".story-editor");
    $churchEditor = $(".church-editor");
    $articleWrapper = $(".article-wrapper");
    $storyWrapper = $(".story-wrapper");
    $articleForm = $(".article-form");
    $storyForm = $(".story-form");
}

$(document).ready(function () {
    initAdmSelectorCache();
    if (window.location.hash)
        goAdmPage(window.location.hash);
    else
        goAdmPage("#stats");
    $(".page-changer").on('click', function () {
        var id = $(this).attr('href');
        goAdmPage(id);
    });
    $("td:not(.noedit)", $("#articles")).on('click', articleEditClick);
    $("td:not(.noedit)", $("#stories")).on('click', storyEditClick);
    $("tr", $("#churches")).on('click', churchRevisionClick);

    $("tr").hover(function () {
        $(this).toggleClass('active');
    }, function () {
        $(this).toggleClass('active');
    });
    $(".text-filter").on("keypress", applyTextFilter);

    $.extend($.fn.api.settings.api, apiExtension);

});

var goAdmPage = function (id) {
    $admpages.hide();
    $wrappers.hide();
    $(id).show();
    $prevPage = $(id);
};

var articleEditClick = function () {
    var id = getId($(this));
    $articleWrapper.empty();
    $articleForm = newArticleForm();
    $articleForm.appendTo($articleWrapper);
    $articleWrapper.append($("<div>Text:</div>"));
    $articleEditor = $('<div id="articles" class="editor-content article-editor content-main">').appendTo($articleWrapper);
    $articleEditor.append(newArticleTemplate());
    if (id != 0)
        $articleEditor.api({on: 'now', action: "get json article", urlData: {id: id}, onSuccess: fillArticle});
    else
        fillArticle(null);

};

var storyEditClick = function () {
    var id = getId($(this));
    $storyEditor.empty();
    $storyEditor.show();
    if (id == 0) {
        $storyEditor.append(newStoryTemplate());
    } else {
        $storyEditor.api({on: 'now', action: "get json story", urlData: {id: id}, onSuccess: fillStory});
    }
};

function fillInRevs(id) {
    $revisions.api(
        {
            on: 'now',
            onSuccess: renderRevisions,
            urlData: {id: id},
            action: 'get church revisions'
        }
    );
}
var churchRevisionClick = function () {
    var id = getId($(this));
    fillInRevs(id);
    $revisions.show();
};

var renderRevisions = function (data) {
    $revisions.empty();
    // iterate thru versions till last approved
    $(data).each(function (a, item) {
        $revisions.append($("<div>").html(item));
    });
};

function newArticleTemplate() {
    return $("<p>Perfect text</p>");
}

function newStoryTemplate() {
    return $("<h2 id='title'>Title</h2><div id='author'>Author</div>" +
        "<div id='lead'>Lead</div>" +
        "<div id='text'>Text</div>");
}

function newArticleForm() {
    return $("<form class='article-form ui form' method='post'>" +
        "<label for='title'>Title</label><input id='title' placeholder='Title' name='title'/>" +
        "<label for='lead'>Lead</label><textarea id='lead' placeholder='Lead' name='lead'/>" +
        "<label for='approvedDT'>Publish on</label><input type='date' name='approvedDT' id='approvedDT'/>" +
        "<input type='hidden' id='id' name='id'/>" +
        "<label for='authors'>Authors</label><select id='authors' name='authors'>" +
        "</select>" +
        "<input type='submit' value='update'>" +
        "</form>");
}

function newStoryForm() {
    return $("<form class='story-form ui form' method='post'>" +
        "<label for='title'>Title</label><input id='title' placeholder='Title' name='title'/>" +
        "<label for='lead'>Lead</label><textarea id='lead' placeholder='Lead' name='lead'/>" +
        "<label for='approvedDT'>Publish on</label><input type='date' name='approvedDT' id='approvedDT'>" +
        "<label for='author'>Author</label><select id='author' name='authors'>" +
        "</select>" +
        "</form>");
}

function fillArticle(data) {
    $admpages.hide();
    if (data) {
        $("#text", $articleEditor).html(data.text);
        $("#title", $articleWrapper).val(data.title);
        $("#lead", $articleWrapper).html(data.lead);
        $("#approvedDT", $articleWrapper).val(data.approvedDT);
        $("#authors", $articleWrapper).html(data.authors);
        $("#id", $articleWrapper).val(data.id);
    }
    var articleSettings = raptorSettings;
    $.extend(articleSettings, articleDefaultSettings);
    $articleEditor.raptor(articleSettings);
    $articleEditor.ready(function () {
        $articleEditor.raptor('enableEditing');
        //$articleEditor.enableEditing();
    });
    $articleWrapper.show();
    $articleEditor.show();
}
function fillStory(data) {
    $storyEditor.html(data.text);
}

function fillRevisions(data) {
    $churchEditor.html(data.text);
}

function getFilter(elem) {
    var classes = elem.attr('class').split(' ');
    var type = null;
    for (var a in classes) {
        var item = classes[a];
        if (item.startsWith('filter-')) {
            type = item.substr(7);
            break;
        }
    }
    return type;
}

function applyTextFilter(e) {
    if (e.keyCode == 13) {
        var $item = $(e.target);
        var entity = $item.parents("div.admin-page").attr('id');
        var filterType = getFilter($item);
        var val = $item.val();
        if (filterType) {
            var hash = location.hash;
            location = '/admin' + '?' + (entity + '_' + filterType) + '=' + val + (hash ? hash : '');
        }
    }
}
