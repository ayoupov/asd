//var currentMCType;

function newFileManager(mctype, id) {
    //currentMCType = mctype;
    if (mctype=='article')
    return $('<form class="ui form" method="POST" class="dropzone" ' +
        'id="fm-form" action="/files/upload" enctype="multipart/form-data">' +
        '<input type="file" name="picture" multiple>' +
        '<p><input type="button" class="ui submit button" value="upload"></p>' +
        '</form>');
    else
    return $("<br>");
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
    var display_path = item.display;
    var path = item.path;
    var thumb = item.thumb;

    var $item = $("<div class='fm-item'/>").append($("<div class='fm-item-path'>").html(display_path).attr('data-lm', item.lm));
    if (thumb)
        $item.append($("<img class='ui image'/>").attr('src', thumb)).append($("<br>"));

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
