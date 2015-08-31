var articleEditClick = function () {
    var id = getId($(this));
    $articleWrapper.empty();
    $articleForm = newArticleForm();
    $(".submit.button", $articleForm).api({
        on: 'click',
        action: 'post update article',
        method: 'POST',
        onSuccess: restorePage,
        //onSuccess: changeRow,
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


function newArticleForm() {
    return $("<form class='article-form ui form' method='post'>" +
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
        "<label for='text'>Text</label><textarea id='text' name='text'>Perfect text of article</textarea>" +
        "<input id='submit' type='button' class='submit button' value='update'>" +
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

function buildArticle() {
    var form = $articleForm.serializeObject();
    //$.extend(form, {text: $articleEditor.html()});
    console.log(form);
    return form;
}

