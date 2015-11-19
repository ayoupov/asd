var $admpages, $editors, $wrappers,
    $requestForm, $churchForm, $churchWrapper, $articleWrapper, $storyWrapper,
    $articleForm, $storyForm, $fm, $emailWrapper, $emailForm;

var apiExtension =
{
    'get json email': '/content/email/{name}',
    'get json requests': '/church/requests/{id}',
    'post ignore suggestion': '/church/requests/{id}/ignore',
    'post fix suggestion': '/church/requests/{id}/fix',
    'get json article': '/article/{id}.json',
    'get json story': '/story/{id}.json',
    'get associated pictures': '/files/list',
    'get story pictures': '/files/story/{id}',

    'post update email': '/content/email/{name}',
    'post update church': '/church/update/{id}',
    'post check email': '/content/email/check/{name}',
    'post update article': '/article/update',
    'preview article': '/preview/article',
    'post update story': '/story/update',
    'preview story': '/preview/story',

    'star article': '/article/star/{id}',
    'star story': '/story/star/{id}',
    'approve article': '/article/approve/{id}/{timestamp}',
    'approve story': '/story/approve/{id}/{timestamp}',
    'disapprove article': '/article/disapprove/{id}',
    'disapprove story': '/story/disapprove/{id}',
    'approve image': '/image/approve/{id}/{timestamp}',
    'disapprove image': '/image/disapprove/{id}',

    'upload files': '/files/upload',

    'remove content': '/{ctype}/{id}',
    'hide feedback' : '/feedback/{id}'
};

var $prevPage;
function restorePage(data) {
    if (data) changeRow(data);
    goAdmPage($prevPage);
}

function changeRow(data) {
    if (data.success && data.entity) {
        if (data.entity == "article") {
            var $articles = $("table", $("#articles"));
            var id = data.id;
            $("#id", $articleForm).val(id);
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
            $row.append($("<td/>").addClass('noedit').html(
                    $(
                        "<a href='/article/" + id + "' target='_blank'><img src='/assets/images/arrow_selector_menu_golden.png'></a>" +
                        "<a href='javascript:removeContent(\"article\", " + id + ");' style='margin-left:20px;'><img src='/assets/images/close_button.png'></a>"
                    )
                )
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

function flipApprove(ctype, id, unapprovedYet) {
    var act = (unapprovedYet) ? 'approve' : 'disapprove';
    var urlData = {
        ctype: ctype,
        id: id
    };
    if (unapprovedYet)
        $.extend(urlData, {timestamp: new Date().getTime()});
    $.api({
        action: act + ' ' + ctype,
        on: 'now',
        urlData: urlData,
        method: 'POST',
        onSuccess: reloadPage
    });
}

function flipStar(ctype, id) {
    $.api({
        action: 'star ' + ctype,
        on: 'now',
        urlData: {
            ctype: ctype,
            id: id
        },
        method: 'POST',
        onSuccess: reloadPage
    });
}

function reloadPage()
{
    window.location = '/admin?_=' + ((new Date()).getTime()) + location.hash;
}

function removeContent(ctype, id) {
    // todo: alert
    $.api({
        action: 'remove content',
        on: 'now',
        urlData: {
            ctype: ctype,
            id: id
        },
        method: 'DELETE',
        onSuccess: reloadPage
    });
}

function hideFeedback(id) {
    $.api({
        action: 'hide feedback',
        on: 'now',
        urlData: {
            id: id
        },
        method: 'DELETE',
        onSuccess: reloadPage
    });
}

function initAdmSelectorCache() {
    $admpages = $(".admin-page");
    $editors = $(".editor-content");
    $wrappers = $(".editor-wrapper");
    $churchForm = $(".church-form");
    $churchWrapper = $(".church-wrapper");
    $articleWrapper = $(".article-wrapper");
    $storyWrapper = $(".story-wrapper");
    $articleForm = $(".article-form");
    $storyForm = $(".story-form");
    $fm = $("#fm");
    $emailForm = $(".email-form");
    $emailWrapper = $(".email-wrapper");
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
    $("td:not(.noedit)", $("#emails")).on('click', emailEditClick);
    $("td:not(.noedit)", $("#churches")).on('click', churchEditClick);

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
            window.location = '/admin' + '?' + (entity + '_' + filterType) + '=' + val + (hash ? hash : '');
        }
    }
}
