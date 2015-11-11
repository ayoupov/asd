
var storyEditClick = function () {
    var id = getId($(this));
    $storyWrapper.empty();
    $storyForm = newStoryForm();
    $("#submit", $storyForm).api({
        on: 'click',
        action: 'post update story',
        method: 'POST',
        onSuccess: apiResult,
        serializeForm: true
    });
    $("#preview", $storyForm).api({
        on: 'click',
        action: 'preview story',
        method: 'POST',
        onSuccess: preview,
        serializeForm: true
    });
    $storyForm.appendTo($storyWrapper);
    $(".tabular.menu .item").tab({
        history: false
    });
    var $editor = $('.story-text');
    $(newToolbar($("textarea", $editor))).prependTo($editor);
    initToolbar();
    $fm.empty();
    $fm.append(newFileManager('story', id));
    $fm.api({on: 'now', action: 'get story pictures', urlData: {id : id}, onSuccess: fillFM});
    $(".story-visible").show();
    if (id != 0) {
        $storyForm.api({on: 'now', action: "get json story", urlData: {id: id}, onSuccess: fillStory});
        //applySubmit();
    }
    else {
        fillStory(null);
        //applySubmit();
    }
};

function newStoryForm() {
    return $("<form class='story-form ui form' method='post'>" +
        "<div class='ui top attached tabular menu basic-props'>" +
        "<a class='item active' data-tab='basic-props'>Basic</a>" +
        "<a class='item' data-tab='story-text'>Story Text</a>" +
        "</div>" +
        "<div class='ui bottom attached tab segment active' data-tab='basic-props'>" +
        "<label for='title'>Title</label><input id='title' placeholder='Title' name='title'/>" +
        "<label for='year'>Year</label><input id='year' placeholder='Year' name='year'/>" +
        "<label for='desc'>Cover description</label><textarea id='desc' placeholder='Text on cover' name='coverDescription'/>" +
        //"<label for='cover'>Cover image path</label><input id='cover' placeholder='Path to image' name='cover'/>" +
        "<label for='alt'>Alternative id</label><input id='alternativeId' placeholder='alternative id' name='alt'/>" +
        "<label for='church'>Dedicated church</label><input id='church' placeholder='church of story'/>" +
        "<label for='starred'>Starred</label><input type='checkbox' name='starred' id='starred' class='ui checkbox'/>" +
        "<br>" +
        "<label for='approvedDT'>Publish on</label><input type='datetime' class='ui datetime' name='approvedDT' id='approvedDT' value='" + datenow() + "'/>" +
        "<br>" +
        "<input type='hidden' id='id' name='id' value='0'/>" +
        "<input type='hidden' id='ctype' name='ctype' value='story'/>" +
        "<label for='author'>Author</label><input id='author' type='text'/>" +
        "<br>" +
        "</div><div class='ui bottom attached tab segment story-text' data-tab='story-text'>" +
        "<textarea id='text' name='text'>Perfect text of story</textarea>" +
        "</div>" +
        "<input id='submit' type='button' class='ui submit button' value='update'>" +
        "<input id='preview' type='button' class='ui submit preview button' value='preview'>" +
        "</form>");
}

function fillStory(data) {
    $admpages.hide();
    if (data) {
        var church = data.church;
        data = data.data;
        $("#title", $storyForm).val(data.title).on('input', changeThumb);
        $("#year", $storyForm).val(data.year);
        $("#desc", $storyForm).html(data.coverDescription).on('keyup', changeThumb);
        //$("#cover", $storyForm).val((data.cover) ? data.cover.path : "");
        $("#alternativeId", $storyForm).val(data.alternativeId);
        $("#church", $storyForm).val(church).attr('disabled', 'disabled');
        if (data.starred)
            $("#starred", $storyForm).attr("checked", "checked");
        else $("#starred", $storyForm).removeAttr("checked");
        var editorDate;
        if (data.approvedDT)
            editorDate = toEditorDate(new Date(data.approvedDT));
        else
            editorDate = '';
        $("#approvedDT", $storyForm).val(editorDate);
        $("#author", $storyForm).val(data.authors[0].name).attr('disabled', 'disabled');
        $("#id", $storyForm).val(data.id);
        $("#text", $storyForm).val(data.text);

        $(".admin-editor-thumb").remove();
        $editorThumb = createMediaThumb('story', data.id, data.coverThumbPath, data.title, data.coverDescription, null, data.lead, data.alternativeId);
        $editorThumb.addClass('admin-editor-thumb admin-page');
        $(".hover-content", $editorThumb).hide();
        $(".wrapper").append($editorThumb);
    }
    $storyWrapper.show();
}
