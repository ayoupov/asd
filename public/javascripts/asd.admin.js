var $admpages, $editors, $wrappers, $revisions,
    $churchEditor, $articleWrapper, $storyWrapper,
    $articleForm, $storyForm, $fm;

var apiExtension =
{
    'get church revisions': '/content/churches/revisions/{id}',
    'get json article': '/article/{id}.json',
    'get json story': '/story/{id}.json',
    'get associated pictures': '/files/list/{id}',
    'post update article': '/article/update',
    'post update story': '/story/update'
};

var $prevPage;
function restorePage(data) {
    if (data) changeRow(data);
    goAdmPage($prevPage);
}

function changeRow(data) {
    if (data.success) {
        if (data.success == "article") {
            var $articles = $("table", $("#articles"));
            var id = data.id;
            var $row = $("#article_" + id);
            if ($row.length == 0)
                $articles.append($row = $("<tr>").attr('id', 'article_' + id));
            $row.empty();
            $row.append($("<td/>").html($("#title", $articleForm).val()));
            $row.append($("<td/>").html($("#authors", $articleForm).val()));
            $row.append($("<td/>").addClass('noedit')
                    .html(
                    $("<input type='date' value='" +
                        $("#approvedDT", $articleForm).val() + "' />")
                )
            );
            $row.append($("<td/>").addClass('noedit').html(
                    $("#starred", $articleForm).val())
            );
        }
    }

}

function initAdmSelectorCache() {
    $admpages = $(".admin-page");
    $revisions = $("#revisions");
    $editors = $(".editor-content");
    $wrappers = $(".editor-wrapper");
    $churchEditor = $(".church-editor");
    $articleWrapper = $(".article-wrapper");
    $storyWrapper = $(".story-wrapper");
    $articleForm = $(".article-form");
    $storyForm = $(".story-form");
    $fm = $("#fm");
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
    $fm.hide();
    $(id).show();
    $prevPage = $(id);
};

var articleEditClick = function () {
    var id = getId($(this));
    $articleWrapper.empty();
    $articleForm = newArticleForm();
    $(".submit.button", $articleForm).api({
        on: 'click',
        action: 'post update article',
        method: 'POST',
        onSuccess: changeRow,
        serializeForm: true
    });
    $articleForm.appendTo($articleWrapper);
    $fm.empty();
    $fm.append(newFileManager('article', id));
    $(".article-visible").show();
    if (id != 0) {
        $articleForm.api({on: 'now', action: "get json article", urlData: {id: id}, onSuccess: fillArticle});
        $fm.api({on: 'now', action: 'get associated pictures', urlData: {id: id}, onSuccess: fillFM});
    }
    else
        fillArticle(null);
};

var storyEditClick = function () {
    var id = getId($(this));
    $storyWrapper.empty();
    $storyForm = newStoryForm();
    $storyForm.appendTo($storyWrapper);
    $storyWrapper.append($("<div>Story text:</div>"));
    $storyEditor = $('<div id="stories" class="editor-content story-editor content-main">').appendTo($storyWrapper);
    $storyEditor.append(newStoryTemplate());
    $fm.empty();
    $fm.append(newFileManager('story', id));
    if (id != 0) {
        $storyEditor.api({on: 'now', action: "get json story", urlData: {id: id}, onSuccess: fillStory});
        $fm.api({on: 'now', action: 'get associated pictures', urlData: {id: id}, onSuccess: fillFM});
    }
    else
        fillStory(null);
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

function newFileManager(mctype, id) {
    return $('<form class="ui form" method="POST" action="/files/upload" enctype="multipart/form-data">' +
        '<input type="file" name="picture">' +
        '<input type="hidden" name="mcid" value="' + id + '">' +
        '<input type="hidden" name="mctype" value="' + mctype + '">' +
        '<p><input type="submit"></p></form>');
}

function fillFM(data) {
    $(data).each(function (a, item) {
        $fm.append(addFileItem(item));
    });
}

function addFileItem(item) {
    var path = item.path;
    var thumb = item.thumb;

    var $item = $("<div class='fm-item'/>").append($("<div class='fm-item-path'>").html(path));
    if (thumb)
        $item.append($("<img/>").attr('src', thumb));

    return $item;
}

function newStoryTemplate() {
    return $("<p>Perfect story</p>");
}

function newArticleForm() {
    return $("<form class='article-form ui form' method='post'>" +
        "<label for='title'>Title</label><input id='title' placeholder='Title' name='title'/>" +
        "<label for='lead'>Lead</label><textarea id='lead' placeholder='Lead' name='lead'/>" +
        "<label for='approvedDT'>Publish on</label><input type='datetime' name='approvedDT' id='approvedDT' value='" + datenow() + "'/>" +
        "<input type='hidden' id='id' name='id' value='0'/>" +
        "<input type='hidden' id='ctype' name='ctype' value='article'/>" +
        "<label for='authors'>Authors</label><select id='authors' name='authors'>" +
        "</select>" +
        "<label for='text'>Text</label><textarea id='text' name='text'>Perfect text of article</textarea>" +
        "<input id='submit' type='button' class='submit button' value='update'>" +
        "</form>");
}

function newStoryForm() {
    return $("<form class='story-form ui form' method='post'>" +
        "<label for='author'>Author</label><input type='text' id='author' name='authors' disabled class='grayish'/>" +
        "<label for='title'>Title</label><input id='title' placeholder='Title' name='title'/>" +
        "<label for='lead'>Lead</label><textarea id='lead' placeholder='Lead' name='lead'/>" +
        "</form>");
}

function fillArticle(data) {
    $admpages.hide();
    if (data) {
        $("#title", $articleForm).val(data.title);
        $("#lead", $articleForm).html(data.lead);
        $("#approvedDT", $articleForm).val(data.approvedDT);
        $("#authors", $articleForm).html(data.authors);    // todo: come on!
        $("#id", $articleForm).val(data.id);
        $("#text", $articleForm).html(data.text);
    }
    $articleWrapper.show();
}

function buildArticle() {
    var form = $articleForm.serializeObject();
    //$.extend(form, {text: $articleEditor.html()});
    console.log(form);
    return form;
}

function fillStory(data) {
    $admpages.hide();
    if (data) {
        $storyEditor.html(data.text);
        $("#title", $storyForm).val(data.title);
        $("#lead", $storyForm).html(data.lead);
        $("#id", $storyForm).val(data.id);
        $("#authors", $storyForm).val(data.addedBy.name);
    }
    var storySettings = raptorSettings;
    $.extend(storySettings, storyDefaultSettings);
    $storyEditor.raptor(storySettings);
    $storyEditor.ready(function () {
        $storyEditor.raptor('enableEditing');
    });
    $storyWrapper.show();
    $storyEditor.show();
}

function buildStory() {
    var form = $storyForm.serializeObject();
    $.extend(form, {text: $storyEditor.html()});
    console.log(form);
    return form;
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
