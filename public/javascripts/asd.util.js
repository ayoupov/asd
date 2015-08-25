function getId(elem) {
    var rawId = elem.attr('id');
    if (typeof rawId == 'undefined')
        rawId = elem.parent().attr('id');
    return rawId.substr(rawId.indexOf("_") + 1);
}

$.fn.serializeObject = function()
{
    var o = {};
    var a = this.serializeArray();
    $.each(a, function() {
        if (o[this.name] !== undefined) {
            if (!o[this.name].push) {
                o[this.name] = [o[this.name]];
            }
            o[this.name].push(this.value || '');
        } else {
            o[this.name] = this.value || '';
        }
    });
    return o;
};