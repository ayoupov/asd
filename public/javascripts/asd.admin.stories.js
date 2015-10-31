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
        $fm.api({on: 'now', action: 'get associated pictures', onSuccess: fillFM});
    }
    else {
        fillStory(null);
        $fm.api({on: 'now', action: 'get associated pictures', onSuccess: fillFM});
    }
};

function newStoryTemplate() {
    return $("<p>Perfect story</p>");
}

function newStoryForm() {
    return $("<form class='story-form ui form' method='post'>" +
        "<label for='author'>Author</label><input type='text' id='author' name='authors' disabled class='grayish'/>" +
        "<label for='title'>Title</label><input id='title' placeholder='Title' name='title'/>" +
        "<label for='lead'>Lead</label><textarea id='lead' placeholder='Lead' name='lead'/>" +
        "</form>");
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
