function getId(elem) {
    var rawId = elem.attr('id');
    if (typeof rawId == 'undefined')
        rawId = elem.parent().attr('id');
    return rawId.substr(rawId.indexOf("_") + 1);
}
