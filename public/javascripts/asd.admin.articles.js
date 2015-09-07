var articleEditClick = function () {
    var id = getId($(this));
    $articleWrapper.empty();
    $articleForm = newArticleForm();
    $(".submit.button", $articleForm).api({
        on: 'click',
        action: 'post update article',
        method: 'POST',
        onSuccess: apiResult,
        //onSuccess: restorePage,
        //onSuccess: changeRow,
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
        $fm.api({on: 'now', action: 'get associated pictures', urlData: {id: id}, onSuccess: fillFM});
        applySubmit(id);
    }
    else
        fillArticle(null);
};

function applySubmit(id)
{
    $('#fm-form').on('submit',(function(e) {
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
                recreateArticleFM(id);
            },
            error: function(data){
                console.log("error");

            }
        });
    }));

    $(".submit.button", $fm).on("click", function() {
        $("#fm-form").submit();
    });
}

function recreateArticleFM(id) {
    $fm.empty();
    $fm.append(newFileManager('article', id));
    $fm.api({on: 'now', action: 'get associated pictures', urlData: {id: id}, onSuccess: fillFM});
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
        $("#title", $articleForm).val(data.title);
        $("#lead", $articleForm).html(data.lead);
        if (data.starred)
            $("#starred", $articleForm).attr("checked", "checked");
        else $("#starred", $articleForm).removeAttr("checked");
        $("#approvedDT", $articleForm).val(data.approvedDT);
        fillAuthors(data.authors);
        $("#id", $articleForm).val(data.id);
        $("#text", $articleForm).html(data.text);
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

