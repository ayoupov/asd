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
            var isNew = $row.length == 0;
            if (isNew)
                $articles.append($row = $("<tr>").attr('id', 'article_' + id));
            $row.empty();
            $row.append($("<td/>").html($("#title", $articleForm).val()));
            var auths = [];
            $("#authors option[selected='selected']", $articleForm).each(function (a, item) {
                auths.push($(item).html());
            });
            $row.append($("<td/>").html(auths.join(", ")));
            $row.append($("<td/>").addClass('noedit')
                    .html(
                    $("<input type='datetime' value='" +
                        $("#approvedDT", $articleForm).val() + "' />")
                )
            );
            $row.append($("<td/>").addClass('noedit').html(
                    $("<input type='checkbox' " +
                        ($("#starred:checked", $articleForm).length > 0 ? "checked" : "") +
                        "/>"))
            );
            $("td:not(.noedit)", $row).on('click', articleEditClick);
            if (isNew)
                $row.hover(function () {
                    $(this).toggleClass('active');
                }, function () {
                    $(this).toggleClass('active');
                });
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
