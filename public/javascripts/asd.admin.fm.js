function newFileManager(mctype, id) {
    return $('<form class="ui form" method="POST" action="/files/upload" enctype="multipart/form-data">' +
        '<input type="file" name="picture">' +
        '<input type="hidden" name="mcid" value="' + id + '">' +
        '<input type="hidden" name="mctype" value="' + mctype + '">' +
        '<p><input type="submit"></p></form>');
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

    return $item;
}
