//var currentMCType;

function newFileManager(mctype, id) {
    //currentMCType = mctype;
    return $('<form class="ui form" method="POST" class="dropzone" ' +
        'id="fm-form" action="/files/upload" enctype="multipart/form-data">' +
        '<input type="file" name="picture" multiple>' +
        '<input type="hidden" name="mcid" value="' + id + '">' +
        '<input type="hidden" name="mctype" value="' + mctype + '">' +
        '<p><input type="button" class="ui submit button" value="upload"></p>' +
        '</form>');
}

function fillFM(data) {
    var $wrapper = $(".fm-items", $fm);
    if ($wrapper.length == 0)
        $wrapper = $("<div class='fm-items'/>").appendTo($fm);
    $(data).each(function (a, item) {
        $wrapper.append(addFileItem(item));
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
