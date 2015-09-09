//var currentMCType;

function newFileManager(mctype, id) {
    //currentMCType = mctype;
    return $('<form class="ui form" method="POST" class="dropzone" ' +
        'id="fm-form" action="/files/upload" enctype="multipart/form-data" multiple>' +
        '<input type="file" name="picture">' +
        '<input type="hidden" name="mcid" value="' + id + '">' +
        '<input type="hidden" name="mctype" value="' + mctype + '">' +
        '<p><input type="button" class="ui submit button" value="upload"></p>' +
        '</form>');
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

    $item.on('click', function () {
        var target = "text";
        if ($("[data-tab=basic-props]").hasClass('active'))
        {
            target = "cover";
        }
        insertAtCaret(target, path);
    });

    return $item;
}
