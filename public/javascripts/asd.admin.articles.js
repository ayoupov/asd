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

