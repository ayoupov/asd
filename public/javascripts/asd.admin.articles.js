var articleEditClick = function () {
    var id = getId($(this));
    $articleWrapper.empty();
    $articleForm = newArticleForm();
    $("#submit", $articleForm).api({
        on: 'click',
        action: 'post update article',
        method: 'POST',
        onSuccess: apiResult,
        serializeForm: true
    });
    $("#preview", $articleForm).api({
        on: 'click',
        action: 'preview article',
        method: 'POST',
        onSuccess: preview,
        serializeForm: true
    });
    $articleForm.appendTo($articleWrapper);
    $(".tabular.menu .item").tab({
        history: false
    });
    var $editor = $('.article-text');
    $(newToolbar($("textarea", $editor))).prependTo($editor);
    initToolbar();
    $fm.empty();
    $fm.append(newFileManager('article', id));
    $(".article-visible").show();
    if (id != 0) {
        $articleForm.api({on: 'now', action: "get json article", urlData: {id: id}, onSuccess: fillArticle});
        $fm.api({on: 'now', action: 'get associated pictures', onSuccess: fillFM});
        applySubmit();
    }
    else {
        $fm.api({on: 'now', action: 'get associated pictures', onSuccess: fillFM});
        fillArticle(null);
        applySubmit();
    }
};

function applySubmit()
{
    $('#fm-form').off('submit').on('submit',(function(e) {
        e.preventDefault();
        var formData = new FormData(this);

        $.ajax({
            type:'POST',
            url: $(this).attr('action'),
            data:formData,
            cache:false,
            contentType: false,
            processData: false,
            success:function(data){
                console.log("success");
                recreateArticleFM();
            },
            error: function(data){
                console.log("error");
            }
        });
    }));

    $(".submit.button", $fm).off('click').on("click", function() {
        $("#fm-form").submit();
    });
}

function recreateArticleFM() {
    $fm.empty();
    $fm.append(newFileManager());
    $fm.api({on: 'now', action: 'get associated pictures', onSuccess: fillFM});
    applySubmit();
}

function newArticleForm() {
    return $("<form class='article-form ui form' method='post'>" +
        "<div class='ui top attached tabular menu basic-props'>" +
        "<a class='item active' data-tab='basic-props'>Basic</a>" +
        "<a class='item' data-tab='article-text'>Article Text</a>" +
        "</div>" +
        "<div class='ui bottom attached tab segment active' data-tab='basic-props'>" +
        "<label for='title'>Title</label><input id='title' placeholder='Title' name='title'/>" +
        "<label for='lead'>Lead</label><textarea id='lead' placeholder='Lead' name='lead'/>" +
        "<label for='desc'>Cover description</label><textarea id='desc' placeholder='Text on cover' name='coverDescription'/>" +
        "<label for='cover'>Cover image path</label><input id='cover' placeholder='Path to image' name='cover'/>" +
        "<label for='alt'>Alternative id</label><input id='alternativeId' placeholder='alternative id' name='alt'/>" +
        "<label for='churches'>Related churches</label><input id='churches' placeholder='list of church ids' name='churches'/>" +
        "<label for='starred'>Starred</label><input type='checkbox' name='starred' id='starred' class='ui checkbox'/>" +
        "<br>" +
        "<label for='approvedDT'>Publish on</label><input type='datetime' class='ui datetime' name='approvedDT' id='approvedDT' value='" + datenow() + "'/>" +
        "<br>" +
        "<input type='hidden' id='id' name='id' value='0'/>" +
        "<input type='hidden' id='ctype' name='ctype' value='article'/>" +
        "<label for='authors'>Authors</label><select id='authors' name='authors' multiple='multiple' class='fullwidth'>" +
        "</select>" +
        "<br>" +
        "</div><div class='ui bottom attached tab segment article-text' data-tab='article-text'>" +
        "<textarea id='text' name='text'>Perfect text of article</textarea>" +
        "</div>" +
        "<input id='submit' type='button' class='ui submit button' value='update'>" +
        "<input id='preview' type='button' class='ui submit preview button' value='preview'>" +
        "</form>");
}

function fillAuthors(authors) {
    $(authors).each(function (a, item) {
        $("#authors").append($("<option>").attr({
            "value": item.id, 'selected': 'selected'
        }).html(item.name));
    });
}

function fillArticle(data) {
    $admpages.hide();
    if (data) {
        var churches = data.churches;
        data = data.data;
        $("#title", $articleForm).val(data.title).on('input', changeThumb);
        $("#lead", $articleForm).html(data.lead).on('keyup', changeThumb);
        $("#desc", $articleForm).html(data.coverDescription).on('keyup', changeThumb);
        $("#cover", $articleForm).val((data.cover) ? data.cover.path : "");
        $("#alternativeId", $articleForm).val(data.alternativeId);
        $("#churches", $articleForm).val(churches);
        if (data.starred)
            $("#starred", $articleForm).attr("checked", "checked");
        else $("#starred", $articleForm).removeAttr("checked");
        $("#approvedDT", $articleForm).val(toEditorDate(new Date(data.approvedDT)));
        fillAuthors(data.authors);
        $("#id", $articleForm).val(data.id);
        $("#text", $articleForm).val(data.text);

        $(".admin-editor-thumb").remove();
        var cover = (data.coverThumbPath) ? data.coverThumbPath : ((data.cover) ? data.cover.path : "");
        $editorThumb = createMediaThumb('article', data.id, cover, data.title, data.coverDescription, null, data.lead, data.alternativeId);
        $editorThumb.addClass('admin-editor-thumb admin-page');
        $(".hover-content", $editorThumb).hide();
        $(".wrapper").append($editorThumb);

    }
    $("#authors", $articleForm).tokenize(
        {
            datas: "/content/authors/",
            placeholder: 'Start typing author name',
            searchParam: "q",
            autosize: true
        });
    $articleWrapper.show();
}

function preview(data) {
    if (data.success)
    {
        window.open('/preview/' + data.contentType + '/' + data.previewId, '_blank');
    }
    else if (data.error)
        noty({
            text: data.error,
            timeout: 4000,
            layout: 'top',
            type: 'error'
        })
}

function apiResult(data) {
    if (data) changeRow(data);
    if (data.success)
        noty({
            text: 'successful update',
            timeout: 4000,
            layout: 'top',
            type: 'success'
        });
    else if (data.error)
        noty({
            text: data.error,
            timeout: 4000,
            layout: 'top',
            type: 'error'
        })
}

function buildArticle() {
    var form = $articleForm.serializeObject();
    //$.extend(form, {text: $articleEditor.html()});
    console.log(form);
    return form;
}

